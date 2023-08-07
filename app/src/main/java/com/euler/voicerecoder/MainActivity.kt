package com.euler.voicerecoder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.euler.voicerecoder.databinding.ActivityMainBinding
import com.euler.voicerecoder.fragement.Fragment1
import com.euler.voicerecoder.fragement.Fragment2


class MainActivity : AppCompatActivity() {
    private lateinit var fragment1 :Fragment
    private lateinit var fragment2 :Fragment
   private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fragment1=Fragment1()
        fragment2=Fragment2()
        replaceFragment(fragment1 )

       binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.action_fragment1 -> replaceFragment(fragment1 )
                R.id.action_fragment2 -> replaceFragment(fragment2 )
            }
            true
        }
    }

    private fun replaceFragment(fragment1: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment1)
            .commit()
    }
}