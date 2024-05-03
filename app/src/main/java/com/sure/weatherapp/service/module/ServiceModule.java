package com.sure.weatherapp.service.module;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.sure.weatherapp.service.Service;
import com.sure.weatherapp.service.ServiceImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn({SingletonComponent.class})
public interface ServiceModule {


    @Provides
    static Service providesService(@ApplicationContext Context context) {
        return new ServiceImpl(provideRequestQueue(context));
    }

    @Singleton
    @Provides
    static RequestQueue provideRequestQueue(@ApplicationContext Context context) {
        return Volley.newRequestQueue(context);
    }

}
