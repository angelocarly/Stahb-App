package be.magnias.stahb

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import be.magnias.stahb.adapter.TabAdapter
import be.magnias.stahb.model.Tab
import be.magnias.stahb.ui.TabViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var tabViewModel: TabViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.setHasFixedSize(true)

        var adapter = TabAdapter()
        recycler_view.adapter = adapter

        tabViewModel = ViewModelProviders.of(this).get(TabViewModel::class.java)

        tabViewModel.getAllTabs().observe(this, Observer<List<Tab>> {
            adapter.submitList(it)
        })
    }
}