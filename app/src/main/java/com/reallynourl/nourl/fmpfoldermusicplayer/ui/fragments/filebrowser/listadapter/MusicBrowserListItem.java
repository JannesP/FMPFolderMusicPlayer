package com.reallynourl.nourl.fmpfoldermusicplayer.ui.fragments.filebrowser.listadapter;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.reallynourl.nourl.fmpfoldermusicplayer.utility.Util;

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
public abstract class MusicBrowserListItem extends RelativeLayout {
    private File mValidFile;

    public MusicBrowserListItem(Context context) {
        super(context);
    }

    public static MusicBrowserListItem create(ViewGroup parent, File validFile) {
        if (validFile.isDirectory()) {
            return DirectoryListItem.inflate(parent, validFile);
        } else if (validFile.isFile()) {
            if (Util.hasAudioExtension(validFile)) {
                return AudioFileListItem.inflate(parent, validFile);
            } else {
                return FileListItem.create(parent, validFile);
            }

        }
        return null;
    }

    protected static MusicBrowserListItem inflate(ViewGroup parent, File validFile, @LayoutRes int layout) {
        MusicBrowserListItem item = (MusicBrowserListItem) LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        item.setId(View.NO_ID);
        item.mValidFile = validFile;
        return item;
    }

}
