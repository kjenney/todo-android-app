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

### 2. Build Signed Release APK (`build-signed-release.yml`)
**Trigger:** Runs only on version tags (e.g., `v1.0.0`)

**What it does:**
- Builds a signed release APK ready for distribution
- Automatically creates a GitHub Release with the APK attached
- Requires keystore secrets to be configured

**Setup Required:**
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

**How to create a release:**
```bash
git tag v1.0.0
git push origin v1.0.0
```

The workflow will automatically build and attach the signed APK to the GitHub release.

## Workflow Features

- ✅ Automatic caching of Gradle dependencies (faster builds)
- ✅ Uses latest GitHub Actions (v4)
- ✅ Java 17 (compatible with Gradle 8.2)
- ✅ Artifacts retained for 30-90 days
- ✅ Continue on error for optional steps
- ✅ Secure keystore handling (loaded from secrets, cleaned up after build)

## Build Status

You can add a build status badge to your README:

```markdown
![Build APK](https://github.com/YOUR_USERNAME/todoapp/workflows/Build%20APK/badge.svg)
```

## Local Testing

To test the workflow locally (requires Docker):
```bash
# Install act
brew install act

# Run the workflow
act -j build
```
