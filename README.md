# Save4Fun Application

## Introduction

Save4Fun is a cutting-edge e-commerce application, crafted using Android Studio, designed to offer a seamless shopping experience. Users can easily sign up or log in through the app, or conveniently via their Google accounts, to explore an array of features. Here’s a glimpse of how it all works:

- **User-Friendly Interface**: Effortless navigation with clickable buttons for creating shopping lists, marking favorite items, checking popular products, and learning more about the company.
- **Profile Editing**: Personal information, such as name, password, date of birth, email, and phone number, can be updated through the intuitive profile section.
- **Product Categories**: Items are sorted into categories like Vegetables, Meat, Snacks, Bread, and Beverages for easy browsing.
- **Product Display**: Products are showcased with images, names, prices, and an "Add to List" option, along with the ability to mark favorites.
  - **Search Feature**: Simply type into the search bar to find desired products quickly.
- **Shopping Lists**: Organize shopping with customized lists, and view them with a pie chart to visualize product categories and distributions.

### Motivation

Save4Fun aims to elevate the online shopping experience by simplifying the process of finding, organizing, and purchasing items, making the entire journey smoother and more enjoyable.

---

## Feature Specifications & Extensions

### 1. **Sign Up and Login**
The login and sign-up functionality offers two seamless methods: create a new account with a username and password or sign in using Google One Tap. With SQLite, user data and activities are stored securely, ensuring an effortless experience whether creating an account for the first time or logging back in.

- **Technologies Used**: `SharedPreferences`, `Bundle`, `Intent`, `Toast`, Google One Tap, SQLite

### 2. **Home Page**
The home page is the hub, with a navigation drawer and bottom navigation bar offering smooth transitions between sections like List, Product, Favorite, and About. Banner sliders highlight hot deals, and the popular product section simplifies shopping.

- **Technologies Used**: `RecyclerView`, `NavigationDrawer`, `BottomNavigationBar`, `FloatingActionButton`, Banner Sliders, Fragment

### 3. **Profile Page**
The profile section, accessible through the navigation drawer, allows instant updates to personal information using `TextWatcher` for real-time changes. The `DatePicker` simplifies selecting birthdates.

- **Technologies Used**: `TextWatcher`, `DatePicker`

### 4. **Product and Favorite Pages**
Products are neatly categorized with visually appealing icons styled using `CircleImageView`. A powerful `SearchView` allows quick product discovery, and items can be added to lists or marked as favorites with a simple tap.

- **Technologies Used**: `CircleImageView`, `SearchView`, `AlertDialog`, `RecyclerView`

### 5. **List Page**
The list page enables users to create and manage shopping lists, enhanced with a `VerticalProgressBar` to guide the process. A pie chart visualization, powered by `MPAndroidChart`, shows product distribution, making list organization simple and intuitive.

- **Technologies Used**: `VerticalProgressBar`, `MPAndroidChart`, `RecyclerView`

### 6. **About Page**
The about section introduces the application with a visually enriched presentation using `WebView`, offering a unique and stylized layout for users to explore the app’s features.

- **Technologies Used**: `WebView`

---

This project strives to deliver a streamlined, enjoyable shopping experience with sleek, modern design and robust functionality. Dive into the world of Save4Fun and make online shopping easier, organized, and more fun!
