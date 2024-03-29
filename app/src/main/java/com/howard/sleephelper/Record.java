package com.howard.sleephelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.howard.sleephelper.database.GetRecord;
import com.howard.sleephelper.database.RecordBean;
import com.howard.sleephelper.recyclerView.Trace;
import com.howard.sleephelper.recyclerView.TraceListAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.howard.sleephelper.database.GetRecord.getRecord;

/**
 * 睡眠记录页面
 */
public class Record extends Activity {

    private RecyclerView rvTrace;
    private List<Trace> traceList = new ArrayList<>();

    private String date = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.records);
        rvTrace = findViewById(R.id.timelList);
        date = this.getIntent().getStringExtra("date");
        initData();
    }

    //睡眠记录数据初始化
    private void initData() {
        GetRecord mGetRecord = getRecord();
        //List<RecordBean> records = mGetRecord.queryAllList();
        List<RecordBean> records = mGetRecord.queryByDate(date);
        if (records.size() == 1) {
            Intent i = new Intent(Record.this, RecordDetails.class);
            i.putExtra("date", date);
            i.putExtra("position", 0);
            Record.this.startActivity(i);
            Record.this.finish();
        } else {
            for (RecordBean e : records) {
                traceList.add(new Trace(e.getDate(), e.getStartTime() + "-" + e.getEndTime()
                        + "  " + e.getTotalTime() / 60 + "时" + e.getTotalTime() % 60 + "分"));
            }
            TraceListAdapter adapter = new TraceListAdapter(this, traceList, date);
            rvTrace.setLayoutManager(new LinearLayoutManager(this));
            rvTrace.setAdapter(adapter);
        }
    }

    //左上的返回
    public void ClickBack(View v) {
        Record.this.finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
