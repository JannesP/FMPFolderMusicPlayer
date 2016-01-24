package com.reallynourl.nourl.fmpfoldermusicplayer.ui.listadapter;

import android.graphics.Color;
import android.os.Process;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.reallynourl.nourl.fmpfoldermusicplayer.backend.MediaManager;
import com.reallynourl.nourl.fmpfoldermusicplayer.backend.playlist.CurrentPlaylist;
import com.reallynourl.nourl.fmpfoldermusicplayer.ui.control.OptionView;
import com.reallynourl.nourl.fmpfoldermusicplayer.ui.listadapter.item.AudioFileListItem;
import com.reallynourl.nourl.fmpfoldermusicplayer.ui.listadapter.item.ItemData;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.file.AudioFileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
public class MusicPlaylistAdapter extends BaseAdapter implements CurrentPlaylist.OnItemsChangedListener, Runnable, CurrentPlaylist.OnCurrentItemChangedListener, OptionView {
    private final Object mDataLock = new Object();
    private final Object mDataLoaderLock = new Object();
    private OnOptionsClickedListener mOnItemOptionsClickedListener;
    private final int mAccentColor;
    private ArrayList<ItemData> mData;
    private View mParent = null;
    private Thread mDataLoader;


    public MusicPlaylistAdapter(int accentColor) {
        this.mAccentColor = accentColor;
        reloadData();
        MediaManager.getInstance().getPlaylist().addOnItemsChangedListener(this);
        MediaManager.getInstance().getPlaylist().addOnCurrentItemChangedListener(this);
    }

    @Override
    public int getCount() {
        return MediaManager.getInstance().getPlaylist().getList().size();
    }

    @Override
    public File getItem(int position) {
        return MediaManager.getInstance().getPlaylist().getList().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        mParent = parent;
        AudioFileListItem audioFileListItem =
                (AudioFileListItem) AudioFileListItem.create(parent,
                        MediaManager.getInstance().getPlaylist().getList().get(position));

        synchronized (mDataLock) {
            ItemData itemData = mData.get(position);
            audioFileListItem.setTitle(itemData.getFile().getName());
            audioFileListItem.setSecondaryData(itemData.getSecondaryData());
        }
        if (MediaManager.getInstance().getPlaylist().getCurrentIndex() == position) {
            audioFileListItem.setBackgroundColor(mAccentColor);
        } else {
            audioFileListItem.setBackgroundColor(Color.TRANSPARENT);
        }
        convertView = audioFileListItem;
        ((OptionView)convertView).setOnItemOptionsClickedListener(mOnItemOptionsClickedListener);
        return convertView;
    }

    @Override
    public void onPlaylistItemsChanged(CurrentPlaylist currentPlaylist) {
        reloadData();
    }

    private void reloadData() {
        synchronized (mDataLoaderLock) {
            if (mDataLoader != null) {
                mDataLoader.interrupt();
            }
        }
        List<File> files = MediaManager.getInstance().getPlaylist().getList();
        mData = new ArrayList<>(files.size());
        for (int i = 0; i < files.size(); i++) {
            mData.add(i, new ItemData(files.get(i)));
        }
        notifyDataSetChanged();

        synchronized (mDataLoaderLock) {
            mDataLoader = new Thread(this);
            mDataLoader.start();
        }
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST);

        int length;
        synchronized (mDataLock) {
            length = mData.size();
        }

        for (int i = 0; i < length; i++) {
            synchronized (mDataLoaderLock) {
                if (mDataLoader.isInterrupted()) {
                    return;
                }
            }
            int duration;
            synchronized (mDataLock) {
                File file = mData.get(i).getFile();
                duration = AudioFileUtil.getDuration(file);
            }

            String time = "Error reading length.";
            if (duration != 0) {
                time = String.format("%d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(duration),
                        TimeUnit.MILLISECONDS.toSeconds(duration) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
                );
            }
            synchronized (mDataLock) {
                if (mData != null && mData.size() > i) {
                    mData.get(i).setSecondaryData(time);
                } else {
                    return;
                }
            }
            if (mParent != null) {
                mParent.post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });

            }
        }
    }

    @Override
    public void onPlaylistCurrentItemChanged(CurrentPlaylist currentPlaylist) {
        notifyDataSetChanged();
    }

    @Override
    public void setOnItemOptionsClickedListener(OnOptionsClickedListener listener) {
        mOnItemOptionsClickedListener = listener;
        notifyDataSetChanged();
    }
}
