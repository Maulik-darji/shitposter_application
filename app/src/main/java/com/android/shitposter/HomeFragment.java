package com.android.shitposter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.TypedValue;

import com.android.shitposter.databinding.FragmentHomeBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class HomeFragment extends Fragment implements SensorEventListener {

    private FragmentHomeBinding binding;
    private HomePagerAdapter pagerAdapter;
    private ActivityResultLauncher<Intent> composePostLauncher;
    private SensorManager sensorManager;
    private Sensor accelerometer;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        composePostLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        refreshFeeds();
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getContext() != null) {
            sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
            if (sensorManager != null) {
                accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            }
        }


        pagerAdapter = new HomePagerAdapter(requireActivity());
        binding.pager.setAdapter(pagerAdapter);

        new TabLayoutMediator(binding.tabs, binding.pager, (tab, position) -> {
            tab.setText(position == 0 ? R.string.tab_for_you : R.string.tab_following);
        }).attach();

        binding.fabPost.setOnClickListener(v -> composePostLauncher.launch(
                new Intent(requireContext(), ComposePostActivity.class)
        ));
    }

    private void refreshFeeds() {
        for (Fragment f : requireActivity().getSupportFragmentManager().getFragments()) {
            if (f instanceof FeedFragment) {
                ((FeedFragment) f).refresh();
            }
        }
        // Also refresh child fragments in the pager.
        for (Fragment f : getChildFragmentManager().getFragments()) {
            if (f instanceof FeedFragment) {
                ((FeedFragment) f).refresh();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    private float smoothedXTilt = 0f;

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float rawXTilt = event.values[0];
            
            // LPF (Low-Pass Filter) creates a weighted rolling average.
            // Alpha of 0.1f filters out jittery micro-movements, leaving a fluid heavy shine.
            float alpha = 0.1f;
            smoothedXTilt = smoothedXTilt + alpha * (rawXTilt - smoothedXTilt);
            
            updateFabShine(smoothedXTilt);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void updateFabShine(float xTilt) {
        if (binding == null || binding.fabShine == null) return;
        binding.fabShine.post(() -> {
            if (binding == null) return;
            float width = binding.fabShine.getWidth();
            float height = binding.fabShine.getHeight();
            if (width == 0) return;

            // Set range to 14f (-7 to +7 m/s^2) for true physical limits of phone tilt
            float range = 14f;
            float normalizedTilt = (xTilt + (range/2f)) / range; 
            normalizedTilt = Math.max(0, Math.min(1, 1 - normalizedTilt)); 

            LinearGradient gradient = new LinearGradient(
                0, height, width, 0,
                new int[]{0x00FFFFFF, 0xFFFFFFFF, 0x00FFFFFF, 0x00FFFFFF, 0x00FFFFFF, 0xFFFFFFFF, 0x00FFFFFF},
                new float[]{0.0f, 0.15f, 0.3f, 0.5f, 0.7f, 0.85f, 1.0f},
                Shader.TileMode.CLAMP
            );

            // Map physical tilt to sweep across the view using Matrix hardware translation
            float span = width * 1.5f;
            float translateX = (normalizedTilt * span) - (span * 0.25f);
            
            android.graphics.Matrix matrix = new android.graphics.Matrix();
            matrix.setTranslate(translateX, 0);
            gradient.setLocalMatrix(matrix);

            float radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, getResources().getDisplayMetrics());
            float[] outerR = new float[]{radius, radius, radius, radius, radius, radius, radius, radius};
            
            ShapeDrawable shape = new ShapeDrawable(new RoundRectShape(outerR, null, null));
            shape.getPaint().setShader(gradient);
            shape.getPaint().setStyle(Paint.Style.STROKE);
            shape.getPaint().setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
            
            binding.fabShine.setBackground(shape);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        pagerAdapter = null;
    }
}

