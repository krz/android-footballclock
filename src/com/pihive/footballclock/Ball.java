package com.pihive.footballclock;

import android.content.Context;
import android.widget.ImageView;

class Ball extends ImageView {
	float vx,vy;
	public Ball(Context context) {
		super(context);
		setImageResource(R.drawable.football);
	}
}