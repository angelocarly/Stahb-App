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
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger.addLogAdapter
import com.orhanobut.logger.PrettyFormatStrategy
import com.orhanobut.logger.FormatStrategy





class MainActivity : AppCompatActivity() {

    private lateinit var tabViewModel: TabViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val formatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
            .methodCount(0)         // (Optional) How many method line to show. Default 2
            .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
            .tag("STAHB")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
            .build()
        addLogAdapter(AndroidLogAdapter(formatStrategy))

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