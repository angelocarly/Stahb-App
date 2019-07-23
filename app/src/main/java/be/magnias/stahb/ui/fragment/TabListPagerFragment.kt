package be.magnias.stahb.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import be.magnias.stahb.R
import be.magnias.stahb.adapter.TabListPagerAdapter
import be.magnias.stahb.model.Status
import be.magnias.stahb.ui.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_tab_list_pager.*
import kotlinx.android.synthetic.main.fragment_tab_list_pager.view.*

class TabListPagerFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_tab_list_pager, container, false)

        setHasOptionsMenu(true)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tab_list_pager_viewpager.adapter = TabListPagerAdapter(childFragmentManager)
        tab_list_pager_tabs.setupWithViewPager(tab_list_pager_viewpager)

    }

    override fun onDetach() {
        super.onDetach()
    }

    companion object {
        fun newInstance() =
            TabListPagerFragment()
    }
}
