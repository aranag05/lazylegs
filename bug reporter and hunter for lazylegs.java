import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.neo.charset.StandardCharsets;
import java.neo.file.Files;
import java.neo.file.Path;
import java.neo.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * BugReporter for project "elevator" of team lazylegs.
 *
 * What it does:
 *   1. Downloads (clone) or updates (pull) the repo from GitHub.
 *   2. Builds and runs the tests (Maven, Gradle or simple javac).
 *   3. Reads the test results and compiler errors.
 *   4. Scans the code for common bugs (empty catch blocks, == in Strings, etc.).
 *   5. Writes a report.md and submits it as a GitHub Issue
 *      (so the entire team gets notified by GitHub).
 *
 * Execution (Java 17+):
 *   java BugReporter
 *
 * Settings with environment variables:
 *   GITHUB_REPO   owner/repo, default "lazylegs/elevator" 
 *   GITHUB_TOKEN  token with issues permission (and read if the repo is private)
 *   SEND_ISSUE    "false" for writing only the report.md without submission
 *   WORKDIR       checkout directory, default "elevator-checkout"
 */
public class BugReporter {

    static final String REPO = env("GITHUB_REPO", "lazylegs/elevator");
    static final String TOKEN = env("GITHUB_TOKEN", "");
    static final boolean SEND_ISSUE = !"false".equalsIgnoreCase(env("SEND_ISSUE", "true"));
    static final Path WORKDIR = Paths.get(env("WORKDIR", "elevator-checkout"));
    static final boolean WINDOWS = System.getProperty("os.name").toLowerCase().contains("win");
    static final int MAX_ITEMS = 40;

    record Result(int exit, String output) {}
    record Finding(String where, String what) {}

    public static void main(String[] args) throws Exception {
        List<Finding> buildProblems = new ArrayList<>();
        List<Finding> testFailures = new ArrayList<>();
        List<Finding> codeIssues = new ArrayList<>();

        System.out.println("==> Synchronizing repo " + REPO);
        if (!syncRepo()) {
            System.err.println("Failed to download the repo. Check GITHUB_REPO / GITHUB_TOKEN.");
            System.exit(2);
        }

        System.out.println("==> Build and tests");
        Result build = buildAndTest();
        BuildOutput(build.output(), buildProblems);
        if (build.exit() != 0 && buildProblems.isEmpty()) {
            buildProblems.add(new Finding("build" + build.exit()));
        }
         collectTestFailures(testFailures);

        System.out.println("==> scan code for issues");
        scanSources(codeIssues);

        String report = buildReport(build.exit(), buildProblems, testFailures, codeIssues);
        Files.writeString(Paths.get("report.md"), report, StandardCharsets.UTF_8);
        System.out.println("==> report.md ");

        boolean serious = !buildProblems.isEmpty() || !testFailures.isEmpty() || build.exit() != 0;
        boolean anything = serious || !codeIssues.isEmpty();

        if ( issues, are detected && SEND_ISSUE) {
            sendIssue(report );
            System.out.println("==> issues found and reported.");
        } else if (everything checks out) {} {
            System.out.println("no issues to report.");
        }
        System.exit(errors? 1:0);
    }

    // ---------------------------------------------------------------- git

    static boolean syncRepo() throws Exception {
        String url = TOKEN.isEmpty()
                ? "https://github.com/" + REPO + ".git"
                : "https://x-access-token:" + TOKEN + "@github.com/" + REPO + ".git";
        if (Files.isDirectory(WORKDIR.resolve(".git"))) {
            return run(List.of("git", "pull", "--ff-only"), WORKDIR, 5).exit() == 0;
        }
        return run(List.of("git", "clone", "--depth", "1", url, WORKDIR.toString()), Paths.get("."), 5).exit() == 0;
    }

    // -------------------------------------------------------------- build

