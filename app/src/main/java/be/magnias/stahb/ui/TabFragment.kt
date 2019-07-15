package be.magnias.stahb.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager

import be.magnias.stahb.R
import be.magnias.stahb.adapter.TabInfoAdapter
import be.magnias.stahb.model.Tab
import be.magnias.stahb.ui.viewmodel.TabViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [TabFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [TabFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class TabFragment : Fragment() {

    private lateinit var tabViewModel: TabViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_tab, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        recycler_view.layoutManager = LinearLayoutManager(activity!!.applicationContext)
//        recycler_view.setHasFixedSize(true)

//        var adapter = TabInfoAdapter()
//        recycler_view.adapter = adapter

        tabViewModel = ViewModelProviders.of(this).get(TabViewModel::class.java)

        tabViewModel.getAllTabs().observe(this, Observer<List<Tab>> {
//            adapter.submitList(it)
        })
    }

    companion object {
        fun newInstance(): TabListFragment {
            return TabListFragment()
        }
    }

}
