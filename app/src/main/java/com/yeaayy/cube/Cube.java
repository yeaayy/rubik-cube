package com.yeaayy.cube;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class Cube {
	private static final float[] DEFAULT_COLOR = {0, 0, 0, 1};
	private static final float[] IDENTITY_MATRIX;

	final Face[] faceList;
	final float[] matrix; // for rendering
	public float[] animationMatrix; // for animating

	public Cube(float x, float y, float z, float w, float h, float t){
		matrix = new float[16];
		reset();
		faceList = new Face[]{
			new Face(new float[]{x+w, y  , z+t, x  , y  , z+t, x  , y+h, z+t, x+w, y+h, z+t}, DEFAULT_COLOR), 
			new Face(new float[]{x  , y  , z  , x+w, y  , z  , x+w, y+h, z  , x  , y+h, z  }, DEFAULT_COLOR), 
			new Face(new float[]{x  , y  , z  , x  , y  , z+t, x+w, y  , z+t, x+w, y  , z  }, DEFAULT_COLOR), 
			new Face(new float[]{x  , y+h, z  , x+w, y+h, z  , x+w, y+h, z+t, x  , y+h, z+t}, DEFAULT_COLOR), 
			new Face(new float[]{x  , y  , z+t, x  , y  , z  , x  , y+h, z  , x  , y+h, z+t}, DEFAULT_COLOR), 
			new Face(new float[]{x+w, y  , z  , x+w, y  , z+t, x+w, y+h, z+t, x+w, y+h, z  }, DEFAULT_COLOR)
		};
	}

	public void reset(){
		Matrix.setIdentityM(matrix, 0);
	}

	public void render(Program p){
		GLES20.glUniformMatrix4fv(p.rot, 1, false, matrix, 0);
		GLES20.glUniformMatrix4fv(p.anim, 1, false, animationMatrix == null ? IDENTITY_MATRIX : animationMatrix, 0);
		for(int i = 0; i<faceList.length; i++){
			faceList[i].render(p);
		}
	}

	static{
		IDENTITY_MATRIX = new float[16];
		Matrix.setIdentityM(IDENTITY_MATRIX, 0);
	}
}
