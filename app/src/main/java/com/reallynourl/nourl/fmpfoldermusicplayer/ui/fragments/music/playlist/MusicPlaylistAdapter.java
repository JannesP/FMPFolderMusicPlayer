package com.reallynourl.nourl.fmpfoldermusicplayer.ui.fragments.music.playlist;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.reallynourl.nourl.fmpfoldermusicplayer.R;
import com.reallynourl.nourl.fmpfoldermusicplayer.ui.fragments.filebrowser.listadapter.AudioFileListItem;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.music.MediaManager;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.music.Playlist;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.music.RepeatMode;

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
public class MusicPlaylistAdapter extends BaseAdapter implements Playlist.OnPlaylistChangedListener {

    private int mAccentColor;

    public MusicPlaylistAdapter(int accentColor) {
        this.mAccentColor = accentColor;
        MediaManager.getInstance().getPlaylist().addOnPlayListChangedListener(this);
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
        if (convertView == null) {
            convertView = AudioFileListItem.create(parent, MediaManager.getInstance().getPlaylist().getList().get(position));
        } else {
            AudioFileListItem audioFileListItem = (AudioFileListItem) convertView;
            audioFileListItem.setFile(MediaManager.getInstance().getPlaylist().getList().get(position));
            convertView = audioFileListItem;
        }
        if (MediaManager.getInstance().getPlaylist().getCurrentIndex() == position) {
            convertView.findViewById(R.id.textViewListItemTitle).setBackgroundColor(mAccentColor);
        } else {
            convertView.findViewById(R.id.textViewListItemTitle).setBackgroundColor(Color.TRANSPARENT);
        }
        return convertView;
    }

    @Override
    public void onPlaylistChanged(Playlist playlist) {
        notifyDataSetChanged();
    }
}
