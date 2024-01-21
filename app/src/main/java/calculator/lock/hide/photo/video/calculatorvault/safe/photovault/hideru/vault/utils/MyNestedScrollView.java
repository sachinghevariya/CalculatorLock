package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

public class MyNestedScrollView extends NestedScrollView {
    private boolean intercept;
    private OnScrollChangedListener onScrollChangedListener;

    public interface OnScrollChangedListener {
        void onScrollChanged(MyNestedScrollView myNestedScrollView, int i, int i2, int i3, int i4);
    }

    public MyNestedScrollView(@NonNull Context context) {
        super(context);
        this.onScrollChangedListener = null;
    }

    public MyNestedScrollView(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        this.onScrollChangedListener = null;
    }

    public MyNestedScrollView(@NonNull Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.onScrollChangedListener = null;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (this.intercept) {
            return true;
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    public void setIntercept(boolean z) {
        this.intercept = z;
    }

    public void setOnScrollChangedListener(OnScrollChangedListener onScrollChangedListener) {
        this.onScrollChangedListener = onScrollChangedListener;
    }

    @Override
    public void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
        OnScrollChangedListener onScrollChangedListener = this.onScrollChangedListener;
        if (onScrollChangedListener != null) {
            onScrollChangedListener.onScrollChanged(this, i, i2, i3, i4);
        }
    }
}
