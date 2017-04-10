package com.project.alertactivity_lasc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DB extends SQLiteOpenHelper{
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "alertactivity";

    // Table Name
    private static final String TABLE_MEMBER = "login";

    public DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

        db.execSQL("CREATE TABLE " + TABLE_MEMBER +
                "(User TEXT(100)," +
                " Pass TEXT(100)," +
                " news TEXT(10));");

        Log.d("CREATE TABLE","Create Table Successfully.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

    // Insert Data
    public long WriteLogin(String strUser, String strPass) {
        // TODO Auto-generated method stub

        try {
            SQLiteDatabase db;
            db = this.getWritableDatabase(); // Write Data

            /**
             *  for API 11 and above
             SQLiteStatement insertCmd;
             String strSQL = "INSERT INTO " + TABLE_MEMBER
             + "(MemberID,Name,Tel) VALUES (?,?,?)";

             insertCmd = db.compileStatement(strSQL);
             insertCmd.bindString(1, strMemberID);
             insertCmd.bindString(2, strName);
             insertCmd.bindString(3, strTel);
             return insertCmd.executeInsert();
             */

            ContentValues Val = new ContentValues();
            Val.put("User", strUser);
            Val.put("Pass", strPass);
            Val.put("news", "0");

            long rows = db.insert(TABLE_MEMBER, null, Val);

            db.close();
            return rows; // return rows inserted.

        } catch (Exception e) {
            return -1;
        }

    }

    // Select Data
    public class sMembers {
        String _User, _Pass;

        // Set Value
        public void sUser(String vUser){
            this._User = vUser;
        }
        public void sPass(String vPass){
            this._Pass = vPass;
        }

        // Get Value
        public String gUser(){
            return _User;
        }
        public String gPass(){
            return _Pass;
        }
    }

    public class sNews {
        String _ID;

        // Set Value
        public void sID(String vID){
            this._ID = vID;
        }

        // Get Value
        public String gID(){
            return _ID;
        }
    }

    public List<sMembers> ReadLogin() {
        // TODO Auto-generated method stub

        try {
            List<sMembers> MemberList = new ArrayList<sMembers>();

            SQLiteDatabase db;
            db = this.getReadableDatabase(); // Read Data

            String strSQL = "SELECT  * FROM " + TABLE_MEMBER;
            Cursor cursor = db.rawQuery(strSQL, null);

            if(cursor != null)
            {
                if (cursor.moveToFirst()) {
                    do {
                        sMembers cMember = new sMembers();
                        cMember.sUser(cursor.getString(0));
                        cMember.sPass(cursor.getString(1));
                        MemberList.add(cMember);
                    } while (cursor.moveToNext());
                }
            }
            cursor.close();
            db.close();
            return MemberList;

        } catch (Exception e) {
            return null;
        }
    }

    public List<sNews> ReadNews() {

        try {
            List<sNews> MemberList = new ArrayList<sNews>();

            SQLiteDatabase db;
            db = this.getReadableDatabase(); // Read Data

            String strSQL = "SELECT news FROM " + TABLE_MEMBER;
            Cursor cursor = db.rawQuery(strSQL, null);

            if(cursor != null)
            {
                if (cursor.moveToFirst()) {
                    do {
                        sNews cMember = new sNews();
                        cMember.sID(cursor.getString(0));
                        MemberList.add(cMember);
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            db.close();
            return MemberList;

        } catch (Exception e) {
            return null;
        }
    }

    // Update Data
    public long UpdateNews(String srtID) {
        // TODO Auto-generated method stub

        try {

            SQLiteDatabase db;
            db = this.getWritableDatabase(); // Write Data

            ContentValues Val = new ContentValues();
            Val.put("news", srtID);

            long rows = db.update(TABLE_MEMBER, Val,null,null);

            db.close();
            return rows; // return rows updated.

        } catch (Exception e) {
            return -1;
        }

    }

    // Delete Data
    public long DeleteLogin() {
        // TODO Auto-generated method stub

        try {

            SQLiteDatabase db;
            db = this.getWritableDatabase(); // Write Data
            long rows =  db.delete(TABLE_MEMBER,null,null);

            db.close();
            return rows; // return rows delete.

        } catch (Exception e) {
            return -1;
        }

    }
}

