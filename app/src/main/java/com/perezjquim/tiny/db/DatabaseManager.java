package com.perezjquim.tiny.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public abstract class DatabaseManager
{
    private static SQLiteDatabase db;
    private static final String DB_NAME = "tiny";

    private static final String BOOKMARK_TABLE = "bookmark";
    private static final String SQL_CREATE_BOOKMARK_TABLE =
            "CREATE TABLE IF NOT EXISTS " + BOOKMARK_TABLE +
            " ("+
            "`id` INTEGER NOT NULL PRIMARY KEY," +
            "`name` VARCHAR(45) NOT NULL," +
            "`url` VARCHAR(45) NOT NULL" +
            ")";
    private static final String SQL_GET_BOOKMARKS =
            "SELECT * FROM " + BOOKMARK_TABLE;
    private static final String SQL_INSERT_BOOKMARK =
            "INSERT INTO " + BOOKMARK_TABLE +" (name,url) VALUES ('%name%','%url%')";
    private static final String SQL_DELETE_BOOKMARK =
            "DELETE FROM " + BOOKMARK_TABLE + " WHERE id='%id%'";
    private static final String SQL_UPDATE_BOOKMARK =
            "UPDATE " + BOOKMARK_TABLE + " SET name='%name', url='%url' WHERE id='%id%'";

    private static final String SQL_CLEAR_DB =
            "DELETE FROM " + BOOKMARK_TABLE;

    public static void initDatabase()
    {
        try
        {
            File dbFolder = new File(Environment.getExternalStorageDirectory(), "/"+DB_NAME);
            if(!dbFolder.exists())
            {
                if (!dbFolder.mkdir())
                { throw new Exception("Could not create database folder"); }
            }
            File dbFile = new File(dbFolder, DB_NAME);
            if(!dbFile.exists())
            {
                dbFile.createNewFile();
            }
            db = SQLiteDatabase.openDatabase(
                    Environment.getExternalStorageDirectory() + "/"+DB_NAME+"/" + DB_NAME,
                    null,
                    SQLiteDatabase.CREATE_IF_NECESSARY);
            createDatabase();
        }
        catch(Exception e)
        { e.printStackTrace(); }
    }

    private static Cursor querySelect(String s)
    {
        Log.e("-- Executing query --",s);
        return db.rawQuery(s,null);
    }

    private static synchronized void query(String s)
    {
        Log.e("-- Executing query --",s);
        db.execSQL(s);
    }

    private static void queryInTransaction(String ... queries)
    {
        db.beginTransaction();
        try
        {
            for(String q : queries)
            {
                query(q);
            }
            db.setTransactionSuccessful();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            db.endTransaction();
        }
    }

    private static void createDatabase()
    {
        queryInTransaction(SQL_CREATE_BOOKMARK_TABLE);
    }

    public static void clearDatabase()
    {
        query(SQL_CLEAR_DB);
    }

    /* ************* */
    /* BOOKMARKS */
    /* ************* */
    public static Cursor getBookmarks()
    {
        return querySelect(SQL_GET_BOOKMARKS);
    }
    public static void insertBookmark(String name, String url)
    {
        query(SQL_INSERT_BOOKMARK
                .replace("%name%", name)
                .replace("%url%",url));
    }
    public static void deleteBookmark(int id)
    {
        query(SQL_DELETE_BOOKMARK
                .replace("%id%",""+id));
    }
    public static void updateBookmark(int id, String name, String url)
    {
        query(SQL_UPDATE_BOOKMARK
                .replace("%id%",""+id)
                .replace("%name%",name)
                .replace("%url%",url));
    }
}
