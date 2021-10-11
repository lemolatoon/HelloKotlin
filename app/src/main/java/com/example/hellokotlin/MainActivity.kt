package com.example.hellokotlin

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
    }

    fun isPrime(n: Int): Boolean {
        if (n == 1) {
            return false;
        }
        var sqrt: Int = (sqrt(n as Float) as Int) + 1;
        for (i in (2 until sqrt)) {
            if ((n % i) == 0) { // dividable
                return false;
            }
        }
        return true;
    }
}