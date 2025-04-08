package com.example.slasher.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.slasher.modular.OnStepClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class CanvasView extends View {

    private Map<Integer, List<Integer>> stepsMap = new HashMap<>();
    private final Map<Integer, float[]> stepPositions = new HashMap<>();
    private Paint nodePaint, linePaint, textPaint;
    private float scaleFactor = 1.0f;
    private float offsetX = 0;
    private float offsetY = 0;
    private float lastTouchX = 0;
    private float lastTouchY = 0;
    private long touchStartTime = 0;
    private ScaleGestureDetector scaleGestureDetector;
    private OnStepClickListener stepClickListener;
    private OnStepClickListener stepLongPressListener;

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        nodePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        nodePaint.setColor(Color.RED);
        nodePaint.setStyle(Paint.Style.FILL);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.RED);
        linePaint.setStrokeWidth(5);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(40);
        textPaint.setTextAlign(Paint.Align.CENTER);

        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(@NonNull ScaleGestureDetector detector) {
                scaleFactor = Math.max(0.5f, Math.min(scaleFactor * detector.getScaleFactor(), 3.0f));
                invalidate();
                return true;
            }
        });
    }

    public void setOnStepClickListener(OnStepClickListener listener) {
        this.stepClickListener = listener;
    }

    public void setOnStepLongPressListener(OnStepClickListener listener) {
        this.stepLongPressListener = listener;
    }

    public void setStepsMap(Map<Integer, List<Integer>> stepsMap) {
        this.stepsMap = stepsMap;
        arrangeSteps();

        // **Ne állítsa be újra az eltolást, ha már van értéke!**
        if (this.offsetX == 0 && this.offsetY == 0) {
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 3;
            setOffset(centerX, centerY);
        }

        invalidate();
    }

    private void arrangeSteps() {
        if (stepsMap.isEmpty()) return;

        int screenWidth = getWidth();
        int screenHeight = getHeight();
        int centerX = screenWidth / 2;
        int centerY = screenHeight / 3;

        stepPositions.clear();
        stepPositions.put(0, new float[]{centerX, centerY});

        Map<Integer, Integer> depthLevels = new HashMap<>();
        depthLevels.put(0, 0);

        Set<Integer> processedSteps = new HashSet<>(); // **Ciklusok megakadályozása**
        Queue<Integer> queue = new LinkedList<>();
        queue.add(0);

        while (!queue.isEmpty()) {
            int step = queue.poll();
            if (processedSteps.contains(step)) continue; // **Ne dolgozzuk fel többször!**
            processedSteps.add(step);

            int depth = depthLevels.get(step);
            List<Integer> children = stepsMap.getOrDefault(step, new ArrayList<>());

            float parentX = stepPositions.get(step)[0];
            float parentY = stepPositions.get(step)[1];
            int totalChildren = children.size();
            int spacing = Math.max(100, 200 - (depth * 10));

            for (int i = 0; i < totalChildren; i++) {
                int childStep = children.get(i);

                if (!processedSteps.contains(childStep)) { // **Ellenőrizzük, hogy már feldolgoztuk-e**
                    queue.add(childStep);
                    depthLevels.put(childStep, depth + 1);

                    float childX = parentX + ((i - totalChildren / 2) * spacing);
                    float childY = parentY + 180;

                    stepPositions.put(childStep, new float[]{childX, childY});
                }
            }
        }

        post(() -> invalidate());
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.scale(scaleFactor, scaleFactor);
        canvas.translate(offsetX, offsetY);

        if (stepsMap.isEmpty()) {
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 4;
            drawNode(canvas, centerX, centerY, "Start");
            canvas.restore();
            return;
        }

        for (Map.Entry<Integer, float[]> entry : stepPositions.entrySet()) {
            int step = entry.getKey();
            float x = entry.getValue()[0];
            float y = entry.getValue()[1];

            if (stepsMap.containsKey(step)) {
                for (Integer childStep : stepsMap.get(step)) {
                    if (stepPositions.containsKey(childStep)) {
                        float childX = stepPositions.get(childStep)[0];
                        float childY = stepPositions.get(childStep)[1];
                        drawLine(canvas, x, y, childX, childY);
                    }
                }
            }
        }

        for (Map.Entry<Integer, float[]> entry : stepPositions.entrySet()) {
            int step = entry.getKey();
            float x = entry.getValue()[0];
            float y = entry.getValue()[1];
            drawNode(canvas, x, y, String.valueOf(step));
        }

        canvas.restore();
    }

    private void drawNode(Canvas canvas, float x, float y, String label) {
        canvas.drawCircle(x, y, 60, nodePaint);
        canvas.drawText(label, x, y + 15, textPaint);
    }

    private void drawLine(Canvas canvas, float startX, float startY, float endX, float endY) {
        canvas.drawLine(startX, startY, endX, endY, linePaint);
    }
/*
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float touchX = (event.getX() - offsetX) / scaleFactor;
            float touchY = (event.getY() - offsetY) / scaleFactor;

            for (Map.Entry<Integer, float[]> entry : stepPositions.entrySet()) {
                int step = entry.getKey();
                float stepX = entry.getValue()[0];
                float stepY = entry.getValue()[1];

                if (Math.abs(touchX - stepX) < 60 && Math.abs(touchY - stepY) < 60) {
                    if (stepClickListener != null) {
                        stepClickListener.onStepClick(step);
                        return true;
                    }
                }
            }
        }

        return super.onTouchEvent(event);
    }
*/
// **Globális változók a húzási pozícióhoz**
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);

        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchX = touchX;
                lastTouchY = touchY;
                touchStartTime = System.currentTimeMillis(); // **Mentjük az érintés időpontját**
                return true;

            case MotionEvent.ACTION_MOVE:
                float deltaX = touchX - lastTouchX;
                float deltaY = touchY - lastTouchY;

                // **Csak akkor mozgassuk a térképet, ha a mozgás elég nagy**
                if (Math.abs(deltaX) > 15 || Math.abs(deltaY) > 15) {
                    offsetX += deltaX;
                    offsetY += deltaY;

                    lastTouchX = touchX;
                    lastTouchY = touchY;

                    invalidate(); // **Frissítjük a nézetet**
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
                long touchDuration = System.currentTimeMillis() - touchStartTime;

                if (touchDuration < 1000 && Math.abs(touchX - lastTouchX) < 15 && Math.abs(touchY - lastTouchY) < 15) {

                    touchX = (event.getX() - offsetX) / scaleFactor;
                    touchY = (event.getY() - offsetY) / scaleFactor;

                    for (Map.Entry<Integer, float[]> entry : stepPositions.entrySet()) {
                        int step = entry.getKey();
                        float stepX = entry.getValue()[0];
                        float stepY = entry.getValue()[1];

                        if (Math.abs(touchX - stepX) < 60 && Math.abs(touchY - stepY) < 60) {
                            if (stepClickListener != null) {
                                stepClickListener.onStepClick(step);
                                return true;
                            }
                        }
                    }
                }
                return true;
        }

        return super.onTouchEvent(event);
    }


    public int getOffsetX() {
        return (int) offsetX;
    }

    public int getOffsetY() {
        return (int) offsetY;
    }

    public void setOffset(int offsetX, int offsetY) {
        if (this.offsetX != offsetX || this.offsetY != offsetY) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            invalidate();
        }
    }
}
