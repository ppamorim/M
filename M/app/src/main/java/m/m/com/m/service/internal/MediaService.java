package m.m.com.m.service.internal;

import android.content.ContentResolver;
import android.database.Cursor;

import java.util.ArrayList;

import m.m.com.m.model.Song;

/**
 * Created by pedro on 14/11/14.
 */
public class MediaService {

    private Cursor mMusicCursor;

    public MediaService(ContentResolver contentResolver) {

        mMusicCursor = contentResolver.query(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);

    }

    public ArrayList<Song> getAllMusics() {

        if(mMusicCursor != null && mMusicCursor.moveToFirst()){

            int titleColumn = mMusicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = mMusicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = mMusicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int durationColumn = mMusicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DURATION);

            ArrayList<Song> mSongList = new ArrayList<Song>();

            do {

                long thisId = mMusicCursor.getLong(idColumn);
                String thisTitle = mMusicCursor.getString(titleColumn);
                String thisArtist = mMusicCursor.getString(artistColumn);
                int duration = mMusicCursor.getInt(durationColumn);
                mSongList.add(new Song(thisId, thisTitle, thisArtist, duration));

            } while (mMusicCursor.moveToNext());

            return mSongList;
        } else {
            return null;
        }
    }

}
