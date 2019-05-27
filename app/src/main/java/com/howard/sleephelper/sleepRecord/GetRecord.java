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

    private RecordBeanDao recordBeanDao;
    private RemindBeanDao remindBeanDao;
    private DaoSession mDaoSession;

    public GetRecord(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper
                (context, context.getExternalFilesDir(null) + File.separator + "sleepRecord.db");
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        mDaoSession = daoMaster.newSession();
        recordBeanDao = mDaoSession.getRecordBeanDao();
        remindBeanDao = mDaoSession.getRemindBeanDao();
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
    public RecordBean insertData(String date, String startTime) {
        RecordBean mRecord = new RecordBean(null, date, startTime, startTime, 0,
                false, 0, 0, 0, "", false);
        try {
            recordBeanDao.insert(mRecord);
        } catch (Exception e) {
            Log.e(TAG, "数据库插入失败");
        }
        return mRecord;
    }

    /**
     * 根据id删除睡眠记录
     *
     * @param id 记录id
     */
    public void deleteById(Long id) {
        try {
            recordBeanDao.deleteByKey(id);
        } catch (Exception e) {
            Log.e(TAG, "数据库删除失败");
        }
    }

    /**
     * 根据记录删除
     *
     * @param mRecord 记录对象
     */
    public void delete(RecordBean mRecord) {
        try {
            recordBeanDao.delete(mRecord);
        } catch (Exception e) {
            Log.e(TAG, "数据库删除失败");
        }
    }

    /**
     * 更新记录
     *
     * @param mRecord     记录对象
     * @param sleepDetail 要新增的信息
     */
    public void update(RecordBean mRecord, String sleepDetail) {
        if (mRecord != null) {
            mRecord.setSleepDetail(mRecord.getSleepDetail() + sleepDetail);
            recordBeanDao.update(mRecord);
        }
    }

    /**
     * 完成一次睡眠记录时的最终更新
     *
     * @param mRecord     睡眠记录对象
     * @param endHour     结束时间
     * @param endMin      结束时间
     * @param totalTime   总时间
     * @param deepTime    深度睡眠时间
     * @param swallowTime 浅层睡眠时间
     * @param awakeTime   醒的时间
     */
    public void finalUpdate(RecordBean mRecord, int endHour, int endMin, long totalTime,
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
        recordBeanDao.update(mRecord);
    }

    /**
     * 查询所有记录
     *
     * @return 记录对象的列表
     */
    public List queryAllList() {
        return recordBeanDao.queryBuilder().orderDesc(RecordBeanDao.Properties.Id).list();
    }

    public List queryByDate(String date) {
        return recordBeanDao.queryBuilder().where(RecordBeanDao.Properties.Date.eq(date)).orderAsc(RecordBeanDao.Properties.Id).list();
    }

    public RecordBean getRecordById(long id) {
        return recordBeanDao.queryBuilder().where(RecordBeanDao.Properties.Id.eq(id)).build().unique();
    }

    public RecordBean getLatestRecord() {
        List<RecordBean> records = recordBeanDao.queryBuilder().orderDesc(RecordBeanDao.Properties.Id).list();
        if (!records.isEmpty())
            return records.get(0);
        return null;
    }

    /**
     * 修改提醒时间
     *
     * @param time 时间
     */
    public void updatePunch(String time) {
        if (remindBeanDao.queryBuilder().list().isEmpty()) {
            remindBeanDao.insert(new RemindBean(null, "22:00"));
        }
        RemindBean remindBean = remindBeanDao.queryBuilder().list().get(0);
        if (remindBean != null) {
            remindBean.setTime(time);
            remindBeanDao.update(remindBean);
        }
    }

    /**
     * 获取提醒时间
     *
     * @return 提醒的时间
     */
    public String getPunch() {
        if (remindBeanDao.queryBuilder().list().isEmpty()) {
            remindBeanDao.insert(new RemindBean(null, "22:00"));
        }
        return remindBeanDao.queryBuilder().list().get(0).getTime();
    }
}
