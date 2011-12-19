package proj.tool;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LoggerHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "mppProjILuvTaxi";
	private static final int DB_VERSION = 1;
	private static final String CREATE_TABLE_STRING =
            "CREATE TABLE GPSLog ( _id INTEGER PRIMARY KEY autoincrement, latitude char(20), longitude char(20), timeval char(30) , unixTime char(30));";


	public LoggerHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_STRING);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
	}

}
