package com.reallynourl.nourl.fmpfoldermusicplayer.ui.fragments.filebrowser.listadapter;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import com.reallynourl.nourl.fmpfoldermusicplayer.R;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.file.FileType;

import java.io.File;
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
public class AudioFileListItem extends MusicBrowserListItem {
    
    public AudioFileListItem(Context context, AttributeSet attrs) {
        super(context, attrs, FileType.AUDIO);
    }

    public AudioFileListItem(Context context) {
        super(context, FileType.AUDIO);
    }

    public AudioFileListItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, FileType.AUDIO);
    }

    public static MusicBrowserListItem inflate(ViewGroup parent) {
        return MusicBrowserListItem.inflate(parent, R.layout.listitem_audio_file);
    }

    @Override
    public void setFile(File file) {
        TextView tv = (TextView) getRootView().findViewById(R.id.textViewListItemTitle);
        tv.setText(file.getName());
        tv = (TextView) getRootView().findViewById(R.id.textViewListItemMoreInfo);
        tv.setText("loading ...");
    }

    public void setTitle(String title) {
        TextView tv = (TextView) getRootView().findViewById(R.id.textViewListItemTitle);
        if (tv != null) {
            tv.setText(title);
        } else {
            Log.e("AudioFile Item", "The textview for the item with the title: " + title + " could not be found!");
        }
    }

    public void setSecondaryData(String data) {
        TextView tv = (TextView) getRootView().findViewById(R.id.textViewListItemMoreInfo);
        if (tv != null) {
            tv.setText(data);
        } else {
            Log.e("AudioFile Item", "The textview for the item with the data: " + data + " could not be found!");
        }
    }
}
