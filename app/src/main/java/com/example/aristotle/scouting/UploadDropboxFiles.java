package com.example.aristotle.scouting;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DeleteErrorException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.Metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

class UploadDropboxFiles extends AsyncTask<FileMetadata, Void, File> {

    private Exception ex;
    private final String ACCESS_TOKEN = "";
    private String localFilename;
    private String dropboxFilename;

    UploadDropboxFiles(String localName, String dropboxName) {
        localFilename = localName;
        dropboxFilename = dropboxName;
    }

    protected File doInBackground(FileMetadata... params) {
        @SuppressWarnings("deprecation")
        DbxRequestConfig config = new DbxRequestConfig("Scouting2018/0.1", "en_US");
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
        System.out.println("first");
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, localFilename);
        try {
            @SuppressWarnings("unused")
            Metadata delete = client.files().delete("/" + dropboxFilename);

            //change this when dealing with multiple users

        } catch (DeleteErrorException e) {
            e.printStackTrace();
        } catch (DbxException e) {
            e.printStackTrace();
        }
        InputStream in = null;
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            @SuppressWarnings("unused")
            FileMetadata metadata = client.files().uploadBuilder("/" + dropboxFilename).uploadAndFinish(in);
        } catch (DbxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(MatchViewActivity feed) {
        // matches: check this.exception
        // matches: do something with the feed
    }
}