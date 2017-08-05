#include "particalsystem.h"
//初始化
void ParticalSystem::init()
{
	over = 0;
	create();
}
//创建
void ParticalSystem::create()
{
	int i;
	for (i = 0; i < ptlCount; i++)
	{
		float x = (rand() % 51) - 25.0f;
		float y = (rand() % 51) - 25.0f;
		float z = (rand() % 51) - 25.0f;
		float o_range = sqrt(x*x + y*y + z*z);
		int n_range = rand() % 3 + 1;
		x = x * n_range / o_range;
		y = y * n_range / o_range;
		z = z * n_range / o_range;
		int type = rand() % 4;
		Particle tmp = { Vector3D(index_x, index_y, index_z), Vector3D(x, y, z), 0.0f, 0.5, 0.012f ,type};
		particles.push_back(tmp);
	}
}
//模拟
void ParticalSystem::simulate(float dt)
{
	aging(dt);
	kinematics(dt);
}
//计时
void ParticalSystem::aging(float dt)
{
	for (vector<Particle>::iterator iter = particles.begin(); iter != particles.end(); iter++)
	{
		if (iter->age <= iter->life)
			iter->age += dt;
	}
}
//运动
void ParticalSystem::kinematics(float dt)
{
	for (vector<Particle>::iterator iter = particles.begin(); iter != particles.end(); iter++)
	{
		iter->position = iter->position + iter->velocity*dt;
	}
}

