package io.github.andrewk2112.dependenciesfetcher.sandbox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.andrewk2112.dependenciesfetcher.sandbox.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tv.text = stringFromJNI()
    }

    private external fun stringFromJNI(): String

    private lateinit var binding: ActivityMainBinding

    init { System.loadLibrary("native-lib") }

}
