package uk.ac.napier.a2048game.view;

/**
 * Created by Administrator on 2018/3/9.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import uk.ac.napier.a2048game.MainActivity;


public class Game2048Layout extends GridLayout{


    private int mColumn = 5;

    private Game2048Item[][] gameItems ;

    private int mMargin = 10;

    private int mPadding;

    private GestureDetector mGestureDetector;


    private boolean isMergeHappen = false;

    private boolean isMoveHappen = false;

    private int mScore = 0;

    private int childWidth;


    private onGame2048Listener mGame2048Listener;


    private boolean isFirst = true;



    private enum ACTION {
        LEFT,RIGHT,UP,DOWM
    }


    public Game2048Layout(Context context) {
        this(context,null);
    }

    public Game2048Layout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public Game2048Layout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,mMargin,getResources().getDisplayMetrics());

        mPadding = min(getPaddingLeft(),getPaddingTop(),getPaddingRight(),getPaddingBottom());
        mGestureDetector = new GestureDetector(context,new MyGestureDetector());

    }


    private int min(int... params){
        int min = params[0];
        for(int param:params){
            if(min > param){
                min = param;
            }
        }
        return min;
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return true;
    }


    private class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {


        final int FLING_MIN_DISTANCE = 50;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            float x = e2.getX() - e1.getX();
            float y = e2.getY() - e1.getY();

            float absX = Math.abs(velocityX);
            float absY = Math.abs(velocityY);


            if(x > FLING_MIN_DISTANCE && absX > absY){
                action(ACTION.RIGHT);
            }else if( x < -FLING_MIN_DISTANCE && absX > absY){

                action(ACTION.LEFT);
            }else if( y > FLING_MIN_DISTANCE && absX < absY){

                action(ACTION.DOWM);
            }else if(y < -FLING_MIN_DISTANCE && absX < absY){

                action(ACTION.UP);
            }
            return true;
        }
    }


    private void action(ACTION action) {
        System.out.println("action:" + action);

        for(int i = 0 ; i < mColumn; i++){

            List<Game2048Item> rowTemp = new ArrayList<>();
            for(int j = 0;j < mColumn; j++){

                int rowIndex = getRowIndexByAction(action,i,j);
                int colindex = getColIndexByAction(action,i,j);
                Game2048Item item = gameItems[rowIndex][colindex];

                if(item.getNumber() != 0){
                    rowTemp.add(item);
                }
            }


            for(int j = 0 ; j < rowTemp.size(); j++){
                int rowIndex = getRowIndexByAction(action,i,j);
                int colIndex = getColIndexByAction(action,i,j);

                Game2048Item item = gameItems[rowIndex][colIndex];

                if(item.getNumber() != rowTemp.get(j).getNumber()){
                    isMoveHappen = true;
                }
            }


            mergeItem(rowTemp);

            for(int j = 0; j < mColumn; j++){
                if(rowTemp.size() > j){
                    int number = rowTemp.get(j).getNumber();
                    switch (action){
                        case LEFT:
                            gameItems[i][j].setNumber(number);
                            break;
                        case RIGHT:

                            gameItems[i][mColumn - j - 1].setNumber(number);
                            break;
                        case UP:
                            gameItems[j][i].setNumber(number);
                            break;
                        case DOWM:
                            gameItems[mColumn - j - 1][i].setNumber(number);
                            break;
                    }
                }else{

                    switch (action){
                        case LEFT:
                            gameItems[i][j].setNumber(0);
                            break;
                        case RIGHT:
                            gameItems[i][mColumn - j - 1].setNumber(0);
                            break;
                        case UP:
                            gameItems[j][i].setNumber(0);
                            break;
                        case DOWM:
                            gameItems[mColumn - j - 1][i].setNumber(0);
                            break;

                    }

                }
            }
        }

        generateNum();
    }


    private void mergeItem(List<Game2048Item> rowTemp) {

        if (rowTemp.size() < 2){
            return;
        }

        boolean isStop = true;
        while(isStop){
            for(int i = 0; i < rowTemp.size() - 1; i++){
                Game2048Item item1 = rowTemp.get(i);
                Game2048Item item2 = rowTemp.get(i + 1);

                if(item1.getNumber() == item2.getNumber()){

                    isMergeHappen = true;
                    int val = item1.getNumber() + item2.getNumber();

                    mScore += val;

                    item1.setNumber(val);
                    mGame2048Listener.onScoreChange(mScore);

                    for(int j = i + 1;j < rowTemp.size() - 1;j++){
                        rowTemp.get(j).setNumber(rowTemp.get(j + 1).getNumber());
                    }

                    rowTemp.get(rowTemp.size() - 1).setNumber(0);
                }
            }

            boolean isSame = true;

            for(int i = 0;i < rowTemp.size() - 1; i++){
                Game2048Item item1 = rowTemp.get(i);
                Game2048Item item2 = rowTemp.get(i + 1);
                if(item1.getNumber() == item2.getNumber() &&
                        item1.getNumber() != 0 ){
                    isSame = false;
                    break;
                }
            }
            if(isSame){

                isStop = false;
            }
        }
    }


    private int getRowIndexByAction(ACTION action,int i,int j){
        int rowIndex = -1;
        switch (action){
            case UP:

                rowIndex = j;
                break;
            case DOWM:

                rowIndex = mColumn - j - 1;
                break;

            case LEFT:
            case RIGHT:
                rowIndex = i;
                break;
        }
        return rowIndex;
    }


    private int getColIndexByAction(ACTION action,int i,int j){
        int ColIndex = -1;
        switch (action){
            case UP:
            case DOWM:

                ColIndex = i;
                break;
            case RIGHT:

                ColIndex = mColumn - j - 1;
                break;
            case LEFT:

                ColIndex = j;
                break;
        }
        return ColIndex;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int length = Math.min(MeasureSpec.getSize(widthMeasureSpec),MeasureSpec.getSize(heightMeasureSpec));

        childWidth = (length - mPadding * 2 - mMargin * (mColumn - 1)) / mColumn;

        setMeasuredDimension(length,length);
    }


    private boolean once  = false;
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if(!once){
            if(gameItems == null){

                gameItems = new Game2048Item[mColumn][mColumn];
            }
            for(int i = 0; i < mColumn; i++){
                for(int j = 0; j < mColumn; j++){

                    Game2048Item item = new Game2048Item(getContext());
                    gameItems[i][j] = item;


                    Spec x = GridLayout.spec(i);
                    Spec y = GridLayout.spec(j);
                    GridLayout.LayoutParams lp = new LayoutParams(x,y);

                    lp.height = childWidth;
                    lp.width = childWidth;
                    if( (j + 1) != mColumn){

                        lp.rightMargin = mMargin;
                    }
                    if( i > 0){

                        lp.topMargin = mMargin;
                    }

                    lp.setGravity(Gravity.FILL);
                    addView(item,lp);
                }
            }

            generateNum();
        }
        once = true;
    }




    private void generateNum() {

        if(isGameOver()){
            Log.e("info", "GAME OVER");
            if(mGame2048Listener != null){
                mGame2048Listener.onGameOver();
            }
            return;
        }


        if(isFirst){
            for(int i = 0 ; i < 4; i++){
                int x = new Random().nextInt(mColumn );
                int y = new Random().nextInt(mColumn );
                Game2048Item item = gameItems[x][y];
                while (item.getNumber() != 0){

                    x = new Random().nextInt(mColumn);
                    y = new Random().nextInt(mColumn);
                    item = gameItems[x][y];
                }
                item.setNumber(Math.random() > 0.7 ? 4:2);

                Animation scaleAnimation = new ScaleAnimation(0,1,0,1,
                        Animation.RELATIVE_TO_SELF,0.5F,Animation.RELATIVE_TO_SELF,0.5f);
                scaleAnimation.setDuration(200);
                item.startAnimation(scaleAnimation);
            }
            isMoveHappen = isMergeHappen = false;
            isFirst = false;
        }
        if(isMoveHappen && !isMergeHappen){

            int x = new Random().nextInt(mColumn);
            int y = new Random().nextInt(mColumn);
            Game2048Item item = gameItems[x][y];
            while (item.getNumber() != 0){

                x = new Random().nextInt(mColumn);
                y = new Random().nextInt(mColumn);
                item = gameItems[x][y];
            }

            item.setNumber(Math.random() > 0.75 ? 4:2 );
        }
        isMergeHappen = isMoveHappen = false;
    }


    private boolean ifFull() {
        for(int i = 0; i < mColumn; i++){
            for(int j = 0 ; j < mColumn; j++){
                Game2048Item item = gameItems[i][j];
                if(item.getNumber() == 0){
                    return false;
                }
            }
        }
        return true;
    }


    private boolean isGameOver(){

        if(!ifFull()){
            return false;
        }

        for(int i = 0 ; i < mColumn ; i++){
            for(int j = 0; j < mColumn ; j++){
                Game2048Item item = gameItems[i][j];

                if( (j + 1) != mColumn){
                    Game2048Item itemRight = gameItems[i][j + 1];
                    if(item.getNumber() == itemRight.getNumber()){
                        return false;
                    }
                }

                if( j != 0){
                    Game2048Item itemLeft = gameItems[i][j - 1];
                    if(item.getNumber() == itemLeft.getNumber()){
                        return false;
                    }
                }

                if( (i + 1) != mColumn){
                    Game2048Item itemBottom = gameItems[i + 1][j];
                    if(item.getNumber() == itemBottom.getNumber()){
                        return false;
                    }
                }

                if( i != 0){
                    Game2048Item itemTop = gameItems[i - 1][j];
                    if(item.getNumber() == itemTop.getNumber()){
                        return false;
                    }
                }
            }
        }
        return true;
    }



    public interface onGame2048Listener{

        void onScoreChange(int score);

        void onGameOver();
    }

    public void setmGame2048Listener(onGame2048Listener mGame2048Listener) {
        this.mGame2048Listener = mGame2048Listener;
    }


    public void reStart(){
        for(int i = 0; i < mColumn; i++){
            for(int j = 0 ; j < mColumn; j++){
                Game2048Item item = gameItems[i][j];
                item.setNumber(0);
            }
        }
        mScore = 0;
        mGame2048Listener.onScoreChange(0);
        isFirst = true;
        generateNum();
    }
}