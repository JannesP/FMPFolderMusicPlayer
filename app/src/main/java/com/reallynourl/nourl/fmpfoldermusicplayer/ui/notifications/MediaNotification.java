package com.reallynourl.nourl.fmpfoldermusicplayer.ui.notifications;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.NotificationCompat;

import com.reallynourl.nourl.fmpfoldermusicplayer.R;
import com.reallynourl.nourl.fmpfoldermusicplayer.backend.MediaIntentReceiver;
import com.reallynourl.nourl.fmpfoldermusicplayer.backend.MediaManager;
import com.reallynourl.nourl.fmpfoldermusicplayer.ui.activity.MainActivity;
import com.reallynourl.nourl.fmpfoldermusicplayer.ui.fragment.MusicPlayingFragment;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.Util;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.file.FileUtil;

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
public final class MediaNotification {
    private MediaNotification() {}
    private static final int NOTIFICATION_ID = 9871579;
    private static final int INTENT_CANCEL_ID = 3000;


    public static void showUpdate(Service service, File track,
                                    MediaSessionCompat mediaSession) {
        Bundle b = new Bundle(1);
        b.putString(MainActivity.FRAGMENT_EXTRA, MusicPlayingFragment.NAME);
        PendingIntent pi = PendingIntent.getActivity(service, 0,
                new Intent(service, MainActivity.class).putExtras(b),
                PendingIntent.FLAG_UPDATE_CURRENT);

        boolean isPlaying = MediaManager.getInstance().isPlaying();
        NotificationCompat.Action playPauseAction =
                isPlaying ? createPauseAction(service) : createPlayAction(service);
        NotificationCompat.Action nextAction = createNextAction(service);
        NotificationCompat.Action prevAction = createPreviousAction(service);

        android.support.v4.app.NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(service)
                .setSmallIcon(R.drawable.ic_play_arrow)
                .setContentTitle(FileUtil.getNameWithoutExtension(track))
                .setContentText(track.getParentFile().getName())
                .setContentIntent(pi)
                .setLargeIcon(
                        BitmapFactory.decodeResource(service.getResources(), R.mipmap.ic_launcher));

        int addedActions = 1;
        if (MediaManager.getInstance().hasPrevious()) {
            notificationBuilder.addAction(prevAction);
            addedActions++;
        }
        notificationBuilder.addAction(playPauseAction);
        if (MediaManager.getInstance().hasNext()) {
            notificationBuilder.addAction(nextAction);
            addedActions++;
        }

        PendingIntent cancelIntent = createCancelIntent(service);
        NotificationCompat.MediaStyle style = new NotificationCompat.MediaStyle()
                .setMediaSession(mediaSession.getSessionToken())
                .setShowCancelButton(true)
                .setCancelButtonIntent(cancelIntent);
        switch (addedActions) {
            case 1:
                style.setShowActionsInCompactView(0);
                break;
            case 2:
                style.setShowActionsInCompactView(0, 1);
                break;
            case 3:
                style.setShowActionsInCompactView(0, 1, 2);
                break;
        }
        notificationBuilder.setStyle(style);

        if (!isPlaying && !Util.isActivityAlive()) {
            service.stopForeground(false);
            notificationBuilder.setOngoing(false);
            notificationBuilder.setDeleteIntent(cancelIntent);
            NotificationManagerCompat.from(service).notify(NOTIFICATION_ID, notificationBuilder.build());
        } else {
            service.startForeground(NOTIFICATION_ID, notificationBuilder.build());
        }
        updateMediaSession(mediaSession);
    }

    public static void remove(Service service) {
        service.stopForeground(true);
        NotificationManagerCompat.from(service).cancel(NOTIFICATION_ID);
    }

    private static NotificationCompat.Action createPlayAction(Context context) {
        return createGenericButtonAction(context, R.drawable.ic_play_arrow, "Play",
                MediaIntentReceiver.ACTION_PLAY, MediaIntentReceiver.INTENT_ID_PLAY);
    }

    private static NotificationCompat.Action createPauseAction(Context context) {
        return createGenericButtonAction(context, R.drawable.ic_pause, "Pause",
                MediaIntentReceiver.ACTION_PAUSE, MediaIntentReceiver.INTENT_ID_PAUSE);
    }

    private static NotificationCompat.Action createNextAction(Context context) {
        return createGenericButtonAction(context, R.drawable.ic_skip_next_white, "Next",
                MediaIntentReceiver.ACTION_NEXT, MediaIntentReceiver.INTENT_ID_NEXT);
    }

    private static NotificationCompat.Action createPreviousAction(Context context) {
        return createGenericButtonAction(context, R.drawable.ic_skip_previous_white, "Previous",
                MediaIntentReceiver.ACTION_PREVIOUS, MediaIntentReceiver.INTENT_ID_PREVIOUS);
    }

    private static NotificationCompat.Action createGenericButtonAction(
            Context context, @DrawableRes int icon, String name, String action, int requestCode) {
        NotificationCompat.Action res = new NotificationCompat.Action.Builder(
                icon,
                name,
                PendingIntent.getBroadcast(context, requestCode,
                        new Intent(context, MediaIntentReceiver.class).setAction(action),
                        PendingIntent.FLAG_UPDATE_CURRENT)
        ).build();
        return res;
    }

    private static PendingIntent createCancelIntent(Context context) {
        return PendingIntent.getBroadcast(context, INTENT_CANCEL_ID,
                new Intent(context, MediaIntentReceiver.class)
                        .setAction(MediaIntentReceiver.ACTION_CLOSE),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static void updateMediaSession(MediaSessionCompat mediaSession) {
        File currentFile = MediaManager.getInstance().getCurrentFile();
        if (currentFile != null) {
            long validActions = PlaybackStateCompat.ACTION_STOP;
            if (MediaManager.getInstance().canPlay()) {
                validActions |= PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE;
            }
            if (MediaManager.getInstance().hasNext()) {
                validActions |= PlaybackStateCompat.ACTION_SKIP_TO_NEXT;
            }
            if (MediaManager.getInstance().hasPrevious()) {
                validActions |= PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS;
            }
            int playState = MediaManager.getInstance().isPlaying()
                    ? PlaybackStateCompat.STATE_PLAYING
                    : PlaybackStateCompat.STATE_PAUSED;
            mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM,
                            currentFile.getParentFile().getName())
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE,
                            FileUtil.getNameWithoutExtension(currentFile))
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION,
                            MediaManager.getInstance().getDuration())
                    .putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER,
                            MediaManager.getInstance().getPlaylist().getCurrentIndex() + 1)
                    .putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS,
                            MediaManager.getInstance().getPlaylist().size())
                            //.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
                    .build());
            mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                    .setState(playState, MediaManager.getInstance().getPosition(), 1.0f)
                    .setActions(validActions)
                    .build());
        } else {
            mediaSession.setActive(false);
        }
    }
}
