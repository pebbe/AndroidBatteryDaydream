package nl.xs4all.pebbe.batdreamer;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.service.dreams.DreamService;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.widget.ImageView;

import java.util.Calendar;
import java.util.Random;

public class MyDaydreamService extends DreamService {

    private static Random rnd = new Random();
    private float density;
    private TimerTask timerTask;
    private ConstraintLayout layout;
    private ImageView image;
    private int imageID;
    private Canvas canvas;
    private Context context;
    private IntentFilter ifilter;

    private class TimerTask extends AsyncTask<Integer, Integer, Integer> {
        protected Integer doInBackground(Integer... dummy) {
            while (!isCancelled()) {
                publishProgress(0);

                synchronized (this) {
                    try {
                        wait(20 * 1000);
                    } catch (Exception e) {
                        //
                    }
                }
            }
            return 0;
        }

        protected void onProgressUpdate(Integer... dummy) {
            try {
                paint();
            } catch (Exception e) {
                //
            }
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        // Exit dream upon user touch? false means yes!
        setInteractive(false);

        // Hide system UI?
        setFullscreen(true);

        // Keep screen at full brightness?
        setScreenBright(true);

        // Set the content view, just like you would with an Activity.
        setContentView(R.layout.my_daydream);

        density = getResources().getDisplayMetrics().density;

        layout = (ConstraintLayout) findViewById(R.id.daydreamLayout);
        imageID = R.id.imageView;
        image = (ImageView) findViewById(imageID);

        float size = 92 * density;
        Bitmap bitmap = Bitmap.createBitmap((int)(2 * size +.5), (int)(2 * size + .5), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        canvas.translate(size, size);
        image.setImageBitmap(bitmap);

        context = getApplicationContext();
        ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    }

    @Override
    public void onDreamingStarted() {
        super.onDreamingStarted();

        timerTask = new TimerTask();
        timerTask.execute();
    }

    @Override
    public void onDreamingStopped() {
        super.onDreamingStopped();

        timerTask.cancel(true);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private void paint() {

        //verplaatsen

        float xp = rnd.nextFloat();
        float yp = rnd.nextFloat();
        ConstraintSet c = new ConstraintSet();
        c.clone(layout);
        c.setHorizontalBias(imageID, xp);
        c.setVerticalBias(imageID, yp);
        c.applyTo(layout);

        // waardes opvragen

        Intent batteryStatus = context.registerReceiver(null, ifilter);
        float battery = .01f;
        try {
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, 1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
            battery = level / (float) scale;
        } catch (Exception e) {
            // ignore
        }

        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR);
        if (hour == 0) {
            hour = 12;
        }
        int minute = now.get(Calendar.MINUTE);
        String time = String.format("%d:%02d", hour, minute);

        // tekenen

        canvas.drawARGB(255, 0, 0, 0);

        float lwd = 10 * density;
        float a1 = 80 * density;
        float a2 = 66 * density;
        float a3 = 52 * density;

        Paint p = new Paint();
        p.setARGB(255, 128, 0, 0);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(lwd);

        canvas.drawArc(-a1, -a1, a1, a1, -90, 360 * battery, false, p);
        if (battery > .5f) {
            canvas.drawArc(-a2, -a2, a2, a2, -90, 360 * (battery - .5f) / .5f, false, p);
            if (battery > .75f) {
                canvas.drawArc(-a3, -a3, a3, a3, -90, 360 * (battery - .75f) / .25f, false, p);
            }
        }

        p.setStyle(Paint.Style.FILL);
        p.setTextSize(density * 26);
        canvas.drawText(time, -.5f * p.measureText(time), 8 * density, p);

        // maak veranderingen zichtbaar

        image.invalidate();
    }
}
