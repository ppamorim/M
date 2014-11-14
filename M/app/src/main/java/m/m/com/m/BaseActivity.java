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

package m.m.com.m;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import m.m.com.m.adapter.SongAdapter;
import m.m.com.m.model.Song;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import m.m.com.m.notification.SongController;
import m.m.com.m.service.internal.MediaService;
import m.m.com.m.service.internal.MusicService;

public class BaseActivity extends Activity {

    //Verify if service is running
    private boolean mMusicBound = false;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mRecyclerLayoutManager;
    private SongAdapter mSongAdapter;

    private ArrayList<Song> mSongList = new ArrayList<Song>();

    private MusicService mMusicService;
    private Intent mPlayIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_list);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mRecyclerLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mRecyclerLayoutManager);

        getSongList();

        mSongAdapter = new SongAdapter(this, mSongList, onItemClick, getScreenSize());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mSongAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mPlayIntent ==null){
            mPlayIntent = new Intent(this, MusicService.class);
            bindService(mPlayIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(mPlayIntent);
        }
    }

    @Override
    protected void onDestroy() {
        stopService(mPlayIntent);
        mMusicService =null;
        super.onDestroy();
    }

    private SongAdapter.OnItemClick onItemClick = new SongAdapter.OnItemClick() {
        @Override
        public void onPlaySong(int position, SeekBar seekBar, TextView time) {
            mMusicService.setSeekBar(seekBar);
            mMusicService.setTimeSong(time);
            mMusicService.setSong(position);
            mMusicService.playSong();
            new SongController(getBaseContext());
        }

        @Override
        public void onPauseSong(SeekBar seekBar) {
            mMusicService.pauseSong(seekBar);
        }

        @Override
        public void onSeekBarScroll(int progress) {
            mMusicService.seekTo(progress);
        }
    };

    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //get service
            mMusicService = binder.getService();
            //pass list
            mMusicService.setListSongs(mSongList);
            mMusicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMusicBound = false;
        }
    };

    public int getScreenSize() {
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        if(android.os.Build.VERSION.SDK_INT >= 13) {
            screenWidth = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
        }
        return screenWidth;
    }

    public void getSongList(){
        mSongList = new MediaService(getContentResolver()).getAllMusics();

        Collections.sort(mSongList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
    }

}
