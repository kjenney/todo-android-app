# Documentation

This directory contains the MkDocs documentation for the Todo App.

## Viewing Documentation

### Online (GitHub)

Documentation markdown files can be viewed directly on GitHub:
- [Home](index.md)
- [Features](features.md)
- [Building](building.md)
- [And more...](../mkdocs.yml)

### Local Preview

To view the documentation as a website locally:

```bash
# Install dependencies
pip install -r requirements.txt

# Serve documentation locally
mkdocs serve

# Open browser to http://127.0.0.1:8000
```

The documentation site will auto-reload as you edit markdown files.

## Building Static Site

To build static HTML files:

```bash
# Build site
mkdocs build

# Output in site/ directory
# Open site/index.html in browser
```

## Documentation Structure

- **index.md** - Documentation home page
- **features.md** - Complete features list
- **building.md** - Build instructions (Docker and local)
- **testing.md** - Testing guide
- **architecture.md** - Technical architecture
- **database.md** - Database schema documentation
- **usage.md** - User guide
- **permissions.md** - Permissions and notification setup
- **widgets.md** - Home screen widgets documentation
- **contributing.md** - Contribution guidelines

## Editing Documentation

1. Edit markdown files in this directory
2. Follow existing formatting and structure
3. Preview changes with `mkdocs serve`
4. Commit and push changes

## MkDocs Configuration

Configuration is in `mkdocs.yml` at the project root:
- Site metadata
- Theme settings (Material theme)
- Navigation structure
- Markdown extensions

## Publishing to GitHub Pages

Documentation is automatically deployed to GitHub Pages on every push to `main` that modifies:
- Files in `docs/` directory
- `mkdocs.yml` configuration
- `requirements.txt` dependencies

**Live Documentation:** https://yourusername.github.io/todoapp/

### Automatic Deployment

The GitHub Actions workflow (`.github/workflows/docs.yml`) automatically:
1. Detects changes to documentation files
2. Builds the MkDocs site
3. Deploys to `gh-pages` branch
4. GitHub Pages serves the site

### Manual Deployment

You can also deploy manually:

```bash
# Deploy from local machine
mkdocs gh-deploy
```

Or trigger the workflow manually:
1. Go to GitHub Actions
2. Select "Deploy Documentation" workflow
3. Click "Run workflow"

### First-Time Setup

To enable GitHub Pages:
1. Go to repository Settings → Pages
2. Source: Deploy from a branch
3. Branch: `gh-pages` / `root`
4. Save

The documentation will be available at: `https://username.github.io/todoapp/`
