package com.slashgil.formula1.di

import com.slashgil.formula1.data.local.DatabaseDriverFactory
import com.slashgil.formula1.data.local.F1LocalDataSource
import com.slashgil.formula1.data.remote.ErgastApiService
import com.slashgil.formula1.data.remote.ErgastApiServiceImpl
import com.slashgil.formula1.data.repository.F1RepositoryImpl
import com.slashgil.formula1.db.F1Database
import com.slashgil.formula1.domain.repository.F1Repository
import com.slashgil.formula1.domain.usecase.GetConstructorStandingsUseCase
import com.slashgil.formula1.domain.usecase.GetDriverStandingsUseCase
import com.slashgil.formula1.domain.usecase.GetDriversUseCase
import com.slashgil.formula1.domain.usecase.GetRaceResultsUseCase
import com.slashgil.formula1.domain.usecase.GetRaceScheduleUseCase
import com.slashgil.formula1.domain.usecase.GetWikipediaSummaryUseCase
import com.slashgil.formula1.ui.F1ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val networkModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    coerceInputValues = true
                })
            }
        }
    }
    single<ErgastApiService> { ErgastApiServiceImpl(get()) }
}

val dataModule = module {
    single { F1Database(get<DatabaseDriverFactory>().createDriver()) }
    single { F1LocalDataSource(get()) }
    single<F1Repository> { F1RepositoryImpl(get(), get()) }
}

val domainModule = module {
    factory { GetDriversUseCase(get()) }
    factory { GetRaceScheduleUseCase(get()) }
    factory { GetDriverStandingsUseCase(get()) }
    factory { GetConstructorStandingsUseCase(get()) }
    factory { GetRaceResultsUseCase(get()) }
    factory { GetWikipediaSummaryUseCase(get()) }
}

val viewModelModule = module {
    factory { F1ViewModel(get(), get(), get(), get(), get()) }
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(
            networkModule,
            dataModule,
            domainModule,
            viewModelModule
        )
    }

fun initKoinWithPlatform(platformModule: Module, appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(
            platformModule,
            networkModule,
            dataModule,
            domainModule,
            viewModelModule
        )
    }