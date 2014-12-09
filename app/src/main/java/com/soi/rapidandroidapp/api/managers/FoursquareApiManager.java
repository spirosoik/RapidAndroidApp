package com.soi.rapidandroidapp.api.managers;

import android.util.Log;

import com.soi.rapidandroidapp.api.GsonInstance;
import com.soi.rapidandroidapp.api.common.AbstractApiManager;
import com.soi.rapidandroidapp.api.common.ApiRequestAttrs;
import com.soi.rapidandroidapp.api.interceptors.ApiRequestInterceptor;
import com.soi.rapidandroidapp.api.interceptors.FoursquareApiRequestInterceptor;
import com.soi.rapidandroidapp.api.managers.events.FoursquareExploreEvent;
import com.soi.rapidandroidapp.api.managers.events.FoursquareSearchEvent;
import com.soi.rapidandroidapp.api.managers.foursquare.FoursquareResources;
import com.soi.rapidandroidapp.api.managers.net.response.foursquare.explore.Explore;
import com.soi.rapidandroidapp.api.managers.net.response.foursquare.search.SearchResult;
import com.soi.rapidandroidapp.api.services.FoursquareApiService;
import com.soi.rapidandroidapp.managers.EnvironmentManager;
import com.soi.rapidandroidapp.modules.Injector;
import com.soi.rapidandroidapp.utilities.Constants;
import com.soi.rapidandroidapp.utilities.ReflectionUtils;
import com.soi.rapidandroidapp.utilities.StringUtils;
import com.squareup.okhttp.OkHttpClient;

import java.text.SimpleDateFormat;

import javax.inject.Inject;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Created by Spiros I. Oikonomakis on 10/17/14.
 *
 * This class handles all the api requests
 */
public class FoursquareApiManager extends AbstractApiManager<FoursquareApiService> {


    private static FoursquareApiManager mInstance;
    public static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");

    public static synchronized FoursquareApiManager getInstance()
    {
        if (mInstance == null)
            mInstance = new FoursquareApiManager();

        return mInstance;
    }

    public FoursquareApiManager()
    {
        RestAdapter.Builder restAdapterBuilder = getDefaultRestAdapterBuilder();
        // Add extra options to rest adapter
        RestAdapter restAdapter = restAdapterBuilder
                .setRequestInterceptor(new FoursquareApiRequestInterceptor()).build();
        this.service = restAdapter.create(FoursquareApiService.class);
    }

    @Override
    protected String getApiUrl() {
        return Constants.FOURSQUARE_API_URL;
    }

    @Override
    public Object execute(String action, Object...args)
    {
        Object o = ReflectionUtils.tryInvoke(getApiService(), action, args);
        if (StringUtils.equals(action, FoursquareResources.EXPLORE)) {
            return new FoursquareExploreEvent((Explore) o);
        } else if (StringUtils.equals(action, FoursquareResources.SEARCH)) {
            return new FoursquareSearchEvent((SearchResult) o);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Get places from Foursquare
     */
    public void explore(String ll, String clientId, String clientSecret,
                        int limit, int sortByDistance, String v)
    {
        ApiRequestAttrs attrs = new ApiRequestAttrs(this, FoursquareResources.EXPLORE, ll, clientId,
                clientSecret, limit, sortByDistance, v);
        super.execute(attrs);
    }

    /**
     * Get places from Foursquare
     */
    public void search(String intent, Integer radius, String ll, String clientId, String clientSecret, //
                       String query, Integer limit, Integer sortByDistance, String v)
    {
        ApiRequestAttrs attrs = new ApiRequestAttrs(this, FoursquareResources.SEARCH, intent, radius,
                ll, clientId, clientSecret, query, limit, sortByDistance, v);
        super.execute(attrs);
    }
}
