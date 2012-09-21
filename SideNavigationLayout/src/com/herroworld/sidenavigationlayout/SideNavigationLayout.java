
package com.herroworld.sidenavigationlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.herroworld.panview.PanView;
import com.herroworld.panview.PanView.OnPanListener;

/**
 * A layout that contains two children, a navigation view to the left and a main
 * view to the right. The main view can be panned to show/hide the navigation
 * view.
 */
public class SideNavigationLayout extends ViewGroup implements PanView.OnMeasuredListener {
    private static final String TAG = SideNavigationLayout.class.getSimpleName();
    private static boolean DEBUG = false;

    // Maximum bounds for the main view
    private int mRightPanBound;
    private int mLeftPanBound;

    // PanView for panning the main view
    private final PanView mPanView;
    private final GestureDetector mGestureDetector;

    // Containers to the navigation and main view
    private final FrameLayout mNavigationViewContainer;
    private final FrameLayout mMainViewContainer;

    public SideNavigationLayout(Context context) {
        this(context, null);
    }

    public SideNavigationLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SideNavigationLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setClipChildren(false);
        setClipToPadding(false);

        // reading attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.SideNavigationLayout);
        final int defaultPanBound = context.getResources().getDimensionPixelSize(
                R.dimen.default_pan_bound);

        mRightPanBound = a.getDimensionPixelSize(R.styleable.SideNavigationLayout_right_pan_bound,
                defaultPanBound);
        mLeftPanBound = a.getDimensionPixelSize(R.styleable.SideNavigationLayout_left_pan_bound,
                defaultPanBound);

        final int navigationLayoutId = a.getResourceId(
                R.styleable.SideNavigationLayout_navigation_layout, 0);
        final int mainLayoutId = a.getResourceId(R.styleable.SideNavigationLayout_main_layout, 0);
        a.recycle();

        // Inflating the navigation view into the container
        final LayoutInflater inflater = LayoutInflater.from(context);
        mNavigationViewContainer = new FrameLayout(context);
        if (navigationLayoutId != 0) {
            inflater.inflate(navigationLayoutId, mNavigationViewContainer, true);
        }

        addView(mNavigationViewContainer, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));

        // Inflating the main view into the container
        mMainViewContainer = new FrameLayout(context) {
            @Override
            public boolean onTouchEvent(MotionEvent event) {
                final Rect mMainViewHitRect = new Rect();

                getHitRect(mMainViewHitRect);
                mMainViewHitRect.offset(-mMainViewContainer.getScrollX(),
                        mMainViewContainer.getScrollY());

                if (mMainViewHitRect.contains((int) event.getX(), (int) event.getY())) {
                    return true;
                }

                return super.onTouchEvent(event);
            }
        };

        if (mainLayoutId != 0) {
            inflater.inflate(mainLayoutId, mMainViewContainer, true);
        }

        addView(mMainViewContainer, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));

        // Listen to when the pan starts/ends and measure
        mPanView = new PanView(context, mMainViewContainer);
        mPanView.setOnMeasuredListener(this);

        mGestureDetector = new GestureDetector(context, mPanView);
        mGestureDetector.setIsLongpressEnabled(false);
    }

    /**
     * Setting the pan listener.
     * 
     * @param listener Pan listener.
     */
    public void setOnPanListener(OnPanListener listener) {
        mPanView.setOnPanListener(listener);
    }

    /**
     * Setting the maximum right pan bound for the main view.
     * 
     * @param bound The right pan bound.
     */
    public void setRightPanBound(int bound) {
        mRightPanBound = bound;
    }

    /**
     * Setting the maximum left pan bound for the main view.
     * 
     * @param bound The left pan bound.
     */
    public void setLeftPanBound(int bound) {
        mLeftPanBound = bound;
    }

    /**
     * @return The navigation view container.
     */
    public FrameLayout getNavigationViewContainer() {
        return mNavigationViewContainer;
    }

    /**
     * @return The main view container.
     */
    public FrameLayout getMainViewContainer() {
        return mMainViewContainer;
    }

    /**
     * @return True if main view is panned, false otherwise.
     */
    public boolean isNavigationViewVisible() {
        return mPanView.isPanned();
    }

    /**
     * @return True if main view is completely hiding the navigation view, false
     *         otherwise.
     */
    public boolean isMainViewVisible() {
        return !mPanView.isPanned();
    }

    /**
     * Pan the main view completely to its right bound.
     */
    public void showNavigationView() {
        mPanView.pan();
    }

    /**
     * Hides the navigation view.
     */
    public void showMainView() {
        mPanView.hide();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();

        // Handle the up event
        if (mPanView.isFocused() && (action == MotionEvent.ACTION_UP)) {
            mPanView.onUp(ev);
            return false;
        }

        // Let ViewController handle all touch events first
        if (mGestureDetector.onTouchEvent(ev) || mPanView.isFocused()) {
            clearPressedState(this);
            return false;
        }

        return super.dispatchTouchEvent(ev);
    }

    /**
     * Setting the right width for the navigation and main view.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        for (int i = 0, childrenCount = getChildCount(); i < childrenCount; ++i) {
            final View view = getChildAt(i);
            if (view == mNavigationViewContainer) {
                final int width = MeasureSpec.getSize(widthMeasureSpec);
                view.measure(
                        MeasureSpec.makeMeasureSpec(width - mRightPanBound, MeasureSpec.EXACTLY),
                        heightMeasureSpec);
            } else if (view == mMainViewContainer) {
                final int contentWidth = MeasureSpec.getSize(widthMeasureSpec);
                view.measure(MeasureSpec.makeMeasureSpec(contentWidth - mLeftPanBound,
                        MeasureSpec.EXACTLY),
                        heightMeasureSpec);
            } else {
                view.measure(widthMeasureSpec, heightMeasureSpec);
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0, childrenCount = getChildCount(); i < childrenCount; ++i) {
            final View view = getChildAt(i);
            if (view == mMainViewContainer) {
                view.layout(mLeftPanBound, 0, mLeftPanBound + view.getMeasuredWidth(),
                        view.getMeasuredHeight());
            } else {
                view.layout(l, 0, l + view.getMeasuredWidth(), view.getMeasuredHeight());
            }
        }
    }

    /**
     * Clears pressed state for all views hierarchy starting from parent view.
     * 
     * @param parent Parent view.
     * @return True if press state was cleared.
     */
    private static boolean clearPressedState(ViewGroup parent) {
        if (parent.isPressed()) {
            parent.setPressed(false);
            return true;
        }

        for (int i = 0, count = parent.getChildCount(); i < count; ++i) {
            final View view = parent.getChildAt(i);
            if (view.isPressed()) {
                view.setPressed(false);
                return true;
            }

            if (!(view instanceof ViewGroup)) {
                continue;
            }

            if (clearPressedState((ViewGroup) view)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Provide maximum right pan.
     * 
     * @return Maximum right pan.
     */
    @Override
    public int getMaxRightPan() {
        return getWidth() - mRightPanBound;
    }

    /**
     * Provide maximum left pan.
     * 
     * @return Maximum left pan.
     */
    @Override
    public int getMaxLeftPan() {
        return mLeftPanBound;
    }
}
