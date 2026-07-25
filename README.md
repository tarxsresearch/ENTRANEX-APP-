# Entranex Android (native)

Chunk 1: project skeleton + working Login/Signup screen wired to
`https://dextroposition2.onrender.com`.

## What's included
- Jetpack Compose UI matching the dark glassmorphism look of `index.html`
- Login + Signup screens with the same password-strength/validation rules
- JWT stored via `EncryptedSharedPreferences` (Android Keystore-backed —
  more secure than the web app's `localStorage`)
- Retrofit networking layer, ready to extend to posts/chat/popup backends
- GitHub Actions workflow that auto-builds a debug APK on every push

## How to use this
1. Push this whole folder to a new GitHub repo (root of the repo, not a subfolder).
2. Go to the repo's **Actions** tab — a build should start automatically.
3. When it finishes (green check), open the run, scroll to **Artifacts**,
   download `entranex-debug-apk` — that's a zip containing `app-debug.apk`.
4. Transfer the APK to your phone and install it (you'll need to allow
   "install unknown apps" for whichever app you use to open it).

## If the build fails
Open the failed Action run, expand the red step, and paste me the error —
first-build failures are normal (dependency version mismatches, SDK
licensing prompts, etc.) and are usually a one-line fix.

## Not built yet (next chunks)
- Feed screen (posts, likes, comments, share)
- Chat screen (WebSocket, conversations, reactions)
- Profile / other-profile / followers-following drawers
- Notifications, search, settings, weather screen
- Popup notification poller
