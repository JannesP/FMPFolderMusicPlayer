package com.reallynourl.nourl.fmpfoldermusicplayer.utility.music;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.File;

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
public class MediaManager {
    private static MediaManager sInstance;

    private Context mContext;
    private MediaManager(Context mContext) {
        this.mContext = mContext;
        mContext.startService(new Intent(mContext, MediaService.class));
    }

    public static void create(Context context) {
        if (sInstance == null) {
            sInstance = new MediaManager(context);
        }
    }

    public static MediaManager getInstance() {
        return sInstance;
    }

    public void play(File file) {
        MediaService.getInstance().play(Uri.fromFile(file));
    }

    public void play() {
        MediaService.getInstance().play();
    }

    public void pause() {
        MediaService.getInstance().pause();
    }

    public void stop() {
        MediaService.getInstance().stop();
    }

    public void seekTo(int msec) {
        MediaService.getInstance().seekTo(msec);
    }

    public int getDuration() {
        return MediaService.getInstance().getDuration();
    }

    public int getPosition() {
        return MediaService.getInstance().getPosition();
    }

    public boolean isPlaying() {
        return MediaService.getInstance().isPlaying();
    }

}
