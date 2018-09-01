package com.example.hihi.kartyaolvaso;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import static com.example.hihi.kartyaolvaso.TimePreference.getHour;
import static com.example.hihi.kartyaolvaso.TimePreference.getMinute;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Camera mCamera;
    private CameraPreview mPreview;
    private TextView mFeedbackCustomer;
    private TextView mDateTimeView;
    private TextView mMealNameView;
    private ProgressBar mTimeProgressbar;
    private TextView mTimeProgressBarMin;
    private TextView mTimeProgressBarMax;
    private SharedPreferences mPreferences;
    private boolean mRunning;

    public static final int CARD_VALID_FOR_CURRENT_MEAL = 0;
    public static final int CARD_ALREADY_USED = 1;
    public static final int CARD_DIFFERENT_LOCATION = 2;
    public static final int CARD_ID_IS_NOT_IN_USE = 3;
    public static final int CARD_NO_CURRENT_MEAL = 4;
    public static final int CARD_INVALID = 5;


    private int mActiveMeal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View root = findViewById(R.id.main_screen);
        mFeedbackCustomer = findViewById(R.id.textview_feedback_customer);
        mDateTimeView = findViewById(R.id.date_time_view);
        mMealNameView = findViewById(R.id.textview_meal_name);
        mTimeProgressbar = findViewById(R.id.time_progress_bar);
        mTimeProgressBarMin = findViewById(R.id.text_view_start_time);
        mTimeProgressBarMax = findViewById(R.id.text_view_end_time);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        mRunning = true;
        mActiveMeal = 1000;

        setActiveMeal();

        //displaying time every second
        final Handler handler = new Handler();
        final int delay = 1000; //milliseconds

        handler.postDelayed(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void run() {
                if(mRunning) {
                    mDateTimeView.setText(DateFormat.format("yyyy.MMM.dd, HH:mm:ss", new Date()));
                    setActiveMeal();
                }
                handler.postDelayed(this, delay);
            }
        }, delay);
        //------------

        //Mocking Card Read every 5 seconds
        final Handler CRHandler = new Handler();
        final int CRdelay = 8000; //milliseconds

        CRHandler.postDelayed(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void run() {
                if(mRunning) {
                    onNewCardRead(0);
                }
                CRHandler.postDelayed(this, CRdelay);
            }
        }, delay);
        //------------

        mPreview = findViewById(R.id.camera_preview);
