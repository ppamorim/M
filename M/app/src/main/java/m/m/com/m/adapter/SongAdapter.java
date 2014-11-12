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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import m.m.com.m.R;
import m.m.com.m.core.service.AbstractService;
import m.m.com.m.model.Song;
import m.m.com.m.service.SamplePhotoService;
import m.m.com.m.utils.DebugUtil;
import m.m.com.m.utils.JsonMapper;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    private int mScreenSize;

    private Context mContext;
    private List<Song> mPhotoList = new ArrayList<Song>();
    private OnItemClick mOnItemClick;

    private HashMap<Integer, Boolean> hasLoaded = new HashMap<Integer, Boolean>();

    public SongAdapter(Context context, List<Song> items, OnItemClick onItemClick, int screenSize) {
        mContext = context;
        mScreenSize = screenSize;
        mPhotoList.addAll(items);
        mOnItemClick = onItemClick;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_song, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

        final Song photo = mPhotoList.get(position);

        if (photo != null) {

            viewHolder.itemView.setTag(position);

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClick.onItemClick(position, viewHolder.progressBar);
                }
            });

            String title = photo.getTitle();

//            if(title != null && mContext != null) {

            try {

                if(hasLoaded.get(position) == null) {

                    hasLoaded.put(position, true);

                    new SamplePhotoService(title, new AbstractService.OnRequestResponse() {
                        @Override
                        public void onSucess(String response) {
                            DebugUtil.log("response: " + response);
                            try {

                                if(response != null) {

                                    String url = new JsonMapper().readTree(response).get("responseData").get("results").get(0).get("url").toString();
                                    String preparedUrl = url.substring(1, url.length() - 1);

                                    if (URLUtil.isValidUrl(preparedUrl)) {
                                        Picasso.with(mContext)
                                                .load(preparedUrl)
                                                .into(viewHolder.image);
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFail(int error) {
                            DebugUtil.log("response error: " + error);
                        }
                    });

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            if(title != null) {
                viewHolder.title.setText(title);
            } else {
                viewHolder.title.setVisibility(View.GONE);
            }
        }

        ViewGroup.LayoutParams layoutParams = viewHolder.itemView.getLayoutParams();
        layoutParams.height = mScreenSize/2;
        viewHolder.itemView.setLayoutParams(layoutParams);

    }

    @Override
    public int getItemCount() {
        return mPhotoList.size();
    }

    public void updateList(ArrayList<Song> photos){
        this.mPhotoList.addAll(photos);
        this.notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final public TextView title;
        final public ImageView image;
        final public ProgressBar progressBar;
        final public String urlImage;

        public ViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            image = (ImageView) itemView.findViewById(R.id.image);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
            urlImage = null;

        }

    }

    public static interface OnItemClick {
        public void onItemClick(int position, ProgressBar progressBar);
    }

}