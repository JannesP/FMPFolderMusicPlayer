package com.reallynourl.nourl.fmpfoldermusicplayer.utility.music;

import android.annotation.TargetApi;
import android.content.Intent;
import android.media.session.MediaSession;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

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
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MediaEventReceiver extends MediaSessionCompat.Callback {
    private static final String TAG = "MediaEventReceiver";

    @Override
    public void onCommand(@NonNull String command, Bundle args, ResultReceiver cb) {
        Log.i(TAG, "onCommand: " + command);
    }

    @Override
    public void onPlay() {
        Log.i(TAG, "onPlay");
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause");
    }

    @Override
    public void onSkipToNext() {
        Log.i(TAG, "onSkipToNext");
    }

    @Override
    public void onSkipToPrevious() {
        Log.i(TAG, "onSkipToPrevious");
    }

    @Override
    public void onFastForward() {
        Log.i(TAG, "onFastForward");
    }

    @Override
    public void onRewind() {
        Log.i(TAG, "onRewind");
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop");
    }

    @Override
    public void onSeekTo(long pos) {
        Log.i(TAG, "onSeekTo");
    }
}
