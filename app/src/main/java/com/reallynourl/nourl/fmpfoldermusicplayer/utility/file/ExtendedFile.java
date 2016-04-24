package com.reallynourl.nourl.fmpfoldermusicplayer.utility.file;

import android.media.MediaMetadataRetriever;
import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Copyright (C) 2016  Jannes Peters
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
public class ExtendedFile extends File {
    private static final String[] VALID_AUDIO_FORMATS = { "3gp", "mp4", "m4a", "aac", "ts", "3gp", "flac", "mp3", "mid", "xmf", "mxmf", "rtttl", "rtx", "ota", "ogg", "mkv", "wav" };

    private int duration = 0;

    public ExtendedFile(File dir, String name) {
        super(dir, name);
    }

    public ExtendedFile(String path) {
        super(path);
    }

    public ExtendedFile(String dirPath, String name) {
        super(dirPath, name);
    }

    public ExtendedFile(URI uri) {
        super(uri);
    }

    public boolean hasAudioExtension() {
        return hasAudioExtension(this);
    }

    public static boolean hasAudioExtension(File file) {
        String extension = getExtension(file);
        if (!extension.equals("")) {
            for (String format : VALID_AUDIO_FORMATS) {
                if (format.equals(extension)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String getExtension(File file) {
        String result = "";
        if (file.isFile() || !file.exists()) {
            String[] parts = file.getAbsolutePath().split("\\.");
            if (parts.length > 1) {
                result = parts[parts.length - 1];
            }
        }
        return result;
    }

    public String getExtension() {
        return getExtension(this);
    }

    public String getNameWithoutExtension() {
        String nameWithoutExtension = getName();
        if (isFile()) {
            nameWithoutExtension = nameWithoutExtension.replaceFirst("[.][^.]+$", "");
        }
        return nameWithoutExtension;
    }

    public List<ExtendedFile> listAudioFiles(boolean includeHidden) {
        return listExtendedFiles(new AudioFileFilter(includeHidden, false, false));
    }

    public static FileType getType(File file) {
        if (file.isDirectory()) {
            return FileType.DIRECTORY;
        } else if (file.isFile()) {
            if (hasAudioExtension(file)) {
                return FileType.AUDIO;
            } else {
                return FileType.FILE;
            }
        }
        return FileType.ERROR;
    }

    public FileType getType() {
        return getType(this);
    }

    public List<ExtendedFile> listExtendedFiles(FileFilter filter) {
        ExtendedFile[] files = listExtendedFiles();
        if (filter == null) {
            return Arrays.asList(files);
        } else if (files == null) {
            return new ArrayList<>();
        }
        List<ExtendedFile> result = new ArrayList<>(files.length);
        for (ExtendedFile file : files) {
            if (filter.accept(file)) {
                result.add(file);
            }
        }
        return result;
    }

    public boolean hasDurationCached() {
        return duration != 0;
    }

    public int getDuration() {
        int result = duration;
        if (result == 0) {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            String path = getAbsolutePath();
            try {
                mmr.setDataSource(path);
            } catch (IllegalArgumentException e) {
                Log.i("PlayList Adapter", "Failed to load duration for: " + path);
            }
            try {
                result = Integer.parseInt(
                        mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            } catch (NumberFormatException ignored) {
            } finally {
                mmr.release();
            }
            duration = result;
        }
        return result;
    }

    /**
     * Returns an array of files contained in the directory represented by this
     * file. The result is {@code null} if this file is not a directory. The
     * paths of the files in the array are absolute if the path of this file is
     * absolute, they are relative otherwise.
     *
     * @return an array of files or {@code null}.
     */
    public ExtendedFile[] listExtendedFiles() {
        return filenamesToExtendedFiles(list());
    }

    /**
     * Converts a String[] containing filenames to a File[].
     * Note that the filenames must not contain slashes.
     * This method is to remove duplication in the implementation
     * of File.list's overloads.
     */
    private ExtendedFile[] filenamesToExtendedFiles(String[] filenames) {
        if (filenames == null) {
            return null;
        }
        int count = filenames.length;
        ExtendedFile[] result = new ExtendedFile[count];
        for (int i = 0; i < count; ++i) {
            result[i] = new ExtendedFile(this, filenames[i]);
        }
        return result;
    }
}
