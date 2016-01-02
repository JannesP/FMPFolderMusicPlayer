package com.reallynourl.nourl.fmpfoldermusicplayer.ui.listadapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.reallynourl.nourl.fmpfoldermusicplayer.ui.control.OptionView;
import com.reallynourl.nourl.fmpfoldermusicplayer.ui.listadapter.item.ItemData;
import com.reallynourl.nourl.fmpfoldermusicplayer.ui.listadapter.item.MusicBrowserListItem;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.file.AudioFileUtil;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.file.FileType;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.file.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
    private File[] mItems;
    private ArrayList<ItemData> mData;
    private View mParent = null;
    private Thread mDataLoader;


    public MusicBrowserAdapter() {
        mItems = new File[0];
    }

    public void setData(File[] files) {
        synchronized (mDataLoaderLock) {
            if (mDataLoader != null) {
                mDataLoader.interrupt();
            }
        }
        if (files == null) files = new File[0];
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                FileType lhsType = FileType.getType(lhs);
                FileType rhsType = FileType.getType(rhs);

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
        mData = new ArrayList<>(files.length);
        for (int i = 0; i < files.length; i++) {
            mData.add(i, new ItemData(files[i]));
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
        return mItems.length;
    }

    @Override
    public File getItem(int position) {
        return mItems[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //FIXME: recycling of views
        mParent = parent;
        MusicBrowserListItem musicBrowserListItem = MusicBrowserListItem.create(parent, mItems[position]);
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
            File threadSafeFile;
            synchronized (mDataLock) {
                File file = mData.get(i).getFile();
                threadSafeFile = new File(file.getAbsolutePath());
            }
            switch (FileType.getType(threadSafeFile)) {
                case AUDIO:
                    int duration;
                    duration = AudioFileUtil.getDuration(threadSafeFile);
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
                    File[] files = FileUtil.listAudioFiles(threadSafeFile, false);
                    secData = files.length + " audio files found.";
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
