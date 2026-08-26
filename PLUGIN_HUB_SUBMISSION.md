# Plugin Hub Submission

Misinformed: PvP and PvM Tracker is prepared for the RuneLite Plugin Hub standard build.

## 1. Publish this repository

Create a public GitHub repository, for example:

`https://github.com/<your-github-username>/clan-companion`

Push the contents of this folder to the repository's default branch.

## 2. Copy the full commit hash

After pushing the release, copy the full 40-character SHA of the release commit.

## 3. Fork RuneLite/plugin-hub

Fork `runelite/plugin-hub`, create a branch such as `clan-companion`, and add:

`plugins/clan-companion`

with exactly:

```properties
repository=https://github.com/<your-github-username>/clan-companion.git
commit=<40-character-release-commit-sha>
```

## 4. Open the Plugin Hub pull request

Open a PR from your fork/branch into `runelite/plugin-hub:master`.

Suggested PR title:

`Add Misinformed: PvP and PvM Tracker`

Suggested description:

> Adds Misinformed: PvP and PvM Tracker by Ybc. It provides local-only PvP/PvM session statistics, loot tracking, valuable-drop highlights, activity history, configurable personal goals, a compact overlay, and an explicit copy-to-clipboard session summary. It does not crowdsource player data, automate gameplay, alter combat interactions, or send Discord/webhook/network data.

## 5. Review CI

Resolve any build or Plugin Hub review requests by updating this repository, then update the commit hash in the same Plugin Hub PR.
