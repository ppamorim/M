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

package m.m.com.m.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import m.m.com.m.R;
import m.m.com.m.core.view.CustomSeekBar;
import m.m.com.m.model.Song;
import m.m.com.m.service.GetAlbumCover;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    private int mScreenSize;

    private Context mContext;
    private List<Song> mSongsList = new ArrayList<Song>();
    private OnItemClick mOnItemClick;

    private HashMap<Integer, Boolean> hasLoaded = new HashMap<Integer, Boolean>();

    private Drawable mPlayDrawable;
    private Drawable mPauseDrawable;

    public SongAdapter(Context context, List<Song> items, OnItemClick onItemClick, int screenSize) {
        mContext = context;
        mScreenSize = screenSize;
        mSongsList.addAll(items);
        mOnItemClick = onItemClick;

        final Resources resources =  mContext.getResources();
        mPlayDrawable = resources.getDrawable(R.drawable.player);
        mPauseDrawable = resources.getDrawable(R.drawable.pause);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_song, viewGroup, false));
    }



    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

        final Song song = mSongsList.get(position);

        if (song != null) {

            if(viewHolder.time.getText().equals("")) {
                viewHolder.time.setText(new StringBuilder("00:00 / ").append(convertToTime(song.getmDuration())));
            }
            viewHolder.title.setText(new StringBuilder(song.getArtist()).append(" - ").append(song.getTitle()));
            viewHolder.itemView.setTag(position);

            viewHolder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        mOnItemClick.onSeekBarScroll(progress);
                    }
                }
            });

            viewHolder.status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(viewHolder.status.getDrawable() == mPlayDrawable) {
                        viewHolder.status.setImageDrawable(mPauseDrawable);
                        mOnItemClick.onPlaySong(position, viewHolder.seekBar, viewHolder.time);
                    } else {
                        viewHolder.status.setImageDrawable(mPlayDrawable);
                        mOnItemClick.onPauseSong(viewHolder.seekBar);
                    }
                }
            });

            String title = song.getTitle();

            if(title != null) {
                viewHolder.title.setText(title);
            } else {
                viewHolder.title.setVisibility(View.GONE);
            }

            new GetAlbumCover(mContext, (int) song.getID(), new GetAlbumCover.OnAlbumRequest() {
                @Override
                public void onSucess(Bitmap bitmap) {
                    viewHolder.image.setImageBitmap(bitmap);
                }

                @Override
                public void onFail() {
                    //TODO Show something
                }
            });

//            try {
//
//                if(hasLoaded.get(position) == null) {
//
//                    hasLoaded.put(position, true);
//
//                    new SamplePhotoService(title, new AbstractService.OnRequestResponse() {
//                        @Override
//                        public void onSucess(String response) {
//                            DebugUtil.log("response: " + response);
//                            try {
//
//                                if(response != null) {
//
//                                    String url = new JsonMapper().readTree(response).get("responseData").get("results").get(0).get("url").toString();
//                                    String preparedUrl = url.substring(1, url.length() - 1);
//
//                                    if (URLUtil.isValidUrl(preparedUrl)) {
//                                        Picasso.with(mContext)
//                                                .load(preparedUrl)
//                                                .into(viewHolder.image);
//                                    }
//                                }
//
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//
//                        @Override
//                        public void onFail(int error) {
//                            DebugUtil.log("response error: " + error);
//                        }
//                    });
//
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

        }

        ViewGroup.LayoutParams layoutParams = viewHolder.itemView.getLayoutParams();
        layoutParams.height = mScreenSize/2;
        viewHolder.itemView.setLayoutParams(layoutParams);

    }

    @Override
    public int getItemCount() {
        return mSongsList.size();
    }

    public void updateList(ArrayList<Song> photos){
        this.mSongsList.addAll(photos);
        this.notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final public TextView title;
        final public TextView time;

        final public ImageView image;
        final public ImageView status;

        final public CustomSeekBar seekBar;
        final public String urlImage;

        public ViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            time = (TextView) itemView.findViewById(R.id.time);

            image = (ImageView) itemView.findViewById(R.id.image);
            status = (ImageView) itemView.findViewById(R.id.status);

            seekBar = (CustomSeekBar) itemView.findViewById(R.id.progress_bar);
            urlImage = null;

        }

    }

    private String convertToTime(int value) {

        int dSeconds = (int) (value / 1000) % 60;
        int dMinutes = (int) ((value / (1000 * 60)) % 60);
        int dHours = (int) ((value / (1000 * 60 * 60)) % 24);

        if (dHours == 0) {
            return String.format("%02d:%02d", dMinutes, dSeconds);
        } else {
            return String.format("%02d:%02d:%02d", dHours, dMinutes, dSeconds);
        }
    }

    public static interface OnItemClick {
        public void onPlaySong(int position, SeekBar seekBar, TextView time);
        public void onSeekBarScroll(int progress);
        public void onPauseSong(SeekBar seekBar);
    }

}