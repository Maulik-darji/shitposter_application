package com.android.shitposter;

import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.shitposter.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private ActivityMainBinding binding;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private static final float INDICATOR_WIDTH_RATIO = 1.15f;
    private static final float NAV_CONTENT_OFFSET_DP = 6f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Indicator must never occlude nav items/touches.
        binding.navIndicator.setClickable(false);
        binding.navIndicator.setFocusable(false);
        binding.navIndicator.setElevation(-1f);
        binding.navIndicator.setTranslationZ(-1f);
        binding.navShine.setElevation(-2f);
        binding.navShine.setTranslationZ(-2f);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        WindowInsetsHelper.applyTopInsetsPadding(binding.toolbar);
        binding.bottomNav.post(this::applyBottomNavContentOffset);

        if (savedInstanceState == null) {
            openRootFragment(new HomeFragment());
            binding.bottomNav.setSelectedItemId(R.id.nav_home);
            updateIndicatorX(R.id.nav_home, false);
        }

        binding.bottomNav.setOnItemSelectedListener(item -> {
            // Restore vibration / haptic feedback
            binding.bottomNav.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP);
            
            int id = item.getItemId();
            updateIndicatorX(id, true);
            
            if (id == R.id.nav_home) {
                openRootFragment(new HomeFragment());
                return true;
            } else if (id == R.id.nav_search) {
                openRootFragment(PlaceholderFragment.newInstance(getString(R.string.nav_search)));
                return true;
            } else if (id == R.id.nav_ai) {
                openRootFragment(PlaceholderFragment.newInstance(getString(R.string.nav_ai)));
                return true;
            } else if (id == R.id.nav_notifications) {
                openRootFragment(PlaceholderFragment.newInstance(getString(R.string.nav_notifications)));
                return true;
            } else if (id == R.id.nav_chat) {
                openRootFragment(PlaceholderFragment.newInstance(getString(R.string.nav_chat)));
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
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

            // Apply Low-Pass Filter: heavily dampens high-frequency jitter/shake
            // An alpha of 0.1 gives a smooth, delayed, "heavy liquid" feel.
            float alpha = 0.1f;
            smoothedXTilt = smoothedXTilt + alpha * (rawXTilt - smoothedXTilt);

            updateGyroShine(smoothedXTilt);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void updateGyroShine(float xTilt) {
        binding.navShine.post(() -> {
            float width = binding.navShine.getWidth();
            float height = binding.navShine.getHeight();
            if (width == 0) return;

            // Map tilt to a wider, smoother range
            // Set range to 14f (-7 to +7 m/s^2) for true physical limits of phone tilt
            float range = 14f;
            float normalizedTilt = (xTilt + (range/2f)) / range; 
            normalizedTilt = Math.max(0, Math.min(1, 1 - normalizedTilt)); 

            // Lock gradient strictly to view bounds for perfect shader math
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

            // 32dp corner radius matches the perfect container geometry
            float radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, getResources().getDisplayMetrics());
            float[] outerR = new float[]{radius, radius, radius, radius, radius, radius, radius, radius};
            
            ShapeDrawable shape = new ShapeDrawable(new RoundRectShape(outerR, null, null));
            shape.getPaint().setShader(gradient);
            shape.getPaint().setStyle(Paint.Style.STROKE);
            shape.getPaint().setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
            
            binding.navShine.setBackground(shape);
        });
    }

    private void updateIndicatorX(int itemId, boolean animate) {
        binding.bottomNav.post(() -> {
            int index = -1;
            int count = binding.bottomNav.getMenu().size();
            for (int i = 0; i < count; i++) {
                if (binding.bottomNav.getMenu().getItem(i).getItemId() == itemId) {
                    index = i;
                    break;
                }
            }
            if (index == -1) return;

            android.view.ViewGroup menuView = binding.bottomNav.getMenuViewGroup();
            if (menuView == null) return;

            android.view.View itemView = menuView.getChildAt(index);
            if (itemView == null) return;

            android.view.View iconView = itemView.findViewById(com.google.android.material.R.id.navigation_bar_item_icon_view);

            // Increase indicator width slightly (15% more than item) for a bold but stable look
            int itemWidth = itemView.getWidth();
            int indicatorWidth = (int) (itemWidth * 1.15f);

            android.view.ViewGroup.LayoutParams params = binding.navIndicator.getLayoutParams();
            if (params.width != indicatorWidth) {
                params.width = indicatorWidth;
                binding.navIndicator.setLayoutParams(params);
            }

            int[] containerLoc = new int[2];
            binding.bottomNavContainer.getLocationOnScreen(containerLoc);

            int[] anchorLoc = new int[2];
            // Compensate translation so the wider indicator remains perfectly centered
            float centeringOffset = (indicatorWidth - itemWidth) / 2f;
            android.view.View anchorView = (iconView != null) ? iconView : itemView;
            anchorView.getLocationOnScreen(anchorLoc);

            float anchorCenterXOnScreen = anchorLoc[0] + (anchorView.getWidth() / 2f);
            float desiredIndicatorLeftInContainer = anchorCenterXOnScreen - containerLoc[0] - (indicatorWidth / 2f);
            float targetTranslationX = desiredIndicatorLeftInContainer - binding.navIndicator.getLeft();

            float anchorCenterYOnScreen = anchorLoc[1] + (anchorView.getHeight() / 2f);
            float indicatorDownShiftPx = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    1f,
                    getResources().getDisplayMetrics()
            );
            float desiredIndicatorTopInContainer =
                    (anchorCenterYOnScreen - containerLoc[1]) - (binding.navIndicator.getHeight() / 2f) + indicatorDownShiftPx;
            float targetTranslationY = desiredIndicatorTopInContainer - binding.navIndicator.getTop();

            if (animate) {
                binding.navIndicator.animate()
                        .translationX(targetTranslationX)
                        .translationY(targetTranslationY)
                        .setDuration(250)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .start();
            } else {
                binding.navIndicator.setTranslationX(targetTranslationX);
                binding.navIndicator.setTranslationY(targetTranslationY);
            }
        });
    }

    private void applyBottomNavContentOffset() {
        android.view.ViewGroup menuView = binding.bottomNav.getMenuViewGroup();
        if (menuView == null) return;

        float offsetPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                NAV_CONTENT_OFFSET_DP,
                getResources().getDisplayMetrics()
        );
        menuView.setTranslationY(offsetPx);
    }

    private void openRootFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainContent, fragment)
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
