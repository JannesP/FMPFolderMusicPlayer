package com.reallynourl.nourl.fmpfoldermusicplayer.utility.music;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.view.KeyEvent;
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
    public static final String ACTION_PLAY = "com.reallynourl.nourl.fmpfoldermusicplayer.PLAY";
    public static final String ACTION_PAUSE = "com.reallynourl.nourl.fmpfoldermusicplayer.PAUSE";
    public static final String ACTION_NEXT = "com.reallynourl.nourl.fmpfoldermusicplayer.NEXT";
    public static final String ACTION_PREVIOUS = "com.reallynourl.nourl.fmpfoldermusicplayer.PREVIOUS";
    public static final String ACTION_CLOSE = "com.reallynourl.nourl.fmpfoldermusicplayer.CLOSE";

    public static final int INTENT_ID_PLAY = 2000;
    public static final int INTENT_ID_NEXT = 2001;
    public static final int INTENT_ID_PREVIOUS = 2002;
    public static final int INTENT_ID_PAUSE = 2003;

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case AudioManager.ACTION_AUDIO_BECOMING_NOISY:
                MediaManager.getInstance().pause();
                break;
            case ACTION_PLAY:
                MediaManager.getInstance().play();
                break;
            case ACTION_NEXT:
                MediaManager.getInstance().next();
                break;
            case ACTION_PREVIOUS:
                MediaManager.getInstance().previous();
                break;
            case ACTION_PAUSE:
                MediaManager.getInstance().pause();
                break;
            case ACTION_CLOSE:
                MediaManager.getInstance().stop();
                MediaManager.getInstance().onMainActivityClosed();
                break;
            case Intent.ACTION_MEDIA_BUTTON:
                KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(
                        Intent.EXTRA_KEY_EVENT);
                if (keyEvent == null || keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                    return;

                switch (keyEvent.getKeyCode()) {
                    case KeyEvent.KEYCODE_HEADSETHOOK:
                    case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                        if (MediaManager.getInstance().isPlaying()) {
                            MediaManager.getInstance().pause();
                        } else {
                            MediaManager.getInstance().play();
                        }
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PLAY:
                        MediaManager.getInstance().play();
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PAUSE:
                        MediaManager.getInstance().pause();
                        break;
                    case KeyEvent.KEYCODE_MEDIA_NEXT:
                        MediaManager.getInstance().next();
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                        MediaManager.getInstance().previous();
                        break;
                    default:
                        Toast.makeText(context,
                                "Got not implemented Media Key: " + keyEvent.getKeyCode(),
                                Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
