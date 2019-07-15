package be.magnias.stahb.ui

import android.content.Context
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
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.fragment_tab_list.*
import kotlinx.android.synthetic.main.fragment_tab_list.view.*

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [TabListFragment.OnListFragmentInteractionListener] interface.
 */
class TabListFragment : Fragment() {

    private lateinit var tabViewModel: TabListViewModel
    private lateinit var adapter: TabInfoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tabViewModel = ViewModelProviders.of(this).get(TabListViewModel::class.java)

        tabViewModel.getAllTabs().observe(this, Observer<List<Tab>> {
            adapter.submitList(it)
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tab_list, container, false)

        view.recycler_view.layoutManager = LinearLayoutManager(activity!!.applicationContext)
        view.recycler_view.setHasFixedSize(true)

        this.adapter = TabInfoAdapter()
        view.recycler_view.adapter = adapter

        return view
    }

    companion object {

        fun newInstance() =
            TabListFragment()
    }
}
