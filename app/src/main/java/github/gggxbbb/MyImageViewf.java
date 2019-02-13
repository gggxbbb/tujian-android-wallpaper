package github.gggxbbb;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

@SuppressLint("AppCompatCustomView")
public class MyImageViewf extends ImageView {

    private float y, lastY;
    private int top, bottom, s_y;
    private Runnable goH;

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
                    s_y = (int) event.getY();
                    top = getTop();
                    bottom = getBottom();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float offsetY = event.getY() - y;
                Log.d("Tujian", "onTouchEvent: " + y + ";" + s_y);
                layout(getLeft(), getTop() + (int) offsetY, getRight(), getBottom() + (int) offsetY);
                break;
            case MotionEvent.ACTION_UP:
                goH.run();
                layout(getLeft(), top, getRight(), bottom);
                break;
        }
        return true;
    }

    public void setGoH(Runnable goH) {
        this.goH = goH;
    }
}
