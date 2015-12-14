package com.reallynourl.nourl.fmpfoldermusicplayer.ui.fragments.music;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.reallynourl.nourl.fmpfoldermusicplayer.R;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.Util;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.music.MediaManager;

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
public class MusicControlFragment extends Fragment implements View.OnClickListener, Runnable {
    private boolean mIsCreated = false;
    private View mRootView = null;

    public MusicControlFragment() {}

    private Thread mRefreshThread;

    private void startRefresh() {
        mRefreshThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    getActivity().runOnUiThread(MusicControlFragment.this);
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        });
        mRefreshThread.start();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate layout for fragment
        if (!mIsCreated) {
            mIsCreated = true;
            mRootView = inflater.inflate(R.layout.fragment_music_controls, container, false);
        }
        return mRootView;
    }

    @Override
    public void onResume() {
        addListeners();
        startRefresh();
        super.onResume();
    }

    @Override
    public void onPause() {
        removeListeners();
        mRefreshThread.interrupt();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mIsCreated = false;
        mRootView = null;
        super.onDestroy();
    }

    private void addListeners() {
        ImageButton lv = (ImageButton) mRootView.findViewById(R.id.imageButtonPlayPause);
        lv.setOnClickListener(this);
        lv = (ImageButton) mRootView.findViewById(R.id.imageButtonStop);
        lv.setOnClickListener(this);
    }

    private void removeListeners() {
        ImageButton lv = (ImageButton) mRootView.findViewById(R.id.imageButtonPlayPause);
        lv.setOnClickListener(null);
        lv = (ImageButton) mRootView.findViewById(R.id.imageButtonStop);
        lv.setOnClickListener(null);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.imageButtonPlayPause:
                if (MediaManager.getInstance().isPlaying()) {
                    MediaManager.getInstance().pause();
                } else {
                    MediaManager.getInstance().play();
                }
                break;
            case R.id.imageButtonStop:
                MediaManager.getInstance().stop();
                break;
        }
    }

    @Override
    public void run() {
        ImageButton lv = (ImageButton) mRootView.findViewById(R.id.imageButtonPlayPause);
        if (MediaManager.getInstance().isPlaying()) {
            lv.setImageResource(R.drawable.ic_pause);
        } else {
            lv.setImageResource(R.drawable.ic_play_arrow);
        }
        SeekBar sb = (SeekBar) mRootView.findViewById(R.id.seekBar);
        int duration = MediaManager.getInstance().getDuration();
        int position = MediaManager.getInstance().getPosition();
        sb.setMax(duration);
        sb.setProgress(position);
        TextView tv = (TextView) mRootView.findViewById(R.id.textViewTimeCurrent);
        tv.setText(Util.getDurationString(position));
        tv = (TextView) mRootView.findViewById(R.id.textViewDuration);
        tv.setText(Util.getDurationString(duration));
    }
}
