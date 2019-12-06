package com.ssports.refreshlayout;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.ssports.refreshlayout.utils.ScreenUtils;
import com.ssports.refreshlayout.view.FootView;
import com.ssports.refreshlayout.view.HeaderView;

/**
 * create  by tomcat on 2019-12-06
 *
 * @description 自定义上拉加载更多下拉刷新空间
 * 非嵌入式 可支持headerview footview自定义
 */
public class SmartRefreshLayout extends FrameLayout {
    private Context mContext;
    private View mHeaderView;
    private View mFootView;
    private View mChildView;

    private int mHeaderHeight = 100;
    private int mMaxHeaderHeight;

    private int mFootHeight = 100;
    private int mMaxFootHeight;

    //控制属性
    private boolean canRefresh = true;//是否可以刷新
    private boolean canLoadMore = true;//是否可以加载更多
    private boolean refreshing = false;//是否正在刷新
    private boolean loadingMore = false;//是否正在加载更多
    private float mLastDown = 0;
    private float mCurrentY = 0;

    private OnRefreshListener onRefreshListener;
    private OnLoadMoreListener onLoadMoreListener;

    public static final int REFRESHING = 0;
    public static final int LOADINGMORE = 1;

    public static final int ANIMATION_DURATION = 300;//动画执行时间

    public boolean isCanRefresh() {
        return canRefresh;
    }

    public void setCanRefresh(boolean canRefresh) {
        this.canRefresh = canRefresh;
    }

    public boolean isCanLoadMore() {
        return canLoadMore;
    }

    public void setCanLoadMore(boolean canLoadMore) {
        this.canLoadMore = canLoadMore;
    }

    public boolean isRefreshing() {
        return refreshing;
    }

    public void setRefreshing(boolean refreshing) {
        this.refreshing = refreshing;
    }

    public boolean isLoadingMore() {
        return loadingMore;
    }

    public void setLoadingMore(boolean loadingMore) {
        this.loadingMore = loadingMore;
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public SmartRefreshLayout(@NonNull Context context) {
        this(context, null);
    }

    public SmartRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmartRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        mHeaderHeight = ScreenUtils.dp2Px(mContext, mHeaderHeight);
        mMaxHeaderHeight = 2 * mHeaderHeight;
        mFootHeight = ScreenUtils.dp2Px(mContext, mFootHeight);
        mMaxFootHeight = 2 * mFootHeight;
        mHeaderView = LayoutInflater.from(mContext).inflate(R.layout.refresh_view, null);
        mFootView = LayoutInflater.from(mContext).inflate(R.layout.pull_view, null);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        mChildView = getChildAt(0);

        addHeaderView();

        addFootView();

    }

