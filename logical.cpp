#include <GL/glut.h>
#include <stdio.h>
#include "glm.h"
#include <stdlib.h>
#include <iostream>
#include <math.h>
#include <vector>
#include "particalsystem.h"
#include <windows.h>
#include <sstream>
#include "windows.h"
using namespace std;

#define BITMAP_ID 0x4D42
#define TEXW 128
#define TEXH 128
#define delta 0.12
#define Pi 3.1415926

/************************初始化***************************/
//初始化观察点参数
float eye[] = { 0, -6.0, 0 };
float center[] = { 0, 0, 0 };
float headup[] = { 0, 0, 1 };
float face[] = { 1, 0, 0 };

//bit图参数
BITMAPINFOHEADER bitmapInfoHeader[7];
unsigned char*   bitmapData[7];

//飞船空间坐标
static float index_x = 0, index_y = 0, index_z = 0;
//速度
static float speed = 0.02;
//计数
static int draw_count = 0;
static int die_count = 30;
//分数
static int score = 0;
//子弹数
static int bullet_count = 10;
//视角模式
static int view_mode = 1;
//模型
GLMmodel* Yun_model[3];
GLMmodel* Ship;
GLMmodel* Bulletmode;
//发光材质参数
GLfloat emission1[] = { 1.0f, 1.0f, 1.0f, 1.0f };
GLfloat emission2[] = { 0.0f, 0.0f, 0.0f, 1.0f };
//材质
GLuint texture[7];
//陨石类
struct Yun{
	float dx;
	float dy;
	float dz;
	float target_x;
	float target_z;
	float yun_x;
	float yun_y;
	float yun_z;
	float size;
	int type;
};
//陨石碎片类
struct piece{
	float mx;
	float my;
	float mz;
	float size;
	float cx;
	float cy;
	float cz;
	int count;
	int type;
	int die;
};
//子弹类
struct Bullet{
	float x;
	float y;
	float z;
	float size;
	int die;
};
//容器
static Yun Yuns[4];
static vector<piece> Pieces;
static vector<Bullet> Bullets;
static vector<ParticalSystem> PS;

/************************函数***************************/
//读取贴图
unsigned char *LoadBitmapFile(char *filename, BITMAPINFOHEADER *bitmapInfoHeader)
{
	FILE *filePtr;	
	BITMAPFILEHEADER bitmapFileHeader;	
	unsigned char	*bitmapImage;		
	unsigned int	imageIdx = 0;		
	unsigned char	tempRGB;	
	fopen_s(&filePtr, filename, "rb");
	if (filePtr == NULL) 
		return NULL;
	fread(&bitmapFileHeader, sizeof(BITMAPFILEHEADER), 1, filePtr);
	if (bitmapFileHeader.bfType != BITMAP_ID) {
		fprintf(stderr, "Error in LoadBitmapFile: the file is not a bitmap file\n");
		return NULL;
	}
	fread(bitmapInfoHeader, sizeof(BITMAPINFOHEADER), 1, filePtr);
	fseek(filePtr, bitmapFileHeader.bfOffBits, SEEK_SET);
	bitmapImage = new unsigned char[bitmapInfoHeader->biSizeImage];
	if (!bitmapImage) {
		fprintf(stderr, "Error in LoadBitmapFile: memory error\n");
		return NULL;
	}
	fread(bitmapImage, 1, bitmapInfoHeader->biSizeImage, filePtr);
	if (bitmapImage == NULL) {
		fprintf(stderr, "Error in LoadBitmapFile: memory error\n");
		return NULL;
	}
	for (imageIdx = 0;
		imageIdx < bitmapInfoHeader->biSizeImage; imageIdx += 3) {
		tempRGB = bitmapImage[imageIdx];
		bitmapImage[imageIdx] = bitmapImage[imageIdx + 2];
		bitmapImage[imageIdx + 2] = tempRGB;
	}
	fclose(filePtr);
	return bitmapImage;
}

