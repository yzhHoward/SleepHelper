package com.howard.sleephelper.sleepRecord;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * 处理睡眠记录数据库
 */
public class GetRecord {

    private BeanDao beanDao;
    private DaoSession mDaoSession;

    public GetRecord(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper
                (context, String.valueOf(context.getExternalFilesDir(null) + File.separator + "sleepRecord.db"));
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        mDaoSession = daoMaster.newSession();
        beanDao = mDaoSession.getBeanDao();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    /**
     * 插入睡眠记录
     *
     * @param date      日期
     * @param startTime 开始时间
     * @return 睡眠记录
     */
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

    /**
     * 根据id删除睡眠记录
     * @param id 记录id
     */
    public void deleteById(Long id) {
        try {
            beanDao.deleteByKey(id);
        } catch (Exception e) {
            Log.e(TAG, "数据库删除失败");
        }
    }

    /**
     * 根据记录删除
     * @param mRecord 记录对象
     */
    public void delete(Bean mRecord) {
        try {
            beanDao.delete(mRecord);
        } catch (Exception e) {
            Log.e(TAG, "数据库删除失败");
        }
    }

    /**
     * 更新记录
     * @param mRecord 记录对象
     * @param sleepDetail 要新增的信息
     */
    public void update(Bean mRecord, String sleepDetail) {
        if (mRecord != null) {
            mRecord.setSleepDetail(mRecord.getSleepDetail() + sleepDetail);
            beanDao.update(mRecord);
        }
    }

    /**
     * 完成一次睡眠记录时的最终更新
     * @param mRecord 睡眠记录对象
     * @param endHour 结束时间
     * @param endMin 结束时间
     * @param totalTime 总时间
     * @param deepTime 深度睡眠时间
     * @param swallowTime 浅层睡眠时间
     * @param awakeTime 醒的时间
     */
    public void finalUpdate(Bean mRecord, int endHour, int endMin, long totalTime,
                            int deepTime, int swallowTime, int awakeTime) {
        totalTime /= 1000 * 60;
        if (totalTime > 2) {
            mRecord.setDrawChart(true);
        }
        mRecord.setEndTime(String.format(Locale.getDefault(), "%02d:%02d", endHour, endMin));
        mRecord.setTotalTime((int) totalTime);
        mRecord.setDeepTime(deepTime);
        mRecord.setSwallowTime(swallowTime);
        mRecord.setAwakeTime(awakeTime);
        mRecord.setValid(true);
        beanDao.update(mRecord);
    }

    /**
     * 查询所有记录
     * @return 记录对象的列表
     */
    public List queryAllList() {
        return beanDao.queryBuilder().orderDesc(BeanDao.Properties.Id).list();
    }

    public Bean getRecordById(long id) {
        return beanDao.queryBuilder().where(BeanDao.Properties.Id.eq(id)).build().unique();
    }

    public Bean getLatestRecord() {
        List<Bean> records = beanDao.queryBuilder().orderDesc(BeanDao.Properties.Id).list();
        if (!records.isEmpty())
            return records.get(0);
        return null;
    }
}
