package course.examples.AudioVideo.AudioManager;

import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class AudioVideoAudioManagerActivity extends Activity {
	
	private SoundPool mSoundPool;
	private int mSoundId[][] = new int[3][30];
	private int mBipId;
	private AudioManager mAudioManager;
	private boolean mCanPlayAudio;
	private char alephbeit[] = { 'à','á','â','ã','ä','å','æ','ç','è','é','ë','ë','ê','ì','î','í','ð','ï','ñ','ò','ô','ô','ó','ö','õ','÷','ø','ù','ù','ú','ú'};
	private int goodAnswer=0;
	private int goodface=0;
	private int badAnswer[] = new int [3];
	private int Bravo=0;
	private int newQ=0;
	private int counter = 0;
	private int result=0;
	final RadioButton radios[] = new RadioButton[3];
	private TextView resultView=null;
	
	private void randomNewLetter(TextView tv) {
		
		if (mCanPlayAudio){
			mSoundPool.play(newQ, 1, 1, 1, 0, 1.0f);
		}

		goodAnswer = (int) (Math.random() * 100) % 30 ;
		goodface= (int) (Math.random() * 100) % 3 ;
		//String aChar = new Character((char) index).toString();
		
		tv.setText( String.valueOf( alephbeit[goodAnswer]));
		
		badAnswer[0] = pickOther();
		badAnswer[1] = pickOther();
		badAnswer[2] = pickOther();
	}
	
	private void updateResult(){

		resultView.setText("Score:" + String.valueOf(result) );
		
	}
	
	private int pickOther(){
		int index =0;
		do {
			index = (int) (Math.random() * 100) % 30 ;
		}while(goodAnswer == index);
		return index;
	}	
	
	private void playSound (int faceNum){
		
		if (mCanPlayAudio){
			if (goodface == faceNum){
				mSoundPool.play(mSoundId[faceNum][goodAnswer], 1, 1, 1, 0, 1.0f);	
			}
			else{
				mSoundPool.play(mSoundId[faceNum][badAnswer[faceNum]], 1, 1, 1, 0, 1.0f);
			}
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Get reference to the AudioManager
		mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		final ImageButton face01Button =(ImageButton) findViewById(R.id.ImageButton01);
		final ImageButton face02Button =(ImageButton) findViewById(R.id.ImageButton02);
		final ImageButton face03Button =(ImageButton) findViewById(R.id.ImageButton03);
		
		// Displays current volume level
		final TextView tv = (TextView) findViewById(R.id.textView1);
		resultView = (TextView) findViewById(R.id.textView2);
		
		radios[0] =(RadioButton) findViewById(R.id.radio0);  
		radios[1] =(RadioButton) findViewById(R.id.radio1);
		radios[2] =(RadioButton) findViewById(R.id.radio2);

		randomNewLetter(tv);
		

		// Increase the volume
		final Button upButton = (Button) findViewById(R.id.button2);

		face01Button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				playSound (0);
			}
		});
		face02Button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				playSound (1);
			}
		});
		face03Button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				playSound (2);
			}
		});

		// Decrease the volume
		final Button downButton = (Button) findViewById(R.id.button1);
		downButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// Play key click sound
				randomNewLetter(tv);

			}
		});
		upButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Play key click sound
				randomNewLetter(tv);
			}
		});

		// Disable the Play Button
		final Button playButton = (Button) findViewById(R.id.button3);
		playButton.setEnabled(false);

		// Create a SoundPool
		mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

		// Load the sound
		mBipId = mSoundPool.load(this, R.raw.slow_whoop_bubble_pop, 1);
		for (int i=0; i<30; i++)
		{
			mSoundId[0][i] = mSoundPool.load(this, R.raw.letter_01_01 + i, 1);	
			mSoundId[1][i] = mSoundPool.load(this, R.raw.letter_02_01 + i, 1);
			mSoundId[2][i] = mSoundPool.load(this, R.raw.letter_03_01 + i, 1);
		}
		
		Bravo = mSoundPool.load(this, R.raw.bravo , 1);
		newQ =  mSoundPool.load(this, R.raw.new_question , 1);
		// Set an OnLoadCompleteListener on the SoundPool
		mSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId,
					int status) {
				if (0 == status) {
					playButton.setEnabled(true);
				}
			}
		});

		// Play the sound using a SoundPool
		playButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (radios[goodface]!=null && radios[goodface].isChecked())
				{
					result++;
					updateResult();
					randomNewLetter(tv);
				}
				else
				{
					result--;
					if (mCanPlayAudio){
						mSoundPool.play(mBipId, 1, 1, 1, 0, 1.0f);
					}
					updateResult();
				}
			}

		});

		// Request audio focus
		int result = mAudioManager.requestAudioFocus(afChangeListener,
				AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

		mCanPlayAudio = AudioManager.AUDIOFOCUS_REQUEST_GRANTED == result;

	}

	// Get ready to play sound effects
	@Override
	protected void onResume() {
		super.onResume();
		mAudioManager.setSpeakerphoneOn(true);
		mAudioManager.loadSoundEffects();
	}

	// Release resources & clean up
	@Override
	protected void onPause() {
		if (null != mSoundPool) {
			for (int i=0; i<30; i++)
			{
				mSoundPool.unload(mSoundId[0][i]);
				mSoundPool.unload(mSoundId[1][i]);
				mSoundPool.unload(mSoundId[2][i]);
			}
			mSoundPool.unload(Bravo);
			mSoundPool.unload(newQ);
					
			mSoundPool.release();
			mSoundPool = null;
		}
		mAudioManager.setSpeakerphoneOn(false);
		mAudioManager.unloadSoundEffects();
		super.onPause();
	}

	// Listen for Audio focus changes
	OnAudioFocusChangeListener afChangeListener = new OnAudioFocusChangeListener() {
		public void onAudioFocusChange(int focusChange) {
			if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
				mAudioManager.abandonAudioFocus(afChangeListener);
				mCanPlayAudio = false;
			}
		}
	};

}