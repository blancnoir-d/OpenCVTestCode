package com.example.saeha.filtertest;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends AppCompatActivity {

    private final static int RESULT_CAMERA = 1001;
    private final static int RESULT_CAMERA_SMALL = 1000;
    private final static int REQUEST_TAKE_ALBUM = 1002;
    private final static int REQUEST_PERMISSION = 2001;
    private final static int REQUEST_IMAGE_CROP = 2002;


    private Uri cameraUri, albumUri;

    private String mImageFileLocation;
    private File cameraFolder;
    private File photoFile;

    private ImageView imageView;
    private Button camera;
    private Button camera2;
    private Button album;
    private boolean cameraCheck = false;
    private boolean albumCheck = false;

    Button cameraBtn;
    Button albumBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraBtn = (Button) findViewById(R.id.camera);
        albumBtn = (Button) findViewById(R.id.album);

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23) {
                    cameraCheck = true;
                    checkPermission();
                } else {
                    //카메라 실행

                    cameraIntent();
                }

            }
        });

        albumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //버전 체크
                //23이상이면 권한 체크
                if (Build.VERSION.SDK_INT >= 23) {
                    albumCheck = true;
                    checkPermission();
                } else {
                    //앨범 실행
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    startActivityForResult(intent, REQUEST_TAKE_ALBUM);
                }


            }
        });


    }


    /**
     * 권한 체크, 요청 부분 Start
     **/

    // Runtime Permission check
    private void checkPermission() {
        //권한을 체크해봄니다
        int permissionCamera = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        int permissionWrite = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionREAD = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);

        Log.d("권한 체크 :", " 카메라" + permissionCamera + " /쓰기 " + permissionWrite + "/읽기 " + permissionREAD);

        // 어느것 하나라도 권한이 안되어 있으면
        if (permissionCamera == PackageManager.PERMISSION_DENIED || permissionWrite == PackageManager.PERMISSION_DENIED || permissionREAD == PackageManager.PERMISSION_DENIED) {
            //권한 요청하고
            requestLocationPermission();

            //권한이 있으면
        } else {
            //앨범 버튼을 누르고 권한체크한 경우
            if (albumCheck) {
                //앨범 실행
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, REQUEST_TAKE_ALBUM);
            }
            //카메라 버튼을 누르고 권한체크한 경우
            if(cameraCheck){
                cameraIntent();
            }
            /*//썸네일이거나 아니거나
            if(!small){
                cameraIntent();
            }else{
                dispatchTakePictureIntent();
            }*/

        }
    }

    //권한 요청하는 곳
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_PERMISSION);

    }


    //권한 요청의 선택(허가, 거부) 값이 이쪽으로 옴.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    int permissionCamera = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
                    int permissionWrite = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    int permissionREAD = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);

                    Log.d("권한 체크 허가후 :", " 카메라" + permissionCamera + " /쓰기 " + permissionWrite + "/읽기 " + permissionREAD);

                    if (albumCheck) {
                        //앨범 실행
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                        startActivityForResult(intent, REQUEST_TAKE_ALBUM);
                    }

                    //카메라 버튼을 누르고 권한체크한 경우
                    if(cameraCheck){
                        cameraIntent();
                    }

                    //썸네일이거나 아니거나
                    /*if (!small) {
                        cameraIntent();
                    } else {
                        dispatchTakePictureIntent();
                    }*/
                } else {

                }
        }

    } /**권한 체크, 요청 부분 End **/



// 파일생성
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//1
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );

//2
        cameraFolder = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "IMG");

        //mkdirs()는 boolean값 과 연관..
        boolean isDirectoryCreated = cameraFolder.exists();
        Log.d("디렉토리 여부", " " + isDirectoryCreated);
        if (!isDirectoryCreated) {
            isDirectoryCreated = cameraFolder.mkdirs();
        }

        File image = File.createTempFile(imageFileName,".jpg",cameraFolder);

        // Save a file: path for use with ACTION_VIEW intents
        mImageFileLocation = image.getAbsolutePath();
        Log.d("이미지 경로 : "," "+mImageFileLocation);
        return image;
    }


    // 썸내일용 intent
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, RESULT_CAMERA_SMALL);
        }
    }

    //빅사이즈 intent
    private void cameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
           photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            Log.d("포토파일 확인"," "+photoFile);

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID+".provider",
                        photoFile);
                Log.d("intent전달Uri"," provider"+photoURI);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, RESULT_CAMERA);
            }
        }
    }



    //사진 찍고나서 오는 곳
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case RESULT_CAMERA:
                galleryAddPic();
//                Bundle extras = data.getExtras();
//                Bitmap imageBitmap = (Bitmap) extras.get("data");
//                imageView.setImageBitmap(imageBitmap);

//              안됌  Uri myUri = Uri.parse("content://media/external/images/media/3037");
//                imageView.setImageURI(myUri);

                //setPic();
                Intent gotoFilterCamera = new Intent(MainActivity.this, Filter.class);
                gotoFilterCamera.putExtra("imagePath",mImageFileLocation);
                startActivity(gotoFilterCamera);
                //finish();

                //CameracropImage();

                break;
            //썸네일 경우
            case RESULT_CAMERA_SMALL:
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imageView.setImageBitmap(imageBitmap);
                break;

            case REQUEST_TAKE_ALBUM:

                cameraUri = data.getData();
                Log.d("앨범 선택 URI"," "+cameraUri);
//                얘도 안됌 imageView.setImageURI(cameraUri);

                File albumFile2 = null;
                try {
                    albumFile2 = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(albumFile2 != null){
                    albumUri = Uri.fromFile(albumFile2);
                }
                cameraUri = data.getData();
                cropImage();
                break;

            case REQUEST_IMAGE_CROP:
                galleryAddPic();
                //크롭한 이미지를 FilterActivity로 전달
                Intent gotoFilter = new Intent(MainActivity.this, Filter.class);
                gotoFilter.putExtra("imagePath",mImageFileLocation);
                startActivity(gotoFilter);
                finish();
                break;
        }
    }


    //동기화
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mImageFileLocation);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mImageFileLocation, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mImageFileLocation, bmOptions);
        imageView.setImageBitmap(bitmap);
    }




    private void cropImage() {
        Log.i("cropImage", "Call");
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(cameraUri, "image/*");
        cropIntent.putExtra("scale", true);
        cropIntent.putExtra("output", albumUri);
        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
    }


    private void CameracropImage() {
        final int width  = 400;
        final int height = 200;

        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");

            cropIntent.setDataAndType(cameraUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 2);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", width);
            cropIntent.putExtra("outputY", height);

            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);

        }catch (ActivityNotFoundException a) {
            Log.e("Activity Not Found",""+a.toString());
        }
    }

}
