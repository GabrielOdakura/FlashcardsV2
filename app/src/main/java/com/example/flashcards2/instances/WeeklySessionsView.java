package com.example.flashcards2.instances;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.flashcards2.utils.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class WeeklySessionsView extends View {

    private final Paint barPaint = new Paint();
    private final Paint axisPaint = new Paint();
    private final Paint textPaint = new Paint();

    private Map<String, Integer> sessionsPerDay = new HashMap<>();
    private int maxSessions = 1;
    private DatabaseHelper db;
    private int userId;

    public WeeklySessionsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        barPaint.setColor(0xFF4CAF50);
        barPaint.setStyle(Paint.Style.FILL);
        barPaint.setAntiAlias(true);

        axisPaint.setColor(0xFF999999);
        axisPaint.setStrokeWidth(4f);
        axisPaint.setAntiAlias(true);

        textPaint.setColor(0xFF000000);
        textPaint.setTextSize(32f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
    }

    public void init(DatabaseHelper db, int userId) {
        this.db = db;
        this.userId = userId;
        loadData();
    }

    private void loadData() {
        if (db == null) return;

        // initialize all 7 days with 0
        sessionsPerDay.clear();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Calendar cal = Calendar.getInstance();
        for (int i = 6; i >= 0; i--) {
            cal.setTimeInMillis(System.currentTimeMillis());
            cal.add(Calendar.DAY_OF_YEAR, -i);
            sessionsPerDay.put(sdf.format(cal.getTime()), 0);
        }

        // query DB
        Cursor c = db.getWeeklySessions(userId);
        if (c != null && c.moveToFirst()) {
            do {
                String day = c.getString(c.getColumnIndexOrThrow("day"));
                int count = c.getInt(c.getColumnIndexOrThrow("sessions"));
                sessionsPerDay.put(day, count);
                if (count > maxSessions) maxSessions = count;
            } while (c.moveToNext());
            c.close();
        }

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (sessionsPerDay.isEmpty()) return;

        float width = getWidth();
        float height = getHeight();
        float padding = 60f;
        float barWidth = (width - padding * 2) / 7f;

        float chartBottom = height - 100f;
        float chartTop = padding;

        // Draw bars
        int i = 0;
        SimpleDateFormat displayFormat = new SimpleDateFormat("EEE", Locale.US);
        Calendar cal = Calendar.getInstance();

        for (String date : sessionsPerDay.keySet()) {
            int count = sessionsPerDay.get(date);
            float ratio = (float) count / maxSessions;
            float barHeight = (chartBottom - chartTop) * ratio;

            float left = padding + i * barWidth + barWidth * 0.2f;
            float right = left + barWidth * 0.6f;
            float top = chartBottom - barHeight;

            // draw bar
            RectF bar = new RectF(left, top, right, chartBottom);
            canvas.drawRoundRect(bar, 12f, 12f, barPaint);

            // label
            try {
                cal.setTime(new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(date));
                String dayLabel = displayFormat.format(cal.getTime());
                canvas.drawText(dayLabel, (left + right) / 2f, height - 40f, textPaint);
            } catch (Exception ignored) {}

            i++;
        }

        // Axis line
        canvas.drawLine(padding, chartBottom, width - padding, chartBottom, axisPaint);
    }
}
