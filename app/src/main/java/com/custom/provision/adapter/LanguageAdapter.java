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
import com.custom.provision.utils.GestureUtils;

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
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout,parent,false);
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        View itemRoot;
        TextView tvLanguageName;
        ImageView imgSelect;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemRoot = itemView.findViewById(R.id.item_root);
            tvLanguageName = itemView.findViewById(R.id.tv_language_name);
            imgSelect = itemView.findViewById(R.id.img_select);
            itemRoot.setOnClickListener(this);
        }

        public void bind(LanguageOption languageOption, int position){
            tvLanguageName.setText(languageOption.getLabel());
            Log.d("LanguageAdapter", "bind: "+ Locale.getDefault().getCountry());
            if (Locale.getDefault().getCountry().equals(languageOption.getLocale().getCountry())){
                itemRoot.setSelected(true);
                imgSelect.setVisibility(View.VISIBLE);
            }else {
                itemRoot.setSelected(false);
                imgSelect.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            if (GestureUtils.isFastClick()) {
                return;
            }
//            LanguageUtils.set(languageOptions.get(getAdapterPosition()).set);
        }
    }
}
