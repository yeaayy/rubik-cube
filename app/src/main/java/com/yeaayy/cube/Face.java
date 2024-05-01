package com.yeaayy.cube;

import android.opengl.GLES20;
import com.yeaayy.math.Ray3;
import com.yeaayy.math.Vec3;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class Face {
	private FloatBuffer vertexBuffer;
	private float[] color, vertex, normal;
	final ArrayList<RotationData> rotation = new ArrayList<RotationData>();

	public Face(float[] vertex, float[] color){
		this.vertex = vertex;
		normal = new float[4];
		this.color=color;
		vertexBuffer = ByteBuffer.allocateDirect(vertex.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		vertexBuffer.put(vertex).position(0);

		float[] a=new float[3], b=new float[3];
		Vec3.min3(a, 0, vertex, 0, vertex, 3);
		Vec3.min3(b, 0, vertex, 0, vertex, 6);
		Vec3.cross3(normal, b, a);
	}

	public void render(Program p){
		GLES20.glUniform4fv(p.color, 1, color, 0);
		GLES20.glUniform4fv(p.norm, 1, normal, 0);
		GLES20.glVertexAttribPointer(p.pos, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexBuffer.capacity()/3);
	}

	public void setColor(float[] color){
		this.color = color;
	}

	public float getIntersetion(Ray3 ray){
		float minDistance = Float.POSITIVE_INFINITY;
		for(int i = 3; i<vertex.length-3; i+= 3){
			float distance = ray.intersectTriangleAt(vertex, 0, vertex, i, vertex, i + 3);
			if(distance>=0) minDistance = Math.min(minDistance, distance);
		}
		return minDistance;
	}
}
