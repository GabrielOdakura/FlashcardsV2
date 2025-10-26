package com.example.flashcards2.instances;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class CustomStatsView extends View {

    private final Paint correctPaint = new Paint();
    private final Paint wrongPaint = new Paint();
    private final Paint textPaint = new Paint();
    private float correctPercent = 0f;

    public CustomStatsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        correctPaint.setColor(0xFF4CAF50); // green
        correctPaint.setStyle(Paint.Style.STROKE);
        correctPaint.setStrokeWidth(40f);
        correctPaint.setAntiAlias(true);

        wrongPaint.setColor(0xFFF44336); // red
        wrongPaint.setStyle(Paint.Style.STROKE);
        wrongPaint.setStrokeWidth(40f);
        wrongPaint.setAntiAlias(true);

        textPaint.setColor(0xFF000000);
        textPaint.setTextSize(64f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
    }

    public void setStats(int correct, int wrong) {
        int total = correct + wrong;
        if (total > 0)
            correctPercent = (float) correct / total;
        else
            correctPercent = 0;
        invalidate(); // redraw
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float size = Math.min(getWidth(), getHeight()) - 60f;
        float left = (getWidth() - size) / 2;
        float top = (getHeight() - size) / 2;
        RectF oval = new RectF(left, top, left + size, top + size);

        // wrong (background)
        canvas.drawArc(oval, -90, 360, false, wrongPaint);
        // correct portion
        canvas.drawArc(oval, -90, 360 * correctPercent, false, correctPaint);

        // percentage text
        String text = String.format("%.0f%%", correctPercent * 100);
        canvas.drawText(text, getWidth() / 2f, getHeight() / 2f + 20f, textPaint);
    }
}
