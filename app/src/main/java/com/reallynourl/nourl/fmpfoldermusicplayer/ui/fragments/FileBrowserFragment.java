package com.reallynourl.nourl.fmpfoldermusicplayer.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.reallynourl.nourl.fmpfoldermusicplayer.R;

/**
 * Created by Jannes Peters on 11.12.2015.
 */
public class FileBrowserFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate layout for fragment
        return inflater.inflate(R.layout.file_browser, container, false);
    }
}
