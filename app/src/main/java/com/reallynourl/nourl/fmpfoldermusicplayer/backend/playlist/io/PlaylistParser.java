package com.reallynourl.nourl.fmpfoldermusicplayer.backend.playlist.io;

import android.util.Log;

import com.reallynourl.nourl.fmpfoldermusicplayer.backend.playlist.Playlist;
import com.reallynourl.nourl.fmpfoldermusicplayer.utility.file.FileUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
public abstract class PlaylistParser {
    public static void saveToFile(Playlist playlist, PlaylistFileType type,
                                  File destination) throws IOException {
        PlaylistParser parser;
        switch (type) {
            case M3U:
            case M3U8:
                parser = new M3uPlaylistParser();
                break;
            default:
                Log.e("PLAYLIST_PARSER", "A known playlist extension: \""
                        + type.getExtension() + "\" was not handled while saveToFile.");
                return;
        }
        List<String> lines = parser.parsePlaylist(playlist);
        if (!destination.createNewFile()) {
            FileUtil.clearFile(destination);
        }
        if (!FileUtil.getExtension(destination).equals(type.getExtension())) {
            if (!destination.renameTo(
                    new File(destination.getAbsolutePath() + "." + type.getExtension()))) {
                Log.i("PLAYLIST_PARSER", "There was an error renaming the playlist file \""
                        + destination.getAbsolutePath() + "\" to \"" + destination.getAbsolutePath()
                        + "." + type.getExtension() + "\".");
            }
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter(destination));
        try {
            String lineSeparator = System.getProperty("line.separator");
            for (String line : lines) {
                bw.write(line);
                bw.write(lineSeparator);
            }
            bw.close();
        } catch (IOException e) {
            bw.close();
            throw e;
        }

    }

    public static Playlist readFromFile(File destination) throws
            PlaylistFileFormatNotSupportedException,
            PlaylistFileFormatCorruptedException, IOException {
        Playlist result;

        String extension = FileUtil.getExtension(destination);
        PlaylistFileType type;
        try {
            type = PlaylistFileType.fromExtension(extension);
        } catch (PlaylistFileType.TypeNotFoundException ignored) {
            throw new PlaylistFileFormatNotSupportedException(extension);
        }
        PlaylistParser parser;
        switch (type) {
            case M3U:
            case M3U8:
                parser = new M3uPlaylistParser();
                break;
            default:
                Log.e("PLAYLIST_PARSER", "A known playlist extension: \""
                        + type.getExtension() + "\" was not handled while readFromFile.");
                return null;
        }

        List<String> fileLines = FileUtil.readAllLines(destination);
        result = parser.parseFile(fileLines);

        return result;
    }

    protected abstract List<String> parsePlaylist(Playlist playlist);

    protected abstract Playlist parseFile(List<String> fileLines);
}
