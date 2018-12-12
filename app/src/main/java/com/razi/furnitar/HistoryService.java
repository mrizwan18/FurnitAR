package com.razi.furnitar;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class HistoryService extends IntentService {


    public HistoryService() {
        super("HistoryService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String word = intent.getStringExtra("purchase");
        if(! isExternalStorageMountable()){
            Log.d("kk", "onHandleIntent: External Storage Not Mountable");
            return;
        }
        File folder = getStorageDir("FurnitAR");
        File file = new File(folder + "/Purchase History.txt");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream fileinput = new FileOutputStream(file, true);
            PrintStream printstream = new PrintStream(fileinput);
            printstream.print(word+"\n");
            fileinput.close();
        }
        catch (Exception e)
        {
            Log.e("kk", "onHandleIntent: " + e.getMessage(), e);
        }
    }

    private boolean isExternalStorageMountable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public File getStorageDir(String fileName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), fileName);
        if (!file.mkdirs()) {
            Log.e("kk", "Directory not created");
        }
        return file;
    }


}
