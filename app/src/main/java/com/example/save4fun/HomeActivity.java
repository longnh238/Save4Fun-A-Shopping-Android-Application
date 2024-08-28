package com.example.save4fun;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.save4fun.db.DBUsersHelper;
import com.example.save4fun.fragment.AboutFragment;
import com.example.save4fun.fragment.FavouriteFragment;
import com.example.save4fun.fragment.HomeFragment;
import com.example.save4fun.fragment.ListFragment;
import com.example.save4fun.fragment.ProductFragment;
import com.example.save4fun.fragment.ProfileFragment;
import com.example.save4fun.model.User;
import com.example.save4fun.util.Constant;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    TextView textViewNavigationHeaderUsername, textViewNavigationHeaderEmail;
    BottomNavigationView bottomNavigation;
    Toolbar toolbar;
    FloatingActionButton fabHome;

    private static final int HOME_FRAGMENT = 0;
    private static final int PROFILE_FRAGMENT = 1;
    private static final int NO_FRAGMENT = -1;
    private int currentFragment = HOME_FRAGMENT;

    private DBUsersHelper dbUsersHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Handling toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Handling navigation drawer
        drawerLayout = findViewById(R.id.drawerLayout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        NavigationView navigationView = findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);
        textViewNavigationHeaderUsername = headerView.findViewById(R.id.textViewNavigationHeaderUsername);
        textViewNavigationHeaderEmail = headerView.findViewById(R.id.textViewNavigationHeaderEmail);

        SharedPreferences sharedPreferences = getSharedPreferences(Constant.PREFERENCES_NAME, 0);
        boolean hasLoggedIn = sharedPreferences.getBoolean(Constant.HAS_LOGGED_IN, false);
        String username = sharedPreferences.getString(Constant.USERNAME, "");

        dbUsersHelper = new DBUsersHelper(HomeActivity.this);
        if (hasLoggedIn && !username.isEmpty()) {
            User user = dbUsersHelper.getUserByUsername(username);
            if(user != null) {
                String email = user.getEmail() != null ? user.getEmail() : "";

                textViewNavigationHeaderUsername.setText(username);
                textViewNavigationHeaderEmail.setText(email);
            }
        }

        // Handling bottom navigation
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setBackground(null);
        deselectBottomNavigation();

        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navList) {
                    replaceFragment(new ListFragment());
                } else if (id == R.id.navProduct) {
                    replaceFragment(new ProductFragment());
                } else if (id == R.id.navFavourite) {
                    replaceFragment(new FavouriteFragment());
                } else if (id == R.id.navAbout) {
                    replaceFragment(new AboutFragment());
                } else {
                    return false;
                }

                deselectNavigationDrawer();
                currentFragment = NO_FRAGMENT;

                setColorFabHome(false);
                return true;
            }
        });

        // Handling fab button
        fabHome = findViewById(R.id.fabHome);
        fabHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new HomeFragment());

                MenuItem menuItemHome = navigationView.getMenu().findItem(R.id.navHome);
                menuItemHome.setChecked(true);
                currentFragment = HOME_FRAGMENT;

                deselectBottomNavigation();
                setColorFabHome(true);
            }
        });

        // Handling back pressed
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    finishAffinity();
                    finish();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);

        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment());
            navigationView.setCheckedItem(R.id.navHome);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        boolean isHome = false;
        if (id == R.id.navHome) {
            if (currentFragment != HOME_FRAGMENT) {
                replaceFragment(new HomeFragment());
                currentFragment = HOME_FRAGMENT;
            }
            isHome = true;
        } else if (id == R.id.navProfile) {
            if (currentFragment != PROFILE_FRAGMENT) {
                replaceFragment(new ProfileFragment());
                currentFragment = PROFILE_FRAGMENT;
            }
        } else if (id == R.id.navLogout) {
            SharedPreferences sharedPreferences = getSharedPreferences(Constant.PREFERENCES_NAME, 0);
            sharedPreferences.edit().remove(Constant.HAS_LOGGED_IN).apply();
            sharedPreferences.edit().remove(Constant.USERNAME).apply();

            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        item.setChecked(true);
        drawerLayout.closeDrawer(GravityCompat.START);
        deselectBottomNavigation();
        setColorFabHome(isHome);
        return true;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();
    }

    private void deselectBottomNavigation() {
        bottomNavigation.getMenu().setGroupCheckable(0, true, false);
        Menu menu = bottomNavigation.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            menuItem.setChecked(false);
        }
        bottomNavigation.getMenu().setGroupCheckable(0, true, true);
    }

    private void deselectNavigationDrawer() {
        Menu menu = navigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem groupItem = menu.getItem(i);
            SubMenu subMenu = groupItem.getSubMenu();
            if (subMenu != null) {
                // Iterate through each item in the group
                int itemCount = subMenu.size();
                for (int j = 0; j < itemCount; j++) {
                    MenuItem item = subMenu.getItem(j);
                    item.setChecked(false);
                }
            } else {
                groupItem.setChecked(false);
            }
        }
    }

    private void setColorFabHome(boolean isSelected) {
        Drawable drawable = fabHome.getDrawable();
        if (isSelected) {
            drawable.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.SRC_ATOP);
        } else {
            drawable.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
        }
    }
}