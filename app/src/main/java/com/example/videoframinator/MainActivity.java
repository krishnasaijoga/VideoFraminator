package com.example.videoframinator;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.SeekBar;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoWriter;

import java.io.File;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    SeekBar sb_low_hue,sb_high_hue,sb_low_sat,sb_high_sat,sb_low_value,sb_high_value;
    float low_hue,high_hue=255,low_sat=0,high_sat=255,low_value=0,high_value=255;
    Mat img = null,hsv=null;
    static boolean camera = true;
    static boolean record = false;
    static List<Mat> recording;
    static double FPS;
    Time start,end;
    static Context mContext;
    static int count=0;


    private CameraBridgeViewBase mCvCamView;
    Mat matInput,matOutput;

    //TO LOAD OPENCV LIBRARY
    static {
        System.loadLibrary("opencv_java3");
        if(!OpenCVLoader.initDebug())
            Log.d("Main Activity","OpenCV not loaded");
        else Log.d("Main Activity","OpenCV loaded");

    }

    private BaseLoaderCallback mLoaderCallback2 = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status)
            {
                case LoaderCallbackInterface.SUCCESS:
                    mCvCamView.enableView();
                    back_sub();
                    break;
                default:super.onManagerConnected(status);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sb_low_hue = findViewById(R.id.hue_low_sb);
        sb_high_hue =findViewById(R.id.hue_high_sb);
        sb_low_sat = findViewById(R.id.sat_low_sb);
        sb_high_sat = findViewById(R.id.sat_high_sb);
        sb_low_value = findViewById(R.id.value_low_sb);
        sb_high_value = findViewById(R.id.value_high_sb);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mContext = this;

        mCvCamView = findViewById(R.id.live_cv);
        mCvCamView.setVisibility(SurfaceView.VISIBLE);
        mCvCamView.setCvCameraViewListener(this);
        mCvCamView.setCameraIndex(1); // front-camera(1),  back-camera(0)
        mLoaderCallback2.onManagerConnected(LoaderCallbackInterface.SUCCESS);

        FPS = 15;
        recording = new ArrayList<>();
//        mVideoView = findViewById(R.id.disp_vv);
//        capturedImageView = findViewById(R.id.img_disp_iv);

//        dispatchTakeVideoIntent();
//        vid_frames = new ArrayList<>();
//        iv_disp = findViewById(R.id.disp_iv);
//        mOpenCVCamView = findViewById(R.id.openCVCamView);
//        mOpenCVCamView.setVisibility(SurfaceView.VISIBLE);
//        mOpenCVCamView.setCvCameraViewListener((CameraBridgeViewBase.CvCameraViewListener) this);
//
//
//        mBaseLoaderCallback = new BaseLoaderCallback(this) {
//            @Override
//            public void onManagerConnected(int status) {
//                switch (status)
//                {
//                    case LoaderCallbackInterface.SUCCESS: {
//                        Log.d("OpenCV loaded", "Success");
//                        mOpenCVCamView.enableView();
//                    }
//                    break;
//                    default:{
//                        super.onManagerConnected(status);
//                    }
//                    break;
//                }
//            }
//        };
//
//        ActivityCompat.requestPermissions(this,PERMISSIONS_STORAGE,1);

    }

    @Override
    protected void onResume() {
        super.onResume();
        super.onResume();

        if (!OpenCVLoader.initDebug()) {
            Log.d(" ", "onResume :: Internal OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback2);
        } else {
            Log.d(" ", "onResume :: OpenCV library found inside package. Using it!");
            mLoaderCallback2.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCvCamView!=null)
            mCvCamView.disableView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCvCamView!=null)
            mCvCamView.disableView();
    }

