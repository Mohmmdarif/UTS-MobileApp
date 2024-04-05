package com.example.tanialtech.field.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tanialtech.R;
import com.example.tanialtech.field.data.FieldItem;

import java.util.List;

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

    @Override
    public void onBindViewHolder(@NonNull FieldViewHolder holder, int position) {
        FieldItem field = ladangList.get(position);
        holder.imageLadang.setImageResource(field.getImageResource());
        holder.namaLadang.setText(field.getNamaLadang());
        holder.kodeLadang.setText(field.getKodeLadang());
        holder.luasLadang.setText(field.getLuasLadang());
        holder.perkiraanMasaTanam.setText(field.getPerkiraanMasaTanam());
    }

    @Override
    public int getItemCount() {
        return ladangList.size();
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
