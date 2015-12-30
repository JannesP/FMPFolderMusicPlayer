package com.reallynourl.nourl.fmpfoldermusicplayer.ui.fragments.music;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.reallynourl.nourl.fmpfoldermusicplayer.R;
import com.reallynourl.nourl.fmpfoldermusicplayer.ui.controls.OptionView;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.Util;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.music.MediaManager;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.music.RepeatMode;

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
public class MusicControlFragment extends Fragment implements View.OnClickListener, Runnable, SeekBar.OnSeekBarChangeListener{
    public final static String NAME = "controls";

    private boolean mIsCreated = false;
    private View mRootView = null;

    //control references
    private boolean mReferencesValid = false;

    private ImageButton mButtonPlay;
    private ImageButton mButtonStop;
    private ImageButton mButtonNext;
    private ImageButton mButtonPrevious;
    private ImageButton mButtonShuffle;
    private ImageButton mButtonRepeat;

    private TextView mTvPosition;
    private TextView mTvDuration;

    private SeekBar mSeekBar;

    private int mAccentColor;

    public MusicControlFragment() {}

    private Thread mRefreshThread;

    private void startRefresh() {
        mRefreshThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    getActivity().runOnUiThread(MusicControlFragment.this);
                    try {
                        Thread.sleep(350);
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
            mAccentColor = Util.getAccentColor(getActivity());
        }
        return mRootView;
    }

    @Override
    public void onResume() {
        setupReferences();
        addListeners();
        startRefresh();
        super.onResume();
    }

    @Override
    public void onPause() {
        removeListeners();
        mRefreshThread.interrupt();
        destroyReferences();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mIsCreated = false;
        mRootView = null;
        super.onDestroy();
    }

    private void setupReferences() {
        mButtonPlay = (ImageButton) mRootView.findViewById(R.id.imageButtonPlayPause);
        mButtonStop = (ImageButton) mRootView.findViewById(R.id.imageButtonStop);
        mButtonNext = (ImageButton) mRootView.findViewById(R.id.imageButtonSkipNext);
        mButtonPrevious = (ImageButton) mRootView.findViewById(R.id.imageButtonSkipPrevious);
        mButtonShuffle = (ImageButton) mRootView.findViewById(R.id.imageButtonShuffle);
        mButtonRepeat = (ImageButton) mRootView.findViewById(R.id.imageButtonRepeat);

        mTvPosition = (TextView) mRootView.findViewById(R.id.textViewTimeCurrent);
        mTvDuration = (TextView) mRootView.findViewById(R.id.textViewDuration);

        mSeekBar = (SeekBar) mRootView.findViewById(R.id.seekBar);

        mReferencesValid = true;
    }

    private void destroyReferences() {
        mReferencesValid = false;

        mButtonPlay = null;
        mButtonStop = null;
        mButtonNext = null;
        mButtonPrevious = null;
        mButtonShuffle = null;
        mButtonRepeat = null;

        mTvPosition = null;
        mTvDuration = null;

        mSeekBar = null;
    }

    private void addListeners() {
        mButtonPlay.setOnClickListener(this);
        mButtonStop.setOnClickListener(this);
        mButtonNext.setOnClickListener(this);
        mButtonPrevious.setOnClickListener(this);
        mButtonShuffle.setOnClickListener(this);
        mButtonRepeat.setOnClickListener(this);

        mSeekBar.setOnSeekBarChangeListener(this);
    }

    private void removeListeners() {
        mButtonPlay.setOnClickListener(null);
        mButtonStop.setOnClickListener(null);
        mButtonNext.setOnClickListener(null);
        mButtonPrevious.setOnClickListener(null);
        mButtonShuffle.setOnClickListener(null);
        mButtonRepeat.setOnClickListener(null);

        mSeekBar.setOnSeekBarChangeListener(null);
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
            case R.id.imageButtonSkipNext:
                MediaManager.getInstance().next();
                break;
            case R.id.imageButtonSkipPrevious:
                MediaManager.getInstance().previous();
                break;
            case R.id.imageButtonShuffle:
                MediaManager.getInstance().getPlaylist().setShuffle(
                       !MediaManager.getInstance().getPlaylist().isShuffle());
                break;
            case R.id.imageButtonRepeat:
                RepeatMode currentMode = MediaManager.getInstance().getPlaylist().getRepeatMode();
                RepeatMode newMode = RepeatMode.OFF;
                switch (currentMode) {
                    case OFF:
                        newMode = RepeatMode.ALL;
                        break;
                    case ALL:
                        newMode = RepeatMode.SINGLE;
                        break;
                    case SINGLE:
                        newMode = RepeatMode.OFF;
                        break;
                }
                MediaManager.getInstance().getPlaylist().setRepeatMode(newMode);
                break;
            default:
                Toast.makeText(getActivity(), "Click handler missing!", Toast.LENGTH_SHORT).show();
        }
        run();
    }

    @Override
    public void run() {
        if (mReferencesValid) {
            //Play button
            int resource;
            if (MediaManager.getInstance().isPlaying()) {
                resource = R.drawable.ic_pause;
            } else {
                resource = R.drawable.ic_play_arrow;
            }

            mButtonPlay.setImageResource(resource);
            mButtonPlay.setEnabled(MediaManager.getInstance().canPlay());

            mButtonStop.setEnabled(!MediaManager.getInstance().isStopped());
            mButtonNext.setEnabled(MediaManager.getInstance().hasNext());
            mButtonPrevious.setEnabled(MediaManager.getInstance().hasPrevious());

            if (MediaManager.getInstance().getPlaylist().isShuffle()) {
                mButtonShuffle.setColorFilter(mAccentColor);
            } else {
                mButtonShuffle.setColorFilter(Color.WHITE);
            }

            RepeatMode repeatMode = MediaManager.getInstance().getPlaylist().getRepeatMode();
            int color = Color.WHITE;
            switch (repeatMode) {
                case OFF:
                    resource = R.drawable.ic_repeat_white;
                    break;
                case ALL:
                    color = mAccentColor;
                    resource = R.drawable.ic_repeat_white;
                    break;
                case SINGLE:
                    color = mAccentColor;
                    resource = R.drawable.ic_repeat_one_white;
                    break;
            }
            mButtonRepeat.setImageResource(resource);
            mButtonRepeat.setColorFilter(color);

            //SeekBar
            int duration = MediaManager.getInstance().getDuration();
            int position = MediaManager.getInstance().getPosition();
            mSeekBar.setMax(duration);
            mSeekBar.setProgress(position);

            //SeekBar labels
            mTvDuration.setText(Util.getDurationString(duration));
            mTvPosition.setText(Util.getDurationString(position));
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            MediaManager.getInstance().seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
