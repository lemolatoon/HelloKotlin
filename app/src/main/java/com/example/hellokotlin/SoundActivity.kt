package com.example.hellokotlin

import android.content.Intent
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar

class SoundActivity : AppCompatActivity() {

    lateinit var Track: AudioTrack;

    var isPlaying: Boolean = false;

    val Fs: Int = 44100;

    // encoding by 16bit(short)
    val buffLength: Int = AudioTrack.getMinBufferSize(
        Fs, android.media.AudioFormat.CHANNEL_OUT_MONO, android.media.AudioFormat.ENCODING_PCM_16BIT
    );

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sound)

        val btnBack: Button = findViewById(R.id.btnBack);
        val seekBarF: SeekBar = findViewById(R.id.seekBarF);

        val btnPlay: Button = findViewById(R.id.btnPlay);
        val btnStop: Button = findViewById(R.id.btnStop);

        btnBack.setOnClickListener {
            startPreviousActivity();
        }

        btnPlay.setOnClickListener {
            Thread {
                initTrack();
                startPlaying();
                playback();
            }.start();
        }

        btnStop.setOnClickListener {
            stopPlaying();
        }
    }

    private fun initTrack() {
        Track = AudioTrack(
            AudioManager.MODE_NORMAL, Fs, AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT, buffLength, AudioTrack.MODE_STREAM
        );
    }

    private fun playback() {
        val frame_out: ShortArray = ShortArray(buffLength);
        val amplitude: Int = 32767; // A
        val frequency: Int = 440; // f
        val TWOPI: Double = 2 * kotlin.math.PI;
        var phase: Double = 0.0;

        while(isPlaying) {
            for (i in 0 until buffLength) {
                // Asin(2πf / Fs)
                frame_out[i] = (amplitude * kotlin.math.sin(phase)).toShort();
                phase += TWOPI * frequency / Fs;
                if (phase > TWOPI) { // make sure: 0 <= phase <= 2pi
                    phase -= TWOPI;
                }
            }
            Track.write(frame_out, 0, buffLength);
        }
    }

    private fun startPlaying() {
        Track.play();
        isPlaying = true;
    }

    private fun stopPlaying() {
        if (isPlaying) {
            isPlaying = false;
            Track.stop();
            Track.release(); // release the resources
        }
    }

    private fun startPreviousActivity() {
        val nextIntent = Intent(this, MainActivity::class.java);
        startActivity(nextIntent);
    }
}