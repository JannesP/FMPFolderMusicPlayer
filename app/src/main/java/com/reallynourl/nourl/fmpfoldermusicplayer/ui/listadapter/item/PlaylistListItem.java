package com.reallynourl.nourl.fmpfoldermusicplayer.ui.listadapter.item;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.reallynourl.nourl.fmpfoldermusicplayer.R;
import com.reallynourl.nourl.fmpfoldermusicplayer.backend.playlist.PlaylistItem;
import com.reallynourl.nourl.fmpfoldermusicplayer.ui.control.OptionView;

/**
 * Copyright (C) 2016  Jannes Peters
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
public class PlaylistListItem extends RelativeLayout implements OptionView, View.OnClickListener {
    private OnOptionsClickedListener mOnItemOptionsClickedListener = null;
    private PlaylistItem mPlaylistItem;

    public PlaylistListItem(Context context) {
        super(context);
    }

    public PlaylistListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PlaylistListItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTitle(String title) {
        TextView tv = (TextView) getRootView().findViewById(R.id.textViewListItemTitle);
        if (tv != null) {
            tv.setText(title);
        }
    }

    public void setSecondaryData(String data) {
        TextView tv = (TextView) getRootView().findViewById(R.id.textViewListItemMoreInfo);
        if (tv != null) {
            tv.setText(data);
        }
    }

    public static PlaylistListItem inflate(ViewGroup parent, PlaylistItem playlistItem) {
        PlaylistListItem playlistListItem = (PlaylistListItem) LayoutInflater
                .from(parent.getContext()).inflate(R.layout.listitem_playlist, parent, false);
        playlistListItem.setPlaylistItem(playlistItem);
        return playlistListItem;
    }

    public PlaylistItem getPlaylistItem() {
        return mPlaylistItem;
    }

    public void setPlaylistItem(PlaylistItem playlistItem) {
        this.mPlaylistItem = playlistItem;
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
