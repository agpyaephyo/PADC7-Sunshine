package net.aung.sunshine.viewholders;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import net.aung.sunshine.R;
import net.aung.sunshine.controllers.ForecastListScreenController;
import net.aung.sunshine.data.vos.WeatherStatusVO;
import net.aung.sunshine.databinding.ListItemForecastBinding;
import net.aung.sunshine.utils.SettingsUtils;
import net.aung.sunshine.utils.WeatherDataUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by aung on 12/10/15.
 */
public class DailyWeatherViewHolder extends WeatherViewHolder
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private WeatherStatusVO mStatus;
    private ListItemForecastBinding binding;


    public DailyWeatherViewHolder(View itemView, ForecastListScreenController controller, WeatherViewHolderController weatherVHController) {
        super(itemView, controller, weatherVHController);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(this);

        binding = DataBindingUtil.bind(itemView);

        PreferenceManager.getDefaultSharedPreferences(itemView.getContext())
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void finalize() throws Throwable {
        if (itemView != null) {
            PreferenceManager.getDefaultSharedPreferences(itemView.getContext())
                    .unregisterOnSharedPreferenceChangeListener(this);
        }
        super.finalize();
    }

    @Override
    public void bind(WeatherStatusVO status, int selectedRow) {
        super.bind(status, selectedRow);
        binding.setWeatherStatus(status);
        this.mStatus = status;

        setIconForWeather(status);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Context context = itemView.getContext();
        if (key.equals(context.getString(R.string.pref_icon_key))) {
            setIconForWeather(mStatus);
        }
    }

    private void setIconForWeather(WeatherStatusVO status) {
        if(SettingsUtils.retrieveIconPackPref() == SettingsUtils.ICON_PACK_UDACITY) {
            Glide.with(ivStatusArt.getContext())
                    .load(WeatherDataUtils.getIconUrlForWeatherCondition(status.getWeather().getId()))
                    .error(WeatherDataUtils.getIconResourceForWeatherCondition(status.getWeather().getId()))
                    .into(ivStatusArt);
        } else {
            int weatherIconResourceId = WeatherDataUtils.getArtResourceForWeatherCondition(status.getWeather().getId());
            ivStatusArt.setImageResource(weatherIconResourceId);
        }
    }
}
