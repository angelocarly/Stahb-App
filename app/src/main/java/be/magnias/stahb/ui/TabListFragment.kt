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
import be.magnias.stahb.ui.viewmodel.TabViewModel
import kotlinx.android.synthetic.main.fragment_tab_list.*

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [TabListFragment.OnListFragmentInteractionListener] interface.
 */
class TabListFragment : Fragment() {

    private lateinit var tabViewModel: TabViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tab_list, container, false)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recycler_view.layoutManager = LinearLayoutManager(activity!!.applicationContext)
        recycler_view.setHasFixedSize(true)

        var adapter = TabInfoAdapter()
        recycler_view.adapter = adapter

        tabViewModel = ViewModelProviders.of(this).get(TabViewModel::class.java)

        tabViewModel.getAllTabs().observe(this, Observer<List<Tab>> {
            adapter.submitList(it)
        })
    }

    companion object {

        fun newInstance() =
            TabListFragment()
    }
}