//材质导入
void texload(int i)
{
	glEnable(GL_TEXTURE_2D);
	glBindTexture(GL_TEXTURE_2D, texture[i]);
	//指定当前纹理的放大/缩小过滤方式
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
	glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, bitmapInfoHeader[i].biWidth,
		bitmapInfoHeader[i].biHeight, 0, GL_RGB, GL_UNSIGNED_BYTE, bitmapData[i]);
}

//画陨石和碎片
void Draw_Yun(float size, float x, float y, float z,int type)
{
	draw_count ++;
	glPushMatrix();
	glTranslatef(x, y, z);
	//单位化
	glmUnitize(Yun_model[type]);
	glmScale(Yun_model[type], size);
	glmDraw(Yun_model[type], GLM_SMOOTH | GLM_MATERIAL);
	glPopMatrix();
}

//大陨石爆炸后变成碎片
void boom(float x,float y,float z,int type)
{
	float mx, my, mz, size;
	for (int i = 1; i <= 2; i++)
	{
		mx = (rand() % 600-300) * 0.01;
		my = (rand() % 600-300) * 0.01;
		mz = (rand() % 600-300) * 0.01;
		size = (rand() % 2 + 1.5) * 0.1;
		struct piece a = { mx, my, mz, size, x, y, z, 600, type ,0};
		Pieces.push_back(a);
	}
}

//发射导弹
void shot()
{
	if (bullet_count != 0)
	{
		Bullet tmp;
		tmp.x = index_x;
		tmp.y = index_y + 0.3;
		tmp.z = index_z;
		tmp.size = 0.05;
		tmp.die = 0;
		Bullets.push_back(tmp);
		bullet_count--;
	}
}

//陨石显示
void Yun_display()
{
	int i;
	for (i = 0; i < 4; i++)
		Draw_Yun(Yuns[i].size, Yuns[i].yun_x, Yuns[i].yun_y, Yuns[i].yun_z, Yuns[i].type);
}

//子弹显示
void Bullet_display()
{
	for (int i = 0; i < Bullets.size(); i++)
	{
		if (Bullets[i].die != 1)
		{
			Bullets[i].y += 0.1;
			glPushMatrix();
			glTranslatef(Bullets[i].x, Bullets[i].y, Bullets[i].z);
			glRotatef(180, 0, 0, 1);
			glmDraw(Bulletmode, GLM_SMOOTH | GLM_COLOR);
			glPopMatrix();
		}
	}
}

//碎片显示
void Pieces_display()
{
	vector<piece>::iterator iter;
	for (iter = Pieces.begin(); iter != Pieces.end();)
	{
		if (iter->cy <= -6.5 || iter->die == 1)
			iter = Pieces.erase(iter);
		else
		{
			if (iter->count != 0)
			{
				iter->cx += iter->mx * 0.001;
				iter->cy += iter->my * 0.001;
				iter->cz += iter->mz * 0.001;
				iter->count--;
			}
			iter->cy -= 0.005;
			Draw_Yun(iter->size, iter->cx, iter->cy, iter->cz, iter->type);
			iter++;
		}		
	}
}

//生成粒子系统
void PS_create(float x,float y,float z)
{
	ParticalSystem tmp(x, y, z, 150);
	tmp.init();
	vector<ParticalSystem>::iterator iter;
	for (iter = PS.begin(); iter != PS.end();)
	{
		if (iter->over == 1)
			iter = PS.erase(iter);
		else
			iter++;
	}
	PS.push_back(tmp);
}

