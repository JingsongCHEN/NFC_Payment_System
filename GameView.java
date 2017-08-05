package com.example.sanguo;






import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import java.io.InputStream;

public class GameView extends View {

    ///////////////////////////////////////
    static int flag=1;
    static int [][]pos = new int [2][50];
    static int old_touch=1;
    static int ox = 0, oy = 0;
    ///////////////////////////////////////

    //map
    Chess mychess = new Chess();

    //type:-2--界外，-1--结盟点，0--空地，1--卒，2--炮，3--车，4--马，5--象，6--士，7--将
    //owner:-1--default(无子的地方)，0--汉献帝，1--魏，2--蜀，3--吴
    //map
    private final int[] ownermap = {3, 2, 0, 1};
    private final int[] mymap = {0, 6, 5, 3, 4, 2, 1, 0};
    //屏幕的宽和高
    private int screenWidth = getWidth();
    private int screenHeight = getHeight();
    //画棋盘的起始位置
    private int startX;
    private int startY = 10;
    //棋盘中每个格子的高和宽
    private float GRID_WIDTH;

    private int GRID_NUM = 16;//要画的棋盘中的线数

    private Paint paint = null;

    private Bitmap[][] chess_entity = new Bitmap[4][7];

    //以最省资源的方式读取图片
    public Bitmap readBitMap(Context context, int resId){

        BitmapFactory.Options opt = new  BitmapFactory.Options();

        opt.inPreferredConfig =  Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;



        //  获取资源图片
        InputStream is =  context.getResources().openRawResource(resId);

        opt.inSampleSize = 25;//参数自行调整

        return  BitmapFactory.decodeStream(is, null, opt);
    }

    public GameView(Context context) {
        super(context);

        //计算一个格子的边长
        GRID_WIDTH = (float)(screenHeight - 2 * startY) / GRID_NUM;
        startX = (screenWidth - (screenHeight - 2 * startY)) / 2;

        paint = new Paint();//实例化一个画笔
        paint.setColor(Color.BLACK);//设置画笔的颜色
        paint.setAntiAlias(true);//设置画笔去锯齿，没有此语句，画的线或图片周围不圆滑

        
        //加载蜀国红色棋子
        chess_entity[0][0] = readBitMap(context, R.drawable.red_shu);
        chess_entity[0][1] = readBitMap(context, R.drawable.red_shi);
        chess_entity[0][2] = readBitMap(context, R.drawable.red_xiang);
        chess_entity[0][3] = readBitMap(context, R.drawable.red_ju);
        chess_entity[0][4] = readBitMap(context, R.drawable.red_ma);
        chess_entity[0][5] = readBitMap(context, R.drawable.red_pao);
        chess_entity[0][6] = readBitMap(context, R.drawable.red_bing);

        //加载吴国绿色棋子
        chess_entity[1][0] = readBitMap(context, R.drawable.green_wu);
        chess_entity[1][1] = readBitMap(context, R.drawable.green_shi);
        chess_entity[1][2] = readBitMap(context, R.drawable.green_xiang);
        chess_entity[1][3] = readBitMap(context, R.drawable.green_ju);
        chess_entity[1][4] = readBitMap(context, R.drawable.green_ma);
        chess_entity[1][5] = readBitMap(context, R.drawable.green_pao);
        chess_entity[1][6] = readBitMap(context, R.drawable.green_bing);

        //加载魏国黑色棋子
        chess_entity[2][0] = readBitMap(context, R.drawable.black_wei);
        chess_entity[2][1] = readBitMap(context, R.drawable.black_shi);
        chess_entity[2][2] = readBitMap(context, R.drawable.black_xiang);
        chess_entity[2][3] = readBitMap(context, R.drawable.black_ju);
        chess_entity[2][4] = readBitMap(context, R.drawable.black_ma);
        chess_entity[2][5] = readBitMap(context, R.drawable.black_pao);
        chess_entity[2][6] = readBitMap(context, R.drawable.black_zu);

        //加载汉国棋子
        chess_entity[3][0] = readBitMap(context, R.drawable.yellow_han);
        chess_entity[3][1] = readBitMap(context, R.drawable.yellow_han);
        chess_entity[3][2] = readBitMap(context, R.drawable.yellow_han);
        chess_entity[3][3] = readBitMap(context, R.drawable.yellow_ju);
        chess_entity[3][4] = readBitMap(context, R.drawable.yellow_han);
        chess_entity[3][5] = readBitMap(context, R.drawable.yellow_pao);
        chess_entity[3][6] = readBitMap(context, R.drawable.yellow_han);

    }


