# Contributing

Thank you for considering contributing to the Todo App!

## Getting Started

1. **Fork the repository** on GitHub
2. **Clone your fork** locally
3. **Create a branch** for your changes
4. **Make your changes** with clear commits
5. **Push to your fork** and submit a pull request

## Development Setup

### Using Docker (Recommended)

```bash
# Clone your fork
git clone https://github.com/yourusername/todoapp.git
cd todoapp

# Build the project
./docker-build.sh debug

# Run tests
./docker-build.sh test
```

### Local Setup

See the [Building](building.md) guide for local development setup.

## Code Style

### Kotlin Style

Follow the [Kotlin Style Guide](https://kotlinlang.org/docs/coding-conventions.html):

- Use 4 spaces for indentation
- Opening braces on same line
- Use meaningful variable names
- Add comments for complex logic

### File Organization

- One class per file
- Group related functionality
- Keep files under 500 lines when possible
- Use packages to organize code logically

## Making Changes

### Before You Start

1. Check existing issues and PRs
2. Create an issue to discuss major changes
3. Keep changes focused and atomic
4. Write tests for new features

### Commit Messages

Use clear, descriptive commit messages:

```
Fix checkbox listener bug in TodoEntityAdapter

- Remove old listener before setting checkbox state
- Prevents unintended toggles during view recycling
- Fixes #123
```

Format:
- First line: Brief summary (50 chars or less)
- Blank line
- Detailed explanation if needed
- Reference issues with #issue-number

### Testing Your Changes

**Run Unit Tests:**
```bash
./docker-build.sh test
```

**Run Instrumented Tests:**
```bash
# With local emulator
./gradlew connectedAndroidTest

# Or with Docker build
./docker-build.sh test
```

**Manual Testing:**
1. Install on device/emulator
2. Test the specific feature you changed
3. Test related features
4. Test on different Android versions if possible

## Pull Request Process

### Before Submitting

- [ ] Code follows style guidelines
- [ ] Tests pass locally
- [ ] Added tests for new features
- [ ] Updated documentation if needed
- [ ] Commit messages are clear
- [ ] No merge conflicts with main

### Submitting

1. Push your branch to your fork
2. Create a pull request to `main`
3. Fill out the PR template
4. Wait for review and CI checks
5. Address review comments

### PR Description

Include:
- **What**: What does this PR do?
- **Why**: Why is this change needed?
- **How**: How does it work?
- **Testing**: How did you test it?
- **Screenshots**: For UI changes

Example:
```markdown
## What
Adds weekly waterfall calendar view

## Why
Provides better visualization of todos across the week

## How
- Creates new CalendarActivity with waterfall layout
- Implements day columns with vertical todo lists
- Adds week navigation

## Testing
- Tested with seeded sample data
- Verified on API 24 and API 33
- Added CalendarWaterfallTest suite

## Screenshots
[Attach screenshots]
```

## Areas for Contribution

### High Priority

- Bug fixes
- Performance improvements
- Test coverage
- Documentation improvements
- Accessibility enhancements

### Feature Ideas

- Search functionality
- Todo categories/tags
- Priority levels
- Dark mode
- More widget customization
- Cloud sync (requires backend)

### Good First Issues

Look for issues labeled `good-first-issue` for beginner-friendly tasks.

## Code Review

### What We Look For

- Correct functionality
- Clean, readable code
- Appropriate tests
- No breaking changes
- Performance considerations
- Security best practices

### Review Time

- Most PRs reviewed within 3-5 days
- Complex changes may take longer
- Ping maintainers if no response after a week

## Testing Guidelines

### Unit Tests

- Test business logic
- Mock dependencies
- Fast execution
- No Android framework dependencies

### Instrumented Tests

- Test UI interactions
- Test database operations
- Use seeded test data
- Extend `BaseInstrumentedTest`

### Adding New Tests

```kotlin
@RunWith(AndroidJUnit4::class)
@LargeTest
class MyNewTest : BaseInstrumentedTest() {
    // Data automatically seeded!

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun myTest() {
        // Test code
    }
}
```

## Documentation

### Where to Update

- `docs/` - MkDocs documentation
- `README.md` - Quick start guide
- Code comments - Complex logic
- `app/src/androidTest/*/README.md` - Test documentation

### Building Documentation

```bash
# Install mkdocs
pip install mkdocs-material

# Serve locally
mkdocs serve

# Build static site
mkdocs build
```

## Getting Help

- **Issues**: Create an issue for bugs or questions
- **Discussions**: Use GitHub Discussions for general questions
- **Code**: Comment on relevant code sections

## License

By contributing, you agree that your contributions will be licensed under the same license as the project.

## Recognition

Contributors will be recognized in:
- GitHub contributors page
- Release notes for significant contributions
- Project documentation

Thank you for contributing! 🎉
