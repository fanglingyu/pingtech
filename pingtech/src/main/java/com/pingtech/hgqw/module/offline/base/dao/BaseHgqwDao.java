package com.pingtech.hgqw.module.offline.base.dao;

import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Calendar;

import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableUtils;
import com.pingtech.hgqw.base.BaseApplication;
import com.pingtech.hgqw.module.offline.base.utils.DataBaseInfo;
import com.pingtech.hgqw.module.offline.bwdm.entity.Bwdm;
import com.pingtech.hgqw.module.offline.cyxx.entity.TBCyxx;
import com.pingtech.hgqw.module.offline.fwxcb.entity.Fwxcb;
import com.pingtech.hgqw.module.offline.hgzjxx.entity.Hgzjxx;
import com.pingtech.hgqw.module.offline.kacbqk.entity.Kacbqk;
import com.pingtech.hgqw.module.offline.mtdm.entity.Mtdm;
import com.pingtech.hgqw.module.offline.offdata.entity.OffData;
import com.pingtech.hgqw.module.offline.pzxx.entity.Pzxx;
import com.pingtech.hgqw.module.offline.qyxx.entity.Qyxx;
import com.pingtech.hgqw.module.offline.sbxx.entity.Sbxx;
import com.pingtech.hgqw.module.offline.scsb.entity.Scsb;
import com.pingtech.hgqw.module.offline.sxtgl.entity.Sxtgl;
import com.pingtech.hgqw.module.offline.txjl.entity.Dkqk;
import com.pingtech.hgqw.module.offline.txjl.entity.TxjlKk;
import com.pingtech.hgqw.module.offline.txjl.entity.TxjlTk;
import com.pingtech.hgqw.module.offline.userinfo.entity.TBUserinfo;
import com.pingtech.hgqw.utils.Log;

/**
 * 
 * 
 * 类描述：所有Dao基类，实现表的创建、表结构更新
 * 
 * <p>
 * Title: 海江港边检勤务综合管理系统-BaseHgqwDao.java
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: 品恩科技
 * </p>
 * 
 * @author liums
 * @version 1.0
 * @date 2013-9-24 下午8:29:32
 * @param <T>
 */
public abstract class BaseHgqwDao<T> extends OrmLiteSqliteOpenHelper {
	private ConnectionSource connectionSource = null;

	private DatabaseConnection databaseConnection = null;

	private Savepoint savepoint = null;

	protected Dao<T, Integer> dao = null;

	private static final String TAG = "BaseHgqwDao";

