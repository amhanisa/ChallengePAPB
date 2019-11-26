package com.example.randomgenerator;


import android.app.Notification;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import org.w3c.dom.Text;

import java.util.Random;

import static com.example.randomgenerator.App.CHANNEL_1_ID;


/**
 * A simple {@link FragmentA} subclass.
 */
public class FragmentA extends Fragment {
    public TextView txtSatu, txtDua, txtTiga;
    public Button startButton, stopButton;
    public volatile boolean stopThreadSatu = false;
    public volatile boolean stopThreadDua = false;
    public volatile boolean stopThreadTiga = false;
    public volatile boolean stopThread = false;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String INTERVAL = "interval";
    public static final String NUMBER = "number";

    int interval, number;

    public FragmentA() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment, container, false);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        interval = Integer.parseInt(sharedPreferences.getString("interval_key", "500"));
        number = Integer.parseInt(sharedPreferences.getString("number_key", "7"));

        txtSatu = view.findViewById(R.id.textNumber1);
        txtDua = view.findViewById(R.id.textNumber2);
        txtTiga = view.findViewById(R.id.textNumber3);

        startButton = view.findViewById(R.id.btnStart);
        stopButton = view.findViewById(R.id.btnStop);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(stopThread) return;

                if (stopThreadSatu == false && stopThreadDua == false && stopThreadTiga == false) {
                    RandomNumberSatu thread = new RandomNumberSatu();

                    new Thread(thread).start();
                    stopThread = true;
                } else if (stopThreadSatu == true && stopThreadDua == true && stopThreadTiga == true) {
                    stopThreadSatu = false;
                    stopThreadDua = false;
                    stopThreadTiga = false;
                    RandomNumberSatu thread = new RandomNumberSatu();

                    new Thread(thread).start();
                    stopThread = true;
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stopThreadSatu == false && stopThreadDua == false && stopThreadTiga == false) {
                    stopThreadSatu = true;
                } else if (stopThreadSatu == true && stopThreadDua == false && stopThreadTiga == false) {
                    stopThreadDua = true;
                } else {
                    stopThreadTiga = true;
                    stopThread = false;
                    checkJackpot();
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        interval = Integer.parseInt(sharedPreferences.getString("interval_key", "500"));
        number = Integer.parseInt(sharedPreferences.getString("number_key", "7"));
    }

    class RandomNumberSatu implements Runnable {
        Random random;

        @Override
        public void run() {
            random = new Random();
            while (true) {
                if (!stopThreadSatu){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtSatu.setText(Integer.toString(random.nextInt(10)));
                        }
                    });
                }
                if(!stopThreadDua){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtDua.setText(Integer.toString(random.nextInt(10)));
                        }
                    });
                }
                if(!stopThreadTiga){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtTiga.setText(Integer.toString(random.nextInt(10)));
                        }
                    });
                }

                if(stopThreadTiga) return;

                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void checkJackpot() {
        int satu = Integer.parseInt(txtSatu.getText().toString());
        int dua = Integer.parseInt(txtDua.getText().toString());
        int tiga = Integer.parseInt(txtTiga.getText().toString());

        if (satu == number && dua == number && tiga == number) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());
            Notification notification = new NotificationCompat.Builder(getActivity(), CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("Yee Jackpot")
                    .setContentText("Selamat kamu dapat jackpot")
                    .build();

            notificationManager.notify(1, notification);
        }
    }

}
