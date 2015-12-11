package com.reallynourl.nourl.fmpfoldermusicplayer.ui.fragments.filebrowser;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.reallynourl.nourl.fmpfoldermusicplayer.R;
import com.reallynourl.nourl.fmpfoldermusicplayer.ui.fragments.filebrowser.listadapter.MusicBrowserAdapter;

import java.io.File;

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
public class FileBrowserFragment extends Fragment implements AdapterView.OnItemClickListener {
    private boolean mIsCreated = false;

    private View mRootView;
    private File mStartPath;
    private File mCurrentPath;

    public FileBrowserFragment() {
        mStartPath = getActivity().getFilesDir();
        mCurrentPath = mStartPath;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate layout for fragment
        if (!mIsCreated) {
            mIsCreated = true;
            mRootView = inflater.inflate(R.layout.file_browser, container, false);
        }
        return mRootView;
    }

    @Override
    public void onResume() {
        addListeners();
        populateFileBrowser();
        super.onResume();
    }

    @Override
    public void onPause() {
        removeListeners();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mIsCreated = false;
        mRootView = null;
        super.onDestroy();
    }

    private void addListeners() {
        ListView lv = (ListView) mRootView.findViewById(R.id.listViewBrowser);
        lv.setOnItemClickListener(this);
    }

    private void removeListeners() {
        ListView lv = (ListView) mRootView.findViewById(R.id.listViewBrowser);
        lv.setOnItemClickListener(null);
    }

    private void populateFileBrowser() {
        if (!mCurrentPath.exists()) {
            Snackbar.make(mRootView, "Couldn't get the storage folder.", Snackbar.LENGTH_LONG);
        } else if (!mCurrentPath.isDirectory()) {
            Snackbar.make(mRootView, "File root is no directory, what did you do?!?", Snackbar.LENGTH_LONG);
        } else if (!mCurrentPath.canRead()) {
            Snackbar.make(mRootView, "Can't read internal storage.", Snackbar.LENGTH_LONG);
        } else if (!mCurrentPath.canWrite()) {
            Snackbar.make(mRootView, "Can't write to storage, this might disable some functionality!", Snackbar.LENGTH_LONG);
        } else {


            if (getBrowserAdapter() == null) {
                ListView lv = getListView();
                if (lv != null) lv.setAdapter(new MusicBrowserAdapter(mCurrentPath));
            } else {
                getBrowserAdapter().setPath(mCurrentPath);
            }

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Nullable
    private ListView getListView() {
        return (ListView) mRootView.findViewById(R.id.listViewBrowser);
    }

    @Nullable
    private MusicBrowserAdapter getBrowserAdapter() {
        ListView lv = getListView();
        if (lv != null) {
            return (MusicBrowserAdapter) getListView().getAdapter();
        }
        return null;
    }
}