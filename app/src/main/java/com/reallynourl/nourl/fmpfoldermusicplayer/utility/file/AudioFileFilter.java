package com.reallynourl.nourl.fmpfoldermusicplayer.utility.file;

import java.io.File;
import java.io.FileFilter;

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
public class AudioFileFilter implements FileFilter {
    private final boolean mAllowHidden;
    private final boolean mAllowNonAudio;
    private final boolean mAllowDirectory;

    public AudioFileFilter(boolean allowHidden, boolean allowNonAudio, boolean allowDirectory) {
        this.mAllowHidden = allowHidden;
        this.mAllowNonAudio = allowNonAudio;
        this.mAllowDirectory = allowDirectory;
    }

    @Override
    public boolean accept(File file) {
        if (!mAllowHidden && file.isHidden()) {
            return false;
        }
        FileType ft = FileType.getType(file);
        if (!mAllowNonAudio && ft == FileType.FILE) {
            return false;
        }
        if (!mAllowDirectory && ft == FileType.DIRECTORY) {
            return false;
        }
        return true;
    }
}
