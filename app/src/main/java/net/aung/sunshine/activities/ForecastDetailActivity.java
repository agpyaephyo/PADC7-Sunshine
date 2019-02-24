package net.aung.sunshine.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import net.aung.sunshine.R;
import net.aung.sunshine.data.vos.WeatherStatusVO;
import net.aung.sunshine.fragments.ForecastDetailFragment;

/**
 * Created by aung on 2/28/16.
 */
public class ForecastDetailActivity extends BaseActivity {

    public static Intent createNewIntent(Context context, WeatherStatusVO weatherStatus) {
        Intent intentToForecastDetail = new Intent(context, ForecastDetailActivity.class);
        intentToForecastDetail.putExtra(IE_WEATHER_STATUS_DATE_TIME, weatherStatus.getDateTime());
        return intentToForecastDetail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        long weatherStatusDateTime = getIntent().getLongExtra(IE_WEATHER_STATUS_DATE_TIME, -1);

        if (savedInstanceState == null && weatherStatusDateTime != -1) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_container, ForecastDetailFragment.newInstance(weatherStatusDateTime), ForecastDetailFragment.TAG)
                    .commit();

            supportPostponeEnterTransition(); //postpone the shared transition until the data in detail screen is being loaded.
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            overridePendingTransition(R.anim.screen_pop_enter_horizontal, R.anim.screen_pop_exit_horizontal);
        }
    }
}
