package org.orzlabs.android.massage;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class Massage extends Activity
	implements OnClickListener,
	OnSeekBarChangeListener {
	private static final String TAG = Massage.class.getSimpleName();

	// TODO to Enum
	private static final int RANDOM = 0;
	private static final int CONTINUOUS = 1;

	private static final int QUIT_ITEM = 0;
	private boolean isVibrating = false;
	private int vibMode;
	private Vibrator vib;
	private static MyVibrator myVibrator;

	private long vibratingTime = 1000L;

	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		SeekBar seekBar = (SeekBar) findViewById(R.id.SeekBar01);
		seekBar.setOnSeekBarChangeListener(this);
		Context context = getBaseContext();
		if (vib == null) {
			vib = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
		}
	}
	
	
	@Override
	public synchronized void onClick(View v) {
		int viewId = v.getId();
		Log.d(TAG, "viewId:" + viewId + "vibMode:" + vibMode);
		
		if (isVibrating) {
			vibStop();
			return;
		}
		if (myVibrator != null) {
			myVibrator.setToDie();
		}
		vibStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem itemQuit = menu.add(0, QUIT_ITEM, 0, "Quit");
		itemQuit.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		vib.cancel();
		if (myVibrator != null) {
			myVibrator.setToDie();
		}
		finish();
		return true;
	}
	private class MyVibrator implements Runnable {
		private boolean isAlive;
		@Override
		public void run() {
			while (isAlive) {
				long vibratingTime = getVibratingTime();
				if (isVibrating) {
					Log.d(TAG, "vibrate start " + vibratingTime + "msec.");
					vib.vibrate(vibratingTime);
					Log.d(TAG, "vibrate end " + vibratingTime + "msec.");
				}
				sleep(vibratingTime);
				Log.d(TAG, "vibrating " + vibratingTime + "msec.");
			}
		}
		void setToAlive() {
			isAlive = true;
		}
		void setToDie() {
			isAlive = false;
		}
		private void sleep(long mVibratingTime) {
			try {
				long sleepBonus = 0;
				
				if (vibMode == RANDOM) {
					sleepBonus = (long) (Math.random() * 200L);
				} else {
					sleepBonus = 1000L - vibratingTime;
				}
				long sleepTime = mVibratingTime + sleepBonus;
				Log.d(TAG, "sleep start " + sleepTime + "msec.");
				Thread.sleep(sleepTime);
				Log.d(TAG, "sleep end " + sleepTime + "msec.");
			} catch (InterruptedException e) {
				Log.e(TAG, e.getMessage(),e);
			}
		}
		private long getVibratingTime() {
			if (vibMode == RANDOM) {
				double rand = Math.random();
				return (long) (rand * 700);
			}
			return 400L;
		}
		
	}
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		Log.d(TAG, "onProgressChanged called.");
		if (progress == 0) {
			vibStop();
			return;
		}
		vibStart();
		if (progress == seekBar.getMax()) {
			vibMode = RANDOM;
			return;
		}
		vibMode = CONTINUOUS;
		vibratingTime  = progress * 10 + 15;
	}

	private synchronized void vibStart() {
		if (myVibrator != null) {
			myVibrator.setToDie();
		}
		myVibrator = new MyVibrator();
		myVibrator.setToAlive();
		Thread myVibratorThread = new Thread(myVibrator);
		myVibratorThread.start();

		isVibrating = true;

		Log.d(TAG, "vibrate start.");
	}

	private synchronized void vibStop() {
		if (myVibrator != null) {
			myVibrator.setToDie();
		}
		vib.cancel();
		isVibrating = false;

		Log.d(TAG, "vibrate end.");
	}
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		Log.d(TAG, "onStartTrackingTouch called.");
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		Log.d(TAG, "onStopTrackingTouch called.");
	}
}