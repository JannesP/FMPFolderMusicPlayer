package com.reallynourl.nourl.fmpfoldermusicplayer.backend.playlist;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
public class Playlist {
    protected final ArrayList<File> mFiles;

    public Playlist() {
        this.mFiles = new ArrayList<>();
    }

    public int append(File file) {
        mFiles.add(file);
        return mFiles.size() - 1;
    }

    public void appendAll(File[] files) {
        Collections.addAll(mFiles, files);
    }

    public void appendAll(Collection<? extends File> files) {
        mFiles.addAll(files);
    }

    public void clear() {
        mFiles.clear();
    }

    public boolean remove(int index) {
        boolean result = false;
        if (index >= 0 && mFiles.size() < index) {
            if (mFiles.remove(index) != null) {
                result = true;
            }
        }
        return result;
    }

    public int addAt(int position, File file) {
        if (mFiles.size() >= (position + 1)) {
            mFiles.add(position, file);
        } else if (mFiles.isEmpty()) {
            mFiles.add(file);
        }
        return position;
    }

    public File getItemAt(int index) {
        File result = null;
        if (index >= 0 && index < mFiles.size()) {
            result = mFiles.get(index);
        }
        return result;
    }

    public List<File> getList() {
        return Collections.unmodifiableList(mFiles);
    }

    public int size() {
        return mFiles.size();
    }

}
