package com.custom.provision.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.custom.provision.R;
import com.custom.provision.entity.LanguageOption;

import java.util.List;
import java.util.Locale;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.ViewHolder> {
    private Context context;
    private List<LanguageOption> languageOptions = LanguageOption.getSupportedLanguages();


    public LanguageAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_language_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       holder.bind(languageOptions.get(position),position);
    }

    @Override
    public int getItemCount() {
        return languageOptions.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        View itemRoot;
        TextView tvLanguageName;
        ImageView imgSelect;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemRoot = itemView.findViewById(R.id.item_root);
            tvLanguageName = itemView.findViewById(R.id.tv_language_name);
            imgSelect = itemView.findViewById(R.id.img_select);
        }

        public void bind(LanguageOption languageOption, int position){
            tvLanguageName.setText(languageOption.getLabel());
            if (Locale.getDefault().getCountry().equals(languageOption.getLocale().getCountry())){
                itemRoot.setSelected(true);
                imgSelect.setVisibility(View.VISIBLE);
            }else {
                itemRoot.setSelected(false);
                imgSelect.setVisibility(View.GONE);
            }
        }

    }
}
