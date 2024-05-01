package com.yeaayy.cube;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.view.MotionEvent;
import android.view.View;
import com.yeaayy.math.Ray3;
import com.yeaayy.math.Vec3;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;

public class Renderer implements GLSurfaceView.Renderer, View.OnTouchListener{
	private static final int DEFAULT_ANIMATION_DURATION = 250;
	private static final int SUFFLING_ANIMATION_DURATION = 100;
	private static final int SHUFFLE_STEP_COUNT = 200;
	private static final float ROTATE_SENSITIVITY = 0.2f;

	AssetManager am;

	Program program;
	float[] mvp = new float[48];
	float[] renderLight = new float[]{1, 1, 1, 0};;
	float[] srcLight = new float[]{0, 0, 1, 0};;
	FloatBuffer textureDrawBuffer;
	float screenWidth, screenHeight;

	ArrayList<Cube> cubeList = new ArrayList<Cube>();
	ArrayList<Face> faces = new ArrayList<Face>();
	Cube[][][] cubes;

	ArrayList<RotationData> animationQueue = new ArrayList<RotationData>();;
	float[] animationMatrix = new float[16];
	float startAngle, endAngle;
	int fullAnimationDuration = DEFAULT_ANIMATION_DURATION;
	int animationDuration;
	boolean animating;
	long animationStartTime;
	long lastTouchEventTime;

	float lx, ly, sx, sy;
	float vx, vy;
	Face touchedFace;
	RotationData selectedRotation;

	public Renderer(MainActivity activity){
		am = activity.getAssets();

		Matrix.setLookAtM(mvp, 32, 5, 5, 5, 0, 0, 0, 0, 1, 0);
		mvp[32+14] = -7;
		Vec3.norm3(srcLight, srcLight);
		Vec3.norm3(renderLight, renderLight);

		textureDrawBuffer = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder()).asFloatBuffer();
		textureDrawBuffer.put(new float[]{0, 0, 0, 1, 1, 1, 1, 0}).position(0);

