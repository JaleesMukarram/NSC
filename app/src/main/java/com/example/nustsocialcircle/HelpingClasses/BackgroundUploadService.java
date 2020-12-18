package com.example.nustsocialcircle.HelpingClasses;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.nustsocialcircle.FirebaseHelper.PostImageUploadTask;
import com.example.nustsocialcircle.Interfaces.DatabaseUploadListener;
import com.example.nustsocialcircle.R;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static com.example.nustsocialcircle.HelpingClasses.MainApplication.NOTIFICATION_CHANNEL_LOW;

public class BackgroundUploadService extends Service {


    public static final int ID_UPLOAD_GENERAL_USER_IMAGE_POSTS = 100;
    private static final String TAG = "BackUploadService";
    private static final int MAX_PROGRESS = 100;

    final NotificationCompat.Builder ImagePostNotification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_LOW)
            .setSmallIcon(R.drawable.ic_file_upload_primary_48)
            .setContentText("Uploading...")
            .setContentTitle("Uploading pics")
            .setProgress(MAX_PROGRESS, 0, false);

    private List<PostImageUploadTask> list;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {

            list = (List<PostImageUploadTask>) intent.getSerializableExtra("ARRAY");

            if (list.size() > 0) {

                ImagePostUploader uploader = new ImagePostUploader();
                uploader.startExecution();

                startForeground(ID_UPLOAD_GENERAL_USER_IMAGE_POSTS, ImagePostNotification.build());

                Log.d(TAG, "Foreground started with size of upload: " + list.size());

            } else {

                Log.d(TAG, "Empty task provided");
            }


        } catch (Exception ex) {

            Log.d(TAG, "Foreground Exception: " + ex);
        }

        return START_STICKY;
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service Destroyed");

    }


    private class ImagePostUploader implements DatabaseUploadListener {

        private Queue queue;

        private ImagePostUploader() {

            this.queue = new LinkedList<PostImageUploadTask>();
            this.addAllElementsToQueue();

        }

        private void addToQueue() {

        }

        private void startExecution() {

            removeAndExecute();

        }

        private void removeAndExecute() {

            if (queueSize() > 0) {

                PostImageUploadTask task = (PostImageUploadTask) queue.remove();
                task.setListener(this);
                task.execute();

                Log.d(TAG, "Asked to start uploading to storage for task: " + getNumberOfObjectProcessed());


            } else {

                stopSelf();
                Log.d(TAG, "Completed All");
            }

        }

        private int queueSize() {
            return queue.size();
        }

        private int getNumberOfObjectProcessed() {

            return (list.size() - queue.size());

        }

        private int getNumberOfObjectProcessingRemaining() {

            return queue.size();

        }

        private void addAllElementsToQueue() {

            queue.addAll(list);

        }

        @Override
        public void listenToProgress(int progress) {

            Log.d(TAG, "Progress: " + progress);

            ImagePostNotification.setContentTitle("Uploading Images")
                    .setContentText("uploading image " + getNumberOfObjectProcessed() + " out of " + list.size())
                    .setProgress(MAX_PROGRESS, progress, false);

            startForeground(ID_UPLOAD_GENERAL_USER_IMAGE_POSTS, ImagePostNotification.build());

        }

        @Override
        public void onCompleteFailure() {

            Log.d(TAG, "Task failed");

        }

        @Override
        public void onTaskCompleted() {

            Log.d(TAG, "Task completed");
            removeAndExecute();

        }
    }

}
