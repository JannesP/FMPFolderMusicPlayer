package com.reallynourl.nourl.fmpfoldermusicplayer.backend;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

import com.reallynourl.nourl.fmpfoldermusicplayer.backend.playlist.Playlist;
import com.reallynourl.nourl.fmpfoldermusicplayer.ui.notifications.MediaNotification;

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
public class MediaManager implements MediaPlayer.OnCompletionListener, Playlist.OnItemsChangedListener, Playlist.OnModeChangedListener {
    private static MediaManager sInstance;

    private Playlist mPlaylist;
    private Context mContext;

    private MediaManager(Context mContext) {
        this.mContext = mContext;
        this.mPlaylist = new Playlist(mContext);
        this.mPlaylist.addOnItemsChangedListener(this);
        this.mPlaylist.addOnModeChangedListener(this);
        mContext.startService(new Intent(mContext, MediaService.class));
    }

    public static void create(Context context) {
        sInstance = new MediaManager(context);
    }

    public static MediaManager getInstance() {
        return sInstance;
    }

    public Playlist getPlaylist() {
        if (mPlaylist == null) {
            mPlaylist = new Playlist(mContext);
        }
        return mPlaylist;
    }

    public void addPlaylistNextAndPlay(File file) {
        mPlaylist.appendNext(file);
        file = mPlaylist.selectNext();
        MediaService mediaService = MediaService.getInstance();
        if (mediaService != null) {
            mediaService.play(file);
        }
    }

    public void playPlaylistItem(int index) {
        File file = mPlaylist.getList().get(index);
        if (file != null) {
            mPlaylist.setCurrent(index);
            MediaService mediaService = MediaService.getInstance();
            if (mediaService != null) {
                mediaService.play(file);
            }
        }
    }

    public void play() {
        MediaService mediaService = MediaService.getInstance();
        if (mediaService != null) {
            mediaService.play();
        }
    }

    public void pause() {
        MediaService mediaService = MediaService.getInstance();
        if (mediaService != null) {
            mediaService.pause();
        }
    }

    public void stop() {
        MediaService mediaService = MediaService.getInstance();
        if (mediaService != null) {
            mediaService.stop();
        }
        mPlaylist.clearCurrent();
    }

    public void seekTo(int msec) {
        MediaService mediaService = MediaService.getInstance();
        if (mediaService != null) {
            mediaService.seekTo(msec);
        }
    }

    public int getDuration() {
        int result = 0;
        MediaService mediaService = MediaService.getInstance();
        if (mediaService != null) {
            result = mediaService.getDuration();
        }
        return result;
    }

    public int getPosition() {
        int result = 0;
        MediaService mediaService = MediaService.getInstance();
        if (mediaService != null) {
            result = mediaService.getPosition();
        }
        return result;
    }

    public boolean isPlaying() {
        MediaService mediaService = MediaService.getInstance();
        return mediaService != null && mediaService.isPlaying();
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
        boolean result = true;
        MediaService mediaService = MediaService.getInstance();
        if (mediaService != null) {
            result = !mediaService.isInitialized();
        }
        return result;
    }

    public void next() {
        File file = mPlaylist.selectNext();
        if (file != null) {
            MediaService mediaService = MediaService.getInstance();
            if (mediaService != null) {
                mediaService.play(file);
            }
        }
    }

    public void previous() {
        File file = mPlaylist.selectPrevious();
        if (file != null) {
            MediaService mediaService = MediaService.getInstance();
            if (mediaService != null) {
                mediaService.play(file);
            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        next();
    }

    public void release() {
        mContext.stopService(new Intent(mContext, MediaService.class));
        sInstance = null;
        mPlaylist = null;
        mContext = null;
        Log.v("MediaManager", "MediaManager released!");
    }

    public File getCurrentFile() {
        File result = null;
        MediaService mediaService = MediaService.getInstance();
        if (mediaService != null) {
            result = mediaService.getCurrentFile();
        }
        return result;
    }

    public void onMainActivityClosed() {
        MediaService mediaService = MediaService.getInstance();
        if (mediaService != null && !isPlaying()) {
            release();
            mediaService.stopSelf();
        }
    }

    @Override
    public void onPlaylistItemsChanged(Playlist playlist) {
        MediaService service = MediaService.getInstance();
        if (service != null) {
            File file = getCurrentFile();
            if (file != null) {
                MediaNotification.showUpdate(service, getCurrentFile(), service.getMediaSession());
            }
        }
    }

    @Override
    public void onPlaylistModeChanged(Playlist playlist) {
        MediaService service = MediaService.getInstance();
        if (service != null) {
            File file = getCurrentFile();
            if (file != null) {
                MediaNotification.showUpdate(service, getCurrentFile(), service.getMediaSession());
            }
        }
    }
}
