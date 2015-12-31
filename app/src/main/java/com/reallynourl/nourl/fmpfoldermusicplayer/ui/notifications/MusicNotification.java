package com.reallynourl.nourl.fmpfoldermusicplayer.ui.notifications;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.media.AudioAttributes;
import android.media.session.MediaSession;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.NotificationCompat;

import com.reallynourl.nourl.fmpfoldermusicplayer.R;
import com.reallynourl.nourl.fmpfoldermusicplayer.ui.activities.MainActivity;
import com.reallynourl.nourl.fmpfoldermusicplayer.ui.fragments.music.MusicControlFragment;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.file.FileUtil;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.music.MediaService;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.music.MusicIntentReceiver;

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
public class MusicNotification {
    public final static int NOTIFICATION_ID = 9871579;

    public static Notification create(Context context, File track,
                                      MediaSessionCompat.Token mediaSessionToken) {
        Bundle b = new Bundle(1);
        b.putString(MainActivity.FRAGMENT_EXTRA, MusicControlFragment.NAME);
        PendingIntent pi = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class).putExtras(b),
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action playAction = createPlayAction(context);
        NotificationCompat.Action nextAction = createNextAction(context);
        NotificationCompat.Action prevAction = createPreviousAction(context);

        Notification noti = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_play_arrow)
                .setContentTitle(FileUtil.getNameWithoutExtension(track))
                .setContentText(track.getParentFile().getName())
                .setContentIntent(pi)
                .setLargeIcon(
                        BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setStyle(new NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSessionToken)
                        .setShowActionsInCompactView(0, 1, 2)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(PendingIntent.getBroadcast(context, 0,
                                new Intent(context, MusicIntentReceiver.class)
                                        .setAction(MusicIntentReceiver.ACTION_CLOSE),
                                PendingIntent.FLAG_UPDATE_CURRENT)))
                .addAction(prevAction)
                .addAction(playAction)
                .addAction(nextAction)
                .build();
        return noti;
    }

    private static NotificationCompat.Action createPlayAction(Context context) {
        return createGenericButtonAction(context, R.drawable.ic_play_arrow, "Play",
                MusicIntentReceiver.EXTRA_VALUE_PLAY);
    }

    private static NotificationCompat.Action createNextAction(Context context) {
        return createGenericButtonAction(context, R.drawable.ic_skip_next_white, "Next",
                MusicIntentReceiver.EXTRA_VALUE_NEXT);
    }

    private static NotificationCompat.Action createPreviousAction(Context context) {
        return createGenericButtonAction(context, R.drawable.ic_skip_previous_white, "Previous",
                MusicIntentReceiver.EXTRA_VALUE_PREVIOUS);
    }

    private static NotificationCompat.Action createGenericButtonAction(
            Context context, @DrawableRes int icon, String name, int command) {
        NotificationCompat.Action res = new NotificationCompat.Action.Builder(
                icon,
                name,
                PendingIntent.getBroadcast(context, command,
                        new Intent(context, MusicIntentReceiver.class)
                                .setAction(MusicIntentReceiver.ACTION_MUSIC_CONTROL)
                                .putExtra(MusicIntentReceiver.EXTRA_MUSIC_CONTROL_KEY
                                        , command),
                        PendingIntent.FLAG_UPDATE_CURRENT)
        ).build();
        return res;
    }
}
