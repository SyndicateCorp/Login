package com.syndicate.login;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CuacaAdapter extends RecyclerView.Adapter<CuacaViewHolder> {
    private Activity _activity;
    private List<CuacaListModel> listModelList;
    private CuacaRootModel rm;

    public CuacaAdapter(Activity activity, CuacaRootModel rm) {
        this._activity = activity;
        this.rm = rm;

        listModelList = rm.getListModelList();
    }

    @NonNull
    @Override
    public CuacaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_cuaca, parent, false);
        return new CuacaViewHolder(view);
    }

    private double toCelcius(double kelvin) {return kelvin - 272.15;}

    public String formatNumber(double number, String format) {
        DecimalFormat decimalFormat = new DecimalFormat(format);
        return decimalFormat.format(number);
    }

    @Override
    public void onBindViewHolder(@NonNull CuacaViewHolder holder, int position) {
        CuacaListModel lm = listModelList.get(position);
        CuacaWeatherModel wm = lm.getWeatherModelList().get(0);
        CuacaMainModel mm = lm.getMainModel();

        String suhu = formatNumber(toCelcius(mm.getTemp_min()), "###.###") + "°C" +
                formatNumber(toCelcius(mm.getTemp_max()), "###.###") + "°C";

        String iconurl = "https://openweathermap.org/img/wn/" + wm.getIcon() + "@2x.png";
        Picasso.with(_activity).load(iconurl).into(holder.cuacaImageView);

        String tanggalWaktuWib = formatWib(lm.getDt_txt());

        holder.namaTextView.setText(wm.getMain());
        holder.deskripsiTextView.setText(wm.getDescription());
        holder.tglWaktuTextView.setText(tanggalWaktuWib);
        holder.suhuTextView.setText(suhu);
    }

    @Override
    public int getItemCount() {
        return (listModelList != null) ? listModelList.size() : 0;
    }

    private String formatWib(String tanggalWaktuGMT_string){
        Log.d("*tw*", "Waktu GMT : " + tanggalWaktuGMT_string);

        Date tanggalwaktuGMT = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            tanggalwaktuGMT = sdf.parse(tanggalWaktuGMT_string);
        }
        catch (ParseException e){
            Log.e("*tw*", e.getMessage());
        }

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(tanggalwaktuGMT);
        calendar.add(Calendar.HOUR_OF_DAY, 7);

        Date tanggalWaktuWIB = calendar.getTime();

        String tanggalWaktuWIB_string = sdf.format(tanggalWaktuWIB);
        tanggalWaktuWIB_string = tanggalWaktuGMT_string.replace("00:00", "00 WIB");

        Log.d("*tw*", "Tanggal Waktu Indonesia Barat: " + tanggalWaktuWIB_string);
        return tanggalWaktuWIB_string;
    }
}
