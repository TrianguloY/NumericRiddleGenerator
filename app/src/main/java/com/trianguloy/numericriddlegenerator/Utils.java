package com.trianguloy.numericriddlegenerator;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Utility functions (all static)
 */

public class Utils {

    //adapted from http://stackoverflow.com/questions/12910503/read-file-as-string
    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile(Context cntx, File fl) {
        FileInputStream fin = null;
        String ret = cntx.getString(R.string.error);
        try {
            fin = new FileInputStream(fl);
            ret = convertStreamToString(fin);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //Make sure you close all streams.
            if(fin!=null){
                try {
                    fin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return ret;
    }

    public static String getRawResource(Context cntx, int resourceId){
        InputStream in = cntx.getResources().openRawResource(resourceId);

        try {
            return convertStreamToString(in);
        } catch (Exception e) {
            e.printStackTrace();
            return cntx.getString(R.string.error);
        }finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void showMessage(Context cntx, String title, String message, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(cntx)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok",listener)
        .show();
    }

    public static void showMessageDontShowAgain(Context cntx, String title, String message, DialogInterface.OnClickListener listenerDontShowAgain) {
        new AlertDialog.Builder(cntx)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", null)
                .setNegativeButton(cntx.getString(R.string.dontShowAgain),listenerDontShowAgain)
        .show();
    }

    public static void createFileIfNecessary(File file, String content) {
        if(!file.exists()) {
            try {
                file.createNewFile();
                FileWriter fw = new FileWriter(file);
                fw.write(content);
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //adapted from https://stackoverflow.com/questions/3382996/how-to-unzip-files-programmatically-in-android
    public static void copyDemo(Context cntx, File folder) {
        ZipInputStream zis;
        try
        {
            String filename;
            zis = new ZipInputStream(new BufferedInputStream(cntx.getResources().openRawResource(R.raw.demozip)));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;

            while ((ze = zis.getNextEntry()) != null)
            {
                filename = ze.getName();

                // Need to create directories if not exists, or
                // it will generate an Exception...
                File fmd = new File(folder, filename);
                if (ze.isDirectory()) {
                    fmd.mkdirs();
                    continue;
                }

                FileOutputStream fout = new FileOutputStream(fmd);

                while ((count = zis.read(buffer)) != -1)
                {
                    fout.write(buffer, 0, count);
                }

                fout.close();
                zis.closeEntry();
            }

            zis.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

    }
}
