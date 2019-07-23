package be.magnias.stahb.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import be.magnias.stahb.R
import be.magnias.stahb.model.Resource
import be.magnias.stahb.model.Status
import be.magnias.stahb.model.Tab
import be.magnias.stahb.ui.viewmodel.TabViewModel
import be.magnias.stahb.ui.viewmodel.TabViewModelFactory
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.fragment_tab.*
import kotlinx.android.synthetic.main.fragment_tab.view.*


class TabFragment : Fragment() {

    private lateinit var tabViewModel: TabViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Get the id from the parameters
        val id = getTabId()

        setHasOptionsMenu(true)

        //Init viewmodel
        tabViewModel = ViewModelProviders.of(this, TabViewModelFactory(id)).get(TabViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_tab, container, false)

        //Losding panel visibility
        tabViewModel.getLoadingVisibility().observe(this, Observer {loadingVisible ->
            if(loadingVisible) {
                view.loading_panel.visibility = View.VISIBLE
                view.checkbox_favorite.visibility = View.GONE
            }
            else {
                view.loading_panel.visibility = View.GONE
                view.checkbox_favorite.visibility = View.VISIBLE
            }
        })

        //Load tab data
        tabViewModel.getTab().observe(this, Observer<Resource<Tab>> {
            if (it.status == Status.SUCCESS) {
                //Set tab data
                val tab = it.data!!
                tab_text.text = tab.tab
                val title = "${tab.artist} - ${tab.song}"
                tab_title.text = title
                tab_tuning.text = tab.tuning

                view.checkbox_favorite.isChecked = tab.favorite

            } else if (it.status == Status.ERROR) {
                tab_error.visibility = View.VISIBLE
            }
        })

        //Add or remove tab based on favorite button
        view.checkbox_favorite.setOnClickListener {
            if(view.checkbox_favorite.isChecked) {
                tabViewModel.addToFavorite()
            }
            else {
                tabViewModel.removeFromFavorite()
            }
        }

        return view
    }

    //Get the tab id from the fragment parameters
    private fun getTabId(): String {
        val id: String?
        if (arguments != null) {
            id = arguments!!.getString("id")

            if (id.isNullOrBlank()) {
                throw IllegalArgumentException("TabFragment requires an id")
            }

        } else {
            throw IllegalArgumentException("TabFragment requires an id")
        }

        return id
    }

    //Instance method
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
