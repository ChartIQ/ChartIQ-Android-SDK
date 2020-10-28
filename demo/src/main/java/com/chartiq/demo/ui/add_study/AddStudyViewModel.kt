package com.chartiq.demo.ui.add_study

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.chartiq.demo.util.combineLatest
import com.chartiq.sdk.model.Study
import java.util.*

class AddStudyViewModel : ViewModel() {

    val originalStudies = MutableLiveData<List<Study>>(emptyList())

    val selectedStudies = MutableLiveData<List<Study>>(emptyList())

    val query = MutableLiveData<String>("")

    val filteredStudies =
        Transformations.map(originalStudies.combineLatest(query)) { (list, query) ->
            val filtered = list.filter { it.name.toLowerCase(Locale.getDefault())
                .contains(query.toLowerCase(Locale.getDefault())) }
            filtered
        }
}
