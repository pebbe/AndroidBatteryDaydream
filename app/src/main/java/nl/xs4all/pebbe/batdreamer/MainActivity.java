package nl.xs4all.pebbe.batdreamer;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openConfig(View view) {
        Intent intent = new Intent(Settings.ACTION_DREAM_SETTINGS);
        startActivity(intent);
    }
}
