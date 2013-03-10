package com.pihive.footballclock;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.widget.ImageView;

/**
 * Linker bzw. rechter Spieler
 */
class Player extends ImageView {
	float vy; // nur vertikale Bewegung
	int goals;
	public Player(Context context) {
		super(context);
	}
	
	public interface ShootCompleteListener {
		void onShootComplete();
	}

	public void bounce(int height) {
		if(getY()<0 && vy<0)
			vy=-vy;			
		if(getY()+getHeight()>height && vy>0)
			vy=-vy;
			
	}
	
	public void animateShoot(boolean fromLeft,Ball ball,final ShootCompleteListener shootCompleteListener)
	{
		// stoppe Spieler und Ball f�r die Schuss-Animation 
		vy=0;
		ball.vx=0;
		ball.vy=0;
		ball.setY(getY()+getHeight()-ball.getHeight()); // an Unterkante ausgerichtet
		// lege Ball vor
		if(fromLeft)
			ball.setX(getX()+getWidth());
		else
			ball.setX(getX()-ball.getWidth());
		
		// Schussanimation als Frame Animation
		final AnimationDrawable shootAnimation=(AnimationDrawable)getDrawable();
		shootAnimation.setVisible(true, true);
		// es gibt keine direkt M�glichkeit, bei Frame Animations auf das Ende der Animation zu reagieren
		// daher posten wir ein Runnable nach der berechneten Animationszeit
		Handler handler = new Handler();
		long duration=shootAnimation.getNumberOfFrames()*shootAnimation.getDuration(0); // alle Frames sind gleich lang
		handler.postDelayed(new Runnable() {			
			@Override
			public void run() {
				shootCompleteListener.onShootComplete();	
			}
		}, duration);
		
	}
	
}