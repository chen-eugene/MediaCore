package com.eugene.mediacore.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SurfaceViewTemplate extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private SurfaceHolder surfaceHolder;
    private Canvas canvas;
    private boolean isDrawing;
    private Paint paint;
    private Path path;
    private int x, y;


    public SurfaceViewTemplate(Context context) {
        super(context);
    }

    public SurfaceViewTemplate(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        initPaint();
    }

    public SurfaceViewTemplate(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    private void init() {
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        setFocusable(true);
        setKeepScreenOn(true);
        setFocusableInTouchMode(true);
    }

    private void initPaint() {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        path = new Path();
        path.moveTo(0, 100);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isDrawing = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isDrawing = false;
    }

    @Override
    public void run() {
        while (isDrawing) {
            drawSomething();
            x += 1;
            y = (int)(100 * Math.sin(2 * x * Math.PI / 180) + 400);
            //加入新的坐标点
            path.lineTo(x, y);
        }
    }

    private void drawSomething() {
        try {
            //获得canvas对象
            canvas = surfaceHolder.lockCanvas();
            //绘制背景
            canvas.drawColor(Color.WHITE);
            //绘图
            canvas.drawPath(path, paint);
        } catch (Exception e) {

        } finally {
            if (canvas != null) {
                //释放canvas对象并提交画布
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }
}
