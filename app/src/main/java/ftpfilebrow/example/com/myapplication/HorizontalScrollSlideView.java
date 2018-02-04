package ftpfilebrow.example.com.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * 横着左滑，底部有展示view和触发到监听的view
 * Created by zxj on 2017/11/5.
 */

public class HorizontalScrollSlideView extends LinearLayout implements ObservableScrollView.OnScrollChangedListener {
    private static final String TAG = "ScrollSlideView";

    //移动触发步幅
    private final int MOVE_STRIDE = 6;
    //记录移动x
    private float mRecodX;
    //记录偏移量
    private float mOffsetX;

    //底部分界线位置
    private int mBottomParting;
    //底部展示区长度
    private int mBottomShow;
    //底部触发区长度
    private int mBottomAll;
    //是否有触摸
    private boolean isDown = false;

    private Handler mHandler;
    //滑动触发的监听
    private OnSlideBottomListener mOnSlideBottomListener;
    //内容外部的滑动view
    private ObservableScrollView mScroolView;
    //包裹内容view
    private LinearLayout mContentView;
    //底部展示view
    private View mBottomShowView;
    //底部触发到监听的view
    private View mBottomGoView;

    private boolean needScrollBottom = true;

    public HorizontalScrollSlideView(Context context) {
        this(context, null);
    }

    public HorizontalScrollSlideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHandler = new Handler();
        //LayoutInflater.from(context).inflate(R.layout.horizontal_scroll_slide_view, this);
        //LayoutInflater.from(context).inflate(R.layout.horizontal_scroll_slide_view_bottom, this);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mScroolView = new ObservableScrollView(context);
        mContentView = new LinearLayout(context);
        mScroolView.setLayoutParams(lp);
        mContentView.setLayoutParams(new ViewGroup.LayoutParams(lp));
        mScroolView.addView(mContentView);
        mScroolView.setHorizontalScrollBarEnabled(false);
        addView(mScroolView);
    }

    /**
     * 设置滑动区的内容
     *
     * @param views
     */
    public void setContentViews(List<View> views) {
        mContentView.removeAllViews();
        for (View view : views) {
            mContentView.addView(view);
        }
    }


    public void setContentView(View view) {
        mContentView.removeAllViews();
        mContentView.addView(view);
    }


    public ViewGroup getContentContainer() {
        return mContentView;
    }

    /**
     * 设置触发goveiw的监听
     *
     * @param listener
     */
    public void setOnSlideBottomListener(OnSlideBottomListener listener) {
        mOnSlideBottomListener = listener;
    }

    /**
     * 覆盖后，返回自定义底部view
     *
     * @return 底部展现view
     */
    protected View getBottomShowView() {
        TextView textView = new TextView(getContext());
        textView.setText("继续滑动\n查看全部");
        textView.setGravity(Gravity.CENTER);
        textView.setClickable(false);
        textView.setEnabled(false);
//        textView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        textView.setTextColor(Color.parseColor("#616161"));
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(dp2px(100), ViewGroup.LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(lp);
        return textView;
    }

    /**
     * 覆盖后，返回自定义底部触发view
     *
     * @return 底部触发view
     */
    protected View getBottomGoView() {
        TextView textView = new TextView(getContext());
        textView.setText("->");
        textView.setGravity(Gravity.CENTER);
//        textView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        textView.setTextColor(Color.parseColor("#616161"));
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(dp2px(20), ViewGroup.LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(lp);
        return textView;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
//        mScroolView = findViewById(R.id.sv);
//        mContentView = findViewById(R.id.content);
        //mBottomShowView = findViewById(R.id.bottom_show);
        //mBottomGoView = findViewById(R.id.bottom_go);
        mScroolView.setOnScrollListener(this);

        View showView = getBottomShowView();
        if (showView != null) {
            addView(showView);
            mBottomShowView = showView;
        }

        View goView = getBottomGoView();
        if (goView != null) {
            addView(goView);
            mBottomGoView = goView;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mBottomShow = mBottomShowView.getWidth();
        mBottomAll = mBottomShow + mBottomGoView.getWidth();
        mBottomParting = mBottomShow / 2;
//        Log.i(TAG, "onmeassure: " + mBottomAll);
    }

    @Override
    public void onScrollChanged(int x, int y, int oldX, int oldY) {
        if (!isDown && x > oldX && isScrollBottom(true)) {
            setScrollX(mBottomShow);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        Log.i(TAG, "dispatch: " + ev.getAction());
        if (isScrollBottom(true) || getScrollX() > 0) {
            handleTouch(ev);
        } else {
            mRecodX = ev.getX();
        }

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            isDown = true;
        } else if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
            isDown = false;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //消费掉，保证dispatchTouchevent
        if (needScrollBottom) {
            ViewParent parent = this;
            while (!((parent = parent.getParent()) instanceof ViewPager))
                parent.requestDisallowInterceptTouchEvent(true);
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean isIntercept = isScrollContentBottom() && ev.getAction() == MotionEvent.ACTION_MOVE;
//        Log.i(TAG, "onInterceptTouchEvent: " + ev.getAction() + "  isINtercept:" + isIntercept);
        if (isIntercept)
            getParent().requestDisallowInterceptTouchEvent(true);
        return isIntercept ? true : super.onInterceptTouchEvent(ev);
    }

    private boolean isScrollBottom(boolean isIncludeEqual) {
        int sx = mScroolView.getScrollX();
        int cwidth = mScroolView.getChildAt(0).getWidth();
        int pwidth = getWidth();
//        Log.i(TAG, "sx: " + sx + "cwidth: " + cwidth + "pwidth: " + pwidth);

        if (needScrollBottom)
            return isIncludeEqual ? sx + pwidth >= cwidth : sx + pwidth > cwidth;
        else
            return false;
    }

    public void setNeedScrollBottom(boolean needScrollBottom) {
        this.needScrollBottom = needScrollBottom;
    }


    private boolean isScrollContentBottom() {
        return getScrollX() > 0;
    }


    private boolean handleTouch(MotionEvent event) {

//        Log.i(TAG, "handletouch: " + event.getAction());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mRecodX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mRecodX == 0)
                    mRecodX = event.getX();
                //移动的距离
                mOffsetX = (event.getX() - mRecodX);
                //是否达移动最小值
                if (Math.abs(mOffsetX) < MOVE_STRIDE) {
                    return true;
                }
                //手指左滑
                boolean isLeft = event.getX() - mRecodX < 0;
                mRecodX = event.getX();
                if (isLeft && getScrollX() >= mBottomAll) {
                    setScrollX(mBottomAll);
                    //Log.i(TAG,"1");
                } else if (!isLeft && getScrollX() <= 0) {
                    setScrollX(0);
                    //Log.i(TAG,"2");
                } else {
                    setScrollX((int) (getScrollX() - mOffsetX));
                    //Log.i(TAG,"3");
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (getScrollX() < mBottomParting) {
                    setScrollX(0);

                } else {
                    int delay = 0;
                    if (getScrollX() >= mBottomAll - MOVE_STRIDE) {
                        Log.i(TAG, "slide bottom!");
                        if (mOnSlideBottomListener != null) {
                            mOnSlideBottomListener.onSlideBottom();
                        }
                        delay = 1000;
                    }
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setScrollX(mBottomShow);
                        }
                    }, delay);

                }
                break;
        }
        return true;
    }

    int dp2px(int dp) {
        return (int) (getContext().getResources().getDisplayMetrics().density * dp + 0.5f);
    }

    public interface OnSlideBottomListener {
        void onSlideBottom();
    }

}
