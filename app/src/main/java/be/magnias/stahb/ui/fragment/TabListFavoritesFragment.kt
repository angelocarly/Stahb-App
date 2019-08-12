package be.magnias.stahb.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import be.magnias.stahb.R
import be.magnias.stahb.adapter.TabAdapter
import be.magnias.stahb.model.Resource
import be.magnias.stahb.model.Status
import be.magnias.stahb.model.Tab
import be.magnias.stahb.ui.MainActivity
import be.magnias.stahb.ui.viewmodel.MainViewModel
import be.magnias.stahb.ui.viewmodel.TabListFavoritesViewModel
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.fragment_tab_list.*
import kotlinx.android.synthetic.main.fragment_tab_list.view.*
import org.jetbrains.anko.support.v4.runOnUiThread

/**
 * The TabList Favorites Fragment.
 * In this fragment, a list with all the user's favorite tabs is shown.
 */
class TabListFavoritesFragment : Fragment() {

    private lateinit var tabFavoritesViewModel: TabListFavoritesViewModel
    // RecyclerView adapter
    private lateinit var adapter: TabAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup ViewModel
        tabFavoritesViewModel = ViewModelProviders.of(this).get(TabListFavoritesViewModel::class.java)

        // Setup Adapter
        this.adapter = TabAdapter()

        // Show the correct views if the ViewModel is loading data
        tabFavoritesViewModel.loadingVisibility.observe(this, Observer {
            loading_panel.visibility = it
        })

        // Show the list of favorites when the ViewModel retrieves them
        tabFavoritesViewModel.getAllFavoriteTabInfo().observe(this, Observer<Resource<List<Tab>>> {

            Logger.d("[Favorites list fragment] Received data from viewmodel")

            if (it.status == Status.SUCCESS) {
                // Display the list of tabs
                if (it.data?.isEmpty()!!) {
                    tab_list_no_tabs.visibility = View.VISIBLE
                } else {
                    tab_list_no_tabs.visibility = View.GONE
                    adapter.submitList(it.data)
                }
            } else if (it.status == Status.ERROR) {
                // Display an error message
                Logger.e("Error occured: ${it.message}")
                tab_list_error.visibility = View.VISIBLE
            }
        })

        // RecyclerView listener when a tab is clicked
        this.adapter.onItemClick = { tab ->
                (activity as MainActivity).showTab(tab._id)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tab_list, container, false)

        // Setup RecyclerView
        view.recycler_view.layoutManager = LinearLayoutManager(activity!!.applicationContext)
        view.recycler_view.setHasFixedSize(true)

        // Setup RecyclerView Adapter
        view.recycler_view.adapter = adapter

        // Access the MainActivity's ViewModel
        activity?.let {
            val sharedViewModel = ViewModelProviders.of(it).get(MainViewModel::class.java)

            // Setup the recyclerView swipe refresh
            view.tab_list_swipe_refresh.setOnRefreshListener {
                sharedViewModel.refreshTabs()
            }

            // Show the correct elements the tabs are refreshed
            sharedViewModel.getRefreshLoadingVisibility().observe(this, Observer {
                    view.tab_list_swipe_refresh.isRefreshing = false
            })
        }

        return view
    }
}
