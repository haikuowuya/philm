package app.philm.in.view;

import com.google.common.base.Preconditions;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import app.philm.in.R;

public class ParallaxContentScrollView extends FrameLayout {

    private static final float PARALLAX_FRICTION = 0.5f;

    private View mHeaderView;
    private View mContentView;

    private FrameLayout mContentViewWrapper;
    private ScrollView mContentViewScrollView;

    private int mContentOverlaySize;

    public ParallaxContentScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ParallaxContentScrollView);

        mContentOverlaySize = a.getDimensionPixelSize(
                R.styleable.ParallaxContentScrollView_contentOverlay, 0);

        a.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Preconditions.checkState(getChildCount() == 2, "There must be children exactly");

        // Get the Views
        mHeaderView = getChildAt(0);
        mContentView = getChildAt(1);

        // Now remove so that we can add our ScrollView
        removeView(mContentView);

        mContentViewWrapper = new FrameLayout(getContext());
        mContentViewWrapper.addView(mContentView);

        mContentViewScrollView = new CustomScrollView(getContext());
        mContentViewScrollView.setFillViewport(true);
        mContentViewScrollView.addView(mContentViewWrapper,
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(mContentViewScrollView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        updateContentViewPaddingTop();
    }

    void updateContentViewPaddingTop() {
        final int targetPaddingTop = mHeaderView.getHeight() - mContentOverlaySize;

        if (mContentViewWrapper.getPaddingTop() != targetPaddingTop) {
            mContentViewWrapper.post(new Runnable() {
                @Override
                public void run() {
                    mContentViewWrapper.setPadding(0, targetPaddingTop, 0, 0);
                }
            });
        }
    }

    void onScrollViewScrollChanged(int y) {
        if (y <= mHeaderView.getHeight()) {
            int newTop = Math.round(-y * PARALLAX_FRICTION);
            mHeaderView.offsetTopAndBottom(newTop - mHeaderView.getTop());
        }
    }

    class CustomScrollView extends ScrollView {

        public CustomScrollView(Context context) {
            super(context);
        }

        @Override
        protected void onScrollChanged(int l, int t, int oldl, int oldt) {
            super.onScrollChanged(l, t, oldl, oldt);
            onScrollViewScrollChanged(t);
        }
    }
}