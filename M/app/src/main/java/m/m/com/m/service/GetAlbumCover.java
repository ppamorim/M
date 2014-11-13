package m.m.com.m.service;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class GetAlbumCover extends AsyncTask<Integer, Void, Bitmap> {

    private Context mContext;
    private OnAlbumRequest mOnAlbumRequest;

    public GetAlbumCover(Context context,int albumId, OnAlbumRequest onAlbumRequest) {

        mContext = context;
        mOnAlbumRequest = onAlbumRequest;

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, albumId);
        } else {
            execute(albumId);
        }
    }

    @Override
    protected Bitmap doInBackground(Integer... integers) {

        try {
            Uri uri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), integers[0]);
            return BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if(bitmap != null) {
            mOnAlbumRequest.onSucess(bitmap);
        } else {
            mOnAlbumRequest.onFail();
        }
    }

    public static interface OnAlbumRequest {
        public void onSucess(Bitmap bitmap);
        public void onFail();
    }

}
