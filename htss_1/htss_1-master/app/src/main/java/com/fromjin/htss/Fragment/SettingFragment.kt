package com.fromjin.htss.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.fromjin.htss.Model.SettingInfo
import com.fromjin.htss.R
import com.fromjin.htss.RecyclerView.SettingListAdapter
import com.fromjin.htss.databinding.FragmentSettingBinding

class SettingFragment : Fragment(), SettingListAdapter.OnItemClickListener {

    private lateinit var view: FragmentSettingBinding


    private val settingList = mutableListOf(
        SettingInfo("로그인", R.drawable.ic_baseline_edit_notifications_24),
        SettingInfo("공지사항", R.drawable.ic_baseline_edit_notifications_24),
        SettingInfo("푸시알람 설정", R.drawable.ic_baseline_edit_notifications_24),
        SettingInfo("앱 정보", R.drawable.ic_baseline_campaign_24)
    )

    private var settingAdapter = SettingListAdapter(settingList)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        view = FragmentSettingBinding.inflate(inflater, container, false)
        return view.root

        view.settingRecyclerview.apply {
            addItemDecoration(
                DividerItemDecoration(
                    view.settingRecyclerview.context,
                    LinearLayoutManager(context).orientation
                )
            )
            layoutManager = LinearLayoutManager(context)
            adapter = settingAdapter
        }
        settingAdapter.setOnItemClickListener(this)

    }

    override fun onItemClick(view: View, data: SettingInfo, position: Int) {
        when (data.name) {
             "로그인"-> replaceFragment(AppinfoFragment())
            }

        }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }

}