	public BaseHgqwDao() {
		super(BaseApplication.instent, DataBaseInfo.DB_NAME, null, DataBaseInfo.DATABASE_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int arg2, int arg3) {
		Log.i(TAG, "onUpgrade");
		// database.execSQL("ALTER TABLE Hgzjxx ADD phonessssss VARCHAR(12) NULL");
		// onCreate(database, connectionSource);// 每次更新要调用建表语句'
		update(database, connectionSource);
	}

	private void update(SQLiteDatabase database, ConnectionSource connectionSource) {
		// Hgzjxx表增加车辆字段
		updateHgzjxx(database, connectionSource);
		updateHgzjxx20150625(database, connectionSource);
	}

	/** 增加三个字段isykt bzkadm bzkamc */
	private void updateHgzjxx20150625(SQLiteDatabase database, ConnectionSource connectionSource) {
		try {
			Log.i(TAG, "updateHgzjxx20150625 表Hgzjxx已存在");
			String sql = null;
			sql = "alter  table hgzjxx  add column isykt varchar (2) null";
			database.execSQL(sql);
			sql = "alter  table hgzjxx  add column bzkadm varchar(30) null";
			database.execSQL(sql);
			sql = "alter  table hgzjxx  add column bzkamc varchar(20) null";
			database.execSQL(sql);
			Log.i(TAG, "updateHgzjxx20150625 表Hgzjxx已更新");
		} catch (Exception e) {
			Log.i(TAG, "updateHgzjxx20150625 更新Hgzjxx出错");
			e.printStackTrace();
		}
	}

	private void updateHgzjxx(SQLiteDatabase database, ConnectionSource connectionSource) {
		try {
			Log.i(TAG, "表Hgzjxx已存在");
			String sql = null;
			sql = "alter  table hgzjxx  add column cllx varchar (20) null";
			database.execSQL(sql);
			sql = "alter  table hgzjxx  add column clsbdh varchar(30) null";
			database.execSQL(sql);
			sql = "alter  table hgzjxx  add column  cphm varchar(20) null";
			database.execSQL(sql);
			sql = "alter  table hgzjxx  add column  clys varchar(20) null";
			database.execSQL(sql);
			sql = "alter  table hgzjxx  add column  jsyhm varchar(30) null";
			database.execSQL(sql);
			sql = "alter  table hgzjxx  add column  fdjh varchar(30) null";
			database.execSQL(sql);
			sql = "alter  table hgzjxx  add column  gj varchar(20) null";
			database.execSQL(sql);
			sql = "alter  table hgzjxx  add column  gsyyz varchar(100) null";
			database.execSQL(sql);
			sql = "alter  table hgzjxx  add column  clpp varchar(100) null";
			database.execSQL(sql);
			sql = "alter  table hgzjxx  add column  lxfs varchar(30) null";
			database.execSQL(sql);
			sql = "alter  table hgzjxx  add column  qylxbs varchar(2) null";
			database.execSQL(sql);
			Log.i(TAG, "表Hgzjxx已更新");
		} catch (Exception e) {
			Log.i(TAG, "更新Hgzjxx出错");
			e.printStackTrace();
		}

		// 更新表字段正检时间、预检时间
		try {
			String sql = null;
			sql = "alter  table kacbqk  add column zjsj varchar (20) null";
			database.execSQL(sql);
			sql = "alter  table kacbqk  add column yjsj varchar(30) null";
			database.execSQL(sql);
			Log.i(TAG, "表Kacbqk已更新");
		} catch (Exception e) {
			Log.i(TAG, "更新Kacbqk出错");
			e.printStackTrace();
		}

	}

	@Override
	public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
		Log.i(TAG, "onCreate");
		createTableHgzjxx(connectionSource);
		createTableCyxx(connectionSource);
		createTableOffData(connectionSource);
		createTableKacbqk(connectionSource);
		createTableMtdm(connectionSource);
		createTableBwdm(connectionSource);
		createTableQyxx(connectionSource);
		createTableFwxcb(connectionSource);
		createTableTBUserinfo(connectionSource);
		createTableTBSbxx(connectionSource);
		createTableTBScsb(connectionSource);
		createTableTBSxtgl(connectionSource);
		createTableTxjlTk(connectionSource);// 梯口通行记录
		createTableTxjlKk(connectionSource);// 卡口通行记录
		createTableDkqk(connectionSource);// 记录搭靠情况
		createTablePzxx(connectionSource);

	}

