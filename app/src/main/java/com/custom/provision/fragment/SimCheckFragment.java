package com.custom.provision.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.custom.provision.Operation;
import com.custom.provision.R;
import com.custom.provision.WelComeActivity;
import com.custom.provision.databinding.SimcheckFragmentBinding;
import com.custom.provision.manager.WifiInstance;
import com.custom.provision.utils.SimUtils;

public class SimCheckFragment extends BaseFragment implements View.OnClickListener {
    SimcheckFragmentBinding binding;
    static final int REQUEST_CODE_SIM_STATE = 1001;

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
        checkPermission();
    }

    private void showSimState(int state) {
        if (state == 0) {
            //检车中
            binding.tvSimCheck.setText(getString(R.string.sim_checking));
            binding.tvSimUncheckedTip.setVisibility(View.GONE);
            binding.tvSimCheck.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.sim_check_icon, 0, 0);
        } else if (state == 1) {
            //无sim卡，有wifi连接
            binding.tvSimCheck.setText(getString(R.string.sim_unchecked));
            binding.tvSimUncheckedTip.setText(getString(R.string.sim_unchecked_wifi_available_tips));
            binding.tvSimUncheckedTip.setVisibility(View.VISIBLE);
            binding.tvSimCheck.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.sim_uncheck_icon, 0, 0);
        } else if (state ==2){
            //无sim卡，且无wifi连接
            binding.tvSimCheck.setText(getString(R.string.sim_unchecked));
            binding.tvSimUncheckedTip.setText(getString(R.string.sim_uncheck_no_wifi_tips));
            binding.tvSimUncheckedTip.setVisibility(View.VISIBLE);
            binding.tvSimCheck.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.sim_uncheck_icon, 0, 0);
        }  else if (state == 3) {
            //有sim卡，且网络可用
            binding.tvSimCheck.setText(getString(R.string.sim_checked));
            binding.tvSimUncheckedTip.setVisibility(View.GONE);
            binding.tvSimCheck.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.sim_check_icon, 0, 0);
        }  else if (state ==4){
            //有sim卡，但是sim数据网路不可用
            binding.tvSimCheck.setText(getString(R.string.sim_check_net_unavailable));
            binding.tvSimUncheckedTip.setVisibility(View.GONE);
            binding.tvSimCheck.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.sim_check_icon, 0, 0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private boolean checkPermission() {
        Activity activity = getActivity();
        if (activity == null) return false;
        boolean checkResult = activity.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
        if (checkResult) {
            checkSim();
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE_SIM_STATE);
        }
        return checkResult;
    }

    private void checkSim() {
        showSimState(0);
        boolean hasSimCard = SimUtils.hasSimCard(getContext());
        showSimState(hasSimCard ? 1 : 2);
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
            if (WifiInstance.getInstance().getConnectedWifiInfo()==null){
                if (!SimUtils.hasSimCard(getContext())){

                }
            }
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
                if (state == TelephonyManager.SIM_STATE_UNKNOWN) {

                } else if (state == TelephonyManager.SIM_STATE_ABSENT) {
                    Log.d(TAG, "onReceive: 设备未插入sim卡");
                } else if (state == TelephonyManager.SIM_STATE_PIN_REQUIRED) {
                    Log.d(TAG, "onReceive: sim卡需要PIN码");
                } else if (state == TelephonyManager.SIM_STATE_PUK_REQUIRED) {
                    Log.d(TAG, "onReceive: sim卡需要PUK码");
                } else if (state == TelephonyManager.SIM_STATE_NETWORK_LOCKED) {
                    Log.d(TAG, "onReceive:  SIM 卡被网络锁锁定（需要网络 PIN 码）");
                } else if (state == TelephonyManager.SIM_STATE_READY) {
                    Log.d(TAG, "onReceive: sim卡已准备好");
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_SIM_STATE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult:  权限赋予");
                checkSim();
            } else {
                Log.d(TAG, "onRequestPermissionsResult:  权限被拒绝");
                Toast.makeText(getContext(), "需要权限才能检查SIM卡状态", Toast.LENGTH_SHORT);
            }
        }
    }
}
