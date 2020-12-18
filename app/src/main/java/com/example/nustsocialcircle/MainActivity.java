package com.example.nustsocialcircle;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivityTAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    private void viewC() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Log.d(TAG, "onChildAdded called");
                Log.d(TAG, "New user added with snap" + dataSnapshot);
                Log.d(TAG, "And string with " + s + "\n");


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Log.d(TAG, "onChildChanged called");
                Log.d(TAG, "child changed with snap" + dataSnapshot);
                Log.d(TAG, "And string with " + s + "\n");

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                Log.d(TAG, "onChildRemoved called");
                Log.d(TAG, "child removed with snap" + dataSnapshot);

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Log.d(TAG, "onChildMoved called");
                Log.d(TAG, "child moved with snap" + dataSnapshot);
                Log.d(TAG, "And string with " + s + "\n");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.d(TAG, "database error occured" + databaseError);

            }
        });

    }

    private ArrayList<File> getAllImageFiles(File externalStorageDirectory) {

        ArrayList<File> b = new ArrayList<>();

        File[] files = externalStorageDirectory.listFiles();

        if (files != null) {

            for (int i = 0; i < files.length; i++) {

                if (files[i].isDirectory()) {

                    b.addAll(getAllImageFiles(files[i]));

                } else {

                    if (files[i].getName().endsWith(".jpg")) {

                        b.add(files[i]);
                    }
                }
            }

        } else {
            Toast.makeText(this, "No image in phone:)", Toast.LENGTH_SHORT).show();
        }


        return b;
    }


}