	private void createTablePzxx(ConnectionSource connectionSource) {
		try {
			Dao<Pzxx, T> d = DaoManager.createDao(connectionSource, Pzxx.class);
			if (!d.isTableExists()) {
				Log.i(TAG, "建表Pzxx");
				TableUtils.createTable(connectionSource, Pzxx.class);
			} else {
				Log.i(TAG, "表Pzxx已存在");
			}
		} catch (java.sql.SQLException e) {
			Log.i(TAG, "建表Pzxx出错");
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @方法名：createTableCyxx
	 * @功能说明：创建Dkqk
	 * @author liums
	 * @date 2013-9-24 下午8:25:47
	 * @param connectionSource
	 */
	private void createTableDkqk(ConnectionSource connectionSource) {
		try {
			Dao<Dkqk, T> d = DaoManager.createDao(connectionSource, Dkqk.class);
			if (!d.isTableExists()) {
				Log.i(TAG, "建表Dkqk");
				TableUtils.createTable(connectionSource, Dkqk.class);
			} else {
				Log.i(TAG, "表Dkqk已存在");
			}
		} catch (java.sql.SQLException e) {
			Log.i(TAG, "建表Dkqk出错");
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @方法名：createTableCyxx
	 * @功能说明：创建TxjlKk
	 * @author liums
	 * @date 2013-9-24 下午8:25:47
	 * @param connectionSource
	 */
	private void createTableTxjlKk(ConnectionSource connectionSource) {
		try {
			Dao<TxjlKk, T> d = DaoManager.createDao(connectionSource, TxjlKk.class);
			if (!d.isTableExists()) {
				Log.i(TAG, "建表TxjlKk");
				TableUtils.createTable(connectionSource, TxjlKk.class);
			} else {
				Log.i(TAG, "表TxjlKk已存在");
			}
		} catch (java.sql.SQLException e) {
			Log.i(TAG, "建表TxjlKk出错");
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @方法名：createTableCyxx
	 * @功能说明：创建TxjlTk
	 * @author liums
	 * @date 2013-9-24 下午8:25:47
	 * @param connectionSource
	 */
	private void createTableTxjlTk(ConnectionSource connectionSource) {
		try {
			Dao<TxjlTk, T> d = DaoManager.createDao(connectionSource, TxjlTk.class);
			if (!d.isTableExists()) {
				Log.i(TAG, "建表TxjlTk");
				TableUtils.createTable(connectionSource, TxjlTk.class);
			} else {
				Log.i(TAG, "表TxjlTk已存在");
			}
		} catch (java.sql.SQLException e) {
			Log.i(TAG, "建表TxjlTk出错");
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @方法名：createTableCyxx
	 * @功能说明：创建船员表
	 * @author liums
	 * @date 2013-9-24 下午8:25:47
	 * @param connectionSource
	 */
	private void createTableCyxx(ConnectionSource connectionSource) {
		try {
			Dao<Hgzjxx, T> d = DaoManager.createDao(connectionSource, Hgzjxx.class);
			if (!d.isTableExists()) {
				Log.i(TAG, "建表Hgzjxx");
				TableUtils.createTable(connectionSource, Hgzjxx.class);
			} else {
				Log.i(TAG, "表Hgzjxx已存在");
			}
		} catch (java.sql.SQLException e) {
			Log.i(TAG, "建表Hgzjxx出错");
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @方法名：createTableHgzjxx
	 * @功能说明：创建证件信息表
	 * @author liums
	 * @date 2013-9-24 下午8:25:58
	 * @param connectionSource
	 */
	private void createTableHgzjxx(ConnectionSource connectionSource) {
		try {
			Dao<TBCyxx, T> d = DaoManager.createDao(connectionSource, TBCyxx.class);
			if (!d.isTableExists()) {
				Log.i(TAG, "建表TBCyxx");
				TableUtils.createTable(connectionSource, TBCyxx.class);
			} else {
				Log.i(TAG, "表TBCyxx已存在");
			}
		} catch (java.sql.SQLException e) {
			Log.i(TAG, "建表TBCyxx出错");
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @方法名：createTableOffData
	 * @功能说明：创建证件信息表
	 * @author liums
	 * @date 2013-9-24 下午8:25:58
	 * @param connectionSource
	 */
	private void createTableOffData(ConnectionSource connectionSource) {
		try {
			Dao<OffData, T> d = DaoManager.createDao(connectionSource, OffData.class);
			if (!d.isTableExists()) {
				Log.i(TAG, "建表OffData");
				TableUtils.createTable(connectionSource, OffData.class);
			} else {
				Log.i(TAG, "表OffData已存在");
			}
		} catch (java.sql.SQLException e) {
			Log.i(TAG, "建表OffData出错");
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @方法名：createTableOffData
	 * @功能说明：创建证件信息表
	 * @author liums
	 * @date 2013-9-24 下午8:25:58
	 * @param connectionSource
	 */
	private void createTableKacbqk(ConnectionSource connectionSource) {
		try {
			Dao<Kacbqk, T> d = DaoManager.createDao(connectionSource, Kacbqk.class);
			if (!d.isTableExists()) {
				Log.i(TAG, "建表Kacbqk");
				TableUtils.createTable(connectionSource, Kacbqk.class);
			} else {
				Log.i(TAG, "表Kacbqk已存在");
			}
		} catch (java.sql.SQLException e) {
			Log.i(TAG, "建表OffData出错");
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @方法名：createTableOffData
	 * @功能说明：创建证件信息表
	 * @author liums
	 * @date 2013-9-24 下午8:25:58
	 * @param connectionSource
	 */
	private void createTableQyxx(ConnectionSource connectionSource) {
		try {
			Dao<Qyxx, T> d = DaoManager.createDao(connectionSource, Qyxx.class);
			if (!d.isTableExists()) {
				Log.i(TAG, "建表Qyxx");
				TableUtils.createTable(connectionSource, Qyxx.class);
			} else {
				Log.i(TAG, "表Qyxx已存在");
			}
		} catch (java.sql.SQLException e) {
			Log.i(TAG, "建表Qyxx出错");
			e.printStackTrace();
		}
	}

	private void createTableFwxcb(ConnectionSource connectionSource) {
		try {
			Dao<Fwxcb, T> d = DaoManager.createDao(connectionSource, Fwxcb.class);
			if (!d.isTableExists()) {
				Log.i(TAG, "建表Fwxcb");
				TableUtils.createTable(connectionSource, Fwxcb.class);
			} else {
				Log.i(TAG, "表Fwxcb已存在");
			}
		} catch (java.sql.SQLException e) {
			Log.i(TAG, "建表Qyxx出错");
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @方法名：createTableOffData
	 * @功能说明：创建证件信息表
	 * @author liums
	 * @date 2013-9-24 下午8:25:58
	 * @param connectionSource
	 */
	private void createTableMtdm(ConnectionSource connectionSource) {
		try {
			Dao<Mtdm, T> d = DaoManager.createDao(connectionSource, Mtdm.class);
			if (!d.isTableExists()) {
				Log.i(TAG, "建表Mtdm");
				TableUtils.createTable(connectionSource, Mtdm.class);
			} else {
				Log.i(TAG, "表Mtdm已存在");
			}
		} catch (java.sql.SQLException e) {
			Log.i(TAG, "建表Mtdm出错");
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @方法名：createTableOffData
	 * @功能说明：创建证件信息表
	 * @author liums
	 * @date 2013-9-24 下午8:25:58
	 * @param connectionSource
	 */
	private void createTableBwdm(ConnectionSource connectionSource) {
		try {
			Dao<Bwdm, T> d = DaoManager.createDao(connectionSource, Bwdm.class);
			if (!d.isTableExists()) {
				Log.i(TAG, "建表Bwdm");
				TableUtils.createTable(connectionSource, Bwdm.class);
			} else {
				Log.i(TAG, "表Bwdm已存在");
			}
		} catch (java.sql.SQLException e) {
			Log.i(TAG, "建表Bwdm出错");
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @方法名：beginTransaction
	 * @功能说明：开启事务,处理大批量数据时使用
	 * @author liums
	 * @date 2013-9-24 下午4:24:49
	 * @throws SQLException
	 */
	public void beginTransaction() throws SQLException {
		// while (BaseApplication.instent.isDbBusy) {
		// Log.i(TAG, "isDbBusy = " + BaseApplication.instent.isDbBusy);
		// SystemClock.sleep(100);
		// }
		// BaseApplication.instent.isDbBusy = true;
		// Log.i(TAG, "the db not busy");
		synchronized (dao) {
			Log.i(TAG, "beginTransaction set the db to busy");
			connectionSource = dao.getConnectionSource();
			databaseConnection = connectionSource.getReadWriteConnection();
			savepoint = databaseConnection.setSavePoint(Calendar.getInstance().getTimeInMillis() + "");
		}
	}

	/**
	 * 
	 * @方法名：endTransaction
	 * @功能说明：结束事务,处理大批量数据时使用
	 * @author liums
	 * @date 2013-9-24 下午4:24:40
	 * @throws SQLException
	 */
	public void endTransaction() throws SQLException {
		Log.i(TAG, "endTransaction set db not busy ");
		databaseConnection.commit(savepoint);
		databaseConnection.close();
		connectionSource.releaseConnection(databaseConnection);
		connectionSource.close();
		// BaseApplication.instent.isDbBusy = false;
	}

	/**
	 * 
	 * @方法名：createTableTBUserinfo
	 * @功能说明：创建用户表
	 * @author 赵琳
	 * @date 2013-10-17 下午4:21:42
	 * @param connectionSource
	 */
	private void createTableTBUserinfo(ConnectionSource connectionSource) {
		try {
			Dao<TBUserinfo, T> d = DaoManager.createDao(connectionSource, TBUserinfo.class);
			if (!d.isTableExists()) {
				Log.i(TAG, "建表TBUserinfo");
				TableUtils.createTable(connectionSource, TBUserinfo.class);
			} else {
				Log.i(TAG, "表TBUserinfo已存在");
			}
		} catch (java.sql.SQLException e) {
			Log.i(TAG, "建表TBUserinfo出错");
			e.printStackTrace();
		}
	}

	/**
	 * @方法名：createTableTBSbxx
	 * @功能说明：创建智能设备概要信息表
	 * @author zhaotf
	 * @date 2013-10-18 下午6:22:05
	 * @param connectionSource
	 */
	private void createTableTBSbxx(ConnectionSource connectionSource) {
		try {
			Dao<Sbxx, T> d = DaoManager.createDao(connectionSource, Sbxx.class);
			if (!d.isTableExists()) {
				Log.i(TAG, "建表TBSbxx");
				TableUtils.createTable(connectionSource, Sbxx.class);
			} else {
				Log.i(TAG, "表TBSbxx已存在");
			}
		} catch (java.sql.SQLException e) {
			Log.i(TAG, "建表TBSbxx出错");
			e.printStackTrace();
		}
	}

	/**
	 * @方法名：createTableTBScsb
	 * @功能说明：创建手持设备概要信息表
	 * @author zhaotf
	 * @date 2013-10-18 下午6:23:20
	 * @param connectionSource
	 */
	private void createTableTBScsb(ConnectionSource connectionSource) {
		try {
			Dao<Scsb, T> d = DaoManager.createDao(connectionSource, Scsb.class);
			if (!d.isTableExists()) {
				Log.i(TAG, "建表TBScsb");
				TableUtils.createTable(connectionSource, Scsb.class);
			} else {
				Log.i(TAG, "表TBScsb已存在");
			}
		} catch (java.sql.SQLException e) {
			Log.i(TAG, "建表TBScsb出错");
			e.printStackTrace();
		}
	}

	/**
	 * @方法名：createTableTBSxtgl
	 * @功能说明：创建摄像头概要信息表
	 * @author zhaotf
	 * @date 2013-10-18 下午6:26:01
	 * @param connectionSource
	 */
	private void createTableTBSxtgl(ConnectionSource connectionSource) {
		try {
			Dao<Sxtgl, T> d = DaoManager.createDao(connectionSource, Sxtgl.class);
			if (!d.isTableExists()) {
				Log.i(TAG, "建表TBSxtgl");
				TableUtils.createTable(connectionSource, Sxtgl.class);
			} else {
				Log.i(TAG, "表TBSxtgl已存在");
			}
		} catch (java.sql.SQLException e) {
			Log.i(TAG, "建表TBSxtgl出错");
			e.printStackTrace();
		}
	}

}