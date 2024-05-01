attribute vec4 aPos;
attribute vec2 aTex;
uniform vec4 uNorm;
uniform mat4 mvp;
uniform mat4 rot;
uniform mat4 anim;
uniform vec3 lightDir;
varying vec2 vTex;
varying float light;

void main(){
	vTex = aTex;
	mat4 m = anim * rot;
	float L = dot(lightDir, (m * uNorm).xyz);
	light = sqrt(0.0625 + max(0.0, L));
	gl_Position = mvp * m * aPos;
}
