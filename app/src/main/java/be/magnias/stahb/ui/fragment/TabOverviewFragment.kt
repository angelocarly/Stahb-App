package be.magnias.stahb.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import be.magnias.stahb.R
import be.magnias.stahb.adapter.TabListPagerAdapter
import be.magnias.stahb.ui.MainActivity
import be.magnias.stahb.ui.viewmodel.TabOverviewViewModel
import kotlinx.android.synthetic.main.fragment_tab_overview.*
import kotlinx.android.synthetic.main.fragment_tab_overview.view.*

class TabOverviewFragment : Fragment() {

    private lateinit var viewModel: TabOverviewViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)

        //Init viewmodel
        viewModel = ViewModelProviders.of(this).get(TabOverviewViewModel::class.java)

        viewModel.getUserLoggedIn().observe(this, Observer {
            if(toolbar != null) {
                if (it) {
                    toolbar.menu.setGroupVisible(R.id.group_loggedIn, true)
                    toolbar.menu.setGroupVisible(R.id.group_loggedOut, false)
                } else {
                    toolbar.menu.setGroupVisible(R.id.group_loggedIn, false)
                    toolbar.menu.setGroupVisible(R.id.group_loggedOut, true)
                }
            }
        })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_tab_overview, container, false)

        view.toolbar.inflateMenu(R.menu.menu_main)
        view.toolbar.setOnMenuItemClickListener {
            onOptionsItemSelected(it)
        }
        if (viewModel.isUserLoggedIn()) {
            view.toolbar.menu.setGroupVisible(R.id.group_loggedIn, true)
            view.toolbar.menu.setGroupVisible(R.id.group_loggedOut, false)
        } else {
            view.toolbar.menu.setGroupVisible(R.id.group_loggedIn, false)
            view.toolbar.menu.setGroupVisible(R.id.group_loggedOut, true)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tab_list_pager_viewpager.adapter = TabListPagerAdapter(childFragmentManager)
        tab_list_pager_tabs.setupWithViewPager(tab_list_pager_viewpager)

    }

    //Options menu settings
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.action_login -> {
                (activity as MainActivity).showLogin()
                true
            }
            R.id.action_logout -> {
                (activity as MainActivity).logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDetach() {
        super.onDetach()
    }

    companion object {
        fun newInstance(): TabOverviewFragment {
            return TabOverviewFragment()
        }
    }
}
