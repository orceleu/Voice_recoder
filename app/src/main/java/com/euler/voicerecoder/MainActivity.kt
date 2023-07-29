package com.euler.voicerecoder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.euler.voicerecoder.fragement.Fragment1
import com.euler.voicerecoder.fragement.Fragment2
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var fragment1 :Fragment
    private lateinit var fragment2 :Fragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fragment1=Fragment1()
        fragment2=Fragment2()
        replaceFragment(fragment1 )

        bottom_navigation.setOnItemSelectedListener {
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