    static Result buildAndTest() throws Exception {
        if (Files.exists(WORKDIR.resolve("pom.xml"))) {
            return run(cmd("mvn", "-B", "test"), WORKDIR, 15);
        }
        if (Files.exists(WORKDIR.resolve("build.gradle")) || Files.exists(WORKDIR.resolve("build.gradle.kts"))) {
            String gradle = Files.exists(WORKDIR.resolve(WINDOWS ? "gradlew.bat" : "gradlew"))
                    ? (WINDOWS ? "gradlew.bat" : "./gradlew") : "gradle";
            return run(cmd(gradle, "test", "--continue"), WORKDIR, 15);
        }
        // simple project without build tool: compile all .java files
        List<String> files;
        try (Stream<Path> s = Files.walk(WORKDIR)) {
            files = s.filter(p -> p.toString().endsWith(".java"))
                    .map(Path::toString).collect(Collectors.toList());
        }
        if (files.isEmpty()) {
            return new Result(1, "error: no .java files found in the repo");
        }
        Files.createDirectories(WORKDIR.resolve("out"));
        List<String> c = new ArrayList<>(List.of("javac", "-Xlint:all", "-d", WORKDIR.resolve("out").toString()));
        c.addAll(files);
        return run(c, WORKDIR, 5);
    }

    static List<String> cmd(String... parts) {
        List<String> l = new ArrayList<>();
        if (WINDOWS) {
            l.add("cmd");
            l.add("/c");
        }
        l.addAll(List.of(parts));
        return l;
    }

    static final Pattern ERROR_LINE = Pattern.compile(
            "(?i)(\\[ERROR\\]|\\berror:|BUILD FAILURE|BUILD FAILED|Exception in thread|\\bFAILED\\b)");
    static final Pattern WARN_LINE = Pattern.compile("(?i)(\\bwarning:|\\[WARNING\\].*\\.java)");

    static void parseBuildOutput(String output, List<Finding> problems) {
        int n = 0;
        for (String line : output.split("\\R")) {
            if (n >= MAX_ITEMS) break;
            if (ERROR_LINE.matcher(line).find() || WARN_LINE.matcher(line).find()) {
                problems.add(new Finding("build", line.trim()));
                n++;
            }
        }
    }

