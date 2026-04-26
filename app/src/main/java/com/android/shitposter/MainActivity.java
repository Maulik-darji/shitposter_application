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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        WindowInsetsHelper.applyTopInsetsPadding(binding.toolbar);

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

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float xTilt = event.values[0]; // -10 to 10 approx
            updateGyroShine(xTilt);
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
            float range = 40f;
            float normalizedTilt = (xTilt + (range/2f)) / range; 
            normalizedTilt = Math.max(0, Math.min(1, 1 - normalizedTilt)); 
            
            float offset = (normalizedTilt * 200) - 100; // Small movement offset

            // Diagonal gradient for top-right and bottom-left focus
            LinearGradient gradient = new LinearGradient(
                offset, height + offset, width + offset, -offset,
                new int[]{0x00FFFFFF, 0x80FFFFFF, 0x00FFFFFF, 0x00FFFFFF, 0x00FFFFFF, 0x80FFFFFF, 0x00FFFFFF},
                new float[]{0.0f, 0.15f, 0.3f, 0.5f, 0.7f, 0.85f, 1.0f},
                Shader.TileMode.CLAMP
            );

            // 32dp corner radius roughly
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
            int index = 0;
            int count = binding.bottomNav.getMenu().size();
            if (count == 0) return;

            for (int i = 0; i < count; i++) {
                if (binding.bottomNav.getMenu().getItem(i).getItemId() == itemId) {
                    index = i;
                    break;
                }
            }

            float navWidth = binding.bottomNav.getWidth();
            float tabWidth = navWidth / count;
            float targetX = (tabWidth * index) + (tabWidth / 2f) - (binding.navIndicator.getWidth() / 2f);

            if (animate) {
                binding.navIndicator.animate()
                        .translationX(targetX)
                        .setDuration(250)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .start();
            } else {
                binding.navIndicator.setTranslationX(targetX);
            }
        });
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
