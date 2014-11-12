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

import java.util.ArrayList;

import m.m.com.m.model.Song;

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private int mSongPosition;
    private ArrayList<Song> mSongsList;

    private MediaPlayer mPlayer;
    private final IBinder musicBind = new MusicBinder();

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

    //play a song
    public void playSong(){
        //play
        mPlayer.reset();
        //get song
        Song playSong = mSongsList.get(mSongPosition);
        //get id
        long currSong = playSong.getID();
        //set uri
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);
        //set the data source
        try{
            mPlayer.setDataSource(getApplicationContext(), trackUri);
        } catch(Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        mPlayer.prepareAsync();
    }

    //play a song
    public void pauseSong(){
        if(mPlayer!= null && mPlayer.isPlaying()) {
            mPlayer.pause();
        }

    }

    //set the song
    public void setSong(int songIndex){
        mSongPosition = songIndex;
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

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        if(mediaPlayer != null) {
            mediaPlayer.start();
        }
    }
}
