package com.example.hellokotlin

import android.content.Intent
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Half.abs
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import java.lang.Exception

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
        val numA: EditText = findViewById(R.id.numA);
        val numB: EditText = findViewById(R.id.numB);
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
            startNextActivity();
            //  not used
//            val fragment = InputWaveFragment();
//            val transaction = supportFragmentManager.beginTransaction();
//            transaction.add(R.id.layout, fragment);
//            transaction.commit();

            val fTmp = object: WaveFunction() {
                override fun f(x: Double): Double {
                    val n = if (numA.text.toString() != "") numA.text.toString().toInt() else 1;
                    var result = 0.0;
                    for (k in 1..n) { // sigma
                        result +=
                            4.0 / ((2 * k - 1) * kotlin.math.PI) * kotlin.math.sin((2 * k - 1) * x);
                    }
                    return result;
                }
            };
        }

        btnPlay.setOnClickListener {
            val input1 = if (numA.text.toString() != "") numA.text.toString().toInt() else 1;
            val input2 = if (numB.text.toString() != "") numB.text.toString().toInt() else 1;
            println("input1: " + numA.text.toString() + " input2: " + numB.text.toString());
            val a = input1 % 10;
            val b = input2 % 10;
            val c = (input1 % 100 - a) % 10;
            val d = (input1 % 100 - b) % 10;
            val f: WaveFunction = object: WaveFunction() {
                override fun f(x: Double): Double {
                    val input1 = if (numA.text.toString() != "") numA.text.toString().toInt() else 1;
                    val input2 = if (numB.text.toString() != "") numB.text.toString().toInt() else 1;
                    val a = input1 % 10;
                    val b = input2 % 10;
                    val c = (input1 % 100 - a) % 10;
                    val d = (input1 % 100 - b) % 10;
                    return a * kotlin.math.sin(x + c / 10.0) + b * kotlin.math.cos( x - d / 100.0);
                }
            };

            val fTmp = object: WaveFunction() {
                override fun f(x: Double): Double {
                    val n = if (numA.text.toString() != "") numA.text.toString().toInt() else 1;
                    var result = 0.0;
                    for (k in 1..n) { // sigma
                        result +=
                        4.0 / ((2 * k - 1) * kotlin.math.PI) * kotlin.math.sin((2 * k - 1) * x);
                    }
                    return result;
                }
            };

            val fTmp2 = object: WaveFunction() {
                override fun f(x: Double): Double {
                    return if (kotlin.math.sin(x) > 0) 0.99999 else 9.9999;
                }
            }
            Thread {
                initTrack();
                startPlaying();
                playback(fTmp2);
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

    private fun playback(wave: WaveFunction = object : WaveFunction(){}) {
        var frameOut: ShortArray = ShortArray(buffLength);
        val amplitude: Int = 32767; // A
        val twoPi: Double = 2 * kotlin.math.PI;
        var phase: Double = 0.0;

        while(isPlaying) {
            for (i in 0 until buffLength) {
                // Asin(2πf / Fs)
                // 振幅はまだ
                frameOut[i] = (wave.f(phase)).toShort();

                phase += twoPi * frequency / Fs;
                if (phase > twoPi) { // make sure: 0 <= phase <= 2pi
                    phase -= twoPi;
                }
            }
            val max = frameOut.map {x -> kotlin.math.abs(x.toInt())}.max() ?: 1;
            if (max != 0) {
                for (i in 0 until buffLength) frameOut[i] = (amplitude / max * frameOut[i]).toShort();
            } else {
                frameOut = ShortArray(buffLength);
            }

            try {
                Track.write(frameOut, 0, buffLength);
            } catch (e: Exception) {
                //Nothing
                println("Failed to write to Track");
            }
        }

    }

    abstract class WaveFunction {
        open fun f(x: Double): Double {
            return kotlin.math.sin(x);
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

    private fun startNextActivity() {
        val nextIntent = Intent(this, WaveActivity::class.java);
        startActivity(nextIntent);
    }
}

