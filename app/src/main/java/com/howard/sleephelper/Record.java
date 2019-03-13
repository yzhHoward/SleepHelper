package com.howard.sleephelper;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.howard.sleephelper.RecyclerView.Trace;
import com.howard.sleephelper.RecyclerView.TraceListAdapter;
import com.howard.sleephelper.sleepRecord.DaoMaster;
import com.howard.sleephelper.sleepRecord.DaoSession;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 睡眠记录页面
 */
public class Record extends Activity {

    private long exitTime = 0;

    private int cur;
    private int[][] time = new int[20][4];

    private RecyclerView rvTrace;
    private List<Trace> traceList = new ArrayList<>();
    private TraceListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.records);
        rvTrace = (RecyclerView) findViewById(R.id.timelList);
        readLog();
        initData();
    }

    //睡眠记录数据初始化
    private void initData() {
        int i;
        for (i = 0; i < cur; ++i) {
            traceList.add(new Trace(dateProcess(time[i][0]), timeProcess(time[i][1], time[i][2], time[i][3])));
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

    //非常垃圾的读日志算法，有bug，后面改成数据库的
    private void readLog() {
        String arr[];
        float test;
        boolean flag = false;
        String t = "t";
        String t0 = "start";
        String t1 = "stop";
        String t2 = "date";
        try {
            BufferedReader bfr = new BufferedReader(new FileReader(Environment.getExternalStorageDirectory().getPath() + "/sleep_record.log"));
            String line = bfr.readLine();
            while (line != null) {
                arr = line.split(" ");
                try {
                    if (arr[1].equals(t)) {
                        if (arr[2].equals(t2))
                            time[cur][0] = Integer.parseInt(arr[3]);
                        else if (arr[2].equals(t0))
                            time[cur][1] = Integer.parseInt(arr[3]);
                        else if (arr[2].equals(t1)) {
                            time[cur][2] = Integer.parseInt(arr[3]);
                            if (flag) {
                                ++cur;
                                flag = false;
                            }
                        } else
                            time[cur][3] = Integer.parseInt(arr[3]);
                    } else if (!flag) {
                        test = Float.parseFloat(arr[1]);
                        flag = true;
                    }
                } catch (Exception e) {
                }
                line = bfr.readLine();
            }
            bfr.close();
        } catch (IOException e) {
        }
    }

    //根据日志算睡眠时间的
    public String timeProcess(int target1, int target2, int target3) {
        String AllTime = "";
        int Hour1 = target1 / 60;
        int Min1 = target1 % 60;
        int Hour2 = target2 / 60;
        int Min2 = target2 % 60;
        int Hour3 = target3 / 60;
        int Min3 = target3 % 60;
        if (Hour1 < 10)
            AllTime += "0" + Hour1;
        else
            AllTime += Hour1;
        if (Min1 < 10)
            AllTime += ":0" + Min1;
        else
            AllTime += ":" + Min1;
        if (Hour2 < 10)
            AllTime += "-0" + Hour2;
        else
            AllTime += "-" + Hour2;
        if (Min2 < 10)
            AllTime += ":0" + Min2;
        else
            AllTime += ":" + Min2;
        AllTime += "    " + Hour3 + "h" + Min3 + "min";
        return AllTime;
    }

    public String dateProcess(int target) {
        int Month = target / 31;
        int Day = target % 31;
        if (Day < 10)
            return Month + "-0" + Day;
        else
            return Month + "-" + Day;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
