package com.reallynourl.nourl.fmpfoldermusicplayer.backend.playlist;

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
public enum RepeatMode {
    OFF(0), ALL(1), SINGLE(2);

    private int mId;
    RepeatMode(int val) {
        mId = val;
    }

    public static RepeatMode get(int id) {
        RepeatMode result = null;
        for (RepeatMode type : values()) {
            if (type.getValue() == id) {
                result = type;
                break;
            }
        }
        return result;
    }

    public int getValue() {
        return mId;
    }
}
