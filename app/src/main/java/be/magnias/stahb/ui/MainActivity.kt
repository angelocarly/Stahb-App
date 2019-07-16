package be.magnias.stahb.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import be.magnias.stahb.R
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.Logger.addLogAdapter
import com.orhanobut.logger.PrettyFormatStrategy
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        //Setup fragment
        if (savedInstanceState == null) {
            val fragment = TabListFragment.newInstance()
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