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
        while (true) {  //could be done without while(true) but looks cleaner this way
            if (!(mPlaylistChangedListeners.remove(listener))) break;
        }
    }

    protected void playlistChanged() {
        for (OnPlaylistChangedListener listener : mPlaylistChangedListeners) {
            listener.onPlaylistChanged(this);
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
        playlistChanged();
        return mFiles.size() - 1;
    }

    public void appendAll(File[] files) {
        Collections.addAll(mFiles, files);
        playlistChanged();
    }

    public void appendAll(Collection<? extends File> files) {
        mFiles.addAll(files);
        playlistChanged();
    }

    public void clear() {
        mFiles.clear();
        playlistChanged();
    }

    public void remove(int index) {
        if (index >= 0 && mFiles.size() < index) {
            if (mFiles.remove(index) != null) {
                playlistChanged();
            }
        }
    }

    public int appendNext(File file) {
        if (mFiles.size() >= (mCurrentFile + 1)) {
            mFiles.add(mCurrentFile + 1, file);
            playlistChanged();
        } else if (mFiles.isEmpty()) {
            mFiles.add(file);
            playlistChanged();
        }
        return mCurrentFile + 1;
    }

    public boolean hasNext() {
        boolean result = false;
        if (mFiles.size() <= 0) {
          result = false;
        } else if (isShuffle()) {
            result = true;
        } else if (mRepeatMode != RepeatMode.OFF) {
            result = true;
        } else if (getItemAt(mCurrentFile + 1) != null) {
            result = true;
        }
        return result;
    }

    public boolean hasPrevious() {
        boolean result = false;
        if (isShuffle() || mFiles.size() > 0) {
            result = false;
        } else if (mRepeatMode != RepeatMode.OFF) {
            result = true;
        } else if (getItemAt(mCurrentFile - 1) != null) {
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
            playlistChanged();
        }
        return result;
    }

    public File selectPrevious() {
        File result = null;
        if (selectPreviousInternal()) {
            result = getItemAt(mCurrentFile);
            playlistChanged();
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
            if (mIsShuffle) {
                mCurrentFile = shufflePlay();
                selectedNext = true;
            } else {
                switch (mRepeatMode) {
                    case OFF:
                        if (mFiles.size() - 1 > mCurrentFile) {
                            mCurrentFile++;
                            selectedNext = true;
                        }
                        break;
                    case ALL:
                        mCurrentFile = ++mCurrentFile % mFiles.size();
                        selectedNext = true;
                        break;
                    case SINGLE:
                        selectedNext = true;
                        break;
                }
            }
        } else {
            mCurrentFile = -1;
        }
        return selectedNext;
    }

    //TODO: Implement good shuffle. Since I'm currently not tracking the last played songs this has to wait.
    private int shufflePlay() {
        int result = mCurrentFile;
        if (getRepeatMode() != RepeatMode.SINGLE) {
            result = (int)(Math.random() * (double)mFiles.size());
        }
        return result;
    }

    private boolean selectPreviousInternal() {
        boolean selectedPrevious = false;
        if (mFiles.size() > 0) {
            switch (mRepeatMode) {
                case OFF:
                    if (mCurrentFile > 0) {
                        mCurrentFile--;
                        selectedPrevious = true;
                    }
                    break;
                case ALL:
                    if (mCurrentFile == 0) {
                        mCurrentFile = mFiles.size() - 1;
                    } else {
                        mCurrentFile--;
                    }
                    selectedPrevious = true;
                    break;
                case SINGLE:
                    selectedPrevious = true;
                    break;
            }
        } else {
            mCurrentFile = -1;
        }
        return selectedPrevious;
    }

    public int getCurrentIndex() {
        return mCurrentFile;
    }

    public void setCurrent(int current) {
        this.mCurrentFile = current;
        playlistChanged();
    }

    public interface OnPlaylistChangedListener {
        void onPlaylistChanged(Playlist playlist);
    }
}
