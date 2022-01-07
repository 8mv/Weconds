package cn.wt3.weconds;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class FloatBallView {


    private Context context;

    private int height = 0;

    private int width = 0;

    public static FloatBallView floatView2;


    private TextView condstext = null;


    public static FloatBallView getInstance(Context context) {
        if (floatView2 == null) {
            floatView2 = new FloatBallView(context);
        }
        return floatView2;
    }

    public FloatBallView(Context c) {

        this.context = c;

    }

    private WindowManager wm;

    private View view;// 浮动按钮

    WindowManager.LayoutParams params;

    /**
     * 添加悬浮View
     *
     * @Cony
     */

    public void createFloatView() {

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.home_floatview, null);
        }


        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        height = wm.getDefaultDisplay().getHeight();
        width = wm.getDefaultDisplay().getWidth();

        params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_BASE_APPLICATION;// 所有程序窗口的“基地”窗口，其他应用程序窗口都显示在它上面。
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.format = PixelFormat.TRANSLUCENT;// 不设置这个弹出框的透明遮罩显示为黑色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }

        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.TOP | Gravity.LEFT;
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        params.y = screenHeight - (4 * height) / 5;//设置距离底部高度为屏幕五分之四
        params.x = screenWidth;
        view.setBackgroundColor(Color.TRANSPARENT);
        view.setVisibility(View.VISIBLE);
        view.setOnTouchListener(new View.OnTouchListener() {
            // 触屏监听
            float lastX, lastY;
            int oldOffsetX, oldOffsetY;
            int tag = 0;// 悬浮球 所需成员变量

            @Override

            public boolean onTouch(View v, MotionEvent event) {
                final int action = event.getAction();
                float x = event.getX();
                float y = event.getY();
                if (tag == 0) {
                    oldOffsetX = params.x; // 偏移量
                    oldOffsetY = params.y; // 偏移量
                }

                if (action == MotionEvent.ACTION_DOWN) {
                    lastX = x;
                    lastY = y;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    params.x += (int) (x - lastX) / 3; // 减小偏移量,防止过度抖动
                    params.y += (int) (y - lastY) / 3; // 减小偏移量,防止过度抖动
                    tag = 1;
                    wm.updateViewLayout(view, params);
                } else if (action == MotionEvent.ACTION_UP) {
                    int newOffsetX = params.x;
                    int newOffsetY = params.y;
                    // 只要按钮一动位置不是很大,就认为是点击事件
                    if (Math.abs(oldOffsetX - newOffsetX) <= 20
                            && Math.abs(oldOffsetY - newOffsetY) <= 20) {
                        if (l != null) {
                            l.onClick(view);
                        }
                    } else {
                        if (params.x < width / 2) {
                            params.x = 0;
                        } else {
                            params.x = width;
                        }
                        wm.updateViewLayout(view, params);
                        tag = 0;
                    }
                }
                return true;
            }
        });
        try {
            wm.addView(view, params);
        } catch (Exception e) {

        }

        /*floatView2.onFloatViewClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //这边是点击悬浮按钮的响应事件
                Toast.makeText(context, "点击了悬浮球", Toast.LENGTH_LONG);
            }
        });*/
    }

    /**
     * 点击浮动按钮触发事件，需要override该方法
     */

    private View.OnClickListener l;

    public void onFloatViewClick(View.OnClickListener l) {
        this.l = l;
    }

    /**
     * 将悬浮View从WindowManager中移除，需要与createFloatView()成对出现
     */

    public void removeFloatView() {
        if (wm != null && view != null) {
            wm.removeViewImmediate(view);
//          wm.removeView(view);//不要调用这个，WindowLeaked
            view = null;
            wm = null;
        }
    }

    /**
     * 隐藏悬浮View
     */

    public void hideFloatView() {
        if (wm != null && view != null && view.isShown()) {
            view.setVisibility(View.GONE);
        }
    }

    /**
     * 显示悬浮View
     */

    public void showFloatView() {
        if (wm != null && view != null && !view.isShown()) {
            view.setVisibility(View.VISIBLE);
        }
    }

    //数据更新测试方法
    public void updatatest(String ss) {
        if (wm != null && view != null && view.isShown()) {
            condstext = (TextView) view.findViewById(R.id.condstext);
            condstext.setText(ss);
        }
    }

    public void updateViewLayout() {
        if (wm != null) {
            int screenWidth = (int) 480;
            int screenHeight = (int) 720;
            if (screenWidth == 0) {
                screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            }

            if (screenHeight == 0) {
                screenHeight = context.getResources().getDisplayMetrics().heightPixels;
                params.y = screenHeight - height / 3;//设置距离底部高度为屏幕三分之一
            } else {
                params.y = screenHeight;
            }
            params.x = screenWidth;
            wm.updateViewLayout(view, params);
        }

    }

}

