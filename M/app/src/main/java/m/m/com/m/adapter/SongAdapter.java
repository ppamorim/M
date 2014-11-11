package m.m.com.m.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import m.m.com.m.R;
import m.m.com.m.model.Song;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    private int mScreenSize;

    private Context mContext;
    private List<Song> mPhotoList = new ArrayList<Song>();
    private OnItemClick mOnItemClick;

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
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        ViewGroup.LayoutParams layoutParams = viewHolder.itemView.getLayoutParams();
        layoutParams.height = mScreenSize/2;

        viewHolder.itemView.setLayoutParams(layoutParams);

        final Song photo = mPhotoList.get(position);

        if (photo != null) {

            viewHolder.itemView.setTag(position);

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClick.onItemClick(position);
                }
            });

            String title = photo.getTitle();

//            if(title != null && mContext != null) {
//
//                Picasso.with(mContext)
//                        .load(image)
//                        .into(viewHolder.photo);
//
//            }

            if(title != null) {
                viewHolder.title.setText(title);
            } else {
                viewHolder.title.setVisibility(View.GONE);
            }
        }

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
        public TextView title;

        public ViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);

        }

    }

    public static interface OnItemClick {
        public void onItemClick(int position);
    }

}