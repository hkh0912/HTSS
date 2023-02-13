package com.fromjin.htss.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.fromjin.htss.Fragment.AllListFragment
import com.fromjin.htss.Fragment.SectorFragment
import com.fromjin.htss.Fragment.HomeFragment
import com.fromjin.htss.R
import com.fromjin.htss.Fragment.SettingFragment
import com.fromjin.htss.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {

    private lateinit var view:ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        view = ActivityMainBinding.inflate(layoutInflater)
        setContentView(view.root)

        // 앱 실행 후 첫화면 설정정;
        replaceFragment(HomeFragment())

        view.navigationBar.setOnItemSelectedListener(this)



    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.home -> {
                val fragment = supportFragmentManager.fragmentFactory.instantiate(classLoader, HomeFragment::class.java.name)
                replaceFragment(fragment)
                Log.d("framgent","home")
            }
            R.id.list -> {
                val fragment = supportFragmentManager.fragmentFactory.instantiate(classLoader, AllListFragment::class.java.name)
                replaceFragment(fragment)
                Log.d("framgent","list")
            }
            R.id.setting -> {
                val fragment = supportFragmentManager.fragmentFactory.instantiate(classLoader, SettingFragment::class.java.name)
                replaceFragment(fragment)
                Log.d("framgent","setting")
            }
        }
        return false
    }

    // 프래그먼트 전환 함수
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }
}