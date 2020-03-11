package com.dsige.apptrinidad.data.module

import com.dsige.apptrinidad.ui.activities.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {

    @ContributesAndroidInjector
    internal abstract fun bindLoginActivity(): LoginActivity

    @ContributesAndroidInjector(modules = [FragmentBindingModule.Main::class])
    internal abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [FragmentBindingModule.Camera::class])
    internal abstract fun bindCameraActivity(): CameraActivity

    @ContributesAndroidInjector
    internal abstract fun bindPreviewCameraActivity(): PreviewCameraActivity

    @ContributesAndroidInjector
    internal abstract fun bindRegistroActivity(): RegistroActivity

    @ContributesAndroidInjector
    internal abstract fun bindDetailActivity(): DetailActivity

    @ContributesAndroidInjector(modules = [FragmentBindingModule.Vehiculo::class])
    internal abstract fun bindVehicleRegisterActivity(): VehicleRegisterActivity

    @ContributesAndroidInjector
    internal abstract fun bindControlActivity(): ControlActivity

    @ContributesAndroidInjector
    internal abstract fun bindRegistroValeActivity(): RegistroValeActivity
}