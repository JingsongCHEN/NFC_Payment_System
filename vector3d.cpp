#include "vector3d.h"

Vector3D::Vector3D()
{
}
Vector3D::~Vector3D()
{
}
Vector3D::Vector3D(float posX, float posY, float posZ)
{
	x = posX;
	y = posY;
	z = posZ;
}

Vector3D Vector3D::operator+(Vector3D v)
{
	return Vector3D(x + v.x, v.y + y, v.z + z);
}
Vector3D Vector3D::operator-(Vector3D v)
{
	return Vector3D(x - v.x, y - v.y, z - v.z);
}
Vector3D Vector3D::operator*(float n)
{
	return Vector3D(x*n, y*n, z*n);
}
Vector3D Vector3D::operator/(float n)
{
	return Vector3D(x / n, y / n, z / n);
}
