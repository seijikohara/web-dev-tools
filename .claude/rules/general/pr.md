# Pull Request Guidelines

## PR Title

Follow Conventional Commits format:

```
<type>(<scope>): <description>
```

Examples:
- `feat(usecase): add GetRdapInformationUseCase`
- `fix(api): handle network timeout errors`
- `refactor(domain): reorganize value objects`

## PR Description Structure

```markdown
## Summary
<1-3 bullet points describing what this PR does>

## Changes
<List of specific changes made>

## Test Plan
<Steps to verify the changes work correctly>

## Checklist
- [ ] Code formatted (`./gradlew spotlessApply`)
- [ ] Lint passes (`./gradlew spotlessCheck`)
- [ ] Unit tests pass (`./gradlew test`)
- [ ] Integration tests pass (`./gradlew integrationTest`)
- [ ] Build succeeds (`./gradlew build`)
- [ ] Documentation consistency verified

## Documentation Updates
- [ ] Code changes are reflected in documentation
- [ ] Documentation examples have been verified
- [ ] No stale references to renamed/removed code

## Related Issues
<Link to related issues if applicable>
```

## Pre-Submission Checklist

**IMPORTANT**: Before creating a PR, you MUST:

1. **Execute each checklist item** and verify it passes
2. **Check the box** (`[x]`) only after confirming success
3. **Do not check boxes** for items that were not run or failed

| Item | Command | When Required |
|------|---------|---------------|
| Format | `./gradlew spotlessApply` | Always |
| Lint Check | `./gradlew spotlessCheck` | Always |
| Unit Tests | `./gradlew test` | Always |
| Integration Tests | `./gradlew integrationTest` | API/DB changes |
| Full Build | `./gradlew build` | Always |
| Full Check | `./gradlew check` | Before merge |
| Doc Consistency | Manual verification | Always |

If any check fails, fix the issues before creating the PR.

## Documentation Consistency

**CRITICAL**: Before creating a PR, verify that code and documentation are consistent.

### When Code Changes

If you modify code, check if related documentation needs updating:

| Code Change | Documentation to Check |
|-------------|------------------------|
| New use case | CLAUDE.md (Quick Reference), architecture docs |
| New API endpoint | CLAUDE.md, OpenAPI annotations |
| New domain model | Architecture docs, code comments |
| Configuration changes | CLAUDE.md, README.md |
| Build/dependency changes | CLAUDE.md (Development Commands) |
| New naming patterns | Naming convention docs |

### When Documentation Changes

If you modify documentation, verify it matches the actual code:

| Documentation Change | Code to Verify |
|---------------------|----------------|
| Command examples | Actually run the commands |
| Package structure | Check actual directory structure |
| Class/interface names | Verify classes exist with exact names |
| Configuration properties | Check application.yml and code |
| API endpoints | Verify controller mappings |

### Verification Steps

1. **Identify affected files**: List all code and documentation files in the PR
2. **Cross-reference**: For each code change, check related docs; for each doc change, verify against code
3. **Run examples**: Execute any command examples in documentation to ensure they work
4. **Check naming**: Ensure class names, method names, and paths in docs match code exactly

### Common Inconsistencies to Watch

- Package paths that have been refactored
- Class names that have been renamed
- Gradle commands that have changed
- API endpoints that have been modified
- Configuration property names
- Project structure descriptions

### PR Description Addition

When documentation is affected, add a section to your PR description:

```markdown
## Documentation Updates
- [ ] Code changes are reflected in documentation
- [ ] Documentation examples have been verified
- [ ] No stale references to renamed/removed code
```

## Branch Strategy

### Branch Naming

```
<type>/<short-description>
```

| Prefix | Use Case |
|--------|----------|
| `feature/` | New features |
| `fix/` | Bug fixes |
| `refactor/` | Code improvements |
| `docs/` | Documentation |
| `test/` | Test additions/fixes |
| `chore/` | Tooling, dependencies |

### Base Branch

- Always branch from and merge to `main`

## Commit Guidelines

### During Development

- Make atomic commits (one logical change per commit)
- Use Conventional Commits format
- Keep commits focused and reviewable

### Before Merge

- Squash related commits if needed
- Ensure commit history tells a clear story

## Review Process

### Requesting Review

- Assign appropriate reviewers
- Add relevant labels
- Ensure CI passes before requesting review

### Responding to Feedback

- Address all comments
- Re-request review after making changes
- Resolve conversations when addressed

## Merge Strategy

- Use **Squash and merge** for feature branches
- Ensure the squash commit follows Conventional Commits format

## Special Cases

### Breaking Changes

Include `BREAKING CHANGE:` in the commit body:

```
feat(api): change response format

BREAKING CHANGE: API responses now use camelCase property names
```

### Dependency Updates

- Group related dependency updates in one PR
- Include changelog highlights for major updates
- Run full test suite before merging

### Frontend Submodule

- NEVER include `frontend/` changes in backend PRs
- Frontend is a separate Git submodule with its own repository
- Update submodule reference only when necessary
