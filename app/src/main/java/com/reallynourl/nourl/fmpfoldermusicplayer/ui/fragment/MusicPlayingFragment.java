package com.reallynourl.nourl.fmpfoldermusicplayer.ui.fragment;

import android.animation.Animator;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.reallynourl.nourl.fmpfoldermusicplayer.R;

/**
 * Copyright (C) 2015  Jannes Peters
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
public class MusicPlayingFragment extends Fragment implements IMainContent {
    public static final String NAME = "music_playing";

    private boolean mIsCreated = false;

    public MusicPlayingFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mIsCreated = true;
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        mIsCreated = false;
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate layout for fragment
        return inflater.inflate(R.layout.fragment_music_playback, container, false);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Fragment getFragment() {
        return this;
    }

    @Override
    public boolean isCreated() {
        return mIsCreated;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
