package com.reallynourl.nourl.fmpfoldermusicplayer.ui.control;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.widget.ImageButton;

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
public class ExtendedImageButton extends ImageButton {
    private final int disabledFilter = Color.GRAY;

    public ExtendedImageButton(Context context) {
        super(context);
    }

    public ExtendedImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtendedImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (!enabled) {
            setColorFilter(disabledFilter, PorterDuff.Mode.SRC_IN);
        } else {
            setColorFilter(Color.TRANSPARENT);
        }
        super.setEnabled(enabled);
    }
}
