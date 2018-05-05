package com.example.chiranth.vibration;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.FitWindowsFrameLayout;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import static com.google.zxing.integration.android.IntentIntegrator.QR_CODE_TYPES;


public class MainActivity extends Activity {

    //declaration of layout elements
    private TextSwitcher textSwitcher;
    private ImageView imageView;
    //private ImageButton back_btn;
    private VideoView videoView;
    private TextView textView;
    private Button next,scan,finish,picture,quit,video,play,hint,display_img;
    private CountDownTimer countDownTimer5,countDownTimer5_3, countDownTimer6,countDownTimer7_3, countDownTimer10;
    private IntentIntegrator qrScan;
    private Uri fileUri,videoUri;

    //Request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    private int index = 0;
    private HashMap<Integer,String> hmap1 = new HashMap<>();
    private HashMap<Integer,CountDownTimer> timer = new HashMap<>();

    ArrayList<Integer> mlist = new ArrayList<Integer>();

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textSwitcher = (TextSwitcher)findViewById(R.id.textSwitcher);
        imageView = (ImageView)findViewById(R.id.imageView);
        videoView = (VideoView)findViewById(R.id.videoView);
        textView = (TextView)findViewById(R.id.textView);
        //back_btn = (ImageButton)findViewById(R.id.back_btn);
        next = (Button)findViewById(R.id.next);
        scan = (Button)findViewById(R.id.scan);
        finish = (Button)findViewById(R.id.finish);
        quit = (Button)findViewById(R.id.quit);
        //picture = (Button)findViewById(R.id.picture);
        video = (Button)findViewById(R.id.video);
        play = (Button)findViewById(R.id.play);
        //hint = (Button)findViewById(R.id.hint);
        //display_img = (Button)findViewById(R.id.display_img);
        qrScan = new IntentIntegrator(this);

        qrScan.setBarcodeImageEnabled(false);
        qrScan.setDesiredBarcodeFormats(QR_CODE_TYPES);

        setTimers();

        setdata();

        shuffle();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
            }
        }

        textSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView text = new TextView(MainActivity.this);
                text.setGravity(Gravity.LEFT);
                text.setTextSize(24);
                text.setTextColor(getResources().getColor(R.color.textColor));
                return text;
            }
        });

        textSwitcher.setText(Html.fromHtml(hmap1.get(mlist.get(index))));
        timer.get(mlist.get(index)).start();

    }

    @Override

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();

            } else {

                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();

            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view

            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
        else if(requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                videoUri = data.getData();
                play.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Video Saved", Toast.LENGTH_LONG).show();

            }
            else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Video recording cancelled.",
                        Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "Failed to record video",
                        Toast.LENGTH_LONG).show();
            }
        }

        if (result != null) {

            if (result.getContents() == null) {
                Toast.makeText(this, "Scan the correct QRCode", Toast.LENGTH_LONG).show();
            }
            else if(result.getContents().equals("QRCode_Nerve_Challenge")){
                textSwitcher.setText("\n\n\n\n\n" + "          " + "Click Next To Continue");
                next.setVisibility(View.VISIBLE);
                finish.setVisibility(View.INVISIBLE);
                scan.setVisibility(View.INVISIBLE);
                video.setVisibility(View.INVISIBLE);
                play.setVisibility(View.INVISIBLE);

                if(index == 23)
                {
                    next.setVisibility(View.GONE);
                    finish.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.GONE);
                }
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

//        hint.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                imageView.setVisibility(View.VISIBLE);
//            }
//        });
//
//        back_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                imageView.setVisibility(View.INVISIBLE);
//            }
//        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrScan.initiateScan();

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index >= 23){
                    next.setVisibility(View.INVISIBLE);
                    finish.setVisibility(View.VISIBLE);
                }
                else {
                    index++;
                    next.setVisibility(View.INVISIBLE);
                    scan.setVisibility(View.INVISIBLE);
                }
                video.setVisibility(View.VISIBLE);
                textSwitcher.setText(Html.fromHtml(hmap1.get(mlist.get(index))));
                timer.get(mlist.get(index-1)).cancel();
                timer.get(mlist.get(index)).start();
            }
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next.setVisibility(View.GONE);
                scan.setVisibility(View.GONE);
                finish.setVisibility(View.GONE);
                video.setVisibility(View.GONE);
                quit.setVisibility(View.GONE);
                play.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);

                textSwitcher.setText("Congratulations you have successfully completed the challenge!!!");
            }
        });

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

                alertDialogBuilder.setMessage("Are you sure you want to Quit?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Glide.with(MainActivity.this)
                                        .load(R.drawable.giphy)
                                        .asGif()
                                        .placeholder(R.drawable.giphy)
                                        .crossFade()
                                        .into(imageView);

                                //setting visibility
                                imageView.setVisibility(View.VISIBLE);
                                //back_btn.setVisibility(View.GONE);
                                //hint.setVisibility(View.GONE);
                                //display_img.setVisibility(View.GONE);
                                next.setVisibility(View.GONE);
                                scan.setVisibility(View.GONE);
                                finish.setVisibility(View.GONE);
                                //picture.setVisibility(View.GONE);
                                video.setVisibility(View.GONE);
                                play.setVisibility(View.GONE);
                                quit.setVisibility(View.GONE);
                                textSwitcher.setVisibility(View.GONE);
                                textView.setVisibility(View.GONE);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = alertDialogBuilder.create();
                //Setting the title manually
                alert.setTitle("Nerve Challenge");
                alert.show();

            }
        });

