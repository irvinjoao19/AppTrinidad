package com.dsige.apptrinidad.data.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dsige.apptrinidad.data.viewModel.*
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(UsuarioViewModel::class)
    internal abstract fun bindUserViewModel(usuarioViewModel: UsuarioViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ServicesViewModel::class)
    internal abstract fun bindServicesViewModel(servicioViewModel: ServicesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SuministroViewModel::class)
    internal abstract fun bindSuministroViewModel(suministroViewModel: SuministroViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}