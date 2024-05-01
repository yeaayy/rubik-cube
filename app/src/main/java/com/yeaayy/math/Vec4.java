package com.yeaayy.math;

public class Vec4{
	public static void norm4(float[] d, float[] s){
		d[0] = s[0] / s[3];
		d[1] = s[1] / s[3];
		d[2] = s[2] / s[3];
		d[3] = 1;
	}
}
