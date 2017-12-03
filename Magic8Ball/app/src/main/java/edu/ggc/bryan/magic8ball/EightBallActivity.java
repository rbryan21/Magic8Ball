package edu.ggc.bryan.magic8ball;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

public class EightBallActivity extends AppCompatActivity implements SensorEventListener {

    private Answers answer;
    private SensorManager manager;
    private Sensor senAccelerometer;
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
    int sensorCounter = 1;
    int sensorCounter1 = 0;

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
        senAccelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        manager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        popPlayer = new MediaPlayer();
        answer = new Answers();

        eightBallText = (TextView) findViewById(R.id.eightBallText);

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
        return inRange(gravity, -9.81f, VERTICAL_TOL) ? true : false;
    }

    private boolean isUp(float gravity) {
        return inRange(gravity, 9.81f, VERTICAL_TOL) ? true : false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        Log.i("Magic8Ball", "Sensor changed!");

        gravity[0] = lowPass(event.values[0], gravity[0]);
        gravity[1] = lowPass(event.values[1], gravity[1]);
        gravity[2] = lowPass(event.values[2], gravity[2]);

        accel[0] = highPass(event.values[0], accel[0]);
        accel[1] = highPass(event.values[1], accel[1]);
        accel[2] = highPass(event.values[2], accel[2]);

//        if (readyForNewAnswer && isDown(gravity[2])) {
//            Log.i(TAG, "Down, grabbing a new answer.");
//            // Grab a new random answer
//
//            popPlayer.start();
//            answer.setCurrentAnswer(answer.getRandomAnswer());
//            readyForNewAnswer = false;
//            // text to speech newRandomAnswer.getAnswerText()
//        } else if (!readyForNewAnswer && isUp(gravity[2])) {
//            Log.i(TAG, "Ready for a new answer!");
//            readyForNewAnswer = true;
//        }
//        if (isUp)

        float z = event.values[2];
        Log.v("Float Z Value", String.valueOf(z));
        if (z > 9 && z < 10) {

            if (sensorCounter == 0)
            {
                Log.v("Line 134", "");
                answer.setCurrentAnswer(answer.getRandomAnswer());
                String message = answer.getCurrentAnswer();
                eightBallText.setText(message);
//                ts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                sensorCounter++;
                sensorCounter1 = 0;
            } else if (z > -10 && z < -9)
            {
                if (sensorCounter1 == 0)
                {
                    Log.v("Line 147", "");
//                    pop.start();
//                    v.vibrate(500);
                    eightBallText.setText("Ask another question");
                    sensorCounter1++;
                    sensorCounter = 0;
                }
            }
        }
    }

    /**
     * Called when the accuracy of the registered sensor has changed.  Unlike
     * onSensorChanged(), this is only called when this accuracy value changes.
     * <p>
     * <p>See the SENSOR_STATUS_* constants in
     * {@link SensorManager SensorManager} for details.
     *
     * @param sensor
     * @param accuracy The new accuracy of this sensor, one of
     *                 {@code SensorManager.SENSOR_STATUS_*}
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
