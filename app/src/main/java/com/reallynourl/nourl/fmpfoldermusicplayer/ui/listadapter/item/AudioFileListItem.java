package com.reallynourl.nourl.fmpfoldermusicplayer.ui.listadapter.item;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.reallynourl.nourl.fmpfoldermusicplayer.R;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.file.ExtendedFile;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.file.FileType;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.file.FileUtil;

import java.io.File;

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
public class AudioFileListItem extends MusicBrowserListItem implements View.OnClickListener {
    private OnOptionsClickedListener mOnItemOptionsClickedListener = null;
    
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
    public void setFile(ExtendedFile file) {
        TextView tv = (TextView) getRootView().findViewById(R.id.textViewListItemTitle);
        tv.setText(file.getNameWithoutExtension());
        tv = (TextView) getRootView().findViewById(R.id.textViewListItemMoreInfo);
        tv.setText(R.string.loading_3_dots);
        super.setFile(file);
    }



    @Override
    public void setOnItemOptionsClickedListener(OnOptionsClickedListener listener) {
        mOnItemOptionsClickedListener = listener;
        ImageView iv = (ImageView) findViewById(R.id.imageViewOptionsIcon);
        if (listener == null) {
            iv.setOnClickListener(null);
        } else {
            iv.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (mOnItemOptionsClickedListener != null) {
            mOnItemOptionsClickedListener.onItemOptionsClicked(this, v);
        }
    }
}
