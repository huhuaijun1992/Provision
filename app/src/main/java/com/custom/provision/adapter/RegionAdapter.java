package com.custom.provision.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.custom.provision.R;
import com.custom.provision.entity.LanguageOption;
import com.custom.provision.utils.LanguageUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class RegionAdapter extends RecyclerView.Adapter<RegionAdapter.ViewHolder> {
    private Context context;
    List<Map.Entry<String, Locale>> usefulLocales = LanguageUtils.getRegion();


    public RegionAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(usefulLocales.get(position).getValue(), position);
    }

    @Override
    public int getItemCount() {
        return usefulLocales.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View itemRoot;
        TextView tvLanguageName;
        ImageView imgSelect;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemRoot = itemView.findViewById(R.id.item_root);
            tvLanguageName = itemView.findViewById(R.id.tv_language_name);
            imgSelect = itemView.findViewById(R.id.img_select);
        }

        public void bind(Locale locale, int position) {
            tvLanguageName.setText(locale.getDisplayCountry(Locale.getDefault()));
        }

    }
}
