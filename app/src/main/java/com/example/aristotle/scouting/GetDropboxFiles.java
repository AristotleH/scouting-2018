package com.example.aristotle.scouting;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class GetDropboxFiles extends AsyncTask<FileMetadata, Void, File> {

    private Exception ex;
    private final String ACCESS_TOKEN = "";
    private final Context c;

    public GetDropboxFiles(final Context context) {
        c = context;
    }

    protected File doInBackground(FileMetadata... params) {
        DbxRequestConfig config = new DbxRequestConfig("Scouting2018/0.1", "en_US");
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
        System.out.println("first");
        File file = null;
        try {
            File path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
            file = new File(path, "matches.txt");//metadata.getName());
            file.delete();
            file = new File(path, "matches.txt");

            OutputStream outputStream = new FileOutputStream(file);
            FileMetadata metadata = client.files().downloadBuilder("/matches.txt").download(outputStream);
            return file;
        } catch (DbxException | IOException e) {
            ex = e;
            System.out.println("this doesn't work (probably because there's no access token): " + e);
        }

        return file;
    }

    protected void onPostExecute(MatchViewActivity feed) {
    }
}