//粒子系统显示
void PS_display()
{
	int i;
	for (i=0; i < PS.size(); i++)
	{
		if (PS[i].over != 1)
		{
			int count = 0;
			for (vector<Particle>::iterator iter = PS[i].particles.begin(); iter != PS[i].particles.end(); iter++)
			{
				if (iter->age <= iter->life)
				{
					count++;
					Vector3D tmp = iter->position;
					glMaterialfv(GL_FRONT, GL_EMISSION, emission1);
					texload(iter->type+2);
					glPushMatrix();
					glTranslatef(tmp.x, tmp.y, tmp.z);
					GLUquadricObj * sphere = gluNewQuadric();
					gluQuadricOrientation(sphere, GLU_OUTSIDE);
					gluQuadricNormals(sphere, GLU_SMOOTH);
					gluQuadricTexture(sphere, GL_TRUE);
					gluSphere(sphere, iter->size, 10, 10);
					gluDeleteQuadric(sphere);
					glDisable(GL_TEXTURE_2D);
					glMaterialfv(GL_FRONT, GL_EMISSION, emission1);
					glPopMatrix();
				}
			}
			if (count == 0)
				PS[i].over = 1;
			PS[i].simulate(0.01);
		}
	}
}

//陨石更新
void Update_Yun(int num)
{
	Yuns[num].target_x = rand() % 60 / 10.0 - 3;
	Yuns[num].yun_x = rand() % 60 / 10.0 - 3;
	Yuns[num].yun_y = 25;
	Yuns[num].target_z = rand() % 50 / 10.0 - 2.5;
	Yuns[num].yun_z = rand() % 50 / 10.0 - 2.5;
	Yuns[num].size = (rand() % 3) * 0.2 + 0.4;
	if (Yuns[num].size == 0.8)
		Yuns[num].size += 0.2;
	Yuns[num].dx = (Yuns[num].target_x - Yuns[num].yun_x)*Yuns[num].dy / 25;
	Yuns[num].dz = (Yuns[num].target_z - Yuns[num].yun_z)*Yuns[num].dy / 25;
	Yuns[num].type = rand() % 3;
	score++;
}

