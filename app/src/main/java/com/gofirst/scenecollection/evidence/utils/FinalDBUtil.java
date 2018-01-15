package com.gofirst.scenecollection.evidence.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2016/11/25.
 */
public class FinalDBUtil {


    public static boolean hasRows(SQLiteDatabase db,String sql){
        Cursor cursor = null;
        boolean hasRows = false;
        try{
            cursor = db.rawQuery(sql, null);
            hasRows = cursor.getCount()>0;
        }catch (Exception e) {
            e.printStackTrace();
        }
        finally{
            if(cursor!=null)
                cursor.close();
        }
        return hasRows;

    }



    private static void updateDataBaseVer1_2(SQLiteDatabase db){
        if(hasRows(db,"SELECT 'x' FROM sqlite_master where type='table' and name='User' ")
                && !hasRows(db,"SELECT 'x' FROM sqlite_master where type='table' and name='User' and tbl_name='test'")
                ){
            db.execSQL("ALTER TABLE User ADD COLUMN test TEXT");

        }
    }

}
