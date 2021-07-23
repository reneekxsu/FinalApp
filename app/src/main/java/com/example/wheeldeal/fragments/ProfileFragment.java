package com.example.wheeldeal.fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.wheeldeal.R;
import com.example.wheeldeal.activities.LoginActivity;
import com.google.android.material.navigation.NavigationView;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment2";
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    View rootview;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_profile, container, false);
        return rootview;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set a Toolbar to replace the ActionBar.
        toolbar = rootview.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        // This will display an Up icon (<-), we will replace it with hamburger later
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Find our drawer view
        mDrawer = rootview.findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();

        // Setup toggle to display hamburger icon with nice animation
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);
        // Find our drawer view
        nvDrawer = rootview.findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);
        nvDrawer.post(new Runnable() {
            @Override
            public void run() {
                nvDrawer.setCheckedItem(R.id.nav_first_fragment);
                ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(nvDrawer.getMenu().getItem(0).getTitle());
                getChildFragmentManager().beginTransaction().replace(R.id.flContent, new AccountDetailsFragment()).commit();
                selectDrawerItem(nvDrawer.getMenu().getItem(0));
                nvDrawer.getMenu().getItem(0).setTitle(nvDrawer.getMenu().getItem(0).getTitle());
            }
        });
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(getActivity(), mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.nav_first_fragment:
                fragmentClass = AccountDetailsFragment.class;
                break;
            case R.id.nav_second_fragment:
                fragmentClass = ViewMyCarsFragment.class;
                break;
            case R.id.nav_third_fragment:
                fragmentClass = LogoutDummyFragment.class;
                Log.i(TAG, "clicked logout button");
                ParseUser.logOut();
                Intent i = new Intent(getView().getContext(), LoginActivity.class);
                startActivity(i);
                getActivity().finish();
                break;
            default:
                fragmentClass = AccountDetailsFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        Log.i(TAG, "menu item set checked index: " + menuItem.getItemId());
        menuItem.setChecked(true);
//        nvDrawer.setCheckedItem(menuItem.getItemId());
        // Set action bar title
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

}
