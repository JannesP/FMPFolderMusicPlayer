package com.reallynourl.nourl.fmpfoldermusicplayer.backend.playlist;

import com.reallynourl.nourl.fmpfoldermusicplayer.utility.file.ExtendedFile;

import java.io.File;
import java.net.URI;

/**
 * Copyright (C) 2016  Jannes Peters
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
public class PlaylistItem extends ExtendedFile {
    private boolean mIsPlayed = false;
    private Playlist mPlaylist = null;

    public PlaylistItem(File dir, String name) {
        super(dir, name);
    }

    public PlaylistItem(String path) {
        super(path);
    }

    public PlaylistItem(String dirPath, String name) {
        super(dirPath, name);
    }

    public PlaylistItem(URI uri) {
        super(uri);
    }

    public PlaylistItem(File file) {
        super(file.getAbsolutePath());
    }

    public Playlist getPlaylist() {
        return mPlaylist;
    }

    public void setPlaylist(Playlist playlist) {
        this.mPlaylist = playlist;
    }

    public boolean isPlayed() {
        return mIsPlayed;
    }

    public void setPlayed(boolean played) {
        mIsPlayed = played;
    }

    public void removeFromPlaylist() {
        if (mPlaylist != null) {
            mPlaylist.remove(this);
        }
    }
}
