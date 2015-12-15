package com.reallynourl.nourl.fmpfoldermusicplayer.utility;

import android.content.Context;
import android.os.Environment;
import android.util.TypedValue;

import java.io.File;
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

}
