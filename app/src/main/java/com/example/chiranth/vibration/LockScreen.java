package com.example.chiranth.vibration;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LockScreen extends Activity {
    private TextView textView,rules_btn;
    private EditText editText;
    private Button button;
    private ImageButton imageButton;
    private ImageView imageView;
    final private String pwd = "12102017";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lock_screen);

        rules_btn = (TextView)findViewById(R.id.rules_btn);
        textView = (TextView)findViewById(R.id.rules);
        imageView =  (ImageView)findViewById(R.id.poster);
        editText = (EditText)findViewById(R.id.editText);
        button = (Button)findViewById(R.id.button);
        imageButton = (ImageButton)findViewById(R.id.back_btn);

        imageView.setImageResource(R.drawable.poster);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setVisibility(View.VISIBLE);
                editText.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);
                textView.setVisibility(View.INVISIBLE);
                rules_btn.setVisibility(View.VISIBLE);
                imageButton.setVisibility(View.INVISIBLE);
            }
        });

        rules_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("1.\tThe timer starts the moment the task is displayed.\n" +
                        "2.\tYou Must only use the camera option provided in this app. \n" +
                        "3.\tYou must capture the video or photo within the time limit.\n" +
                        "4.\tYou must carry out the task only at the spot specified.\n" +
                        "5.\tYou must record the task, return and show it to the volunteer you’ve been assigned all within the time limit.\n" +
                        "6.\texternal help (asking other people to “hold your camera”) is not allowed\n" +
                        "7.\tif you do complete the task yet the timer runs out before you reach your volunteer.. you will be eliminated.\n" +
                        "8.\tIf you press the bail button you will be directly eliminated and you shall see a pic of yourself.\n" +
                        "9.\tTime is priceless. You waste it.. you lose.\n" +
                        "10.\tSo will you give up or do you dare??\n");

                //setting visibility
                imageView.setVisibility(View.INVISIBLE);
                editText.setVisibility(View.INVISIBLE);
                button.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.VISIBLE);
                rules_btn.setVisibility(View.INVISIBLE);
                imageButton.setVisibility(View.VISIBLE);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = editText.getText().toString();
                if(temp.equals(pwd))
                {
                    Intent intent = new Intent(LockScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                    Toast.makeText(getApplicationContext(),"Enter Correct Password",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {

    }
}
