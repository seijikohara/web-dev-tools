# Git Conventions

## Commit Messages

Follow Conventional Commits format:

```
<type>(<scope>): <description>

[optional body]
```

### Types

| Type | Description |
|------|-------------|
| `feat` | New feature |
| `fix` | Bug fix |
| `docs` | Documentation only |
| `style` | Formatting, no code change |
| `refactor` | Code change without feature/fix |
| `perf` | Performance improvement |
| `test` | Adding/updating tests |
| `chore` | Build, tooling, dependencies |
| `build` | Build system or external dependencies |

### Scope Examples

| Scope | Description |
|-------|-------------|
| `usecase` | Application use case changes |
| `domain` | Domain layer changes |
| `infra` | Infrastructure layer changes |
| `api` | REST API/controller changes |
| `db` | Database-related changes |
| `config` | Configuration changes |
| `deps` | Dependency updates |

### Examples

```
feat(usecase): add GetRdapInformationUseCase
fix(api): handle timeout errors in NetworkInfoController
refactor(domain): extract IpAddress value object
test(usecase): add SearchHtmlEntitiesUseCase tests
chore(deps): update Spring Boot to 3.4.0
build(gradle): configure integration test suite
```

### Commit Message Guidelines

- Use imperative mood in subject line ("add" not "added")
- Do not end subject line with period
- Limit subject line to 72 characters
- Separate subject from body with blank line
- Use body to explain "what" and "why", not "how"

## Branch Naming

```
<type>/<short-description>
```

| Prefix | Use Case |
|--------|----------|
| `feature/` | New features |
| `fix/` | Bug fixes |
| `refactor/` | Code improvements |
| `docs/` | Documentation updates |
| `test/` | Test additions/fixes |
| `chore/` | Tooling, dependencies |

### Examples

```
feature/add-dns-lookup
fix/handle-invalid-ip-address
refactor/extract-pagination-model
```

## Pull Requests

See `pr.md` for detailed PR guidelines.

## Important Notes

- NEVER modify files under `frontend/` directory (Git submodule)
- Always run `./gradlew spotlessCheck` before committing
- Run tests before pushing: `./gradlew check`
