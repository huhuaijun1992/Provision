package com.custom.provision.fragment;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.custom.provision.Operation;
import com.custom.provision.R;
import com.custom.provision.WelComeActivity;
import com.custom.provision.adapter.WifiAdapter;
import com.custom.provision.databinding.WifiFragmentBinding;
import com.custom.provision.entity.WifiNetwork;
import com.custom.provision.manager.WifiManager;
import com.custom.provision.utils.VerticalSpaceItemDecoration;

import java.util.List;

/**
 * Author: created by huhuaijun on 2025/8/5 17:05
 * Function:
 */
public class WifiFragment extends BaseFragment implements View.OnClickListener {
    WifiFragmentBinding binding;
    WifiAdapter adapter;


    @Override
    public void onResume() {
        super.onResume();
        WifiManager.getInstance().startScan();
    }


    @Override
    public void onPause() {
        super.onPause();
        WifiManager.getInstance().stopScan();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        WifiManager.getInstance().getWifiNetworks().removeObservers(this);
    }

    @Override
    public View getContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        binding = WifiFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void initView() {
        binding.bottomView.tvNext.setOnClickListener(this);
        binding.bottomView.tvBack.setOnClickListener(this);
        binding.bottomView.tvSkip.setOnClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rvWifi.setLayoutManager(linearLayoutManager);
        binding.rvWifi.addItemDecoration(new VerticalSpaceItemDecoration(40));
        adapter = new WifiAdapter(getContext());
        binding.rvWifi.setAdapter(adapter);
    }

    @Override
    public void initListeners() {
        WifiManager.getInstance().getWifiNetworks().observe(this, new Observer<List<WifiNetwork>>() {
            @Override
            public void onChanged(List<WifiNetwork> wifiNetworks) {
                adapter.updateList(wifiNetworks);
                binding.rvWifi.setVisibility(wifiNetworks.isEmpty() ? GONE : VISIBLE);
                binding.tvNoWifi.setVisibility(wifiNetworks.isEmpty() ? VISIBLE : GONE);
                binding.bottomView.tvNext.setVisibility(wifiNetworks.isEmpty() ? GONE : VISIBLE);
                binding.bottomView.tvSkip.setVisibility(wifiNetworks.isEmpty() ? VISIBLE : GONE);
            }
        });
    }

    @Override
    public void initData() {

    }

    public static WifiFragment newInstance() {

        Bundle args = new Bundle();

        WifiFragment fragment = new WifiFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_next) {
            ((WelComeActivity) getActivity()).showFragment(Operation.checkSim);
        } else if (id == R.id.tv_back) {
            ((WelComeActivity) getActivity()).showFragment(Operation.region);
        } else if (id == R.id.tv_skip) {
            ((WelComeActivity) getActivity()).showFragment(Operation.checkSim);
        }
    }
}
