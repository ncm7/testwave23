package com.example.testwave2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import java.io.ByteArrayOutputStream;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.IOException;
import java.util.Arrays;
import android.widget.Toast;
import android.os.Handler;
import android.view.Gravity;

public class MainActivity extends AppCompatActivity {
        private static final int SAMPLE_RATE = 44100;
        private boolean isRecording = false;
        private byte[] capturedAudioData;
         private Handler handler;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            Button startButton = findViewById(R.id.start_button);
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startRecording();
                }
            });

            Button stopButton = findViewById(R.id.stop_button);
            stopButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    //stopRecording();
                }
            });

            handler = new Handler();
        }

        private void startRecording() {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
            } else {
                isRecording = true;
                capturedAudioData = captureAudioData();
            }
            //continuously update screen
            stopRecording();
        }

        private void stopRecording() {
            isRecording = false;
            // Check if capturedAudioData is not null
            if (capturedAudioData != null) {
              //  String dataString = Arrays.toString(capturedAudioData);
              //  Toast.makeText(this, dataString, Toast.LENGTH_SHORT).show();
              //  Toast.makeText(this, "It's not null", Toast.LENGTH_SHORT).show();


                //int max = findMaxValue(capturedAudioData);
                //String maxString = String.valueOf(max);
                //Toast.makeText(this, "Maximum value: " + maxString, Toast.LENGTH_SHORT).show();

                //double average = calculateAverageValue(capturedAudioData);
                //String averageString = String.format("%.2f", average);
                //Toast.makeText(this, "Average value: " + averageString, Toast.LENGTH_SHORT).show();


                //double fundamentalFrequency = calculateFundamentalFrequency(capturedAudioData);
                //if (fundamentalFrequency > 0) {
                //    String frequencyString = String.format("%.2f", fundamentalFrequency);
                //    Toast.makeText(this, "Fundamental frequency: " + frequencyString + " Hz", Toast.LENGTH_SHORT).show();
                //} else {
                //    Toast.makeText(this, "Fundamental frequency not detected", Toast.LENGTH_SHORT).show();
                //}


                WaveformView waveformView = findViewById(R.id.waveformView);
                waveformView.setWaveform(capturedAudioData);




            }else{
                //Toast.makeText(this, "Press Start Recording to Begin Training", Toast.LENGTH_SHORT).show();


                Toast toast = Toast.makeText(this, "Press Start Training to Begin", Toast.LENGTH_SHORT);
                //toast.setGravity(Gravity.TOP | Gravity.START, 0, 0);
                toast.setGravity(Gravity.TOP, 100, 100);
                toast.show();


            }
            // Delay for 1 second before starting recording again
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startRecording();
                }
            }, 1090);
            //change this time to the refresh time
        }


    private int findMaxValue(byte[] array) {
        int max = Integer.MIN_VALUE;
        for (byte value : array) {
            int intValue = value & 0xFF; // Convert byte value to unsigned int value
            if (intValue > max) {
                max = intValue;
            }
        }
        return max;
    }

    private double calculateAverageValue(byte[] array) {
        long sum = 0;
        for (byte value : array) {
            int intValue = value & 0xFF; // Convert byte value to unsigned int value
            sum += intValue;
        }
        return (double) sum / array.length;
    }


    private double calculateFundamentalFrequency(byte[] array) {
        // Convert byte array to double array
        double[] audioData = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            audioData[i] = array[i] / 32768.0; // Convert signed 16-bit PCM to double between -1 and 1
        }

        // Autocorrelation
        int sampleRate = 44100;
        int windowSize = array.length;
        int maxLag = windowSize / 2;
        double[] autocorrelation = new double[maxLag];
        for (int lag = 0; lag < maxLag; lag++) {
            for (int i = 0; i < windowSize - lag; i++) {
                autocorrelation[lag] += audioData[i] * audioData[i + lag];
            }
        }

        // Find peak in autocorrelation
        int minLag = 0;
        double maxCorrelation = autocorrelation[minLag];
        for (int lag = 1; lag < maxLag; lag++) {
            if (autocorrelation[lag] > maxCorrelation) {
                maxCorrelation = autocorrelation[lag];
                minLag = lag;
            }
        }

        // Calculate fundamental frequency
        double fundamentalFrequency = sampleRate / (double) (minLag);
        return fundamentalFrequency;
    }


    private byte[] captureAudioData() {
        int SAMPLE_RATE = 44100;
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        AudioRecord audioRecord = null;

        // Check if RECORD_AUDIO permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        } else {
            // Handle the case where permission is not granted
            // You can request the permission here or handle it in your code accordingly
            return null;
        }

        if (audioRecord == null || audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            // Handle initialization failure
            return null;
        }

        audioRecord.startRecording();

        byte[] audioData = new byte[bufferSize];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;

        while (isRecording && elapsedTime < 1000) {
            int read = audioRecord.read(audioData, 0, audioData.length);
            if (read != AudioRecord.ERROR_INVALID_OPERATION) {
                outputStream.write(audioData, 0, read);
            }
            if (!isRecording) {
                break; // Break out of the loop if isRecording becomes false
            }
            elapsedTime = System.currentTimeMillis() - startTime;
        }

        audioRecord.stop();
        audioRecord.release();

        byte[] capturedData = outputStream.toByteArray();

        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return capturedData;
    }

}
