package com.example.hellokotlin

import android.content.Intent
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView

class SoundActivity : AppCompatActivity() {

    lateinit var Track: AudioTrack;

    private var isPlaying: Boolean = false;

    private val Fs: Int = 44100;

    // f
    var frequency: Int = 440;

    lateinit var seekBarF: SeekBar;

    // encoding by 16bit(short)
    val buffLength: Int = AudioTrack.getMinBufferSize(
        Fs, android.media.AudioFormat.CHANNEL_OUT_MONO, android.media.AudioFormat.ENCODING_PCM_16BIT
    );

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sound)

        val btnBack: Button = findViewById(R.id.btnBack);
        seekBarF = findViewById(R.id.seekBarF);
        val txtFrequency: TextView = findViewById(R.id.txtFrequency);
        seekBarF.progress = 0;
        seekBarF.max = 1000;

        seekBarF.setOnSeekBarChangeListener(
            object: SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    frequency = 2 * progress;
                    txtFrequency.text = frequency.toString() + "Hz";
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            }
        );

        val btnPlay: Button = findViewById(R.id.btnPlay);
        val btnStop: Button = findViewById(R.id.btnStop);

        val btnF: Button = findViewById(R.id.btnF);
        val fInput: EditText = findViewById(R.id.fInput);

        val btnAddF: Button = findViewById(R.id.btnAddF);
        val btnSubF: Button = findViewById(R.id.btnSubF);

        btnF.setOnClickListener {
            frequency = if (fInput.text.toString() != "") {
                fInput.text.toString().toInt()
            } else {
                0
            }
            txtFrequency.text = frequency.toString() + "Hz";
        }

        btnAddF.setOnClickListener {
            frequency += 100;
            txtFrequency.text = frequency.toString() + "Hz";
        }

        btnSubF.setOnClickListener {
            frequency -= 100;
            txtFrequency.text = frequency.toString() + "Hz";
        }

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
        val frameOut: ShortArray = ShortArray(buffLength);
        val amplitude: Int = 32767; // A
        val twoPi: Double = 2 * kotlin.math.PI;
        var phase: Double = 0.0;

        while(isPlaying) {
            for (i in 0 until buffLength) {
                // Asin(2πf / Fs)
                frameOut[i] = (amplitude * kotlin.math.sin(phase)).toShort();
                phase += twoPi * frequency / Fs;
                if (phase > twoPi) { // make sure: 0 <= phase <= 2pi
                    phase -= twoPi;
                }
            }
            Track.write(frameOut, 0, buffLength);
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

