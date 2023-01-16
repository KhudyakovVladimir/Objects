package com.khudyakovvladimir.objects.dependencies

import android.app.Application
import android.content.Context
import androidx.fragment.app.ListFragment
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.khudyakovvladimir.objects.database.DBHelper
import com.khudyakovvladimir.objects.database.ObjectDatabase
import com.khudyakovvladimir.objects.utils.TimeHelper
import com.khudyakovvladimir.objects.view.ChartFragment
import com.khudyakovvladimir.objects.view.NotificationFragment
import com.khudyakovvladimir.objects.view.ObjectFragment
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides

@Component(modules = [MainModule::class])
interface AppComponent {

    fun injectListFragment(list: com.khudyakovvladimir.objects.view.ListFragment)
    fun injectObjectFragment(objectFragment: ObjectFragment)
    fun injectChartFragment(chartFragment: ChartFragment)
    fun injectNotificationFragment(notificationFragment: NotificationFragment)

    @Component.Builder
    interface Builder {
        fun build(): AppComponent

        @BindsInstance
        fun application(application: Application): Builder
    }
}

@Module
class MainModule {

    @Provides
    fun provideObjectDao(objectDatabase: ObjectDatabase) = objectDatabase.objectDao()

    @Provides
    fun provideDBHelper(context: Context): DBHelper {
        return DBHelper(context)
    }

    @Provides
    fun provideNewsDatabase(application: Application): ObjectDatabase {
        return Room.databaseBuilder(application, ObjectDatabase::class.java, "object_db")
            .build()
    }

    @Provides
    fun provideSortType(): Int {
        return 0
    }

    @Provides
    fun provideTimeHelper(): TimeHelper {
        return TimeHelper()
    }
}

