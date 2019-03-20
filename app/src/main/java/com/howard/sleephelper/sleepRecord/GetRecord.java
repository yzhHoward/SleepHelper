package com.howard.sleephelper.sleepRecord;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class GetRecord {

    private BeanDao beanDao;
    private DaoSession mDaoSession;

    public GetRecord(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "sleepRecord.db");
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        mDaoSession = daoMaster.newSession();
        beanDao = mDaoSession.getBeanDao();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    //增
    public Bean insertData(String date, String startTime) {
        Bean mRecord = new Bean(null, date, startTime, startTime, 0,
                false, 0, 0, 0, "", false);
        try {
            beanDao.insert(mRecord);
        } catch (Exception e) {
            Log.e(TAG, "数据库插入失败");
        }
        return mRecord;
    }

    //删
    public void deleteById(Long id) {
        try {
            beanDao.deleteByKey(id);
        } catch (Exception e) {
            Log.e(TAG, "数据库删除失败");
        }
    }

    public void delete(Bean mRecord) {
        try {
            beanDao.delete(mRecord);
        } catch (Exception e) {
            Log.e(TAG, "数据库删除失败");
        }
    }

    //改
    public void update(Bean mRecord, String sleepDetail) {
        if (mRecord != null) {
            mRecord.setSleepDetail(mRecord.getSleepDetail() + sleepDetail);
            beanDao.update(mRecord);
        }
    }

    public void finalUpdate(Bean mRecord, int endHour, int endMin, long totalTime,
                            int deepTime, int swallowTime, int awakeTime) {
        totalTime /= 1000 * 60;
        if (totalTime>2) {
            mRecord.setDrawChart(true);
        }
        mRecord.setEndTime(String.format(Locale.getDefault(), "%02d:%02d", endHour, endMin));
        mRecord.setTotalTime((int)totalTime);
        mRecord.setDeepTime(deepTime);
        mRecord.setSwallowTime(swallowTime);
        mRecord.setAwakeTime(awakeTime);
        mRecord.setValid(true);
        beanDao.update(mRecord);
    }

    //查
    public List queryAllList() {
        return beanDao.queryBuilder().orderDesc(BeanDao.Properties.Id).list();
    }

    public Bean getRecordById(long id) {
        return beanDao.queryBuilder().where(BeanDao.Properties.Id.eq(id)).build().unique();
    }

    public Bean getLatestRecord() {
        List<Bean> records =  beanDao.queryBuilder().orderDesc(BeanDao.Properties.Id).list();
        if(!records.isEmpty())
            return records.get(0);
        return null;
    }
}
