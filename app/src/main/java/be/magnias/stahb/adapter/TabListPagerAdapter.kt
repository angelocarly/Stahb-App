package be.magnias.stahb.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import be.magnias.stahb.ui.fragment.TabListFavoritesFragment
import be.magnias.stahb.ui.fragment.TabListFragment

/**
 * PagerAdapter for the TabOverviewFragment
 * Configures which fragments are shown in the pager
 */
class TabListPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getCount(): Int {
        // Show 2 fragment tabs
        return 2
    }

    override fun getItem(i: Int): Fragment {
        // Show the TabList at index 0
        if (i == 0) return TabListFragment()
        // Show the TabListFavorites at index 1
        return TabListFavoritesFragment()
    }

    override fun getPageTitle(position: Int): CharSequence {
        // Show the heading 'New' at index 0
        if (position == 0) return "New"
        // Show the heading 'Favorites' at index 1
        else return "Favorites"
    }
}