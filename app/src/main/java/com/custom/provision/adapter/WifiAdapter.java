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
import com.custom.provision.entity.WifiNetwork;

import java.util.ArrayList;
import java.util.List;

public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.ViewHolder> {
    private Context context;
    private List<WifiNetwork> wifiNetworkList = new ArrayList<>();


    public WifiAdapter(Context context) {
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
       holder.bind(wifiNetworkList.get(position),position);
    }

    @Override
    public int getItemCount() {
        return wifiNetworkList.size();
    }

    public void updateList(List<WifiNetwork> wifiNetworkList){
        this.wifiNetworkList = wifiNetworkList;
        notifyDataSetChanged();
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

        public void bind(WifiNetwork wifiNetwork, int position){
            tvLanguageName.setText(wifiNetwork.ssid);
            if (wifiNetwork.isConnected){
                itemRoot.setSelected(true);
                imgSelect.setVisibility(View.VISIBLE);
            }else {
                itemRoot.setSelected(false);
                imgSelect.setVisibility(View.GONE);
            }
        }

    }
}