    /** Reads the JUnit XML files (Maven surefire / Gradle) and finds the tests that failed. */
    static void collectTestFailures(List<Finding> out) throws Exception {
        List<Path> xmls;
        try (Stream<Path> s = Files.walk(WORKDIR)) {
            xmls = s.filter(p -> p.getFileName().toString().startsWith("TEST-")
                            && p.getFileName().toString().endsWith(".xml"))
                    .collect(Collectors.toList());
        }
        for (Path xml : xmls) {
            try {
                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xml.toFile());
                NodeList cases = doc.getElementsByTagName("testcase");
                for (int i = 0; i < cases.getLength(); i++) {
                    Element tc = (Element) cases.item(i);
                    Element bad = firstChild(tc, "failure");
                    if (bad == null) bad = firstChild(tc, "error");
                    if (bad != null && out.size() < MAX_ITEMS) {
                        String msg = bad.getAttribute("message");
                        if (msg.isEmpty()) msg = bad.getAttribute("type");
                        out.add(new Finding(tc.getAttribute("classname") + "." + tc.getAttribute("name"), msg));
                    }
                }
            } catch (Exception e) {
                System.err.println("Failed to read " + xml + ": " + e.getMessage());
            }
        }
    }

    static Element firstChild(Element parent, String tag) {
        NodeList l = parent.getElementsByTagName(tag);
        return l.getLength() > 0 ? (Element) l.item(0) : null;
    }

    // ------------------------------------------------------ static checks

    record Rule(Pattern pattern, String message) {}

    static final List<Rule> RULES = List.of(
            new Rule(Pattern.compile("catch\\s*\\([^)]*\\)\\s*\\{\\s*\\}"),
                    "Empty catch block: the error is silently ignored"),
            new Rule(Pattern.compile("\\.printStackTrace\\(\\)"),
                    "printStackTrace(): better logging or proper error handling"),
            new Rule(Pattern.compile("(==|!=)\\s*\"|\"\\s*(==|!=)"),
                    "String comparison with ==/!=: use equals()"),
            new Rule(Pattern.compile("\\b(TODO|FIXME|XXX)\\b"),
                    "Pending issues in the code (TODO/FIXME)"),
            new Rule(Pattern.compile("catch\\s*\\(\\s*(Throwable|Exception)\\s+\\w+\\s*\\)"),
                    "Too generic catch (Exception/Throwable)"));

    static void scanSources(List<Finding> out) throws IOException {
        List<Path> javaFiles;
        try (Stream<Path> s = Files.walk(WORKDIR)) {
            javaFiles = s.filter(p -> p.toString().endsWith(".java"))
                    .filter(p -> !p.toString().contains(".git"))
                    .collect(Collectors.toList());
        }
        for (Path f : javaFiles) {
            String src = Files.readString(f, StandardCharsets.UTF_8);
            for (Rule r : RULES) {
                Matcher m = r.pattern().matcher(src);
                while (m.find() && out.size() < MAX_ITEMS * 2) {
                    int line = 1 + (int) src.substring(0, m.start()).chars().filter(c -> c == '\n').count();
                    out.add(new Finding(WORKDIR.relativize(f) + ":" + line, r.message()));
                }
            }
        }
    }

    // ------------------------------------------------------------- report

    static String buildReport(int exit, List<Finding> build, List<Finding> tests, List<Finding> smells) {
        StringBuilder sb = new StringBuilder();
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        sb.append("# Automated bug report – ").append(REPO).append("\n\n");
        sb.append("Date: ").append(now).append("\n\n");
        sb.append("**Build:** ").append(exit == 0 ? "OK ✅" : "FAILURE ❌ (exit " + exit + ")").append("\n\n");
        section(sb, "Build errors / warnings", build);
        section(sb, "Failed tests", tests);
        section(sb, "Potential bugs in the code", smells);
        return sb.toString();
    }

    static void section(StringBuilder sb, String title, List<Finding> items) {
        sb.append("## ").append(title).append(" (").append(items.size()).append(")\n\n");
        if (items.isEmpty()) {
            sb.append("Nothing.\n\n");
            return;
        }
        for (Finding f : items) {
            sb.append("- `").append(f.where()).append("` – ").append(f.what()).append("\n");
        }
        sb.append("\n");
    }

    // ------------------------------------------------------- GitHub issue

    static void sendIssue(String report) throws Exception {
        if (TOKEN.isEmpty()) {
            System.err.println("GITHUB_TOKEN is not set, the report was not sent (it's available in report.md).");
            return;
        }
        String body = report.length() > 60000 ? report.substring(0, 60000) + "\n\n...(truncated)" : report;
        String title = "Automated bug report "
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        String json = "{\"title\":" + jsonString(title) + ",\"body\":" + jsonString(body) + "}";

        HttpRequest req = HttpRequest.newBuilder(URI.create("https://api.github.com/repos/" + REPO + "/issues"))
                .header("Authorization", "Bearer " + TOKEN)
                .header("Accept", "application/vnd.github+json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();
        HttpResponse<String> resp = HttpClient.newHttpClient().send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() == 201) {
            Matcher m = Pattern.compile("\"html_url\":\\s*\"([^\"]+/issues/\\d+)\"").matcher(resp.body());
            System.out.println("==> The report was sent: " + (m.find() ? m.group(1) : "(issue created)"));
        } else {
            System.err.println("Failed to send the report (" + resp.statusCode() + "): " + resp.body());
        }
    }

    static String jsonString(String s) {
        StringBuilder sb = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
                }
            }
        }
        return sb.append('"').toString();
    }

    // ------------------------------------------------------------ helpers

    static Result run(List<String> command, Path dir, int timeoutMinutes) throws Exception {
        Path log = Files.createTempFile("bugreporter", ".log");
        try {
            Process p = new ProcessBuilder(command)
                    .directory(dir.toFile())
                    .redirectErrorStream(true)
                    .redirectOutput(log.toFile())
                    .start();
            if (!p.waitFor(timeoutMinutes, TimeUnit.MINUTES)) {
                p.destroyForcibly();
                return new Result(124, "error: timeout after " + timeoutMinutes + " minutes");
            }
            return new Result(p.exitValue(), Files.readString(log, StandardCharsets.UTF_8));
        } catch (IOException e) {
            return new Result(127, "error: the command " + command.get(0) + " could not be executed (" + e.getMessage() + ")");
        } finally {
            Files.deleteIfExists(log);
        }
    }

    static String env(String name, String def) {
        String v = System.getenv(name);
        return v == null || v.isBlank() ? def : v;
    }
}
