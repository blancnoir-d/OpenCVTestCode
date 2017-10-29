package com.example.saeha.filtertest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

public class Filter extends AppCompatActivity {
    String cropImgPath;
    ImageView originalImg;
    ImageView filterImg;
    Button filterTest;

    //Mat
    private Mat img_input;
    private Mat img_output;


    static {
        //추가 start
        System.loadLibrary("opencv_java3");
        //추가 end
        System.loadLibrary("native-lib");
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    //public native String stringFromJNI();
    public native void loadImage(String imageFileName, long img);
    public native void imageprocessing(long inputImage, long outputImage);
    public native void gray(long inputImage, long outputImage);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        Intent intent = getIntent();
        cropImgPath = intent.getStringExtra("imagePath");
        originalImg = (ImageView)findViewById(R.id.imageViewInput);
        filterImg = (ImageView)findViewById(R.id.imageViewOutput);
        filterTest = (Button)findViewById(R.id.filter);

        //이미지 로드
        read_image_file();
        imageprocess_and_showResult();
        filterTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageprocess_and_showResult2();
            }
        });
    }



    private void read_image_file() {
        // copyFile("photo0.jpg");
        //Mat BGRMat = Imgcodecs.imread (ResourcesCompat.getDrawable (getResources (), R.drawable.photo0, null) .toString ());
        img_input = new Mat();
        img_output = new Mat();

        //이미지 경로로 이미지 로드
        loadImage(cropImgPath, img_input.getNativeObjAddr());
    }

    private void imageprocess_and_showResult() {

        imageprocessing(img_input.getNativeObjAddr(), img_output.getNativeObjAddr());

        Bitmap bitmapInput = Bitmap.createBitmap(img_input.cols(), img_input.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img_input, bitmapInput);
        originalImg.setImageBitmap(bitmapInput);

        Bitmap bitmapOutput = Bitmap.createBitmap(img_output.cols(), img_output.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img_output, bitmapOutput);
        filterImg.setImageBitmap(bitmapOutput);
    }


    private void imageprocess_and_showResult2() {

        gray(img_input.getNativeObjAddr(), img_output.getNativeObjAddr());

        Bitmap bitmapInput = Bitmap.createBitmap(img_input.cols(), img_input.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img_input, bitmapInput);
        originalImg.setImageBitmap(bitmapInput);

        Bitmap bitmapOutput = Bitmap.createBitmap(img_output.cols(), img_output.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img_output, bitmapOutput);
        filterImg.setImageBitmap(bitmapOutput);
    }


    /*private void setPic() {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(cropImgPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(cropImgPath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }*/

}
