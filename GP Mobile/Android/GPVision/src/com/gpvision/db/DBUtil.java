package com.gpvision.db;

import android.database.Cursor;
import com.gpvision.datamodel.Video;
import com.gpvision.datamodel.Video.Status;
import com.gpvision.utils.LogUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBUtil {
	private static final String TABLE_VIDEO = "video";
	private static final String ID = "id";
	private static final String VIDEO_NAME = "videoName";
	private static final String VIDEO_MD5 = "videoMd5";
	private static final String VIDEO_PATH = "videoPath";
	private static final String VIDEO_MINE_TYPE = "videoMineType";
	private static final String VIDEO_SIZE = "videoSize";
	private static final String VIDEO_UPLOADED_SIZE = "videoUploadedSize";
	private static final String VIDEO_STATUS = "videoStatus";

	private DatabaseHelper mOpenHelper;

	public DBUtil(Context context) {
		super();
		mOpenHelper = new DatabaseHelper(context);
	}

	/**
	 * add video info
	 * 
	 * @param video
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	public long addVideo(Video video) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(VIDEO_NAME, video.getOriginalName());
		values.put(VIDEO_MD5, video.getMd5());
		values.put(VIDEO_PATH, video.getOriginalPath());
		values.put(VIDEO_MINE_TYPE, video.getMineType());
		values.put(VIDEO_SIZE, video.getVideoSize());
		values.put(VIDEO_UPLOADED_SIZE, video.getUploadedSize());
		values.put(VIDEO_STATUS, video.getStatus().name());
		long c = db.insert(TABLE_VIDEO, null, values);
		LogUtil.logI("insert:" + c);
		db.close();
		return c;
	}

	/**
	 * update video info
	 * 
	 * @param video
	 * @return the number of rows affected
	 */
	public int update(Video video) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(VIDEO_UPLOADED_SIZE, video.getUploadedSize());
		values.put(VIDEO_STATUS, video.getStatus().name());
		int row = db.update(TABLE_VIDEO, values, VIDEO_MD5 + "=?",
				new String[] { video.getMd5() });
		db.close();
		LogUtil.logI("update:" + row);
		return row;
	}

	/**
	 * delete video
	 * 
	 * @param video
	 */
	public void delete(Video video) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		db.delete(TABLE_VIDEO, VIDEO_MD5 + "=?",
				new String[] { video.getMd5() });
	}

	/**
	 * query the db
	 * 
	 * @return all video info from db
	 */
	public ArrayList<Video> query() {
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_VIDEO, new String[] { VIDEO_NAME,
				VIDEO_MD5, VIDEO_PATH, VIDEO_MINE_TYPE, VIDEO_SIZE,
				VIDEO_UPLOADED_SIZE, VIDEO_STATUS }, null, null, null, null,
				null);
		if (cursor != null && cursor.getCount() > 0) {
			ArrayList<Video> videos = new ArrayList<Video>();
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				Video video = new Video();
				video.setOriginalName(cursor.getString(cursor
						.getColumnIndex(VIDEO_NAME)));
				video.setMd5(cursor.getString(cursor.getColumnIndex(VIDEO_MD5)));
				video.setOriginalPath(cursor.getString(cursor
						.getColumnIndex(VIDEO_PATH)));
				video.setMineType(cursor.getString(cursor
						.getColumnIndex(VIDEO_MINE_TYPE)));
				video.setVideoSize(cursor.getLong(cursor
						.getColumnIndex(VIDEO_SIZE)));
				video.setUploadedSize(cursor.getLong(cursor
						.getColumnIndex(VIDEO_UPLOADED_SIZE)));
				String status = cursor.getString(cursor
						.getColumnIndex(VIDEO_STATUS));
				if (status.equals(Status.uploading.name())) {
					video.setStatus(Status.uploading);
				} else if (status.equals(Status.paused.name())) {
					video.setStatus(Status.paused);
				}
				videos.add(video);
			}
			cursor.close();
			db.close();
			return videos;
		}
		db.close();
		return null;
	}

	public void close() {
		mOpenHelper.close();
	}

	class DatabaseHelper extends SQLiteOpenHelper {
		private static final String DATABASE_NAME = "video.db";
		private static final int DATABASE_VERSION = 1;

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_VIDEO + "(" + ID
					+ " INTEGER PRIMARY KEY," + VIDEO_NAME + " TEXT,"
					+ VIDEO_MD5 + " TEXT," + VIDEO_PATH + " TEXT,"
					+ VIDEO_MINE_TYPE + " TEXT," + VIDEO_SIZE + " INTEGER,"
					+ VIDEO_UPLOADED_SIZE + " INTEGER," + VIDEO_STATUS
					+ " TEXT" + ");";
			db.execSQL(sql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub

		}

	}
}
