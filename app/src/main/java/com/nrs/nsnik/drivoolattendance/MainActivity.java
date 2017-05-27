package com.nrs.nsnik.drivoolattendance;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.nrs.nsnik.drivoolattendance.adapters.PagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.mainToolBar) Toolbar mMainToolbar;
    @BindView(R.id.mainDrawerLayout) DrawerLayout mDrawerLayout;
    @BindView(R.id.mainNavigationView) NavigationView mNavigationView;
    @BindView(R.id.mainViewPager) ViewPager mMainViewPager;
    @BindView(R.id.mainTabLayout) TabLayout mMainTabLayout;
    private PagerAdapter mPagerAdapter;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setTheme(R.style.tranparentStatusBar);
        }
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initialize();
        listener();
        initializeDrawer();
    }

    private void initialize(){
        setSupportActionBar(mMainToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.attendance));
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mMainViewPager.setAdapter(mPagerAdapter);
        mMainTabLayout.setupWithViewPager(mMainViewPager);
    }

    private void initializeDrawer() {
        mNavigationView.getMenu().getItem(0).setChecked(true);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mMainToolbar, R.string.drawerOpen, R.string.drawerClose) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mDrawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.navItemAttendance:
                        Toast.makeText(MainActivity.this,"Attendance",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.navItemOther:
                        startActivity(new Intent(MainActivity.this,HistoryActivity.class));
                        break;
                    case R.id.navItemSettings:
                        Toast.makeText(MainActivity.this,"Settings",Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });
    }

    private void listener(){
    }

}
