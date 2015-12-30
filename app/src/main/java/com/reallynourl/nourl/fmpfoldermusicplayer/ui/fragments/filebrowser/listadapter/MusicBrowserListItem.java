package com.reallynourl.nourl.fmpfoldermusicplayer.ui.fragments.filebrowser.listadapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.reallynourl.nourl.fmpfoldermusicplayer.ui.controls.OptionView;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.file.FileType;

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
public abstract class MusicBrowserListItem extends RelativeLayout implements OptionView {
    private File mFile = null;
    private final FileType mType;

    public MusicBrowserListItem(Context context, FileType type) {
        super(context);
        mType = type;
    }

    public MusicBrowserListItem(Context context, AttributeSet attrs, FileType type) {
        super(context, attrs);
        mType = type;
    }

    public MusicBrowserListItem(Context context, AttributeSet attrs, int defStyleAttr, FileType type) {
        super(context, attrs, defStyleAttr);
        mType = type;
    }

    public File getFile() {
        return mFile;
    }

    public void setFile(File file) {
        mFile = file;
    }

    public static MusicBrowserListItem create(ViewGroup parent, File validFile) {
        MusicBrowserListItem item = null;
        switch (FileType.getType(validFile)) {
            case DIRECTORY:
                item = DirectoryListItem.inflate(parent);
                break;
            case AUDIO:
                item = AudioFileListItem.inflate(parent);
                break;
            case FILE:
                item = FileListItem.inflate(parent);
                break;
            default:
                Log.i("File Browser", "File produced type error.");
        }
        if (item != null) item.setFile(validFile);
        return item;
    }

    public FileType getType() {
        return mType;
    }

    public boolean isType(File file) {
        return mType.equals(FileType.getType(file));
    }

    public boolean isType(FileType type) {
        return mType.equals(type);
    }

    protected static MusicBrowserListItem inflate(ViewGroup parent, @LayoutRes int layout) {
        MusicBrowserListItem item = (MusicBrowserListItem) LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return item;
    }

    @Override
    public void setOnItemOptionsClickedListener(OnOptionsClickedListener listener) { }
}
