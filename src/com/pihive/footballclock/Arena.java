package com.pihive.footballclock;



import android.animation.TimeAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.*;
import android.widget.TimePicker.OnTimeChangedListener;

/**
 * 
 * Hauptklasse f�r die Animation der Fussballuhr, enth�lt Spieler, Ball, Toranzeige
 *
 */
public class Arena extends FrameLayout implements TimeAnimator.TimeListener {
	private final float interval_ms=3000; // wie lange dauert ein Schuss quer �bers Feld
	public final TimeAnimator timeAnimator; // animator f�r regelm��ige Updates aller Views
	
	private final Player leftPlayer; // spielt f�r die Stunden
	private final Player rightPlayer; // spielt f�r die Minuten 
	private final Ball ball;
	private final TimeDisplay goalDisplay;
	
	
	public Arena(Context context) {
		super(context);
		setBackgroundColor(Color.BLACK);
		timeAnimator = new TimeAnimator();
		timeAnimator.setTimeListener(this);
		final FrameLayout.LayoutParams wrapLayoutParams=new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		leftPlayer = new Player(context);
		leftPlayer.setImageResource(R.drawable.shootanimationh); // AnimationDrawable
		addView(leftPlayer,wrapLayoutParams);
		rightPlayer = new Player(context);
		rightPlayer.setImageResource(R.drawable.shootanimationm);
		addView(rightPlayer,wrapLayoutParams);
		ball = new Ball(context);
		addView(ball,wrapLayoutParams);
		goalDisplay=new TimeDisplay(context);
		addView(goalDisplay,wrapLayoutParams);
		// initialisiere mit aktueller Zeit
		leftPlayer.goals=TimeDisplay.currentHours();
		rightPlayer.goals=TimeDisplay.currentMinutes();
	}
	
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        timeAnimator.start();
    }

    @Override
    public void onDetachedFromWindow() {
    	timeAnimator.cancel();
        super.onDetachedFromWindow();
    }
    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
    		int bottom) {
    	super.onLayout(changed, left, top, right, bottom);
    	if(!changed)
    		return;
    	float w=right-left;
    	float h=bottom-top;
        Log.v("SoccerClock","onLayout "+w+"x"+h);        
        leftPlayer.setX(10);
        leftPlayer.setY((h-leftPlayer.getHeight())/2);
        rightPlayer.setX(w-rightPlayer.getWidth()-10);
        rightPlayer.setY((h-rightPlayer.getHeight())/2);
		ball.setX(w/2);
		ball.setY(h/2);
		goalDisplay.setX(w/2-goalDisplay.getWidth()/2);
		goalDisplay.setY(20);
		// initiales Aufsetzen der Geschwindigkeiten
		calculateVelocities(w,h);        
    }
    
	// setze Geschwindigkeiten so, dass Spieler und Ball sich treffen
	// oder bei �nderung der Stunden bzw. Minuten sich verfehlen
	private void calculateVelocities(float width, float height) {
		// zuf�llige vertikale Ball-Position
		float targetBallY = (float) (Math.random() * (height - ball.getHeight()));
		ball.vy = (targetBallY-ball.getY()) / interval_ms;
		// wohin muss der Spieler, damit er den Ball genau am Fu� trifft (beide Spieler sind gleich hoch)
		float targetPlayerY=targetBallY+ball.getHeight()-rightPlayer.getHeight(); // an Unterkante ausgerichtet
		if (ball.getX() < width / 2) {
			// nach rechts
			ball.vx = (rightPlayer.getX() - (ball.getX()+ball.getWidth())) / interval_ms;
			if (TimeDisplay.currentHours() != leftPlayer.goals)
				targetPlayerY=targetBallY>getHeight()/2 ? 0 : height-rightPlayer.getHeight(); // l�uft in die falsche Ecke, verfehlt den Ball
			rightPlayer.vy = (targetPlayerY-rightPlayer.getY())/interval_ms;
			leftPlayer.vy = (float) (Math.random() * height) / interval_ms; // egal
		} else {
			// nach links
			ball.vx = -(ball.getX() - (leftPlayer.getX() + leftPlayer.getWidth())) / interval_ms;
			if (TimeDisplay.currentMinutes() != rightPlayer.goals)
				targetPlayerY=targetBallY>getHeight()/2 ? 0 : height-rightPlayer.getHeight(); // l�uft in die falsche Ecke, verfehlt den Ball
			leftPlayer.vy = (targetPlayerY - leftPlayer.getY())/interval_ms;
			rightPlayer.vy = (float) (Math.random() * height) / interval_ms; // egal
		}
	}

	// Callback wenn Schussanimation beendet ist
	final private Player.ShootCompleteListener shootCompleteListener=new Player.ShootCompleteListener() {
		@Override
		public void onShootComplete() {
			// neuer Schuss, neues Gl�ck
			calculateVelocities(getWidth(), getHeight());				
		}
	};
/**
 * Eigentliche Animationsroutine, hier werden Spieler und Ball positioniert 
 * und die Schuss- und Tor-Animationen durchgef�hrt.
 * {@link OnTimeChangedListener}
 */
	@Override
	public void onTimeUpdate(TimeAnimator animation, long totalTime_ms,
			long deltaTime_ms) {
		ball.setRotation(totalTime_ms / 5);// in Grad
		ball.setX(ball.getX() + deltaTime_ms * ball.vx);
		ball.setY(ball.getY() + deltaTime_ms * ball.vy);
		leftPlayer.setY(leftPlayer.getY() + deltaTime_ms * leftPlayer.vy);
		rightPlayer.setY(rightPlayer.getY() + deltaTime_ms * rightPlayer.vy);
		// Spieler �ndern automatisch Richtung am oberen/unteren Rand
		rightPlayer.bounce(getHeight());
		leftPlayer.bounce(getHeight());

		if (ball.getX() < leftPlayer.getX() + leftPlayer.getWidth()
				&& ball.vx < 0) {
			// linker Rand
			if (rightPlayer.goals != TimeDisplay.currentMinutes()) {
				// links ins Aus, Tor f�r rechts
				Log.v("SoccerClock", "right player (minutes) scores");
				rightPlayer.goals = TimeDisplay.currentMinutes();
				goalDisplay.minutesChanged();
			}
			leftPlayer.animateShoot(true, ball, shootCompleteListener);
		} else if (ball.getX() + ball.getWidth() > rightPlayer.getX()
				&& ball.vx > 0) {
			// rechter Rand
			if (leftPlayer.goals != TimeDisplay.currentHours()) {
				// rechts ins Aus, Tor f�r links
				Log.v("SoccerClock", "left player (hours) scores");
				leftPlayer.goals = TimeDisplay.currentHours();
				goalDisplay.hoursChanged();
			}
			rightPlayer.animateShoot(false, ball, shootCompleteListener);
		}
	}
}
