package com.reallynourl.nourl.fmpfoldermusicplayer.ui.fragments.filebrowser;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.reallynourl.nourl.fmpfoldermusicplayer.R;
import com.reallynourl.nourl.fmpfoldermusicplayer.ui.activities.MainActivity;
import com.reallynourl.nourl.fmpfoldermusicplayer.ui.fragments.filebrowser.listadapter.MusicBrowserAdapter;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.file.AudioFileFilter;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.file.FileType;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.file.FileUtil;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.music.MediaManager;

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

    public FileBrowserFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (FileUtil.isExternalStorageWritable() || FileUtil.isExternalStorageReadable()) {
            mStartPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);//getExternalStorageDirectory() for root
        }
        mCurrentPath = mStartPath;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate layout for fragment
        if (!mIsCreated) {
            mIsCreated = true;
            mRootView = inflater.inflate(R.layout.fragment_file_browser, container, false);
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

    /**
     * @return If the back press did anything.
     */
    public boolean onBackPressed() {
        File parent = getValidParent();
        if (parent != null) {
            changeDirectory(parent);
            return true;
        }
        return false;
    }

    private void changeDirectory(File directory) {
        mCurrentPath = directory;
        populateFileBrowser();
    }

    private File getValidParent() {
        File parent = mCurrentPath.getParentFile();
        if (parent != null) {
            if (parent.canRead() && parent.canWrite() == mCurrentPath.canWrite()) {
                return parent;
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
                ListView lv = getListView();
                if (lv != null) lv.setAdapter(new MusicBrowserAdapter());
            }

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            boolean allowHidden = prefs.getBoolean(getString(R.string.pref_file_browser_show_hidden), false);
            boolean allowNonAudio = prefs.getBoolean(getString(R.string.pref_file_browser_show_non_audio), false);

            File[] files = mCurrentPath.listFiles(new AudioFileFilter(allowHidden, allowNonAudio));
            getBrowserAdapter().setData(files);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MusicBrowserAdapter adapter = (MusicBrowserAdapter) parent.getAdapter();

        File file = adapter.getItem(position);
        FileType ft = FileType.getType(file);
        switch (ft) {
            case DIRECTORY:
                changeDirectory(file);
                break;
            case FILE:
                Snackbar.make(parent, "You clicked on " + file.getName(), Snackbar.LENGTH_LONG).show();
                break;
            case AUDIO:
                MediaManager.getInstance().getPlaylist().clear();
                MediaManager.getInstance().getPlaylist().appendAll(mCurrentPath.listFiles(new AudioFileFilter(false, false)));
                MediaManager.getInstance().playPlayListItem(position);
                Bundle b = new Bundle(1);
                b.putString(MainActivity.FRAGMENT_EXTRA, "CONTROLS");
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtras(b);
                startActivity(intent);
                break;
            default:
                Snackbar.make(parent, "What did you do? You selected an non existing file!", Snackbar.LENGTH_LONG).show();
        }

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
