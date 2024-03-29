package com.howard.sleephelper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.howard.sleephelper.calendarInfo.CustomDayView;
import com.howard.sleephelper.database.GetRecord;
import com.howard.sleephelper.service.GoSleepService;
import com.ldf.calendar.component.CalendarAttr;
import com.ldf.calendar.component.CalendarViewAdapter;
import com.ldf.calendar.interf.OnSelectDateListener;
import com.ldf.calendar.model.CalendarDate;
import com.ldf.calendar.view.MonthPager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static com.howard.sleephelper.database.GetRecord.getRecord;

/**
 * 睡眠记录页面
 */
public class CalendarPage extends Activity {

    Button btn_left;
    Button btn_right;

    GetRecord mGetRecord;
    boolean[][] recordData;

    private TimePickerView pvTime;

    private long exitTime = 0;

    //calendar 属性
    TextView tvYear;
    TextView tvMonth;
    CoordinatorLayout content;
    MonthPager monthPager;

    private ArrayList<com.ldf.calendar.view.Calendar> currentCalendars = new ArrayList<>();
    private CalendarViewAdapter calendarAdapter;
    private OnSelectDateListener onSelectDateListener;
    private CalendarDate currentDate;
    private boolean initiated = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);
        mGetRecord = getRecord();
        monthPager = findViewById(R.id.calendar_view);
        recordData = new boolean[13][];
        //此处强行setViewHeight，毕竟你知道你的日历牌的高度
        //monthPager.setViewHeight(Utils.dpi2px(this, 300));
        initView();
        initTimePicker();
    }

    private void initView() {
        //Calendar calendar = Calendar.getInstance();
        //month = calendar.get(Calendar.MONTH) + 1;
        int dayNum;
        for (int m = 1; m <= 12; ++m) {
            if (m == 1 || m == 3 || m == 5 || m == 7 || m == 8 || m == 10 || m == 12) {
                dayNum = 31;
            } else if (m == 2) {
                dayNum = 28;
            } else {
                dayNum = 30;
            }
            recordData[m] = mGetRecord.queryByMonth(Integer.toString(m), dayNum);
        }

        btn_left = findViewById(R.id.left);
        btn_right = findViewById(R.id.right);
        content = findViewById(R.id.content);
        tvYear = findViewById(R.id.show_year_view);
        tvMonth = findViewById(R.id.show_month_view);
        initCurrentDate();
        initCalendarView();
        initToolbarClickListener();
    }

