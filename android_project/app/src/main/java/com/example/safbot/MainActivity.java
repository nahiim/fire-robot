package com.example.safbot;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity
{
    private String server_url;

    private ImageButton forward_button, backward_button, left_button, right_button;

    private SeekBar car_slider, pump_slider, base_slider, elbow_slider;

    private Handler hold_front_handler;
    private Handler hold_back_handler;
    private Handler hold_left_handler;
    private Handler hold_right_handler;

    private boolean is_holding_front = false, is_holding_back = false, is_holding_left = false, is_holding_right = false;

    int front_pressed = 0, back_pressed = 0, right_pressed = 0, left_pressed = 0;
    int car_pwm = 0, pump_pwm = 0, base_pwm = 0, elbow_pwm = 0;

    private RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hold_back_handler = new Handler();
        hold_front_handler = new Handler();
        hold_left_handler = new Handler();
        hold_right_handler = new Handler();

        // Set the title of the Action Bar
        getSupportActionBar().setTitle("SAFBOT");

        // Get the RequestQueue from Volley singleton
        requestQueue = Volley.newRequestQueue(this);

        forward_button = findViewById(R.id.ForwardButton);
        backward_button = findViewById(R.id.BackwardButton);
        left_button = findViewById(R.id.leftButton);
        right_button = findViewById(R.id.rightButton);

        car_slider   = findViewById(R.id.car_seekbar);
        base_slider = findViewById(R.id.base_seekbar);
        elbow_slider = findViewById(R.id.elbow_seekbar);
        pump_slider  = findViewById(R.id.pump_seekbar);

        car_slider.setOnSeekBarChangeListener(seekBarChangeListener);
        base_slider.setOnSeekBarChangeListener(seekBarChangeListener);
        elbow_slider.setOnSeekBarChangeListener(seekBarChangeListener);
        pump_slider.setOnSeekBarChangeListener(seekBarChangeListener);

        forward_button.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    // Button pressed
                    is_holding_front = true;
                    front_pressed = 1;
                    hold_front_handler.postDelayed(holdFrontRunnable, 100); // 100 milliseconds
                    sendPostRequest();
                }
                else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                {
                    // Button released
                    is_holding_front = false;
                    front_pressed = 0;
                    hold_front_handler.removeCallbacks(holdFrontRunnable);
                    sendPostRequest();
                }
                return false;
            }
        });

        backward_button.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    // Button pressed
                    is_holding_back = true;
                    back_pressed = 1;
                    hold_back_handler.postDelayed(holdBackRunnable, 100); // 100 milliseconds
                    sendPostRequest();
                }
                else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                {
                    // Button released
                    is_holding_back = false;
                    back_pressed = 0;
                    hold_back_handler.removeCallbacks(holdBackRunnable);
                    sendPostRequest();
                }
                return false;
            }
        });

        left_button.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    // Button pressed
                    is_holding_left = true;
                    left_pressed = 1;
                    hold_left_handler.postDelayed(holdLeftRunnable, 100); // 100 milliseconds
                    sendPostRequest();
                }
                else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                {
                    // Button released
                    is_holding_left = false;
                    left_pressed = 0;
                    hold_left_handler.removeCallbacks(holdLeftRunnable);
                    sendPostRequest();
                }
                return false;
            }
        });

        right_button.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    // Button pressed
                    is_holding_right = true;
                    right_pressed = 1;
                    hold_right_handler.postDelayed(holdRightRunnable, 100); // 100 milliseconds
                    sendPostRequest();
                }
                else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                {
                    // Button released
                    is_holding_right = false;
                    right_pressed = 0;
                    hold_right_handler.removeCallbacks(holdRightRunnable);
                    sendPostRequest();
                }
                return false;
            }
        });
    }


    private final SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener()
    {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
        {
            // Whenever any SeekBar value changes, this method will be called automatically.

            car_pwm = car_slider.getProgress();
            base_pwm = base_slider.getProgress();
            elbow_pwm = elbow_slider.getProgress();
            pump_pwm = pump_slider.getProgress();

            sendPostRequest();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar)
        {
            // This method is called when the user starts interacting with the SeekBar.
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar)
        {
            // This method is called when the user stops interacting with the SeekBar.
        }
    };





    private void sendPostRequest()
    {
        server_url = "http://192.168.4.1/?" +
                "base_pwm=" + base_pwm +
                "&car_pwm="+ car_pwm +
                "&back=" + back_pressed +
                "&pump_pwm=" + pump_pwm +
                "&front=" + front_pressed +
                "&elbow_pwm=" + elbow_pwm +
                "&left=" + left_pressed +
                "&right=" + right_pressed;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.d("Volley", "RESPONSE RECEIVED!!!: \n\t");
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        error.printStackTrace();
                        Log.d("Volley", "NO RESPONSE FROM SERVER. RESENDING REQUEST... \n\t");
                    }
                });
        requestQueue.add(stringRequest);
    }


    private Runnable holdFrontRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            if (is_holding_front)
            {}
        }
    };

    private Runnable holdBackRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            if (is_holding_back)
            {}
        }
    };

    private Runnable holdLeftRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            if (is_holding_left)
            {}
        }
    };

    private Runnable holdRightRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            if (is_holding_right)
            {}
        }
    };
}