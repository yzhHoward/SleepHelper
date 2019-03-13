package com.howard.sleephelper.sleepRecord;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.List;

import static android.content.ContentValues.TAG;

public class GetRecord {

    private BeanDao beanDao;
    private DaoSession mDaoSession;

    private void initGreenDao(Context context) {
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
        Bean mRecord = new Bean(null, date, startTime, startTime,
                "0", "0", "0", "0",
                null);
        try {
            beanDao.insert(mRecord);
        } catch (Exception e) {
            Log.e(TAG, "数据库插入失败");
        }
        return mRecord;
    }

    //删
    public void deleteByKey(Long id) {
        try {
            beanDao.deleteByKey(id);
        } catch (Exception e) {
            Log.e(TAG, "数据库删除失败");
        }
    }

    //改
    public void update(Bean mRecord, String sleepDetail) {
//        Bean mRecord = beanDao.queryBuilder().where(BeanDao.Properties.Id.eq(id)).build().unique();
        if(mRecord!=null) {
            mRecord.setSleepDetail(mRecord.getSleepDetail()+sleepDetail);
            beanDao.update(mRecord);
        }
    }

    public void finalUpdate(Bean mRecord, String sleepDetail) {
//        Bean mRecord = beanDao.queryBuilder().where(BeanDao.Properties.Id.eq(id)).build().unique();
        int startTime = Integer.parseInt(mRecord.getStartTime());
        //TODO: 增加其他几个数据
        if(mRecord!=null) {
            mRecord.setSleepDetail(mRecord.getSleepDetail()+sleepDetail);
            beanDao.update(mRecord);
        }
    }

    //查
    public List queryAllList() {
        return beanDao.queryBuilder().list();
    }
}
