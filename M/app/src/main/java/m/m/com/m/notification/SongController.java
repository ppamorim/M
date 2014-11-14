package m.m.com.m.notification;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import m.m.com.m.R;
import m.m.com.m.core.activity.HelperActivity;
import m.m.com.m.model.Song;

public class SongController extends Notification {

    private static final int ID_NOTIFICATION = 548853;

    private Context mContext;
    private NotificationManager mNotificationManager;

    private RemoteViews mRemoteViews;

    @SuppressLint("NewApi")
    public SongController(Context context) {
        super();

        mContext = context;
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mRemoteViews = new RemoteViews(mContext.getPackageName(), R.layout.controller_song);
        configNotification(mRemoteViews);

    }

    private void configNotification(RemoteViews contentView) {
        Notification.Builder builder = new Notification.Builder(mContext);
        @SuppressWarnings("deprecation")
        Notification notification=builder.getNotification();
        notification.when=when;
        notification.tickerText=tickerText;
        notification.icon= R.drawable.ic_launcher;
        notification.flags |= Notification.FLAG_NO_CLEAR;

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN) {
            notification.priority = Notification.PRIORITY_MAX;
        } else {
            notification.priority = Notification.FLAG_HIGH_PRIORITY;
        }



        notification.bigContentView = contentView;
        mNotificationManager.notify(ID_NOTIFICATION, notification);
        setListeners(contentView);
    }

    public void updateView(Song song) {
        mRemoteViews.setTextViewText(R.id.album_title, new StringBuilder(song.getArtist()).append(song.getTitle()));
    }

    public void setListeners(RemoteViews view){
        //radio listener
        Intent radio=new Intent(mContext,HelperActivity.class);
        radio.putExtra("DO", "radio");
        PendingIntent pRadio = PendingIntent.getActivity(mContext, 0, radio, 0);
        view.setOnClickPendingIntent(R.id.radio, pRadio);

//        //volume listener
//        Intent volume=new Intent(mContext, HelperActivity.class);
//        volume.putExtra("DO", "volume");
//        PendingIntent pVolume = PendingIntent.getActivity(mContext, 1, volume, 0);
//        view.setOnClickPendingIntent(R.id.volume, pVolume);
//
//        //reboot listener
//        Intent reboot=new Intent(mContext, HelperActivity.class);
//        reboot.putExtra("DO", "reboot");
//        PendingIntent pReboot = PendingIntent.getActivity(mContext, 5, reboot, 0);
//        view.setOnClickPendingIntent(R.id.reboot, pReboot);

        //top listener
        Intent top=new Intent(mContext, HelperActivity.class);
        top.putExtra("DO", "top");
        PendingIntent pTop = PendingIntent.getActivity(mContext, 3, top, 0);
        view.setOnClickPendingIntent(R.id.top, pTop);

        //app listener
        Intent app=new Intent(mContext, HelperActivity.class);
        app.putExtra("DO", "app");
        PendingIntent pApp = PendingIntent.getActivity(mContext, 4, app, 0);
        view.setOnClickPendingIntent(R.id.back_song, pApp);
    }
}
