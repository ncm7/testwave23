package com.example.testwave2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class WaveformView extends View {
    private byte[] waveformData;
    private Paint paint;

    public WaveformView(Context context) {
        super(context);
        init();
    }

    public WaveformView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setWaveform(byte[] waveformData) {
        this.waveformData = waveformData;
        invalidate();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(1f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (waveformData == null || waveformData.length == 0) {
            return;
        }

        float width = getWidth();
        float height = getHeight();
        float centerY = height / 2;

        float increment = width / waveformData.length;
        float x = 0;

        for (byte data : waveformData) {
            float amplitude = data / 128f;  // Convert byte to float (-128 to 127) to (-1 to 1)
            float y = centerY - (amplitude * centerY);
            canvas.drawLine(x, y, x, height - y, paint);
            x += increment;
        }
    }
}