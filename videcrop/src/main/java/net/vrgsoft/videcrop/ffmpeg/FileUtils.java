package net.vrgsoft.videcrop.ffmpeg;

import android.content.Context;
import android.os.Build;

import androidx.core.graphics.drawable.DrawableCompat;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class FileUtils {
    private static final String FFMPEG_FILE_NAME = "ffmpeg";
    private static  File folder;
    static File getFFmpeg(Context context) {
        folder = context.getFilesDir();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            folder = new File(context.getApplicationInfo().nativeLibraryDir);
        }
        return new File(folder, FFMPEG_FILE_NAME);
    }

    static boolean inputStreamToFile(InputStream stream, File file) {
        try {
            InputStream input = new BufferedInputStream(stream);
            OutputStream output = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                output.write(buffer, 0, bytesRead);
            }
            output.flush();
            output.close();
            input.close();
            return true;
        } catch (IOException e) {
            Log.e("error while writing ff binary file", e);
        }
        return false;
    }
}