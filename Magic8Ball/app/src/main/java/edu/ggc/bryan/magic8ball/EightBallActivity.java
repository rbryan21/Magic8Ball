package edu.ggc.bryan.magic8ball;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class EightBallActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eight_ball);

        FloatingActionButton aboutFab = (FloatingActionButton) findViewById(R.id.aboutFab);

        aboutFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EightBallActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

    }
}
