package com.yeaayy.cube;
import android.opengl.Matrix;

public class RotationData{
	float[] axis, dir;
	int[][] cubeIdx;
	int[][][] g;
	int m;

	public RotationData(float[] axis, float[] dir, int[][] cubeIdx,int[][][]g,int m){
		this.axis=axis;
		this.dir=dir;
		this.cubeIdx=cubeIdx;
		this.g=g;
		this.m=m;
	}

	public float directionDotProduct(float[] mvp, int indexStart, float x, float y){
		float[] v = new float[4];
		Matrix.multiplyMV(v, 0, mvp, indexStart, dir, 0);
		return v[0] * x + v[1] * y;
	}

	public void attachMatrix(Cube[][][] cube, float[] m){
		for(int[]i:cubeIdx){
			cube[i[0]][i[1]][i[2]].animationMatrix = m;
		}
	}

	public void dettachMatrix(Cube[][][] cube, int n){
		for(int[] i : cubeIdx){
			Cube c = get(cube, i);
			Matrix.multiplyMM(c.matrix, 0, c.animationMatrix, 0, c.matrix,0);
			c.animationMatrix = null;
		}
		while(n<0) n += 4;
		if(n==2){
			c(cube, g[0]);
			c(cube, g[1]);
		}
		if((n&1)==0)return;
		if(((n>>1)^m)==1){
			a(cube, g[0]);
			b(cube, g[1]);
		}else{
			b(cube, g[0]);
			a(cube, g[1]);
		}
	}

	private Cube get(Cube[][][] cube, int[] i){
		return cube[i[0]][i[1]][i[2]];
	}

	private void set(Cube[][][] cube, int[] i, Cube v){
		cube[i[0]][i[1]][i[2]]=v;
	}

	private void a(Cube[][][] c, int[][] g){
		Cube tmp=get(c,g[0]);
		set(c,g[0],get(c,g[1]));
		set(c,g[1],get(c,g[2]));
		set(c,g[2],get(c,g[3]));
		set(c,g[3],tmp);
	}

	private void b(Cube[][][] c, int[][] g){
		Cube tmp=get(c,g[3]);
		set(c,g[3],get(c,g[2]));
		set(c,g[2],get(c,g[1]));
		set(c,g[1],get(c,g[0]));
		set(c,g[0],tmp);
	}

	// double rotation
	private void c(Cube[][][] c, int[][] g){
		Cube tmp=get(c,g[0]);
		set(c,g[0],get(c,g[2]));
		set(c,g[2],tmp);

		tmp=get(c,g[1]);
		set(c,g[1],get(c,g[3]));
		set(c,g[3],tmp);
	}
}
