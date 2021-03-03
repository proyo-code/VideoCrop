package net.vrgsoft.videocrop;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import net.vrgsoft.videcrop.VideoCropActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.os.Environment.getExternalStoragePublicDirectory;


public class MainActivity extends AppCompatActivity {
    private static final int CROP_REQUEST = 200;
    private File mSnapFile;
    private ImageView mPreviewImage;
    private VideoView mPreviewVideo;
    private String folder = null;
    String x;
    TextView mtxt;

    private static final String TAG = "MainActivity";
    private List<String> mPath;
    private Button button;
    private boolean withTimeStamp = true;
    private static final String SNAP_NAME = "snap";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPreviewVideo = findViewById(R.id.video_preview);


        mtxt = findViewById(R.id.textView5);

        mPreviewVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        findViewById(R.id.snap_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMediaFromGallery("video/*",0);
            }
        });


    }
    private void selectMediaFromGallery(String mimeType, int resultCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mimeType);
        startActivityForResult(intent, resultCode);
    }

    private void cropImage(Uri sourceUri) {
        startActivityForResult(VideoCropActivity.createIntent(this, sourceUri.toString(), getImageFile().toString()), CROP_REQUEST);

        x = getImageFile().getPath();
        mtxt.setText(x);


    }
    protected Uri getImageFile()
    {
//        String imagePathStr = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" +  (folder == null ? Environment.DIRECTORY_DCIM + "/" + getApplication().getString(R.string.app_name) + "/" + getApplication().getString(R.string.nude): folder);
        String imagePathStr = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + (folder == null ? "/" + getString(R.string.snapbytes) + "/" + getString(R.string.normal) : folder);
        File path = new File(imagePathStr);
        if (!path.exists()) {
            path.mkdirs();
        }
        String finalPhotoName = "VID" +
                (withTimeStamp ? "_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date(System.currentTimeMillis())) :  "")
                + ".mp4";
        // long currentTimeMillis = System.currentTimeMillis();
        // String photoName = imageName + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date(currentTimeMillis)) + ".jpg";
        File photo = new File(path, finalPhotoName);

        return Uri.fromFile(photo);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                Uri selectedVideoUri = data.getData();

                // OI FILE Manager
                String filemanagerstring = selectedVideoUri.toString();

                mtxt.setText(filemanagerstring);

                // MEDIA GALLERY
                String selectedVideoPath = getPath(selectedVideoUri);
                if (selectedVideoPath != null) {
                    mtxt.setText(selectedVideoPath);
                    cropImage(Uri.parse(selectedVideoPath));
                }
            }
            if(requestCode == CROP_REQUEST){
                try {
                    mPreviewVideo.setVisibility(View.VISIBLE);
                    mPreviewVideo.setVideoURI(Uri.parse(x));
                    mPreviewVideo.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //this function returns null when using IO file manager
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Video.Media.DATA };
        getContentResolver();
        Cursor cursor = getApplication().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }
}
