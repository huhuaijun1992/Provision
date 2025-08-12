package com.custom.provision.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.internal.telephony.TelephonyIntents;
import com.custom.provision.Operation;
import com.custom.provision.R;
import com.custom.provision.WelComeActivity;
import com.custom.provision.databinding.SimcheckFragmentBinding;
import com.custom.provision.utils.SimUtils;

public class SimCheckFragment extends BaseFragment implements View.OnClickListener {
    SimcheckFragmentBinding binding;

    @Override
    public View getContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        binding = SimcheckFragmentBinding.inflate(inflater, container, false);
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
        boolean hasSimCard = SimUtils.hasSimCard(getContext());
        if (hasSimCard) {
            binding.tvSimCheck.setText("已检测到SIM卡");
        }
    }

    private void showSimState(boolean b){
        if (b){
            binding.tvSimCheck.setText("已检测到SIM卡");
        }
    }

    public static SimCheckFragment newInstance(Bundle args) {

        SimCheckFragment fragment = new SimCheckFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onClick(View view) {
        WelComeActivity activity = (WelComeActivity) getActivity();
        if (activity == null) return;
        int id = view.getId();
        if (id == R.id.tv_next) {
            activity.finishSetup();

        } else if (id == R.id.tv_skip) {
            activity.finishSetup();

        } else if (id == R.id.tv_back) {
            // TODO: 2025/8/6 根据状态处理，暂时先返回
            activity.showFragment(Operation.wifySetting);
        }

    }

    static class SimStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(TelephonyManager.ACTION_SIM_CARD_STATE_CHANGED)) {
                int state = intent.getIntExtra(TelephonyManager.EXTRA_SIM_STATE, -1);
                Log.d(TAG, "SIM 状态变化: " + state);
                if (state == TelephonyManager.SIM_STATE_UNKNOWN){

                } else if (state == TelephonyManager.SIM_STATE_ABSENT){
                    Log.d(TAG, "onReceive: 设备未插入sim卡");
                }else if (state == TelephonyManager.SIM_STATE_PIN_REQUIRED){
                    Log.d(TAG, "onReceive: sim卡需要PIN码");
                }else if (state == TelephonyManager.SIM_STATE_PUK_REQUIRED){
                    Log.d(TAG, "onReceive: sim卡需要PUK码");
                }else if (state == TelephonyManager.SIM_STATE_NETWORK_LOCKED){
                    Log.d(TAG, "onReceive:  SIM 卡被网络锁锁定（需要网络 PIN 码）");
                }else if (state == TelephonyManager.SIM_STATE_READY){
                    Log.d(TAG, "onReceive: sim卡已准备好");
                }
            }
        }
    }
}