    /**
     * 添加foot
     */
    private void addFootView() {
        if (mFootView == null) {
            mFootView = new FootView(mContext);
        }
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);
        layoutParams.gravity = Gravity.BOTTOM;
        mFootView.setLayoutParams(layoutParams);
        if (mFootView.getParent() != null) {
            ((ViewGroup) mFootView.getParent()).removeAllViews();
        }
        addView(mFootView);
    }

    /**
     * 添加headerview
     */
    private void addHeaderView() {
        if (mHeaderView == null) {
            mHeaderView = new HeaderView(mContext);
        }
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);
        mHeaderView.setLayoutParams(layoutParams);
        if (mHeaderView.getParent() != null) {
            ((ViewGroup) mHeaderView.getParent()).removeAllViews();
        }
        addView(mHeaderView, 0);

    }

    /**
     * 自动刷新控件
     */
    public void autoRefresh() {

        if (canRefresh) {
            startRefresh(0, mHeaderHeight);
        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (!canRefresh && !canLoadMore) {
            return super.onInterceptTouchEvent(ev);
        }

        if (refreshing || loadingMore) {
            return true;
        }


        /**
         * 判断拦截条件
         */

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastDown = ev.getY();
                mCurrentY = mLastDown;
                break;
            case MotionEvent.ACTION_MOVE:

                float dy = ev.getY() - mCurrentY;
                if (canRefresh) {
                    if (dy > 0 && !isCanScroolUp()) {
                        //此处可以自定义view进行一系列动画
                        return true;

                    }
                }

                if (canLoadMore) {
                    if (dy < 0 && !isCanScroolDown()) {
                        //此处同样可以自定义做一系列动画
                        return true;
                    }
                }


                break;


        }


        return super.onInterceptTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (refreshing || loadingMore) {
            return true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                mCurrentY = event.getY();
                float dy = mCurrentY - mLastDown;
                //首先判断当前是否能够刷新
                if (dy > 0 && canRefresh) {
                    dy = dy > mMaxHeaderHeight ? mMaxHeaderHeight : dy;
                    dy = Math.max(0, dy);
                    mHeaderView.getLayoutParams().height = (int) dy;
                    ViewCompat.setTranslationY(mChildView, dy);
                    requestLayout();
                } else {
                    if (canLoadMore) {
                        float mDy = Math.max(0, Math.min(Math.abs(dy), mMaxFootHeight));
                        mFootView.getLayoutParams().height = (int) mDy;
                        ViewCompat.setTranslationY(mChildView, -mDy);
                        requestLayout();
                    }
                }


                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //此时 松开手取消后进行相应处理 采用动画自动更新或者回到原处 临界值大于 mheaderheight高度的
                int dy2 = (int) (mCurrentY - mLastDown);
                if (dy2 > 0 && canRefresh) {
                    if (dy2 > mHeaderHeight) {
                        startRefresh(dy2 > mMaxHeaderHeight ? mMaxHeaderHeight : dy2, mHeaderHeight);
                    } else {
                        endRefresh(mHeaderHeight);
                    }
                } else {
                    if (canLoadMore) {
                        if (Math.abs(dy2) > mFootHeight) {
                            startLoadMore(Math.abs(dy2) > mMaxFootHeight ? mMaxFootHeight : Math.abs(dy2), mFootHeight);
                        } else {
                            endLoadMore(Math.abs(dy2));
                        }
                    }
                }

                reset();
                break;
        }


        return super.onTouchEvent(event);
    }


    private void reset() {
        mCurrentY = 0;
        mLastDown = 0;
    }


    private void startRefresh(int startY, int endY) {

        createTransformAnimation(REFRESHING, startY, endY, new CallBack() {
            @Override
            public void onSuccess() {
                if (onRefreshListener != null) {
                    refreshing = true;
                    onRefreshListener.onRefresh();
                }
            }
        });


    }


    private void startLoadMore(int startY, int endY) {

        createTransformAnimation(LOADINGMORE, startY, endY, new CallBack() {
            @Override
            public void onSuccess() {
                if (onLoadMoreListener != null) {
                    loadingMore = true;
                    onLoadMoreListener.onLoadMore();
                }

            }
        });

    }

    /**
     * 开始执行动画回归临界点
     */
    private void createTransformAnimation(final int state, int starty, final int endy, final CallBack callBack) {

        ValueAnimator valueAnimator = ValueAnimator.ofInt(starty, endy);
        valueAnimator.setDuration(ANIMATION_DURATION);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                int animatedValue = (int) animation.getAnimatedValue();

                if (state == REFRESHING) {

                    mHeaderView.getLayoutParams().height = animatedValue;

                    ViewCompat.setTranslationY(mChildView, animatedValue);

                } else if (state == LOADINGMORE) {

                    mFootView.getLayoutParams().height = animatedValue;

                    ViewCompat.setTranslationY(mChildView, -animatedValue);

                }

                if (animatedValue == endy) {
                    if (callBack != null) {
                        callBack.onSuccess();
                    }
                }

                requestLayout();
            }
        });
        valueAnimator.start();

    }


    /**
     * 结束上拉加载
     */
    public void endLoadMore() {
        if (mFootView != null && mFootView.getLayoutParams().height > 0 && loadingMore) {
            endLoadMore(mFootHeight);
        }
    }


    private void endLoadMore(int mCurrentY) {
        createTransformAnimation(LOADINGMORE, mCurrentY, 0, new CallBack() {
            @Override
            public void onSuccess() {
                loadingMore = false;
            }
        });


    }

    private void endRefresh(int mCurrentY) {
        createTransformAnimation(REFRESHING, mCurrentY, 0, new CallBack() {
            @Override
            public void onSuccess() {
                refreshing = false;
            }
        });

    }


    public void endRefresh() {

        if (mHeaderView != null && mHeaderView.getLayoutParams().height > 0 && refreshing) {
            endRefresh(mHeaderHeight);
        }

    }


    //是否已经到达顶部继续往下拉
    private boolean isCanScroolUp() {

        return mChildView != null && ViewCompat.canScrollVertically(mChildView, -1);
    }

    //是否已经到达底部可以继续往上拉
    private boolean isCanScroolDown() {

        return mChildView != null && ViewCompat.canScrollVertically(mChildView, 1);
    }


    public interface OnRefreshListener {
        void onRefresh();
    }


    public interface OnLoadMoreListener {
        void onLoadMore();
    }


    public interface CallBack {

        void onSuccess();
    }

}
