package com.reallynourl.nourl.fmpfoldermusicplayer.backend.playlist;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
    public static final String PREF_REPEAT_MODE = "pref_playlist_repeat_mode";
    public static final String PREF_SHUFFLE = "pref_playlist_shuffle";

    private ArrayList<File> mFiles;
    private int mCurrentFile;
    private boolean mIsShuffle;
    private RepeatMode mRepeatMode;
    private List<OnItemsChangedListener> mOnItemsChangedListeners;
    private List<OnCurrentItemChangedListener> mOnCurrentItemChangedListeners;
    private List<OnModeChangedListener> mOnModeChangedListeners;
    private Context mContext;


    public Playlist(Context context) {
        mContext = context;
        loadPreferences(context);
        mFiles = new ArrayList<>();
        mCurrentFile = -1;
        mOnItemsChangedListeners = new ArrayList<>(3);
        mOnCurrentItemChangedListeners = new ArrayList<>(3);
        mOnModeChangedListeners = new ArrayList<>(3);
    }

    private void loadPreferences(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        mIsShuffle = prefs.getBoolean(PREF_SHUFFLE, false);
        mRepeatMode = RepeatMode.get(prefs.getInt(PREF_REPEAT_MODE, 0));
    }

    private void saveRepeatMode() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PREF_REPEAT_MODE, mRepeatMode.getValue());
        editor.apply();
    }

    private void saveShuffle() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(PREF_SHUFFLE, mIsShuffle);
        editor.apply();
    }

    public void addOnItemsChangedListener(OnItemsChangedListener l) {
        mOnItemsChangedListeners.remove(l);
        mOnItemsChangedListeners.add(l);
    }

    public void addOnModeChangedListener(OnModeChangedListener l) {
        mOnModeChangedListeners.remove(l);
        mOnModeChangedListeners.add(l);
    }

    public void addOnCurrentItemChangedListener(OnCurrentItemChangedListener l) {
        mOnCurrentItemChangedListeners.remove(l);
        mOnCurrentItemChangedListeners.add(l);
    }

    public void removeOnPlaylistChangedListener(OnModeChangedListener listener) {
        while (true) {  //could be done without while(true) but looks cleaner this way
            if (!(mOnModeChangedListeners.remove(listener))) break;
        }
    }

    public void removeOnModeChangedListener(OnItemsChangedListener listener) {
        while (true) {  //could be done without while(true) but looks cleaner this way
            if (!(mOnItemsChangedListeners.remove(listener))) break;
        }
    }

    public void removeOnPlaylistCurrentItemChangedListener(OnCurrentItemChangedListener listener) {
        while (true) {  //could be done without while(true) but looks cleaner this way
            if (!(mOnCurrentItemChangedListeners.remove(listener))) break;
        }
    }

    protected void itemsChanged() {
        for (OnItemsChangedListener listener : mOnItemsChangedListeners) {
            listener.onPlaylistItemsChanged(this);
        }
    }

    protected void currentItemChanged() {
        for (OnCurrentItemChangedListener listener : mOnCurrentItemChangedListeners) {
            listener.onPlaylistCurrentItemChanged(this);
        }
    }

    protected void modeChanged() {
        for (OnModeChangedListener listener : mOnModeChangedListeners) {
            listener.onPlaylistModeChanged(this);
        }
    }

    public boolean isShuffle() {
        return mIsShuffle;
    }

    public void setShuffle(boolean isShuffle) {
        if (isShuffle != mIsShuffle) {
            this.mIsShuffle = isShuffle;
            saveShuffle();
            modeChanged();
        }
    }

    public int append(File file) {
        mFiles.add(file);
        itemsChanged();
        return mFiles.size() - 1;
    }

    public void appendAll(File[] files) {
        Collections.addAll(mFiles, files);
        itemsChanged();
    }

    public void appendAll(Collection<? extends File> files) {
        mFiles.addAll(files);
        itemsChanged();
    }

    public void clear() {
        mFiles.clear();
        itemsChanged();
    }

    public void remove(int index) {
        if (index >= 0 && mFiles.size() < index) {
            if (mFiles.remove(index) != null) {
                itemsChanged();
            }
        }
    }

    public int appendNext(File file) {
        if (mFiles.size() >= (mCurrentFile + 1)) {
            mFiles.add(mCurrentFile + 1, file);
            itemsChanged();
        } else if (mFiles.isEmpty()) {
            mFiles.add(file);
            itemsChanged();
        }
        return mCurrentFile + 1;
    }

    public boolean hasNext() {
        boolean result = false;
        if (mFiles.size() <= 0 || mCurrentFile == -1) {
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
        if (isShuffle() || mFiles.size() <= 0 || mCurrentFile == -1) {
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
        }
        return result;
    }

    public File selectPrevious() {
        File result = null;
        if (selectPreviousInternal()) {
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
        if (mRepeatMode != repeatMode) {
            this.mRepeatMode = repeatMode;
            saveRepeatMode();
            modeChanged();
        }

    }

    private boolean selectNextInternal() {
        boolean selectedNext = false;
        if (mFiles.size() > 0) {
            if (mIsShuffle) {
                setCurrent(shufflePlay());
                selectedNext = true;
            } else {
                switch (mRepeatMode) {
                    case OFF:
                        if (mFiles.size() - 1 > mCurrentFile) {
                            setCurrent(mCurrentFile + 1);
                            selectedNext = true;
                        }
                        break;
                    case ALL:
                        setCurrent(++mCurrentFile % mFiles.size());
                        selectedNext = true;
                        break;
                    case SINGLE:
                        selectedNext = true;
                        break;
                }
            }
        } else {
            setCurrent(-1);
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
                        setCurrent(mCurrentFile - 1);
                        selectedPrevious = true;
                    }
                    break;
                case ALL:
                    if (mCurrentFile == 0) {
                        setCurrent(mFiles.size() - 1);
                    } else {
                        setCurrent(mCurrentFile - 1);
                    }
                    selectedPrevious = true;
                    break;
                case SINGLE:
                    selectedPrevious = true;
                    break;
            }
        } else {
            setCurrent(-1);
        }
        return selectedPrevious;
    }

    public int getCurrentIndex() {
        return mCurrentFile;
    }

    public void setCurrent(int current) {
        this.mCurrentFile = current;
        currentItemChanged();
    }

    public void clearCurrent() {
        setCurrent(-1);
    }

    public int size() {
        return mFiles.size();
    }

    public interface OnItemsChangedListener {
        void onPlaylistItemsChanged(Playlist playlist);
    }

    public interface OnCurrentItemChangedListener {
        void onPlaylistCurrentItemChanged(Playlist playlist);
    }

    public interface OnModeChangedListener {
        void onPlaylistModeChanged(Playlist playlist);
    }
}
