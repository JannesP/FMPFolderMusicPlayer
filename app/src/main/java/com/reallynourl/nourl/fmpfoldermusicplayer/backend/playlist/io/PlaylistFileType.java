package com.reallynourl.nourl.fmpfoldermusicplayer.backend.playlist.io;

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
public enum PlaylistFileType {
    M3U("m3u");

    private final String mExtension;

    PlaylistFileType(String extension) {
        this.mExtension = extension;
    }

    public String getExtension() {
        return mExtension;
    }

    public static PlaylistFileType fromExtension(String extension) throws TypeNotFoundException {
        for (PlaylistFileType type : values()) {
            if (type.getExtension().equals(extension)) {
                return type;
            }
        }
        throw new TypeNotFoundException(extension);
    }

    public static class TypeNotFoundException extends Throwable {
        public TypeNotFoundException(String extension) {
            super("The extension \"" + extension + "\" is not supported.");
        }
    }
}
