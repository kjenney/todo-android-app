# GitHub Actions Workflows

This directory contains automated build workflows for the Todo App.

## Workflows

### 1. Build APK (`build-apk.yml`)
**Trigger:** Runs on every push/PR to `main` branch

**What it does:**
- Builds debug APK automatically
- Attempts to build unsigned release APK
- Uploads APKs as artifacts (downloadable for 30 days)

**How to download:**
1. Go to GitHub repo → Actions tab
2. Click on the latest workflow run
3. Scroll to "Artifacts" section at the bottom
4. Download `todo-app-debug.apk`

### 2. Auto Release (`auto-release.yml`) 🆕
**Trigger:** Runs when changes are pushed to `main` that affect:
- `app/**` directory
- `build.gradle`
- `settings.gradle`
- `gradle.properties`

**What it does:**
- 🤖 Automatically determines semantic version bump based on commit messages
- 🏷️ Creates and pushes a new version tag
- 📋 Generates release notes from commit history
- 🚀 Triggers the signed release build workflow

**Semantic Versioning Logic:**
- **Major** (v2.0.0): Commits with `BREAKING CHANGE:`, `major:`, `!:`, or `[major]`
- **Minor** (v1.1.0): Commits with `feat:`, `feature:`, `minor:`, `[minor]`, `Add`, or `Added`
- **Patch** (v1.0.1): Commits with `fix:`, `bugfix:`, `patch:`, `[patch]`, `Fix`, `Fixed`, or `Update`

**Example Commit Messages:**
```bash
# Creates v1.1.0 (minor bump)
git commit -m "feat: Add calendar widget"

# Creates v1.0.1 (patch bump)
git commit -m "fix: Fix notification bug on Pixel devices"

# Creates v2.0.0 (major bump)
git commit -m "BREAKING CHANGE: Change database schema"
```

**How it works:**
1. Push changes to `main` branch
2. Workflow analyzes commit messages since last tag
3. Determines appropriate version bump
4. Creates new tag with semantic version
5. Automatically triggers build-signed-release workflow
6. Release appears in GitHub Releases with APK attached

### 3. Build Signed Release APK (`build-signed-release.yml`)
**Trigger:** Runs only on version tags (e.g., `v1.0.0`)
- Can be triggered manually by pushing a tag
- Automatically triggered by Auto Release workflow

**What it does:**
- Builds a signed release APK ready for distribution (if keystore configured)
- Falls back to unsigned APK if no keystore secrets available
- Automatically creates a GitHub Release with the APK attached
- Uploads APK as artifact (90 day retention)

**Setup Required (Optional - for signed releases):**
To enable signed releases, add these secrets to your GitHub repository:
(Settings → Secrets and variables → Actions → New repository secret)

1. `KEYSTORE_BASE64` - Base64 encoded keystore file
2. `SIGNING_KEY_ALIAS` - Keystore key alias
3. `SIGNING_KEY_PASSWORD` - Key password
4. `SIGNING_STORE_PASSWORD` - Keystore password

**How to encode your keystore:**
```bash
base64 -i your-keystore.jks | pbcopy
```
Then paste the output as the `KEYSTORE_BASE64` secret.

**Manual release creation:**
```bash
git tag v1.0.0
git push origin v1.0.0
```

The workflow will automatically build and attach the APK to the GitHub release.

## Workflow Features

- ✅ Automatic caching of Gradle dependencies (faster builds)
- ✅ Uses latest GitHub Actions (v4)
- ✅ Java 17 (compatible with Gradle 8.2)
- ✅ Artifacts retained for 30-90 days
- ✅ Continue on error for optional steps
- ✅ Secure keystore handling (loaded from secrets, cleaned up after build)
- 🆕 Automatic semantic versioning based on commit messages
- 🆕 Automatic release creation with generated notes
- 🆕 Smart version bump detection (major/minor/patch)

## Build Status

You can add a build status badge to your README:

```markdown
![Build APK](https://github.com/YOUR_USERNAME/todoapp/workflows/Build%20APK/badge.svg)
```

## Complete Release Flow

### Automatic Flow (Recommended)
1. Make changes to your app code
2. Commit with semantic commit message:
   ```bash
   git commit -m "feat: Add new feature X"
   ```
3. Push to main:
   ```bash
   git push origin main
   ```
4. Auto Release workflow runs:
   - Analyzes commits
   - Creates appropriate version tag (e.g., v1.1.0)
   - Pushes tag
5. Build Signed Release workflow automatically triggers:
   - Builds APK (signed if keystore configured)
   - Creates GitHub Release
   - Attaches APK to release

**That's it! Fully automated.**

### Manual Flow
1. Make and push changes
2. Manually create tag:
   ```bash
   git tag v1.2.3
   git push origin v1.2.3
   ```
3. Build Signed Release workflow triggers
4. Release created with APK

## Best Practices

### Commit Message Format
Follow Conventional Commits for best results:

- `feat: description` - New features (minor bump)
- `fix: description` - Bug fixes (patch bump)
- `docs: description` - Documentation only (patch bump)
- `refactor: description` - Code refactoring (patch bump)
- `BREAKING CHANGE: description` - Breaking changes (major bump)

### Example Workflow
```bash
# Feature addition
git add .
git commit -m "feat: Add dark mode support"
git push origin main
# → Creates v1.1.0 automatically

# Bug fix
git add .
git commit -m "fix: Fix crash on startup"
git push origin main
# → Creates v1.1.1 automatically

# Breaking change
git add .
git commit -m "BREAKING CHANGE: Migrate to new API"
git push origin main
# → Creates v2.0.0 automatically
```

## Local Testing

To test the workflow locally (requires Docker):
```bash
# Install act
brew install act

# Run the workflow
act -j build
```

## Troubleshooting

**Issue:** Auto-release doesn't create a tag
- **Solution:** Check that changes were in `app/**` or build files
- **Solution:** Verify commit messages follow expected patterns

**Issue:** Release workflow doesn't find APK
- **Solution:** Check Gradle build succeeded in workflow logs
- **Solution:** Verify APK path in workflow matches Gradle output

**Issue:** Can't download APK from release
- **Solution:** Wait for Build Signed Release workflow to complete
- **Solution:** Check workflow ran successfully (green checkmark)
