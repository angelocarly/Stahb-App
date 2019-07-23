package be.magnias.stahb.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import kotlinx.android.synthetic.main.fragment_tab_list.view.*
import kotlinx.android.synthetic.main.fragment_tab_list.view.loading_panel

class TabListFavoritesFragment : Fragment() {

    private lateinit var tabFavoritesViewModel: TabListFavoritesViewModel
    private lateinit var adapter: TabAdapter
    private var listLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Setup viewmodel
        tabFavoritesViewModel = ViewModelProviders.of(this).get(TabListFavoritesViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tab_list, container, false)

        //Setup recyclerview
        view.recycler_view.layoutManager = LinearLayoutManager(activity!!.applicationContext)
        view.recycler_view.setHasFixedSize(true)

        //Setup recyclerview adapter
        this.adapter = TabAdapter()
        view.recycler_view.adapter = adapter

        tabFavoritesViewModel.loadingVisibility.observe(this, Observer {
            view.loading_panel.visibility = it
        })

        tabFavoritesViewModel.getAllFavoriteTabInfo().observe(this, Observer<Resource<List<Tab>>> {

            Logger.d("[Favorites list] Received new tabs")

            if (it.status == Status.SUCCESS) {

                if (it.data?.isEmpty()!!) {
                    view.tab_list_no_tabs.visibility = View.VISIBLE
                } else {
                    view.tab_list_no_tabs.visibility = View.GONE
                    adapter.submitList(it.data)
                }
                listLoaded = true
            } else if (it.status == Status.ERROR) {
                Logger.e("Error occured: ${it.message}")
                view.tab_list_error.visibility = View.VISIBLE
            }
        })

        //Setup clicks
        this.adapter.onItemClick = { tab ->
            (activity as MainActivity).showTab(tab._id)
        }

        //Setup swipe refresh
        activity?.let {
            val sharedViewModel = ViewModelProviders.of(it).get(MainViewModel::class.java)

            //Setup swipe refresh
            view.tab_list_swipe_refresh.setOnRefreshListener {
                sharedViewModel.refreshTabs()
            }

            sharedViewModel.getRefreshLoadingVisibility().observe(this, Observer {
                    view.tab_list_swipe_refresh.isRefreshing = false
            })
        }

        return view
    }

    companion object {
        fun newInstance() =
            TabListFavoritesFragment()
    }
}
