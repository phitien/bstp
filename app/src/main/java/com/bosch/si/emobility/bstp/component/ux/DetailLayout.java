package com.bosch.si.emobility.bstp.component.ux;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.bosch.si.emobility.bstp.R;


/**
 * Created by sgp0458 on 24/12/15.
 */
public class DetailLayout extends RelativeLayout {

    private final ViewDragHelper dragHelper;

    private View headerView;
    private View contentView;

    private float mInitialMotionX;
    private float mInitialMotionY;

    private int mDragRange;
    private int mTop;
    private float mDragOffset;

    private Button reserveButtonRef;

    public View getHeaderView() {
        return headerView;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public View getContentView() {
        return contentView;
    }

    public void setContentView(View contentView) {
        this.contentView = contentView;
    }

    public DetailLayout(Context context) {
        this(context, null);
    }

    public DetailLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onFinishInflate() {
        headerView = findViewById(R.id.viewHeader);
        contentView = findViewById(R.id.viewContent);
        reserveButtonRef = (Button) findViewById(R.id.reserveButton);
    }

    public DetailLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        dragHelper = ViewDragHelper.create(this, 1f, new DragHelperCallback());

    }

    public void maximize() {
        smoothSlideTo(0f);
    }

    public void minimize() {
        smoothSlideTo(1f);
    }

    public int getDragRange() {
        if (mDragRange <= 0)
            mDragRange = ((View) getParent()).getMeasuredHeight() - headerView.getMeasuredHeight();
        return mDragRange;
    }

    boolean smoothSlideTo(float slideOffset) {
        int mDragRange = getDragRange();
        final int topBound = getPaddingTop();
        int y = (int) (topBound + slideOffset * mDragRange);

        if (dragHelper.smoothSlideViewTo(headerView, headerView.getLeft(), y)) {
            ViewCompat.postInvalidateOnAnimation(this);
            return true;
        }
        return false;
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == headerView;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            mTop = top;
            mDragOffset = (float) top / getDragRange();
            headerView.setPivotY(headerView.getHeight());
            requestLayout();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int dragRange = getDragRange();
            int top = getPaddingTop();
            if (yvel > 0 || (yvel == 0 && mDragOffset > 0.5f)) {
                top += dragRange;
            }
            dragHelper.settleCapturedViewAt(releasedChild.getLeft(), top);

            int releasedChildTop = releasedChild.getTop();

            if (yvel > 0) {//moving down
                if (releasedChildTop > 200) {
                    smoothSlideTo(1f);
                } else {
                    smoothSlideTo(0f);
                }
            } else {//moving up
                if (releasedChildTop < dragRange + 200) {
                    smoothSlideTo(0f);
                } else {
                    smoothSlideTo(1f);
                }
            }
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return getDragRange();
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            final int topBound = getPaddingTop();
            final int bottomBound = getHeight() - headerView.getHeight() - headerView.getPaddingBottom();

            return Math.min(Math.max(top, topBound), bottomBound);
        }
    }

    @Override
    public void computeScroll() {
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public static boolean isPointInsideView(float x, float y, View view) {
        int location[] = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];

        //point is inside view bounds
        if ((x > viewX && x < (viewX + view.getWidth())) &&
                (y > viewY && y < (viewY + view.getHeight()))) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (isPointInsideView(ev.getRawX(), ev.getRawY(), reserveButtonRef)) {
            super.onInterceptTouchEvent(ev);
            return false;
        }

        final int action = MotionEventCompat.getActionMasked(ev);

        if ((action != MotionEvent.ACTION_DOWN)) {

            dragHelper.cancel();
            return super.onInterceptTouchEvent(ev);
        }

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {

            dragHelper.cancel();
            return false;
        }

        final float x = ev.getX();
        final float y = ev.getY();
        boolean interceptTap = false;

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mInitialMotionX = x;
                mInitialMotionY = y;
                interceptTap = dragHelper.isViewUnder(headerView, (int) x, (int) y);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final float adx = Math.abs(x - mInitialMotionX);
                final float ady = Math.abs(y - mInitialMotionY);
                final int slop = dragHelper.getTouchSlop();
                if (ady > slop && adx > ady) {
                    dragHelper.cancel();
                    return false;
                }
            }
        }

        return dragHelper.shouldInterceptTouchEvent(ev) || interceptTap;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (isPointInsideView(ev.getRawX(), ev.getRawY(), reserveButtonRef)) {
            super.onTouchEvent(ev);
            return true;
        }

        dragHelper.processTouchEvent(ev);

        final int action = ev.getAction();
        final float x = ev.getX();
        final float y = ev.getY();

        boolean isHeaderViewUnder = dragHelper.isViewUnder(headerView, (int) x, (int) y);
        switch (action & MotionEventCompat.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                mInitialMotionX = x;
                mInitialMotionY = y;
                break;
            }

            case MotionEvent.ACTION_UP: {
                final float dx = x - mInitialMotionX;
                final float dy = y - mInitialMotionY;
                final int slop = dragHelper.getTouchSlop();
                if (dx * dx + dy * dy < slop * slop && isHeaderViewUnder) {
                    if (mDragOffset == 0) {
                        smoothSlideTo(1f);
                    } else {
                        smoothSlideTo(0f);
                    }
                }
                break;
            }
        }

        return isHeaderViewUnder && isViewHit(headerView, (int) x, (int) y) || isViewHit(contentView, (int) x, (int) y);
    }


    private boolean isViewHit(View view, int x, int y) {
        int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);
        int[] parentLocation = new int[2];
        this.getLocationOnScreen(parentLocation);
        int screenX = parentLocation[0] + x;
        int screenY = parentLocation[1] + y;
        return screenX >= viewLocation[0] && screenX < viewLocation[0] + view.getWidth() &&
                screenY >= viewLocation[1] && screenY < viewLocation[1] + view.getHeight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, 0),
                resolveSizeAndState(maxHeight, heightMeasureSpec, 0));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        headerView.layout(
                0,
                mTop,
                r,
                mTop + headerView.getMeasuredHeight());

        contentView.layout(
                0,
                mTop + headerView.getMeasuredHeight(),
                r,
                mTop + b);
    }
}