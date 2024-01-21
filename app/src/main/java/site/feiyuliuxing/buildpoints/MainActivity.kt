package site.feiyuliuxing.buildpoints

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import site.feiyuliuxing.buildpoints.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Example of a call to a native method
    }

}