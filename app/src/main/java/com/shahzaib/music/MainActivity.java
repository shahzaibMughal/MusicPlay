package com.shahzaib.music;

        import android.media.AudioAttributes;
        import android.media.AudioFocusRequest;
        import android.media.AudioManager;
        import android.media.MediaPlayer;
        import android.os.Build;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;

public class MainActivity extends AppCompatActivity implements AudioManager.OnAudioFocusChangeListener {

    static final String LOG_TAG = "123456";

    MediaPlayer mediaPlayer;
    AudioManager audioManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS:
                // stop playback, i.e user started some other playback app.
                // remember, unregistered your controls/buttons and release focus
                Log.i(LOG_TAG, "Focus Loss permanently");
                abandonFocus_releaseMediaplayer();
                break;

            case AudioManager.AUDIOFOCUS_GAIN:
                // you hold the audio focus again, i.e phone call ends
                Log.i(LOG_TAG, "Granted Audio Focus Again");
                startAgain();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // your audio focus is temporarily stolen, but will back soon (i.e for phone calls)
                Log.i(LOG_TAG, "Pause Playback");
                pauseMusic();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // lower the volume, because someone also also playing music over you, i.e notifications
                Log.i(LOG_TAG, "Lower the volume, keep playing");
                pauseMusic();
                break;
        }

    }


    public void playThroughSpeaker(View view) {
        stopMusic(); // if media is already playing through playMusicThroughEarSpeaker

        // first tell what type of music you are playing, this helps system to know how to play
        // means if music it play through main speaker, if VOICE_Communication then play through ear speaker
        // and this is done through Attributes

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            int requestResult = -1;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Log.i(LOG_TAG, "Device is >= android O");
                AudioAttributes attributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();
                AudioFocusRequest audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                        .setOnAudioFocusChangeListener(this)
                        .setAcceptsDelayedFocusGain(true)
                        .setAudioAttributes(attributes)
                        .setWillPauseWhenDucked(true)
                        .build();
                requestResult = audioManager.requestAudioFocus(audioFocusRequest);
            } else // if device is >= Lollipop & less than O
            {
                requestResult = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            }


            //********** checking for requestResult
            if (requestResult == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
                Log.i(LOG_TAG, "Focus Request Failed");
            } else if (requestResult == AudioManager.AUDIOFOCUS_REQUEST_DELAYED) {
                Log.i(LOG_TAG, "Focus Request Delayed, when focus granted, it will inform you");
            } else if (requestResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                Log.i(LOG_TAG, "Focus Request Granted");
                playMusicThroughMainSpeaker();
            }

        }
    }


    public void playThroughEarSpeaker(View view) {
        stopMusic(); // if media is already playing through playMusicThroughMainSpeaker
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            int requestResult = -1;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Log.i(LOG_TAG, "Device is >= android O");
                AudioAttributes attributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();
                AudioFocusRequest audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                        .setOnAudioFocusChangeListener(this)
                        .setAcceptsDelayedFocusGain(true)
                        .setAudioAttributes(attributes)
                        .setWillPauseWhenDucked(true)
                        .build();
                requestResult = audioManager.requestAudioFocus(audioFocusRequest);
            } else // if device is >= Lollipop & less than O
            {
                requestResult = audioManager.requestAudioFocus(this, AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN);
            }


            //********** checking for requestResult
            if (requestResult == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
                Log.i(LOG_TAG, "Focus Request Failed");
            } else if (requestResult == AudioManager.AUDIOFOCUS_REQUEST_DELAYED) {
                Log.i(LOG_TAG, "Focus Request Delayed, when focus granted, it will inform you");
            } else if (requestResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                Log.i(LOG_TAG, "Focus Request Granted");
                playMusicThroughEarSpeaker();
            }

        }
    }


    public void stopMusic(View view) {
        stopMusic();
    }


    private void playMusicThroughMainSpeaker() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                mediaPlayer = MediaPlayer.create(this, R.raw.music); // just play because attributes are already set
            } else { // if device is greater than lollipop && less than marshMellow
                mediaPlayer = MediaPlayer.create(this, R.raw.music);
                // set the attributes
                mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build());
            }
            mediaPlayer.start();
        }
    }

    private void playMusicThroughEarSpeaker() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                mediaPlayer = MediaPlayer.create(this, R.raw.music); // just play because attributes are already set
            } else { // if device is greater than lollipop && less than marshMellow
                AudioAttributes attributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();
                mediaPlayer = MediaPlayer.create(this, R.raw.music,attributes,1);
            }
            mediaPlayer.start();
        }
    }



    private void stopMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
            abandonFocus_releaseMediaplayer();
        }
        else Log.i(LOG_TAG, "Can't Stop, MediaPlayer is Null OR is not playing");
    }

    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) mediaPlayer.pause();
        else Log.i(LOG_TAG, "Can't Pause, MediaPlayer is Null OR Not playing");

    }

    private void startAgain() {
        if (mediaPlayer != null) mediaPlayer.start();
        else Log.i(LOG_TAG, "Can't StartAgain, MediaPlayer is Null");

    }

    private void abandonFocus_releaseMediaplayer() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                // note: you should use same
                AudioAttributes attributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();
                AudioFocusRequest audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                        .setOnAudioFocusChangeListener(this)
                        .setAcceptsDelayedFocusGain(true)
                        .setAudioAttributes(attributes)
                        .setWillPauseWhenDucked(true)
                        .build();
                audioManager.abandonAudioFocusRequest(audioFocusRequest);
            } else {
                audioManager.abandonAudioFocus(this);
            }

            mediaPlayer.release();
            mediaPlayer = null;
            Log.i(LOG_TAG, "Focus Abandoned && Media player released");
        }






   /* public void playMusic(View view) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build();
            mediaPlayer = MediaPlayer.create(this, R.raw.music, attributes, 1);
        } else {
            mediaPlayer = MediaPlayer.create(this, R.raw.music);
        }
        mediaPlayer.start();
    }

    public void stopMusic(View view) {
        mediaPlayer.stop();
    }*/
    }
}

