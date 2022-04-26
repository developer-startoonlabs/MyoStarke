package com.start.apps.pheezee.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.start.apps.pheezee.fragments.OnBoardingFragment;


public class OnBoardingAdapter extends FragmentStateAdapter {

    private Integer itemsCount;

    public OnBoardingAdapter(@NonNull FragmentActivity fragmentActivity, Integer itemsCount) {
        super(fragmentActivity);
        this.itemsCount = itemsCount;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return OnBoardingFragment.getInstance(position);
    }

    @Override
    public int getItemCount() {
        return itemsCount;
    }
}