package com.mtsahakis.mediaprojectiondemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;


public class ScreenCaptureImageActivity extends Activity {
     ImageView img;
    private MediaProjectionManager mProjectionManager;
    private static MediaProjection sMediaProjection;

    private static final String TAG = ScreenCaptureImageActivity.class.getName();
    private static final int REQUEST_CODE = 100;
    private static final int REQUEST_CODE2 = 200;


    Boolean isOneImage;

    /****************************************** Activity Lifecycle methods ************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // call for the projection manager
        img= findViewById(R.id.img_screenshot);
        mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        AppUtil.mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        Button startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                img.setImageResource(R.color.macdinh);
                isOneImage=true;
                startProjection();

            }
        });

        Button startButton2 = (Button) findViewById(R.id.startButton2);
        startButton2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                isOneImage=false;
                img.setImageResource(R.color.macdinh);
                startProjection();

            }
        });

        Button startButton3 = (Button) findViewById(R.id.startButton3);
        startButton3.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                isOneImage=true;
                img.setImageResource(R.color.macdinh);
                startProjection2();

            }
        });

        Button startButton4 = (Button) findViewById(R.id.startButton4);
        startButton4.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                isOneImage=false;
                img.setImageResource(R.color.macdinh);
                startProjection2();

            }
        });


    }
        private void startProjection() {
        startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
    }

    private void startProjection2() {
        if(AppUtil.data==null){
            startActivityForResult(AppUtil.mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE2);
        }else {
            new ScreenShotUtil(ScreenCaptureImageActivity.this,getWindowManager(),img, isOneImage);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            sMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
            new ScreenShot(ScreenCaptureImageActivity.this,getWindowManager(),sMediaProjection,img, isOneImage);
        }else if(requestCode == REQUEST_CODE2){
            AppUtil.resultCode=resultCode;
            AppUtil.data=data;
            new ScreenShotUtil(ScreenCaptureImageActivity.this,getWindowManager(),img, isOneImage);

        }
    }









    /****************************************** UI Widget Callbacks *******************************/

//    private  void init(){
//        DisplayMetrics metrics = getResources().getDisplayMetrics();
//        mDensity = metrics.densityDpi;
//        mDisplay = getWindowManager().getDefaultDisplay();
//
//        createVirtualDisplay();
//
//        sMediaProjection.registerCallback(new MediaProjectionStopCallback(), mHandler);
//    }
//    private void startProjection() {
//        startedActivity=false;
//        startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
//    }
//
//    private void stopProjection() {
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                if (sMediaProjection != null) {
//                    sMediaProjection.stop();
//                }
//            }
//        });
//    }
//
//    /****************************************** Factoring Virtual Display creation ****************/
//    private void createVirtualDisplay() {
//        // get width and height
//        Point size = new Point();
//        mDisplay.getRealSize(size);
//        mWidth = size.x;
//        mHeight = size.y;
//
//        // start capture reader
//        mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
//        mVirtualDisplay = sMediaProjection.createVirtualDisplay(SCREENCAP_NAME, mWidth, mHeight, mDensity, VIRTUAL_DISPLAY_FLAGS, mImageReader.getSurface(), null, mHandler);
//        mImageReader.setOnImageAvailableListener(new ImageAvailableListener(), mHandler);
//    }
//
//
//
//
//    private class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
//        @Override
//        public void onImageAvailable(ImageReader reader) {
//
//            if(startedActivity){
//                startedActivity=false;
//                return;
//
//            }
//            startedActivity=true;
//
//            Image image = null;
//            FileOutputStream fos = null;
//            Bitmap bitmap = null;
//
//
//            try {
//                image = reader.acquireLatestImage();
//                if (image != null) {
//                    Image.Plane[] planes = image.getPlanes();
//                    ByteBuffer buffer = planes[0].getBuffer();
//                    int pixelStride = planes[0].getPixelStride();
//                    int rowStride = planes[0].getRowStride();
//                    int rowPadding = rowStride - pixelStride * mWidth;
//
//                    // create bitmap
//                    bitmap = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight, Bitmap.Config.ARGB_8888);
//                    bitmap.copyPixelsFromBuffer(buffer);
//
//                    Log.e("abc","bitmap....");
//
//                    Bitmap imageBitmap = Bitmap.createBitmap(bitmap, 0,0,bitmap.getWidth(),bitmap.getHeight());
//                    img.setImageBitmap(imageBitmap);
//
//
//                    // write bitmap to a file
////                    fos = new FileOutputStream(STORE_DIRECTORY + "/myscreen_" + IMAGES_PRODUCED + ".png");
////                    bitmap.compress(CompressFormat.JPEG, 100, fos);
////
////                    IMAGES_PRODUCED++;
////                    Log.e(TAG, "captured image: " + IMAGES_PRODUCED);
//                    stopProjection();
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                //Toast.makeText(ScreenCaptureImageActivity.this, "Lỗi: "+e.getMessage(), Toast.LENGTH_SHORT).show();
//            } finally {
//                if (fos != null) {
//                    try {
//                        fos.close();
//                    } catch (IOException ioe) {
//                        ioe.printStackTrace();
//                    }
//                }
//
//                if (bitmap != null) {
//                    bitmap.recycle();
//                }
//
//                if (image != null) {
//                    image.close();
//                }
//            }
//        }
//    }
//
//
//    private class MediaProjectionStopCallback extends MediaProjection.Callback {
//        @Override
//        public void onStop() {
//            Log.e("ScreenCapture", "stopping projection.");
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    if (mVirtualDisplay != null) mVirtualDisplay.release();
//                    if (mImageReader != null) mImageReader.setOnImageAvailableListener(null, null);
//                  //  if (mOrientationChangeCallback != null) mOrientationChangeCallback.disable();
//                    sMediaProjection.unregisterCallback(MediaProjectionStopCallback.this);
//                }
//            });
//        }
//    }

}