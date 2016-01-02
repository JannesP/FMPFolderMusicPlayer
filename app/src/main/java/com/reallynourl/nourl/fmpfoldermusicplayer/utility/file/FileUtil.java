package com.reallynourl.nourl.fmpfoldermusicplayer.utility.file;

import android.os.Environment;

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
public final class FileUtil {
    private FileUtil() {}

    private static final String[] AUDIO_FORMATS = { "3gp", "mp4", "m4a", "aac", "ts", "3gp", "flac", "mp3", "mid", "xmf", "mxmf", "rtttl", "rtx", "ota", "ogg", "mkv", "wav" };

    public static boolean hasAudioExtension(File file) {
        String[] parts = file.getAbsolutePath().split("\\.");
        if (parts.length == 0) {
            return false;
        }
        for (String format : AUDIO_FORMATS) {
            if (format.equals(parts[parts.length - 1])) {
                return true;
            }
        }
        return false;
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public static String getNameWithoutExtension(File file) {
        return file.getName().replaceFirst("[.][^.]+$", "");
    }

    public static File[] listAudioFiles(File dir, boolean includeHidden) {
        return dir.listFiles(new AudioFileFilter(includeHidden, false, false));
    }
}
