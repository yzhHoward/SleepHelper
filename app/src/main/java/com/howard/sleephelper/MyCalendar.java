package com.howard.sleephelper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectChangeListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.howard.sleephelper.sleepRecord.RecordBean;
import com.howard.sleephelper.sleepRecord.GetRecord;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 睡眠记录页面
 */
public class MyCalendar extends Activity {

    Button btn_left;
    Button btn_right;

    private List<RecordBean> records;
    private RecordBean mRecord;

    private TimePickerView pvTime;


    private boolean left_invisible;
    private boolean right_invisible;
    private int month;
    private long exitTime = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);
        initView();

        initTimePicker();
    }

    public void onClickRemind(View v) {
        if (pvTime != null) {
            // pvTime.setDate(Calendar.getInstance());
            /* pvTime.show(); //show timePicker*/
            pvTime.show(v);//弹出时间选择器，传递参数过去，回调的时候则可以绑定此view
        }
    }

    private void initView() {
        btn_left = findViewById(R.id.left);
        btn_right = findViewById(R.id.right);
        Calendar calendar = Calendar.getInstance();
        month = calendar.get(Calendar.MONTH) + 1;
        // 这里写日历的东西
    }

    private void initTimePicker() {//Dialog 模式下，在底部弹出
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {

            public void onTimeSelect(Date date, View v) {//选中事件回调
                // Toast.makeText(MyCalendar.this, getTime(date), Toast.LENGTH_SHORT).show();
                new GetRecord(MyCalendar.this).updatePunch(getTime(date));
            }
        })
                .setType(new boolean[]{false, false, false, true, true, false})// 默认全部显示
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确认")//确认按钮文字
                .setTitleSize(20)//标题文字大小
                .setTitleText("选择提醒时间")//标题文字
                .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true)//是否循环滚动
                .setTitleColor(Color.BLACK)//标题文字颜色
                .setSubmitColor(Color.BLUE)//确定按钮文字颜色
                .setCancelColor(Color.BLUE)//取消按钮文字颜色
                //.setTitleBgColor(0xFF666666)//标题背景颜色 Night mode
                //.setBgColor(0xFF333333)//滚轮背景颜色 Night mode
                .setLabel("年", "月", "日", "时", "分", "秒")//默认设置为年月日时分秒
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(true)//是否显示为对话框样式
                .build();

        Dialog mDialog = pvTime.getDialog();
        if (mDialog != null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM);

            params.leftMargin = 0;
            params.rightMargin = 0;
            pvTime.getDialogContainerLayout().setLayoutParams(params);

            Window dialogWindow = mDialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim);//修改动画样式
                dialogWindow.setGravity(Gravity.BOTTOM);//改成Bottom,底部显示
                dialogWindow.setDimAmount(0.1f);
            }
        }
    }

    public void ClickSleep(View v) {
        Intent i = new Intent();
        i.setClass(MyCalendar.this, MainActivity.class);
        MyCalendar.this.startActivity(i);
        MyCalendar.this.finish();
    }

    //向左切换睡眠记录
    public void ClickLeft(View v) {
        // 切换日历
        if (month == 0) {
            left_invisible = true;
            btn_left.setVisibility(View.INVISIBLE);
        }
        if (right_invisible) {
            right_invisible = false;
            btn_right.setVisibility(View.VISIBLE);
        }
    }

    //向右切换睡眠记录
    public void ClickRight(View v) {
        // 切换日历
        if (month >= 12) {
            right_invisible = true;
            btn_right.setVisibility(View.INVISIBLE);
        }
        if (left_invisible) {
            left_invisible = false;
            btn_left.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void readLog() {
        GetRecord mGetRecord = new GetRecord(this);
        // 这里读日志
        /*
        records = mGetRecord.queryAllList();
        mRecord = records.get(idx);
        max = records.size() - 1;
        String arr[] = mRecord.getDate().split("-");
        month = Integer.parseInt(arr[0]);
        day = Integer.parseInt(arr[1]);
        */
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
