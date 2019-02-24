package net.aung.sunshine.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import net.aung.sunshine.R;
import net.aung.sunshine.data.vos.WeatherStatusVO;
import net.aung.sunshine.databinding.ViewPodWeatherHpwGridBinding;
import net.aung.sunshine.databinding.ViewPodWeatherHpwLinearBinding;
import net.aung.sunshine.utils.SettingsUtils;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by aung on 2/29/16.
 */
public class ViewPodWeatherHPW implements SharedPreferences.OnSharedPreferenceChangeListener {

    private ViewDataBinding binding;

    private View mView;
    private WeatherStatusVO mWeatherStatus;

    @Bind(R.id.lbl_humidity)
    TextView lblHumidity;

    @Bind(R.id.lbl_pressure)
    TextView lblPressure;

    @Bind(R.id.lbl_wind)
    TextView lblWind;

    public ViewPodWeatherHPW(View view) {
        binding = DataBindingUtil.bind(view);
        ButterKnife.bind(this, view);
        this.mView = view;

        PreferenceManager.getDefaultSharedPreferences(mView.getContext())
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void finalize() throws Throwable {
        if (mView != null) {
            PreferenceManager.getDefaultSharedPreferences(mView.getContext())
                    .unregisterOnSharedPreferenceChangeListener(this);
        }
        super.finalize();
    }

    public void bind(WeatherStatusVO weatherStatus) {
        this.mWeatherStatus = weatherStatus;
        if (binding instanceof ViewPodWeatherHpwGridBinding) {
            ((ViewPodWeatherHpwGridBinding) binding).setWeatherStatus(weatherStatus);
        } else if (binding instanceof ViewPodWeatherHpwLinearBinding) {
            ((ViewPodWeatherHpwLinearBinding) binding).setWeatherStatus(weatherStatus);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Context context = mView.getContext();
        if (key.equals(context.getString(R.string.pref_language_key))) {
            /*
            if (binding instanceof ViewPodWeatherHpwGridBinding) {
                ((ViewPodWeatherHpwGridBinding) binding).setWeatherStatus(mWeatherStatus);
            } else if (binding instanceof ViewPodWeatherHpwLinearBinding) {
                ((ViewPodWeatherHpwLinearBinding) binding).setWeatherStatus(mWeatherStatus);
            }
            */
            lblHumidity.setText(context.getString(R.string.lbl_humidity));
            lblPressure.setText(context.getString(R.string.lbl_pressure));
            lblWind.setText(context.getString(R.string.lbl_wind));
        }
    }
}