//碰撞检测
void test()
{
	float dis = 0;
	float xy_dis = 0;
	int i,j;
	//飞船和陨石
	for (i = 0; i < 5; i++)
	{
		xy_dis = sqrt((Yuns[i].yun_x - index_x)*(Yuns[i].yun_x - index_x)
			+ (Yuns[i].yun_y - index_y)*(Yuns[i].yun_y - index_y));
		dis = sqrt((Yuns[i].yun_x - index_x)*(Yuns[i].yun_x - index_x)
			+ (Yuns[i].yun_y - index_y)*(Yuns[i].yun_y - index_y) + (Yuns[i].yun_z - index_z)*(Yuns[i].yun_z - index_z));
		if (xy_dis <= 0.7)
		{
			if (dis <= 0.2 + Yuns[i].size)
			{
				die_count--;
				float pengx = Yuns[i].yun_x + (index_x - Yuns[i].yun_x)*Yuns[i].size / dis;
				float pengy = Yuns[i].yun_y + (index_y - Yuns[i].yun_y)*Yuns[i].size / dis;
				float pengz = Yuns[i].yun_z + (index_z - Yuns[i].yun_z)*Yuns[i].size / dis;
				if (Yuns[i].size > 0.6)
					boom(Yuns[i].yun_x, Yuns[i].yun_y, Yuns[i].yun_z, Yuns[i].type);
				PS_create(pengx, pengy, pengz);
				Update_Yun(i);
			}
		}
		else
		{
			if (dis <= 0.7 + Yuns[i].size)
			{
				die_count--;
				float pengx = Yuns[i].yun_x + (index_x - Yuns[i].yun_x)*Yuns[i].size / dis;
				float pengy = Yuns[i].yun_y + (index_y - Yuns[i].yun_y)*Yuns[i].size / dis;
				float pengz = Yuns[i].yun_z + (index_z - Yuns[i].yun_z)*Yuns[i].size / dis;
				if (Yuns[i].size > 0.6)
					boom(Yuns[i].yun_x, Yuns[i].yun_y, Yuns[i].yun_z, Yuns[i].type);
				PS_create(pengx, pengy, pengz);
				Update_Yun(i);
			}
		}
	}
	//飞船和陨石碎片
	for (i = 0; i < Pieces.size(); i++)
	{
		if (Pieces[i].die == 0)
		{
			xy_dis = sqrt((Pieces[i].cx - index_x)*(Pieces[i].cx - index_x)
				+ (Yuns[i].yun_y - index_y)*(Yuns[i].yun_y - index_y));
			dis = sqrt((Pieces[i].cx - index_x)*(Pieces[i].cx - index_x)
				+ (Pieces[i].cy - index_y)*(Pieces[i].cy - index_y) + (Pieces[i].cz - index_z)*(Pieces[i].cz - index_z));
			if (xy_dis <= 0.7)
			{
				if (dis <= 0.2 + Pieces[i].size)
				{
					die_count--;
					Pieces[i].die = 1;
				}
			}
			else
			{
				if (dis <= 0.7 + Pieces[i].size)
				{
					die_count--;
					Pieces[i].die = 1;
				}
			}
		}
	}
	//陨石和碎片
	for (i = 0; i < Pieces.size(); i++)
	{
		if (Pieces[i].die != 1)
		{
			for (int j = 0; j < 5; j++)
			{
				dis = sqrt((Pieces[i].cx - Yuns[j].yun_x)*(Pieces[i].cx - Yuns[j].yun_x)
					+ (Pieces[i].cy - Yuns[j].yun_y)*(Pieces[i].cy - Yuns[j].yun_y) + (Pieces[i].cz - Yuns[j].yun_z)*(Pieces[i].cz - Yuns[j].yun_z));
				if (dis <= Pieces[i].size + Yuns[j].size)
				{
					Pieces[i].die = 1;
				}
			}
		}
	}
	//陨石和子弹
	for (i = 0; i < Bullets.size(); i++)
	{
		if (Bullets[i].die != 1)
		{
			for (int j = 0; j < 5; j++)
			{
				dis = sqrt((Bullets[i].x - Yuns[j].yun_x)*(Bullets[i].x - Yuns[j].yun_x)
					+ (Bullets[i].y - Yuns[j].yun_y)*(Bullets[i].y - Yuns[j].yun_y) + (Bullets[i].z - Yuns[j].yun_z)*(Bullets[i].z - Yuns[j].yun_z));
				if (dis <= 0.3 + Yuns[j].size)
				{
					PS_create(Bullets[i].x, Bullets[i].y, Bullets[i].z);
					if (Yuns[j].size > 0.6)
						boom(Yuns[j].yun_x, Yuns[j].yun_y, Yuns[j].yun_z, Yuns[j].type);
					Bullets[i].die = 1;
					Update_Yun(j);
				}
			}
		}
	}
}

//陨石运动模拟
void run()
{
	for (int i = 0; i < 5; i++)
	{
		Yuns[i].yun_y -= Yuns[i].dy;
		if (Yuns[i].yun_y <= -5.5)
		{
			Update_Yun(i);
		}
		if (i == 2 || i == 4)
		{
			Yuns[i].dx = 0.1*(index_x-Yuns[i].yun_x) / (20 - index_y);
			Yuns[i].dz = 0.1*(index_z-Yuns[i].yun_z) / (20 - index_y);
			Yuns[i].yun_x += Yuns[i].dx;
			Yuns[i].yun_z += Yuns[i].dz;
		}
		else
		{
			Yuns[i].yun_x += Yuns[i].dx;
			Yuns[i].yun_z += Yuns[i].dz;
		}
	}
}

//飞船显示
void Ship_display()
{
	if (die_count != 0)
	{
		glPushMatrix();
		glTranslatef(index_x, index_y, index_z);
		glRotatef(180, 0, 1, 0);
		glRotatef(-90, 1, 0, 0);
		//glRotatef(270, 0, 0, 1);
		glmDraw(Ship, GLM_SMOOTH|GLM_MATERIAL);
		glPopMatrix();
	}
}

