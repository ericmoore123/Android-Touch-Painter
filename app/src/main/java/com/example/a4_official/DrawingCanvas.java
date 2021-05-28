package com.example.a4_official;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Iterator;
import java.util.LinkedList;

public class DrawingCanvas extends View {

    private Paint paint;
    private Path path;

    LinkedList<Paint> paintsList;
    LinkedList<Path> pathsList;

//    Drawing Modes
    boolean rectangleMode;
    boolean circleMode;
    boolean lineMode;

    public static int pathColor;

    public DrawingCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

//        Save all stroke and color data to lists
        pathsList = new LinkedList<>();
        paintsList = new LinkedList<>();

//        Declare custom shape linkedLists
        Circle.circleList = new LinkedList<>();
        Rectangle.rectangleList = new LinkedList<>();

        paint = new Paint();
        paint.setColor(pathColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(10);
        path = new Path();
    }

    @Override
    public void onDraw(Canvas canvas){

//        Paths ondraw handler
        if(pathsList.size() > 0){
            for(int i = 0; i < pathsList.size(); i++){
                canvas.drawPath(pathsList.get(i), paintsList.get(i));
                super.onDraw(canvas);
            }
        }

//        Circle mode ondraw handler
        if(Circle.circleList.size() > 0){
            for(int i = 0; i < Circle.circleList.size(); i++){
                canvas.drawCircle(Circle.circleList.get(i).posX, Circle.circleList.get(i).posY, Circle.circleList.get(i).radius, paintsList.get(i));
                super.onDraw(canvas);
            }
        }

//        Attempt at implementing rectangle mode
//        if(Rectangle.rectangleList.size() > 0){
//            for(int i = 0; i < Rectangle.rectangleList.size(); i++){
//                canvas.drawRect();
//                super.onDraw(canvas);
//            }
//        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent touchEvent){
//        Gets number of touch points on screen (fingers on screen)
        int touchCounter = touchEvent.getPointerCount();

//        Line mode component handler
        if(lineMode && pathsList.size() > 0){
            paint.setColor(pathColor);
            pathsList.getLast().lineTo(touchEvent.getX(), touchEvent.getY());
            paintsList.addLast(paint);

            invalidate();
        }else if(circleMode){

            paint.setColor(pathColor);
            Circle.circleList.add(new Circle(this.getContext(), touchEvent.getX(0), touchEvent.getY(0), 100 ));
            paintsList.addLast(paint);

            invalidate();
//            A single finger on screen listener
        } else if(touchCounter == 1){
                switch (touchEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        paint.setColor(pathColor);
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeJoin(Paint.Join.ROUND);
                        paint.setStrokeCap(Paint.Cap.ROUND);
                        paint.setStrokeWidth(10);

                        pathsList.addLast(path);
                        paintsList.addLast(paint);

                        //                Moves paint to clicked position
                        pathsList.getLast().moveTo(touchEvent.getX(), touchEvent.getY());
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //                Draw to position from old position
                        pathsList.getLast().lineTo(touchEvent.getX(), touchEvent.getY());
                        //                Call action on queue for onDraw
                        invalidate();
                        break;
                    case MotionEvent.ACTION_UP:
//                        When clicked off screen
                        paint = new Paint();
                        path = new Path();

                        break;
                }
//            Two fingers on screen listener

        }else if(touchCounter == 2){
                switch(touchEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        paint.setColor(pathColor);
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeJoin(Paint.Join.ROUND);
                        paint.setStrokeCap(Paint.Cap.ROUND);
                        paint.setStrokeWidth(10);

                        pathsList.addLast(path);
                        paintsList.addLast(paint);

//                        Create start at first finger (at index 0)
                        pathsList.getLast().moveTo(touchEvent.getX(0), touchEvent.getY(0));
//                       Create line to second finger at index 1
                        pathsList.getLast().lineTo(touchEvent.getX(1), touchEvent.getY(1));
                        invalidate();

                        break;
                    case MotionEvent.ACTION_MOVE:
//                        Simply creates more lines between points at each point in movement
                        pathsList.getLast().moveTo(touchEvent.getX(0), touchEvent.getY(0));
                        pathsList.getLast().lineTo(touchEvent.getX(1), touchEvent.getY(1));
                        invalidate();

                        break;
                }
//                3 Fingers on screen listener
            }else if(touchCounter == 3){
                switch (touchEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        paint.setColor(pathColor);
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeJoin(Paint.Join.ROUND);
                        paint.setStrokeCap(Paint.Cap.ROUND);
                        paint.setStrokeWidth(10);

                        pathsList.addLast(path);
                        paintsList.addLast(paint);

//                        Create start at first finer (at index 0)
                        pathsList.getLast().moveTo(touchEvent.getX(0), touchEvent.getY(0));
//                       Create line to second finger at index 1
                        pathsList.getLast().lineTo(touchEvent.getX(1), touchEvent.getY(1));

                        pathsList.getLast().moveTo(touchEvent.getX(1), touchEvent.getY(1));
                        pathsList.getLast().lineTo(touchEvent.getX(2), touchEvent.getY(2));
                        invalidate();

                        break;
                    case MotionEvent.ACTION_MOVE:
//                        Simply creates more lines between points at each point in movement
                        pathsList.getLast().moveTo(touchEvent.getX(0), touchEvent.getY(0));
                        pathsList.getLast().lineTo(touchEvent.getX(1), touchEvent.getY(1));

                        pathsList.getLast().moveTo(touchEvent.getX(1), touchEvent.getY(1));
                        pathsList.getLast().lineTo(touchEvent.getX(2), touchEvent.getY(2));
                        invalidate();

                        break;
                }
            }

        return true;
    }
}
