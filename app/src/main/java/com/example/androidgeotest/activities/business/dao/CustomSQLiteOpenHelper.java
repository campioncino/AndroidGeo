package com.example.androidgeotest.activities.business.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Database helper class used to manage the creation and upgrading of your
 * database. This class also usually provides the DAOs used by the other
 * classes.
 */
public class CustomSQLiteOpenHelper extends OrmLiteSqliteOpenHelper {

    // name of the database file for your application -- change to something
    // appropriate for your app
    private static String DATABASE_NAME = "mydatabase.db";

    //TABLE RACE DATA
    private static final String RACE_TABLE_NAME = "RACE";
    private static final String RACE_COL_ID = "RACE_ID";
    private static final String RACE_COL_START = "START";
    private static final String RACE_COL_STOP = "STOP";
    private static final String RACE_COL_DURATION = "DURATION";
    private static final String RACE_COL_DISTANCE = "DISTANCE";
    private static final String RACE_COL_TRACK = "TRACK";

    // any time you make changes to your database, you may have to increase the
    // database version
    private static final int DATABASE_VERSION = 1;

    public CustomSQLiteOpenHelper(Context context) {
        super(context, whichDatabase(), null, DATABASE_VERSION);
//        boolean dbexist = isDatabaseExistent(context);
        Log.wtf("databaseHelper", "creazione");
//        if (!dbexist) {
//            Log.d("prova", "provo a copiare");
//            copyFiles(context);
//        }


    }

    private static String whichDatabase() {
//		return "pippo.db";
        return DATABASE_NAME;
    }

    @Override
    public void onCreate(SQLiteDatabase database,
                         ConnectionSource connectionSource) {
        String CREATE_RACE_TABLE = "CREATE TABLE IF NOT EXISTS " + DATABASE_NAME + "( RACE_ID INTEGER PRIMARY KEY AUTOINCREMENT KEY_START DATE  KEY_STOP DATE KEY_DISTANCE  TEXT  KEY_DURATION TEXT KEY_RACE_TRACK TEXT )";
    }

    @Override
    public void onUpgrade(SQLiteDatabase database,
                          ConnectionSource connectionSource, int OldVersion, int newVersion) {
    }

    public boolean isDatabaseExistent(Context context) {
        boolean checkdb = false;
        String myPath = context.getDatabasePath(whichDatabase()).toString();
        File dbfile = new File(myPath);
        checkdb = dbfile.exists();
        Log.wtf(CustomSQLiteOpenHelper.class.getName(), "DB Exist : " + checkdb);
        return checkdb;
    }

    public boolean clearDatabase(Context context) {
        String myPath = context.getDatabasePath(whichDatabase()).toString();
        File dbfile = new File(myPath);
        if (dbfile.exists()){
            return dbfile.delete();
        }
        return false;
    }



    private void copyFiles(Context context) {
        try {
            InputStream myInput = context.getAssets().open("database.db");
            String outFileName = context.getDatabasePath(DATABASE_NAME)
                    .getAbsolutePath();
            File f = new File(outFileName);
            File dir = f.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
//            System.out.println(context.getFilesDir().getAbsolutePath());
            Log.wtf(this.getClass().getSimpleName(),context.getFilesDir().getAbsolutePath());
            Log.wtf("openFileToCopy", outFileName);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int size = 0;
            // Read the entire resource into a local byte buffer.
            byte[] buffer = new byte[1024];
            while ((size = myInput.read(buffer, 0, 1024)) >= 0) {
                outputStream.write(buffer, 0, size);
            }
            myInput.close();
            buffer = outputStream.toByteArray();
            FileOutputStream fos = new FileOutputStream(outFileName);
            fos.write(buffer);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}