package com.example.ee_drivefinal.Utils;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileHandler {


    public static void appendLog(String text)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        String timeYMD = formatter.format(date);
        File f = new File(Environment.getExternalStorageDirectory(), "EE-Drive");
        if (!f.exists()) {
            f.mkdirs();
        }
        File f1 = new File(Environment.getExternalStorageDirectory() + "/" + "EE-Drive", "Logs");
        if (!f1.exists()) {
            f1.mkdirs();
        }

        File logFile = new File(f1,"log-"+timeYMD+".txt");
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                FileHandler.appendLog(e.toString());
                e.printStackTrace();

            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            SimpleDateFormat formatter2 = new SimpleDateFormat("HH:mm:ss");
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            String timeHMS = formatter2.format(date);
            buf.append(timeHMS);
            buf.newLine();
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            FileHandler.appendLog(e.toString());
            e.printStackTrace();
        }
    }
}