//    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
//        @Override
//        public void onManagerConnected(int status) {
//            if (status == LoaderCallbackInterface.SUCCESS ) {
//                // now we can call opencv code !
////                try {
////                    Mat start = Utils.loadResource(mContext,R.raw.harry);
////                    Bitmap temp_bmp = Bitmap.createBitmap(start.cols(),start.rows(),Bitmap.Config.ARGB_8888);
////                    Utils.matToBitmap(start,temp_bmp);
////                    iv_disp.setImageBitmap(temp_bmp);
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
//                back_sub();
//            } else {
//                super.onManagerConnected(status);
//            }
//        }
//    };


    @Override
    protected void onStart() {
        super.onStart();
        //OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0,this,mLoaderCallback);
        //deFrameVideo("android.resource://"+getPackageName()+"/raw/fight");

        //back_sub();
//        VideoCapture vc = new VideoCapture();
//        vc.open("android.resource://"+getPackageName()+"/raw/fight");
//        if(vc.isOpened())
//            Log.d("Opened "," True");
//        else Log.d("Opened "," False");

    }

    private void back_sub() {
        try {
                //SEEKBAR TO SET LOW FOR HUE
                sb_low_hue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        low_hue = (float) ((float)progress*2.55);
//                        Log.d("HUE VALUE ",""+low_hue);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        //startChanges();
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        //setChanges();
                    }
                });

                //SEEKBAR TO SET HIGH FOR HUE
                sb_high_hue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        high_hue = (float) ((float)progress*2.55);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        //startChanges();
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        //setChanges();
                    }
                });

                //SEEKBAR TO SET LOW FOR SATURATION
                sb_low_sat.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        low_sat = (float) ((float)progress*2.55);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        //startChanges();
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        //setChanges();
                    }
                });

                //SEEKBAR TO SET HIGH FOR SATURATION
                sb_high_sat.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        high_sat = (float) ((float)progress*2.55);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        //startChanges();
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        //setChanges();
                    }
                });

                //SEEKBAR TO SET LOW FOR VALUE
                sb_low_value.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        low_value = (float) ((float)progress*2.55);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        //startChanges();
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        //setChanges();
                    }
                });

                //SEEKBAR TO SET HIGH FOR VALUE
                sb_high_value.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        high_value = (float) ((float)progress*2.55);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        //startChanges();
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        //setChanges();
                    }
                });
                //vd.release();
//                }
            } catch (NullPointerException ne) {
                ne.printStackTrace();
            } catch (CvException ce) {
                ce.printStackTrace();
            }
    }



//    private void startChanges()
//    {
//        try {
////            String path = Environment.getExternalStorageDirectory().getPath()+"/sdcard/android/lion";// TODO : Try to open a video using VideoCapture Or shift to using FFmpeg
////            VideoCapture vd = new VideoCapture(path);
////            if(!vd.isOpened())
////                Log.d("Camera Opened ","FALSE");
////            Mat vidFrame = new Mat();
////            vd.read(vidFrame);
//            //img = Utils.loadResource(mContext, R.raw.harry);
//
//            hsv = new Mat(matInput.rows(),matInput.cols(),matInput.type());
//            Imgproc.cvtColor(matInput, hsv, Imgproc.COLOR_RGB2HSV);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void setChanges()
//    {
//        Scalar lower = new Scalar(low_hue, low_sat, low_value);
//        Scalar upper = new Scalar(high_hue, high_sat, high_value);
//        //Log.d("TYPE OF IMG ",""+img.type());
//        Mat mask = new Mat(hsv.size(), hsv.type());
//        Mat mask_inv = new Mat(hsv.size(), hsv.type());
//        Core.inRange(hsv, lower, upper, mask);
//        Mat res = new Mat(matInput.rows(),matInput.cols(),matInput.type());
//        Core.bitwise_not(mask,mask_inv);
//        Core.bitwise_and(matInput, matInput, res, mask_inv);
//        Bitmap bmp = Bitmap.createBitmap(res.cols(), res.rows(), Bitmap.Config.ARGB_8888);
//        Mat result = new Mat(matInput.rows(),matInput.cols(),matInput.type());
//        //Imgproc.cvtColor(res,result,Imgproc.COLOR_BGR2RGB);
//        Utils.matToBitmap(res, bmp);
//        res.copyTo(matInput);
//        //iv_disp.setImageBitmap(bmp);
//    }

