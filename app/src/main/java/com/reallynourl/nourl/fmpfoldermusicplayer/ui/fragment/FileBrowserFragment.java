package com.reallynourl.nourl.fmpfoldermusicplayer.ui.fragment;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.reallynourl.nourl.fmpfoldermusicplayer.R;
import com.reallynourl.nourl.fmpfoldermusicplayer.backend.MediaManager;
import com.reallynourl.nourl.fmpfoldermusicplayer.ui.activity.MainActivity;
import com.reallynourl.nourl.fmpfoldermusicplayer.ui.control.OptionView;
import com.reallynourl.nourl.fmpfoldermusicplayer.ui.control.OptionsListView;
import com.reallynourl.nourl.fmpfoldermusicplayer.ui.listadapter.MusicBrowserAdapter;
import com.reallynourl.nourl.fmpfoldermusicplayer.ui.listadapter.item.MusicBrowserListItem;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.Util;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.file.AudioFileFilter;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.file.ExtendedFile;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.file.FileType;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.file.FileUtil;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
public class FileBrowserFragment extends Fragment implements IMainContent,
        AdapterView.OnItemClickListener,
        OptionView.OnOptionsClickedListener, PopupMenu.OnMenuItemClickListener {
    public static final String NAME = "file_browser";
    private static final String TAG = "FileBrowserFragment";
    private static final String LIST_VIEW_NAME = "fb_list_view";

    private boolean mIsCreated = false;

    private View mRootView;
    private MusicBrowserListItem mCurrentMenuItem;
    private ExtendedFile mCurrentPath;

    public FileBrowserFragment() {}

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Fragment getFragment() {
        return this;
    }

    @Override
    public boolean isCreated() {
        return mIsCreated;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExtendedFile startPath = null;
        if (FileUtil.isExternalStorageWritable() || FileUtil.isExternalStorageReadable()) {
            String storedPath = PreferenceManager.getDefaultSharedPreferences(
                    getActivity()).getString(getString(R.string.pref_last_file_browser_path), "");
            if (!storedPath.equals("")) {
                startPath = new ExtendedFile(storedPath);
                if (!startPath.exists() || !startPath.isDirectory()) {
                    startPath = null;
                }
            }
            if (startPath == null) {
                //getExternalStorageDirectory() for root
                startPath = new ExtendedFile(Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
                        .getAbsolutePath());
            }
        } else {
            Toast.makeText(getActivity(),
                    "Please allow this app storage access before using.", Toast.LENGTH_LONG).show();
        }
        mCurrentPath = startPath;
        mIsCreated = true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //inflate layout for fragment
        if (mRootView == null && mCurrentPath != null) {
            mIsCreated = true;
            mRootView = inflater.inflate(R.layout.fragment_file_browser, container, false);
        }

        populateFileBrowser();
        if (savedInstanceState != null) {
            OptionsListView lv = (OptionsListView) mRootView.findViewById(R.id.listViewBrowser);
            if (lv != null) {
                Util.loadScrollPositionFromBundle(savedInstanceState, lv, LIST_VIEW_NAME);
            }
        }

        return mRootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        OptionsListView lv = (OptionsListView) mRootView.findViewById(R.id.listViewBrowser);
        Util.saveScrollPositionToBundle(outState, lv, LIST_VIEW_NAME);
    }

    @Override
    public void onResume() {
        addListeners();
        super.onResume();
    }

    @Override
    public void onPause() {
        removeListeners();
        SharedPreferences.Editor edit =
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        edit.putString(getString(R.string.pref_last_file_browser_path),
                mCurrentPath.getAbsolutePath());
        edit.apply();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mIsCreated = false;
        mRootView = null;
        super.onDestroy();
    }

    private void addListeners() {
        OptionsListView lv = getListView();
        lv.setOnItemClickListener(this);
        lv.setOnItemOptionsClickedListener(this);
    }

    private void removeListeners() {
        OptionsListView lv = (OptionsListView) mRootView.findViewById(R.id.listViewBrowser);
        lv.setOnItemClickListener(null);
        lv.setOnItemOptionsClickedListener(null);
    }

    @Override
    public boolean onBackPressed() {
        ExtendedFile parent = getValidParent();
        if (parent != null) {
            changeDirectory(parent);
            return true;
        }
        return false;
    }

    private void changeDirectory(ExtendedFile directory) {
        mCurrentPath = directory;
        populateFileBrowser();
    }

    private ExtendedFile getValidParent() {
        File parent = mCurrentPath.getParentFile();
        if (parent != null) {
            if (parent.canRead() && parent.canWrite() == mCurrentPath.canWrite()) {
                return new ExtendedFile(parent.getAbsolutePath());
            }
        }
        return null;
    }

    private void populateFileBrowser() {
        if (!mCurrentPath.exists()) {
            Snackbar.make(mRootView, "Couldn't get the storage folder.", Snackbar.LENGTH_LONG).show();
        } else if (!mCurrentPath.isDirectory()) {
            Snackbar.make(mRootView, "File root is no directory, what did you do?!?", Snackbar.LENGTH_LONG).show();
        } else if (!mCurrentPath.canRead()) {
            Snackbar.make(mRootView, "Can't read internal storage.", Snackbar.LENGTH_LONG).show();
        } else if (!mCurrentPath.canWrite()) {
            Snackbar.make(mRootView, "Can't write to storage, this might disable some functionality!", Snackbar.LENGTH_LONG).show();
        } else {


            if (getBrowserAdapter() == null) {
                OptionsListView lv = getListView();
                lv.setAdapter(new MusicBrowserAdapter());
                lv.setOnItemOptionsClickedListener(this);
            }

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            boolean allowHidden = prefs.getBoolean(getString(R.string.pref_file_browser_show_hidden), false);
            boolean allowNonAudio = prefs.getBoolean(getString(R.string.pref_file_browser_show_non_audio), false);

            List<ExtendedFile> files = mCurrentPath.listExtendedFiles(
                    new AudioFileFilter(allowHidden, allowNonAudio, true));
            getBrowserAdapter().setData(files);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MusicBrowserAdapter adapter = (MusicBrowserAdapter) parent.getAdapter();

        ExtendedFile file = adapter.getItem(position);
        FileType ft = FileType.getType(file);
        switch (ft) {
            case DIRECTORY:
                changeDirectory(file);
                break;
            case FILE:
                Snackbar.make(parent,
                        "You clicked on " + file.getName(), Snackbar.LENGTH_LONG).show();
                break;
            case AUDIO:
                MediaManager.getInstance().getPlaylist().clear();
                File selectedFile = getBrowserAdapter().getItem(position);
                if (selectedFile != null) {
                    List<ExtendedFile> files =
                            mCurrentPath.listExtendedFiles(
                                    new AudioFileFilter(false, false, false));
                    Collections.sort(files);
                    position = files.indexOf(getBrowserAdapter().getItem(position));
                    MediaManager.getInstance().getPlaylist().appendAll(files);
                    MediaManager.getInstance().playPlaylistItem(position);
                    MainActivity.selectFragment(getActivity().getApplicationContext(),
                            MusicPlayingFragment.NAME);
                }
                break;
            default:
                Snackbar.make(parent, "What did you do? You selected an non existing file!",
                        Snackbar.LENGTH_LONG).show();
        }

    }

    private OptionsListView getListView() {
        return (OptionsListView) mRootView.findViewById(R.id.listViewBrowser);
    }

    private MusicBrowserAdapter getBrowserAdapter() {
        return (MusicBrowserAdapter) getListView().getAdapter();
    }

    @Override
    public void onItemOptionsClicked(View view, View anchor) {
        mCurrentMenuItem = (MusicBrowserListItem) view;
        PopupMenu popupMenu = new PopupMenu(getActivity(), anchor);
        popupMenu.setOnMenuItemClickListener(this);
        switch (mCurrentMenuItem.getType()) {
            case AUDIO:
                popupMenu.inflate(R.menu.file_browser_audio_menu);
                break;
            case DIRECTORY:
                popupMenu.inflate(R.menu.file_browser_directory_menu);
                break;
        }
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        boolean handled = false;
        switch (mCurrentMenuItem.getType()) {
            case AUDIO:
                switch (item.getItemId()) {
                    case R.id.item_append:
                        MediaManager.getInstance().getPlaylist().append(mCurrentMenuItem.getFile());
                        break;
                    case R.id.item_append_next:
                        MediaManager.getInstance().getPlaylist().appendNext(mCurrentMenuItem.getFile());
                        break;
                    case R.id.item_append_next_play:
                        MediaManager.getInstance().addPlaylistNextAndPlay(mCurrentMenuItem.getFile());
                        break;
                    default:
                        Log.e(TAG, "Action for item is missing!");
                        Snackbar.make(mRootView, "Action for item is missing!", Snackbar.LENGTH_LONG).show();
                }
                handled = true;
                break;
            case DIRECTORY:
                switch (item.getItemId()) {
                    case R.id.item_append_all:
                        ExtendedFile dir = mCurrentMenuItem.getFile();
                        boolean playHidden = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(getString(R.string.pref_file_browser_show_hidden), false);
                        List<ExtendedFile> files = dir.listAudioFiles(playHidden);
                        if (files != null && files.size() > 0) {
                            MediaManager.getInstance().getPlaylist().appendAll(files);
                            Snackbar.make(mRootView, "Added " + files.size() + " items to the queue.", Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(mRootView, "The folder doesn't contain audio files.", Snackbar.LENGTH_LONG).show();
                        }
                        break;
                }
                handled = true;
                break;
        }
        return handled;
    }
}
