package com.custom.provision.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.custom.provision.Operation;
import com.custom.provision.R;
import com.custom.provision.WelComeActivity;
import com.custom.provision.adapter.LanguageAdapter;
import com.custom.provision.adapter.RegionAdapter;
import com.custom.provision.databinding.RegionFragmentBinding;
import com.custom.provision.utils.GestureUtils;
import com.custom.provision.utils.LanguageUtils;
import com.custom.provision.utils.VerticalSpaceItemDecoration;

import java.util.List;
import java.util.Locale;

public class RegionFragment extends BaseFragment implements View.OnClickListener {
    Locale defaultLocal;
    RegionFragmentBinding binding;
    RegionAdapter adapter;
    List<Locale> list = LanguageUtils.getRegionList();


    @Override
    public View getContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        binding = RegionFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void initView() {
        adapter = new RegionAdapter(getContext(), list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rvRegion.setLayoutManager(linearLayoutManager);
        binding.rvRegion.addItemDecoration(new VerticalSpaceItemDecoration(40));
        binding.rvRegion.setAdapter(adapter);

    }

    @Override
    public void initListeners() {
        binding.bottomView.tvNext.setOnClickListener(this::onClick);
        binding.bottomView.tvBack.setOnClickListener(this::onClick);
        adapter.setOnItemClickListener(new RegionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (GestureUtils.isFastClick()) {
                    return;
                }
                Locale locale  =list.get(position);
                if(LanguageUtils.setRegion(locale.getCountry())){
                    binding.region.tvLanguageName.setText(locale.getDisplayCountry());
                }
            }
        });

    }

    @Override
    public void initData() {
        defaultLocal = Locale.getDefault();
        binding.region.itemRoot.setSelected(true);
        binding.region.tvLanguageName.setText(defaultLocal.getDisplayCountry());
        binding.region.imgSelect.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        WelComeActivity activity = (WelComeActivity) getActivity();
        int id = view.getId();
        if (id == R.id.tv_back) {
            activity.showFragment(Operation.languge);
        } else if (id == R.id.tv_next) {
            activity.showFragment(Operation.wifySetting);
        }

    }

    @Override
    public void languageChange() {

    }


    public static RegionFragment newInstance() {

        Bundle args = new Bundle();

        RegionFragment fragment = new RegionFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
