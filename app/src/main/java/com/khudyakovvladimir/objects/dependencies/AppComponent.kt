package com.khudyakovvladimir.objects.dependencies

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.Module

@Component(modules = [MainModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        fun build(): AppComponent

        @BindsInstance
        fun application(application: Application): Builder
    }
}

@Module
class MainModule {

}