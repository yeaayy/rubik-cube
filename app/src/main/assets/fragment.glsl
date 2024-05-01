precision mediump float;

uniform vec4 color;
uniform sampler2D tex;
varying vec2 vTex;
varying float light;

void main(){
	gl_FragColor = light * color * texture2D(tex, vTex);
}
