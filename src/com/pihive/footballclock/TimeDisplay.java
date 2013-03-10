package com.pihive.footballclock;

import java.util.Calendar;

import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.*;
import android.view.animation.*;
import android.view.animation.Animation.AnimationListener;
import android.widget.*;

/**
 * Zeitanzeige mit Flip-Animation
 *
 */
public class TimeDisplay extends LinearLayout {

	private TextView hoursTextView;
	private TextView minutesTextView;

	public TimeDisplay(Context context) {
		super(context);
		setBackgroundColor(Color.TRANSPARENT);
		setOrientation(LinearLayout.HORIZONTAL);
		final LinearLayout.LayoutParams wrapLayoutParams=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		// lade spezielle Schriftart
		Typeface digitalTypeface = Typeface.createFromAsset(context.getAssets(), "digitaldream.ttf");		
		
		// die Anzeige besteht aus drei Teilen: HH : MM
		final float textSize=30;
		hoursTextView = new TextView(context);
		hoursTextView.setTypeface(digitalTypeface);
		hoursTextView.setTextSize(textSize);
		hoursTextView.setTextColor(Color.WHITE);
		addView(hoursTextView,wrapLayoutParams);
		
		TextView colonTextView = new TextView(context);
		colonTextView.setTypeface(digitalTypeface);
		colonTextView.setTextSize(textSize);
		colonTextView.setTextColor(Color.WHITE);
		colonTextView.setText(":");
		addView(colonTextView,wrapLayoutParams);
		
		minutesTextView = new TextView(context);
		minutesTextView.setTypeface(digitalTypeface);
		minutesTextView.setTextSize(textSize);
		minutesTextView.setTextColor(Color.WHITE);
		addView(minutesTextView,wrapLayoutParams);
		
		// set current time
		minutesChanged();
		hoursChanged();
	}
	/**
	 * F�hre 90-Grad Flip-Animation durch
	 * @param textView zu kippender View
	 * @param newvalue neuer Wert, der am Ende der Animation gesetzt wird
	 */
	private void animateUpdate(final TextView textView,final String newvalue)
	{
		Animation flipAnimation=new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime,
					Transformation t) {
				final Matrix matrix = t.getMatrix();
				Camera camera=new Camera();
				camera.rotateX(90*interpolatedTime);
				camera.getMatrix(matrix);
				float centerX=textView.getX()+textView.getWidth()/2;
				float centerY=textView.getY()+textView.getHeight()/2;
				matrix.preTranslate(-centerX,-centerY);
				matrix.postTranslate(centerX,centerY);
			}
		};
		flipAnimation.setAnimationListener(new AnimationListenerAdapter() {
			@Override
			public void onAnimationEnd(Animation animation) {
				textView.setText(newvalue);
			}
		});
		flipAnimation.setDuration(600);
		textView.startAnimation(flipAnimation);
	}

	public void hoursChanged() {
		String hours=String.valueOf(currentHours());
		if(hours.length()==1)
			hours="0"+hours;
		if(hoursTextView.isShown())
			animateUpdate(hoursTextView,hours);
		else
			hoursTextView.setText(hours); // initial setup
	}

	public void minutesChanged() {
		String minutes=String.valueOf(currentMinutes());
		if(minutes.length()==1)
			minutes="0"+minutes;
		if(minutesTextView.isShown())
			animateUpdate(minutesTextView,minutes);
		else
			minutesTextView.setText(minutes); // initial setup
	}

	public static int currentHours() {
		Calendar currentCalendar=Calendar.getInstance();
		// zum Testen return (currentCalendar.get(Calendar.MINUTE)*60+currentCalendar.get(Calendar.SECOND))/40;  
		return currentCalendar.get(Calendar.HOUR_OF_DAY); // 24h
	}

	public static int currentMinutes() {
		Calendar currentCalendar=Calendar.getInstance();
		// zum Testen return (currentCalendar.get(Calendar.MINUTE)*60+currentCalendar.get(Calendar.SECOND))/20;  
		return currentCalendar.get(Calendar.MINUTE);
	}

	/**
	 * Leere Implementation von {@link AnimationListener}
	 * zum einfachen �berschreiben einzelner Methoden,
	 * modelliert nach {@link AnimatorListenerAdapter}
	 */
	public static class AnimationListenerAdapter implements AnimationListener
	{
		@Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}
		
	}
}