    @Override
    protected void onDraw(Canvas canvas) {//重写View中的该方法，该方法主要承担绘图的工作，每刷新一次，就调用一次该方法
        super.onDraw(canvas);

        canvas.drawColor(Color.WHITE);//把屏幕的底色绘成黄色，此处不仅仅是起把屏幕绘成这种颜色的作用，还有刷屏的作用，对以前绘制的进行清除
        paint.setColor(Color.BLACK);//此处是把画笔变成绿色，是绘制的棋盘变成绿色

        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.STROKE);

        screenWidth = getWidth();
        screenHeight = getHeight();

        //计算一个格子的边长
        GRID_WIDTH = (float)(screenHeight - 2 * startY) / GRID_NUM;

        startX = (screenWidth - (screenHeight - 2 * startY)) / 2;
        int borderX = screenWidth - startX;
        int borderY = screenHeight - startY;

        for (int i=0;i<=GRID_NUM;i++)
        {
            //画横线
            canvas.drawLine(startX, startY + i * GRID_WIDTH, borderX , startY + i * GRID_WIDTH, paint);
            //画纵线
            canvas.drawLine(startX + i * GRID_WIDTH, startY, startX + i * GRID_WIDTH, borderY, paint);
        }


        //加载棋盘图片文件，位于屏幕正中央
        //Bitmap newmap = AdjustBitmap(bm, );
        //canvas.drawBitmap(newmap, (screenWidth-newmap.getWidth()) / 2, 0, paint);

        //绘制棋子
        for(int i=0;i<=GRID_NUM;i++)
        {
            for(int j=0;j<=GRID_NUM;j++)
            {
                //type:-2--界外，-1--结盟点，0--空地，1--卒，2--炮，3--车，4--马，5--象，6--士，7--将
                //owner:-1--default(无子的地方)，0--汉献帝，1--魏，2--蜀，3--吴
                switch (mychess.Qipan[j][i].owner) {
                    case -1:
                        break;
                    case 0:case 1:case 2:case 3:
                    {
                        int x = ownermap[mychess.Qipan[j][i].owner];
                        int y = mymap[mychess.Qipan[j][i].type];
                        float xx = startX + i * GRID_WIDTH - GRID_WIDTH / 2;
                        float yy = startY + j * GRID_WIDTH - GRID_WIDTH / 2;
                        canvas.drawBitmap(chess_entity[x][y], xx, yy, paint);
                    }
                        break;
                    default:
                        break;
                }
                if (mychess.Qipan[j][i].owner == mychess.turn) {
                paint.setColor(Color.BLUE);
                for (int k = 0; k < mychess.l; k++) {
                    int x = pos[1][k], y = pos[0][k];
                    float xx = startX + x * GRID_WIDTH;
                    float yy = startY + y * GRID_WIDTH;
                    //画出棋子可选行走点
                    canvas.drawCircle(xx, yy, GRID_WIDTH / 3, paint);
                }
            }
           
            }

        }
    }

    //重写View的监听触摸事件的方法
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float touchX = event.getX();
        float touchY = event.getY();


        //System.out.println(touchX + " " + touchY);

        if(touchX < startX || touchX>startX+(GRID_NUM)*GRID_WIDTH || touchY < startY || touchY>startY+(GRID_NUM)*GRID_WIDTH)
        {//点击到棋盘以外的位置
            System.out.println("......哥们点跑偏了，呵呵");
        }
        else
        {
            //根据点击的位置，从而获知在棋盘上的哪个位置，即是数组的脚标
            int index_y = Math.round((touchX-startX)/GRID_WIDTH);
            int index_x = Math.round((touchY-startY)/GRID_WIDTH);
            System.out.println(index_x + " "+ index_y+" "+mychess.turn);
            if(old_touch == 1)
            {
                pos[0][0]=pos[0][1]=0;
                pos = mychess.Premove(index_x, index_y);
                System.out.println("XIAO DINGDING");
            }
            
            for(int m=0;m<mychess.l;m++)
            {
            	 //System.out.println(pos[0][m]+" "+pos[1][m]);
            }
            if(old_touch == 0)
            {
                int k;
                for(k=0;k<mychess.l;k++)
                {
                    if(pos[0][k]==index_x && pos[1][k]==index_y)
                    {
                        break;
                    }
                }
                if(k!=mychess.l)
                {
                    mychess.Move(ox, oy, index_x, index_y);
                    mychess.Judge_kill(index_x, index_y);
                    if((mychess.die[1]+mychess.die[2]+mychess.die[3])==2)
                    {
                    	System.out.println("Gameover");
                    	flag = 0;
                    }
                }
                old_touch = 1;
                pos[0][0]=pos[1][0]=0;
            }
            if(old_touch == 1 && pos[0][0]!=0 && pos[1][0]!=0)
            {
            	System.out.println("done");
                old_touch = 0;
                ox = index_x;
                oy = index_y;
            }

        }

        invalidate();//点击完成后，通知重绘即再次执行onDraw方法
        return super.onTouchEvent(event);
    }


}
