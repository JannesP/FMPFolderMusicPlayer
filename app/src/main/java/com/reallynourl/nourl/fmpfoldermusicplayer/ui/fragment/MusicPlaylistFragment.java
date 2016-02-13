package com.reallynourl.nourl.fmpfoldermusicplayer.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.reallynourl.nourl.fmpfoldermusicplayer.R;
import com.reallynourl.nourl.fmpfoldermusicplayer.backend.playlist.PlaylistItem;
import com.reallynourl.nourl.fmpfoldermusicplayer.ui.control.OptionView;
import com.reallynourl.nourl.fmpfoldermusicplayer.ui.control.OptionsListView;
import com.reallynourl.nourl.fmpfoldermusicplayer.ui.listadapter.item.AudioFileListItem;
import com.reallynourl.nourl.fmpfoldermusicplayer.ui.listadapter.MusicPlaylistAdapter;
import com.reallynourl.nourl.fmpfoldermusicplayer.ui.listadapter.item.PlaylistListItem;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.Util;
import com.reallynourl.nourl.fmpfoldermusicplayer.backend.MediaManager;

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
public class MusicPlaylistFragment extends Fragment implements AdapterView.OnItemClickListener, OptionView.OnOptionsClickedListener, PopupMenu.OnMenuItemClickListener {
    private static final String LIST_NAME = "lv_playlist";
    private View mRootView;
    private PlaylistItem mCurrentPopupItem;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate layout for fragment
        mRootView = inflater.inflate(R.layout.fragment_music_playlist, container, false);
        OptionsListView lv = (OptionsListView) mRootView.findViewById(R.id.listViewPlaylist);
        int mAccentColor = Util.getAccentColor(getActivity());
        MusicPlaylistAdapter mpa = new MusicPlaylistAdapter(mAccentColor);
        lv.setAdapter(mpa);
        lv.setOnItemClickListener(this);
        lv.setOnItemOptionsClickedListener(this);
        if (savedInstanceState != null) {
            Util.loadScrollPositionFromBundle(savedInstanceState, lv, LIST_NAME);
        }
        return mRootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MediaManager.getInstance().playPlaylistItem(position);
    }

    @Override
    public void onItemOptionsClicked(View view, View anchor) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), anchor);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.playlistitem);
        mCurrentPopupItem = ((PlaylistListItem)view).getPlaylistItem();
        popupMenu.show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        OptionsListView lv = (OptionsListView) mRootView.findViewById(R.id.listViewPlaylist);
        Util.saveScrollPositionToBundle(outState, lv, LIST_NAME);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        boolean handled = true;
        switch (item.getItemId()) {
            case R.id.item_remove:
                mCurrentPopupItem.removeFromPlaylist();
                break;
            case R.id.item_play:
                MediaManager.getInstance().playPlaylistItem(mCurrentPopupItem);
                break;
            case R.id.item_play_next:
                Snackbar.make(mRootView, "Not implemented yet.", Snackbar.LENGTH_LONG).show();
                break;
            default:
                handled = false;

        }
        mCurrentPopupItem = null;
        return handled;
    }
}
