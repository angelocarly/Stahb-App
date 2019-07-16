package be.magnias.stahb.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import be.magnias.stahb.R
import be.magnias.stahb.adapter.TabInfoAdapter
import be.magnias.stahb.model.Tab
import be.magnias.stahb.ui.viewmodel.TabListViewModel
import kotlinx.android.synthetic.main.fragment_tab_list.view.*

class TabListFragment : Fragment() {

    private lateinit var tabViewModel: TabListViewModel
    private lateinit var adapter: TabInfoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Setup viewmodel
        tabViewModel = ViewModelProviders.of(this).get(TabListViewModel::class.java)
        tabViewModel.getAllTabs().observe(this, Observer<List<Tab>> {
            adapter.submitList(it)
        })

        //Setup recyclerview adapter
        this.adapter = TabInfoAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tab_list, container, false)

        //Setup recyclerview
        view.recycler_view.layoutManager = LinearLayoutManager(activity!!.applicationContext)
        view.recycler_view.setHasFixedSize(true)
        view.recycler_view.adapter = adapter

        //Setup clicks
        this.adapter.onItemClick = { tab ->
            (activity as MainActivity).showTab(tab._id)
        }

        return view
    }

    companion object {

        fun newInstance() =
            TabListFragment()
    }
}
