package com.reallynourl.nourl.fmpfoldermusicplayer.backend.playlist;

import com.reallynourl.nourl.fmpfoldermusicplayer.utility.file.ExtendedFile;

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
    protected final ArrayList<PlaylistItem> mItems;

    public Playlist() {
        this.mItems = new ArrayList<>();
    }

    public int append(PlaylistItem item) {
        mItems.add(item);
        return mItems.size() - 1;
    }

    public void appendAll(ExtendedFile[] files) {
        for (ExtendedFile file : files) {
            mItems.add(new PlaylistItem(file));
        }
    }

    public void appendAll(Collection<? extends ExtendedFile> files) {
        for (ExtendedFile file : files) {
            mItems.add(new PlaylistItem(file));
        }
    }

    public void clear() {
        mItems.clear();
    }

    public boolean remove(int index) {
        boolean result = false;
        if (index >= 0 && mItems.size() < index) {
            if (mItems.remove(index) != null) {
                result = true;
            }
        }
        return result;
    }

    public int addAt(int position, PlaylistItem item) {
        if (mItems.size() >= (position + 1)) {
            mItems.add(position, item);
        } else if (mItems.isEmpty()) {
            mItems.add(item);
        }
        return position;
    }

    public PlaylistItem getItemAt(int index) {
        PlaylistItem result = null;
        if (index >= 0 && index < mItems.size()) {
            result = mItems.get(index);
        }
        return result;
    }

    public List<PlaylistItem> getList() {
        return Collections.unmodifiableList(mItems);
    }

    public int size() {
        return mItems.size();
    }

}
