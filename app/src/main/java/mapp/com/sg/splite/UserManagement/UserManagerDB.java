package mapp.com.sg.splite.UserManagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by home on 3/2/2018.
 */

public class UserManagerDB {

    public static final String KEY_ROWID = "_id";
    public static final String KEY_USERNAME = "Username";
    public static final String KEY_PASSWORD = "Password";

    private static final String DATEBASE_NAME = "SPLiteDB";
    private static final String DATABASE_TABLE = "UserManager";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE =
            "create table " + DATABASE_TABLE + " (" + KEY_ROWID + " integer primary key autoincrement, " +
                    KEY_USERNAME + " text not null, " + KEY_PASSWORD + " text not null);";

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public UserManagerDB(Context ctx)
    {
        DBHelper = new DatabaseHelper(ctx);
        db = DBHelper.getWritableDatabase();
    }

    public long insertUser(UserInfo info)
    {
        long id = getUserID(info.getUsername());
        if (id >= 0){
            ContentValues UpdatedValues = new ContentValues();
            UpdatedValues.put(KEY_PASSWORD,info.getEncryptedPassword());
            db.update(DATABASE_TABLE,UpdatedValues,KEY_ROWID + "=" + id,null);
            info.setId(id);
            return id;
        }
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_USERNAME,info.getUsername());
        initialValues.put(KEY_PASSWORD,info.getEncryptedPassword());

        id = db.insert(DATABASE_TABLE,null,initialValues);
        info.setId(id);
        return id;
    }

    public boolean deleteUser(long Id)
    {
        return db.delete(DATABASE_TABLE,KEY_ROWID + "=" + Id,null) > 0;
    }

    public int getUserCount()
    {
        Cursor mCursor = db.query(DATABASE_TABLE,new String[]{KEY_ROWID,KEY_USERNAME,KEY_PASSWORD},
                null,null,null,null,null);
        int Count = 0;
        if (mCursor == null)
            return Count;
        if (mCursor.moveToFirst()){
            do{
                Count++;
            }while(mCursor.moveToNext());
        }
        return Count;
    }

    public ArrayList<String> getAllUsername()
    {
        ArrayList<String> usernames = new ArrayList<>();
        Cursor mCursor = db.query(DATABASE_TABLE,new String[]{KEY_ROWID,KEY_USERNAME,KEY_PASSWORD},
                null,null,null,null,null);
        if (mCursor == null)
            return usernames;
        if (mCursor.moveToFirst()){
            do{
                usernames.add(mCursor.getString(1));
            }while(mCursor.moveToNext());
        }
        return usernames;
    }

    public UserInfo getUser(long rowId) throws SQLException
    {
        Cursor mCursor = db.query(true,DATABASE_TABLE,new String[]{KEY_ROWID,KEY_USERNAME,KEY_PASSWORD},
                KEY_ROWID + "=" + rowId,null,null,null,null,null);
        if (mCursor == null || !mCursor.moveToFirst())
            return null;
        return new UserInfo(mCursor.getInt(0),mCursor.getString(1),mCursor.getString(2));
    }

    public int getUserID(String Username) throws SQLException
    {
        Cursor mCursor = db.query(true,DATABASE_TABLE,new String[]{KEY_ROWID,KEY_USERNAME,KEY_PASSWORD},
                KEY_USERNAME + "='" + Username + "'",null,null,null,null,null);
        if (mCursor == null || !mCursor.moveToFirst())
            return -1;
        return mCursor.getInt(0);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context)
        {
            super(context,DATEBASE_NAME,null,DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try{
                db.execSQL(DATABASE_CREATE);
            }catch(SQLException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.v(TAG,"Upgrading database from version " + oldVersion + " to  " +
                    newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

}
