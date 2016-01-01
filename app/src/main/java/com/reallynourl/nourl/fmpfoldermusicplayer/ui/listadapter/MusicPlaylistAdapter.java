package com.reallynourl.nourl.fmpfoldermusicplayer.ui.listadapter;

import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.reallynourl.nourl.fmpfoldermusicplayer.ui.control.OptionView;
import com.reallynourl.nourl.fmpfoldermusicplayer.ui.listadapter.item.AudioFileListItem;
import com.reallynourl.nourl.fmpfoldermusicplayer.backend.MediaManager;
import com.reallynourl.nourl.fmpfoldermusicplayer.backend.playlist.Playlist;

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
public class MusicPlaylistAdapter extends BaseAdapter implements Playlist.OnItemsChangedListener, Runnable, Playlist.OnCurrentItemChangedListener, OptionView {
    private final Object mDataLock = new Object();
    private OnOptionsClickedListener mOnItemOptionsClickedListener;
    private int mAccentColor;
    private ArrayList<ItemData> mData;
    private View mParent = null;


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
            audioFileListItem.setTitle(mData.get(position).getFile().getName());
            audioFileListItem.setSecondaryData(mData.get(position).getSecondaryData());
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
    public void onPlaylistItemsChanged(Playlist playlist) {
        reloadData();
    }

    private void reloadData() {
        List<File> files = MediaManager.getInstance().getPlaylist().getList();
        mData = new ArrayList<>(files.size());
        for (int i = 0; i < files.size(); i++) {
            mData.add(i, new ItemData(files.get(i)));
        }
        notifyDataSetChanged();

        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST);

        int length;
        synchronized (mDataLock) {
            length = mData.size();
        }

        for (int i = 0; i < length; i++) {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            String path;
            synchronized (mDataLock) {
                path = mData.get(i).getFile().getAbsolutePath();
            }
            boolean error = false;
            try {
                mmr.setDataSource(mData.get(i).getFile().getAbsolutePath());
            } catch (IllegalArgumentException e) {
                Log.i("PlayList Adapter", "Failed to load data for: " + path);
                error = true;
            }
            if (!error) {
                String durationString = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                int duration = durationString == null ? 0 : Integer.parseInt(durationString);
                String time = String.format("%d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(duration),
                        TimeUnit.MILLISECONDS.toSeconds(duration) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
                );
                synchronized (mDataLock) {
                    mData.get(i).setSecondaryData(time);
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
    }

    @Override
    public void onPlaylistCurrentItemChanged(Playlist playlist) {
        notifyDataSetChanged();
    }

    @Override
    public void setOnItemOptionsClickedListener(OnOptionsClickedListener listener) {
        mOnItemOptionsClickedListener = listener;
        notifyDataSetChanged();
    }

    private class ItemData {
        private File mFile;
        private String mSecondaryData;

        public ItemData(File mFile) {
            this.mFile = mFile;
            this.mSecondaryData = "";
        }

        public File getFile() {
            return mFile;
        }

        public void setSecondaryData(String secondaryData) {
            this.mSecondaryData = secondaryData;
        }

        public String getSecondaryData() {
            return mSecondaryData;
        }
    }
}
