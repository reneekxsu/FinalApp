package com.example.wheeldeal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.wheeldeal.fragments.HomeFragment;
import com.example.wheeldeal.fragments.LoadingFragment;
import com.example.wheeldeal.fragments.ProfileFragment;
import com.example.wheeldeal.fragments.ScheduleFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        boolean fromLogin = intent.getExtras().getBoolean("flag");

        final FragmentManager fragmentManager = getSupportFragmentManager();

        // define your fragments here
        final Fragment fragment1 = new HomeFragment();
        final Fragment fragment2 = new ScheduleFragment();
        final Fragment fragment3 = new ProfileFragment();
        final Fragment fragment4 = new LoadingFragment();

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // handle navigation selection
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment fragment;
                        switch (item.getItemId()) {
                            case R.id.action_home:
                                item.setCheckable(true);
                                fragment = fragment1;
                                break;
                            case R.id.action_schedule:
                                fragment = fragment2;
                                break;
                            case R.id.action_profile:
                                fragment = fragment3;
                                break;
                            default:
                                fragment = fragment1;
                                break;
                        }
                        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
                        return true;
                    }
                });
        // Set default selection
        if (fromLogin){
            bottomNavigationView.getMenu().getItem(0).setCheckable(false);
            fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment4).commit();
        } else {
            bottomNavigationView.setSelectedItemId(R.id.action_home);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        Log.i(TAG, "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111){
            bottomNavigationView.setSelectedItemId(R.id.action_profile);
        }
    }

    public void setNavigationVisibility(boolean visible) {
        if (bottomNavigationView.isShown() && !visible) {
            bottomNavigationView.setVisibility(View.GONE);
        }
        else if (!bottomNavigationView.isShown() && visible){
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }
}