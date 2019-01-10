package com.example.samsung.weathertest;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.samsung.weathertest.MainActivity;
import com.example.samsung.weathertest.R;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

interface ApiService {
    //베이스 Url
    static final String BASEURL = "https://api2.sktelecom.com/";
    static final String APPKEY ="5dc01571-afac-44f0-9853-d1cdc0d4da9a";
    //get 메소드를 통한 http rest api 통신
    @GET("weather/current/hourly")
    Call<JsonObject> getHourly (@Header("appKey")String appKey , @Query("version") int version,
                                @Query("lat") double latitude, @Query("lon") double longitude);

}



public class Splash extends AppCompatActivity implements LocationListener {
    LocationManager locationManager;
   // ImageView SeoulDawnTitle;
    ConnectivityManager manager;
    double latitude;
    double longitude;
    StringBuffer sb = new StringBuffer();
    String tc, tmax, tmin, code , name, stormYn;
    JSONObject array;
//    TextView latText, lonText, weather;
//    Button but1;
    ImageView WMTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            requestLocation();
        }
        //but1=findViewById(R.id.button1);

        if (Build.VERSION.SDK_INT >= 21) {
            // 21 버전 이상일 때
            getWindow().setStatusBarColor(Color.BLACK);
        }

        WMTitle = findViewById(R.id.wmsplash);
        Animation Alpha = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.splashalpha);

        // SeoulDawnTitle = findViewById(R.id.seouldawntitle);
       // Animation Aplha = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.activity_splash);
        if (!isConnected()) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(Splash.this);
            dialog.setMessage("네트워크 연결을 확인해주세요");
            dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //ActivityCompat.finishAffinity(getParent());
                    finish();
                }
            }).show();
        } else {
            WMTitle.startAnimation(Alpha);
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                  Intent intent = new Intent(getApplicationContext(), DBActivity.class);
                            intent.putExtra("tc", tc);
                            intent.putExtra("tmax", tmax);
                            intent.putExtra("tmin", tmin);
                            intent.putExtra("code", code);
                            intent.putExtra("name", name);
                            intent.putExtra("stormYn", stormYn);

                    Log.e("TSPLASH", "오늘 기온 :"+tc+"최고 기온 : "+tmax+"최저 기온"+tmin+""+"하늘코드 : "+code+"하늘 상태 : "+name + "stormYn" + stormYn);
                    startActivity(intent);
                    finish();
                }
            }, 3000);
        }

    }


    private boolean isConnected () {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetWork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetWork != null && activeNetWork.isConnectedOrConnecting();

        return isConnected;
    }

    private int getNetworkType () {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetWork = cm.getActiveNetworkInfo();

        return activeNetWork.getType();
    }

    private void initView() {
        //뷰세팅
//        latText = (TextView) findViewById(R.id.latitude);
//        lonText = (TextView) findViewById(R.id.longitude);
//        weather = (TextView) findViewById(R.id.weather);
    }


    @Override
    public void onLocationChanged(Location location) {
        /*현재 위치에서 위도경도 값을 받아온뒤 우리는 지속해서 위도 경도를 읽어올것이 아니니
        날씨 api에 위도경도 값을 넘겨주고 위치 정보 모니터링을 제거한다.*/
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        //위도 경도 텍스트뷰에 보여주기
//        latText.setText(String.valueOf(latitude));
//        lonText.setText(String.valueOf(longitude));
        //날씨 가져오기 통신
        getWeather(latitude, longitude);
        //위치정보 모니터링 제거
        locationManager.removeUpdates(Splash.this);
    }




    private void getWeather(double latitude, double longitude) {
        Retrofit retrofit =new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                .baseUrl(ApiService.BASEURL)
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        Call<JsonObject> call = apiService.getHourly(ApiService.APPKEY,1,latitude,longitude);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()){
                    //날씨데이터를 받아옴
                    JsonObject object = response.body();
                    String str = object.toString();


                    try {
                        JSONObject jsonObject = new JSONObject(str);
                        JSONObject tem = jsonObject.getJSONObject("weather");
                        JSONArray hour = tem.getJSONArray("hourly");
                        // Log.e("JSON CHECK hour", ""+hour);
                        // Log.e("JSONLENGTH", hour.length()+"");
                        JSONObject data = hour.getJSONObject(0);
                        JSONObject temp = data.getJSONObject("temperature");
                        tc = temp.getString("tc");
                        tmax = temp.getString("tmax");
                        tmin = temp.getString("tmin");
                        JSONObject sky = data.getJSONObject("sky");
                        code =  sky.getString("code");
                        name =  sky.getString("name");
                        JSONObject common = jsonObject.getJSONObject("common");
                        stormYn = common.getString("stormYn");


                        // tc = data.getString("tc");
                        //  Log.e("JSONTC", tc);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (object != null) {
                        //데이터가 null 이 아니라면 날씨 데이터를 텍스트뷰로 보여주기
                        //weather.setText("오늘 기온 :"+tc+"최고 기온 : "+tmax+"최저 기온"+tmin+""+"하늘코드 : "+code+"하늘 상태 : "+name);

                    }

                }
            }
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
            }
        });
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void requestLocation () {
        //사용자로 부터 위치정보 권한체크
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 1, this);

        }

    }
}