//显示字符串
void drawString(const char* str) {
	static GLuint lists;
	lists = glGenLists(128);
	wglUseFontBitmaps(wglGetCurrentDC(), 0, 128, lists);
	for (; *str != '\0'; ++str)
		glCallList(lists + *str);
}

//显示参数
void Data_display()
{
	glPushMatrix();
	glDisable(GL_LIGHTING);
	glColor3f(1.0f, 1.0f, 1.0f);
	if (view_mode == 1)
		glRasterPos3f(3.0f, -1.0f, -2.0f);
	else if (view_mode == 2)
		glRasterPos3f(center[0] + 2.5f, center[1], center[2] - 1.4f);
	else if (view_mode == 3)
		glRasterPos3f(10.0f, 13.0f, -2.0f);	
	stringstream ss;
	ss << die_count;
	//血量
	string HP = "HP: " + ss.str();
	ss.str("");
	drawString(HP.c_str());
	if (view_mode == 1)
		glRasterPos3f(3.0f, -1.0f, -2.2f);
	else if (view_mode == 2)
		glRasterPos3f(center[0] + 2.5f, center[1], center[2] - 1.6f);
	else if (view_mode == 3)
		glRasterPos3f(10.0f, 13.0f, -2.2f);
	ss << bullet_count;
	//子弹数
	string Bullet = "Bullet: " + ss.str();
	ss.str("");
	drawString(Bullet.c_str());
	if (view_mode == 1)
		glRasterPos3f(3.0f, -1.0f, -2.4f);
	else if (view_mode == 2)
		glRasterPos3f(center[0] + 2.5f, center[1], center[2] - 1.8f);
	else if (view_mode == 3)
		glRasterPos3f(10.0f, 13.0f, -2.4f);
	ss << score;
	//分数
	string Score = "Score: " + ss.str();
	ss.str("");
	drawString(Score.c_str());
	glEnable(GL_LIGHTING);
	glPopMatrix();
}

//显示函数
void display()
{
	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();
	//设置观察点
	if (view_mode == 1)
	{
		eye[0] = 0;
		eye[1] = -6;
		eye[2] = 0;
		center[0] = 0;
		center[1] = 0;
		center[2] = 0;
		gluLookAt(eye[0], eye[1], eye[2], center[0], center[1], center[2], headup[0], headup[1], headup[2]);
	}
	else if (view_mode == 2)
	{
		eye[0] = index_x;
		eye[1] = index_y+0.35;
		eye[2] = index_z+0.1;
		center[0] = eye[0];
		center[1] = eye[1]+4;
		center[2] = eye[2];
		gluLookAt(eye[0], eye[1], eye[2], center[0], center[1], center[2], headup[0], headup[1], headup[2]);
	}
	else if (view_mode == 3)
	{
		eye[0] = 15;
		eye[1] = 10;
		eye[2] = 0;
		center[0] = 0;
		center[1] = 10;
		center[2] = 0;
		gluLookAt(eye[0], eye[1], eye[2], center[0], center[1], center[2], headup[0], headup[1], headup[2]);
	}
	glClear(GL_DEPTH_BUFFER_BIT); 
	glClear(GL_COLOR_BUFFER_BIT);
	Data_display();
	Ship_display();
	Yun_display();
	Bullet_display();
	Pieces_display();
	PS_display();
	if (die_count != 0)
		test();
	run();
	//设置背景贴图
	glPushMatrix();
	glMaterialfv(GL_FRONT, GL_EMISSION, emission1);	
	if (view_mode!=3)
		texload(0);
	else
		texload(1);
	if (view_mode != 3)
	{
		glBegin(GL_QUADS);
		glTexCoord2i(1, 1); glVertex3f(40.0, 30.0, 25.0);
		glTexCoord2i(1, 0); glVertex3f(40.0, 30.0, -25.0);
		glTexCoord2i(0, 0); glVertex3f(-40.0, 30.0, -25.0);
		glTexCoord2i(0, 1); glVertex3f(-40.0, 30.0, 25.0);
		glEnd();
	}
	else
	{
		glBegin(GL_QUADS);
		glTexCoord2i(1, 1); glVertex3f(-20.0, 50.0, 25.0);
		glTexCoord2i(1, 0); glVertex3f(-20.0, 50.0, -25.0);
		glTexCoord2i(0, 0); glVertex3f(-20.0, -30.0, -25.0);
		glTexCoord2i(0, 1); glVertex3f(-20.0, -30.0, 25.0);
		glEnd();
	}
	glPopMatrix();
	glDisable(GL_TEXTURE_2D);
	glMaterialfv(GL_FRONT, GL_EMISSION, emission2);
	glutSwapBuffers();
	glFlush();
}

