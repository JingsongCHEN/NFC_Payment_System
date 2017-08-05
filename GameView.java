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

    //type:-2--���⣬-1--���˵㣬0--�յأ�1--�䣬2--�ڣ�3--����4--��5--��6--ʿ��7--��
    //owner:-1--default(���ӵĵط�)��0--���׵ۣ�1--κ��2--��3--��
    //map
    private final int[] ownermap = {3, 2, 0, 1};
    private final int[] mymap = {0, 6, 5, 3, 4, 2, 1, 0};
    //��Ļ�Ŀ�͸�
    private int screenWidth = getWidth();
    private int screenHeight = getHeight();
    //�����̵���ʼλ��
    private int startX;
    private int startY = 10;
    //������ÿ�����ӵĸߺͿ�
    private float GRID_WIDTH;

    private int GRID_NUM = 16;//Ҫ���������е�����

    private Paint paint = null;

    private Bitmap[][] chess_entity = new Bitmap[4][7];

    //����ʡ��Դ�ķ�ʽ��ȡͼƬ
    public Bitmap readBitMap(Context context, int resId){

        BitmapFactory.Options opt = new  BitmapFactory.Options();

        opt.inPreferredConfig =  Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;



        //  ��ȡ��ԴͼƬ
        InputStream is =  context.getResources().openRawResource(resId);

        opt.inSampleSize = 25;//�������е���

        return  BitmapFactory.decodeStream(is, null, opt);
    }

    public GameView(Context context) {
        super(context);

        //����һ�����ӵı߳�
        GRID_WIDTH = (float)(screenHeight - 2 * startY) / GRID_NUM;
        startX = (screenWidth - (screenHeight - 2 * startY)) / 2;

        paint = new Paint();//ʵ����һ������
        paint.setColor(Color.BLACK);//���û��ʵ���ɫ
        paint.setAntiAlias(true);//���û���ȥ��ݣ�û�д���䣬�����߻�ͼƬ��Χ��Բ��

        
        //���������ɫ����
        chess_entity[0][0] = readBitMap(context, R.drawable.red_shu);
        chess_entity[0][1] = readBitMap(context, R.drawable.red_shi);
        chess_entity[0][2] = readBitMap(context, R.drawable.red_xiang);
        chess_entity[0][3] = readBitMap(context, R.drawable.red_ju);
        chess_entity[0][4] = readBitMap(context, R.drawable.red_ma);
        chess_entity[0][5] = readBitMap(context, R.drawable.red_pao);
        chess_entity[0][6] = readBitMap(context, R.drawable.red_bing);

        //���������ɫ����
        chess_entity[1][0] = readBitMap(context, R.drawable.green_wu);
        chess_entity[1][1] = readBitMap(context, R.drawable.green_shi);
        chess_entity[1][2] = readBitMap(context, R.drawable.green_xiang);
        chess_entity[1][3] = readBitMap(context, R.drawable.green_ju);
        chess_entity[1][4] = readBitMap(context, R.drawable.green_ma);
        chess_entity[1][5] = readBitMap(context, R.drawable.green_pao);
        chess_entity[1][6] = readBitMap(context, R.drawable.green_bing);

        //����κ����ɫ����
        chess_entity[2][0] = readBitMap(context, R.drawable.black_wei);
        chess_entity[2][1] = readBitMap(context, R.drawable.black_shi);
        chess_entity[2][2] = readBitMap(context, R.drawable.black_xiang);
        chess_entity[2][3] = readBitMap(context, R.drawable.black_ju);
        chess_entity[2][4] = readBitMap(context, R.drawable.black_ma);
        chess_entity[2][5] = readBitMap(context, R.drawable.black_pao);
        chess_entity[2][6] = readBitMap(context, R.drawable.black_zu);

        //���غ�������
        chess_entity[3][0] = readBitMap(context, R.drawable.yellow_han);
        chess_entity[3][1] = readBitMap(context, R.drawable.yellow_han);
        chess_entity[3][2] = readBitMap(context, R.drawable.yellow_han);
        chess_entity[3][3] = readBitMap(context, R.drawable.yellow_ju);
        chess_entity[3][4] = readBitMap(context, R.drawable.yellow_han);
        chess_entity[3][5] = readBitMap(context, R.drawable.yellow_pao);
        chess_entity[3][6] = readBitMap(context, R.drawable.yellow_han);

    }


    @Override
    protected void onDraw(Canvas canvas) {//��дView�еĸ÷������÷�����Ҫ�е���ͼ�Ĺ�����ÿˢ��һ�Σ��͵���һ�θ÷���
        super.onDraw(canvas);

        canvas.drawColor(Color.WHITE);//����Ļ�ĵ�ɫ��ɻ�ɫ���˴��������������Ļ���������ɫ�����ã�����ˢ�������ã�����ǰ���ƵĽ������
        paint.setColor(Color.BLACK);//�˴��ǰѻ��ʱ����ɫ���ǻ��Ƶ����̱����ɫ

        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.STROKE);

        screenWidth = getWidth();
        screenHeight = getHeight();

        //����һ�����ӵı߳�
        GRID_WIDTH = (float)(screenHeight - 2 * startY) / GRID_NUM;

        startX = (screenWidth - (screenHeight - 2 * startY)) / 2;
        int borderX = screenWidth - startX;
        int borderY = screenHeight - startY;

        for (int i=0;i<=GRID_NUM;i++)
        {
            //������
            canvas.drawLine(startX, startY + i * GRID_WIDTH, borderX , startY + i * GRID_WIDTH, paint);
            //������
            canvas.drawLine(startX + i * GRID_WIDTH, startY, startX + i * GRID_WIDTH, borderY, paint);
        }


        //��������ͼƬ�ļ���λ����Ļ������
        //Bitmap newmap = AdjustBitmap(bm, );
        //canvas.drawBitmap(newmap, (screenWidth-newmap.getWidth()) / 2, 0, paint);

        //��������
        for(int i=0;i<=GRID_NUM;i++)
        {
            for(int j=0;j<=GRID_NUM;j++)
            {
                //type:-2--���⣬-1--���˵㣬0--�յأ�1--�䣬2--�ڣ�3--����4--��5--��6--ʿ��7--��
                //owner:-1--default(���ӵĵط�)��0--���׵ۣ�1--κ��2--��3--��
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
                    //�������ӿ�ѡ���ߵ�
                    canvas.drawCircle(xx, yy, GRID_WIDTH / 3, paint);
                }
            }
           
            }

        }
    }

    //��дView�ļ��������¼��ķ���
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float touchX = event.getX();
        float touchY = event.getY();


        //System.out.println(touchX + " " + touchY);

        if(touchX < startX || touchX>startX+(GRID_NUM)*GRID_WIDTH || touchY < startY || touchY>startY+(GRID_NUM)*GRID_WIDTH)
        {//��������������λ��
            System.out.println("......���ǵ���ƫ�ˣ��Ǻ�");
        }
        else
        {
            //���ݵ����λ�ã��Ӷ���֪�������ϵ��ĸ�λ�ã���������Ľű�
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

        invalidate();//�����ɺ�֪ͨ�ػ漴�ٴ�ִ��onDraw����
        return super.onTouchEvent(event);
    }


}
