package github.gggxbbb;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

@SuppressLint("AppCompatCustomView")
public class MyImageViewf extends ImageView {

    private float y;
    private int top, bottom;
    private Runnable goH;
    private boolean canGo = true;

    public MyImageViewf(Context context) {
        super(context);
    }

    public MyImageViewf(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public MyImageViewf(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                y = event.getY();
                if (top == 0 && bottom == 0) {
                    top = getTop();
                    bottom = getBottom();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float offsetY = event.getY() - y;
                Log.d("Tujian", "onTouchEvent: " + getBottom());
                if (canGo)
                    layout(getLeft(), getTop() + (int) offsetY, getRight(), getBottom() + (int) offsetY);
                break;
            case MotionEvent.ACTION_UP:
                layout(getLeft(), top, getRight(), bottom);
                break;
        }

        if ((getBottom() < (bottom / 8 * 7)) && canGo) {
            canGo = false;
            layout(getLeft(), top, getRight(), bottom);
            goH.run();
        }
        if (getBottom() == bottom) {
            canGo = true;
        }
        return true;
    }

    public void setGoH(Runnable goH) {
        this.goH = goH;
    }
}
