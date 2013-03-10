package com.pihive.footballclock;

import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import android.view.ViewGroup.LayoutParams;

/**
 * Normale Launch-Aktivit�t, zeigt das Spiel genau wie der Bildschirmschoner an.
 */
public class MainActivity extends Activity  {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Arena arena = new Arena(this);
		arena.setLayoutParams(new 
	            ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		setContentView(arena);
	}


}
