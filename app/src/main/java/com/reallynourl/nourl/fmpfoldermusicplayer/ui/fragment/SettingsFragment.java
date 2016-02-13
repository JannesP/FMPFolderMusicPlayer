package com.reallynourl.nourl.fmpfoldermusicplayer.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.reallynourl.nourl.fmpfoldermusicplayer.R;

/**
 * Copyright (C) 2016  Jannes Peters
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class SettingsFragment extends PreferenceFragment implements IMainContent {
    public static final String NAME = "settings";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_general);
        addPreferencesFromResource(R.xml.pref_file_browser);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public Fragment getFragment() {
        return this;
    }
}
