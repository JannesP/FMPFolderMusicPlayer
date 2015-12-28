package com.reallynourl.nourl.fmpfoldermusicplayer.ui.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.reallynourl.nourl.fmpfoldermusicplayer.R;
import com.reallynourl.nourl.fmpfoldermusicplayer.ui.fragments.MainContentFragment;
import com.reallynourl.nourl.fmpfoldermusicplayer.ui.fragments.filebrowser.FileBrowserFragment;
import com.reallynourl.nourl.fmpfoldermusicplayer.ui.fragments.music.MusicControlFragment;
import com.reallynourl.nourl.fmpfoldermusicplayer.ui.fragments.music.MusicPlayingFragment;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.MyUncaughtExceptionHandler;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.Util;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.music.MediaManager;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.music.MediaService;

/**
 * Copyright (C) 2015  Jannes Peters
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public final static int REQUEST_PERMISSION_STORAGE = 123;
    public final static String FRAGMENT_EXTRA = "fragment";
    private static MainActivity sInstance;
    private Snackbar mCloseSnackBar = null;
    private MainContentFragment mActiveFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Thread.currentThread().setName("main");
        Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler(Thread.getDefaultUncaughtExceptionHandler()));
        sInstance = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mCloseSnackBar = Snackbar.make(findViewById(android.R.id.content), "Press again to exit ...", Snackbar.LENGTH_SHORT);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Log.d("MainActivity", "onNewIntent called!");
    }

    private void loadFragmentFromBundle(Bundle bundle) {
        String res = "";
        if (bundle != null) {
            res = bundle.getString(FRAGMENT_EXTRA, "none");
        }
        if (res.equals(MusicControlFragment.NAME)) {
            setNavigationItem(R.id.nav_libraries);
        } else {
            if (MediaManager.getInstance().isPlaying()) {
                setNavigationItem(R.id.nav_currently_playing);
            } else {
                setNavigationItem(R.id.nav_file_browser);
            }
        }
    }

    public static void selectFragment(Context context, @NonNull String fragment) {
        if (sInstance == null) {
            Bundle b = new Bundle(1);
            b.putString(MainActivity.FRAGMENT_EXTRA, fragment);
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtras(b);
            context.startActivity(intent);
        } else {
            if (fragment.equals(MusicControlFragment.NAME)) {
                sInstance.setNavigationItem(R.id.nav_currently_playing);
            } else {
                Log.i("MainActivity", "Something tried to load the unknown fragment: " + fragment);
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (mCloseSnackBar.isShown()) {
                finish();
                return;
            } else {
                mCloseSnackBar = Snackbar.make(findViewById(android.R.id.content), "Press again to exit ...", Snackbar.LENGTH_SHORT);
            }
            if (mActiveFragment != null) {
                if (!mActiveFragment.onBackPressed()) {
                    mCloseSnackBar.show();
                }
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_file_browser:
                loadFragmentToContent(new FileBrowserFragment());
                Log.d("Navigation", "Loading file browser fragment!");
                break;
            case R.id.nav_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_currently_playing:
                loadFragmentToContent(new MusicPlayingFragment());
                Log.d("Navigation", "Loading Player fragment");
                break;
            default:
                Log.e("Navigation", "Navigation button has no action, id: " + id + " text: " + item.getTitle());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        if (Util.hasStoragePermission(this)) {
            loadUi();
        } else {
            Util.requestStoragePermission(this, REQUEST_PERMISSION_STORAGE);
        }
        super.onResume();
    }

    private void loadUi() {
        MediaManager mediaManager = MediaManager.getInstance();
        if (mediaManager == null) {
            MediaManager.create(getApplicationContext());
        }
        Bundle bundle = getIntent().getExtras();
        loadFragmentFromBundle(bundle);
    }

    @Override
    protected void onPause() {
        MediaManager mediaManager = MediaManager.getInstance();
        if (mediaManager != null) {
            mediaManager.onMainActivityClosed();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        sInstance = null;
        super.onDestroy();
    }

    private void loadFragmentToContent(MainContentFragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.content_panel, fragment);
        ft.commit();
        mActiveFragment = fragment;
    }

    private void setNavigationItem(@IdRes int id) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(id);
        MenuItem selected = navigationView.getMenu().findItem(id);
        onNavigationItemSelected(selected);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadUi();
                } else {
                    Toast.makeText(this, "Storage permission denied. Can't play music without :(\nYou can still manually enable them in the settings.", Toast.LENGTH_LONG).show();
                    finish();
                }

                break;
        }
    }
}
