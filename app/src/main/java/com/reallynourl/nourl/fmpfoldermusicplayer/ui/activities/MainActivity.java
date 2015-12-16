package com.reallynourl.nourl.fmpfoldermusicplayer.ui.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
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

import com.reallynourl.nourl.fmpfoldermusicplayer.R;
import com.reallynourl.nourl.fmpfoldermusicplayer.ui.fragments.filebrowser.FileBrowserFragment;
import com.reallynourl.nourl.fmpfoldermusicplayer.ui.fragments.music.MusicControlFragment;
import com.reallynourl.nourl.fmpfoldermusicplayer.ui.fragments.music.MusicPlayingFragment;
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
    public final static String FRAGMENT_EXTRA = "fragment";
    private Snackbar mCloseSnackBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        //set default item to file browser
        //TODO remember last fragment
        Bundle bundle = getIntent().getExtras();
        String res = "";
        if (bundle != null) {
            res = bundle.getString(FRAGMENT_EXTRA, "none");
        }
        if (res.equals("CONTROLS")) {
            setNavigationItem(R.id.nav_libraries);
        } else {
            setNavigationItem(R.id.nav_file_browser);
        }

        mCloseSnackBar = Snackbar.make(findViewById(android.R.id.content), "Press again to exit ...", Snackbar.LENGTH_SHORT);
        MediaManager.create(getApplicationContext());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (mCloseSnackBar.isShown()) {
                finish();
            } else {
                mCloseSnackBar = Snackbar.make(findViewById(android.R.id.content), "Press again to exit ...", Snackbar.LENGTH_SHORT);
            }
            getFragmentManager().executePendingTransactions();
            if (getFragmentManager().findFragmentById(R.id.content_panel).isVisible()) {
                if (!((FileBrowserFragment)getFragmentManager().findFragmentById(R.id.content_panel)).onBackPressed()) {
                    mCloseSnackBar.show();
                }
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
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
            case R.id.nav_libraries:
                loadFragmentToContent(new MusicPlayingFragment());
                Log.d("Navigation", "Loading Player fragemnt");
                break;
            default:
                Log.e("Navigation", "Navigation button has no action, id: " + id + " text: " + item.getTitle());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        if (!MediaManager.getInstance().isPlaying()) {
            getApplicationContext().stopService(new Intent(getApplicationContext(), MediaService.class));
        }
        super.onDestroy();
    }

    private void loadFragmentToContent(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.content_panel, fragment);
        ft.commit();
    }

    private void setNavigationItem(@IdRes int id) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(id);
        MenuItem selected = navigationView.getMenu().findItem(id);
        onNavigationItemSelected(selected);
    }
}
