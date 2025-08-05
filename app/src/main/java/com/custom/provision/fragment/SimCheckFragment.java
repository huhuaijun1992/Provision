package com.custom.provision.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.custom.provision.Operation;
import com.custom.provision.R;
import com.custom.provision.WelComeActivity;
import com.custom.provision.databinding.SimcheckFragmentBinding;

public class SimCheckFragment extends BaseFragment implements View.OnClickListener{
    SimcheckFragmentBinding binding;
    @Override
    public View getContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        binding = SimcheckFragmentBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void initView() {

    }

    @Override
    public void initListeners() {
        binding.bottomView.tvNext.setOnClickListener(this::onClick);
        binding.bottomView.tvBack.setOnClickListener(this::onClick);
        binding.bottomView.tvSkip.setOnClickListener(this::onClick);


    }

    @Override
    public void initData() {

    }

    public static SimCheckFragment newInstance(Bundle args) {

        SimCheckFragment fragment = new SimCheckFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onClick(View view) {
        WelComeActivity activity = (WelComeActivity) getActivity();
        int id = view.getId();
        if (id == R.id.tv_next){
            activity.finishSetup();

        }else if (id == R.id.tv_skip){
            activity.finishSetup();

        }else if (id == R.id.tv_back){
            // TODO: 2025/8/6 根据状态处理，暂时先返回
            activity.showFragment(Operation.wifySetting);
        }

    }
}
