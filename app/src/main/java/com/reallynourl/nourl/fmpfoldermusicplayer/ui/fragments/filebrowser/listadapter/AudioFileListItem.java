package com.reallynourl.nourl.fmpfoldermusicplayer.ui.fragments.filebrowser.listadapter;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
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
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();    //TODO: find threaded solution like caching it in some way.
        mmr.setDataSource(getContext(), Uri.fromFile(file));
        String durationString = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int duration = durationString == null ? 0 : Integer.parseInt(durationString);
        String time = String.format("%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        );
        tv = (TextView) getRootView().findViewById(R.id.textViewListItemMoreInfo);
        tv.setText(time + " and I know everything is laggy!!!");
    }
}
