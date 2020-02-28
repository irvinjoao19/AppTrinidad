package com.dsige.apptrinidad.data.module

import com.dsige.apptrinidad.ui.fragments.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

abstract class FragmentBindingModule {

    @Module
    abstract class Main {
        @ContributesAndroidInjector
        internal abstract fun providMainFragment(): MainFragment
    }

    @Module
    abstract class Camera {
        @ContributesAndroidInjector
        internal abstract fun providCameraFragment(): CameraFragment
    }
}