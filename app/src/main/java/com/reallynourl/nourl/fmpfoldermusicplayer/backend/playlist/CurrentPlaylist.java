package com.reallynourl.nourl.fmpfoldermusicplayer.backend.playlist;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.reallynourl.nourl.fmpfoldermusicplayer.R;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.file.ExtendedFile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

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
public class CurrentPlaylist extends Playlist {
    private int mCurrentFile;
    private boolean mIsShuffle;
    private RepeatMode mRepeatMode;
    private final List<OnCurrentItemChangedListener> mOnCurrentItemChangedListeners;
    private final List<OnModeChangedListener> mOnModeChangedListeners;
    private final Context mContext;


    public CurrentPlaylist(Context context) {
        super();
        mContext = context;
        loadPreferences();
        mCurrentFile = -1;
        mOnCurrentItemChangedListeners = new ArrayList<>(3);
        mOnModeChangedListeners = new ArrayList<>(3);
    }

    private void loadPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        mIsShuffle = prefs.getBoolean(mContext.getString(R.string.pref_playlist_shuffle), false);
        mRepeatMode = RepeatMode.get(prefs.getInt(mContext.getString(R.string.pref_playlist_repeat_mode), 0));
    }

    private void saveRepeatMode() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(mContext.getString(R.string.pref_playlist_repeat_mode), mRepeatMode.getValue());
        editor.apply();
    }

    private void saveShuffle() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(mContext.getString(R.string.pref_playlist_shuffle), mIsShuffle);
        editor.apply();
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

    public void removeOnPlaylistCurrentItemChangedListener(OnCurrentItemChangedListener listener) {
        while (true) {  //could be done without while(true) but looks cleaner this way
            if (!(mOnCurrentItemChangedListeners.remove(listener))) break;
        }
    }

    private void currentItemChanged() {
        for (OnCurrentItemChangedListener listener : mOnCurrentItemChangedListeners) {
            listener.onPlaylistCurrentItemChanged(this);
        }
    }

    private void modeChanged() {
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

    public int appendNext(ExtendedFile file) {
        return super.addAt(mCurrentFile + 1, new PlaylistItem(file));
    }

    public boolean hasNext() {
        boolean result = false;
        if (mItems.size() <= 0 || mCurrentFile == -1) {
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
        if (isShuffle() || mItems.size() <= 0 || mCurrentFile == -1) {
            result = false;
        } else if (mRepeatMode != RepeatMode.OFF) {
            result = true;
        } else if (getItemAt(mCurrentFile - 1) != null) {
            result = true;
        }
        return result;
    }

    public PlaylistItem getCurrent() {
        return getItemAt(mCurrentFile);
    }

    public PlaylistItem selectNext() {
        PlaylistItem result = null;
        if (selectNextInternal()) {
            result = getItemAt(mCurrentFile);
        }
        return result;
    }

    public PlaylistItem selectPrevious() {
        PlaylistItem result = null;
        if (selectPreviousInternal()) {
            result = getItemAt(mCurrentFile);
        }
        return result;
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
        if (mItems.size() > 0) {
            if (mIsShuffle) {
                int next = shufflePlay();
                if (next != -1) {
                    setCurrent(next);
                    selectedNext = true;
                }
            } else {
                switch (mRepeatMode) {
                    case OFF:
                        if (mItems.size() - 1 > mCurrentFile) {
                            setCurrent(mCurrentFile + 1);
                            selectedNext = true;
                        }
                        break;
                    case ALL:
                        setCurrent(++mCurrentFile % mItems.size());
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

    private int shufflePlay() {
        int unplayed = countRemainingUnplayed();
        Log.d("CURRENT_PLAYLIST", unplayed + " items left to play.");
        int result = mCurrentFile;
        switch (mRepeatMode) {
            case OFF:
                if (unplayed == 0) {
                    result = -1;
                }
                break;
            case ALL:
                if (unplayed == 0) {
                    Log.d("CURRENT_PLAYLIST", "No items left to play, resetting cause repeat mode all.");
                    for (PlaylistItem playlistItem : mItems) {
                        playlistItem.setPlayed(false);
                    }
                    unplayed = mItems.size();
                }
                break;
        }
        if (result != -1 && mRepeatMode != RepeatMode.SINGLE) {
            Random random = new Random(SystemClock.uptimeMillis());
            int nextUnplayed = random.nextInt(unplayed);
            for (int i = 0; i < mItems.size(); i++) {
                if (!mItems.get(i).isPlayed()) {
                    if (nextUnplayed-- == 0) {
                        result = i;
                        break;
                    }
                }
            }
        }
        return result;
    }

    private int countRemainingUnplayed() {
        int unplayed = 0;
        for (PlaylistItem item : mItems) {
            if (!item.isPlayed()) {
                unplayed++;
            }
        }
        return unplayed;
    }

    private boolean selectPreviousInternal() {
        boolean selectedPrevious = false;
        if (mItems.size() > 0) {
            switch (mRepeatMode) {
                case OFF:
                    if (mCurrentFile > 0) {
                        setCurrent(mCurrentFile - 1);
                        selectedPrevious = true;
                    }
                    break;
                case ALL:
                    if (mCurrentFile == 0) {
                        setCurrent(mItems.size() - 1);
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

    public void setCurrent(PlaylistItem playlistItem) {
        for (int i = 0; i < mItems.size(); i++) {
            if (mItems.get(i).getAbsolutePath().equals(playlistItem.getAbsolutePath())) {
                setCurrent(i);
            }
        }
    }

    public interface OnCurrentItemChangedListener {
        void onPlaylistCurrentItemChanged(CurrentPlaylist currentPlaylist);
    }

    public interface OnModeChangedListener {
        void onPlaylistModeChanged(CurrentPlaylist currentPlaylist);
    }
}
