# Authentication & Home Fragment Guide

**Last Updated:** December 16, 2025  
**Project:** FilmSpace Mobile App

---

## 📋 Table of Contents
1. [Authentication Flow Overview](#authentication-flow-overview)
2. [Detailed Authentication Flows](#detailed-authentication-flows)
3. [User Session Management](#user-session-management)
4. [Working with Stored User Data](#working-with-stored-user-data)
5. [Home Fragment](#home-fragment)
6. [Important Notes for Developers](#important-notes-for-developers)

---

## 🔐 Authentication Flow Overview

### Entry Point: **MainActivity** (Launcher)
- **Guest Users**: Can browse movies/genres without authentication
- **Logged-in Users**: Automatically redirected to authenticated features
- Session persistence: Users remain logged in across app restarts

### Available Authentication Flows
1. **Sign In Flow** → Login with existing account
2. **Register Flow** → Create new account with OTP verification
3. **Forgot Password Flow** → Reset password via OTP verification

---

## 📝 Detailed Authentication Flows

### 1️⃣ Sign In Flow
**File:** `SignInFragment.java`

**Steps:**
1. User enters email and password
2. **Input Validation:**
   - Email: Required, valid email format
   - Password: Required, min 6 characters, trimmed before submission
3. Click "Sign In" button
4. **API Call:** `POST /api/auth/login`
   - Request: `{ "email": "user@example.com", "password": "password123" }`
   - Response: `{ "success": true, "token": "Bearer...", "user": {...} }`
5. **On Success:**
   - Save user session via `UserSessionManager.saveUserSession()`
   - Navigate to `MainActivity` (with fragment back stack cleared)
6. **On Failure:**
   - Display error message from API

**Navigations:**
- "Don't have an account? Sign Up" → `RegisterFragment`
- "Forgot Password?" → `ForgotPasswordFragment`

---

### 2️⃣ Register Flow
**Files:** `RegisterFragment.java` → `OTPVerificationFragment.java`

**Step 1: RegisterFragment**
1. User fills in registration form:
   - **Fullname**: Required, English letters only (a-z, A-Z, spaces)
   - **Username**: Required, min 3 chars, no spaces allowed
   - **Email**: Required, valid email format
   - **Password**: Required, min 6 characters
   - **Confirm Password**: Must match password
2. All inputs are trimmed before validation
3. Click "Continue" button
4. **API Call:** `POST /api/auth/register`
   - Request: `{ "fullName": "John Doe", "username": "johndoe", "email": "john@example.com", "password": "password123" }`
   - Response: `{ "success": true, "message": "OTP sent to email", "email": "john@example.com" }`
5. **On Success:** Navigate to `OTPVerificationFragment` with email bundle

**Step 2: OTPVerificationFragment**
1. User enters 6-digit OTP (auto-focus between fields)
2. OTP is sent to user's email
3. Click "Verify" button
4. **API Call:** `POST /api/auth/verify-otp`
   - Request: `{ "email": "john@example.com", "otp": "123456" }`
   - Response: `{ "success": true, "token": "Bearer...", "user": {...} }`
5. **On Success:**
   - Save user session via `UserSessionManager.saveUserSession()`
   - Navigate to `MainActivity` (authenticated)
6. **Features:**
   - Auto-focus to next field on digit entry
   - Delete key focuses previous field
   - Resend OTP option (30-second cooldown)

**Navigations:**
- "Already have an account? Sign In" (RegisterFragment) → `SignInFragment`

---

### 3️⃣ Forgot Password Flow
**Files:** `ForgotPasswordFragment.java` → `CreateNewPasswordFragment.java`

**Step 1: ForgotPasswordFragment**
1. User enters registered email
2. Click "Continue" button
3. **API Call:** `POST /api/auth/forgot-password`
   - Request: `{ "email": "user@example.com" }`
   - Response: `{ "success": true, "message": "OTP sent to email" }`
4. **On Success:** Navigate to `CreateNewPasswordFragment` with email bundle

**Step 2: CreateNewPasswordFragment**
1. User fills in:
   - **OTP**: 6-digit code sent to email
   - **New Password**: Min 6 characters, trimmed
   - **Confirm Password**: Must match new password, trimmed
2. Click "Continue" button
3. **API Call:** `POST /api/auth/reset-password`
   - Request: `{ "email": "user@example.com", "otp": "123456", "newPassword": "newpass123" }`
   - Response: `{ "success": true, "message": "Password reset successful" }`
4. **On Success:**
   - Display success message
   - Navigate to `SignInFragment`
5. **Features:**
   - Resend OTP option (30-second cooldown)

**Navigations:**
- "Remember your password? Sign In" (ForgotPasswordFragment) → `SignInFragment`

---

## 💾 User Session Management

### SessionManager Class
**File:** `UserSessionManager.java`  
**Location:** `com.example.filmspace_mobile.data.local`

### Storage Mechanism
- **Technology:** Android SharedPreferences
- **Persistence:** Data persists across app restarts until cleared
- **Single User Policy:** ⚠️ **Only 1 user can be stored at a time** - old user data is automatically cleared when saving new user

### Stored User Data
```java
{
    "isLoggedIn": true,
    "userId": 123,
    "username": "johndoe",
    "email": "john@example.com",
    "avatarUrl": "https://...",
    "name": "John Doe",
    "token": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Key Methods

#### ✅ Save User Session (Login/Register)
```java
UserSessionManager sessionManager = new UserSessionManager(context);
sessionManager.saveUserSession(
    userId,        // int
    username,      // String
    email,         // String
    avatarUrl,     // String
    name,          // String
    token          // String (Bearer token)
);
```
**Important:** This method automatically clears any existing user data before saving the new user.

#### ✅ Check Login Status
```java
boolean isLoggedIn = sessionManager.isLoggedIn();
if (isLoggedIn) {
    // User is authenticated
} else {
    // Redirect to login
}
```

#### ✅ Get User Data
```java
int userId = sessionManager.getUserId();           // Returns -1 if not found
String username = sessionManager.getUsername();     // Returns null if not found
String email = sessionManager.getEmail();           // Returns null if not found
String avatarUrl = sessionManager.getAvatarUrl();   // Returns null if not found
String name = sessionManager.getName();             // Returns null if not found
String token = sessionManager.getToken();           // Returns null if not found
```

#### ✅ Logout / Clear Session
```java
sessionManager.clearSession();
// All user data is removed
// User will need to login again
```

#### ✅ Update Avatar (Optional)
```java
sessionManager.updateAvatarUrl("https://new-avatar-url.com/image.jpg");
```

---

## 🏠 Home Fragment

**File:** `HomeFragment.java`  
**Purpose:** Main screen displaying movies and genres after user navigates from MainActivity

### Features
- **Movie List:** Displays all available movies (fetched from API)
- **Genre List:** Displays all movie genres (fetched from API)
- **Guest Access:** No authentication required to view content
- **Authenticated Access:** Additional features available for logged-in users (future implementation)

### Data Fetching
```java
// ViewModels used
MovieViewModel movieViewModel;
GenreViewModel genreViewModel;

// Observers
movieViewModel.getMovies().observe(viewLifecycleOwner, movies -> {
    // Update movie RecyclerView
});

genreViewModel.getGenres().observe(viewLifecycleOwner, genres -> {
    // Update genre RecyclerView
});
```

### Current State
- **No User-Specific UI:** Greeting/profile features removed
- **Public Content:** Movies and genres visible to all users
- **Session Independent:** Works for both guest and authenticated users

---

## ⚠️ Important Notes for Developers

### 1. Authentication Token (Bearer Token)
**Usage:** All authenticated API requests must include the Bearer token in the Authorization header.

**Implementation:** `AuthInterceptor.java` automatically adds the token:
```java
// AuthInterceptor automatically handles this
String token = sessionManager.getToken();
if (token != null) {
    chain.proceed(request.newBuilder()
        .addHeader("Authorization", token)
        .build());
}
```

**Auto-Logout:** If API returns 401 (Unauthorized) or 403 (Forbidden):
- User session is automatically cleared
- User is considered logged out
- Use case: Admin deleted the user account

### 2. Input Validation Rules
All authentication forms enforce these rules:

| Field | Validation Rules |
|-------|-----------------|
| **Fullname** | Required, English letters only (a-z, A-Z, spaces) |
| **Username** | Required, min 3 chars, no spaces allowed |
| **Email** | Required, valid email format |
| **Password** | Required, min 6 characters |
| **All Inputs** | Trimmed before submission (no leading/trailing spaces) |

### 3. Error Handling
- **Network Errors:** Display user-friendly messages
- **API Errors:** Show error message from API response
- **Validation Errors:** Show inline errors on TextInputLayout fields

### 4. Loading States
All authentication fragments show ProgressBar during API calls:
- Continue/Submit buttons are disabled
- ProgressBar is visible
- User cannot interact with form

### 5. Navigation Flow
```
MainActivity (Launcher)
├── Guest Users → HomeFragment (browse content)
└── Auth Users → HomeFragment (authenticated features)

Auth Flows:
├── SignInFragment ──────────────────→ MainActivity
├── RegisterFragment → OTPVerification → MainActivity
└── ForgotPassword → CreateNewPassword → SignInFragment
```

### 6. Session Lifecycle
- **App Launch:** MainActivity checks `sessionManager.isLoggedIn()`
- **Background:** Session persists (SharedPreferences)
- **Logout:** Manual logout or auto-logout on 401/403
- **Re-Login:** Old user data automatically replaced with new user

### 7. Backend Integration
Ensure your backend API endpoints match these contracts:

**POST /api/auth/login**
```json
Request: { "email": "string", "password": "string" }
Response: { "success": bool, "token": "string", "user": {...} }
```

**POST /api/auth/register**
```json
Request: { "fullName": "string", "username": "string", "email": "string", "password": "string" }
Response: { "success": bool, "message": "string", "email": "string" }
```

**POST /api/auth/verify-otp**
```json
Request: { "email": "string", "otp": "string" }
Response: { "success": bool, "token": "string", "user": {...} }
```

**POST /api/auth/forgot-password**
```json
Request: { "email": "string" }
Response: { "success": bool, "message": "string" }
```

**POST /api/auth/reset-password**
```json
Request: { "email": "string", "otp": "string", "newPassword": "string" }
Response: { "success": bool, "message": "string" }
```

### 8. User Object Structure
```java
{
    "userId": 123,
    "username": "johndoe",
    "email": "john@example.com",
    "fullName": "John Doe",
    "avatarUrl": "https://...",
    "createdAt": "2025-12-16T10:30:00Z"
}
```

---

## 🚀 Next Steps for Team

### For Backend Developers
1. Ensure all API endpoints return proper error messages
2. Implement token expiration and refresh logic
3. Add email OTP sending functionality
4. Handle user deletion (should return 401/403 for deleted users)

### For Frontend Developers
1. **Profile Features:** Implement user profile screen (view/edit profile)
2. **Favorite Movies:** Add movie favorites functionality for authenticated users
3. **Watchlist:** Implement watchlist feature
4. **Reviews/Ratings:** Add movie review and rating system
5. **Social Features:** Follow users, share movies, etc.

### For QA/Testing
1. Test all authentication flows
2. Verify session persistence across app restarts
3. Test auto-logout on deleted user accounts
4. Validate input sanitization (fullname, username, passwords)
5. Test OTP resend functionality and cooldowns

---

## 📞 Questions?
Contact the authentication module maintainer or refer to the codebase files mentioned in this guide.

---

**File Structure Reference:**
```
app/src/main/java/com/example/filmspace_mobile/
├── ui/auth/
│   ├── SignInFragment/
│   ├── RegisterFragment/
│   ├── OTPVerificationFragment/
│   ├── ForgotPasswordFragment/
│   └── CreateNewPasswordFragment/
├── ui/home/
│   └── HomeFragment/
├── data/
│   ├── local/
│   │   └── UserSessionManager.java
│   └── remote/
│       ├── RetrofitClient.java
│       └── AuthInterceptor.java
└── viewmodels/
    └── AuthViewModel.java
```
