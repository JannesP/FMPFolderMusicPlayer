package com.reallynourl.nourl.fmpfoldermusicplayer.utility;

import android.util.Log;

import com.reallynourl.nourl.fmpfoldermusicplayer.backend.MediaService;

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
public class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final Thread.UncaughtExceptionHandler mUncaughtExceptionHandler;
    public MyUncaughtExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        mUncaughtExceptionHandler = uncaughtExceptionHandler;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (thread.getName().equals("main")) {
            Log.e("Main thread", "Main Thread died! Trying to shutdown all the things.");
            MediaService mediaService = MediaService.getInstance();
            if (mediaService != null) {
                mediaService.stopSelf();
            }
        }
        mUncaughtExceptionHandler.uncaughtException(thread, ex);
    }
}