//设置光照
void Light()
{
	glEnable(GL_LIGHTING);
	glEnable(GL_COLOR_MATERIAL);
	glLightModeli(GL_FRONT, GL_AMBIENT_AND_DIFFUSE); 
	//允许光源1使用
	glEnable(GL_LIGHT1);  
	GLfloat light_position1[] = { -10.0f, 0.0f, 5.0f, 1.0f };   
	GLfloat light_diffuse1[] = { 1.0, 1.0, 1.0, 1.0 };  
	GLfloat light_specular1[] = { 0.0, 1.0, 1.0, 1.0 };  
	glLightfv(GL_LIGHT1, GL_POSITION, light_position1);
	glLightfv(GL_LIGHT1, GL_DIFFUSE, light_diffuse1);
	glLightfv(GL_LIGHT1, GL_SPECULAR, light_specular1);
	//允许光源2使用
	glEnable(GL_LIGHT2);   
	GLfloat light_position2[] = { 10.0f, 0.0f, 5.0f, 1.0f };  
	GLfloat light_diffuse2[] = { 1.0, 1.0, 1.0, 1.0 };  
	GLfloat light_specular2[] = { 0.0, 1.0, 1.0, 1.0 };  
	glLightfv(GL_LIGHT2, GL_POSITION, light_position2);
	glLightfv(GL_LIGHT2, GL_DIFFUSE, light_diffuse2);
	glLightfv(GL_LIGHT2, GL_SPECULAR, light_specular2);
}

void reshape(int w,int h)
{
	//设置机口	
	glViewport(0, 0, (GLsizei)w, (GLsizei)h); 
	glMatrixMode(GL_PROJECTION);
	glEnable(GL_DEPTH_TEST);
	glDepthFunc(GL_LEQUAL);
	glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
	glLoadIdentity();
	//创建透视投影矩阵
	gluPerspective(60, (GLfloat)w / (GLfloat)h, 0.1,50); 
}

void TimerFunc(int x)
{
	glutPostRedisplay();
	glutTimerFunc(10, TimerFunc, 1);
}

//加速函数
void speed_up(unsigned char k)
{
	static unsigned char check = '0';
	int time = 0;
	if (check == k)
	{
		if (time <= 10)
		{
			time++;
		}
	}
	else
	{
		time = 0;
		check = k;
	}
	speed = -(10 - time) * (10 - time) * (10 - time) * 0.001 + 1;
}