//    @RequiresApi(api = Build.VERSION_CODES.M)
//    private void deFrameVideo(String path)
//    {
//        Uri uri = Uri.parse(path);
//        //Log.d("path = ",uri+"");
//        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//        String video_duration="";
//        String frame_rate="";
//        float frames;
//        try {
//            retriever.setDataSource(this,uri);
//            video_duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//            frame_rate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE);
//            //frames = (float)(Long.parseLong(video_duration)*Long.parseLong(frame_rate));
//            Log.d("Duration of video = ",video_duration);
//            Log.d("frame rate",""+frame_rate);
//            //Log.d("Total Number of Frames",""+frames);
//        }
//        catch (IllegalArgumentException iae)
//        {
//            Log.d("Status","NOPE");
//            iae.printStackTrace();
//        }
//
//
//        Bitmap bmpOriginal = retriever.getFrameAtTime(0);
//        int bmpVideoHeight = bmpOriginal.getHeight();
//        int bmpVideoWidth = bmpOriginal.getWidth();
//        byte [] lastSavedByteArray = new byte[0];
//
//        float factor = 20f;
//        int scaleWidth = (int) ( (float) bmpVideoWidth * factor );
//        int scaleHeight = (int) ( (float) bmpVideoHeight * factor );
//        int max = (int) Long.parseLong(video_duration);
//
//        for(int i=0;i<max;i++) {
//
//            bmpOriginal = retriever.getFrameAtTime(i*1000,MediaMetadataRetriever.OPTION_CLOSEST);
//            bmpVideoHeight = (bmpOriginal==null)?-1:bmpOriginal.getHeight();
//            bmpVideoWidth = (bmpOriginal==null)?-1:bmpOriginal.getWidth();
//            int byteCount = bmpVideoHeight*bmpVideoWidth*4;
//            ByteBuffer tempByteBuffer = ByteBuffer.allocate(byteCount);
//            if (bmpOriginal == null)
//                continue;
//            bmpOriginal.copyPixelsToBuffer(tempByteBuffer);
//            byte[] tempByteArray = tempByteBuffer.array();
//
//            if(!Arrays.equals(tempByteArray,lastSavedByteArray)) {
//
////                File outputfile = new File("/sdcard/android/", "fight_"+i+".jpeg");
////                OutputStream out = null;
////                try {
////                    out = new FileOutputStream(outputfile);
////                    //Log.d("FILE", "Found");
////                } catch (FileNotFoundException e) {
////                    e.printStackTrace();
////                }
//
//                Bitmap bmpScaledSize = Bitmap.createScaledBitmap(bmpOriginal, scaleWidth, scaleHeight, false);
//                vid_frames.add(bmpScaledSize);
//                //bmpScaledSize.compress(Bitmap.CompressFormat.PNG, 100, out);
//
////                try {
////                    assert out != null;
////                    //Log.d("IMAGE STATUS", "Successful");
////                    out.close();
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
//
//                lastSavedByteArray = tempByteArray;
//                Log.d("Frame num "," "+i);
//
//            }
//            Log.e("Time "," "+i);
//        }
//
//        //iv_disp.setImageBitmap(bmpOriginal);
//        retriever.release();
//    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        if (matOutput==null || matOutput.empty())
            matOutput = new Mat(height,width,CvType.CV_8UC4,new Scalar(0,0,0,0));

    }

    @Override
    public void onCameraViewStopped() {
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        matInput = inputFrame.rgba();   //  received frame as Mat object

        //  CameraBridgeViewBase by default works in landscape mode
        //  Here we are not converting it into potrait but the Mat object values are rotated anticlockwise for front camera
        Mat matT = matInput.t();//Transposes a Mat

        //  Flips a 2D array around vertical, horizontal, or both axes
        //  Flag to specify how to flip the array. 0 means flipping around the x-axis. Positive value (for example, 1) means flipping around y-axis. Negative value (for example, -1) means flipping around both axes
        if (camera)
            Core.flip(matInput.t(),matT,-1);// front-camera(-1),  back-camera(1)
        else Core.flip(matInput.t(),matT,1);// front-camera(-1),  back-camera(1)

        Imgproc.resize(matT,matInput,matInput.size());//    resize source according to given size and places it in destination

        try {
            hsv = new Mat(matInput.rows(),matInput.cols(),matInput.type());
            Imgproc.cvtColor(matInput, hsv, Imgproc.COLOR_RGB2HSV);//   converts source Mat to given colorspace covnversion code
            //here from RGB colorspace to HSV colorspace
        } catch (Exception e) {
            e.printStackTrace();
        }

        Scalar lower = new Scalar(low_hue, low_sat, low_value);//   creates scalar object with low hue, saturation and value
        Scalar upper = new Scalar(high_hue, high_sat, high_value);//    creates scalar object with high hue, saturation and value

        Mat mask = new Mat(hsv.size(), hsv.type());
        Mat mask_inv = new Mat(hsv.size(), hsv.type());

        Core.inRange(hsv, lower, upper, mask);//    Checks if array elements lie between the elements of two other arrays.
        //  For every element of a single-channel input array:
        //  dst(I)= lowerb(I)_0 <= src(I)_0 <= upperb(I)_0

        Core.bitwise_not(mask,mask_inv);//  applies bitwise not to src and stores result in destination

        Mat res = new Mat(matInput.rows(),matInput.cols(),matInput.type());
        Core.bitwise_and(matInput, matInput, res, mask_inv);//  Calculates the per-element bit-wise conjunction of two arrays or an array and a scalar
        //  dst(I) = src1(I) & src2(I) if mask(I) != 0

        //  Imgproc.cvtColor(res,result,Imgproc.COLOR_BGR2RGB);

        Bitmap bmp = Bitmap.createBitmap(res.cols(), res.rows(), Bitmap.Config.ARGB_8888);//    Creating a bitmap similar to Mat and with ARGB_8888 conguration
        Utils.matToBitmap(res, bmp);//  Converts a mat to bitmap
        res.copyTo(matOutput);//    copies result into matOutput

        if (record)
            recording.add(matOutput);
//        Log.d("size of recording ",""+recording.size());
//        if(matOutput!=null)
//            Log.d("MATOUTPUT ","NOT NULL");
        return matOutput;// matOutput is returned for display
    }

    public void change_cam(View view) {
        mCvCamView.disableView();   //  Before changing camera we have to first disable cam view
        if (camera)
            mCvCamView.setCameraIndex(0); // front-camera(1),  back-camera(0)
        else mCvCamView.setCameraIndex(1);
        mCvCamView.enableView();
        camera = !camera;
    }

    public void record_video(View view) {
        if (record) {
            WriteVideo writeVideo = new WriteVideo();
            writeVideo.execute();
        }
        record = !record;
    }

    public static class WriteVideo extends AsyncTask<String,String,String>
    {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(mContext,"Wait","Please wait for sometime");
        }

        @Override
        protected String doInBackground(String... strings) {
//            File sddir = Environment.getExternalStorageDirectory();
//            File vrdir = new File(sddir, "android");
//            File file = new File(vrdir, "video.avi");
                String filename = Environment.getExternalStorageDirectory().getAbsolutePath()+"/android/video"+count+".avi";
                count++;
                VideoWriter vw = new VideoWriter(filename,VideoWriter.fourcc('M','J','P','G'),FPS,recording.get(0).size());

                if (vw.isOpened()) {
                    int i = 0;
                    while (i < recording.size()) {
                        Mat x = recording.get(i);
                        vw.write(x);
                        i++;
                    }
                }
                vw.release();
                recording = new ArrayList<>();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
        }
    }
}