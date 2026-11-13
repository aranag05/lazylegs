# Contributing

A few rules so we don't step on each other's work.

## Git setup

Make sure your commits use your aueb email, otherwise GitHub won't show them under your account
(this already happened once because of an `aeub` typo):

    git config --global user.email "t8xxxxxx@aueb.gr"
    git config user.email

## Branches

- Don't commit straight to main.
- Pull main first, then make a branch for your task:

      git checkout main
      git pull
      git checkout -b feature/something

- Names we use: `feature/...` for new code, `fix/...` for bugs, `docs/...` for documentation.

## Commits

- Keep them small, one change each.
- Write the message in English, e.g. `Add capacity check to elevator`.
- Don't commit `target/` or IntelliJ files (the .gitignore handles that).

## Before pushing

- Run `mvn verify` and check it says BUILD SUCCESS.
- Check the email on your commits:

      git log --format="%h %ae %s" origin/main..HEAD

## Pull requests

- Open a PR into main and write in English what you changed and how you tested it.
- Ask someone else to review it. Don't merge your own PR.
- Merge with "Create a merge commit", not squash, so we keep all the commits
  (like we did in #3 and #4).
- Delete the branch after the merge.

## API

The interfaces we agreed with the other teams are in `docs/openapi.yaml`.
Java field names have to match the JSON names exactly (`fromFloor`, `toFloor`, ...).
If something there needs to change, we talk to the other teams first.
