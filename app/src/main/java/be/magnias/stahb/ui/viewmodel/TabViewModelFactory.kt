package be.magnias.stahb.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Factory to create viewModels with an 'id' parameter.
 * @param id the id of the requested tab.
 */
@Suppress("UNCHECKED_CAST")
class TabViewModelFactory(private val id: String) : ViewModelProvider.NewInstanceFactory() {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TabViewModel(id) as T
    }

}