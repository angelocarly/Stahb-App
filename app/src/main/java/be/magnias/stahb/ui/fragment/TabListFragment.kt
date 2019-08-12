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
import be.magnias.stahb.ui.viewmodel.TabListViewModel
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.fragment_tab_list.*
import kotlinx.android.synthetic.main.fragment_tab_list.view.*
import org.jetbrains.anko.support.v4.runOnUiThread

/**
 * The TabList Fragment.
 * In this fragment, a list with all the available tabs are shown.
 */
class TabListFragment : Fragment() {

    private lateinit var tabViewModel: TabListViewModel
    // RecyclerView adapter
    private lateinit var adapter: TabAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Setup viewmodel
        tabViewModel = ViewModelProviders.of(this).get(TabListViewModel::class.java)

        //Setup recyclerview adapter
        this.adapter = TabAdapter()

        // Show the correct views if the ViewModel is loading data
        tabViewModel.getLoadingVisibility().observe(this, Observer {
            loading_panel.visibility = it
        })

        // Show the list of tabs when the ViewModel retrieves them
        tabViewModel.getAllTabInfo().observe(this, Observer<Resource<List<Tab>>> {

            Logger.d("[New list fragment] Received tabs from viewmodel")

            if (it.status == Status.SUCCESS) {
                // Display the list of tabs
                if (it.data?.isEmpty()!!) {
                    tab_list_no_tabs.visibility = View.VISIBLE
                } else {
                    tab_list_no_tabs.visibility = View.GONE
                    adapter.submitList(it.data)
                    adapter.notifyDataSetChanged()
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

        // Setup recyclerview
        view.recycler_view.layoutManager = LinearLayoutManager(activity!!.applicationContext)
        view.recycler_view.setHasFixedSize(true)
        Logger.d("Createview")
        // Setup RecyclerView Adapter
        view.recycler_view.adapter = this.adapter

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

    override fun onResume() {
        super.onResume()
    }
}
