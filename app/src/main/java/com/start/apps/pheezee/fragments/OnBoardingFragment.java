package com.start.apps.pheezee.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import start.apps.pheezee.R;
import start.apps.pheezee.databinding.FragmentOnboardingBinding;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class OnBoardingFragment extends Fragment {

    private static String ARG_POSITION = "ARG_POSITION";

    private static String[] onBoardingTitles = {"Pheezee", "Live monitoring based evidence", "Set targets", "Audio and Visual feedback"};

    private static String[] onBoardingDescriptions = {
            "India's first smart device that generates reports for all your physiotherapy sessions. It's session-wise reports and the overall reports can be shared with the patients and referring doctors.",
            "Pheezee provides range of motion and EMG biofeedback for every session. It also tracks other parameters like hold time, repetitions, active time etc.",
            "You can now set daily targets for your patients and allow them to push their limits for faster recovery.",
            "Our gamification themes will motivate your patients to complete their daily sessions. It also provides them engaging experience."
    };

    public static OnBoardingFragment getInstance(Integer position){
        OnBoardingFragment onBoardingFragment = new OnBoardingFragment();
        Bundle args = new Bundle();
        args.putInt(OnBoardingFragment.ARG_POSITION, position);
        onBoardingFragment.setArguments(args);
        return onBoardingFragment;
    }

    private FragmentOnboardingBinding fragmentOnboardingBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentOnboardingBinding = FragmentOnboardingBinding.inflate(inflater, container, false);
        return fragmentOnboardingBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Integer position = requireArguments().getInt(OnBoardingFragment.ARG_POSITION);
        fragmentOnboardingBinding.onBoardTitle.setText(OnBoardingFragment.onBoardingTitles[position]);
        fragmentOnboardingBinding.onBoardDescription.setText(OnBoardingFragment.onBoardingDescriptions[position]);
        InputStream imageStream = this.getResources().openRawResource(getOnBoardImageAssets().get(position));
        Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
        fragmentOnboardingBinding.imageViewOnBoardFragment.setImageBitmap(bitmap);


    }

    private List<Integer> getOnBoardImageAssets(){
        List<Integer> imageAssets = new ArrayList<Integer>();
        imageAssets.add(R.raw.on_boarding_screen_1);
        imageAssets.add(R.raw.on_boarding_screen_2);
        imageAssets.add(R.raw.on_boarding_screen_3);
        imageAssets.add(R.raw.on_boarding_screen_4);
        return imageAssets;
    }
}