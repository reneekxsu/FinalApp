package com.example.wheeldeal;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.wheeldeal.fragments.HomeFragment;
import com.example.wheeldeal.fragments.LoadingFragment;
import com.example.wheeldeal.fragments.ProfileFragment;
import com.example.wheeldeal.fragments.ScheduleViewpagerFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * @brief This activity contains three fragments, accessed via the bottom navigation tab. Each
 * fragment is represented by one of the icons on the bottom navigation tab. By default, when
 * starting this activity, the home feed fragment will be what the user sees first. However, in the
 * case where they have just logged in, the user will see a welcome loading screen fragment.
 */
public class MainActivity extends AppCompatActivity {

    // Global variable declarations
    public static final String TAG = "MainActivity";
    public BottomNavigationView bottomNavigationView;
    final FragmentManager fragmentManager = getSupportFragmentManager();
    boolean fromLogin;

    /**
     * @brief We create and inflate the bottom navigation view. There are four fragments here—three
     * are for the tabs, and one is for the initial loading display (where all bottom navigation
     * tabs are shown as unchecked).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get a boolean flag for determining whether to display the loading fragment
        Intent intent = getIntent();
        fromLogin = false;
        if (intent.getExtras() != null){
            fromLogin = intent.getExtras().getBoolean("flag");
        }

        // Define fragments
        final Fragment homeFragment = new HomeFragment();
//        final Fragment scheduleFragment = new ScheduleFragment();
        final Fragment scheduleFragment = new ScheduleViewpagerFragment();
        final Fragment profileFragment = new ProfileFragment();
        final Fragment loadingFragment = new LoadingFragment();

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Define each menu tab
        Menu menu = bottomNavigationView.getMenu();
        MenuItem homeTab = menu.findItem(R.id.action_home);
        MenuItem scheduleTab = menu.findItem(R.id.action_schedule);
        MenuItem profTab = menu.findItem(R.id.action_profile);

        // Set the different home tab icons (selected and deselected)
        Drawable homeOutline = getDrawable(R.drawable.ic_outline_home_24);
        Drawable homeFilled = getDrawable(R.drawable.ic_baseline_home_24);
        homeTab.setIcon(homeOutline);

        // Set the different schedule tab icons (selected and deselected)
        Drawable scheduleOutline = getDrawable(R.drawable.ic_outline_calendar_today_24);
        Drawable scheduleFilled = getDrawable(R.drawable.ic_baseline_calendar_today_24);
        scheduleTab.setIcon(scheduleOutline);

        // Set the different profile tab icons (selected and deselected)
        Drawable profOutline = getDrawable(R.drawable.ic_outline_person_outline_24);
        Drawable profFilled = getDrawable(R.drawable.ic_baseline_person_24);
        profTab.setIcon(profOutline);

        // Handle navigation selection
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment fragment;
                        switch (item.getItemId()) {
                            case R.id.action_home:
                                // Set the home tab is checkable in case loading fragment was used
                                // previously
                                item.setCheckable(true);
                                fragment = homeFragment;
                                // Change menu icons appearances
                                item.setIcon(homeFilled);
                                scheduleTab.setIcon(scheduleOutline);
                                profTab.setIcon(profOutline);
                                break;
                            case R.id.action_schedule:
                                fragment = scheduleFragment;
                                // Change menu icons appearances
                                item.setIcon(scheduleFilled);
                                homeTab.setIcon(homeOutline);
                                profTab.setIcon(profOutline);
                                break;
                            case R.id.action_profile:
                                fragment = profileFragment;
                                item.setIcon(profFilled);
                                // Change menu icons appearances
                                homeTab.setIcon(homeOutline);
                                scheduleTab.setIcon(scheduleOutline);
                                break;
                            default:
                                // Dummy case, since all cases are covered above. Should not reach
                                // this case
                                fragment = homeFragment;
                                break;
                        }
                        // Show the selected fragment
                        fragmentManager.beginTransaction().replace(R.id.fragmentContainer,
                                fragment).commit();
                        return true;
                    }
                });

        // Start with a default fragment selected
        setDefaultSelection(loadingFragment);
    }

    /**
     * @brief Sets the default selection/fragment upon launching MainActivity. We either show the
     * loading fragment or the home fragment.
     * @param fragment The fragment we will be showing if fromLogin is true (we expect this to be
     *                 the loading fragment)
     */
    private void setDefaultSelection(Fragment fragment){
        if (fromLogin){
            // Deselect the default fragment tab (home fragment)
            bottomNavigationView.getMenu().findItem(R.id.action_home).setCheckable(false);
            // Show the loading fragment
            fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
        } else {
            // Select the home fragment tab
            bottomNavigationView.setSelectedItemId(R.id.action_home);
        }
    }
}