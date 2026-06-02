# Claude Code Project Instructions

## Core Rule

This project uses file-based project memory.

Before starting work, Claude must read this file and then read or create the files under:

- `docs/ai-context/project-overview.md`
- `docs/ai-context/architecture.md`
- `docs/ai-context/current-status.md`
- `docs/ai-context/todo.md`
- `docs/ai-context/decisions.md`
- `docs/ai-context/dev-log.md`
- `docs/ai-context/next-session.md`

If these files do not exist, Claude must perform a read-only project review first, then create them.

## First-Time Project Review Workflow

When this project is opened for the first time, or when the `docs/ai-context/` files are missing:

1. Do not modify source code.
2. Scan the project structure.
3. Identify the backend framework, frontend framework, database configuration, main modules, and run commands.
4. Identify important pages, APIs, controllers, services, mappers, entities, and frontend files.
5. Create the following files:

    - `docs/ai-context/project-overview.md`
    - `docs/ai-context/architecture.md`
    - `docs/ai-context/current-status.md`
    - `docs/ai-context/todo.md`
    - `docs/ai-context/decisions.md`
    - `docs/ai-context/dev-log.md`
    - `docs/ai-context/next-session.md`

6. Write a concise but useful summary into each file.
7. Do not make feature changes during the first review.

## Normal Session Start Workflow

At the beginning of every new session:

1. Read all files under `docs/ai-context/`.
2. Summarize the current project status.
3. Summarize the current TODO list.
4. Read `next-session.md` and recommend the best next task.
5. Do not modify code until a specific task is selected.

## Development Workflow

When implementing a feature or fixing a bug:

1. Use `brainstorming` if the task involves feature design, UI design, or behavior changes.
2. Use `writing-plans` before modifying code.
3. Use `planning-with-files` for medium or large tasks.
4. Use `test-driven-development` when the task has testable logic.
5. Use `systematic-debugging` when handling errors, failed tests, or unexpected behavior.
6. Use `ui-ux-pro-max` for frontend UI/UX improvement.
7. Use `verification-before-completion` before claiming the task is complete.
8. Use `code-review` to review the current diff before final summary.

## Required End-of-Task Workflow

Before ending any task, Claude must update:

- `docs/ai-context/current-status.md`
- `docs/ai-context/todo.md`
- `docs/ai-context/decisions.md` if any technical decision was made
- `docs/ai-context/dev-log.md`
- `docs/ai-context/next-session.md`

The update must include:

1. What was changed.
2. Which files were changed.
3. What was verified.
4. What failed or was not verified.
5. What remains unfinished.
6. What should be done next session.

Claude must not mark a task as complete unless verification was performed, or clearly explain why verification was not possible.

## Project Safety Rules

- Do not modify database schema unless explicitly requested.
- Do not modify backend API paths unless explicitly requested.
- Do not modify Controller, Service, Mapper, entity fields, or database logic during frontend-only tasks.
- For UI/UX optimization, preserve existing API requests, field names, routes, and business logic.
- Prefer small, reviewable changes.
- Never delete files without explaining the reason.