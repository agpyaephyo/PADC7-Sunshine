package net.aung.sunshine.viewholders;

import android.view.View;

import net.aung.sunshine.R;
import net.aung.sunshine.controllers.ForecastListScreenController;
import net.aung.sunshine.data.vos.WeatherStatusVO;
import net.aung.sunshine.views.ViewPodWeatherInfo;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by aung on 12/13/15.
 */
public class TodayWeatherViewHolder extends WeatherViewHolder {

    @Bind(R.id.vp_weather_info)
    View vpWeatherInfoView;

    private ViewPodWeatherInfo vpWeatherInfo;

    public TodayWeatherViewHolder(View itemView, ForecastListScreenController controller, WeatherViewHolderController weatherVHController) {
        super(itemView, controller, weatherVHController);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(this);

        vpWeatherInfo = new ViewPodWeatherInfo(vpWeatherInfoView);
    }

    @Override
    public void bind(WeatherStatusVO status, int selectedRow) {
        super.bind(status, selectedRow);
        vpWeatherInfo.bind(status);
    }
}
