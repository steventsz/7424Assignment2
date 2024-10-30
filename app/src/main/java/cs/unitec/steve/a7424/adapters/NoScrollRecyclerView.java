package cs.unitec.steve.a7424.adapters;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.recyclerview.widget.RecyclerView;

public class NoScrollRecyclerView extends RecyclerView {

    public NoScrollRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // Disable touch event for scrolling
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        // Disable intercepting touch events for scrolling
        return false;
    }
}