//        mPreview.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
//        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
//        preview.addView(mPreview);

        root.setLongClickable(true);
        root.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Intent HirtoryIntent = new Intent(MainActivity.this, CardReadHistoryActivity.class);
                MainActivity.this.startActivity(HirtoryIntent);

                return false;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCamera.stopPreview();
        releaseCamera();
        mRunning = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRunning = true;
        if(safeCameraOpen(Camera.CameraInfo.CAMERA_FACING_BACK)) {
            mPreview.setmCamera(mCamera);
            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override public void onPreviewFrame(byte[] bytes, Camera camera) {
//                    Log.d(TAG, "onPreviewFrame: ");
                }
            });
            new Handler().postDelayed(new Runnable() {
                @Override public void run() {
                    try {
                        takeAndDisplayPicture();
                    } catch(Exception e) {
                        Log.d(TAG, "takeAndDisplayPicture failed");
                        e.printStackTrace();
                    }
                }
            }, 1000);
        } else {
            Log.d(MainActivity.class.getSimpleName(), "camera open failed");
        }

    }

    private boolean takeAndDisplayPicture() {
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Log.d("MainActivity", "OnPictureTaken started");
                if(data != null) {
                    Log.d("takeAndDisplayPicture", "data is not null");
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    if(bitmap != null) {
                        Log.d("takeAndDisplayPicture", "bitmap is not null");
//                        mImageView.setImageBitmap(bitmap);
                    }
                }
            }
        });
        return false;
    }


    private boolean safeCameraOpen(int id) {
        boolean qOpened = false;

        try {
            releaseCamera();
            mCamera = Camera.open(id);
            Log.d(MainActivity.class.getSimpleName(), "camera open result:" + ((mCamera != null) ? "success" : "fail"));
            qOpened = (mCamera != null);
        } catch(Exception e) {
            Log.e(getString(R.string.app_name), "failed to open Camera");
            e.printStackTrace();
        }

        return qOpened;
    }

    private void releaseCamera() {
        if(mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    private void setActiveMeal() {
        Calendar rightNow = Calendar.getInstance();
        int MinuteOfDay = rightNow.get(Calendar.HOUR_OF_DAY)*60 + rightNow.get(Calendar.MINUTE);

        //Log.d("setActiveMeal:", "MinuteOfDay = " + String.valueOf(MinuteOfDay));
        int NextActiveMeal = 0;
        String EndTime = "00:00";
        String StartTime = "00:00";

        for(int MealInx = 0; MealInx < 6; MealInx++) {
            StartTime = EndTime;
            EndTime = mPreferences.getString("meal" + String.valueOf(MealInx + 1) + "_start_time", "23:59");
            //Log.d("setActiveMeal:", StartTime + " - " + EndTime + "  iteration:(" + String.valueOf(MealInx) + ")");
            int MealStartMinutes = 60*getHour(EndTime) + getMinute(EndTime);
            //Log.d("setActiveMeal:","MealStartMinutes = " + String.valueOf(MealStartMinutes));
            if(MinuteOfDay < MealStartMinutes) {
                NextActiveMeal = MealInx;
                //Log.d("setActiveMeal:", "break at " + String.valueOf(MealInx));
                break;
            }
        }
        if(NextActiveMeal == 0) {
            StartTime = mPreferences.getString("meal6_start_time", "23:59");
            EndTime = mPreferences.getString("meal1_start_time", "23:59");
        }
        //Log.d("setActiveMeal:", StartTime + " - " + EndTime + "  (" + String.valueOf(NextActiveMeal) + ")");


        if(mActiveMeal != NextActiveMeal) {
            // change static parts of the display
            mTimeProgressBarMin.setText(StartTime);
            mTimeProgressBarMax.setText(EndTime);
            mMealNameView.setText(mPreferences.getString("meal" + String.valueOf(NextActiveMeal) + "_name", getString(R.string.str_no_meal_time)));
        }

        // change progress bar
        int StartMinutes = 60*getHour(StartTime) + getMinute(StartTime);
        int EndMinutes = 60*getHour(EndTime) + getMinute(EndTime);
        if(StartMinutes > EndMinutes) {
            EndMinutes += 24*60;
        }
        if(StartMinutes > MinuteOfDay) {
            MinuteOfDay += 24*60;
        }
        if(EndMinutes == StartMinutes) {
            mTimeProgressbar.setProgress(100);
        } else {
            mTimeProgressbar.setProgress(100*(MinuteOfDay - StartMinutes)/(EndMinutes - StartMinutes));
        }

    }

    public void onNewCardRead(int Code) {
        int Delay = Integer.parseInt(mPreferences.getString("wait_time_after_notok", "5"));
        switch(CheckCardValidity(mActiveMeal, Code)) {
            case CARD_VALID_FOR_CURRENT_MEAL:
                mFeedbackCustomer.setText(R.string.fbc_valid_for_meal);
                mFeedbackCustomer.setBackgroundColor(getResources().getColor(R.color.OkBackground));
                mFeedbackCustomer.setTextColor(getResources().getColor(R.color.OkText));
                Delay = Integer.parseInt(mPreferences.getString("wait_time_after_ok", "3"));
                break;
            case CARD_ALREADY_USED:
                mFeedbackCustomer.setText(R.string.fbc_aldready_used);
                mFeedbackCustomer.setBackgroundColor(getResources().getColor(R.color.NotOkBackground));
                mFeedbackCustomer.setTextColor(getResources().getColor(R.color.NotOkText));
                break;
            case CARD_DIFFERENT_LOCATION:
                mFeedbackCustomer.setText(R.string.fbc_different_location);
                mFeedbackCustomer.setBackgroundColor(getResources().getColor(R.color.NotOkBackground));
                mFeedbackCustomer.setTextColor(getResources().getColor(R.color.NotOkText));
                break;
            case CARD_ID_IS_NOT_IN_USE:
                mFeedbackCustomer.setText(R.string.fbc_in_not_in_use);
                mFeedbackCustomer.setBackgroundColor(getResources().getColor(R.color.NotOkBackground));
                mFeedbackCustomer.setTextColor(getResources().getColor(R.color.NotOkText));
                break;
            case CARD_NO_CURRENT_MEAL:
                mFeedbackCustomer.setText(R.string.fbc_no_current_meal);
                mFeedbackCustomer.setBackgroundColor(getResources().getColor(R.color.NotOkBackground));
                mFeedbackCustomer.setTextColor(getResources().getColor(R.color.NotOkText));
                break;
            case CARD_INVALID:
                mFeedbackCustomer.setText(R.string.fbc_invalid);
                mFeedbackCustomer.setBackgroundColor(getResources().getColor(R.color.NotOkBackground));
                mFeedbackCustomer.setTextColor(getResources().getColor(R.color.NotOkText));
                break;
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setFeedbackDefault();
            }
        }, 1000*Delay);

    }

    private void setFeedbackDefault() {
        mFeedbackCustomer.setText(R.string.feedback_customer_default);
        mFeedbackCustomer.setBackgroundColor(getResources().getColor(R.color.DarkBackground));
        mFeedbackCustomer.setTextColor(getResources().getColor(R.color.GoldText));
    }


    private int CheckCardValidity(int mActiveMeal, int code) {
        // mock code:
        Random r = new Random();
        return Math.max(r.nextInt(10) - 5, 0);
    }


}

