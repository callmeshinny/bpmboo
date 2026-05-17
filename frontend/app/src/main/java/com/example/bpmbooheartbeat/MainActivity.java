package com.example.bpmbooheartbeat;

import android.os.Bundle;
import android.view.View;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.bpmbooheartbeat.utils.LocaleHelper;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navView = findViewById(R.id.bottom_navigation);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            setupBottomNavigation();
            setupDestinationListener();
        }
    }

    private void setupBottomNavigation() {
        navView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.measureFragment) {
                navigateToTab(R.id.measureFragment);
                return true;
            }

            if (itemId == R.id.historyFragment) {
                navigateToTab(R.id.historyFragment);
                return true;
            }

            if (itemId == R.id.analyticsFragment) {
                navigateToTab(R.id.analyticsFragment);
                return true;
            }

            if (itemId == R.id.profileFragment) {
                navigateToTab(R.id.profileFragment);
                return true;
            }

            return false;
        });
    }

    private void navigateToTab(int destinationId) {
        if (navController == null) return;

        NavOptions navOptions = new NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setPopUpTo(R.id.measureFragment, false)
                .build();

        navController.navigate(destinationId, null, navOptions);
    }

    private void setupDestinationListener() {
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destinationId = destination.getId();

            if (destinationId == R.id.onboardingFragment) {
                navView.setVisibility(View.GONE);
                return;
            }

            navView.setVisibility(View.VISIBLE);

            if (destinationId == R.id.measureFragment) {
                navView.getMenu().findItem(R.id.measureFragment).setChecked(true);
            } else if (destinationId == R.id.historyFragment) {
                navView.getMenu().findItem(R.id.historyFragment).setChecked(true);
            } else if (destinationId == R.id.analyticsFragment) {
                navView.getMenu().findItem(R.id.analyticsFragment).setChecked(true);
            } else if (destinationId == R.id.profileFragment) {
                navView.getMenu().findItem(R.id.profileFragment).setChecked(true);
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase));
    }
}