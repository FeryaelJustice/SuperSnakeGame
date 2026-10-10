# Privacy Policy for SuperSnakeGame

**Last updated:** October 10, 2026

**SuperSnakeGame** ("we", "our", or "the application") is an open-source arcade game developed by **Feryael Justice**. This Privacy Policy explains how personal and non-personal data is collected, used, shared, and protected when you use our mobile application.

We take your privacy seriously. SuperSnakeGame is designed with privacy in mind and adheres strictly to Google Play Developer Policies, including the User Data and Families policies.

---

## 1. Information We Collect

### A. Information You Provide Directly
When you choose to authenticate using Google Sign-In:
- **Google Account Profile Data:** We receive your display name, email address, and profile photo URL via Google Credential Manager and Firebase Authentication.
- **Game Records and High Scores:** When signed in, your highest score, timestamps, and associated user ID (`userId`) are recorded and synchronized to Firestore so you can view your personal record and compete on global leaderboards.

*Note: Authentication is entirely optional. You can play SuperSnakeGame anonymously without creating an account or providing any personal identification.*

### B. Information Collected Automatically
When you run the game, certain technical and usage information may be processed automatically by third-party services:
- **Firebase Analytics & Performance:** Device identifiers (such as Android Advertising ID or app instance ID), device model, OS version, general app performance metrics, session duration, and gameplay telemetry (e.g., games started, game over events).
- **Diagnostics and Crash Reports:** Crash logs, stack traces, and system diagnostics to help us identify and fix bugs.

---

## 2. How We Use Your Information

We use collected information solely for the following legitimate purposes:
1. **Gameplay and Progression:** To maintain your high scores and records across game sessions.
2. **Account Management:** To authenticate your identity when signing in with Google.
3. **App Improvement and Diagnostics:** To analyze technical errors, reduce crash rates, and improve overall app stability and performance.
4. **Leaderboards:** To display your score alongside your chosen display name in the game record tables.

We do **not** sell, rent, or trade your personal information to third parties.

---

## 3. Third-Party Services and SDKs

SuperSnakeGame utilizes reputable third-party services provided by Google LLC to manage backend infrastructure and analytics. These providers process data according to their respective privacy standards:

- **Google Play Services / Credential Manager:** Facilitates secure, one-tap authentication.  
  [Google Privacy Policy](https://policies.google.com/privacy)
- **Firebase Authentication:** Handles authentication tokens and user session persistence.  
  [Firebase Privacy & Security](https://firebase.google.com/support/privacy)
- **Cloud Firestore:** Stores high scores and user profile references securely.  
  [Firebase Privacy & Security](https://firebase.google.com/support/privacy)
- **Firebase Analytics:** Measures gameplay engagement and telemetry to improve game balance.  
  [Google Analytics for Firebase Data Collection](https://support.google.com/firebase/answer/6318039)

---

## 4. Permissions Requested

SuperSnakeGame does not request dangerous or intrusive runtime permissions (such as precise location, camera, microphone, or contacts). The app only requires standard network permissions:
- `android.permission.INTERNET`: Required to communicate with Firebase Auth, Cloud Firestore, and Google Play Services.
- `android.permission.ACCESS_NETWORK_STATE`: Required to detect network availability and avoid redundant cloud synchronization attempts when offline.

---

## 5. Data Retention and Deletion

We retain data only as long as necessary to provide the services described:
- **Game Records & Auth Credentials:** Stored in Cloud Firestore and Firebase Auth until you request deletion or your account is deleted.
- **Analytics Data:** Retained according to Firebase's default retention policies (typically 2 to 14 months).

### Requesting Data Deletion
Users have the right to request the deletion of their personal data (including account references and high scores stored in Firestore). To request complete account and data removal:
- Send an email to the developer contact provided below with the subject line `Data Deletion Request - SuperSnakeGame`, specifying the email address linked to your Google Sign-In account.
- Your data will be deleted within 30 business days.

---

## 6. Children's Privacy

SuperSnakeGame does not knowingly collect personally identifiable information from children under the age of 13 (or under the applicable age of consent in your jurisdiction). If you are a parent or guardian and believe that your child has provided personal information without consent, please contact us immediately so we can remove the data.

---

## 7. Security of Your Information

We prioritize protecting your information. All communications between SuperSnakeGame and Firebase backend services are encrypted in transit using Transport Layer Security (TLS/HTTPS). Cloud Firestore database rules enforce authentication checks so that only authorized users can update their own records.

---

## 8. Changes to This Privacy Policy

We may update this Privacy Policy from time to time. Any updates will be posted on this page with an updated "Last updated" date. We encourage you to review this policy periodically.

---

## 9. Source Code Transparency

SuperSnakeGame is open source. You can inspect the implementation, dependencies, and data handling directly in the public repository:
- **Repository:** [https://github.com/FeryaelJustice/SuperSnakeGame](https://github.com/FeryaelJustice/SuperSnakeGame)

---

## 10. Contact Us

If you have questions, feedback, or data requests regarding this Privacy Policy, you can reach out via:
- **GitHub Issues:** [https://github.com/FeryaelJustice/SuperSnakeGame/issues](https://github.com/FeryaelJustice/SuperSnakeGame/issues)
- **Developer:** Feryael Justice
- **Repository:** [https://github.com/FeryaelJustice/SuperSnakeGame](https://github.com/FeryaelJustice/SuperSnakeGame)
