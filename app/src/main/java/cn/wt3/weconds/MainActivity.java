package cn.wt3.weconds;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.TimeZone;

import android.os.Handler;


public class MainActivity extends AppCompatActivity {

    private static final int msgKey1 = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        new TimeThread().start();


        Button button1 = findViewById(R.id.button1);
        Button button2 = findViewById(R.id.button2);
        Button button3 = findViewById(R.id.button3);
        TextView devsite = findViewById(R.id.devsite);
        Button safe = findViewById(R.id.safe);


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //检查是否已经授予权限
                    if (!Settings.canDrawOverlays(MainActivity.this)) {
                        //若未授权则请求权限
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, 0);
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Weconds已经获取到悬浮框权限！", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        //显示悬浮球
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("点击了打开悬浮框按钮");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //检查是否已经授予权限
                    if (!Settings.canDrawOverlays(MainActivity.this)) {
                        //若未授权则请求权限
                        Toast.makeText(getApplicationContext(),
                                "请先获取悬浮框权限！不授权我怎么显示出来？", Toast.LENGTH_LONG).show();
                    } else {
                        FloatBallView.getInstance(MainActivity.this).createFloatView();
                        Toast.makeText(getApplicationContext(),
                                "秒数框已经出现！可以拖动，自动吸附边缘！", Toast.LENGTH_LONG).show();


                    }
                }
            }
        });

        //隐藏悬浮球
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatBallView.getInstance(MainActivity.this).removeFloatView();
                Toast.makeText(getApplicationContext(),
                        "秒数框已隐藏！可以再次开启！", Toast.LENGTH_LONG).show();
            }
        });

        //点击开发者文字跳转到官网
        devsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Uri uri = Uri.parse("http://wt3.cn/");
                final Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
            }
        });

        safe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showAlterDialog();

//                之前测试了浮窗数据写入的
//                double sjs = Math.random();
//                String sjsss = String.valueOf(sjs);
//                FloatBallView.getInstance(MainActivity.this).updatatest(sjsss);


            }
        });

        //这边是点击悬浮按钮的响应事件

        FloatBallView.getInstance(MainActivity.this).onFloatViewClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "我比较喜欢靠边站，拖哪都一样！", Toast.LENGTH_LONG).show();
            }
        });
    }

    public class TimeThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    //线程每次休息100毫秒，
                    Thread.sleep(100);
                    Message msg = new Message();
                    msg.what = msgKey1;
                    mHandler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case msgKey1:
                    //mTime.setText(getTime());
                    FloatBallView.getInstance(MainActivity.this).updatatest(getTime());
                    break;
                default:
                    break;
            }
        }
    };

    //获得当前年月日时分秒星期
    public String getTime() {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String mHour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));//时
        if (c.get(Calendar.HOUR_OF_DAY) < 10) {
            mHour = "0" + mHour;
        }
        String mMinute = String.valueOf(c.get(Calendar.MINUTE));//分
        if (c.get(Calendar.MINUTE) < 10) {
            mMinute = "0" + mMinute;
        }
        String mSecond = String.valueOf(c.get(Calendar.SECOND));//秒
        if (c.get(Calendar.SECOND) < 10) {
            mSecond = "0" + mSecond;
        }
        return mHour + ":" + mMinute + ":" + mSecond;
    }

    private void showAlterDialog() {
        final AlertDialog.Builder alterDiaglog = new AlertDialog.Builder(MainActivity.this);

        alterDiaglog.setTitle("Weconds隐私保护声明");//文字
        alterDiaglog.setMessage("Weconds获取悬浮窗权限之后将时分秒小浮窗显示在其他App上层，并且以0.1秒为周期获取手机的系统时间。(当前版本号 V3.0)\n\n" +
                "Weconds不涉及任何联网行为，也不会读取任何无关文件，请放心使用。\n\n" +
                "如有问题请联系邮箱：ghcony@qq.com\n\n" +
                "本项目已开源，如有兴趣请前往 https://github.com/omcc/Weconds 获取项目。\n\n" +
                "By：王探长工作室(官网：wt3.cn)");//提示消息
        //积极的选择
        alterDiaglog.setPositiveButton("复制邮箱地址", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("clip", "ghcony@qq.com");
                cm.setPrimaryClip(clip);
                Toast.makeText(MainActivity.this, "期待您的反馈！", Toast.LENGTH_SHORT).show();
            }
        });
        //消极的选择
        alterDiaglog.setNegativeButton("复制GitHub地址", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("clip", "https://github.com/omcc/Weconds");
                cm.setPrimaryClip(clip);
                Toast.makeText(MainActivity.this, "请粘贴到浏览器访问", Toast.LENGTH_SHORT).show();
            }
        });
        //中立的选择
        alterDiaglog.setNeutralButton("同意声明", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "感谢您的支持，请您放心使用！", Toast.LENGTH_SHORT).show();
            }
        });

        //显示
        alterDiaglog.show();
    }


}



