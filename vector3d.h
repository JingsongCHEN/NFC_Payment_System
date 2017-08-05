#ifndef Vector3D_H
#define Vector3D_H
#include <iostream>
#include <cmath>
using namespace std;

class Vector3D
{
	public:
	float x;
	float y;
	float z;
	// 缺省构造函数
	Vector3D();
	~Vector3D();
	// 用户构造函数
	Vector3D(float posX, float posY, float posZ);
	//矢量加法
	Vector3D operator+(Vector3D v);
	//矢量减法
	Vector3D operator-(Vector3D v);
	//数乘
	Vector3D operator*(float n);
	//数除
	Vector3D operator/(float n);
};

#endif
