package m.m.com.m.core.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

/**
 * Created by pedro on 13/11/14.
 */
public class CustomSeekBar extends SeekBar {

    private boolean mCanBeScrolled = true;
    private Drawable mThumb;

    public CustomSeekBar(Context context) {
        super(context);
    }

    public CustomSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    @Override
    public void setThumb(Drawable thumb) {
        super.setThumb(thumb);
        mThumb = thumb;
    }

    private void setCanBeScrolled(boolean canBeScrolled) {
        mCanBeScrolled = canBeScrolled;
    }

    @Override
    public void setThumbOffset(int thumbOffset) {
        super.setThumbOffset(0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

//            if(mThumb.getBounds().contains((int)event.getX(), (int)event.getY())) {
            if (event.getX() >= (mThumb.getBounds().left)*2 && event.getX() <= (mThumb.getBounds().right)*2 && mCanBeScrolled) {

                super.onTouchEvent(event);
            } else {
                return false;
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            return false;
        } else {
            super.onTouchEvent(event);
        }

        return true;
    }

}
