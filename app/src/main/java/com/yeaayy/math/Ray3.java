package com.yeaayy.math;

import android.opengl.Matrix;
import java.util.Arrays;

public class Ray3{
	public float[] direction, start;

	public Ray3(float[] start, float[] direction){
		this.direction = new float[3];
		this.start = new float[3];
		Vec3.norm3(this.direction, direction);
		System.arraycopy(start, 0, this.start, 0, 3);
	}

	public static Ray3 fromLine(float[] a, float[] b){
		float[] dir = new float[3];
		Vec3.min3(dir, b, a);
		return new Ray3(a, dir);
	}

	private static final float eps = 0.00001f;
	public float intersectTriangleAt(float[] v0, int i0, float[] v1, int i1, float[] v2, int i2){
		float[] e1 = new float[3], e2 = new float[3];
		float[] p = new float[3], t = new float[3], q = new float[3];
		Vec3.min3(e1, 0, v1, i1, v0, i0);
		Vec3.min3(e2, 0, v2, i2, v0, i0);
		Vec3.cross3(p, direction, e2);
		float det = Vec3.dot3(e1, p);
		if(Math.abs(det)<eps)return Float.POSITIVE_INFINITY;
		det = 1.0f/det;
		Vec3.min3(t, 0, this.start, 0, v0, i0);
		float u = Vec3.dot3(t, p)*det;
		if(u<0.0f || u>1.0f)return Float.POSITIVE_INFINITY;
		Vec3.cross3(q, t, e1);
		float v = Vec3.dot3(this.direction, q)*det;
		if(v<0.0f || (u+v)>1.0f)return Float.POSITIVE_INFINITY;
		return Vec3.dot3(e2, q)*det;
	}

	public static Ray3 unproject(float[] im, int i, float x, float y){
		float[] n = new float[]{x, y, -1, 1};
		float[] f = new float[]{x, y,  1, 1};
		Matrix.multiplyMV(n, 0, im, i, n, 0);
		Matrix.multiplyMV(f, 0, im, i, f, 0);
		Vec4.norm4(n, n);
		Vec4.norm4(f, f);
		return fromLine(n, f);
	}
}
