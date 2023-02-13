package com.fromjin.htss.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.fromjin.htss.R
import com.fromjin.htss.databinding.FragmentAllListBinding


class AllListFragment : Fragment(), View.OnClickListener {

    private lateinit var view: FragmentAllListBinding

    var focus = "sector"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        view = FragmentAllListBinding.inflate(inflater, container, false)

        view.sector.setOnClickListener(this)
        view.theme.setOnClickListener(this)

        view.sector.performClick()

        focus = arguments?.getString("focus").toString()

        when(focus){
            "sector" -> view.sector.performClick()
            "theme" -> view.theme.performClick()
        }




        return view.root
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.all_list_frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onClick(p0: View?) {
        when(p0?.id) {
            R.id.sector -> {
                view.sector.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
                view.theme.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_brown))
                replaceFragment(SectorListFragment())
            }
            R.id.theme -> {
                view.theme.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
                view.sector.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_brown))
                replaceFragment(ThemeListFragment())
            }
        }
    }

}