package com.custom.provision.fragment;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.custom.provision.Operation;
import com.custom.provision.R;
import com.custom.provision.WelComeActivity;
import com.custom.provision.adapter.WifiAdapter;
import com.custom.provision.databinding.WifiFragmentBinding;
import com.custom.provision.entity.WifiNetwork;
import com.custom.provision.manager.WifiInstance;
import com.custom.provision.utils.VerticalSpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: created by huhuaijun on 2025/8/5 17:05
 * Function:
 */
public class WifiFragment extends BaseFragment implements View.OnClickListener {
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    WifiFragmentBinding binding;
    WifiAdapter adapter;
    private boolean isPasswordVisible = false;


    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        if (checkPermissionChangeWifiState()) {
            Log.d(TAG, "onPause: ");
            WifiInstance.getInstance().stopScan();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        WifiInstance.getInstance().getWifiNetworks().removeObservers(this);
    }

    @Override
    public View getContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        binding = WifiFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void initView() {

        binding.imgPasswordShow.setOnClickListener(this);
        binding.bottomView.tvNext.setOnClickListener(this);
        binding.bottomView.tvBack.setOnClickListener(this);
        binding.bottomView.tvSkip.setOnClickListener(this);
        binding.cancle.setOnClickListener(this);
        binding.connect.setOnClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rvWifi.setLayoutManager(linearLayoutManager);
        binding.rvWifi.addItemDecoration(new VerticalSpaceItemDecoration(40));
        adapter = new WifiAdapter(getContext());
        binding.rvWifi.setAdapter(adapter);
        binding.connect.setSelected(true);
    }

    @Override
    public void initListeners() {
        WifiInstance.getInstance().getWifiNetworks().observe(this, new Observer<List<WifiNetwork>>() {
            @Override
            public void onChanged(List<WifiNetwork> wifiNetworks) {
                Log.d("WifiFragment", "onChanged: " + wifiNetworks.size());
                adapter.updateList(wifiNetworks);
                binding.rvWifi.setVisibility(wifiNetworks.isEmpty() ? GONE : VISIBLE);
                binding.tvNoWifi.setVisibility(wifiNetworks.isEmpty() ? VISIBLE : GONE);
                binding.bottomView.tvNext.setVisibility(wifiNetworks.isEmpty() ? GONE : VISIBLE);
                binding.bottomView.tvSkip.setVisibility(wifiNetworks.isEmpty() ? VISIBLE : GONE);
            }
        });
        WifiInstance.getInstance().getCurrentWaitConnectWifiNetwork().observe(getViewLifecycleOwner(), new Observer<WifiNetwork>() {
            @Override
            public void onChanged(WifiNetwork wifiNetwork) {
                if (wifiNetwork != null && binding.password.getVisibility() == GONE) {
                    binding.tvWifiName.setText(getString(R.string.input_wifi_pass_tip, wifiNetwork.ssid));
                    binding.password.setVisibility(VISIBLE);
                }
            }
        });
    }

    @Override
    public void initData() {
        if (checkAndRequestPermissions()) {
//            if (!WifiInstance.getInstance().wifiEnable()) {
//                Intent panelIntent = new Intent(Settings.Panel.ACTION_WIFI);
//                getActivity().startActivity(panelIntent);
//            }else {
            WifiInstance.getInstance().startScan();
//            }

        }
    }

    private boolean checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissionsNeeded = new ArrayList<>();

            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (!permissionsNeeded.isEmpty()) {
                requestPermissions(permissionsNeeded.toArray(new String[0]), PERMISSIONS_REQUEST_CODE);
                return false;
            } else {
                return true;
            }

        }
        return false;
    }

    private boolean checkPermissionChangeWifiState() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
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
        } else if (id == R.id.img_password_show) {
            togglePasswordVisibility();
        } else if (id == R.id.cancle) {
            binding.password.setVisibility(GONE);
            binding.editPassword.setText("");
            WifiInstance.getInstance().getCurrentWaitConnectWifiNetwork().setValue(null);
        } else if (id == R.id.connect) {
            String password = binding.editPassword.getText().toString();
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(getContext(), R.string.password_empty_tip, Toast.LENGTH_SHORT).show();
            } else {
                binding.password.setVisibility(GONE);
                WifiNetwork wifiNetwork = WifiInstance.getInstance().getCurrentWaitConnectWifiNetwork().getValue();
//                 WifiInstance.getInstance().connectToWifi(wifiNetwork.getSsid(),binding.editPassword.getText().toString(),wifiNetwork.getwifiEncryptionType());
                WifiInstance.getInstance().connect(getContext(), wifiNetwork.scanResult, binding.editPassword.getText().toString());
                binding.editPassword.setText("");

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            boolean allGranted = true;
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED)
            if (allGranted) {
                // 权限已授予，初始化 WiFi 扫描
                WifiInstance.getInstance().startScan();
            } else {
                // 部分或全部权限被拒绝
                Toast.makeText(getContext(), "需要权限才能扫描 WiFi 网络", Toast.LENGTH_SHORT).show();
                // 可以在这里再次请求权限或解释为什么需要这些权限
            }
        }
    }

    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;

        // 切换输入类型
        if (isPasswordVisible) {
            // 显示明文
            binding.editPassword.setInputType(
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            // 显示密文
            binding.editPassword.setInputType(
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }

        // 将光标移动到文本末尾
        binding.editPassword.setSelection(binding.editPassword.getText().length());
    }
}