//        picture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//                File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/image1.jpg");
//                fileUri = Uri.fromFile(path);
//
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//
//                // start the image capture Intent
//                startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
//            }
//        });


        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

//                File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/video.mp4");
//                //videoUri = Uri.fromFile(path);
//
//                videoUri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider", path);
//
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);

                // start the image capture Intent
                startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.setVideoURI(videoUri);
                MediaController mediaController = new MediaController(MainActivity.this);
                videoView.requestFocus();
                videoView.setMediaController(mediaController);
                videoView.start();

                //setting visibility
                videoView.setVisibility(View.VISIBLE);
                textSwitcher.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.INVISIBLE);
                video.setVisibility(View.INVISIBLE);
                scan.setVisibility(View.INVISIBLE);
                quit.setVisibility(View.INVISIBLE);
                play.setVisibility(View.INVISIBLE);
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.setVisibility(View.INVISIBLE);
                textSwitcher.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
                video.setVisibility(View.VISIBLE);
                scan.setVisibility(View.VISIBLE);
                quit.setVisibility(View.VISIBLE);
                play.setVisibility(View.VISIBLE);
            }
        });

    }


    private void shuffle() {

        for(Integer t : hmap1.keySet())
            mlist.add(t);

        Collections.shuffle(mlist, new Random());

    }


    private void setTimers() {

        countDownTimer5 = new CountDownTimer(300000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                long seconds = millisUntilFinished / 1000;
                textView.setText(String.format("%02d", seconds / 60) + ":" + String.format("%02d", seconds % 60));
            }

            @Override
            public void onFinish() {

                textSwitcher.setText("                    " + "Time Out\n\n\n\n" + "        " + "Scan QRCode to Continue");
                textView.setText("00:00");

                //setting visibility
                //back_btn.setVisibility(View.INVISIBLE);
                scan.setVisibility(View.VISIBLE);
                finish.setVisibility(View.INVISIBLE);
                //hint.setVisibility(View.INVISIBLE);
                //picture.setVisibility(View.INVISIBLE);
                video.setVisibility(View.INVISIBLE);

                finishActivity(CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
                finishActivity(CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

                //displayImage();
            }
        };

        //timer for 5min 30 sec
        countDownTimer5_3 = new CountDownTimer(330000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                long seconds = millisUntilFinished / 1000;
                textView.setText(String.format("%02d", seconds / 60) + ":" + String.format("%02d", seconds % 60));
            }

            @Override
            public void onFinish() {

                textSwitcher.setText("Time Out\n\n \tScan QRCode to Continue");
                textView.setText("00:00");

                //setting visibility
                //back_btn.setVisibility(View.INVISIBLE);
                scan.setVisibility(View.VISIBLE);
                finish.setVisibility(View.INVISIBLE);
                //hint.setVisibility(View.INVISIBLE);
                //picture.setVisibility(View.INVISIBLE);
                video.setVisibility(View.INVISIBLE);

                finishActivity(CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
                finishActivity(CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

                //displayImage();
            }
        };

        //timer for 6 minutes
        countDownTimer6 = new CountDownTimer(360000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                long seconds = millisUntilFinished / 1000;
                textView.setText(String.format("%02d", seconds / 60) + ":" + String.format("%02d", seconds % 60));
            }

            @Override
            public void onFinish() {

                textSwitcher.setText("Time Out\n\n \tScan QRCode to Continue");
                textView.setText("00:00");

                //setting visibility
                //back_btn.setVisibility(View.INVISIBLE);
                scan.setVisibility(View.VISIBLE);
                finish.setVisibility(View.INVISIBLE);
                //hint.setVisibility(View.INVISIBLE);
                //picture.setVisibility(View.INVISIBLE);
                video.setVisibility(View.INVISIBLE);

                finishActivity(CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
                finishActivity(CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

                //displayImage();
            }
        };

        //timer for 7min 30sec
        countDownTimer7_3 = new CountDownTimer(450000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                long seconds = millisUntilFinished / 1000;
                textView.setText(String.format("%02d", seconds / 60) + ":" + String.format("%02d", seconds % 60));
            }

            @Override
            public void onFinish() {

                textSwitcher.setText("Time Out\n\n \tScan QRCode to Continue");
                textView.setText("00:00");

                //setting visibility
                //back_btn.setVisibility(View.INVISIBLE);
                scan.setVisibility(View.VISIBLE);
                finish.setVisibility(View.INVISIBLE);
                //hint.setVisibility(View.INVISIBLE);
                //picture.setVisibility(View.INVISIBLE);
                video.setVisibility(View.INVISIBLE);

                finishActivity(CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
                finishActivity(CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

                //displayImage();
            }
        };


        //timer for 10 minutes
        countDownTimer10 = new CountDownTimer(600000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                long seconds = millisUntilFinished / 1000;
                textView.setText(String.format("%02d", seconds / 60) + ":" + String.format("%02d", seconds % 60));
            }

            @Override
            public void onFinish() {

                textSwitcher.setText("Time Out\n\n \tScan QRCode to Continue");
                textView.setText("00:00");

                //setting visibility
                //back_btn.setVisibility(View.INVISIBLE);
                scan.setVisibility(View.VISIBLE);
                finish.setVisibility(View.INVISIBLE);
                //hint.setVisibility(View.INVISIBLE);
                //picture.setVisibility(View.INVISIBLE);
                video.setVisibility(View.INVISIBLE);

                finishActivity(CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
                finishActivity(CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

                //displayImage();
            }
        };
    }


    private void setdata() {
        String[] string = new String[24];
        string[0] = "Nerve Challenge" + "<br>" + "<br>" + "<b>" + "Task 1" + "</b>" + "<br>" + "Run to the parking lot and find a vehicle number that adds up to the number 14." + "<br>" + "<br>" +
                    "<b>" + "RULES:" + "<br>" + "</b>" +
                "1.\tClick a picture of the vehicle such that the number on the number plate is clearly visible." + "<br>" +
                "2.\tTime limit: 5 minutes 30 seconds." + "<br>";

        string[1] = "Nerve Challenge" + "<br>" + "<br>" + "<b>" + "Task 2" + "</b>" + "<br>" + "Record yourself counting the steps from the arch near the parking to the temple." + "<br>" + "<br>" +
                "<b>" + "RULES:" + "<br>" + "</b>" +
                "1.\tRecord yourself counting the steps aloud and report the number to your nearest volunteer." + "<br>" +
                "2.\tBoth should jump/hop and count the number of steps. Both the participants should jump simultaneously." + "<br>" +
                "3.\tTime limit: 5 minutes." + "<br>";

        string[2] = "Nerve Challenge" + "<br>" + "<br>" + "<b>" + "Task 3" + "</b>" + "<br>" + "Sing a song at crowded place in such a way that it annoys people.(CD Sagar Building)" + "<br>" + "<br>" +
                "<b>" + "RULES:" + "<br>" + "</b>" +
                "1.\tBe loud, clear and obvious." + "<br>" +
                "2.\tYour partner must record the reaction of the people around you as well." + "<br>" +
                "3.\tTime limit: 5 minutes." + "<br>";

        string[3] = "Nerve Challenge" + "<br>" + "<br>" + "<b>" + "Task 4" + "</b>" + "<br>" + "Talk to a person wearing blue shoes about how red shoes would be better." + "<br>" + "<br>" +
                "<b>" + "RULES:" + "<br>" + "</b>" +
                "1.\tRecord yourself having this conversation, make it funny." + "<br>" +
                "2.\tTime limit: 5 minutes 30 seconds." + "<br>";

        string[4] = "Nerve Challenge" + "<br>" + "<br>" + "<b>" + "Task 5" + "</b>" + "<br>" + "Play a song and pole dance inside LOTUS" + "<br>" + "<br>" +
                "<b>" + "RULES:" + "<br>" + "</b>" +
                "1.\tThe song should be some Bollywood masala song and should be loud." + "<br>" +
                "2.\tYou ahould gather a large crowd while performimg and record a video." + "<br>" +
                "3.\tTime limit: 10 minutes." + "<br>";

        string[5] = "Nerve Challenge" + "<br>" + "<br>" + "<b>" + "Task 6" + "</b>" + "<br>" + "Act as if you are drunk and create a scene.(Football seating area)" + "<br>" + "<br>" +
                "<b>" + "RULES:" + "<br>" + "</b>" +
                "1.\tDo not touch or behave inappropriately with anyone." + "<br>" +
                "2.\tMake the scene as funny as possible." + "<br>" +
                "3.\tTime limit: 5 minutes." + "<br>";

        string[6] ="Nerve Challenge" + "<br>" + "<br>" + "<b>" + "Task 7" + "</b>" + "<br>" + "Do the “Whip and nay nay” dance.(Center of Football Court" + "<br>" + "<br>" +
                "<b>" + "RULES:" + "<br>" + "</b>" +
                "1.\tDo the dance step in any crowded place." + "<br>" +
                "2.\tBoth the participants should do the dance." + "<br>" +
                "3.\tTime limit: 5 minutes." + "<br>";

        string[7] = "Nerve Challenge" + "<br>" + "<br>" + "<b>" + "Task 8" + "</b>" + "<br>" + "Count number of colored glass on the new architecture building" + "<br>" + "<br>" +
                "<b>" + "RULES:" + "<br>" + "</b>" +
                "1.\tCount the number of glasses loudly which should disturb others." + "<br>" +
                "2.\tTime limit: 5 minutes." + "<br>";

        string[8] = "Nerve Challenge" + "<br>" + "<br>" + "<b>" + "Task 9" + "</b>" + "<br>" + "Wash your face in college fountain" + "<br>" + "<br>" +
                "<b>" + "RULES:" + "<br>" + "</b>" +
                "1.\tYou have to wash your face with the water from at least 2 fountains in the college. " + "<br>" +
                "2.\tTime limit: 7 minutes 30 seconds." + "<br>";

        string[9] = "Nerve Challenge" + "<br>" + "<br>" + "<b>" + "Task 10" + "</b>" + "<br>" + "Find a person wearing a green shirt and take a selfie with them." + "<br>" + "<br>" +
                "<b>" + "RULES:" + "<br>" + "</b>" +
                "1.\tFind the person whose details have been described in the description. In the selfie both the participants should be present." + "<br>" +
                "2.\tTime limit: 5 minutes 30 seconds." + "<br>";

        string[10] = "Nerve Challenge" + "<br>" + "<br>" + "<b>" + "Task 11" + "</b>" + "<br>" + "Shout \"I LOVE RAKHI SAWANT\" in a crowded place THRICE and run away.(NRI canteen)" + "<br>" + "<br>" +
                "<b>" + "RULES:" + "<br>" + "</b>" +
                "1.\tPerform the task in a crowded place and record it." + "<br>" +
                "2.\tTime limit: 5 minutes." + "<br>";

        string[11] = "Nerve Challenge" + "<br>" + "<br>" + "<b>" + "Task 12" + "</b>" + "<br>" + "Convince people to do Harlem shake dance with you.(ME or ECE block)" + "<br>" + "<br>" +
                "<b>" + "RULES:" + "<br>" + "</b>" +
                "1.\tThey should agree with you to do the dance." + "<br>" +
                "2.\tIf they deny you will lose the game." + "<br>" +
                "3.\tTime limit: 5 minutes." + "<br>";

        string[12] = "Nerve Challenge" + "<br>" + "<br>" + "<b>" + "Task 13" + "</b>" + "<br>" + "Do a fast paced dance to a slow paced song.(Center of Basketball Court)" + "<br>" + "<br>" +
                "<b>" + "RULES:" + "<br>" + "</b>" +
                "1.\tBoth the participants should be involve and a video should be recorded." + "<br>" +
                "2.\tBe funny and grab people's attention." + "<br>" +
                "3.\tTime limit: 5 minutes." + "<br>";

        string[13] = "Nerve Challenge" + "<br>" + "<br>" + "<b>" + "Task 14" + "</b>" + "<br>" + "Prank call someone.(ECE block)" + "<br>" + "<br>" +
                "<b>" + "RULES:" + "<br>" + "</b>" +
                "1.\tPrank shouldn’t be offensive and record the audio." + "<br>" +
                "2.\tTime limit: 5 minutes 30 seconds." + "<br>";

        string[14] = "Nerve Challenge" + "<br>" + "<br>" + "<b>" + "Task 15" + "</b>" + "<br>" + "Tie the shoe laces of you and your partner and run from business block to mechanical block" + "<br>" + "<br>" +
                "<b>" + "RULES:" + "<br>" + "</b>" +
                "1.\tYou may take external help to record a video." + "<br>" +
                "2.\tIf you fall or fail to complete the task within the time limit you'll lose." + "<br>" +
                "3.\tTime limit: 10 minutes." + "<br>";

        string[15] = "Nerve Challenge" + "<br>" + "<br>" + "<b>" + "Task 16" + "</b>" + "<br>" + "Convince someone to do a tapanguchi duet with you.(Near EEE or IEM block)" + "<br>" + "<br>" +
                "<b>" + "RULES:" + "<br>" + "</b>" +
                "1.\tOne person should dance with the stranger for 2 minutes and the other person should record it." + "<br>" +
                "2.\tTime limit: 10 minutes." + "<br>";

        string[16] = "Nerve Challenge" + "<br>" + "<br>" + "<b>" + "Task 17" + "</b>" + "<br>" + "Ask a stranger of same gender to do arm wrestling with you.(Near School)" + "<br>" + "<br>" +
                "<b>" + "RULES:" + "<br>" + "</b>" +
                "1.\tOne should record and one should play." + "<br>" +
                "2.\tTime limit: 6 minutes." + "<br>";

        string[17] = "Nerve Challenge" + "<br>" + "<br>" + "<b>" + "Task 18" + "</b>" + "<br>" + "Have a stare down with a random person till they react.(Infront of Aeronautical block)" + "<br>" + "<br>" +
                "<b>" + "RULES:" + "<br>" + "</b>" +
                "1.\tIt should be the same person." + "<br>" +
                "2.\tTime limit: 5 minutes." + "<br>";

        string[18] = "Nerve Challenge" + "<br>" + "<br>" + "<b>" + "Task 19" + "</b>" + "<br>" + "Make faces at someone" + "<br>" + "<br>" +
                "<b>" + "RULES:" + "<br>" + "</b>" +
                "1.\tYou have to make weird faces at a random person." + "<br>" +
                "2.\tThere should be eye contact with that random person." + "<br>" +
                "3.\tTime limit: 5 minutes." + "<br>";

        string[19] = "Nerve Challenge" + "<br>" + "<br>" + "<b>" + "Task 20" + "</b>" + "<br>" + "Say “I love you” to a person of the same gender and convince them to give their number to you." + "<br>" + "<br>" +
                "<b>" + "RULES:" + "<br>" + "</b>" +
                "1.\tYou should make the person say the same to you too." + "<br>" +
                "2.\tTime limit: 7 minutes 30 seconds." + "<br>";

        string[20]  = "Nerve Challenge" + "<br>" + "<br>" + "<b>" + "Task 21" + "</b>" + "<br>" + "Ask someone to lend you cash for lunch." + "<br>" + "<br>" +
                "<b>" + "RULES:" + "<br>" + "</b>" +
                "1.\tCollect 50 rupees." + "<br>" +
                "2.\tYou may use any method you see fit." + "<br>" +
                "3.\tTime limit: 10 minutes." + "<br>";

        string[21] = "Nerve Challenge" + "<br>" + "<br>" + "<b>" + "Task 22" + "</b>" + "<br>" + "Act in a queer manner with a person." + "<br>" + "<br>" +
                "<b>" + "RULES:" + "<br>" + "</b>" +
                "1.\tAny thing you do is at your own risk" + "<br>" +
                "2.\tTime limit: 5 minutes." + "<br>";

        string[22] = "Nerve Challenge" + "<br>" + "<br>" + "<b>" + "Task 23" + "</b>" + "<br>" + "Stick a note on someone with something funny and instigate people to call them out." + "<br>" + "<br>" +
                "<b>" + "RULES:" + "<br>" + "</b>" +
                "1.\tNo vulgar or body shaming words. Be wise and chose something witty in a hilarious way and Record a video." + "<br>" +
                "2.\tTime limit: 5 minutes." + "<br>";

        string[23] = "Nerve Challenge" + "<br>" + "<br>" + "<b>" + "Task 24" + "</b>" + "<br>" + "Sing 'Selfie Mene Leliya' aloud for 1 minute.(Cuppa)" + "<br>" + "<br>" +
                "<b>" + "RULES:" + "<br>" + "</b>" +
                "1.\tIt's a selfie video task." + "<br>" +
                "2.\tYou should get all people present at the mentioned location on tape." + "<br>" +
                "3.\tTime limit: 5 minutes." + "<br>";


        for(int i=0; i < 24;i++)
            hmap1.put(i + 1, string[i]);

        //setting timer for each task
        timer.put(1, countDownTimer5_3);
        timer.put(2, countDownTimer5);
        timer.put(3, countDownTimer5);
        timer.put(4, countDownTimer5_3);
        timer.put(5, countDownTimer10);
        timer.put(6, countDownTimer5);
        timer.put(7, countDownTimer5);
        timer.put(8, countDownTimer5);
        timer.put(9, countDownTimer7_3);
        timer.put(10, countDownTimer5_3);
        timer.put(11, countDownTimer5);
        timer.put(12, countDownTimer5);
        timer.put(13, countDownTimer5);
        timer.put(14, countDownTimer5_3);
        timer.put(15, countDownTimer10);
        timer.put(16, countDownTimer10);
        timer.put(17, countDownTimer6);
        timer.put(18, countDownTimer5);
        timer.put(19, countDownTimer5);
        timer.put(20, countDownTimer7_3);
        timer.put(21, countDownTimer10);
        timer.put(22, countDownTimer5);
        timer.put(23, countDownTimer5);
        timer.put(24, countDownTimer5);

    }

}
