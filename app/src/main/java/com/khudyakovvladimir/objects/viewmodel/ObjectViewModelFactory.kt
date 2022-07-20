package com.khudyakovvladimir.objects.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.khudyakovvladimir.objects.database.ObjectDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.lang.IllegalArgumentException

class ObjectViewModelFactory @AssistedInject constructor(
    @Assisted("application")
    var application: Application,
    var objectDao: ObjectDao
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ObjectViewModel::class.java)) {
            @Suppress("UNCHECKED CAST")
            return ObjectViewModel(
                application = application,
                objectDao = objectDao
            ) as T
        }
        throw IllegalArgumentException("Unable to construct ObjectViewModel")
    }
    @AssistedFactory
    interface Factory {
        fun createObjectViewModelFactory(@Assisted("application") application: Application): ObjectViewModelFactory
    }
}