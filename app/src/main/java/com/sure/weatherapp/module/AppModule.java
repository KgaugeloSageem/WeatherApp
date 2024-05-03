package com.sure.weatherapp.module;

import com.sure.weatherapp.mapview.service.WeatherService;
import com.sure.weatherapp.mapview.service.WeatherServiceImpl;
import com.sure.weatherapp.mapview.repository.WeatherRepository;
import com.sure.weatherapp.mapview.repository.WeatherRepositoryImpl;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn({SingletonComponent.class})
public interface AppModule {

    @Binds
    WeatherService bindWeatherService(WeatherServiceImpl impl);

    @Binds
    WeatherRepository bindWeatherRepository(WeatherRepositoryImpl impl);

}
