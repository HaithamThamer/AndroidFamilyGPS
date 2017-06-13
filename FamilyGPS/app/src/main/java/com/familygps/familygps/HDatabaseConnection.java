package com.familygps.familygps;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;



public class HDatabaseConnection {

    private String dbName = "data.db";//أسم قاعدة البيانات
    private String dbPath;//مسار قاعدة البيانات
    private SQLiteDatabase db; // قاعدة البيانات
    private Context context;//الواجهة الرئيسية
    private boolean isExists;// التحقق من وجود قاعدة البيانات
    private File dbFile;//ملف قاعدة البيانات
    //المشيد لأععطاء القيم الاولية للخصائص
    public HDatabaseConnection(Context context){
        this.context = context;
        dbPath = context.getApplicationInfo().dataDir+"/"; // مسار قاعدة البيانات
        dbFile = new File(dbPath+dbName);// أسم قاعدة البيانات
        isExists = dbFile.exists();// التحقق من وجود قاعدة البيانات

        //الكود التحقق ان كانت قاعدة البيانات مضافة مسبقا
        if (!isExists){
            try{
                InputStream inputStream = context.getAssets().open(dbName);// قرائة قاعدة البيانات من التطبيق
                OutputStream outputStream = new FileOutputStream(dbFile); // متغير لكتابة قاعدة البيانات
                int length;
                byte[] buffer = new byte[1024];
                // قرائة كيلو بايت كيلو بايت الى متغير الاخراج
                while((length = inputStream.read(buffer)) > 0){
                    outputStream.write(buffer,0,length);
                }
                outputStream.flush(); // تحديث لمتغير الاخراج
                outputStream.close();// اغلاق متغير الاخراج
                inputStream.close(); // اغلاق متغير الادخال
            }catch (IOException e){
                String msg = e.getMessage();
            }
            // التحقق ان كانت قاعدة البيانات مستخرج
            isExists = dbFile.exists();
        }
    }
    // فتح الاتصاال مع قاعدة البيانات
    private boolean openConnection(){
        if (isExists){
            db = SQLiteDatabase.openDatabase(dbPath+dbName,null,SQLiteDatabase.OPEN_READWRITE);
            return db.isOpen();
        }
        return false;
    }
    private boolean closeConnection(){
        if (isExists && db.isOpen()){
            db.close();
            return db.isOpen();
        }
        return false;
    }
    public boolean isPasswordSetted(){
        boolean isSetted = false;
        if (openConnection()){
            Cursor cursor = db.rawQuery("select tbl_info.value from tbl_info where tbl_info.name = 'password';",null);
            cursor.moveToFirst();
            if (!cursor.isAfterLast()){
                String s = cursor.getString(0);
                isSetted = s != null;
            }
            closeConnection();
        }
        return isSetted;
    }
    public boolean setPassword(String password){
        boolean done = false;
        if (openConnection()){
            db.execSQL("update tbl_info set tbl_info.value = '"+password+"' where tbl_info.name = 'password';",null);
            closeConnection();
            done = true;
        }
        return done;
    }
    public ArrayList<Number> getNumbers(){
        ArrayList<Number> numbers = new ArrayList<>();
        if (openConnection()){
            Cursor cursor = db.rawQuery("select tbl_numbers.id,tbl_numbers.number,tbl_numbers.isSaveMode from tbl_numbers",null);
            if (cursor.getCount() > 0){
                cursor.moveToFirst();
                while (!cursor.isAfterLast()){
                    numbers.add(new Number(cursor.getLong(0),cursor.getString(1),cursor.getInt(2) == 1 ? true : false));
                    cursor.moveToNext();
                }
            }
            closeConnection();
        }
        return numbers;
    }
    public boolean addNumber(String number){
        if (openConnection()){
            db.execSQL("insert into tbl_numbers (number) values ('"+number+"');");
            closeConnection();
            return true;
        }
        return true;
    }
    public boolean isNumber(String number){
        boolean isFound = false;
        Log.e("isNumber","111");
        if (openConnection()){
            Log.e("isNumber",number.length()+"");
            Cursor cursor = db.rawQuery("select count(tbl_numbers.number) from tbl_numbers where tbl_numbers.number = '"+number+"';",null);
            Log.e("isNumber","333");
            cursor.moveToFirst();
            Log.e("isNumber",cursor.getInt(0)+"");
            isFound = cursor.getInt(0)>= 1;
            closeConnection();
        }
        Log.e("isNumber",isFound+"");
        return isFound;
    }
    public boolean removeNumber(String number){
        boolean isRemoved = false;
        Log.e("database","111");
        if (openConnection()){
            Log.e("database","222");
            String sql = "delete from tbl_numbers where tbl_numbers.number = '"+number+"';";
            db.execSQL(sql);
            Log.e("database",sql);
            closeConnection();
            Log.e("database","444");
            isRemoved = true;
        }
        return isRemoved;
    }
    public void saveMode(int id,int state){
        if (openConnection()){
            db.execSQL("update tbl_numbers set isSaveMode = '"+state+"' where tbl_numbers.id = '"+id+"';");
            closeConnection();
        }
    }
    public ArrayList<Number> getNumberInSaveMode(){
        ArrayList<Number> numbers = this.getNumbers();
        for (int i = 0; i < numbers.size(); i++){
            if (!numbers.get(i).isSaveMode)
                numbers.remove(i);
        }
        return numbers;
    }
}
