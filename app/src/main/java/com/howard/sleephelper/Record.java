package com.howard.sleephelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.howard.sleephelper.recyclerView.Trace;
import com.howard.sleephelper.recyclerView.TraceListAdapter;
import com.howard.sleephelper.sleepRecord.Bean;
import com.howard.sleephelper.sleepRecord.GetRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * 睡眠记录页面
 */
public class Record extends Activity {

    private long exitTime = 0;

    private RecyclerView rvTrace;
    private List<Trace> traceList = new ArrayList<>();
    private TraceListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.records);
        rvTrace = findViewById(R.id.timelList);
        initData();
    }

    //睡眠记录数据初始化
    private void initData() {
        GetRecord mGetRecord = new GetRecord(this);
        List<Bean> records = mGetRecord.queryAllList();
        for (Bean e : records) {
            traceList.add(new Trace(e.getDate(), e.getStartTime() + "-" + e.getEndTime()
                    + "  " + e.getTotalTime() / 60 + "时" + e.getTotalTime() % 60 + "分"));
        }
        adapter = new TraceListAdapter(this, traceList);
        rvTrace.setLayoutManager(new LinearLayoutManager(this));
        rvTrace.setAdapter(adapter);
    }

    public void ClickSleep(View v) {
        Intent i = new Intent();
        i.setClass(Record.this, MainActivity.class);
        Record.this.startActivity(i);
        Record.this.finish();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
