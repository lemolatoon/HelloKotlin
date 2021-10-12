package com.example.hellokotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kotlin.math.sqrt


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var inputNumber: EditText = findViewById(R.id.inputNumber);
        var result: TextView = findViewById(R.id.txtResult);
        var btnCalc: Button = findViewById(R.id.btnCalc);
        var btnNext: Button = findViewById(R.id.btnNext);

        // calc
        result.text = "it worked";

        btnCalc.setOnClickListener {
            result.text = "calculating...";
            var text = inputNumber.text;
            System.out.println(text);
            var num = text.toString().toInt();
            System.out.println(num);
            result.text = text;
            if (num == 0) {
                result.text = "それはゼロ";
            } else if (isPrime(num)) {
                result.text = num.toString() + "は素数";
            } else {
                result.text = num.toString() + "は素数でない";
            }
        }

        btnNext.setOnClickListener {
            startSoundActivity();
        }
    }

    private fun startSoundActivity() {
        val nextIntent = Intent(this, SoundActivity::class.java);
        startActivity(nextIntent);
    }

    private fun isPrime(n: Int): Boolean {
        if (n == 1) {
            return false;
        }
        var nSqrt: Int = (sqrt(n.toFloat()).toInt()) + 1;
        for (i in (2 until nSqrt)) {
            if ((n % i) == 0) { // dividable
                return false;
            }
        }
        return true;
    }
}