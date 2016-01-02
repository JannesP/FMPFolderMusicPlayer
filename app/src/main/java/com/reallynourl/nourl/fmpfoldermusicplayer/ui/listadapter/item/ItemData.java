package com.reallynourl.nourl.fmpfoldermusicplayer.ui.listadapter.item;

import java.io.File;

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
public class ItemData {
    private final File mFile;
    private String mSecondaryData;

    public ItemData(File mFile) {
        this.mFile = mFile;
        this.mSecondaryData = "";
    }

    public File getFile() {
        return mFile;
    }

    public void setSecondaryData(String secondaryData) {
        this.mSecondaryData = secondaryData;
    }

    public String getSecondaryData() {
        return mSecondaryData;
    }
}
