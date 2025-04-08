package com.example.slasher;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.navigation.NavigationView;
import com.example.slasher.fragments.*;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        // Menü ikonnal megjelenítés
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_character) {
                selectedFragment = new CharacterFragment();
            } else if (itemId == R.id.navigation_fight) {
                selectedFragment = new FightFragment();
            } else if (itemId == R.id.navigation_map) {
                selectedFragment = new MapFragment();
            } else if (itemId == R.id.navigation_inventory) {
                selectedFragment = new InventoryFragment();
            } else if (itemId == R.id.navigation_achievements) {
                selectedFragment = new AchievementFragment();
            } else if (itemId == R.id.navigation_settings) {
                selectedFragment = new SettingsFragment();
            }

            if (selectedFragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment, selectedFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                drawerLayout.closeDrawer(GravityCompat.START); // Menü bezárása
            }
            return true;
        });

        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.navigation_character);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new CharacterFragment())
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
