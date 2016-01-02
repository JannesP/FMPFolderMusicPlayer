package com.reallynourl.nourl.fmpfoldermusicplayer.utility;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;

import com.reallynourl.nourl.fmpfoldermusicplayer.ui.activity.MainActivity;

import java.util.concurrent.TimeUnit;

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
public final class Util {
    private Util() {}

    public static String getDurationString(int msec) {
        String time = String.format("%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(msec),
                TimeUnit.MILLISECONDS.toSeconds(msec) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(msec))
        );
        return time;
    }

    public static int getAccentColor(final Context context) {
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(android.support.design.R.attr.colorAccent, value, true);
        return value.data;
    }

    public static boolean hasStoragePermission(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestStoragePermission(Activity activity, int requestCode) {
        ActivityCompat.requestPermissions(activity, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, requestCode);
    }

    public static boolean isActivityAlive() {
        return MainActivity.isAlive();
    }
}