//键盘
void keyboard(unsigned char k, int x, int y)
{
	switch (k)
	{
		case 'a':
			speed_up(k);
			index_x -= speed;
			if (index_x <= -3)
				index_x = -3;
			break;
		case 'd':
			speed_up(k);
			index_x += speed;
			if (index_x >= 3)
				index_x = 3;
			break;
		case 'w':
			speed_up(k);
			index_z += speed;
			if (index_z >= 2.5)
				index_z = 2.5;
			break;
		case 's':
			speed_up(k);
			index_z -= speed;
			if (index_z <= -2.5)
				index_z = -2.5;
			break;
		case 'q':
			speed_up(k);
			index_y += speed;
			if (index_y >= 15)
				index_y = 15;
			break;
		case 'e':
			speed_up(k);
			index_y -= speed;
			if (index_y <= -3)
				index_y = -3;
			break;
		//切换视角
		case'm':
			view_mode++;
			if (view_mode == 4)
				view_mode = 1;
			break;
		case ' ':
			if (Bullets.size()<10)
				shot();
			break;
	}
	glutPostRedisplay();
}

//读取模型
void ReadModel()
{
	Yun_model[0] = glmReadOBJ("obj//ball1.obj");
	Yun_model[1] = glmReadOBJ("obj//ball2.obj");
	Yun_model[2] = glmReadOBJ("obj//ball3.obj");
	Ship = glmReadOBJ("obj//spaceship2.obj");
	Bulletmode = glmReadOBJ("obj//b2.obj");
	//初始化模型
	glmUnitize(Ship);
	glmScale(Ship, 0.7);          
	glmUnitize(Bulletmode);
	glmScale(Bulletmode, 0.4);
	bitmapData[0] = LoadBitmapFile("bmp//back0.bmp", &bitmapInfoHeader[0]);
	bitmapData[1] = LoadBitmapFile("bmp//back1.bmp", &bitmapInfoHeader[1]);
	bitmapData[2] = LoadBitmapFile("bmp//partical1.bmp", &bitmapInfoHeader[2]);
	bitmapData[3] = LoadBitmapFile("bmp//partical2.bmp", &bitmapInfoHeader[3]);
	bitmapData[4] = LoadBitmapFile("bmp//partical3.bmp", &bitmapInfoHeader[4]);
	bitmapData[5] = LoadBitmapFile("bmp//partical4.bmp", &bitmapInfoHeader[5]);
}

//初始化
void init()
{
	glClearColor(0, 0, 0, 0);
	glShadeModel(GL_SMOOTH);
}

//陨石初始化
void Yun_init()
{
	Yuns[0].yun_y = 25;
	Yuns[1].yun_y = 30;
	Yuns[2].yun_y = 35;
	Yuns[3].yun_y = 40;
	Yuns[4].yun_y = 45;
	for (int i = 0; i < 5; i++)
	{
		Yuns[i].target_x = rand() % 60 / 10.0 - 3;
		Yuns[i].yun_x = rand() % 60 / 10.0 - 3;
		Yuns[i].target_z = rand() % 50 / 10.0 - 2.5;
		Yuns[i].yun_z = rand() % 50 / 10.0 - 2.5;
		Yuns[i].size = (rand() % 3) * 0.2 + 0.4;
		if (Yuns[i].size == 0.8)
			Yuns[i].size += 0.2;
		Yuns[i].dx = (Yuns[i].target_x - Yuns[i].yun_x)*Yuns[i].dy / Yuns[i].yun_y;
		Yuns[i].dy = 0.06;
		Yuns[i].dz = (Yuns[i].target_z - Yuns[i].yun_z)*Yuns[i].dy / Yuns[i].yun_y;
		Yuns[i].type = rand() % 3;
	}
}

//主函数
int main(int argc, char *argv[])
{
	Yun_init();
	ReadModel();
	glutInit(&argc, argv);
	glutInitDisplayMode(GLUT_RGB | GLUT_SINGLE);
	glutInitWindowSize(800, 500);
	glutInitWindowPosition(100, 100);
	glutCreateWindow("GL_Project");
	init();
	//显示
	glutDisplayFunc(display);
	glutReshapeFunc(reshape);
	Light();
	glutKeyboardFunc(keyboard);
	//动画循环
	glutTimerFunc(1000, TimerFunc, 1);
	glutMainLoop();
	return 0;
}