package be.magnias.stahb.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import be.magnias.stahb.ui.fragment.TabListFavoritesFragment
import be.magnias.stahb.ui.fragment.TabListFragment

class TabListPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getCount(): Int {
        return 2
    }

    override fun getItem(i: Int): Fragment {
        if (i == 0) return TabListFragment()
        return TabListFavoritesFragment()
//        val fragment = TabFragment()
//        fragment.arguments = Bundle().apply {
//            putString("id", "5d24c28a019c2408cc9fadd7")
//        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        if (position == 0) return "New"
        else return "Favorites"
    }
}