package com.reallynourl.nourl.fmpfoldermusicplayer.ui.fragments.filebrowser.listadapter;

import android.content.Context;
import android.view.ViewGroup;

import com.reallynourl.nourl.fmpfoldermusicplayer.R;

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
public class DirectoryListItem extends MusicBrowserListItem {
    protected DirectoryListItem(Context context) {
        super(context);
    }

    protected static DirectoryListItem inflate(ViewGroup parent, File file) {
        return (DirectoryListItem) MusicBrowserListItem.inflate(parent, file, R.layout.listitem_directory);
    }


}