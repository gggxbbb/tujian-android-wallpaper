package github.gggxbbb;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

import ml.cerasus.pics.g.MainActivity;

public class MyScrollView extends ScrollView {

    public MyScrollView(Context context){
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
    }

    public MyScrollView(Context context,AttributeSet attributeSet,int defStyleAttr){
        super(context,attributeSet,defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }



}
