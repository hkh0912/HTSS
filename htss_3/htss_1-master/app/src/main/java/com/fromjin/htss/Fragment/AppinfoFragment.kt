package com.fromjin.htss.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fromjin.htss.R
import com.fromjin.htss.databinding.FragmentAppinfoBinding

class AppinfoFragment : Fragment() {

    private lateinit var view: FragmentAppinfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        view = FragmentAppinfoBinding.inflate(inflater, container, false)
        return view.root
    }
}