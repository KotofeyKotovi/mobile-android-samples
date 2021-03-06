package com.carto.advancedmap.sections.geocoding.offline;

import android.os.Bundle;

import com.carto.advancedmap.main.ActivityData;
import com.carto.advancedmap.sections.geocoding.base.ReverseGeocodingBaseActivity;
import com.carto.geocoding.PackageManagerReverseGeocodingService;

/**
 * Created by aareundo on 21/08/2017.
 */
@ActivityData(name = "Offline Reverse Geocoding", description = "Click the map to find an address")
public class OfflineReverseGeocodingActivity extends ReverseGeocodingBaseActivity {

    @Override
    public String getFailMessage() {
        return "Couldn't find any addresses. Are you sure you have downloaded the region you're trying to reverse geocode?";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        service = new PackageManagerReverseGeocodingService(contentView.manager);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!contentView.hasLocalPackages()) {
            contentView.banner.alert("Click the globe icon to download a package");
        }
    }
}
