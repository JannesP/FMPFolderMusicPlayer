package com.reallynourl.nourl.fmpfoldermusicplayer.utility.music;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.reallynourl.nourl.fmpfoldermusicplayer.ui.notifications.MusicNotification;

import java.io.IOException;

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
public class MediaService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, AudioManager.OnAudioFocusChangeListener, MediaPlayer.OnCompletionListener {
    private final static int MEDIA_ERROR_SYSTEM = -2147483648;

    private static MediaService sInstance = null;
    private MediaPlayer mMediaPlayer;
    private boolean mIsPreparedToPlay;

    private void setupMediaPlayer(Uri file) {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnCompletionListener(this);
        } else {
            if (mMediaPlayer.isPlaying()) mMediaPlayer.stop();
            mMediaPlayer.reset();
        }
        try {
            mMediaPlayer.setDataSource(getApplicationContext(), file);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Failed to load the file.", Toast.LENGTH_LONG).show();
        }
        mMediaPlayer.prepareAsync();
    }

    public void play(Uri file) {
        setupMediaPlayer(file);
    }

    public boolean requestAudioFocus() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Toast.makeText(getApplicationContext(), "Failed to get audio focus. Not starting playback.", Toast.LENGTH_LONG).show();
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mIsPreparedToPlay = false;
            }
            return false;
        }
        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MediaService.sInstance = this;
        mIsPreparedToPlay = false;
        Log.d("Media Service", "Media Service created!");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        mIsPreparedToPlay = false;
        mMediaPlayer.release();
        mMediaPlayer = null;
        sInstance = null;
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(MusicNotification.NOTIFICATION_ID);
        super.onDestroy();
    }

    public static MediaService getInstance() {
        return sInstance;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (requestAudioFocus()) {
            mp.start();
            mIsPreparedToPlay = true;
            Notification notification = MusicNotification.create(getApplicationContext(), "TextName");
            startForeground(MusicNotification.NOTIFICATION_ID, notification);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mIsPreparedToPlay = false;
        stopForeground(false);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mIsPreparedToPlay = false;
        String message = "";
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                message = "The media server died. This is probably not my fault, but playback had to be stopped, feel free to start it again.";
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                switch (extra) {
                    case MediaPlayer.MEDIA_ERROR_IO:
                        message = "There was an error reading the current media file.";
                        break;
                    case MediaPlayer.MEDIA_ERROR_MALFORMED:
                        message = "The file is probably not a valid audio file.";
                        break;
                    case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                        message = "The current file is not supported by your device, I'm really sorry.";
                        break;
                    case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                        message = "The media player timed out.";
                        break;
                    case MediaService.MEDIA_ERROR_SYSTEM:
                        message = "A low level system error occured. This should have never happened, sorry :/";
                        break;
                }
                break;
        }
        releasePlayer();
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                if (mMediaPlayer != null) {
                    mMediaPlayer.start();
                    mMediaPlayer.setVolume(1.0f, 1.0f);
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                if (mMediaPlayer != null) {
                    releasePlayer();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    mMediaPlayer.setVolume(0.1f, 0.1f);
                }
                break;
        }
    }

    private void releasePlayer() {
        mIsPreparedToPlay = false;
        if (mMediaPlayer.isPlaying()) mMediaPlayer.stop();
        mMediaPlayer.release();
        mMediaPlayer = null;
    }

    public void pause() {
        if (mMediaPlayer != null && mIsPreparedToPlay && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    public void play() {
        if (mMediaPlayer != null && mIsPreparedToPlay && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }
    }

    public void seekTo(int msec) {
        if (mMediaPlayer != null && mMediaPlayer.getDuration() > msec) {
            mMediaPlayer.seekTo(msec);
        }
    }

    public int getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    public int getPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    public void stop() {
        if (mMediaPlayer != null && mIsPreparedToPlay) {
            mIsPreparedToPlay = false;
            if (mMediaPlayer.isPlaying()) mMediaPlayer.stop();
        }
    }

    public boolean isPreparedToPlay() {
        return mIsPreparedToPlay;
    }
}
