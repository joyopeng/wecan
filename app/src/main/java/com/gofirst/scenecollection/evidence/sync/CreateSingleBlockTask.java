package com.gofirst.scenecollection.evidence.sync;

import android.os.AsyncTask;

import com.gofirst.scenecollection.evidence.model.UploadFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 异步创建单个分割块
 *
 * @author maxiran
 */
public class CreateSingleBlockTask extends AsyncTask<UploadFile, Integer, String> {

    private String partPath;
    private int singleSize;
    private int currentCount;

    public CreateSingleBlockTask(String partPath, int singleSize, int currentCount) {
        this.partPath = partPath;
        this.singleSize = singleSize;
        this.currentCount = currentCount;
    }

    @Override
    protected String doInBackground(UploadFile... params) {
        try {
            File recFile = new File(params[0].getFilePath());
            RandomAccessFile randomAccessFile = new RandomAccessFile(recFile, "rw");
            byte[] buffer = new byte[singleSize];
            randomAccessFile.seek(currentCount * singleSize);
            int byteCount = randomAccessFile.read(buffer);
            FileOutputStream fileOutputStream = new FileOutputStream(partPath);
            fileOutputStream.write(buffer, 0, byteCount);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
