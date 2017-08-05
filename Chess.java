package com.example.sanguo;

//type:-2--界外，-1--结盟点，0--空地，1--卒，2--炮，3--车，4--马，5--象，6--士，7--将
//owner:-1--default(无子的地方)，0--汉献帝，1--魏，2--蜀，3--吴

public class Chess {
	//棋子
	class Point {
	    int type = 0;
	    int owner = -1;
	}
	//棋子
	public Point [][]Qipan = new Point [17][17];
	//结盟方
	public int [] ally = new int [4];
	//轮数
	public int turn = 3;//1:魏;2:蜀;3:吴
	//出局标志
	public int [] die = new int [4];
	public int l=0;
	public Chess() {
	    int i=0,j=0;
	    for(i=0;i<=16;i++)
	        for(j=0;j<=16;j++)
	            Qipan[i][j] = new Point();
	    ally[1] = -2;//魏的盟友
	    ally[2] = -2;//蜀的盟友
	    ally[3] = -2;//吴的盟友
	    //出局
	    die[1] = 0;
	    die[2] = 0;
	    die[3] = 0;
	    //界外
	    for(i=0;i<=3;i++)
	        for(j=0;j<=3;j++)
	            Qipan[i][j].type = -2;
	    for(i=0;i<=3;i++)
	        for(j=13;j<=16;j++)
	            Qipan[i][j].type = -2;
	    for(i=13;i<=16;i++)
	        for(j=0;j<=3;j++)
	            Qipan[i][j].type = -2;
	    for(i=13;i<=16;i++)
	        for(j=13;j<=16;j++)
	            Qipan[i][j].type = -2;
	    //结盟点
	    Qipan[7][0].type = -1;
	    Qipan[8][1].type = -1;
	    Qipan[9][0].type = -1;
	    //卒
	    for(j=4;j<=12;j+=2)
	    {
	        Qipan[3][j].type = 1;
	        Qipan[3][j].owner = 1;
	    }
	    for(j=4;j<=12;j+=2)
	    {
	        Qipan[13][j].type = 1;
	        Qipan[13][j].owner = 2;
	    }
	    for(i=4;i<=12;i+=2)
	    {
	        Qipan[i][13].type = 1;
	        Qipan[i][13].owner = 3;
	    }
	    //炮
	    Qipan[2][5].type = 2;
	    Qipan[2][5].owner = 1;
	    Qipan[2][11].type = 2;
	    Qipan[2][11].owner = 1;
	    Qipan[14][5].type = 2;
	    Qipan[14][5].owner = 2;
	    Qipan[14][11].type = 2;
	    Qipan[14][11].owner = 2;
	    Qipan[5][14].type = 2;
	    Qipan[5][14].owner = 3;
	    Qipan[11][14].type = 2;
	    Qipan[11][14].owner = 3;
	    Qipan[8][2].type = 2;
	    Qipan[8][2].owner = 0;
	    //车
	    Qipan[0][4].type = 3;
	    Qipan[0][4].owner = 1;
	    Qipan[0][12].type = 3;
	    Qipan[0][12].owner = 1;
	    Qipan[16][4].type = 3;
	    Qipan[16][4].owner = 2;
	    Qipan[16][12].type = 3;
	    Qipan[16][12].owner = 2;
	    Qipan[4][16].type = 3;
	    Qipan[4][16].owner = 3;
	    Qipan[12][16].type = 3;
	    Qipan[12][16].owner = 3;
	    Qipan[6][3].type = 3;
	    Qipan[6][3].owner = 0;
	    Qipan[8][3].type = 3;
	    Qipan[8][3].owner = 0;
	    Qipan[10][3].type = 3;
	    Qipan[10][3].owner = 0;
	    //马
	    Qipan[0][5].type = 4;
	    Qipan[0][5].owner = 1;
	    Qipan[0][11].type = 4;
	    Qipan[0][11].owner = 1;
	    Qipan[16][5].type = 4;
	    Qipan[16][5].owner = 2;
	    Qipan[16][11].type = 4;
	    Qipan[16][11].owner = 2;
	    Qipan[5][16].type = 4;
	    Qipan[5][16].owner = 3;
	    Qipan[11][16].type = 4;
	    Qipan[11][16].owner = 3;
	    //象
	    Qipan[0][6].type = 5;
	    Qipan[0][6].owner = 1;
	    Qipan[0][10].type = 5;
	    Qipan[0][10].owner = 1;
	    Qipan[16][6].type = 5;
	    Qipan[16][6].owner = 2;
	    Qipan[16][10].type = 5;
	    Qipan[16][10].owner = 2;
	    Qipan[6][16].type = 5;
	    Qipan[6][16].owner = 3;
	    Qipan[10][16].type = 5;
	    Qipan[10][16].owner = 3;
	    //士
	    Qipan[0][7].type = 6;
	    Qipan[0][7].owner = 1;
	    Qipan[0][9].type = 6;
	    Qipan[0][9].owner = 1;
	    Qipan[16][7].type = 6;
	    Qipan[16][7].owner = 2;
	    Qipan[16][9].type = 6;
	    Qipan[16][9].owner = 2;
	    Qipan[7][16].type = 6;
	    Qipan[7][16].owner = 3;
	    Qipan[9][16].type = 6;
	    Qipan[9][16].owner = 3;
	    //将
	    Qipan[0][8].type = 7;
	    Qipan[0][8].owner = 1;
	    Qipan[16][8].type = 7;
	    Qipan[16][8].owner = 2;
	    Qipan[8][16].type = 7;
	    Qipan[8][16].owner = 3;
	    Qipan[8][0].type = 7;
	    Qipan[8][0].owner = 0;
	}
	public int[][] Premove(int x,int y){
	    int [][] pos = new int[2][50];
	    pos[0][0]=pos[0][1]=0;
	    l=0;
	    if(Qipan[x][y].owner == turn)
	    {
	    	//卒
	        if(Qipan[x][y].type == 1)
	        {
	            if(turn == 2)
	            {
	                if((x+1)<=13 && Qipan[x+1][y].type!=-2 && Qipan[x+1][y].owner!=0
	                        && Qipan[x+1][y].owner!=turn && Qipan[x+1][y].owner!=ally[turn])
	                {
	                    pos[0][l] = x+1;
	                    pos[1][l++]= y;
	                }
	            }
	            else
	            {
	                if((x+1)<=16 && Qipan[x+1][y].type!=-2 && Qipan[x+1][y].owner!=0
	                        && Qipan[x+1][y].owner!=turn && Qipan[x+1][y].owner!=ally[turn])
	                {
	                    pos[0][l] = x+1;
	                    pos[1][l++]= y;
	                }
	            }
	            if(turn == 1)
	            {
	                if((x-1)>=3 && Qipan[x-1][y].type!=-2 && Qipan[x-1][y].owner!=0
	                        && Qipan[x-1][y].owner!=turn && Qipan[x-1][y].owner!=ally[turn])
	                {
	                    pos[0][l] = x-1;
	                    pos[1][l++]= y;
	                }
	            }
	            else
	            {
	                if((x-1)>=0 && Qipan[x-1][y].type!=-2 && Qipan[x-1][y].owner!=0
	                        && Qipan[x-1][y].owner!=turn && Qipan[x-1][y].owner!=ally[turn])
	                {
	                    pos[0][l] = x-1;
	                    pos[1][l++]= y;
	                }
	            }
	            if(turn == 3)
	            {
	                if((y+1)<=13 && Qipan[x][y+1].type!=-2 && Qipan[x][y+1].owner!=0
	                        && Qipan[x][y+1].owner!=turn && Qipan[x][y+1].owner!=ally[turn])
	                {
	                    pos[0][l] = x;
	                    pos[1][l++]= y+1;
	                }
	            }
	            else
	            {
	                if((y+1)<=13 && Qipan[x][y+1].type!=-2 && Qipan[x][y+1].owner!=0
	                        && Qipan[x][y+1].owner!=turn && Qipan[x][y+1].owner!=ally[turn])
	                {
	                    pos[0][l] = x;
	                    pos[1][l++]= y+1;
	                }
	            }
	            if((y-1)>=0 && Qipan[x][y-1].type!=-2 && Qipan[x][y-1].owner!=0
	                    && Qipan[x][y-1].owner!=turn && Qipan[x][y-1].owner!=ally[turn])
	            {
	                pos[0][l] = x;
	                pos[1][l++]= y-1;
	            }
	        }
	        //炮
	        if(Qipan[x][y].type == 2)
	        {
	            int k;
	            //向下
	            for(k=1;(x+k)<=16;k++)
	            {
	                if(Qipan[x+k][y].type == 0 || Qipan[x+k][y].type == -1)
	                {
	                    pos[0][l] = x+k;
	                    pos[1][l++]= y;
	                }
	                else
	                    break;
	            }
	            for(k++;(x+k)<=16;k++)
	            {
	                if(Qipan[x+k][y].type > 0)
	                {
	                    if(Qipan[x+k][y].owner!=turn && Qipan[x+k][y].owner!=0 &&
	                            Qipan[x+k][y].owner!=ally[turn])
	                    {
	                        pos[0][l] = x+k;
	                        pos[1][l++]= y;
	                    }
	                    break;
	                }
	                else if(Qipan[x+k][y].type == -2)
	                    break;
	            }
	            
	            //向上
	            for(k=1;(x-k)>=0;k++)
	            {
	                if(Qipan[x-k][y].type == 0 || Qipan[x-k][y].type == -1)
	                {
	                    pos[0][l] = x-k;
	                    pos[1][l++]= y;
	                }
	                else
	                    break;
	            }
	            for(k++;(x-k)>=0;k++)
	            {
	                if(Qipan[x-k][y].type > 0)
	                {
	                    if(Qipan[x-k][y].owner!=turn && Qipan[x-k][y].owner!=0 &&
	                            Qipan[x-k][y].owner!=ally[turn])
	                    {
	                        pos[0][l] = x-k;
	                        pos[1][l++]= y;
	                    }
	                    break;
	                }
	                else if(Qipan[x-k][y].type == -2)
	                    break;
	            }
	            
	            //向右
	            for(k=1;(y+k)<=16;k++)
	            {
	                if(Qipan[x][y+k].type == 0 || Qipan[x][y+k].type == -1)
	                {
	                    pos[0][l] = x;
	                    pos[1][l++]= y+k;
	                }
	                else
	                    break;
	            }
	            for(k++;(y+k)<=16;k++)
	            {
	                if(Qipan[x][y+k].type > 0)
	                {
	                    if(Qipan[x][y+k].owner!=turn && Qipan[x][y+k].owner!=0 &&
	                            Qipan[x][y+k].owner!=ally[turn])
	                    {
	                        pos[0][l] = x;
	                        pos[1][l++]= y+k;
	                    }
	                    break;
	                }
	                else if(Qipan[x][y+k].type == -2)
	                    break;
	            }
	            //向左
	            for(k=1;(y-k)>=0;k++)
	            {
	                if(Qipan[x][y-k].type == 0 || Qipan[x][y-k].type == -1)
	                {
	                    pos[0][l] = x;
	                    pos[1][l++]= y-k;
	                }
	                else
	                    break;
	            }
	            for(k++;(y-k)>=0;k++)
	            {
	                if(Qipan[x][y-k].type > 0)
	                {
	                    if(Qipan[x][y-k].owner!=turn && Qipan[x][y-k].owner!=0 &&
	                            Qipan[x][y-k].owner!=ally[turn])
	                    {
	                        pos[0][l] = x;
	                        pos[1][l++]= y-k;
	                    }
	                    break;
	                }
	                else if(Qipan[x][y-k].type == -2)
	                    break;
	            }
	        }
	        //车
	        if(Qipan[x][y].type == 3)
	        {
	            int k;
	            //向下
	            for(k=1;(x+k)<=16;k++)
	            {
	                if(Qipan[x+k][y].type == 0 || Qipan[x+k][y].type == -1)
	                {
	                    pos[0][l] = x+k;
	                    pos[1][l++]= y;
	                }
	                else if(Qipan[x+k][y].type > 0)
	                {
	                    if(Qipan[x+k][y].owner!=turn && Qipan[x+k][y].owner!=0 &&
	                            Qipan[x+k][y].owner!=ally[turn])
	                    {
	                        pos[0][l] = x+k;
	                        pos[1][l++]= y;
	                    }
	                    break;
	                }
	                else if(Qipan[x+k][y].type == -2)
	                    break;
	            }
	            //向上
	            for(k=1;(x-k)>=0;k++)
	            {
	                if(Qipan[x-k][y].type == 0 || Qipan[x-k][y].type == -1)
	                {
	                    pos[0][l] = x-k;
	                    pos[1][l++]= y;
	                }
	                else if(Qipan[x-k][y].type > 0)
	                {
	                    if(Qipan[x-k][y].owner!=turn && Qipan[x-k][y].owner!=0 &&
	                            Qipan[x-k][y].owner!=ally[turn])
	                    {
	                        pos[0][l] = x-k;
	                        pos[1][l++]= y;
	                    }
	                    break;
	                }
	                else if(Qipan[x-k][y].type == -2)
	                    break;
	            }
	            //向右
	            for(k=1;(y+k)<=16;k++)
	            {
	                if(Qipan[x][y+k].type == 0 || Qipan[x][y+k].type == -1)
	                {
	                    pos[0][l] = x;
	                    pos[1][l++]= y+k;
	                }
	                else if(Qipan[x][y+k].type > 0)
	                {
	                    if(Qipan[x][y+k].owner!=turn && Qipan[x][y+k].owner!=0 &&
	                            Qipan[x][y+k].owner!=ally[turn])
	                    {
	                        pos[0][l] = x;
	                        pos[1][l++]= y+k;
	                    }
	                    break;
	                }
	                else if(Qipan[x][y+k].type == -2)
	                    break;
	            }
	            //向左
	            for(k=1;(y-k)>=0;k++)
	            {
	                if(Qipan[x][y-k].type == 0 || Qipan[x][y-k].type == -1)
	                {
	                    pos[0][l] = x;
	                    pos[1][l++]= y-k;
	                }
	                else if(Qipan[x][y-k].type > 0)
	                {
	                    if(Qipan[x][y-k].owner!=turn && Qipan[x][y-k].owner!=0 &&
	                            Qipan[x][y-k].owner!=ally[turn])
	                    {
	                        pos[0][l] = x;
	                        pos[1][l++]= y-k;
	                    }
	                    break;
	                }
	                else if(Qipan[x][y-k].type == -2)
	                    break;
	            }
	        }
	        //马
	        if(Qipan[x][y].type == 4)
	        {
	            //下右
	            if((x+2)<=16 && (y+1)<=16 && ((Qipan[x+2][y+1].type!=-2 && Qipan[x+2][y+1].owner!=turn 
	            		&& Qipan[x+2][y+1].owner!=ally[turn] && Qipan[x+2][y+1].owner!= 0)
	            		|| (Qipan[x+2][y+1].owner==0 && Qipan[x+2][y+1].type==7)))
	            {
	                pos[0][l] = x+2;
	                pos[1][l++]= y+1;
	            }
	            //下左
	            if((x+2)<=16 && (y-1)>=0 && ((Qipan[x+2][y-1].type!=-2 && Qipan[x+2][y-1].owner!=turn && Qipan[x+2][y-1].owner!=ally[turn]
	            		&& Qipan[x+2][y-1].owner!= 0)
	            		|| (Qipan[x+2][y-1].owner==0 && Qipan[x+2][y-1].type==7)))
	            {
	                pos[0][l] = x+2;
	                pos[1][l++]= y-1;
	            }
	            //上右
	            if((x-2)>=0 && (y+1)<=16 && ((Qipan[x-2][y+1].type!=-2 && Qipan[x-2][y+1].owner!=turn && Qipan[x-2][y+1].owner!=ally[turn]
	            		&& Qipan[x-2][y+1].owner!= 0)
	            		|| (Qipan[x-2][y+1].owner==0 && Qipan[x-2][y+1].type==7)))
	            {
	                pos[0][l] = x-2;
	                pos[1][l++]= y+1;
	            }
	            //上左
	            if((x-2)>=0 && (y-1)>=0 && ((Qipan[x-2][y-1].type!=-2 && Qipan[x-2][y-1].owner!=turn && Qipan[x-2][y-1].owner!=ally[turn]
	            		&& Qipan[x-2][y-1].owner!= 0)
	            		|| (Qipan[x-2][y-1].owner==0 && Qipan[x-2][y-1].type==7)))
	            {
	                pos[0][l] = x-2;
	                pos[1][l++]= y-1;
	            }
	            //右上
	            if((x-1)>=0 && (y+2)<=16 && ((Qipan[x-1][y+2].type!=-2 && Qipan[x-1][y+2].owner!=turn && Qipan[x-1][y+2].owner!=ally[turn]
	            		&& Qipan[x-1][y+2].owner!= 0)
	            		|| (Qipan[x-1][y+2].owner==0 && Qipan[x-1][y+2].type==7)))
	            {
	                pos[0][l] = x-1;
	                pos[1][l++]= y+2;
	            }
	            //右下
	            if((x+1)<=16 && (y+2)<=16 && ((Qipan[x+1][y+2].type!=-2 && Qipan[x+1][y+2].owner!=turn && Qipan[x+1][y+2].owner!=ally[turn]
	            		&& Qipan[x+1][y+2].owner!= 0)
	            		|| (Qipan[x+1][y+2].owner==0 && Qipan[x+1][y+2].type==7)))
	            {
	                pos[0][l] = x+1;
	                pos[1][l++]= y+2;
	            }
	            //左上
	            if((x-1)>=0 && (y-2)>=0 && ((Qipan[x-1][y-2].type!=-2 && Qipan[x-1][y-2].owner!=turn && Qipan[x-1][y-2].owner!=ally[turn]
	            		&& Qipan[x-1][y-2].owner!= 0)
	            		|| (Qipan[x-1][y-2].owner==0 && Qipan[x-1][y-2].type==7)))
	            {
	                pos[0][l] = x-1;
	                pos[1][l++]= y-2;
	            }
	            //左下
	            if((x+1)<=16 && (y-2)>=0 && ((Qipan[x+1][y-2].type!=-2 && Qipan[x+1][y-2].owner!=turn && Qipan[x+1][y-2].owner!=ally[turn]
	            		&& Qipan[x+1][y-2].owner!= 0)
	            		|| (Qipan[x+1][y-2].owner==0 && Qipan[x+1][y-2].type==7)))
	            {
	                pos[0][l] = x+1;
	                pos[1][l++]= y-2;
	            }
	        }
	        //象
	        if(Qipan[x][y].type == 5)
	        {
	            if(x <= 4 && y<=12)
	            {
	                //右下
	                if((x+2)<=4 && (y+2)<=12 && Qipan[x+2][y+2].owner!=turn
	                        && Qipan[x+2][y+2].owner!=ally[turn])
	                {
	                    pos[0][l] = x+2;
	                    pos[1][l++]= y+2;
	                }
	                //左下
	                if((x+2)<=4 && (y-2)>=4 && Qipan[x+2][y-2].owner!=turn
	                        && Qipan[x+2][y-2].owner!=ally[turn])
	                {
	                    pos[0][l] = x+2;
	                    pos[1][l++]= y-2;
	                }
	                //右上
	                if((x-2)>=0 && (y+2)<=12 && Qipan[x-2][y+2].owner!=turn
	                        && Qipan[x-2][y+2].owner!=ally[turn])
	                {
	                    pos[0][l] = x-2;
	                    pos[1][l++]= y+2;
	                }
	                //左上
	                if((x-2)>=0 && (y-2)>=4 && Qipan[x-2][y-2].owner!=turn
	                        && Qipan[x-2][y-2].owner!=ally[turn])
	                {
	                    pos[0][l] = x-2;
	                    pos[1][l++]= y-2;
	                }
	            }
	            else if(x >= 12 && y<=12)
	            {
	                //右下
	                if((x+2)<=16 && (y+2)<=12 && Qipan[x+2][y+2].owner!=turn
	                        && Qipan[x+2][y+2].owner!=ally[turn])
	                {
	                    pos[0][l] = x+2;
	                    pos[1][l++]= y+2;
	                }
	                //左下
	                if((x+2)<=16 && (y-2)>=4 && Qipan[x+2][y-2].owner!=turn
	                        && Qipan[x+2][y-2].owner!=ally[turn])
	                {
	                    pos[0][l] = x+2;
	                    pos[1][l++]= y-2;
	                }
	                //右上
	                if((x-2)>=12 && (y+2)<=12 && Qipan[x-2][y+2].owner!=turn
	                        && Qipan[x-2][y+2].owner!=ally[turn])
	                {
	                    pos[0][l] = x-2;
	                    pos[1][l++]= y+2;
	                }
	                //左上
	                if((x-2)>=12 && (y-2)>=4 && Qipan[x-2][y-2].owner!=turn
	                        && Qipan[x-2][y-2].owner!=ally[turn])
	                {
	                    pos[0][l] = x-2;
	                    pos[1][l++]= y-2;
	                }
	            }
	            else
	            {
	                //右下
	                if((x+2)<=12 && (y+2)<=16 && Qipan[x+2][y+2].owner!=turn
	                        && Qipan[x+2][y+2].owner!=ally[turn])
	                {
	                    pos[0][l] = x+2;
	                    pos[1][l++]= y+2;
	                }
	                //左下
	                if((x+2)<=12 && (y-2)>=12 && Qipan[x+2][y-2].owner!=turn
	                        && Qipan[x+2][y-2].owner!=ally[turn])
	                {
	                    pos[0][l] = x+2;
	                    pos[1][l++]= y-2;
	                }
	                //右上
	                if((x-2)>=4 && (y+2)<=16 && Qipan[x-2][y+2].owner!=turn
	                        && Qipan[x-2][y+2].owner!=ally[turn])
	                {
	                    pos[0][l] = x-2;
	                    pos[1][l++]= y+2;
	                }
	                //左上
	                if((x-2)>=4 && (y-2)>=12 && Qipan[x-2][y-2].owner!=turn
	                        && Qipan[x-2][y-2].owner!=ally[turn])
	                {
	                    pos[0][l] = x-2;
	                    pos[1][l++]= y-2;
	                }
	            }
	        }
	        //士
	        if(Qipan[x][y].type == 6)
	        {
	            if(x <= 2)
	            {
	                //右下
	                if((x+1)<=2 && (y+1)<=9 && Qipan[x+1][y+1].owner!=turn
	                        && Qipan[x+1][y+1].owner!=ally[turn])
	                {
	                    pos[0][l] = x+1;
	                    pos[1][l++]= y+1;
	                }
	                //左下
	                if((x+1)<=2 && (y-1)>=7 && Qipan[x+1][y-1].owner!=turn
	                        && Qipan[x+1][y-1].owner!=ally[turn])
	                {
	                    pos[0][l] = x+1;
	                    pos[1][l++]= y-1;
	                }
	                //右上
	                if((x-1)>=0 && (y+1)<=9 && Qipan[x-1][y+1].owner!=turn
	                        && Qipan[x-1][y+1].owner!=ally[turn])
	                {
	                    pos[0][l] = x-1;
	                    pos[1][l++]= y+1;
	                }
	                //左上
	                if((x-1)>=0 && (y-1)>=7 && Qipan[x-1][y-1].owner!=turn
	                        && Qipan[x-1][y-1].owner!=ally[turn])
	                {
	                    pos[0][l] = x-1;
	                    pos[1][l++]= y-1;
	                }
	            }
	            else if(x >= 14)
	            {
	                //右下
	                if((x+1)<=16 && (y+1)<=9 && Qipan[x+1][y+1].owner!=turn
	                        && Qipan[x+1][y+1].owner!=ally[turn])
	                {
	                    pos[0][l] = x+1;
	                    pos[1][l++]= y+1;
	                }
	                //左下
	                if((x+1)<=16 && (y-1)>=7 && Qipan[x+1][y-1].owner!=turn
	                        && Qipan[x+1][y-1].owner!=ally[turn])
	                {
	                    pos[0][l] = x+1;
	                    pos[1][l++]= y-1;
	                }
	                //右上
	                if((x-1)>=14 && (y+1)<=9 && Qipan[x-1][y+1].owner!=turn
	                        && Qipan[x-1][y+1].owner!=ally[turn])
	                {
	                    pos[0][l] = x-1;
	                    pos[1][l++]= y+1;
	                }
	                //左上
	                if((x-1)>=14 && (y-1)>=7 && Qipan[x-1][y-1].owner!=turn
	                        && Qipan[x-1][y-1].owner!=ally[turn])
	                {
	                    pos[0][l] = x-1;
	                    pos[1][l++]= y-1;
	                }
	            }
	            else
	            {
	                //右下
	                if((x+1)<=9 && (y+1)<=16 && Qipan[x+1][y+1].owner!=turn
	                        && Qipan[x+1][y+1].owner!=ally[turn])
	                {
	                    pos[0][l] = x+1;
	                    pos[1][l++]= y+1;
	                }
	                //左下
	                if((x+1)<=9 && (y-1)>=14 && Qipan[x+1][y-1].owner!=turn
	                        && Qipan[x+1][y-1].owner!=ally[turn])
	                {
	                    pos[0][l] = x+1;
	                    pos[1][l++]= y-1;
	                }
	                //右上
	                if((x-1)>=7 && (y+1)<=16 && Qipan[x-1][y+1].owner!=turn
	                        && Qipan[x-1][y+1].owner!=ally[turn])
	                {
	                    pos[0][l] = x-1;
	                    pos[1][l++]= y+1;
	                }
	                //左上
	                if((x-1)>=7 && (y-1)>=14 && Qipan[x-1][y-1].owner!=turn
	                        && Qipan[x-1][y-1].owner!=ally[turn])
	                {
	                    pos[0][l] = x-1;
	                    pos[1][l++]= y-1;
	                }
	            }
	        }
	        //将
	        if(Qipan[x][y].type == 7)
	        {
	            if(turn == 1)
	            {
	                //上
	                if((x-1)>=0 && Qipan[x-1][y].owner!=turn
	                        && Qipan[x-1][y].owner!=ally[turn])
	                {
	                    pos[0][l] = x-1;
	                    pos[1][l++]= y;
	                }
	                //下
	                if((x+1)<=2 && Qipan[x+1][y].owner!=turn
	                        && Qipan[x+1][y].owner!=ally[turn])
	                {
	                    pos[0][l] = x+1;
	                    pos[1][l++]= y;
	                }
	                //左
	                if((y-1)>=7 && Qipan[x][y-1].owner!=turn
	                        && Qipan[x][y-1].owner!=ally[turn])
	                {
	                    pos[0][l] = x;
	                    pos[1][l++]= y-1;
	                }
	                //右
	                if((y+1)<=9 && Qipan[x][y+1].owner!=turn
	                        && Qipan[x][y+1].owner!=ally[turn])
	                {
	                    pos[0][l] = x;
	                    pos[1][l++]= y+1;
	                }
	            }
	            if(turn == 2)
	            {
	                //上
	                if((x-1)>=14 && Qipan[x-1][y].owner!=turn
	                        && Qipan[x-1][y].owner!=ally[turn])
	                {
	                    pos[0][l] = x-1;
	                    pos[1][l++]= y;
	                }
	                //下
	                if((x+1)<=16 && Qipan[x+1][y].owner!=turn
	                        && Qipan[x+1][y].owner!=ally[turn])
	                {
	                    pos[0][l] = x+1;
	                    pos[1][l++]= y;
	                }
	                //左
	                if((y-1)>=7 && Qipan[x][y-1].owner!=turn
	                        && Qipan[x][y-1].owner!=ally[turn])
	                {
	                    pos[0][l] = x;
	                    pos[1][l++]= y-1;
	                }
	                //右
	                if((y+1)<=9 && Qipan[x][y+1].owner!=turn
	                        && Qipan[x][y+1].owner!=ally[turn])
	                {
	                    pos[0][l] = x;
	                    pos[1][l++]= y+1;
	                }
	            }
	            if(turn == 3)
	            {
	                //上
	                if((x-1)>=7 && Qipan[x-1][y].owner!=turn
	                        && Qipan[x-1][y].owner!=ally[turn])
	                {
	                    pos[0][l] = x-1;
	                    pos[1][l++]= y;
	                }
	                //下
	                if((x+1)<=9 && Qipan[x+1][y].owner!=turn
	                        && Qipan[x+1][y].owner!=ally[turn])
	                {
	                    pos[0][l] = x+1;
	                    pos[1][l++]= y;
	                }
	                //左
	                if((y-1)>=14 && Qipan[x][y-1].owner!=turn
	                        && Qipan[x][y-1].owner!=ally[turn])
	                {
	                    pos[0][l] = x;
	                    pos[1][l++]= y-1;
	                }
	                //右
	                if((y+1)<=16 && Qipan[x][y+1].owner!=turn
	                        && Qipan[x][y+1].owner!=ally[turn])
	                {
	                    pos[0][l] = x;
	                    pos[1][l++]= y+1;
	                }
	            }
	        }
	    }
	    else
	        System.out.println("The piece is not valid.");
	    return pos;
	}
	public void Move(int ox, int oy, int nx,int ny)
	{
		//马走向结盟点
	    if(Qipan[ox][oy].type == 4 && Qipan[nx][ny].type == -1)
	    {
	        if(Qipan[ox][oy].owner == 1 && nx == 7 && ny == 0)
	        {
	            ally[1] = 2;
	            ally[2] = 1;
	            Qipan[6][3].owner = 3;
	            Qipan[8][3].owner = 3;
	            Qipan[10][3].owner = 3;
	            Qipan[8][2].owner = 3;
	        }
	        if(Qipan[ox][oy].owner == 1 && nx == 8 && ny == 1)
	        {
	            ally[1] = 3;
	            ally[3] = 1;
	            Qipan[6][3].owner = 2;
	            Qipan[8][3].owner = 2;
	            Qipan[10][3].owner = 2;
	            Qipan[8][2].owner = 2;
	        }
	        if(Qipan[ox][oy].owner == 2 && nx == 9 && ny == 0)
	        {
	            ally[1] = 2;
	            ally[2] = 1;
	            Qipan[6][3].owner = 3;
	            Qipan[8][3].owner = 3;
	            Qipan[10][3].owner = 3;
	            Qipan[8][2].owner = 3;
	        }
	        if(Qipan[ox][oy].owner == 2 && nx == 8 && ny == 1)
	        {
	            ally[3] = 2;
	            ally[2] = 3;
	            Qipan[6][3].owner = 1;
	            Qipan[8][3].owner = 1;
	            Qipan[10][3].owner = 1;
	            Qipan[8][2].owner = 1;
	        }
	        if(Qipan[ox][oy].owner == 3 && nx == 7 && ny == 0)
	        {
	            ally[3] = 2;
	            ally[2] = 3;
	            Qipan[6][3].owner = 1;
	            Qipan[8][3].owner = 1;
	            Qipan[10][3].owner = 1;
	            Qipan[8][2].owner = 1;
	        }
	        if(Qipan[ox][oy].owner == 3 && nx == 9 && ny == 0)
	        {
	            ally[1] = 3;
	            ally[3] = 1;
	            Qipan[6][3].owner = 2;
	            Qipan[8][3].owner = 2;
	            Qipan[10][3].owner = 2;
	            Qipan[8][2].owner = 2;
	        }
	    }
	    //将被吃掉
	    if(Qipan[nx][ny].type==7 && Qipan[nx][ny].owner != 0)
	    {
	        int i,j;
	        for(i=0;i<=16;i++)
	            for(j=0;j<=16;j++)
	            {
	                if(Qipan[i][j].owner==Qipan[nx][ny].owner && (i != nx || j != ny))
	                {
	                    Qipan[i][j].owner=Qipan[ox][oy].owner;
	                }
	            }
	        die[Qipan[nx][ny].owner] = 1;
	        if(Qipan[nx][ny].owner==1)
	        {
	            ally[2]=ally[3]=-2;
	        }
	        if(Qipan[nx][ny].owner==2)
	        {
	            ally[1]=ally[3]=-2;
	        }
	        if(Qipan[nx][ny].owner==3)
	        {
	            ally[2]=ally[1]=-2;
	        }
	    }
	    if(Qipan[nx][ny].owner == 0 &&  Qipan[nx][ny].type == 7)
	    {
	        Qipan[6][3].owner=Qipan[ox][oy].owner;
	        Qipan[8][3].owner=Qipan[ox][oy].owner;
	        Qipan[10][3].owner=Qipan[ox][oy].owner;
	        Qipan[8][2].owner=Qipan[ox][oy].owner;
	        if(Qipan[ox][oy].owner == 1)
	        { 
	        	ally[3] = 2;
	        	ally[2] = 3;
	        }
	        if(Qipan[ox][oy].owner == 2)
	        { 
	        	ally[1] = 3;
	        	ally[3] = 1;
	        }
	        if(Qipan[ox][oy].owner == 3)
	        { 
	        	ally[1] = 2;
	        	ally[2] = 1;
	        }  
	    }
	    Qipan[nx][ny].type=Qipan[ox][oy].type;
	    Qipan[nx][ny].owner=Qipan[ox][oy].owner;
	    Qipan[ox][oy].type=0;
	    Qipan[ox][oy].owner=-1;
	}
	public void Judge_kill(int x,int y){
	    int[][] pos;
	    int i;
	    int nextturn=0;
	    int count = 0;
	    int rem =turn;
	    pos=Premove(x,y);
	    int tmp_l = l;
	    for(i=0;i<tmp_l;i++)
	    {
	        int tx,ty;
	        tx=pos[0][i];
	        ty=pos[1][i];
	        //被将军
	        if(Qipan[tx][ty].type == 7 && Qipan[tx][ty].owner != turn &&
	                Qipan[tx][ty].owner != ally[turn] && Qipan[tx][ty].owner != 0)
	        {
	            int[][] Allpos = new int [2][1000];
	            int[][] Tmp;
	            int flag=0;
	            int p=0;
	            int k,h;
	            int t;
	            for(k=0;k<=16;k++)
	                for(h=0;h<=16;h++)
	                {
	                    if(Qipan[k][h].owner!=0 && Qipan[k][h].owner!= Qipan[tx][ty].owner && Qipan[k][h].type>0 && Qipan[k][h].type<=4)
	                    {
	                    	turn = Qipan[k][h].owner;
	                        Tmp=Premove(k,h);
	                        turn = rem;
	                        for(t=0;t<l;t++)
	                        {
	                            Allpos[0][p] = Tmp[0][t];
	                            Allpos[1][p++] = Tmp[1][t];
	                        }
	                    }
	                }
	            turn = Qipan[tx][ty].owner;
	            Tmp=Premove(tx,ty);
	            turn = rem;
	            //检查
	            for(k=0;k<l;k++)
	            {
	                for(h=0;h<p;h++)
	                {
	                    if(Allpos[0][h]==Tmp[0][k] && Allpos[1][h]==Tmp[1][k])
	                    {
	                        flag=1;
	                        break;
	                    }
	                }
	                if(flag != 1)
	                {
	                    break;
	                }
	                flag=0;
	            }
	            //一个玩家出局
	            if(k==l)
	            {
	                die[Qipan[tx][ty].owner]=1;
	            }
	            nextturn = Qipan[tx][ty].owner;
	            count++;
	        }
	    }
	    //被将军
	    if(nextturn!=0 && count != 2)
	    {
	    	if(die[nextturn]!=1)
	    	{
	    		turn = nextturn;
	    		return;
	    	}
	    	else
	    	{
	    		nextturn = 6 - turn - nextturn;//找到另外一个玩家
	    		turn = nextturn;
	    		return;
	    	}
	    }
	    nextturn = turn;
	    //按顺序轮换
	    nextturn++;
	    if(nextturn==4)
	    {
	        nextturn=1;
	    }
	    if(die[nextturn]==1)
	    {
	        nextturn++;
	        if(nextturn==4)
	        {
	            nextturn=1;
	        }
	    }
	    turn = nextturn;
	    return;
	}
}