//init--------------------------------

    /**
     * 初始化currentDate
     */
    private void initCurrentDate() {
        currentDate = new CalendarDate();
        tvYear.setText(String.format(getResources().getString(R.string.year), currentDate.getYear()));
        tvMonth.setText(String.format(getResources().getString(R.string.month), currentDate.getMonth()));
    }

    /**
     * 初始化CustomDayView，并作为CalendarViewAdapter的参数传入
     */
    private void initCalendarView() {
        initListener();
        CustomDayView customDayView = new CustomDayView(this, R.layout.custom_day);
        calendarAdapter = new CalendarViewAdapter(
                this,
                onSelectDateListener,
                CalendarAttr.CalendarType.MONTH,
                CalendarAttr.WeekArrayType.Monday,
                customDayView);
        calendarAdapter.setOnCalendarTypeChangedListener(new CalendarViewAdapter.OnCalendarTypeChanged() {
            @Override
            public void onCalendarTypeChanged(CalendarAttr.CalendarType type) {
            }
        });
        initMarkData();
        initMonthPager();
    }

    //使用此方法回调日历点击事件
    private void initListener() {
        onSelectDateListener = new OnSelectDateListener() {
            @Override
            public void onSelectDate(CalendarDate date) {
                refreshClickDate(date);
                Intent i = new Intent();
                int d;
                d = date.getDay();
                if (recordData[date.month][d]) {
                    i.setClass(CalendarPage.this, Record.class);
                    i.putExtra("date", date.month + "-" + date.day); // yourDate的格式和readLog()一样
                    CalendarPage.this.startActivity(i);
                } else {
                    Toast.makeText(CalendarPage.this, "没有记录信息", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSelectOtherMonth(int offset) {
                //偏移量 -1表示上一个月 ， 1表示下一个月
                monthPager.selectOtherMonth(offset);
            }
        };
    }

    //使用此方法初始化日历标记数据
    private void initMarkData() {
        HashMap<String, String> markData = new HashMap<>();
        //1表示红点，0表示灰点，只在日历上标注出灰点表示没有打卡的日期
        String s;
        int dayNum;
        for (int m = 1; m <= 12; m++) {
            if (m == 1 || m == 3 || m == 5 || m == 7 || m == 8 || m == 10 || m == 12) {
                dayNum = 31;
            } else if (m == 2) {
                dayNum = 28;
            } else {
                dayNum = 30;
            }
            for (int i = 1; i <= dayNum; i++) {
                // 其实把dayNum直接设定成31效率更高
                if (recordData[m][i]) {
                    s = "2019-" + m + "-" + i;
                    markData.put(s, "1");
                }
            }
        }
        calendarAdapter.setMarkData(markData);
    }

    /**
     * 初始化monthPager，MonthPager继承自ViewPager
     */
    private void initMonthPager() {
        monthPager.setAdapter(calendarAdapter);
        monthPager.setCurrentItem(MonthPager.CURRENT_DAY_INDEX);
        monthPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                position = (float) Math.sqrt(1 - Math.abs(position));
                page.setAlpha(position);
            }
        });
        monthPager.addOnPageChangeListener(new MonthPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentCalendars = calendarAdapter.getPagers();
                if (currentCalendars.get(position % currentCalendars.size()) != null) {
                    CalendarDate date = currentCalendars.get(position % currentCalendars.size()).getSeedDate();
                    currentDate = date;
                    tvYear.setText(String.format(getResources().getString(R.string.year), date.getYear()));
                    tvMonth.setText(String.format(getResources().getString(R.string.month), date.getMonth()));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * 初始化对应功能的listener
     */
    private void initToolbarClickListener() {
        btn_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                monthPager.setCurrentItem(monthPager.getCurrentPosition() - 1);
            }
        });
        btn_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                monthPager.setCurrentItem(monthPager.getCurrentPosition() + 1);
            }
        });
    }

    private void initTimePicker() {//Dialog 模式下，在底部弹出
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                Toast.makeText(CalendarPage.this, "设置成功！", Toast.LENGTH_SHORT).show();
                mGetRecord.updateRemind(getTime(date));
                stopGoSleepService();
                startGoSleepService();
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

    private void refreshClickDate(CalendarDate date) {
        currentDate = date;
        tvYear.setText(String.format(getResources().getString(R.string.year), date.getYear()));
        tvMonth.setText(String.format(getResources().getString(R.string.month), date.getMonth()));
    }

    //init----------------------------------
    public void onClickRemind(View v) {
        if (pvTime != null) {
            // pvTime.setDate(Calendar.getInstance());
            /* pvTime.show(); //show timePicker*/
            pvTime.show(v);//弹出时间选择器，传递参数过去，回调的时候则可以绑定此view
        }
    }

    public void ClickSleep(View v) {
        Intent i = new Intent();
        i.setClass(CalendarPage.this, MainActivity.class);
        CalendarPage.this.startActivity(i);
        CalendarPage.this.finish();
    }

    //重写onWindowFocusChanged方法，使用此方法得知calendar和day的尺寸
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && !initiated) {
            CalendarDate today = new CalendarDate();
            calendarAdapter.notifyDataChanged(today);
            initiated = true;
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

    private String getTime(Date date) {//可根据需要自行截取数据显示
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);
    }

    public void startGoSleepService() {
        Intent ifSleepIntent = new Intent(this, GoSleepService.class);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForegroundService(ifSleepIntent);
        } else {
            this.startService(ifSleepIntent);
        }*/
        this.startService(ifSleepIntent);
    }

    public void stopGoSleepService() {
        Intent ifSleepIntent = new Intent(this, GoSleepService.class);
        this.stopService(ifSleepIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
