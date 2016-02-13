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
//TODO: Implement multiple of the same file.
public class Playlist {
    protected final ArrayList<PlaylistItem> mItems;
    private final List<OnItemsChangedListener> mOnItemsChangedListeners;

    public Playlist() {
        this.mItems = new ArrayList<>();
        mOnItemsChangedListeners = new ArrayList<>(3);
    }

    public int append(ExtendedFile item) {
        PlaylistItem pItem = new PlaylistItem(item);
        return append(pItem);
    }

    public int append(PlaylistItem item) {
        mItems.add(item);
        item.setPlaylist(this);
        itemsChanged();
        return mItems.size() - 1;
    }

    public void appendAll(ExtendedFile[] files) {
        for (ExtendedFile file : files) {
            PlaylistItem item = new PlaylistItem(file);
            mItems.add(item);
            item.setPlaylist(this);
        }
        itemsChanged();
    }

    public void appendAll(Collection<? extends ExtendedFile> files) {
        for (ExtendedFile file : files) {
            PlaylistItem item = new PlaylistItem(file);
            mItems.add(item);
            item.setPlaylist(this);
        }
        itemsChanged();
    }

    public void remove(PlaylistItem file) {
        boolean removed = mItems.remove(file);
        if (removed) {
            itemsChanged();
        }
    }

    public void clear() {
        mItems.clear();
        itemsChanged();
    }

    public boolean remove(int index) {
        boolean removed = false;
        if (index >= 0 && mItems.size() < index) {
            if (mItems.remove(index) != null) {
                removed = true;
            }
        }
        if (removed) itemsChanged();
        return removed;
    }

    public int addAt(int position, PlaylistItem item) {
        if (mItems.size() >= (position + 1)) {
            mItems.add(position, item);
        } else if (mItems.isEmpty()) {
            mItems.add(item);
        }
        item.setPlaylist(this);
        itemsChanged();
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

    private void itemsChanged() {
        for (OnItemsChangedListener listener : mOnItemsChangedListeners) {
            listener.onPlaylistItemsChanged(this);
        }
    }

    public void removeOnModeChangedListener(OnItemsChangedListener listener) {
        while (true) {  //could be done without while(true) but looks cleaner this way
            if (!(mOnItemsChangedListeners.remove(listener))) break;
        }
    }

    public void addOnItemsChangedListener(OnItemsChangedListener l) {
        mOnItemsChangedListeners.remove(l);
        mOnItemsChangedListeners.add(l);
    }

    public interface OnItemsChangedListener {
        void onPlaylistItemsChanged(Playlist playlist);
    }
}
