package com.example.maciejbihun.myweather.backgroundServices;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;

import java.io.ByteArrayOutputStream;

/**
 * Created by MaciekBihun on 2016-04-12.
 */
public class DownloadPhotoAsynckTask extends AsyncTask<Void, Void, Bitmap> {

    //static variables
    private static final String userCurrentPlaceId = "ChIJN1t_tDeuEmsRUsoyG83frY4";
    private static final String TAG = DownloadPhotoAsynckTask.class.getSimpleName();
    public static final String PHOTO_STRING = "photo_string";

    //normal variables
    private GoogleApiClient mGoogleApiClient;
    private Context mContext;

    public DownloadPhotoAsynckTask(Context mContext,GoogleApiClient mGoogleApiClient) {
        this.mGoogleApiClient = mGoogleApiClient;
        this.mContext = mContext;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        return getPhotos();
    }


    @Override
    protected void onPostExecute(Bitmap bitmap) {
        savePhoto(bitmap);
    }

    //Download photo from google databases
    public Bitmap getPhotos() {
        Bitmap image = null;
        // Get a PlacePhotoMetadataResult containing metadata for the first 10 photos.
        PlacePhotoMetadataResult result = Places.GeoDataApi
                .getPlacePhotos(mGoogleApiClient, userCurrentPlaceId).await();
// Get a PhotoMetadataBuffer instance containing a list of photos (PhotoMetadata).
        if (result != null && result.getStatus().isSuccess()) {
            PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();

            // Get the first photo in the list.
            PlacePhotoMetadata photo = photoMetadataBuffer.get(0);
// Get a full-size bitmap for the photo.
            image = photo.getPhoto(mGoogleApiClient).await()
                    .getBitmap();

// Get the attribution text.
            CharSequence attribution = photo.getAttributions();

            Log.i(TAG, attribution.toString());
        }
        return image;
    }

    //Save bitmap as a String to SharedPreferences.
    public void savePhoto(Bitmap image){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();

        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        //textEncode.setText(encodedImage);

        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor edit = shre.edit();
        edit.putString(PHOTO_STRING,encodedImage);
        edit.commit();
    }

    //Get information about current place.
    public void getInformationsAboutUserPlace() {
        //Download data about user current place
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                .getCurrentPlace(mGoogleApiClient, null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                    Log.i(TAG, String.format("Place '%s' has likelihood: %g",
                            placeLikelihood.getPlace().getName(),
                            placeLikelihood.getLikelihood()));
                }
                likelyPlaces.release();
            }
        });
    }
}
