package be.magnias.stahb.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import be.magnias.stahb.R
import be.magnias.stahb.model.Tab
import be.magnias.stahb.ui.viewmodel.TabViewModel
import be.magnias.stahb.ui.viewmodel.TabViewModelFactory
import kotlinx.android.synthetic.main.fragment_tab.*


class TabFragment : Fragment() {

    private lateinit var tabViewModel: TabViewModel

    private lateinit var tab: Tab

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Get the id from the parameters
        val id = getTabId()

        //Init viewmodel
        tabViewModel = ViewModelProviders.of(this, TabViewModelFactory(id)).get(TabViewModel::class.java)

        tabViewModel.getTab().observe(this, Observer<Tab> {
            tab_text.text = it.tab
            val title = "${it.artist} - ${it.song}"
            tab_title.text = title
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_tab, container, false)
    }

    private fun getTabId(): String {
        val id: String?
        if (arguments != null) {
            id = arguments!!.getString("id")

            if(id.isNullOrBlank()) {
                throw IllegalArgumentException("TabFragment requires an id")
            }

        } else {
            throw IllegalArgumentException("TabFragment requires an id")
        }

        return id
    }

    companion object {
        fun newInstance(id: String): TabFragment {

            val frag = TabFragment()

            val args = Bundle()
            args.putString("id", id)
            frag.arguments = args

            return frag
        }
    }

}
