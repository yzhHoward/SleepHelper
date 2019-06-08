package com.howard.sleephelper.calendarInfo;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.howard.sleephelper.R;
import com.ldf.calendar.Utils;
import com.ldf.calendar.component.State;
import com.ldf.calendar.interf.IDayRenderer;
import com.ldf.calendar.model.CalendarDate;
import com.ldf.calendar.view.DayView;

import java.util.Objects;

public class CustomDayView extends DayView {

    private final CalendarDate today = new CalendarDate();
    private TextView dateTv;
    private ImageView marker;
    private View selectedBackground;
    private View todayBackground;

    /**
     * 构造器
     *
     * @param context        上下文
     * @param layoutResource 自定义DayView的layout资源
     */

    public CustomDayView(Context context, int layoutResource) {
        super(context, layoutResource);
        dateTv = findViewById(R.id.date);
        marker = findViewById(R.id.maker);
        selectedBackground = findViewById(R.id.selected_background);
        todayBackground = findViewById(R.id.today_background);
    }

    @Override
    public void refreshContent() {
        //你的代码 你可以在这里定义你的显示规则
        super.refreshContent();
        renderToday(day.getDate());
        renderSelect(day.getState());
        renderMarker(day.getDate(), day.getState());
        // dateTv.setTextColor(Color.CYAN);
        dateTv.setTextSize(19);
        super.refreshContent();
    }

    private void renderMarker(CalendarDate date, State state) {
        if (Utils.loadMarkData().containsKey(date.toString())) {
            if (state == State.SELECT || date.toString().equals(today.toString())) {
                marker.setVisibility(GONE);
            } else {
                marker.setVisibility(VISIBLE);
                if (Objects.equals(Utils.loadMarkData().get(date.toString()), "0")) {
                    marker.setEnabled(true);
                } else {
                    marker.setEnabled(false);
                }
            }
        } else {
            marker.setVisibility(GONE);
        }
    }

    private void renderSelect(State state) {
        if (state == State.SELECT) {
            selectedBackground.setVisibility(View.VISIBLE);
            dateTv.setTextColor(Color.BLACK);
        } else if (state == State.NEXT_MONTH || state == State.PAST_MONTH) {
            selectedBackground.setVisibility(GONE);
            dateTv.setTextColor(Color.parseColor("#3666a2"));
        } else {
            selectedBackground.setVisibility(GONE);
            dateTv.setTextColor(Color.WHITE);
        }
    }

    private void renderToday(CalendarDate date) {
        if (date != null) {
            if (date.equals(today)) {
                dateTv.setText("今");
                dateTv.setTextColor(Color.BLACK);
                //todayBackground.setVisibility(VISIBLE);
            } else {
                dateTv.setText(date.day + "");
                todayBackground.setVisibility(GONE);
            }
        }
    }

    @Override
    public IDayRenderer copy() {
        return new CustomDayView(context, layoutResource);
    }
}
