package cn.wt3.weconds;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import cn.wt3.weconds.R;

public class FloatBallView {


    private Context context;
    private int height = 0;
    private int width = 0;
    private int flag = 1;
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
    private View view;// 浮动模块
    WindowManager.LayoutParams params;

    //添加悬浮View
    public void createFloatView() {

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.floatview, null);
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

        // 触屏监听移动悬浮框
        view.setOnTouchListener(new View.OnTouchListener() {

            float lastX, lastY;
            int oldOffsetX, oldOffsetY;
            int tag = 0;// 悬浮框 所需成员变量

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
            //将控件view和布局参数params加入到windowmanager
            wm.addView(view, params);

        } catch (Exception e) {
            System.out.println("addView失败");
        }
    }

    //点击浮动按钮触发事件，需要override该方法
    private View.OnClickListener l;

    public void onFloatViewClick(View.OnClickListener l) {
        this.l = l;
    }

    //将悬浮View从WindowManager中移除，需要与createFloatView()成对出现
    public void removeFloatView() {
        if (wm != null && view != null) {
            wm.removeViewImmediate(view);
            view = null;
            wm = null;
        }
    }

    //秒数框数据更新方法
    public void updata(String ss) {
        if (wm != null && view != null && view.isShown()) {
            condstext = (TextView) view.findViewById(R.id.condstext);
            condstext.setText(ss);
        }
    }


    public boolean isopfl() {
        if (wm != null && view != null && view.isShown()) {
            return true;
        } else {
            return false;
        }

    }

    //设置浮窗大小尺寸方法
    public void setsize(float fontsize, int width, int height) {

        //获取TextView对应的LayoutParams
        ViewGroup.LayoutParams layoutParams = condstext.getLayoutParams();

        if (layoutParams != null) {

            final float scale = context.getResources().getDisplayMetrics().density;

            //设置字体大小
            condstext.setTextSize(fontsize);
            //设置数字在框内居中
            condstext.setGravity(Gravity.CENTER);
            //设置宽度
            layoutParams.width = (int) (width * scale + 0.5f);
            //设置高度
            layoutParams.height = (int) (height * scale + 0.5f);

            condstext.setLayoutParams(layoutParams);
        }
    }

    //设置浮框不可触摸方法
    public boolean notouch() {

        if (wm != null && view != null) {

            params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            wm.updateViewLayout(view, params);

            return true;
        } else {

            return false;
        }

    }

    //恢复浮框可触摸方法
    public boolean retouch() {

        if (wm != null && view != null) {

            params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

            wm.updateViewLayout(view, params);
            return true;

        } else {
            return false;
        }

    }
}

