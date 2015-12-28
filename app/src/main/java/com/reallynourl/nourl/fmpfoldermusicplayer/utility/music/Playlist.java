package com.reallynourl.nourl.fmpfoldermusicplayer.utility.music;

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
    private List<OnPlaylistItemsChangedListener> mPlaylistChangedListeners;
    private List<OnPlaylistCurrentItemChangedListener> mPlaylistCurrentItemChangedListeners;
    private Context mContext;


    public Playlist(Context context) {
        mContext = context;
        loadPreferences(context);
        mFiles = new ArrayList<>();
        mCurrentFile = -1;
        mPlaylistChangedListeners = new ArrayList<>(1);
        mPlaylistCurrentItemChangedListeners = new ArrayList<>(1);
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

    public void addOnPlayListChangedListener(OnPlaylistItemsChangedListener listener) {
        mPlaylistChangedListeners.remove(listener);
        mPlaylistChangedListeners.add(listener);
    }

    public void addOnPlaylistCurrentItemChangedListener(OnPlaylistCurrentItemChangedListener listener) {
        mPlaylistCurrentItemChangedListeners.remove(listener);
        mPlaylistCurrentItemChangedListeners.add(listener);
    }

    public void removeOnPlaylistChangedListener(OnPlaylistItemsChangedListener listener) {
        while (true) {  //could be done without while(true) but looks cleaner this way
            if (!(mPlaylistChangedListeners.remove(listener))) break;
        }
    }

    public void removeOnPlaylistCurrentItemChangedListener(OnPlaylistCurrentItemChangedListener listener) {
        while (true) {  //could be done without while(true) but looks cleaner this way
            if (!(mPlaylistCurrentItemChangedListeners.remove(listener))) break;
        }
    }

    protected void playlistChanged() {
        for (OnPlaylistItemsChangedListener listener : mPlaylistChangedListeners) {
            listener.onPlaylistItemsChanged(this);
        }
    }

    protected void currentItemChanged() {
        for (OnPlaylistCurrentItemChangedListener listener : mPlaylistCurrentItemChangedListeners) {
            listener.onPlaylistCurrentItemChanged(this);
        }
    }

    public boolean isShuffle() {
        return mIsShuffle;
    }

    public void setShuffle(boolean isShuffle) {
        this.mIsShuffle = isShuffle;
        saveShuffle();
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
        this.mRepeatMode = repeatMode;
        saveRepeatMode();
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

    public interface OnPlaylistItemsChangedListener {
        void onPlaylistItemsChanged(Playlist playlist);
    }

    public interface OnPlaylistCurrentItemChangedListener {
        void onPlaylistCurrentItemChanged(Playlist playlist);
    }
}
