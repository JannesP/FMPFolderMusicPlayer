package com.reallynourl.nourl.fmpfoldermusicplayer.ui.listadapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.reallynourl.nourl.fmpfoldermusicplayer.ui.control.OptionView;
import com.reallynourl.nourl.fmpfoldermusicplayer.ui.listadapter.item.ItemData;
import com.reallynourl.nourl.fmpfoldermusicplayer.ui.listadapter.item.MusicBrowserListItem;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.file.ExtendedFile;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.file.FileType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (C) 2015  Jannes Peters
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class MusicBrowserAdapter extends BaseAdapter implements OptionView, Runnable {
    private final Object mDataLock = new Object();
    private final Object mDataLoaderLock = new Object();
    private OnOptionsClickedListener mOnItemOptionsClickedListener;
    private List<ExtendedFile> mItems;
    private ArrayList<ItemData> mData;
    private View mParent = null;
    private Thread mDataLoader;


    public MusicBrowserAdapter() {
        mItems = new ArrayList<>(0);
    }

    public void setData(List<ExtendedFile> files) {
        synchronized (mDataLoaderLock) {
            if (mDataLoader != null) {
                mDataLoader.interrupt();
            }
        }
        if (files == null) files = new ArrayList<>(0);
        Collections.sort(files, new Comparator<ExtendedFile>() {
            @Override
            public int compare(ExtendedFile lhs, ExtendedFile rhs) {
                FileType lhsType = lhs.getType();
                FileType rhsType = rhs.getType();

                if (lhsType == rhsType) {
                    return lhs.getName().compareToIgnoreCase(rhs.getName());
                } else if (lhsType == FileType.DIRECTORY) {
                    return -1;
                } else if (rhsType == FileType.DIRECTORY) {
                    return +1;
                } else if (lhsType == FileType.AUDIO) {
                    return -1;
                } else {    //all other possibilities are already cut done
                    return +1;
                }
            }
        });
        mData = new ArrayList<>(files.size());
        for (int i = 0; i < files.size(); i++) {
            mData.add(i, new ItemData(files.get(i)));
        }
        mItems = files;
        notifyDataSetChanged();

        synchronized (mDataLoaderLock) {
            mDataLoader = new Thread(this);
            mDataLoader.start();
        }
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public ExtendedFile getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //FIXME: recycling of views
        mParent = parent;
        MusicBrowserListItem musicBrowserListItem = MusicBrowserListItem.create(parent, mItems.get(position));
        synchronized (mDataLock) {
            ItemData itemData = mData.get(position);
            musicBrowserListItem.setTitle(itemData.getFile().getName());
            musicBrowserListItem.setSecondaryData(itemData.getSecondaryData());
        }
        convertView = musicBrowserListItem;
        ((OptionView)convertView).setOnItemOptionsClickedListener(mOnItemOptionsClickedListener);
        return convertView;
    }

    @Override
    public void setOnItemOptionsClickedListener(OnOptionsClickedListener listener) {
        mOnItemOptionsClickedListener = listener;
        notifyDataSetChanged();
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_LOWEST);

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
            boolean dataChanged = false;
            String secData = "";
            ExtendedFile threadSafeFile;
            int duration = 0;
            synchronized (mDataLock) {
                ExtendedFile file = mData.get(i).getFile();
                threadSafeFile = new ExtendedFile(file.getAbsolutePath());
                if (file.hasDurationCached()) {
                    duration = file.getDuration();
                }
            }
            switch (FileType.getType(threadSafeFile)) {
                case AUDIO:
                    if (duration == 0) {
                        duration = threadSafeFile.getDuration();
                    }
                    if (duration != 0) {
                        secData = String.format("%d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes(duration),
                                TimeUnit.MILLISECONDS.toSeconds(duration) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
                        );
                    } else {
                        secData = "Error reading length.";
                    }
                    dataChanged = true;
                    break;
                case DIRECTORY:
                    List<ExtendedFile> files = threadSafeFile.listAudioFiles(false);
                    secData = files.size() + " audio files found.";
                    dataChanged = true;
                    break;
            }
            if (dataChanged) {
                synchronized (mDataLock) {
                    if (mData != null && mData.size() > i) {
                        mData.get(i).setSecondaryData(secData);
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
    }
}
