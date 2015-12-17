package com.reallynourl.nourl.fmpfoldermusicplayer.utility.music;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
public class Playlist {
    private ArrayList<File> mFiles;
    private int mCurrentFile;
    private boolean mIsShuffle;
    private RepeatMode mRepeatMode;
    private List<OnPlaylistChangedListener> mPlaylistChangedListeners;


    public Playlist() {
        mFiles = new ArrayList<>();
        mIsShuffle = false;
        mCurrentFile = -1;
        mRepeatMode = RepeatMode.OFF;
        mPlaylistChangedListeners = new ArrayList<>(1);
    }

    public void addOnPlayListChangedListener(OnPlaylistChangedListener listener) {
        mPlaylistChangedListeners.remove(listener);
        mPlaylistChangedListeners.add(listener);
    }

    public void removeOnPlaylistChangedListener(OnPlaylistChangedListener listener) {
        while (true) {
            if (!(mPlaylistChangedListeners.remove(listener))) break;
        }
    }

    public boolean isShuffle() {
        return mIsShuffle;
    }

    public void setShuffle(boolean isShuffle) {
        this.mIsShuffle = isShuffle;
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

    public void remove(int index) {
        if (index >= 0 && mFiles.size() < index) {
            mFiles.remove(index);
        }
    }

    public int appendNext(File file) {
        if (mFiles.size() >= (mCurrentFile + 1)) {
            mFiles.add(mCurrentFile + 1, file);
        } else if (mFiles.isEmpty()) {
            mFiles.add(file);
        }
        return mCurrentFile + 1;
    }

    public boolean hasNext() {
        boolean result = false;
        if (getItemAt(mCurrentFile + 1) != null) {
            result = true;
        }
        return result;
    }

    public boolean hasPrevious() {
        boolean result = false;
        if (getItemAt(mCurrentFile - 1) != null) {
            result = true;
        }
        return result;
    }

    private File getItemAt(int index) {
        File result = null;
        if (index >= 0 && index < mFiles.size()) {
            result = mFiles.get(index);
        }
        return result;
    }

    public File getCurrent() {
        return getItemAt(mCurrentFile);
    }

    public File selectNext() {
        File result = null;
        if (selectNextInternal()) {
            result = getItemAt(mCurrentFile);
        }
        return result;
    }

    public List<File> getList() {
        return Collections.unmodifiableList(mFiles);
    }

    public RepeatMode getRepeatMode() {
        return mRepeatMode;
    }

    public void setRepeatMode(RepeatMode repeatMode) {
        this.mRepeatMode = repeatMode;
    }

    private boolean selectNextInternal() {
        boolean selectedNext = false;
        if (mFiles.size() > 0) {
            switch (mRepeatMode) {
                case OFF:
                    if (mFiles.size() >= (mCurrentFile + 1)) {
                        mCurrentFile++;
                        selectedNext = true;
                    }
                    break;
                case ALL:
                    mCurrentFile = mCurrentFile % mFiles.size();
                    selectedNext = true;
                    break;
                case SINGLE:
                    selectedNext = true;
                    break;
            }
        } else {
            mCurrentFile = -1;
        }
        return selectedNext;
    }

    public int getCurrentIndex() {
        return mCurrentFile;
    }

    public void setCurrent(int current) {
        this.mCurrentFile = current;
    }

    public interface OnPlaylistChangedListener {
        void onPlaylistChanged(Playlist playlist);
    }
}
