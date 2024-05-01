package com.yeaayy.math;

public class Vec3 {

	public static float dot3(float[] a, float[] b){
		return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
	}

	public static void add3(float[] r, float[] a, float[] b){
		r[0] = a[0] + b[0];
		r[1] = a[1] + b[1];
		r[2] = a[2] + b[2];
	}

	public static void add3(float[] r, int i, float[] a, int j, float[] b, int k){
		r[i  ] = a[j  ] + b[k  ];
		r[i+1] = a[j+1] + b[k+1];
		r[i+2] = a[j+2] + b[k+2];
	}

	public static void min3(float[] r, int i, float[] a, int j, float[] b, int k){
		r[i  ] = a[j  ] - b[k  ];
		r[i+1] = a[j+1] - b[k+1];
		r[i+2] = a[j+2] - b[k+2];
	}

	public static void min3(float[] r, float[] a, float[] b){
		r[0] = a[0] - b[0];
		r[1] = a[1] - b[1];
		r[2] = a[2] - b[2];
	}

	public static void mul3(float[] r, float[] a, float b){
		r[0] = a[0] * b;
		r[1] = a[1] * b;
		r[2] = a[2] * b;
	}

	public static void div3(float[] r, float[] a, float b){
		r[0] = a[0] / b;
		r[1] = a[1] / b;
		r[2] = a[2] / b;
	}

	public static void cross3(float[]dst, float[] a, float[] b){
		float[] r = new float[3];
		r[0] = a[1] * b[2] - a[2] * b[1];
		r[1] = a[2] * b[0] - a[0] * b[2];
		r[2] = a[0] * b[1] - a[1] * b[0];
		System.arraycopy(r, 0, dst, 0, 3);
	}

	public static float len3(float[]v){
		return (float) Math.sqrt(dot3(v, v));
	}

	public static void norm3(float[]d, float[]s){
		float r = len3(s);
		div3(d, s, r);
	}
}
