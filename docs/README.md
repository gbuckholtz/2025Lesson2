# Robot Incremental Development Docs

We will evolve the robot in discrete capability stages. For each stage you will ask me to document; I will then:
1. Create or update a `stage-XX-name.md` file (two‑digit index).
2. Commit code + documentation.
3. Create a git tag `stage-XX-name` pointing to that commit.

## File Naming
`stage-01-initial.md`, `stage-02-drive-base.md`, etc. Use concise, kebab-case names.

## Template Sections
Each stage doc will include:
- Stage Number & Name
- Date
- Summary
- New Capability / Rationale
- Implementation Highlights (classes, methods modified/added)
- Testing / Verification Steps
- Next Steps / Possible Extensions

## Workflow
1. You implement or request a capability.
2. When ready, you say it's time to document the stage.
3. I generate/update the markdown, commit, and tag.

## Git Tagging
Tags: `stage-XX-name`. Lightweight annotated tags (with message summarizing capability).

## Diff Reference
For deeper review later we can run: `git diff stage-01-initial stage-02-drive-base` etc.

## Template
See `STAGE_TEMPLATE.md` for the structure reused.
