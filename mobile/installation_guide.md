# 📱 How to Install the Smart Rover App

Follow these steps to get the app onto your Android phone.

## 1. Build the APK
Since this is a development project, you need to generate the installation file (APK):
1. In the project root, run the following command in your terminal:
   ```bash
   ./gradlew assembleDebug
   ```
2. Once finished, find the APK file here:
   `app/build/outputs/apk/debug/app-debug.apk`

## 2. Transfer to Phone
- **USB Cable**: Copy the `.apk` file to your phone's storage.
- **Cloud/Email**: Upload the `.apk` to Google Drive or email it to yourself.

## 3. Install on Android
1. Open the file manager on your phone.
2. Locate and tap the `app-debug.apk`.
3. If prompted, enable **"Allow installation from unknown sources"** in your phone settings.
4. Tap **Install**.

## 4. Developer Mode (Optional)
For the best experience:
1. Enable **Developer Options** on your phone (Tap "Build Number" 7 times in About Phone).
2. Enable **USB Debugging**.
3. Connect your phone to your computer and run directly from Android Studio or using `./gradlew installDebug`.
