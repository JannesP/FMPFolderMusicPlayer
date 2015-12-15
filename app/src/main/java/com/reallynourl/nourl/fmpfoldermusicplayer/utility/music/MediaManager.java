package com.reallynourl.nourl.fmpfoldermusicplayer.utility.music;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;
import java.util.List;

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

    private Playlist mPlaylist;

    private Context mContext;
    private MediaManager(Context mContext) {
        this.mContext = mContext;
        this.mPlaylist = new Playlist();
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

    public Playlist getPlaylist() {
        return mPlaylist;
    }

    public void play(File file) {
        mPlaylist.clear();//FIXME: Obvious testing code.
        mPlaylist.appendNext(file);
        file = mPlaylist.selectNext();
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

    public boolean hasNext() {
        return mPlaylist.hasNext();
    }

    public boolean hasPrevious() {
        return mPlaylist.hasPrevious();
    }

    public boolean canPlay() {
        return mPlaylist.getCurrent() != null;
    }

    public boolean isStopped() {
        return !MediaService.getInstance().isPreparedToPlay();
    }

    public void next() {
        Toast.makeText(mContext, "Next not implemented!", Toast.LENGTH_LONG).show();
    }

    public void previous() {
        Toast.makeText(mContext, "Previous not implemented!", Toast.LENGTH_LONG).show();
    }
}
