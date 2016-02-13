package com.reallynourl.nourl.fmpfoldermusicplayer.ui.fragment;

import android.app.Fragment;

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
public interface IMainContent {

    /**
     * Gets a unique name for the fragment contained by the class.
     * @return a unique name
     */
    String getName();

    /**
     * Called when the back button is pressed and the Fragment is loaded as main content.
     * @return if the back press was handled by the fragment.
     */
    boolean onBackPressed();

    /**
     * Gets the Fragment which should be displayed when accessed.
     * @return a Fragment contained by the class
     */
    Fragment getFragment();

}
