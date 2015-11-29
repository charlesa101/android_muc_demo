package com.softwarejoint.chatdemo.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.softwarejoint.chatdemo.AppPrefs.AppPreferences;
import com.softwarejoint.chatdemo.constant.AppConstant;
import com.softwarejoint.chatdemo.xmpp.XMPPUtils;

public class AppDbHelper extends SQLiteOpenHelper {

    public static final boolean DEBUG = true;
    /**
     * ***************** Logcat TAG ***********
     */
    public static final String LOG_TAG = "DBAdapter";
    public static final String DATABASE_NAME = "softwarejoint";

    /*............TABLE names of all tables...........*/


    /*  ...#pragma - mark COLUMN_MSG_STATUS ..*/

    public static final int MSG_STATUS_FAILED_FILE = -1;
    public static final int MSG_STATUS_UPLOAD_FILE = 0;
    public static final int MSG_STATUS_TO_SEND = 1;
    public static final int MSG_STATUS_SENT_TO_SERVER = 2;
    public static final int MSG_STATUS_SENT_TO_DEVICE = 3;
    public static final int MSG_STATUS_READ_BY_DEVICE = 4;
    public static final int MSG_STATUS_RECEIVED = 6;
    public static final int MSG_READ_STATUS_DELIVERY_REPORT_SENT = 7;
    public static final int MSG_READ_STATUS_READ_REPORT_SENT = 8;

    //default value for column

    static AppPreferences mPrefs;
    private static AppDbHelper sAppDbHelper;

    private AppDbHelper(Context context, int DATABASE_VERSION) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is synchronized method used to return instance of this class once it
     * is not created
     *
     * @param context
     * @return {@link AppDbHelper}
     * @throws NameNotFoundException
     */

    public static synchronized AppDbHelper getInstance(Context context) {
        if (sAppDbHelper == null) {
            int versionCode = 1;
            try {
                versionCode = context.getPackageManager().getPackageInfo(
                        context.getPackageName(), 0).versionCode;

            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
            mPrefs = AppPreferences.getInstance(context);
            Log.e("AppDbHelper", "version code : " + versionCode);
            sAppDbHelper = new AppDbHelper(context, versionCode);
            sAppDbHelper.getReadableDatabase();
        }

        return sAppDbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            //TODO: create table here...

	        db.execSQL(GroupMessageTable.getCreateTableStmt());
        } catch (Exception exception) {
            Log.e(LOG_TAG, "Exception Create Table: " + exception.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        clearDatabase();
        onCreate(db);
    }

	/**
	 * This method is used to clear all database
	 */
	public void clearDatabase() {
		SQLiteDatabase db = getWritableDatabase();
		//TODO: add delete table commands here
		db.execSQL(GroupMessageTable.DELETE_STATEMENT);
	}

	@Override
	public synchronized void close() {
		if (sAppDbHelper != null) {
			sAppDbHelper.close();
			super.close();
		}
	}

    /**
     * This is fetchQuery method used to return instance of this class once it
     * is not created
     *
     * @param
     * @return {@link AppDbHelper}
     */

    public Cursor fetchQuery(String query) {
        Cursor cursor = null;
        try {
            final SQLiteDatabase readableDatabase = getReadableDatabase();
            cursor = readableDatabase.rawQuery(query, null);

            if (cursor != null) {
                cursor.moveToFirst();
            }

            return cursor;
        } catch (Exception e) {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            Log.d(LOG_TAG, e.getMessage());
        }
        return null;
    }

    public long insertQuery(ContentValues contentValues, String tablename){
        return insertQuery(contentValues, tablename, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public long insertQuery(ContentValues contentValues, String tablename, int conflictAlgorithm)
            throws SQLException {
        long val = -1;
        try {
            final SQLiteDatabase writableDatabase = getWritableDatabase();
            val = writableDatabase.insertWithOnConflict(tablename, null, contentValues, conflictAlgorithm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return val;
    }

    /**
     * This method is used to delete data from table by table name on behalf of
     * selection and where clause
     *
     * @param table
     * @param whereClause
     * @param whereArgs
     */
    public int delete(String table, String whereClause, String[] whereArgs) {
        final SQLiteDatabase writableDatabase = getWritableDatabase();
        return writableDatabase.delete(table, whereClause, whereArgs);
    }

    /**
     * This method is used to update record by ID with contentValues
     *
     * @param contentValues
     * @param tableName
     * @param whereClause
     * @param whereArgs
     * @return void
     */
    public int updateQuery(ContentValues contentValues, String tableName,
                           String whereClause, String[] whereArgs) {
        int val = -1;
        try {
            final SQLiteDatabase writableDatabase = getWritableDatabase();
            val = writableDatabase.update(tableName, contentValues,
                    whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return val;
    }

    public static void sendPendingMessages() {
        //TODO: add sending message query

    }

	public static void incrementUnreadCount(String groupId){

	}

    public static void clearUnReadCountForGroup(String groupId) {
        //TODO: clear db unread count
    }

	public void saveNewGroupChatMessage(String groupId, @Nullable String userId, String body,
	                                    XMPPUtils.XMPP_MESSAGE_TYPE bodyType, boolean isOutGoing, String messageId)
	{
		userId = (userId == null) ? mPrefs.getUserName() : userId;
		//TODO: insert group chat message into database;
		int msgStatus = isOutGoing ? MSG_STATUS_SENT_TO_SERVER : MSG_STATUS_RECEIVED;

		ContentValues values = new ContentValues();
		values.put(GroupMessageTable.groupId, groupId);
		values.put(GroupMessageTable.isOutGoing, isOutGoing);
		values.put(GroupMessageTable.userId, userId);
		values.put(GroupMessageTable.body, body);
		values.put(GroupMessageTable.bodyType, bodyType.toString());
		values.put(GroupMessageTable.msgStatus, msgStatus);
		values.put(GroupMessageTable.ts, System.currentTimeMillis());
		values.put(GroupMessageTable.messageId, messageId);
		sAppDbHelper.insertQuery(values, GroupMessageTable.TABLE_NAME, SQLiteDatabase.CONFLICT_IGNORE);
		if(!isOutGoing){
			//todo: increment unread count
		}

	}

	public void addOfflineMessage(String groupId, String body, XMPPUtils.XMPP_MESSAGE_TYPE bodyType)
	{


	}
}
