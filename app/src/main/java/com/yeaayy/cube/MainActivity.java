package com.yeaayy.cube;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import com.yeaayy.util.Log;

public class MainActivity extends Activity implements View.OnClickListener, DialogInterface.OnClickListener{
	private static final int SHUFFLE=0x4642, RESET=0x953f;

	Renderer renderer;
	int action;
	AlertDialog confirmDialog;

	@Override
	public void onCreate(Bundle savedInstanceState){
		setTheme(android.R.style.Theme_Material_NoActionBar);
		super.onCreate(savedInstanceState);
		Log.init(getExternalFilesDir(null));

		GLSurfaceView surfaceView=new GLSurfaceView(this);
		renderer=new Renderer(this);
		surfaceView.setOnTouchListener(renderer);
		surfaceView.setEGLContextClientVersion(2);
		surfaceView.setRenderer(renderer);
		surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

		LinearLayout rootContainer=new LinearLayout(this);
		LinearLayout buttonContainer=new LinearLayout(this);

		Button shuffleButton = new Button(this);
		shuffleButton.setId(SHUFFLE);
		shuffleButton.setText("Shuffle");
		shuffleButton.setOnClickListener(this);

		Button resetButton = new Button(this);
		resetButton.setId(RESET);
		resetButton.setText("Reset");
		resetButton.setOnClickListener(this);

		buttonContainer.addView(shuffleButton, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
		buttonContainer.addView(resetButton, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));

		rootContainer.addView(buttonContainer, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		rootContainer.addView(surfaceView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		rootContainer.setOrientation(LinearLayout.VERTICAL);
		setContentView(rootContainer);

		confirmDialog=new AlertDialog.Builder(this).
			setTitle("(Title)").
			setMessage("Are you sure?").
			setNegativeButton("No", null).
			setPositiveButton("Yes", this).
			create();
	}

	@Override
	public void onClick(View view){
		if(renderer.animationQueue.size()==0){
			Button button=(Button) view;
			confirmDialog.setTitle(button.getText());
			action = button.getId();
			confirmDialog.show();
		}
	}

	@Override
	public void onClick(DialogInterface di, int bid){
		if(action==SHUFFLE){
			renderer.shuffle();
		}else{
			renderer.reset();
		}
	}
}
