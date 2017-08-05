#ifndef PARTICALSYSTEM_H
#define PARTICALSYSTEM_H
#include <vector>
#include <math.h>
#include <time.h>
#include <stdlib.h>
#include <iostream>
#include <GL/glut.h>
#include <GL/glu.h>
#include "vector3d.h"
#define PI 3.1415926
using namespace std;

//粒子类
typedef struct
{
	Vector3D position;
	Vector3D velocity;
	float age;
	float life;
	float size;
	int type;
}Particle;

//粒子系统类
class ParticalSystem
{
	public:
		ParticalSystem(float _x,float _y,float _z,int _count)
		{
			index_x = _x;
			index_y = _y;
			index_z = _z;
			ptlCount = _count; 
		};
		void init();
		void simulate(float dt);
		void aging(float dt);
		void kinematics(float dt);
		void create();
		int over;
		vector<Particle> particles;
	private:
		int ptlCount;
		float index_x, index_y, index_z;	
};
#endif
