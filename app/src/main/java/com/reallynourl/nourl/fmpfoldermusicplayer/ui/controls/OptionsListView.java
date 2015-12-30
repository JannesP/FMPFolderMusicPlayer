package com.reallynourl.nourl.fmpfoldermusicplayer.ui.controls;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

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
public class OptionsListView extends ListView implements OptionView {
    private OnOptionsClickedListener mOnItemOptionsClickedListener = null;

    public OptionsListView(Context context) {
        super(context);
    }

    public OptionsListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OptionsListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnItemOptionsClickedListener(OnOptionsClickedListener listener) {
        mOnItemOptionsClickedListener = listener;
        ListAdapter adapter = getAdapter();
        if (adapter != null && adapter instanceof OptionView) {
            ((OptionView)adapter).setOnItemOptionsClickedListener(mOnItemOptionsClickedListener);
        }
    }
}
