package edu.ggc.bryan.magic8ball;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

public class EightBallActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager manager;
    private MediaPlayer popPlayer;
    private MediaPlayer backgroundPlayer;
    private static final String TAG = "EightBall";
    private static final float VERTICAL_TOL = 0.3f;
    private static final float ALPHA = 0.80f; // weighing factor used by the low pass filter
    float[] gravity = new float[3];
    float[] accel = new float[3];
    private TextToSpeech tts;
    private TextView eightBallText;
    private boolean readyForNewAnswer = true;
    Vibrator vibrate = null;

    private boolean inRange(float value, float target, float tol) {
        return value >= target - tol && value <= target + tol;
    }

    // de-emphasize transient forces
    private float lowPass(float current, float gravity) {
        return current * (1 - ALPHA) + gravity * ALPHA; // ALPHA indicates the influence of past observations
    }

    // de-emphasize constant forces
    private float highPass(float current, float gravity) {
        return current - gravity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eight_ball);

        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        popPlayer = new MediaPlayer();
        eightBallText = (TextView) findViewById(R.id.eightBallText);
        vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                tts.setLanguage(Locale.US);
            }
        });

        FloatingActionButton aboutFab = (FloatingActionButton) findViewById(R.id.aboutFab);

        aboutFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EightBallActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        manager.registerListener(this, manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.unregisterListener(this);
    }

    private boolean isDown(float gravity) {
        return inRange(gravity, -9.81f, VERTICAL_TOL) ? true: false;
    }

    private boolean isUp(float gravity) {
        return inRange(gravity, 9.81f, VERTICAL_TOL) ? true: false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//        Log.i("Magic8Ball", "Sensor changed!");

        gravity[0] = lowPass(event.values[0], gravity[0]);
        gravity[1] = lowPass(event.values[1], gravity[1]);
        gravity[2] = lowPass(event.values[2], gravity[2]);

        accel[0] = highPass(event.values[0], accel[0]);
        accel[1] = highPass(event.values[1], accel[1]);
        accel[2] = highPass(event.values[2], accel[2]);

        if (readyForNewAnswer && isDown(gravity[2])) {
            Log.i(TAG, "Down, grabbing a new answer.");
            // Grab a new random answer
            Answer newRandomAnswer = Answers.getRandomAnswer();
            popPlayer.start();
            eightBallText.setText(newRandomAnswer.getAnswerText());
            vibrate.vibrate(500);
            Log.i(TAG, "Phone should have vibrated!");
            readyForNewAnswer = false;
            // text to speech newRandomAnswer.getAnswerText()
        } else if (!readyForNewAnswer && isUp(gravity[2])) {
            Log.i(TAG, "Ready for a new answer!");
            eightBallText.setText("Ask Me Another Question!");
            readyForNewAnswer = true;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}
