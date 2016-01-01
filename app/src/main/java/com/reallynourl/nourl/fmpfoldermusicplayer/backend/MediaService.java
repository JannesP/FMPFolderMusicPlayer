package com.reallynourl.nourl.fmpfoldermusicplayer.backend;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.Toast;

import com.reallynourl.nourl.fmpfoldermusicplayer.ui.activity.MainActivity;
import com.reallynourl.nourl.fmpfoldermusicplayer.ui.notifications.MediaNotification;

import java.io.File;
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
public class MediaService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, AudioManager.OnAudioFocusChangeListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnInfoListener {
    private final static int MEDIA_ERROR_SYSTEM = -2147483648;
    private final static String MEDIA_SESSION_NAME = "fmp_folder_music_player_playback";
    private static final int INTENT_MEDIA_KEYS = 1000;
    private static final int INTENT_SESSION_ACTIVITY = 1001;

    private static MediaService sInstance = null;
    private MediaPlayer mMediaPlayer;
    private MediaSessionCompat mMediaSession;
    private MediaEventReceiver mMediaEventReceiver;
    private boolean mIsPreparedToPlay = false;
    private File mCurrentFile;

    private void setupMediaPlayer(File file) {
        mIsPreparedToPlay = false;
        mCurrentFile = file;
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnInfoListener(this);
        } else {
            if (mMediaPlayer.isPlaying()) mMediaPlayer.stop();
            mMediaPlayer.reset();
        }
        try {
            mMediaPlayer.setDataSource(getApplicationContext(), Uri.fromFile(file));
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),
                    "Failed to load the file.", Toast.LENGTH_LONG).show();
            return;
        }
        mMediaPlayer.prepareAsync();
    }

    public void play(File file) {
        setupMediaPlayer(file);
    }

    public boolean requestAudioFocus() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(
                this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mIsPreparedToPlay = false;
            Toast.makeText(getApplicationContext(),
                    "Failed to get audio focus. Not starting playback.", Toast.LENGTH_LONG).show();
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mIsPreparedToPlay = false;
        if (sInstance != null) {
            sInstance = null;
        }
        mMediaEventReceiver = new MediaEventReceiver();
        setupMediaSession();
        sInstance = this;
        Log.d("Media Service", "Media Service created!");
    }

    private void setupMediaSession() {
        mMediaSession = new MediaSessionCompat(getApplicationContext(), MEDIA_SESSION_NAME);
        mMediaSession.setCallback(mMediaEventReceiver);
        mMediaSession.setMediaButtonReceiver(
                PendingIntent.getBroadcast(
                        getApplicationContext(),
                        INTENT_MEDIA_KEYS,
                        new Intent(getApplicationContext(), MusicIntentReceiver.class),
                        PendingIntent.FLAG_UPDATE_CURRENT
                )
        );
        mMediaSession.setQueueTitle("Now Playing");
        mMediaSession.setSessionActivity(
                PendingIntent.getActivity(
                        getApplicationContext(),
                        INTENT_SESSION_ACTIVITY,
                        new Intent(getApplicationContext(), MainActivity.class),
                        PendingIntent.FLAG_UPDATE_CURRENT
                )
        );
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        releasePlayer();
        sInstance = null;
        mMediaSession.release();
        mMediaSession = null;
        MediaManager mediaManager = MediaManager.getInstance();
        if (mediaManager != null) mediaManager.release();
        stopForeground(true);
        mMediaEventReceiver = null;
        Log.d("Media Service", "Media Service stopped!");
        super.onDestroy();
    }

    @Nullable
    public static MediaService getInstance() {
        return sInstance;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (requestAudioFocus()) {
            mp.start();
            mp.setOnCompletionListener(this);
            mMediaSession.setActive(true);
            mIsPreparedToPlay = true;
            MediaNotification.showUpdate(this, mCurrentFile, mMediaSession);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mIsPreparedToPlay = false;
        Log.d("MediaService", "onCompletion got called!");
        MediaManager mediaManager = MediaManager.getInstance();
        if (mediaManager == null) {
            stopSelf();
        } else {
            MediaManager.getInstance().onCompletion(mp);
            MediaNotification.showUpdate(this, mCurrentFile, mMediaSession);
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mIsPreparedToPlay = false;
        String message = "undefined media player error";
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
                    play();
                    mMediaPlayer.setVolume(1.0f, 1.0f);
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                if (mMediaPlayer != null) {
                    releasePlayer();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                pause();
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
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                stop();
            }
            mMediaSession.setActive(false);
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void pause() {
        if (mMediaPlayer != null && mIsPreparedToPlay && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            MediaNotification.showUpdate(this, mCurrentFile, mMediaSession);
        }
    }

    public void play() {
        if (mMediaPlayer != null && mIsPreparedToPlay && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
            MediaNotification.showUpdate(this, mCurrentFile, mMediaSession);
        }
    }

    public void seekTo(int msec) {
        if (mMediaPlayer != null && mIsPreparedToPlay && mMediaPlayer.getDuration() > msec) {
            mMediaPlayer.seekTo(msec);
            MediaNotification.showUpdate(this, mCurrentFile, mMediaSession);
        }
    }

    public int getDuration() {
        if (mMediaPlayer != null && mIsPreparedToPlay) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    public int getPosition() {
        if (mMediaPlayer != null && mIsPreparedToPlay) {
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
            releasePlayer();
            stopForeground(true);
        }
    }

    public boolean isInitialized() {
        return mIsPreparedToPlay;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        Toast.makeText(this,
                "An info event was fired by the MediaPlayer.", Toast.LENGTH_SHORT).show();
        return true;
    }

    public File getCurrentFile() {
        return mCurrentFile;
    }

    public MediaSessionCompat getMediaSession() {
        return mMediaSession;
    }
}
