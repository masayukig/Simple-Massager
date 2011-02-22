package org.orzlabs.android.massage;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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

	private static final int QUIT_ITEM = 0;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		SeekBar seekBar = (SeekBar) findViewById(R.id.SeekBar01);
		seekBar.setOnSeekBarChangeListener(this);
	}

	@Override  
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(TAG, "onKeyDown called:keyCode:" + keyCode + ", event:" + event);

		boolean ret = true;
		SeekBar seekBar = (SeekBar) findViewById(R.id.SeekBar01);
		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			seekBar.setProgress(seekBar.getProgress() + 10);
			onProgressChanged(seekBar, seekBar.getProgress(), true);
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			seekBar.setProgress(seekBar.getProgress() - 10);
			onProgressChanged(seekBar, seekBar.getProgress(), true);
		} else {
			ret = super.onKeyDown(keyCode, event);
		}

		return ret;
	}

	public synchronized void onClick(View v) {
		vibStop();
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
		super.onOptionsItemSelected(item);
		vibStop();

		finish();
		return true;
	}

	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		Log.d(TAG, "onProgressChanged called.");
		vibStop();
		if (progress == 0) {
			return;
		}
		vibStart();
		if (progress == seekBar.getMax()) {
			MassageService.vibMode = MassageService.RANDOM;
			return;
		}
		MassageService.vibMode = MassageService.CONTINUOUS;
		MassageService.vibratingTime  = progress * 10 + 15;
	}

	private synchronized void vibStart() {
		Intent service = new Intent(this, MassageService.class);
		startService(service);

		Log.d(TAG, "vibrate start.");
	}

	private synchronized void vibStop() {
		Intent service = new Intent(this, MassageService.class);
		stopService(service);

		Log.d(TAG, "vibrate end.");
	}
	@Override
	public void onPause() {
		super.onPause();
	}
	public void onStartTrackingTouch(SeekBar seekBar) {
		Log.d(TAG, "onStartTrackingTouch called.");
	}

	public void onStopTrackingTouch(SeekBar seekBar) {
		Log.d(TAG, "onStopTrackingTouch called.");
	}
}