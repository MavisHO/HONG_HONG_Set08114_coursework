package uk.ac.napier.a2048game.view;

/**
 * Created by Administrator on 2018/3/9.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;


public class Game2048Item extends View{

    private Paint paint;
    private int mNumber;
    private String mNumberVal;
    private int fontSize = 100;
    private Rect mBound;

    public Game2048Item(Context context) {
        this(context,null);
    }

    public Game2048Item(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public Game2048Item(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
    }



    public int getNumber(){
        return mNumber;
    }


    public void setNumber(int mNumber) {
        this.mNumber = mNumber;
        mNumberVal = mNumber + "";

        paint.setTextSize(fontSize);
        mBound = new Rect();

        paint.getTextBounds(mNumberVal,0,mNumberVal.length(),mBound);

        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        String mBgColor = "#EA7821";
        switch (mNumber){
            case 0:
                mBgColor = "#CCC0B3";
                break;
            case 2:
                mBgColor = "#EEE4DA";
                break;
            case 4:
                mBgColor = "#EDE0C8";
                break;
            case 8:
                mBgColor = "#F2B179";
                break;
            case 16:
                mBgColor = "#F49563";
                break;
            case 32:
                mBgColor = "#F57940";
                break;
            case 64:
                mBgColor = "#F55D37";
                break;
            case 128:
                mBgColor = "#EEE863";
                break;
            case 256:
                mBgColor = "#EDB040";
                break;
            case 512:
                mBgColor = "#ECB040";
                break;
            case 1024:
                mBgColor = "#EB9437";
                break;
            case 2048:
                mBgColor = "EA7821";
                break;
            default:
                mBgColor = "#EA7821";
                break;
        }

        paint.setColor(Color.parseColor(mBgColor));

        paint.setStyle(Paint.Style.FILL);

        canvas.drawRect(0,0,getWidth(),getHeight(),paint);

        if(mNumber != 0){
            drawText(canvas);
        }
    }


    private void drawText(Canvas canvas) {
        paint.setColor(Color.BLACK);
        float x = (getWidth() - mBound.width())/2;
        float y = getHeight()/2 + mBound.height()/2;
        canvas.drawText(mNumberVal,x,y,paint);
    }


    public void setFontSize(int size){
        fontSize = size;
    }
}