		cubes = new Cube[3][3][3];
		for(int x = 0;x<3;x++){
			for(int y = 0;y<3;y++){
				for(int z = 0;z<3;z++){
					cubeList.add(cubes[x][y][z] = new Cube(x-1.5f, y-1.5f, z-1.5f, 1, 1, 1));
				}
			}
		}
		//+z -z -y +y -x +x
		int[][][] fid = new int[9][9][];
		int[][][][] g = new int[9][2][4][];
		int[][] n = new int[9][2];
		int i = 0;
		for(int x = 0;x<3;x++){
			for(int y = 0;y<3;y++){
				add(fid, g, n, x, y, 2, 0, i, new float[]{1, 0, 0, 1});
				add(fid, g, n, x, y, 0, 1, i, new float[]{1, 0.5f, 0, 1});
				add(fid, g, n, x, 0, y, 2, i, new float[]{1, 1, 1, 1});
				add(fid, g, n, x, 2, y, 3, i, new float[]{1, 1, 0, 1});
				add(fid, g, n, 0, x, y, 4, i, new float[]{0, 0, 1, 1});
				add(fid, g, n, 2, x, y, 5, i, new float[]{0, 1, 0, 1});

				add(fid, g, n, x, y, 1, 6, i, null);
				add(fid, g, n, x, 1, y, 7, i, null);
				add(fid, g, n, 1, x, y, 8, i, null);
				i++;
			}
		}
		float[]
			px = {1, 0, 0, 0}, nx = {-1, 0, 0, 0}, 
			py = {0, 1, 0, 0}, ny = {0, -1, 0, 0}, 
			pz = {0, 0, 1, 0}, nz = {0, 0, -1, 0};
		for(i=0; i<3; i++){
			//red
			add2(i*6, 18, px, ny, 1, fid, g, 4, 8, 5);
			add2(i*18, 6, py, px, 0, fid, g, 2, 7, 3);
			//orange
			add2(i*6+1, 18, nx, ny, 0, fid, g, 4, 8, 5);
			add2(i*18+1, 6, py, nx, 0, fid, g, 2, 7, 3);
			//white
			add2(i*6+2, 18, px, nz, 1, fid, g, 4, 8, 5);
			add2(i*18+2, 6, pz, px, 1, fid, g, 1, 6, 0);
			//yellow
			add2(i*6+3, 18, px, pz, 1, fid, g, 4, 8, 5);
			add2(i*18+3, 6, pz, nx, 1, fid, g, 1, 6, 0);
			//blue
			add2(i*6+4, 18, py, pz, 0, fid, g, 2, 7, 3);
			add2(i*18+4, 6, nz, py, 0, fid, g, 1, 6, 0);
			//green
			add2(i*6+5, 18, py, nz, 0, fid, g, 2, 7, 3);
			add2(i*18+5, 6, pz, py, 1, fid, g, 1, 6, 0);
		}
	}

	private void add(int[][][] fid, int[][][][] group, int[][] n, int x, int y, int z, int f, int i, float[] color){
		if(f<6){
			Face face = cubes[x][y][z].faceList[f];
			face.setColor(color);
			faces.add(face);
		}
		int[] F = {x, y, z};
		fid[f][i] = F;
		if(i==4)return;
		int j = i%2;
		group[f][j][n[f][j]^(n[f][j]>>1)] = F;
		n[f][j]++;
	}

	private void add2(int a, int b, float[]axis, float[]dir, int multiplier, int[][][] fid, int[][][][] g, int...idx){
		for(int i = 0;i<3;i++){
			faces.get(a+b*i).rotation.add(new RotationData(axis, dir, fid[idx[i]], g[idx[i]], multiplier));
		}
	}

	public void shuffle(){
		fullAnimationDuration = SUFFLING_ANIMATION_DURATION;
		Random rand = new Random();
		int[][] lastFaceId = null;
		for(int i = 0; i<SHUFFLE_STEP_COUNT; i++){
			Face rp = faces.get(rand.nextInt(faces.size()));
			RotationData rd = rp.rotation.get(rand.nextInt(rp.rotation.size()));
			if(rd.cubeIdx.equals(lastFaceId)){
				i--;
				continue;
			}
			lastFaceId = rd.cubeIdx;
			animationQueue.add(rd);
		}
	}

	public void reset(){
		for(int i = 0; i<cubeList.size(); i++){
			cubeList.get(i).reset();
		}
		int i = 0;
		for(int x = 0;x<3;x++){
			for(int y = 0;y<3;y++){
				for(int z = 0;z<3;z++){
					cubes[x][y][z] = cubeList.get(i++);
				}
			}
		}
	}

	public void onSurfaceCreated(GL10 p1, EGLConfig p2){
		glClearColor(0.5f, 0.5f, 0.5f, 1);
		program = new Program(loadString("vertex.glsl"), loadString("fragment.glsl"));
		program.use();

		glActiveTexture(GL_TEXTURE0);
		byte[] data = loadAsset("grid.png");
		Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
		int[] tex = new int[1];
		glGenTextures(1, tex, 0);
		glBindTexture(GL_TEXTURE_2D, tex[0]);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		GLUtils.texImage2D(GL_TEXTURE_2D, 0, bmp, 0);
		bmp.recycle();

		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glFrontFace(GL_CW);
	}

	@Override
	public void onSurfaceChanged(GL10 unused, int w, int h){
		screenWidth = w;
		screenHeight = h;
		glViewport(0, 0, w, h);
		float r = (float) w / h;
		Matrix.frustumM(mvp, 16, -r, r, -1, 1, 1.5f, 100);
		Matrix.multiplyMM(mvp, 0, mvp, 16, mvp, 32);
	}

	@Override
	public void onDrawFrame(GL10 p1){
		if(animating){
			long time = System.currentTimeMillis();
			long dt = time - animationStartTime;
			float[] axis = selectedRotation.axis;
			if(dt<animationDuration){
				Matrix.setRotateM(animationMatrix, 0, startAngle+(endAngle-startAngle)*dt/animationDuration, axis[0], axis[1], axis[2]);
			}else{
				animating = false;
				Matrix.setRotateM(animationMatrix, 0, endAngle, axis[0], axis[1], axis[2]);
				selectedRotation.dettachMatrix(cubes, Math.round(endAngle/90)%4);
				if(animationQueue.size()==0) fullAnimationDuration = 250;
				else fullAnimationDuration = Math.max(fullAnimationDuration-1, 0);
				touchedFace = null;
			}
		}
		if(!animating && animationQueue.size()>0){
			if(fullAnimationDuration<50){
				while(animationQueue.size()>0){
					RotationData rd = animationQueue.remove(0);
					float[] axis = rd.axis;
					float endAngle = (float) (1+Math.floor(Math.random()*3))*90;
					Matrix.setRotateM(animationMatrix, 0, endAngle, axis[0], axis[1], axis[2]);
					rd.attachMatrix(cubes, animationMatrix);
					rd.dettachMatrix(cubes, Math.round(endAngle/90)%4);
				}
				fullAnimationDuration = DEFAULT_ANIMATION_DURATION;
			}else{
				RotationData rd = animationQueue.remove(0);
				this.selectedRotation = rd;
				touchedFace = null;
				startAngle = 0;
				int n = 1;
				endAngle = n*90;
				animationDuration = Math.abs(n*fullAnimationDuration);
				Matrix.setIdentityM(animationMatrix, 0);
				rd.attachMatrix(cubes, animationMatrix);
				animationStartTime = System.currentTimeMillis();
				animating = true;
			}
		}

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glUniformMatrix4fv(program.mvp, 1, false, mvp, 0);
		glUniform3fv(program.lightDir, 1, renderLight, 0);
		glVertexAttribPointer(program.texCoord , 2, GL_FLOAT, false, 0, textureDrawBuffer);

		for(Cube c:cubeList){
			c.render(program);
		}
	}

	private void updateTouchedFace(float x, float y){
		touchedFace = null;
		selectedRotation = null;
		float[] im = new float[16];
		Matrix.invertM(im, 0, mvp, 0);
		Ray3 ray = Ray3.unproject(im, 0, x*2/screenWidth-1.0f, 1.0f-2*y/screenHeight);
		float minDistance = Float.POSITIVE_INFINITY;
		for(Face rp : faces){
			float distance = rp.getIntersetion(ray);
			if(distance<minDistance){
				minDistance = distance;
				touchedFace = rp;
			}
		}
	}

	@Override
	public boolean onTouch(View unused, MotionEvent e){
		float x = e.getX(), y = e.getY();
		float[] im = new float[16];
		switch(e.getAction()){
			case MotionEvent.ACTION_DOWN:
				sx = x;
				sy = y;
				if(!animating) updateTouchedFace(x, y);
				vx = 0;
				vy = 0;
				break;

			case MotionEvent.ACTION_MOVE:
				if(Math.hypot(sx-x, sy-y)>25 && touchedFace!=null && selectedRotation==null){
					float maxDotProduct = 0;
					for(RotationData rd : touchedFace.rotation){
						float dotProduct = Math.abs(rd.directionDotProduct(mvp, 32, x - sx, sy - y));
						if(dotProduct>maxDotProduct){
							selectedRotation = rd;
							maxDotProduct = dotProduct;
						}
					}
					if(selectedRotation!=null){
						selectedRotation.attachMatrix(cubes, animationMatrix);
					}
				}
				if(touchedFace==null){
					Matrix.invertM(im, 0, mvp, 32);
					float u[] = {0, 1, 0, 0};
					Matrix.multiplyMV(u, 0, im, 0, u, 0);
					Matrix.rotateM(mvp, 32, (x - lx) * 180 / screenWidth, u[0], u[1], u[2]);

					u = new float[]{1, 0, 0, 0};
					Matrix.multiplyMV(u, 0, im, 0, u, 0);
					Matrix.rotateM(mvp, 32, (y - ly) * 180 / screenWidth, u[0], u[1], u[2]);

					Matrix.multiplyMM(mvp, 0, mvp, 16, mvp, 32);
				}else if(selectedRotation!=null){
					float[] axis = selectedRotation.axis;
					Matrix.setRotateM(animationMatrix, 0, selectedRotation.directionDotProduct(mvp, 32, x - sx, sy - y) * ROTATE_SENSITIVITY, axis[0], axis[1], axis[2]);
				}
				vx = vx * 0.8f + 200.0f * (x - lx) / (e.getEventTime() - lastTouchEventTime);
				vy = vy * 0.8f + 200.0f * (y - ly) / (e.getEventTime() - lastTouchEventTime);
				break;

			case MotionEvent.ACTION_UP:
				if(selectedRotation!=null && touchedFace!=null){
					startAngle = selectedRotation.directionDotProduct(mvp, 32, x - sx, sy - y) * ROTATE_SENSITIVITY;
					float addAngle = selectedRotation.directionDotProduct(mvp, 32, 0.1f * vx, -0.1f * vy) * ROTATE_SENSITIVITY;
					endAngle = Math.round((startAngle + addAngle) / 90) * 90;
					animationDuration = (int) Math.abs(fullAnimationDuration*(endAngle-startAngle)/90);
					animationStartTime = System.currentTimeMillis();
					animating = true;
				}
				break;
		}
		Matrix.invertM(im, 0, mvp, 32);
		Matrix.multiplyMV(renderLight, 0, im, 0, srcLight, 0);
		lx = x;
		ly = y;
		lastTouchEventTime = e.getEventTime();
		return true;
	}

	public byte[] loadAsset(String name){
		try{
			InputStream i = am.open(name);
			byte[] b = new byte[i.available()];
			i.read(b);
			i.close();
			return b;
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	public String loadString(String name){
		return new String(loadAsset(name));
	}
}
