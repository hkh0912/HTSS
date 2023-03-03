package com.fromjin.htss.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.fromjin.htss.Fragment.AllListFragment
import com.fromjin.htss.Fragment.HomeFragment
import com.fromjin.htss.R
import com.fromjin.htss.Fragment.SettingFragment
import com.fromjin.htss.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener{

    private lateinit var view:ActivityMainBinding

    val home = HomeFragment()
//    val list = AllListFragment()
//    val setting = SettingFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        view = ActivityMainBinding.inflate(layoutInflater)


        replaceFragment(home)

        view.navigationBar.setOnItemSelectedListener(this)

        setContentView(view.root)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.home -> {

                replaceFragment(home)
            }
            //R.id.list -> {
//                val fragment = supportFragmentManager.fragmentFactory.instantiate(classLoader, AllListFragment::class.java.name)
//                replaceFragment(fragment)
            //    replaceFragment(list)
            //}
            R.id.chatbot -> {
//                val fragment = supportFragmentManager.fragmentFactory.instantiate(classLoader, SettingFragment::class.java.name)
//                replaceFragment(fragment)
//                replaceFragment(setting)
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