package com.carto.advancedmap.sections.geocoding.base;

import android.os.Bundle;

import com.carto.advancedmap.baseclasses.activities.PackageManagerBaseActivity;
import com.carto.advancedmap.baseclasses.views.PackageManagerBaseView;
import com.carto.advancedmap.sections.basemap.BaseMapsView;
import com.carto.advancedmap.utils.Sources;
import com.carto.core.MapPos;
import com.carto.datasources.LocalVectorDataSource;
import com.carto.geocoding.GeocodingResult;
import com.carto.geometry.FeatureCollection;
import com.carto.geometry.Geometry;
import com.carto.geometry.LineGeometry;
import com.carto.geometry.MultiGeometry;
import com.carto.geometry.PointGeometry;
import com.carto.geometry.PolygonGeometry;
import com.carto.graphics.Color;
import com.carto.layers.CartoOnlineVectorTileLayer;
import com.carto.layers.VectorLayer;
import com.carto.styles.AnimationStyleBuilder;
import com.carto.styles.AnimationType;
import com.carto.styles.BalloonPopupMargins;
import com.carto.styles.BalloonPopupStyleBuilder;
import com.carto.styles.GeometryCollectionStyleBuilder;
import com.carto.styles.LineStyleBuilder;
import com.carto.styles.PointStyleBuilder;
import com.carto.styles.PolygonStyleBuilder;
import com.carto.vectorelements.BalloonPopup;
import com.carto.vectorelements.GeometryCollection;
import com.carto.vectorelements.Line;
import com.carto.vectorelements.Point;
import com.carto.vectorelements.Polygon;

/**
 * Base class for all geocoding - both reverse and regular
 */
public class BaseGeocodingActivity extends PackageManagerBaseActivity {

    public String getFailMessage() {
        // Should be overridden in child class
        throw new UnsupportedOperationException();
    }

    @Override
    public String getFolderName() {
        return "com.carto.geocodingpackages";
    }

    @Override
    public String getSource() {
        return Sources.GEOCODING_TAG + Sources.OFFLINE_GEOCODING;
    }

    LocalVectorDataSource geocodingSource;
    VectorLayer geocodingLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        contentView = new PackageManagerBaseView(this);
        setContentView(contentView);

        super.onCreate(savedInstanceState);

        CartoOnlineVectorTileLayer baseLayer = new CartoOnlineVectorTileLayer(BaseMapsView.DEFAULT_STYLE);
        contentView.mapView.getLayers().add(baseLayer);

        geocodingSource = new LocalVectorDataSource(contentView.projection);
        geocodingLayer = new VectorLayer(geocodingSource);
        contentView.mapView.getLayers().add(geocodingLayer);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void showResult(GeocodingResult result, String title, String description, boolean goToPosition) {

        geocodingSource.clear();

        AnimationStyleBuilder animationBuilder = new AnimationStyleBuilder();
        animationBuilder.setRelativeSpeed(2.0f);
        animationBuilder.setFadeAnimationType(AnimationType.ANIMATION_TYPE_SMOOTHSTEP);

        BalloonPopupStyleBuilder builder = new BalloonPopupStyleBuilder();
        builder.setLeftMargins(new BalloonPopupMargins(0, 0, 0, 0));
        builder.setRightMargins(new BalloonPopupMargins(6, 3, 6, 3));
        builder.setCornerRadius(5);
        builder.setAnimationStyle(animationBuilder.buildStyle());
        // Make sure this label is shown on top of all other labels
        builder.setPlacementPriority(10);

        FeatureCollection collection = result.getFeatureCollection();
        int count = collection.getFeatureCount();

        MapPos position = null;
        Geometry geometry;

        Color color = new Color((short)0, (short)100,(short)200, (short)150);

        for (int i = 0; i < count; i++) {
            geometry = collection.getFeature(i).getGeometry();

            PointStyleBuilder pointBuilder = new PointStyleBuilder();
            pointBuilder.setColor(color);

            LineStyleBuilder lineBuilder = new LineStyleBuilder();
            lineBuilder.setColor(color);

            PolygonStyleBuilder polygonBuilder = new PolygonStyleBuilder();
            polygonBuilder.setColor(color);

            if (geometry instanceof PointGeometry) {
                geocodingSource.add(new Point((PointGeometry)geometry, pointBuilder.buildStyle()));
            } else if (geometry instanceof LineGeometry) {
                geocodingSource.add(new Line((LineGeometry)geometry, lineBuilder.buildStyle()));
            }  else if (geometry instanceof PolygonGeometry) {
                geocodingSource.add(new Polygon((PolygonGeometry)geometry, polygonBuilder.buildStyle()));
            } else if (geometry instanceof MultiGeometry) {

                GeometryCollectionStyleBuilder collectionBuilder = new GeometryCollectionStyleBuilder();
                collectionBuilder.setPointStyle(pointBuilder.buildStyle());
                collectionBuilder.setLineStyle(lineBuilder.buildStyle());
                collectionBuilder.setPolygonStyle(polygonBuilder.buildStyle());

                geocodingSource.add(new GeometryCollection((MultiGeometry)geometry, collectionBuilder.buildStyle()));
            }

            position = geometry.getCenterPos();
        }

        if (goToPosition) {
            contentView.mapView.setFocusPos(position, 1.0f);
            contentView.mapView.setZoom(17.0f, 1.0f);
        }

        BalloonPopup popup = new BalloonPopup(position, builder.buildStyle(), title, description);
        geocodingSource.add(popup);
    }
}
