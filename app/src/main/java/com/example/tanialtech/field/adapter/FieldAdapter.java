package com.example.tanialtech.field.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tanialtech.R;
import com.example.tanialtech.field.data.FieldItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FieldAdapter extends RecyclerView.Adapter<FieldAdapter.FieldViewHolder> {

    private List<FieldItem> ladangList;
    private Context mContext;

    public FieldAdapter(Context context, List<FieldItem> ladangList){
        this.mContext = context;
        this.ladangList = ladangList;
    }



    @NonNull
    @Override
    public FieldViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.field_item,parent,false);
        return new FieldViewHolder(view);
    }

    private String formatDate(String inputDateString) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.ENGLISH);
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
        try {
            Date date = inputDateFormat.parse(inputDateString);
            return outputDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return inputDateString; // Return the original string in case of error
        }
    }

    @Override
    public void onBindViewHolder(@NonNull FieldViewHolder holder, int position) {
        FieldItem field = ladangList.get(position);
        holder.namaLadang.setText(field.getNamaLadang());
        holder.kodeLadang.setText(field.getKodeLadang());
        holder.luasLadang.setText(field.getLuasLadang());
        holder.perkiraanMasaTanam.setText(formatDate(field.getPerkiraanMasaTanam()));

        Glide.with(holder.imageLadang.getContext())
                .load("https://api-simdoks.simdoks.web.id/" + field.getImageResource())
                .placeholder(R.drawable.pic_1)
                .error(R.drawable.pic_1)
                .into(holder.imageLadang);
    }

    @Override
    public int getItemCount() {
        return ladangList.size();
    }

    public void setFieldData(List<FieldItem> newLadangList) {
        this.ladangList = newLadangList;
        notifyDataSetChanged();
    }

    public class FieldViewHolder extends RecyclerView.ViewHolder {
        ImageView imageLadang;
        TextView namaLadang, kodeLadang, luasLadang, perkiraanMasaTanam;
        public FieldViewHolder(@NonNull View itemView) {
            super(itemView);
            imageLadang = itemView.findViewById(R.id.image_ladang);
            namaLadang = itemView.findViewById(R.id.nama_ladang);
            kodeLadang = itemView.findViewById(R.id.kode_ladang);
            luasLadang = itemView.findViewById(R.id.luas_ladang);
            perkiraanMasaTanam = itemView.findViewById(R.id.perkiraan_masa_tanam);
        }
    }
}
