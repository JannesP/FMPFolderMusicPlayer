package com.reallynourl.nourl.fmpfoldermusicplayer.utility;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ListView;

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
    private static final String TAG = "Util";
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
        ActivityCompat.requestPermissions(activity,
                new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, requestCode);
    }

    public static boolean isActivityAlive() {
        return MainActivity.isAlive();
    }

    /**
     * Saves the top item and padding to the bundle.
     * @param bundle the bundle to save the data to
     * @param listView the listview which should be used
     * @param lvName the name to create the unique name for the stored data.
     *               Used names in the bundle are:
     *               lvName_scroll_index
     *               lvName_top
     */
    public static void saveScrollPositionToBundle(Bundle bundle, ListView listView, String lvName) {
        //get the top most visible item
        int scrollIndex = listView.getFirstVisiblePosition();
        //save the top most ListItem created to calculate the exact position
        View topItem = listView.getChildAt(0);
        int top = (topItem == null) ? 0 : (topItem.getTop() - listView.getPaddingTop());

        bundle.putInt(lvName + "_scroll_index", scrollIndex);
        bundle.putInt(lvName + "_top", top);
    }

    /**
     * Loads the position of the ListView from the Bundle. Make sure you saved if before with
     * {@link Util#saveScrollPositionToBundle(Bundle, ListView, String)}
     * This method does nothing if th data couldn't be found.
     * @param bundle the bundle to load the data from
     * @param listView the ListView to scroll
     * @param lvName the name previously used in
     * {@link Util#saveScrollPositionToBundle(Bundle, ListView, String)}
     */
    public static void loadScrollPositionFromBundle(Bundle bundle, ListView listView, String lvName) {
        int scrollIndex = bundle.getInt(lvName + "_scroll_index", Integer.MIN_VALUE);
        int top = bundle.getInt(lvName + "_top", Integer.MIN_VALUE);
        if (scrollIndex == Integer.MIN_VALUE || top == Integer.MIN_VALUE) {
            Log.i(TAG, "Failed to load scroll position from bundle for name: " + lvName);
        } else {
            listView.setSelectionFromTop(scrollIndex, top);
        }
    }
}
