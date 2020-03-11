package com.dsige.apptrinidad.data.module

import com.dsige.apptrinidad.ui.fragments.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

abstract class FragmentBindingModule {
    @Module
    abstract class Vehiculo {
        @ContributesAndroidInjector
        internal abstract fun providGeneralFragment(): VehicleGeneralFragment

        @ContributesAndroidInjector
        internal abstract fun providVehicleCombustibleFragment(): VehicleCombustibleFragment

        @ContributesAndroidInjector
        internal abstract fun providVehiclePorVencerFragment(): VehiclePorVencerFragment
    }


    @Module
    abstract class Main {
        @ContributesAndroidInjector
        internal abstract fun providMainFragment(): MainFragment

        @ContributesAndroidInjector
        internal abstract fun providVehicleFragment(): VehicleFragment
    }

    @Module
    abstract class Camera {
        @ContributesAndroidInjector
        internal abstract fun providCameraFragment(): CameraFragment
    }
}