package com.mcal.uidesigner.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Utils {
    @Nullable
    public static String getRealFileNameFromUri(Context context, Uri uri) {
        String path = getRealPathFromUri(context, uri);
        if (path == null) {
            return null;
        }
        return path.substring(path.lastIndexOf("/") + 1, path.length());
    }

    @Nullable
    public static String getRealPathFromUri(Context context, Uri uri) {
        if ("content".equals(uri.getScheme())) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{"_data"}, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow("_data");
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else if ("file".equals(uri.getScheme())) {
            return uri.getPath();
        } else {
            return null;
        }
    }

    public static long transfer(@NonNull InputStream in, OutputStream out) throws IOException {
        long size = 0;
        byte[] buf = new byte[1000];
        while (true) {
            int c = in.read(buf);
            if (c != -1) {
                size += (long) c;
                out.write(buf, 0, c);
            } else {
                in.close();
                out.close();
                return size;
            }
        }
    }

    @NonNull
    public static String suggestNewLayoutName(String resDirPath) {
        File layoutDir = new File(resDirPath, "layout");
        layoutDir.mkdirs();
        int i = 1;
        while (true) {
            int i2 = i + 1;
            File file = new File(layoutDir, "layout" + i + ".xml");
            if (!file.exists()) {
                return file.getName();
            }
            i = i2;
        }
    }

    @Nullable
    public static String createNewLayoutFile(String resDirPath, String name, String content) {
        try {
            File layoutDir = new File(resDirPath, "layout");
            layoutDir.mkdirs();
            if (name == null || name.trim().length() == 0) {
                name = suggestNewLayoutName(resDirPath);
            }
            File file = new File(layoutDir, name);
            if (file.exists()) {
                file = new File(layoutDir, suggestNewLayoutName(resDirPath));
            }
            FileWriter writer = new FileWriter(file);
            writer.write(content);
            writer.close();
            return file.getPath();
        } catch (IOException e) {
            return null;
        }
    }

    @NonNull
    public static String readFileAsString(String filePath) {
        try {
            StringBuilder fileData = new StringBuilder();
            BufferedReader reader = new BufferedReader(
                    new FileReader(filePath));
            char[] buf = new char[1024];
            int numRead = 0;
            while ((numRead = reader.read(buf)) != -1) {
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
            }
            reader.close();
            return fileData.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String createNewLayoutFile(String resDirPath, String name) {
        return createNewLayoutFile(resDirPath, name, "");
    }

    @NonNull
    public static String getDefaultResDirPath() {
        return getSDCardPath() + "/AppProjects/Designs/res";
    }

    @NonNull
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    @NonNull
    public static List<File> findLayoutFiles(String resDirPath) {
        List<File> layoutFiles = new ArrayList<>();
        File[] allFiles = new File(resDirPath, "layout").listFiles();
        if (allFiles != null) {
            for (File f : allFiles) {
                if (f.getName().endsWith(".xml")) {
                    layoutFiles.add(f);
                }
            }
        }
        Collections.sort(layoutFiles, new Comparator<File>() {
            public int compare(File lhs, File rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });
        return layoutFiles;
    }

    public static String chooseLayoutOrCreateNew(String resDirPath) {
        File[] allFiles = new File(resDirPath, "layout").listFiles();
        if (allFiles != null) {
            for (File f : allFiles) {
                if (f.getName().endsWith(".xml")) {
                    return f.getPath();
                }
            }
        }
        return createNewLayoutFile(resDirPath, suggestNewLayoutName(resDirPath));
    }
}
