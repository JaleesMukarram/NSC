package com.example.nustsocialcircle.HelpingClasses;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GeneralFileHandling {

    private Context context;
    private ArrayList<File> fileArrayList;

    public GeneralFileHandling(Context context) {
        this.context = context;
    }

    private ArrayList<File> getAllImageFiles(File externalStorageDirectory) {

        ArrayList<File> b = new ArrayList<>();

        File[] files = externalStorageDirectory.listFiles();

        if (files != null) {

            for (File file : files) {

                if (file.isDirectory()) {

                    if (file.getName().contains(".")) {
                        continue;
                    }
                    b.addAll(getAllImageFiles(file));

                } else {

                    if (file.getName().endsWith(".jpg")) {

                        b.add(file);
                    }
                }
            }

        }


        return b;
    }

    private void sortImageFiles() {

        Collections.sort(fileArrayList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return Long.valueOf(o2.lastModified()).compareTo(o1.lastModified());
            }
        });
    }

    public ArrayList<File> getAllSortedImageFiles() {

        fileArrayList = getAllImageFiles(Environment.getExternalStorageDirectory());
        sortImageFiles();
        return fileArrayList;
    }

}
