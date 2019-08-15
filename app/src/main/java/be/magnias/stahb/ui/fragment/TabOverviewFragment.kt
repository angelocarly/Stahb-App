package be.magnias.stahb.ui.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import be.magnias.stahb.R
import be.magnias.stahb.adapter.TabListPagerAdapter
import be.magnias.stahb.ui.MainActivity
import be.magnias.stahb.ui.viewmodel.TabOverviewViewModel
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.fragment_tab_overview.*
import kotlinx.android.synthetic.main.fragment_tab_overview.view.*

/**
 * Tab Overview Fragment.
 * In this fragment, the different tab lists are shown parallel in a viewpager
 */
class TabOverviewFragment : Fragment() {

    private lateinit var viewModel: TabOverviewViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // Enable the fragment's toolbar
        setHasOptionsMenu(true)

        // Init viewmodel
        viewModel = ViewModelProviders.of(this).get(TabOverviewViewModel::class.java)

        // Listen to changes of the logged in user
        viewModel.getUserLoggedIn().observe(this, Observer { loggedIn : Boolean ->
            toolbar?.let {
                if (loggedIn) {
                    // Show the login option
                    toolbar.menu.setGroupVisible(R.id.group_loggedIn, true)
                    toolbar.menu.setGroupVisible(R.id.group_loggedOut, false)
                } else {
                    // Show the logout option
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

        // Inflate the toolbar
        view.toolbar.inflateMenu(R.menu.menu_main)

        // Toolbar listener
        view.toolbar.setOnMenuItemClickListener {
            onOptionsItemSelected(it)
        }

        if (viewModel.isUserLoggedIn()) {
            // Show the login option
            view.toolbar.menu.setGroupVisible(R.id.group_loggedIn, true)
            view.toolbar.menu.setGroupVisible(R.id.group_loggedOut, false)
        } else {
            // Show the logout option
            view.toolbar.menu.setGroupVisible(R.id.group_loggedIn, false)
            view.toolbar.menu.setGroupVisible(R.id.group_loggedOut, true)
        }

        // Dont show details pane in tablet portrait mode
        if(resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE) {
            view.details_pane?.visibility = View.GONE
            Logger.d("INVIDISDAFIBLE")
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize viewpager
        tab_list_pager_viewpager.adapter = TabListPagerAdapter(childFragmentManager)
        tab_list_pager_tabs.setupWithViewPager(tab_list_pager_viewpager)

    }

    // Options menu settings
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Options menu listener.
     * Executes the correct action when an option is clicked.
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.action_login -> {
                (activity as MainActivity).showLogin()
                true
            }
            R.id.action_logout -> {
                Toast.makeText(context, "Logged out!", Toast.LENGTH_LONG).show()
                (activity as MainActivity).logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        /**
         * Create a new TabOverviewFragment instance.
         */
        fun newInstance(): TabOverviewFragment {
            return TabOverviewFragment()
        }
    }
}
