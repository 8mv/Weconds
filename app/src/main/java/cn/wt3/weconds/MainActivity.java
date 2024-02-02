package cn.wt3.weconds;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity {

    CountDownTimer cdt;
    boolean iscdt = false;
    CountDownTimer nt;
    boolean isnt = false;


    //倒计时方法
    void startCDT(int hourOfDay, int minute) {

        cdt = new CountDownTimer(getCountTime(hourOfDay, minute), 19) {
            @Override
            public void onTick(long millisUntilFinished) {
                //每个倒计时周期执行浮框更新
                FloatBallView.getInstance(MainActivity.this).updata(countTimerText(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                System.out.println("倒计时结束");
                cancel();
                FloatBallView.getInstance(MainActivity.this).updata("00:00:00:000");
            }

        }.start();
        iscdt = true;

    }

    void startNT() {

        nt = new CountDownTimer(9999999, 19) {
            @Override
            public void onTick(long millisUntilFinished) {


            }

            @Override
            public void onFinish() {
                this.start();
            }
        }.start();
        isnt = true;

    }


    //将毫秒转换为时分秒毫秒格式
    public String countTimerText(long millsFinished) {
        String sMills = String.valueOf(millsFinished % 1000);
        long Millsnum = millsFinished % 1000;
        String sSec = String.valueOf((millsFinished / 1000) % 60);
        long Secnum = (millsFinished / 1000) % 60;
        String sHour = String.valueOf(millsFinished / 3600000);
        long Hournum = millsFinished / 3600000;
        String sMin = String.valueOf((millsFinished - Hournum * 3600000) / 60000);
        long Minnum = (millsFinished - Hournum * 3600000) / 60000;

        if (Hournum < 10) {
            sHour = "0" + sHour;
        }
        if (Minnum < 10) {
            sMin = "0" + sMin;
        }
        if (Secnum < 10) {
            sSec = "0" + sSec;
        }
        if (Millsnum < 100) {
            if (Millsnum < 10) {
                sMills = "00" + sMills;
            } else {
                sMills = "0" + sMills;
            }
        }

        return sHour + ":" + sMin + ":" + sSec + ":" + sMills;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView getpermiss = findViewById(R.id.getpermiss);
        TextView shownowtime = findViewById(R.id.shownowtime);
        TextView closefloat = findViewById(R.id.closefloat);
        TextView devsite = findViewById(R.id.devsite);
        TextView stablebt = findViewById(R.id.stablebt);
        TextView countdown = findViewById(R.id.countdown);
        TextView setsize = findViewById(R.id.setsize);
        EditText sizenum = findViewById(R.id.sizenum);
        TextView help = findViewById(R.id.help);

        getpermiss.setOnClickListener(new View.OnClickListener() {
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
                                "Weconds已经获取到悬浮框权限！", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });

        //显示毫秒悬浮框
        shownowtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //检查是否已经授予权限
                    if (Settings.canDrawOverlays(MainActivity.this)) {
                        if (iscdt) {
                            cdt.cancel();
                            iscdt = false;
                        }
                        if (isnt) {
                            nt.cancel();
                            isnt = false;
                        }
                        startNT();
                        FloatBallView.getInstance(MainActivity.this).createFloatView();
                        Toast.makeText(getApplicationContext(),
                                "秒数框已经出现！可以拖动，自动吸附边缘！", Toast.LENGTH_SHORT).show();
                    } else {
                        //若未授权则请求权限
                        Toast.makeText(getApplicationContext(),
                                "请先获取悬浮框权限！不授权我怎么显示出来？", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });


        //倒计时模块点击事件
        countdown.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //检查是否已经授予权限
                    if (Settings.canDrawOverlays(MainActivity.this)) {
                        TimePickerDialog timepickerdialog = new TimePickerDialog(MainActivity.this,
                                R.style.timepicker, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                if (isnt) {
                                    nt.cancel();
                                    isnt = false;
                                }
                                if (iscdt) {
                                    cdt.cancel();
                                    iscdt = false;
                                }
                                FloatBallView.getInstance(MainActivity.this).createFloatView();
                                //先计算时间差值，再传入倒计时方法
                                startCDT(hourOfDay, minute);
                                Toast.makeText(getApplicationContext(),
                                        "秒数框已经出现！可以拖动，自动吸附边缘！", Toast.LENGTH_SHORT).show();
                            }
                        }, 13, 14, true);

                        timepickerdialog.setTitle("选择倒计时目标时间点");
                        timepickerdialog.show();
                    } else {
                        //若未授权则请求权限
                        Toast.makeText(getApplicationContext(),
                                "请先获取悬浮框权限！不授权我怎么显示出来？", Toast.LENGTH_SHORT).show();

                    }
                }
            }

        });


        //关闭悬浮框
        closefloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatBallView.getInstance(MainActivity.this).removeFloatView();

                if (iscdt) {
                    cdt.cancel();
                    iscdt = false;
                }

                if (isnt) {
                    nt.cancel();
                    isnt = false;
                }
                stablebt.setText("锁定悬浮框位置");
                Toast.makeText(getApplicationContext(),
                        "秒数框已隐藏！可以再次开启！", Toast.LENGTH_SHORT).show();
            }
        });

        //点击开发者文字跳转到官网
        devsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Uri uri = Uri.parse("http://wt3.cn/s");
                final Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
            }
        });


        //这边是点击悬浮按钮的响应事件
        FloatBallView.getInstance(MainActivity.this).

                onFloatViewClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "我比较喜欢靠边站，拖哪都一样！", Toast.LENGTH_SHORT).show();
                    }
                });


        //设置浮框锁定解锁按键的事件
        stablebt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (stablebt.getText() != "解锁悬浮框位置") {
                    System.out.println("执行固定操作");

                    if (FloatBallView.getInstance(MainActivity.this).notouch()) {
                        stablebt.setText("解锁悬浮框位置");
                        Toast.makeText(MainActivity.this, "浮窗已锁定", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "没开浮窗锁定啥？得开格局！", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    System.out.println("执行解锁操作");
                    if (FloatBallView.getInstance(MainActivity.this).retouch()) {
                        stablebt.setText("锁定悬浮框位置");
                        Toast.makeText(MainActivity.this, "浮窗已解锁", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "没开浮窗解锁啥？格局重开！", Toast.LENGTH_SHORT).show();
                    }


                }
            }
        });

        //设置浮框大小按钮的监听事件
        setsize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FloatBallView.getInstance(MainActivity.this).isopfl()) {
                    if (sizenum.getText().toString().equals("") || sizenum.getText().toString() == null) {
                        Toast.makeText(MainActivity.this, "你敢填我就敢调", Toast.LENGTH_SHORT).show();
                        System.out.println("无输入");
                    } else {

                        String s = sizenum.getText().toString();
                        int sint = Integer.parseInt(s);
                        int fh = sint * 22 / 16;
                        int fw = sint * 106 / 16;
                        float sfloat = sint * 19 / 16;
                        FloatBallView.getInstance(MainActivity.this).setsize(sfloat, fw, fh);
                        sizenum.setText("");
                        Toast.makeText(MainActivity.this, "100以下可以起飞，调回默认填16", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "没开浮窗没法调，格局打不开！", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //帮助与反馈按钮监听事件
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHelpDialog();
            }
        });

    }


    //获得当前时分秒毫秒
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

        String mMillis = String.valueOf(c.get(Calendar.MILLISECOND));//毫秒
        if (c.get(Calendar.MILLISECOND) < 10) {
            mMillis = "00" + mMillis;
        } else if (c.get(Calendar.MILLISECOND) < 100) {
            mMillis = "0" + mMillis;
        }

        return mHour + ":" + mMinute + ":" + mSecond + ":" + mMillis;
    }

    //传入选择的时钟分钟，计算往后最近时间点相差毫秒值
    public long getCountTime(int hourOfDay, int minute) {
        final Calendar cc = Calendar.getInstance();
        cc.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

        //如果当天的该时间点已过，则对应后一天的该时间点
        if (hourOfDay < cc.get(Calendar.HOUR_OF_DAY) ||
                (hourOfDay == cc.get(Calendar.HOUR_OF_DAY) && minute <= cc.get(Calendar.MINUTE))) {
            hourOfDay = hourOfDay + 24;
        }


        int hournum = hourOfDay - cc.get(Calendar.HOUR_OF_DAY);
        int minnum = minute - cc.get(Calendar.MINUTE);
        if (minnum < 0) {
            hournum = hournum - 1;
            minnum = minnum + 60;
        }

        int secnum = 0;
        if (cc.get(Calendar.SECOND) != 0) {
            secnum = 60 - cc.get(Calendar.SECOND);
            minnum = minnum - 1;
            if (minnum < 0) {
                minnum = 59;
                hournum = hournum - 1;
            }
        }

        int millisnum = 0;
        if (cc.get(Calendar.MILLISECOND) != 0) {
            millisnum = 1000 - cc.get(Calendar.MILLISECOND);
            secnum = secnum - 1;
            if (secnum < 0) {
                secnum = 59;
                minnum = minnum - 1;
                if (minnum < 0) {
                    hournum = hournum - 1;
                    minnum = 59;
                }
            }

        }
        long millissum = (hournum * 60 * 60 + minnum * 60 + secnum) * 1000 + millisnum;
        return millissum;


    }


    //显示帮助与反馈弹窗的方法
    private void showHelpDialog() {

        final AlertDialog.Builder alterDiaglog = new AlertDialog.Builder(MainActivity.this);

        alterDiaglog.setTitle("Weconds V3.2 帮助与反馈");//标题文字
        alterDiaglog.setMessage("1.Weconds Android V3.2版更新于2022年5月2日凌晨，较上一版本，时间精确到毫秒级别，加入倒计时模块；\n\n" +
                "2.Weconds所有时间相关操作皆以系统时间为基准，包括一些程序计算过程，由于显示精确到毫秒级别，所以可能存在误差，可反馈优化，但带来误导概不负责；\n\n" +
                "3.Weconds所有程序行为都在本地完成，不涉及任何联网行为，也不会读取任何无关内容，请放心使用；\n\n" +
                "4.Weconds开启全局悬浮框需要系统悬浮窗权限，点击“获取悬浮框权限”按钮之后，会跳转到获取权限页面；\n\n" +
                "5.点击“开启实时浮框”之后，默认可以拖动，自动吸附边缘。拖到指定位置后，可以点击“锁定悬浮框位置”按钮锁定位置。再次点击按钮解除锁定，或者关闭悬浮框再次开启也会解除锁定；\n\n" +
                "6.点击“开启倒计浮框”之后，会弹出一个时间选择框，滑动选择一个时间点确定后，Weconds会以系统时间为基准向未来24小时内的目标时间点进行倒计时；\n\n" +
                "7.第5点与第6点会互相覆盖；\n\n" +
                "8.在输入框内填写0~99的数字，点击“设置浮框大小”之后会立即生效，不管是否锁定位置都生效；\n\n" +
                "9.关闭悬浮框后重新开启，大小、位置、内容、锁定状态都会重置，需要重新设置。\n\n" +
                "另外：如有问题或建议，请私信微信公众号：王探长（我肯定会看到，只是超过48小时后微信官方不让回复）\n\n");//提示消息
        //第三个按钮
        alterDiaglog.setPositiveButton("版本更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("clip", "http://wt3.cn/s");
                cm.setPrimaryClip(clip);

                final Uri uri = Uri.parse("http://wt3.cn/s/");
                final Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);

                Toast.makeText(MainActivity.this, "链接已复制，正在自动跳转...", Toast.LENGTH_SHORT).show();
            }
        });
        //第二个按钮
        alterDiaglog.setNegativeButton("项目开源地址", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("clip", "http://wt3.cn/s");
                cm.setPrimaryClip(clip);

                final Uri uri = Uri.parse("http://wt3.cn/s");
                final Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);

                Toast.makeText(MainActivity.this, "链接已复制，正在自动跳转...", Toast.LENGTH_SHORT).show();
            }
        });
        //第一个按钮
        alterDiaglog.setNeutralButton("关闭窗口", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        //显示
        alterDiaglog.show();
    }

}



