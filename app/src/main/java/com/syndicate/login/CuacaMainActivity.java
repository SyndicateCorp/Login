package com.syndicate.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class CuacaMainActivity extends AppCompatActivity {
    private RecyclerView _recyclerView1;
    private CuacaRootModel _rootModel;
    private SwipeRefreshLayout _swipeRefreshLayout1;
    private TextView _totalTextView , _textViewCityInfo;

    @SuppressLint("MissingInflated")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuaca_main);

        _recyclerView1 = findViewById(R.id.recyclerView2);
        _totalTextView = findViewById(R.id.totalTextView);
        _textViewCityInfo = findViewById(R.id.textView_cityInfo);

        initSwipeRefreshLayout();
        bindRecyclerView1();
    }

    private void initSwipeRefreshLayout() {
        _swipeRefreshLayout1 = findViewById(R.id.swipeRefreshLayout2);
        _swipeRefreshLayout1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                bindRecyclerView1();
            }
        });
    }

    private void bindRecyclerView1() {
        _swipeRefreshLayout1.setRefreshing(true);
        String url = "http://api.openweathermap.org/data/2.5/forecast?id=1630789&appid=4a7d57248f59c47f26c4c210c5643311";
        AsyncHttpClient ahc = new AsyncHttpClient();
        ahc.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Gson gson = new Gson();
                _rootModel = gson.fromJson(new String(responseBody), CuacaRootModel.class);

                initCityInfo();

                RecyclerView.LayoutManager lm = new LinearLayoutManager(CuacaMainActivity.this);
                CuacaAdapter ca = new CuacaAdapter(CuacaMainActivity.this, _rootModel);

                _recyclerView1.setLayoutManager(lm);
                _recyclerView1.setAdapter(ca);
                _totalTextView.setText("Total Record : " + ca.getItemCount());
                _swipeRefreshLayout1.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                _swipeRefreshLayout1.setRefreshing(false);
            }
        });
    }

    private void initCityInfo() {
        CuacaCityModel cm = _rootModel.getCityModel();
        long sunrise = cm.getSunrise();
        long sunset = cm.getSunset();
        String cityName = cm.getName();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String sunriseTime = sdf.format(new Date(sunrise * 1000));
        String sunsetTime = sdf.format(new Date(sunset * 1000));

        String cityInfo = "Kota: " + cityName + "\n" +
                "Matahari Terbit: " + sunriseTime + "(Lokal)\n" +
                "Matahari Terbenam: " + sunsetTime + "(Lokal)\n";

        _textViewCityInfo.setText(cityInfo);
    }
}