/*
* Copyright 2014 Pedro Paulo de Amorim
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package m.m.com.m.service;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import m.m.com.m.model.Song;
import m.m.com.m.utils.DebugUtil;

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private int mSongPosition;
    private ArrayList<Song> mSongsList;

    private MediaPlayer mPlayer;
    private final IBinder musicBind = new MusicBinder();
    private ProgressBar mProgressBarPlayer;
    private TextView mTimeText;

    private int current = 0;
    private boolean running = true;
    private boolean canBePlayed = false;
    private int duration = 0;
    private int mRatioRefreshProgress = 50;
    private int mRatioRefreshTime = 1000;

    public void onCreate() {
        super.onCreate();
        //initialize position
        mSongPosition =0;
        //create mPlayer
        mPlayer = new MediaPlayer();
        //initialize
        initMusicPlayer();
    }

    public void initMusicPlayer() {
        //set mPlayer properties
        mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //set listeners
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
    }

    public void setListSongs(ArrayList<Song> theSongs) {
        mSongsList =theSongs;
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public void setSeekBar(ProgressBar progressBar) {
        mProgressBarPlayer = progressBar;
    }

    public void setTimeSong(final TextView textView) {
        mTimeText = textView;
    }

    //play a song
    public void playSong(){

        if(mPlayer != null) {

            if (canBePlayed) {
                mPlayer.start();
                updateProgressBar();
                return;
            }

            canBePlayed = false;
            mPlayer.reset();
            Uri trackUri = ContentUris.withAppendedId(
                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    mSongsList.get(mSongPosition).getID());
            //set the data source
            try {
                mPlayer.setDataSource(getApplicationContext(), trackUri);
            } catch (Exception e) {
                Log.e("MUSIC SERVICE", "Error setting data source", e);
            }
            mPlayer.prepareAsync();
        }
    }

    //play a song
    public void pauseSong(ProgressBar progressBar){
        if(mPlayer!= null && mPlayer.isPlaying()) {
            progressBar.setMax(mPlayer.getDuration());
            progressBar.setProgress(mPlayer.getCurrentPosition());
            mPlayer.pause();
        }

    }

    //set the song
    public void setSong(int songIndex){
        if(mSongPosition != songIndex) {
            canBePlayed = false;
            mSongPosition = songIndex;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        mPlayer.stop();
        mPlayer.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if(mProgressBarPlayer != null) {
            mProgressBarPlayer.setProgress(0);
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        if(mediaPlayer != null && mProgressBarPlayer != null) {
            canBePlayed = true;
            mediaPlayer.start();
            mProgressBarPlayer.setProgress(0);
            duration = mPlayer.getDuration();
            mProgressBarPlayer.setMax(duration);
            updateProgressBar();
            updateTimeText();
        }
    }

    private void updateProgressBar() {
        if(mProgressBarPlayer != null) {
            mProgressBarPlayer.postDelayed(onEveryMilisecond, mRatioRefreshProgress);
        }
    }

    private void updateTimeText() {
        if(mProgressBarPlayer != null) {
            mProgressBarPlayer.postDelayed(onEverySecond, mRatioRefreshTime);
        }
    }

    private Runnable onEveryMilisecond = new Runnable() {
        @Override
        public void run(){
            if(running){
                if(mProgressBarPlayer != null) {
                    mProgressBarPlayer.setProgress(mPlayer.getCurrentPosition());
                }

                if(mPlayer.isPlaying() && mProgressBarPlayer != null) {
                    if(mPlayer.getCurrentPosition() < mPlayer.getDuration()) {
                        updateProgressBar();
                    } else {
                        mProgressBarPlayer.setProgress(0);
                    }
                }
            }
        }
    };

    private Runnable onEverySecond = new Runnable() {
        @Override
        public void run() {
            if(running){
                if(mPlayer.isPlaying() && mProgressBarPlayer != null) {
                    if(mPlayer.getCurrentPosition() < mPlayer.getDuration()) {
                        updateTime();
                    } else {
                        mTimeText.setText("00:00 / 00:00");
                    }
                }
            }
        }
    };

    private void updateTime(){
        do {
            if(mPlayer != null && canBePlayed) {
                current = mPlayer.getCurrentPosition();
                int dSeconds = (int) (duration / 1000) % 60;
                int dMinutes = (int) ((duration / (1000 * 60)) % 60);
                int dHours = (int) ((duration / (1000 * 60 * 60)) % 24);

                int cSeconds = (int) (current / 1000) % 60;
                int cMinutes = (int) ((current / (1000 * 60)) % 60);
                int cHours = (int) ((current / (1000 * 60 * 60)) % 24);

                if (mTimeText != null) {
                    if (dHours == 0) {
                        mTimeText.setText(String.format("%02d:%02d / %02d:%02d", cMinutes, cSeconds, dMinutes, dSeconds));
                    } else {
                        mTimeText.setText(String.format("%02d:%02d:%02d / %02d:%02d:%02d", cHours, cMinutes, cSeconds, dHours, dMinutes, dSeconds));
                    }
                }

                try {

                    if (mProgressBarPlayer.getProgress() >= 100) {
                        break;
                    }
                } catch (Exception e) {
                }
            }
        }while (mProgressBarPlayer.getProgress() <= 100 && canBePlayed);
    }

}
