package com.example.asus.hoodle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.display.DisplayManager;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.method.Touch;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;


import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    //弹珠的移动速度
    public int speed_x=1;
    public int speed_y=1;

    //弹珠的初始位置
    public int bead_X;
    public int bead_Y;

    //屏幕的尺寸
    public int screen_X;
    public int screen_Y;

    //记录手机滑动的距离
    public int Touch_X;

    //记录结束的计时
    public Boolean time_boolean=false;
    public int time=3;

    //记录游戏是否结束
    public Boolean isFinish=false;

    private Display display;
    public GameView gameView;
    private DisplayMetrics displayMetrics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        gameView=new GameView(this);
        setContentView(gameView);

        final MyHandler handler=new MyHandler();

        Touch_X=100;
        bead_X=200;
        bead_Y=200;
        speed_x=1;
        speed_y=1;
        //得到屏幕的尺寸
        display=getWindowManager().getDefaultDisplay();
        displayMetrics=new DisplayMetrics();
        display.getMetrics(displayMetrics);

        screen_Y=displayMetrics.heightPixels;
        screen_X=displayMetrics.widthPixels;

//        //弹珠的游戏进程
        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (bead_X <= 20) {            //弹珠碰到左墙
                    speed_x = -speed_x;
                    bead_X += speed_x;
                    bead_Y += speed_y;
                    handler.sendEmptyMessage(0x1);
                } else if (bead_Y <= 20) {
                    speed_y = -speed_y;
                    bead_X += speed_x;
                    bead_Y += speed_y;
                    handler.sendEmptyMessage(0x1);
                } else if (bead_X >= screen_X - 60) {
                    speed_x = -speed_x;
                    bead_X += speed_x;
                    bead_Y += speed_y;
                    handler.sendEmptyMessage(0x1);
                } else if (bead_Y == (screen_Y - screen_Y/12) && bead_X >= Touch_X - 60 && bead_X <= Touch_X + 60) {
                    speed_y = -speed_y;
                    bead_X += speed_x;
                    bead_Y += speed_y;
                    handler.sendEmptyMessage(0x1);
                } else if (bead_Y > screen_Y - screen_Y/12) {
                    handler.sendEmptyMessage(0x2);
//                    timer.cancel();
//                    isFinish=true;
                } else {
                    bead_X += speed_x;
                    bead_Y += speed_y;
                    handler.sendEmptyMessage(0x1);
                }

            }
        },10,8);
    }


    //自定义Handler
    class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x1:
                    gameView.invalidate();

                    break;
                case 0x2:
                    isFinish = true;
                    gameView.invalidate();
                    final Timer timer2 = new Timer();
                    time_boolean=true;
                    timer2.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (time==0){
                                timer2.cancel();
                                Intent intent=new Intent(MainActivity.this,Entrance.class);
                                startActivity(intent);
                            }else{
                                gameView.invalidate();
                            }
                        }
                    },300,300);

                    break;

            }
        }
    }


    class GameView extends View {

        Paint paint=new Paint();
        public GameView(Context context) {
            super(context);
//            setFocusable(true);
            setBackgroundColor(Color.BLACK);
        }


//        滑动控制短板左右移动
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            int i= (int) event.getX();
            if(i<60||i>screen_X-60){
                //不做任何动作
            }else{
                Touch_X=i;
                invalidate();
            }
            return true;
        }

        @Override
        protected void onDraw(Canvas canvas) {

            if(isFinish){
                Paint paint_text=new Paint();
                paint_text.setColor(Color.RED);
                paint_text.setTextSize(60);
                canvas.drawText("游戏结束",screen_X/2-100,screen_X/2,paint_text);
            }else if(time_boolean==true) {
                Paint paint_text=new Paint();
                paint_text.setColor(Color.RED);
                paint_text.setTextSize(60);
                canvas.drawText(time+"",screen_X/2-100,screen_X/2,paint_text);
                --time;


            }else
             {
                paint.setStyle(Paint.Style.FILL);
                paint.setAntiAlias(true);
                paint.setColor(Color.RED);
                canvas.drawRect(Touch_X - 60,screen_Y -screen_Y/12, Touch_X + 60, screen_Y, paint);
                canvas.drawCircle(bead_X, bead_Y, 20, paint);
            }
        }
    }
}