package com.pihive.footballclock;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.*;
import android.service.dreams.DreamService;
import android.view.*;
import android.view.ViewGroup.LayoutParams;

/**
 * Bildschirmschoner (Daydream) Service. L�dt die {@link Arena}.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1) // DreamService erst ab 4.2
public class SoccerDreamService extends DreamService {
	
	private Arena arena;

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean dimmed=prefs.getBoolean("dimmed",false);
		setScreenBright(!dimmed);
		// optional kein Status-Bar oben
		//setFullscreen(true);
	}
	@Override
	public void onDreamingStarted() {
		super.onDreamingStarted();
		// erzeuge ViewGroup f�r Animation
		arena = new Arena(this);
		arena.setLayoutParams(new 
	            ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		setContentView(arena);
	}
	
	@Override
	public void onDreamingStopped() {
		arena.timeAnimator.cancel(); // stoppe Animationen
		super.onDreamingStopped();
	}

}
