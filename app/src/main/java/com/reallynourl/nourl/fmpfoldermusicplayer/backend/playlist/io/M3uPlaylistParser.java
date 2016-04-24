package com.reallynourl.nourl.fmpfoldermusicplayer.backend.playlist.io;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.reallynourl.nourl.fmpfoldermusicplayer.backend.playlist.Playlist;
import com.reallynourl.nourl.fmpfoldermusicplayer.backend.playlist.PlaylistItem;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.file.ExtendedFile;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.file.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
public class M3uPlaylistParser extends PlaylistParser {
    private static final char EXTENDED_MARKER = '#';

    @Override
    protected List<String> parsePlaylist(@NonNull Playlist playlist) {
        List<String> lines = new ArrayList<>(playlist.size());

        if (playlist.size() > 0) {
            List<PlaylistItem> files = playlist.getList();
            for (File file : files) {
                lines.add(file.getAbsolutePath());
            }
        } else {
            lines = null;
        }

        return lines;
    }

    @Override
    protected Playlist parseFile(@NonNull List<String> fileLines) {
        Playlist result = new Playlist();

        for (String line : fileLines) {
            if (line != null && line.length() > 0) {
                if (line.charAt(0) != EXTENDED_MARKER) {
                    ExtendedFile testFile = new ExtendedFile(line);
                    if (testFile.exists() && testFile.hasAudioExtension()) {
                        result.append(new PlaylistItem(testFile));
                    }
                }
            }
        }

        return result;
    }
}
