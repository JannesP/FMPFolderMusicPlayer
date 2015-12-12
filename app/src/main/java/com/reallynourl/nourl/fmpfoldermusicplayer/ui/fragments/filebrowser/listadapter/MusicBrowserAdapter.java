package com.reallynourl.nourl.fmpfoldermusicplayer.ui.fragments.filebrowser.listadapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.reallynourl.nourl.fmpfoldermusicplayer.utility.file.FileType;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.file.FileUtil;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

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
public class MusicBrowserAdapter extends BaseAdapter{
    private File[] mItems;

    public MusicBrowserAdapter() {
        mItems = new File[0];
    }

    public void setData(File[] files) {
        if (files == null) files = new File[0];
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                FileType lhsType = FileType.getType(lhs);
                FileType rhsType = FileType.getType(rhs);

                if (lhsType == rhsType) {
                    return lhs.getName().compareToIgnoreCase(rhs.getName());
                } else if (lhsType == FileType.DIRECTORY) {
                    return -1;
                } else if (rhsType == FileType.DIRECTORY) {
                    return +1;
                } else if (lhsType == FileType.AUDIO) {
                    return -1;
                } else {    //all other possibilities are already cut done
                    return +1;
                }
            }
        });
        mItems = files;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItems.length;
    }

    @Override
    public File getItem(int position) {
        return mItems[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return MusicBrowserListItem.create(parent, mItems[position]);   //TODO: Fix reusing of already created convertViews.
    }
}
