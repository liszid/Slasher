package com.example.slasher;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.slasher.fragments.AchievementFragment;
import com.example.slasher.fragments.CharacterFragment;
import com.example.slasher.fragments.FightFragment;
import com.example.slasher.fragments.InventoryFragment;
import com.example.slasher.fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        //noinspection deprecation
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_character) {
                selectedFragment = new CharacterFragment();
            } else if (itemId == R.id.navigation_fight) {
                selectedFragment = new FightFragment();
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
            }
            return true;
        });

        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_character);
        }
    }
}
