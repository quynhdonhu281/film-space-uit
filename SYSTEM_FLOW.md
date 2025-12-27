# FilmSpace System Flow Documentation

## Table of Contents
1. [Application Architecture](#application-architecture)
2. [Technology Stack](#technology-stack)
3. [System Entry Point](#system-entry-point)
4. [Main Flows](#main-flows)
5. [Data Flow Architecture](#data-flow-architecture)
6. [Authentication Flow](#authentication-flow)
7. [Movie Discovery Flow](#movie-discovery-flow)
8. [User Profile & Settings Flow](#user-profile--settings-flow)
9. [API Integration](#api-integration)
10. [State Management](#state-management)

---

## Application Architecture

FilmSpace follows **MVVM (Model-View-ViewModel)** architecture pattern with **Repository Pattern** for data management.

```
┌─────────────────────────────────────────────────────┐
│                    Presentation Layer                │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐ │
│  │  Activity   │  │  Fragment   │  │  Adapter    │ │
│  └─────────────┘  └─────────────┘  └─────────────┘ │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│                   ViewModel Layer                    │
│  ┌──────────────────────────────────────────────┐  │
│  │         LiveData + Data Binding              │  │
│  └──────────────────────────────────────────────┘  │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│                  Repository Layer                    │
│  ┌─────────────┐  ┌─────────────┐  ┌────────────┐  │
│  │    Auth     │  │    Movie    │  │   Genre    │  │
│  │ Repository  │  │ Repository  │  │ Repository │  │
│  └─────────────┘  └─────────────┘  └────────────┘  │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│                   Network Layer                      │
│  ┌──────────────────────────────────────────────┐  │
│  │       Retrofit + ApiService + OkHttp         │  │
│  └──────────────────────────────────────────────┘  │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│                    Backend API                       │
│                   (REST API)                         │
└──────────────────────────────────────────────────────┘
```

---

## Technology Stack

### Frontend (Android)
- **Language**: Java
- **UI Framework**: Android SDK, Material Design
- **Architecture**: MVVM + Repository Pattern
- **Dependency Injection**: Hilt (Dagger)
- **Image Loading**: Glide
- **Networking**: Retrofit2 + OkHttp3
- **Data Binding**: Android Data Binding
- **Async**: LiveData + Callbacks

### Key Libraries
```gradle
- Hilt (Dependency Injection)
- Retrofit2 (REST API Client)
- Glide (Image Loading)
- Material Design Components
- ViewPager2 (Slider)
- RecyclerView (Lists)
```

---

## System Entry Point

### App Launch Flow

```
┌────────────────────────────────────────────┐
│     FilmSpaceApplication (onCreate)        │
│  - Initialize Hilt Dependency Injection    │
└──────────────────┬─────────────────────────┘
                   │
┌──────────────────▼─────────────────────────┐
│           MainActivity (Launcher)           │
│  - Bottom Navigation Setup                 │
│  - Default: HomeFragment                   │
└──────────────────┬─────────────────────────┘
                   │
        ┌──────────┴──────────┐
        │                     │
┌───────▼─────────┐    ┌─────▼──────────┐
│  HomeFragment   │    │ Bottom Nav Bar  │
│  (Default View) │    │  - Home         │
└─────────────────┘    │  - Search       │
                       │  - Favorite     │
                       │  - Profile      │
                       └─────────────────┘
```

**Key File**: [MainActivity.java](app/src/main/java/com/example/filmspace_mobile/ui/main/MainActivity.java)

---

## Main Flows

### 1. Home Fragment Flow

```
┌─────────────────────────────────────────────────┐
│              HomeFragment Launch                │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│            HomeViewModel.init()                 │
│  - fetchAllMovies()                             │
│  - fetchGenres()                                │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│          MovieRepository.getAllMovies()         │
│  - API Call: GET /api/Movies                    │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│         Process Movie Data in ViewModel         │
│  1. Slider Movies (ViewPager2 Auto-scroll)     │
│  2. Recommended Movies (Horizontal Scroll)      │
│  3. Top Rating Movies (Vertical Scroll)         │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│           Update UI via LiveData                │
│  - MovieSliderAdapter (Top banner)              │
│  - MovieHorizontalAdapter (Recommended)         │
│  - MovieAdapter (Top Rating - Vertical)         │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│          User Clicks on Movie Card              │
│  → Navigate to MovieDetailActivity              │
└─────────────────────────────────────────────────┘
```

**Key Components**:
- [HomeFragment.java](app/src/main/java/com/example/filmspace_mobile/ui/main/HomeFragment/HomeFragment.java)
- [HomeViewModel.java](app/src/main/java/com/example/filmspace_mobile/ui/main/HomeFragment/HomeViewModel.java)
- [MovieRepository.java](app/src/main/java/com/example/filmspace_mobile/data/repository/MovieRepository.java)

**UI Features**:
- **Auto-scrolling ViewPager2** at the top (featured movies)
- **Horizontal RecyclerView** for recommended movies (if available)
- **Vertical RecyclerView** for top-rated movies with ratings

---

### 2. Search Fragment Flow

```
┌─────────────────────────────────────────────────┐
│            SearchFragment Launch                │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│         SearchViewModel.fetchMovies()           │
│  - Load all movies initially                    │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│      Display ALL Movies in Grid Layout          │
│  (No search input → Show all top rating)        │
└──────────────────┬──────────────────────────────┘
                   │
       ┌───────────┴────────────┐
       │                        │
┌──────▼─────────┐      ┌──────▼──────────┐
│  User Types    │      │  No Search      │
│  Search Query  │      │  (Empty Input)  │
└──────┬─────────┘      └──────┬──────────┘
       │                        │
┌──────▼────────────────────────▼──────────┐
│   SearchViewModel.filterMovies()         │
│  - Filter by title containing query      │
└──────────────────┬───────────────────────┘
                   │
       ┌───────────┴────────────┐
       │                        │
┌──────▼──────────┐    ┌────────▼─────────┐
│  Movies Found   │    │  No Movies Found │
│  Display Grid   │    │  Show "Sorry"    │
└──────┬──────────┘    └──────────────────┘
       │
┌──────▼──────────┐
│  Click Movie    │
│  → Movie Detail │
└─────────────────┘
```

**Key Components**:
- [SearchFragment.java](app/src/main/java/com/example/filmspace_mobile/ui/main/SearchFragment/SearchFragment.java)
- [SearchViewModel.java](app/src/main/java/com/example/filmspace_mobile/ui/main/SearchFragment/SearchViewModel.java)
- [SearchGridAdapter.java](app/src/main/java/com/example/filmspace_mobile/ui/adapters/SearchGridAdapter.java)

**UI Features**:
- **GridLayout** with wrap behavior
- **Real-time search** filtering
- **Empty state** message when no results

---

### 3. Favorite (History) Fragment Flow

```
┌─────────────────────────────────────────────────┐
│          FavoriteFragment Launch                │
│  (Actually shows Watch History)                 │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│  Check User Login Status (UserSessionManager)  │
└──────────────────┬──────────────────────────────┘
                   │
       ┌───────────┴────────────┐
       │                        │
┌──────▼──────────┐    ┌────────▼─────────┐
│  User Logged In │    │ User Not Logged  │
└──────┬──────────┘    └────────┬─────────┘
       │                        │
┌──────▼──────────────────┐    │
│ FavoriteViewModel        │    │
│ .fetchWatchHistory()     │    │
└──────┬──────────────────┘    │
       │                        │
┌──────▼──────────────────┐    │
│ HistoryRepository        │    │
│ .getWatchHistory()       │    │
│ API: GET /api/history/   │    │
│      watched             │    │
└──────┬──────────────────┘    │
       │                        │
┌──────▼──────────────────┐    ┌▼─────────────────┐
│ Display Watch History   │    │ Show Login       │
│ in Grid Layout          │    │ Required Message │
│ (Like Search)           │    └──────────────────┘
└──────┬──────────────────┘
       │
┌──────▼──────────┐
│  Click Movie    │
│  → Movie Detail │
└─────────────────┘
```

**Key Components**:
- [FavoriteFragment.java](app/src/main/java/com/example/filmspace_mobile/ui/main/FavoriteFragment/FavoriteFragment.java)
- [FavoriteViewModel.java](app/src/main/java/com/example/filmspace_mobile/ui/main/FavoriteFragment/FavoriteViewModel.java)
- [HistoryRepository.java](app/src/main/java/com/example/filmspace_mobile/data/repository/HistoryRepository.java)

**UI Features**:
- Shows **user's watch history**
- Same grid layout as Search
- Requires authentication

---

### 4. Profile Fragment Flow

```
┌─────────────────────────────────────────────────┐
│            ProfileFragment Launch               │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│  Check User Login Status (UserSessionManager)  │
└──────────────────┬──────────────────────────────┘
                   │
       ┌───────────┴────────────┐
       │                        │
┌──────▼──────────┐    ┌────────▼─────────────────┐
│  User Logged In │    │  User Not Logged In      │
└──────┬──────────┘    └────────┬─────────────────┘
       │                        │
┌──────▼────────────────────┐  │
│ Display Profile Options:  │  │
│ - User Info (Avatar, etc) │  │
│ - Edit Profile            │  │
│ - Change Password         │  │
│ - Settings (like below)   │  │
│ - Logout Button           │  │
└──────┬────────────────────┘  │
       │                        │
       │               ┌────────▼─────────────────┐
       │               │ Display Only:            │
       │               │ - Login Button           │
       │               │ - Register Button        │
       │               └────────┬─────────────────┘
       │                        │
       │                        │
┌──────▼────────────────────────▼──────────────────┐
│           User Action Handling                   │
└──────┬───────────────────────────────────────────┘
       │
       ├── Edit Profile → EditProfileActivity
       │   (Update name, email, avatar)
       │
       ├── Change Password → ChangePasswordActivity
       │   (OTP verification flow)
       │
       ├── Settings → SettingActivity
       │   (Notifications, Security, Help, Legal, Language)
       │
       ├── Login → AuthActivity (SignInFragment)
       │
       ├── Register → AuthActivity (RegisterFragment)
       │
       └── Logout → Clear session & refresh UI
```

**Key Components**:
- [ProfileFragment.java](app/src/main/java/com/example/filmspace_mobile/ui/main/ProfileFragment/ProfileFragment.java)
- [ProfileViewModel.java](app/src/main/java/com/example/filmspace_mobile/ui/main/ProfileFragment/ProfileViewModel.java)
- [UserSessionManager.java](app/src/main/java/com/example/filmspace_mobile/data/local/UserSessionManager.java)

**Profile Actions**:
1. **Edit Profile**: Update user info & avatar
2. **Change Password**: OTP-based password reset
3. **Settings**: App preferences
4. **Logout**: Clear session data

---

## Data Flow Architecture

### MVVM + Repository Pattern

```
┌─────────────────────────────────────────────────┐
│                   UI Layer                      │
│  (Activity/Fragment observes LiveData)          │
└──────────────────┬──────────────────────────────┘
                   │
                   │ User Action
                   ▼
┌─────────────────────────────────────────────────┐
│                ViewModel Layer                  │
│  - Holds UI State (LiveData)                    │
│  - Business Logic                               │
│  - Calls Repository methods                     │
└──────────────────┬──────────────────────────────┘
                   │
                   │ Data Request
                   ▼
┌─────────────────────────────────────────────────┐
│              Repository Layer                   │
│  - Data Source abstraction                      │
│  - Calls ApiService                             │
│  - Returns data via RepositoryCallback          │
└──────────────────┬──────────────────────────────┘
                   │
                   │ Network Request
                   ▼
┌─────────────────────────────────────────────────┐
│               Network Layer                     │
│  ApiService (Retrofit) + AuthInterceptor        │
│  - Makes HTTP requests                          │
│  - Handles authentication tokens                │
└──────────────────┬──────────────────────────────┘
                   │
                   │ HTTP Request
                   ▼
┌─────────────────────────────────────────────────┐
│                Backend API                      │
│  REST API (ASP.NET / Node.js)                   │
└─────────────────────────────────────────────────┘
```

### Example: Fetching Movies

```java
// 1. UI Layer (Fragment)
homeViewModel.getMovies().observe(viewLifecycleOwner, movies -> {
    if (movies != null) {
        movieAdapter.setMovies(movies);
    }
});

// 2. ViewModel Layer
public void fetchMovies() {
    loadingLiveData.setValue(true);
    movieRepository.getAllMovies(new RepositoryCallback<List<Movie>>() {
        @Override
        public void onSuccess(List<Movie> data) {
            moviesLiveData.setValue(data);
            loadingLiveData.setValue(false);
        }
        
        @Override
        public void onError(String error) {
            errorLiveData.setValue(error);
            loadingLiveData.setValue(false);
        }
    });
}

// 3. Repository Layer
public void getAllMovies(RepositoryCallback<List<Movie>> callback) {
    apiService.getAllMovies().enqueue(new Callback<List<Movie>>() {
        @Override
        public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
            if (response.isSuccessful()) {
                callback.onSuccess(response.body());
            } else {
                callback.onError("Failed to load movies");
            }
        }
        
        @Override
        public void onFailure(Call<List<Movie>> call, Throwable t) {
            callback.onError("Network error");
        }
    });
}

// 4. Network Layer (Retrofit Interface)
@GET("api/Movies")
Call<List<Movie>> getAllMovies();
```

---

## Authentication Flow

### 1. Registration Flow

```
┌─────────────────────────────────────────────────┐
│     User Opens AuthActivity (Register)          │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│      RegisterFragment (Input Form)              │
│  - Email, Password, Username, Full Name         │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│   RegisterViewModel.register()                  │
│   → AuthRepository.register()                   │
│   → API: POST /api/Auth/register                │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│     Navigate to OTPVerificationFragment         │
│  (Email sent with OTP code)                     │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│      User Enters OTP Code                       │
│  OTPVerificationViewModel.verifyOTP()           │
│  → API: POST /api/Auth/verify-otp               │
└──────────────────┬──────────────────────────────┘
                   │
       ┌───────────┴────────────┐
       │                        │
┌──────▼───────┐       ┌────────▼────────┐
│ OTP Valid    │       │  OTP Invalid    │
│ → Login User │       │  → Show Error   │
└──────┬───────┘       └─────────────────┘
       │
┌──────▼──────────────────────────────────────────┐
│  Save Session (UserSessionManager)              │
│  → Navigate to MainActivity                     │
└─────────────────────────────────────────────────┘
```

**Key Components**:
- [RegisterFragment.java](app/src/main/java/com/example/filmspace_mobile/ui/auth/RegisterFragment/RegisterFragment.java)
- [OTPVerificationFragment.java](app/src/main/java/com/example/filmspace_mobile/ui/auth/OTPVerificationFragment/OTPVerificationFragment.java)
- [AuthRepository.java](app/src/main/java/com/example/filmspace_mobile/data/repository/AuthRepository.java)

---

### 2. Login Flow

```
┌─────────────────────────────────────────────────┐
│      User Opens AuthActivity (Login)            │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│       SignInFragment (Input Form)               │
│  - Email, Password                              │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│   SignInViewModel.login()                       │
│   → AuthRepository.login()                      │
│   → API: POST /api/Auth/login                   │
└──────────────────┬──────────────────────────────┘
                   │
       ┌───────────┴────────────┐
       │                        │
┌──────▼────────┐      ┌────────▼──────────┐
│ Login Success │      │  Login Failed     │
└──────┬────────┘      └────────┬──────────┘
       │                        │
┌──────▼──────────────────┐    │
│ Save Session:           │    │
│ - User ID               │    │
│ - Username              │    │
│ - Email                 │    │
│ - JWT Token             │    │
│ (UserSessionManager)    │    │
└──────┬──────────────────┘    │
       │                        │
┌──────▼──────────────────┐    ┌▼────────────────┐
│ Navigate to MainActivity│    │ Show Error      │
└─────────────────────────┘    └─────────────────┘
```

**Key Components**:
- [SignInFragment.java](app/src/main/java/com/example/filmspace_mobile/ui/auth/SignInFragment/SignInFragment.java)
- [SignInViewModel.java](app/src/main/java/com/example/filmspace_mobile/ui/auth/SignInFragment/SignInViewModel.java)
- [UserSessionManager.java](app/src/main/java/com/example/filmspace_mobile/data/local/UserSessionManager.java)

---

### 3. Forgot Password Flow

```
┌─────────────────────────────────────────────────┐
│   User Clicks "Forgot Password" on Login       │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│     ForgotPasswordFragment (Enter Email)        │
│  ForgotPasswordViewModel.forgotPassword()       │
│  → API: POST /api/Auth/forgot-password          │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│      Navigate to OTPVerificationFragment        │
│  (OTP sent to email)                            │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│         User Enters OTP Code                    │
│  OTPVerificationViewModel.verifyOTP()           │
│  → API: POST /api/Auth/verify-otp               │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│    Navigate to CreateNewPasswordFragment        │
│  User enters new password & confirm             │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│  CreateNewPasswordViewModel.resetPassword()     │
│  → API: POST /api/Auth/reset-password           │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│      Password Reset Success                     │
│  → Navigate back to Login                       │
└─────────────────────────────────────────────────┘
```

**Key Components**:
- [ForgotPasswordFragment.java](app/src/main/java/com/example/filmspace_mobile/ui/auth/ForgotPasswordFragment/ForgotPasswordFragment.java)
- [OTPVerificationFragment.java](app/src/main/java/com/example/filmspace_mobile/ui/auth/OTPVerificationFragment/OTPVerificationFragment.java)
- [CreateNewPasswordFragment.java](app/src/main/java/com/example/filmspace_mobile/ui/auth/CreateNewPasswordFragment/CreateNewPasswordFragment.java)

---

## Movie Discovery Flow

### Movie Detail Flow

```
┌─────────────────────────────────────────────────┐
│   User Clicks Movie Card (from anywhere)        │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│       Navigate to MovieDetailActivity           │
│  Pass movieId via Intent                        │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│    MovieViewModel.fetchMovieById(movieId)       │
│  → MovieRepository.getMovieById()               │
│  → API: GET /api/Movies/{id}                    │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│         Display Movie Details in Tabs           │
│  TabLayout + ViewPager2                         │
└──────────────────┬──────────────────────────────┘
                   │
       ┌───────────┼───────────┐
       │           │           │
┌──────▼─────┐ ┌──▼─────┐ ┌───▼────────┐
│  About Tab │ │Episodes│ │ Reviews    │
└──────┬─────┘ └──┬─────┘ └───┬────────┘
       │          │            │
       │          │            │
┌──────▼──────────▼────────────▼──────────────────┐
│          MovieAboutFragment                     │
│  - Title, Poster, Rating                        │
│  - Overview, Release Date                       │
│  - Season/Episode Count                         │
│  - Cast List (Horizontal)                       │
│  - Genres                                       │
└─────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────┐
│         MovieEpisodeFragment                    │
│  - List of Episodes (RecyclerView)              │
│  - Episode Number, Title, Duration              │
│  - Click → Play Video (External Player)         │
└─────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────┐
│         MovieReviewFragment                     │
│  - List of User Reviews                         │
│  - Rating, Comment, User Info                   │
│  - Option to Add Review (if logged in)          │
└─────────────────────────────────────────────────┘
```

**Key Components**:
- [MovieDetailActivity.java](app/src/main/java/com/example/filmspace_mobile/ui/movie/MovieDetailActivity.java)
- [MovieAboutFragment.java](app/src/main/java/com/example/filmspace_mobile/ui/movie/MovieAboutFragment.java)
- [MovieEpisodeFragment.java](app/src/main/java/com/example/filmspace_mobile/ui/movie/MovieEpisodeFragment.java)
- [MovieReviewFragment.java](app/src/main/java/com/example/filmspace_mobile/ui/movie/MovieReviewFragment.java)

---

### Movies by Genre Flow

```
┌─────────────────────────────────────────────────┐
│   User Clicks Genre from HomeFragment           │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│     Navigate to MoviesByGenreActivity           │
│  Pass genreId & genreName via Intent            │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│  MovieViewModel.getMoviesByGenre(genreId)       │
│  → MovieRepository.getMoviesByGenre()           │
│  → API: GET /api/Movies/genre/{id}              │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│  Display Movies in Grid Layout                  │
│  (Similar to Search Fragment)                   │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│  User Clicks Movie → MovieDetailActivity        │
└─────────────────────────────────────────────────┘
```

**Key Components**:
- [MoviesByGenreActivity.java](app/src/main/java/com/example/filmspace_mobile/ui/movie/MoviesByGenreActivity.java)
- [MovieViewModel.java](app/src/main/java/com/example/filmspace_mobile/viewmodel/MovieViewModel.java)

---

## User Profile & Settings Flow

### Edit Profile Flow

```
┌─────────────────────────────────────────────────┐
│    User Clicks "Edit Profile" from Profile     │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│      Navigate to EditProfileActivity            │
│  Display current user information               │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│      User Modifies:                             │
│  - Username                                     │
│  - Email                                        │
│  - Name                                         │
│  - Avatar (Image Picker)                        │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│  EditProfileViewModel.updateUser()              │
│  → UserRepository.updateUser()                  │
│  → API: PUT /api/users/{userId}                 │
│  (Multipart if avatar changed)                  │
└──────────────────┬──────────────────────────────┘
                   │
       ┌───────────┴────────────┐
       │                        │
┌──────▼────────┐      ┌────────▼──────────┐
│ Update Success│      │  Update Failed    │
│ - Save Session│      │  - Show Error     │
│ - Go Back     │      └───────────────────┘
└───────────────┘
```

**Key Components**:
- [EditProfileActivity.java](app/src/main/java/com/example/filmspace_mobile/ui/setting/EditProfileActivity.java)
- [UserRepository.java](app/src/main/java/com/example/filmspace_mobile/data/repository/UserRepository.java)

---

### Change Password Flow

```
┌─────────────────────────────────────────────────┐
│   User Clicks "Change Password" from Profile   │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│     Navigate to ChangePasswordActivity          │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│  User Enters:                                   │
│  - New Password                                 │
│  - Confirm Password                             │
│  (Must match)                                   │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│  Request OTP → Send OTP to User Email           │
│  API: POST /api/Auth/forgot-password            │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│      User Enters OTP in Input Box               │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│  Enable "Change Now" Button                     │
│  (Only if OTP entered & passwords match)        │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│  User Clicks "Change Now"                       │
│  → ChangePasswordViewModel.resetPassword()      │
│  → API: POST /api/Auth/reset-password           │
└──────────────────┬──────────────────────────────┘
                   │
       ┌───────────┴────────────┐
       │                        │
┌──────▼─────────┐     ┌────────▼──────────┐
│ Password       │     │  Change Failed    │
│ Changed        │     │  - Show Error     │
│ - Show Success │     └───────────────────┘
│ - Go Back      │
└────────────────┘
```

**Key Components**:
- [ChangePasswordActivity.java](app/src/main/java/com/example/filmspace_mobile/ui/setting/ChangePasswordActivity.java)

---

### Settings Flow

```
┌─────────────────────────────────────────────────┐
│    User Clicks Settings Option from Profile    │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│         Navigate to SettingActivity             │
│  (RecyclerView with Setting Options)            │
└──────────────────┬──────────────────────────────┘
                   │
       ┌───────────┼───────────────────┐
       │           │                   │
┌──────▼─────┐ ┌──▼────────┐ ┌────────▼─────────┐
│Notifications│ │ Security  │ │  Help & Support │
│Activity     │ │ Activity  │ │  Activity       │
└─────────────┘ └───────────┘ └──────────────────┘
       │           │                   │
┌──────▼─────┐ ┌──▼────────┐ ┌────────▼─────────┐
│Legal &     │ │ Language  │ │   ...            │
│Policies    │ │ Activity  │ │                  │
└────────────┘ └───────────┘ └──────────────────┘
```

**Key Components**:
- [SettingActivity.java](app/src/main/java/com/example/filmspace_mobile/ui/setting/SettingActivity.java)
- [NotificationsActivity.java](app/src/main/java/com/example/filmspace_mobile/ui/setting/NotificationsActivity.java)
- [SecurityActivity.java](app/src/main/java/com/example/filmspace_mobile/ui/setting/SecurityActivity.java)
- [HelpSupportActivity.java](app/src/main/java/com/example/filmspace_mobile/ui/setting/HelpSupportActivity.java)
- [LegalPoliciesActivity.java](app/src/main/java/com/example/filmspace_mobile/ui/setting/LegalPoliciesActivity.java)
- [LanguageActivity.java](app/src/main/java/com/example/filmspace_mobile/ui/setting/LanguageActivity.java)

---

## API Integration

### API Endpoints Overview

```
Authentication APIs:
├── POST /api/Auth/login            → User login
├── POST /api/Auth/register         → User registration
├── POST /api/Auth/verify-otp       → Verify OTP
├── POST /api/Auth/resend-otp       → Resend OTP
├── POST /api/Auth/forgot-password  → Forgot password
├── POST /api/Auth/reset-password   → Reset password
└── POST /api/Auth/logout           → User logout

User APIs:
├── GET  /api/users/{userId}        → Get user by ID
├── PUT  /api/users/{userId}        → Update user (with avatar)
└── PUT  /api/users/{userId}        → Update user (without avatar)

Movie APIs:
├── GET  /api/Movies                → Get all movies
├── GET  /api/Movies/{id}           → Get movie by ID
├── GET  /api/Movies/genre/{id}     → Get movies by genre
└── GET  /api/genres                → Get all genres

Watch History APIs:
├── GET    /api/history/watched           → Get watch history
├── POST   /api/history                   → Add to history
├── DELETE /api/history/{movieId}         → Delete from history
├── DELETE /api/history/clear             → Clear all history
└── GET    /api/history/recommendations   → Get recommended movies

Review APIs:
└── POST /api/Reviews                → Create review
```

### Authentication Mechanism

The app uses **JWT (JSON Web Token)** for authentication:

1. **Login** → Receive JWT token
2. **Save Token** → UserSessionManager (SharedPreferences)
3. **Authenticated Requests** → AuthInterceptor adds token to headers

```java
// AuthInterceptor.java
public class AuthInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        
        String token = sessionManager.getToken();
        if (token != null && !token.isEmpty()) {
            Request request = original.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();
            return chain.proceed(request);
        }
        
        return chain.proceed(original);
    }
}
```

**Key Components**:
- [ApiService.java](app/src/main/java/com/example/filmspace_mobile/data/api/ApiService.java)
- [AuthInterceptor.java](app/src/main/java/com/example/filmspace_mobile/data/api/AuthInterceptor.java)
- [UserSessionManager.java](app/src/main/java/com/example/filmspace_mobile/data/local/UserSessionManager.java)

---

## State Management

### Session Management

User session is managed via **SharedPreferences**:

```java
// Save session after login
public void saveSession(int userId, String username, String email, String token) {
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putInt(KEY_USER_ID, userId);
    editor.putString(KEY_USERNAME, username);
    editor.putString(KEY_EMAIL, email);
    editor.putString(KEY_TOKEN, token);
    editor.putBoolean(KEY_IS_LOGGED_IN, true);
    editor.apply();
}

// Check if user is logged in
public boolean isLoggedIn() {
    return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
}

// Clear session on logout
public void clearSession() {
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.clear();
    editor.apply();
}
```

**Key File**: [UserSessionManager.java](app/src/main/java/com/example/filmspace_mobile/data/local/UserSessionManager.java)

---

### LiveData Observers

All UI components observe **LiveData** from ViewModels:

```java
// In Fragment/Activity
viewModel.getMovies().observe(getViewLifecycleOwner(), movies -> {
    // Update UI when data changes
    movieAdapter.setMovies(movies);
});

viewModel.getError().observe(getViewLifecycleOwner(), error -> {
    // Show error message
    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
});

viewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
    // Show/hide loading indicator
    progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
});
```

---

## Navigation Flow Diagram

```
                    ┌──────────────────┐
                    │  MainActivity    │
                    │  (Bottom Nav)    │
                    └────────┬─────────┘
                             │
         ┌───────────────────┼───────────────────┐
         │                   │                   │
    ┌────▼────┐        ┌─────▼─────┐      ┌─────▼─────┐
    │  Home   │        │  Search   │      │ Favorite  │
    │Fragment │        │ Fragment  │      │ Fragment  │
    └────┬────┘        └─────┬─────┘      └─────┬─────┘
         │                   │                   │
         │                   │                   │
         └───────────────────┼───────────────────┘
                             │
                    ┌────────▼─────────┐
                    │ ProfileFragment  │
                    └────────┬─────────┘
                             │
         ┌───────────────────┼───────────────────┐
         │                   │                   │
    ┌────▼────────┐    ┌─────▼──────┐    ┌──────▼──────┐
    │ AuthActivity│    │SettingAct. │    │EditProfileA.│
    └────┬────────┘    └────────────┘    └─────────────┘
         │
    ┌────▼─────────────────────────────┐
    │ - SignInFragment                 │
    │ - RegisterFragment               │
    │ - ForgotPasswordFragment         │
    │ - OTPVerificationFragment        │
    │ - CreateNewPasswordFragment      │
    └──────────────────────────────────┘

    ┌────────────────────────────────┐
    │  From Any Movie Item Click:    │
    │  → MovieDetailActivity         │
    │    - MovieAboutFragment        │
    │    - MovieEpisodeFragment      │
    │    - MovieReviewFragment       │
    └────────────────────────────────┘

    ┌────────────────────────────────┐
    │  From Genre Click:             │
    │  → MoviesByGenreActivity       │
    └────────────────────────────────┘
```

---

## Key Features Summary

### 1. **Home Screen**
- Auto-scrolling movie carousel (ViewPager2)
- Recommended movies (horizontal scroll)
- Top-rated movies (vertical scroll)
- Genre filtering

### 2. **Search**
- Real-time search filtering
- Grid layout display
- Empty state handling

### 3. **Watch History (Favorite)**
- Personalized watch history
- Requires authentication
- Grid display

### 4. **Profile**
- User information display
- Edit profile with avatar upload
- Change password with OTP
- Settings & preferences
- Login/Register for guests

### 5. **Movie Details**
- Comprehensive movie information
- Cast list
- Episode list
- User reviews
- Play functionality

### 6. **Authentication**
- Register with email verification
- Login with JWT tokens
- Forgot password with OTP
- Session management

---

## Error Handling

The app implements comprehensive error handling:

```java
// Network errors
private String getNetworkErrorMessage(Throwable t) {
    if (t instanceof UnknownHostException || t instanceof IOException) {
        return "No internet connection";
    } else if (t instanceof SocketTimeoutException) {
        return "Request timed out";
    } else {
        return "Something went wrong";
    }
}

// HTTP errors
private String getHttpErrorMessage(int code) {
    switch (code) {
        case 400: return "Invalid request";
        case 401: return "Unauthorized";
        case 403: return "Access forbidden";
        case 404: return "Not found";
        case 500: return "Server error";
        default: return "Unknown error";
    }
}
```

---

## Dependency Injection (Hilt)

The app uses **Hilt** for dependency injection:

```java
// Application class
@HiltAndroidApp
public class FilmSpaceApplication extends Application {}

// Network module
@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {
    @Provides
    @Singleton
    public ApiService provideApiService() {
        // Retrofit setup
    }
}

// ViewModel injection
@HiltViewModel
public class HomeViewModel extends ViewModel {
    @Inject
    public HomeViewModel(MovieRepository movieRepository) {
        // Constructor injection
    }
}

// Activity/Fragment injection
@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {}
```

**Key Files**:
- [FilmSpaceApplication.java](app/src/main/java/com/example/filmspace_mobile/FilmSpaceApplication.java)
- [NetworkModule.java](app/src/main/java/com/example/filmspace_mobile/di/NetworkModule.java)

---

## Conclusion

FilmSpace is a well-architected Android application following modern Android development best practices:

- ✅ **MVVM Architecture** for separation of concerns
- ✅ **Repository Pattern** for data abstraction
- ✅ **Dependency Injection** (Hilt) for modularity
- ✅ **LiveData** for reactive UI updates
- ✅ **JWT Authentication** for security
- ✅ **RESTful API** integration
- ✅ **Material Design** UI components
- ✅ **Comprehensive Error Handling**

The system provides a seamless movie browsing experience with user authentication, personalized recommendations, and detailed movie information.
