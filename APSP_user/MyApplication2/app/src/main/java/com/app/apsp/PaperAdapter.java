package com.app.apsp;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.app.apsp.R;
import com.google.firebase.storage.StorageReference;

public class PaperAdapter extends ArrayAdapter<String> {
    StorageReference yearRef;
    private long downloadId;

    public PaperAdapter(@NonNull Context context, StorageReference yearsRef, int resource, @NonNull String[] objects) {
        super(context, resource, objects);
        this.yearRef = yearsRef;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_paper, parent, false);
        }

        String fileName = getItem(position);
        Button btnDownload = convertView.findViewById(R.id.btnDownload);

        // Set the button text to the file name (without extension)
        String buttonText = getFileNameWithoutExtension(fileName);
        btnDownload.setText(buttonText);

        btnDownload.setOnClickListener(v -> yearRef.child(fileName).getDownloadUrl().addOnSuccessListener(uri -> {
            DownloadManager downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
            request.setTitle(fileName);
            request.setDescription("Downloading " + fileName);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            downloadId = downloadManager.enqueue(request);
            Toast.makeText(getContext(), "Downloading...", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(exception -> Toast.makeText(getContext(), "Download failed", Toast.LENGTH_SHORT).show()));

// Handle opening the file after download is completed
        BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long receivedDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (receivedDownloadId == downloadId) {
                    // The correct download is complete
                    if (isDownloadSuccessful(downloadId)) {
                        openDownloadedFile(downloadId);
                    } else {
                        Log.e("fail", "Download failed or file is empty.");
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        getContext().registerReceiver(downloadReceiver, filter);

        return convertView;
    }

    private boolean isDownloadSuccessful(long downloadId) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);

        DownloadManager downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        Cursor cursor = downloadManager.query(query);
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            return status == DownloadManager.STATUS_SUCCESSFUL;
        }
        return false;
    }

    private void openDownloadedFile(long downloadId) {
        DownloadManager downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = downloadManager.getUriForDownloadedFile(downloadId);

        if (uri != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


            try {
                getContext().startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Log.e("error", "Error opening file: " + e.getMessage());
            }
        } else {
            Log.e("not found", "Downloaded file not found.");
        }
    }


    // Method to extract file name without extension
    private String getFileNameWithoutExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex != -1) {
            return fileName.substring(0, lastDotIndex);
        } else {
            return fileName;
        }
    }
}
