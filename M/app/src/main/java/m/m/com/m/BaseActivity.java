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
import android.provider.MediaStore;
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
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import m.m.com.m.service.MusicService;

public class BaseActivity extends Activity {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mRecyclerLayoutManager;
    private SongAdapter mSongAdapter;

    private ArrayList<Song> mSongList = new ArrayList<Song>();

    private MusicService mMusicService;
    private Intent mPlayIntent;

    private boolean mMusicBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_list);
        mRecyclerLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mRecyclerLayoutManager);

        getSongList();

        Collections.sort(mSongList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });

        int ScreenWidth = getResources().getDisplayMetrics().widthPixels;
        if(android.os.Build.VERSION.SDK_INT >= 13) {
            ScreenWidth = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
        }

        //create and set adapter
        SongAdapter songAdt = new SongAdapter(this, mSongList, onItemClick, ScreenWidth);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(songAdt);


    }

    private SongAdapter.OnItemClick onItemClick = new SongAdapter.OnItemClick() {
        @Override
        public void onPlaySong(int position, SeekBar seekBar, TextView time) {
            mMusicService.setSeekBar(seekBar);
            mMusicService.setTimeSong(time);
            mMusicService.setSong(position);
            mMusicService.playSong();
        }

        @Override
        public void onPauseSong(SeekBar seekBar) {
            mMusicService.pauseSong(seekBar);
        }

        @Override
        public void onResumeSong(SeekBar seekBar) {

        }

        @Override
        public void onSeekBarScroll(int progress) {
            mMusicService.seekTo(progress);
        }
    };

    //connect to the service
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

    //method to retrieve song info from device
    public void getSongList(){
        //query external audio

        Cursor musicCursor = getContentResolver().query(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);

        if(musicCursor != null && musicCursor.moveToFirst()){

            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int durationColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DURATION);

            do {

                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                int duration = musicCursor.getInt(durationColumn);
                mSongList.add(new Song(thisId, thisTitle, thisArtist, duration));

            } while (musicCursor.moveToNext());
        }
    }

    //start and bind the service when the activity starts
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

    //user song select
    public void songPicked(View view){
        mMusicService.setSong(Integer.parseInt(view.getTag().toString()));
        mMusicService.playSong();
    }

}
