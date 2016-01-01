package com.reallynourl.nourl.fmpfoldermusicplayer.utility.music;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

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
public class MusicIntentReceiver extends BroadcastReceiver{
    public static final String ACTION_MUSIC_CONTROL = "music_control";
    public static final String ACTION_CLOSE = "close";

    public static final String EXTRA_MUSIC_CONTROL_KEY = "command";
    public static final int EXTRA_VALUE_PLAY = 2000;
    public static final int EXTRA_VALUE_NEXT = 2001;
    public static final int EXTRA_VALUE_PREVIOUS = 2002;
    public static final int EXTRA_VALUE_PAUSE = 2003;

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case AudioManager.ACTION_AUDIO_BECOMING_NOISY:
                MediaManager.getInstance().pause();
                break;
            case ACTION_MUSIC_CONTROL:
                int command = intent.getIntExtra(EXTRA_MUSIC_CONTROL_KEY, -1);
                switch (command) {
                    case EXTRA_VALUE_PLAY:
                        MediaManager.getInstance().play();
                        break;
                    case EXTRA_VALUE_NEXT:
                        MediaManager.getInstance().next();
                        break;
                    case EXTRA_VALUE_PREVIOUS:
                        MediaManager.getInstance().previous();
                        break;
                    case EXTRA_VALUE_PAUSE:
                        MediaManager.getInstance().pause();
                        break;
                    default:
                        Toast.makeText(context,
                                "FMP: Got invalid control intent: " + command,
                                Toast.LENGTH_SHORT).show();
                }
                break;
            case ACTION_CLOSE:
                Toast.makeText(context,
                        "This is not implemented at this point.", Toast.LENGTH_SHORT).show();
                break;
            case Intent.ACTION_MEDIA_BUTTON:
                Toast.makeText(context, "Received ACTION_MEDIA_BUTTON", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
