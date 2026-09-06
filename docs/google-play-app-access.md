# Google Play App Access Compliance Guide

This guide provides the complete solution to resolve the Google Play Console review inspection warning regarding **App Access** policy compliance.

---

## 1. Root Cause of the Warning

Google Play requires that all functionality of an app be accessible to automated test crawlers and human review teams.

### The Warning Received

> "Inspection info: To comply with Google Play policies, you must provide the information we need to access every part of your app. This allows our team to review your app for compliance before it reaches users. You can submit these details in the Play Console. Your credentials must remain active and accessible regardless of our reviewer's location. If your app uses dynamic security like two-factor authentication (2FA), provide a bypass or a static alternative. If you use non-text methods like QR codes, provide a static URL. For content behind paywalls, ensure we have full, free access to review those features.
>
> **Dos:**
> - Review our e-learning course on Login Credentials before submitting your app.
> - Disable 2FA or provide a bypass.
> - Include clear instructions if your app requires users to set up their own password (for example, a PIN).
> - Link to a static URL if you are providing a QR code for login access.
> - Ensure credentials work throughout the entire review.
>
> **Don'ts:**
> - Use credentials that expire during review.
> - Submit login details in non-English languages.
> - Forget instructions for third-party sign-ins.
> - Require reviewers to pay for subscriptions."

### Why Super Snake Game Triggered This Warning

1. **Gatekept Functionality**: The entire game is positioned behind the `MainMenuScreen`. A player cannot access the gameplay canvas without authenticating first.
2. **Third-Party Sign-In**: Authentication is handled exclusively through **Google Sign-In via Firebase** (`androidx.credentials` + `FirebaseAuth`).
3. **Missing or Incomplete Console Declaration**:
   - In Google Play Console under **App content -> App access**, either "All functionality is available without special access" was selected (which is incorrect because login is required), or
   - Instructions for the third-party Google Sign-In were omitted or provided in a non-English language ("Don't: Forget instructions for third-party sign-ins" / "Don't: Submit login details in non-English languages").

---

## 2. Step-by-Step Resolution in Google Play Console

To fix this rejection and allow your app to pass review immediately, update your declaration in the Google Play Console:

### Step 1: Navigate to App Access Settings

1. Log in to [Google Play Console](https://play.google.com/console).
2. Select **Super Snake Game** from your app list.
3. In the left navigation menu, scroll down to **Policy and programs** (or **Políticas y programas**).
4. Click on **App content** (or **Contenido de la app**).
5. Locate the section titled **App access** (or **Acceso a aplicaciones**) and click **Manage** (or **Administrar**).

### Step 2: Select Restricted Functionality

Select the option:
- **"All or some functionality in my app is restricted"** (or *"Todas o algunas funciones de mi aplicación están restringidas"*).

### Step 3: Add New Instructions

Click **+ Add instructions** (or **+ Agregar instrucciones**). A dialog will open asking for details.

Fill in the fields with the exact English instructions provided below:

#### Form Fields:

- **Instruction Name / Title**:
  ```text
  Google Sign-In Third-Party Reviewer Access
  ```

- **Account name / Username**:
  *(Provide a dedicated Google Test Account or state the reviewer Google Account)*:
  ```text
  supersnake.tester@gmail.com
  ```
  *(Note: Replace with your actual test Google account email, or see Option B below).*

- **Password**:
  *(Provide the password for the test account)*:
  ```text
  YourTestAccountPassword123!
  ```

- **Any other instructions (in English)**:
  Copy and paste the exact text below into the instructions box:

```text
1. Open app and tap "Sign in with Google" on main menu.
2. Select the provided test Google account (or any active Google account on the device).
3. No 2FA is required.
4. The app authenticates via Firebase and opens the game board immediately.
5. All features (snake movement, food dots, score, cloud high scores, restart, sign out) are fully accessible.
```

- **Requires 2FA / Dynamic Security**:
  Select **No** (Ensure 2-Step Verification is turned OFF for the test account to comply with the "Disable 2FA or provide a bypass" rule).

### Step 4: Save and Resubmit

Click **Save** and return to your release dashboard to submit the updated declaration for review.

---

## 3. Important Checklist for Google Sign-In on Firebase

For Google Sign-In to work on Google Play review devices without throwing `DEVELOPER_ERROR (10)`:

### A. Add Google Play App Signing SHA-1 to Firebase

When Google Play compiles your App Bundle (`.aab`), it re-signs the APK with the **Play App Signing key**. If that key is not in your Firebase project, Google Sign-In will fail for reviewers:

1. In Google Play Console, go to **Test and release** -> **Setup** -> **App signing**.
2. Copy the **SHA-1 certificate fingerprint** under **App signing key certificate**.
3. Also copy the **SHA-1 certificate fingerprint** under **Upload key certificate**.
4. Go to [Firebase Console](https://console.firebase.google.com/) -> **Project Settings** -> **General** -> **Your Android apps** (`com.feryaeljustice.supersnakegame`).
5. Click **Add fingerprint** and paste both SHA-1 fingerprints.
6. Download the latest `google-services.json` if needed.

### B. Verify OAuth 2.0 Web Client ID

Ensure the Web Client ID in `app/src/main/res/values/strings.xml` matches the **Web Client (Auto-created by Google Service)** found under Google Cloud Console / Firebase Authentication settings.

---

## 4. Alternative: In-App Guest / Reviewer Bypass Mode

Google Play guidelines explicitly state:
> *"If your app uses dynamic security like two-factor authentication (2FA), provide a bypass or a static alternative."*

If managing a permanent test Google account without 2FA is inconvenient, an industry best practice is to add an optional **"Play as Guest"** or hidden reviewer tap on the main menu:

- When tapped, the app signs in anonymously with Firebase (`firebaseAuth.signInAnonymously()`) or creates a local guest user session.
- Reviewers and test bots can tap this button and access 100% of the game instantly without entering any credentials.
- In Google Play Console App Access, you simply write:
  > *"Reviewers can tap the 'Play as Guest' button on the main screen to access all features immediately without requiring credentials."*
