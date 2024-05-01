package com.yeaayy.cube;

import com.yeaayy.util.Log;

import static android.opengl.GLES20.*;

public class Program{
	private int program;
	public final int mvp, trans, pos, texCoord;
	public final int color, lightDir, norm, rot, tex, anim;

	private void createShader(int type, String source){
		int shader = glCreateShader(type);
		glShaderSource(shader, source);
		glCompileShader(shader);
		Log.println(glGetShaderInfoLog(shader));
		glAttachShader(program, shader);
	}

	public Program(String vertexSource, String fragmentSource){
		program = glCreateProgram();
		createShader(GL_FRAGMENT_SHADER, fragmentSource);
		createShader(GL_VERTEX_SHADER, vertexSource);
		glValidateProgram(program);
		glLinkProgram(program);
		Log.println(glGetProgramInfoLog(program));

		mvp = glGetUniformLocation(program, "mvp");
		rot = glGetUniformLocation(program, "rot");
		anim = glGetUniformLocation(program, "anim");
		tex = glGetUniformLocation(program, "tex");
		trans = glGetUniformLocation(program, "trans");
		color = glGetUniformLocation(program, "color");
		lightDir = glGetUniformLocation(program, "lightDir");
		pos = glGetAttribLocation(program, "aPos");
		norm = glGetUniformLocation(program, "uNorm");
		texCoord = glGetAttribLocation(program, "aTex");

		glEnableVertexAttribArray(pos);
		glEnableVertexAttribArray(texCoord);
	}

	public void use(){
		glUseProgram(program);
	}
}
