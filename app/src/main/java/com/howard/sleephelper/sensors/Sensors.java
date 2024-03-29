package com.howard.sleephelper.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

import com.howard.sleephelper.database.GetRecord;
import com.howard.sleephelper.database.RecordBean;

import java.util.Calendar;

import static android.content.ContentValues.TAG;
import static com.howard.sleephelper.database.GetRecord.getRecord;

public class Sensors {
    private int deepTime;
    private int swallowTime;
    private int awakeTime;
    private int size = 0;
    private float k = 0;
    private SensorManager mSensorManager;
    private Sensor Accelerometer;
    //    private Sensor Gyroscope;
    private RecordBean mRecord;
    private GetRecord mGetRecord;

    public Sensors(Context context, RecordBean mRecord) {

        getSensorManager(context);
        startSensor();
        this.mRecord = mRecord;
        mGetRecord = getRecord();
    }

    /**
     * 开始记录的算法，主要是取欧氏距离
     */
    private SensorEventListener listener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        public void onSensorChanged(SensorEvent event) {
            float x, y, z;
            if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];
                ++size;
                k += (float) Math.sqrt(x * x + y * y + z * z);
                if (size == 600) {
                    size = 0;
                    k /= 3f;
                    if (k > 1.0f)
                        k = k / 20 + 1.0f;
                    if (k >= 0.8f)
                        ++awakeTime;
                    else if (k >= 0.37f)
                        ++swallowTime;
                    else
                        ++deepTime;
                    if (k >= 10)
                        k /= 10;
                    Log.d(TAG, "onSensorChanged: go");
                    mGetRecord.update(mRecord, getTime() + "," + k + " ");
                    k = 0;
                }
            }
            /*
            重力传感器，暂时先去掉了
            else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];
                ++size;
                k += (float) Math.sqrt(x * x + y * y + z * z);
                if (size == 600) {
                    size = 0;
                    k /= 3f;
                    if (k > 1.0f)
                        k = k / 100 + 1.2f;
                    if (k >= 0.8f)
                        ++awakeTime;
                    else if (k >= 0.37f)
                        ++swallowTime;
                    else
                        ++deepTime;
                    if (k >= 10)
                        k /= 10;
//                    writeLog(" " + k);
                    k = 0;
                }
            }
            */
        }
    };

    //下面都是传感器，可以放到其他文件里，一class到底真的难看
    private void getSensorManager(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager != null) {
            Accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
//            Gyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        } else {
            Toast.makeText(context, "无法获取传感器，请在设置中授权！", Toast.LENGTH_SHORT).show();
        }
    }

    private void startSensor() {
        if (mSensorManager != null) {
            mSensorManager.registerListener(listener, Accelerometer, 100000);
//            mSensorManager.registerListener(listener, Gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public int[] stopSensor() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(listener);
        }
        size = 0;
        return new int[]{deepTime, swallowTime, awakeTime};
    }

    private String getTime() {
        int TimeDay;
        int TimeHour;
        int TimeMin;
        Calendar calendar = Calendar.getInstance();
        TimeDay = calendar.get(Calendar.DAY_OF_YEAR);
        TimeHour = calendar.get(Calendar.HOUR_OF_DAY);
        TimeMin = calendar.get(Calendar.MINUTE);
        return String.valueOf(TimeDay * 1440 + TimeHour * 60 + TimeMin);
    }
}
