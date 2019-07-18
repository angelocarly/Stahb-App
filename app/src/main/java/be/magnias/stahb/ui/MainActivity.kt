package be.magnias.stahb.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import be.magnias.stahb.R
import be.magnias.stahb.ui.fragment.TabFragment
import be.magnias.stahb.ui.fragment.TabListPagerFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        //Setup fragment
        if (savedInstanceState == null) {
            val fragment = TabListPagerFragment.newInstance()
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }

    }

    // Display a tab via id
    fun showTab(id: String) {
        val fragment = TabFragment.newInstance(id)
        supportFragmentManager
            .beginTransaction()
            .addToBackStack("tabs")
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}