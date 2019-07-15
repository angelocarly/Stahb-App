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
import be.magnias.stahb.ui.viewmodel.TabListViewModel
import be.magnias.stahb.ui.viewmodel.TabViewModel
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.fragment_tab.*

class TabFragment : Fragment() {

    private lateinit var tabViewModel: TabViewModel

    private lateinit var tab: Tab

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tabViewModel = ViewModelProviders.of(this).get(TabViewModel::class.java)

        tabViewModel.getTab().observe(this, Observer<Tab> {
            tab_text.text = it.tab
            tab_title.text = "${it.artist} - ${it.song}"
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_tab, container, false)
    }

    companion object {
        fun newInstance(): TabFragment {
            return TabFragment()
        }
    }

}
