package com.reallynourl.nourl.fmpfoldermusicplayer.ui.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.reallynourl.nourl.fmpfoldermusicplayer.R;
import com.reallynourl.nourl.fmpfoldermusicplayer.ui.activities.MainActivity;

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

    public static Notification create(Context context, String songName) {
        Bundle b = new Bundle(1);
        b.putString(MainActivity.FRAGMENT_EXTRA, "CONTROLS");
        PendingIntent pi = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class).putExtras(b), PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle("Playing Music!");
        builder.setContentText(songName);
        builder.setSmallIcon(R.drawable.ic_play_arrow);
        builder.setContentIntent(pi);
        builder.setOngoing(true);
        return builder.getNotification();
    }
}
