/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.aung.sunshine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.WindowInsets;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Digital watch face with seconds. In ambient mode, the seconds aren't displayed. On devices with
 * low-bit ambient mode, the text is drawn without anti-aliasing in ambient mode.
 */
public class SunshineWatchFace extends CanvasWatchFaceService {
    private static final Typeface BOLD_TYPEFACE =
            Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
    private static final Typeface NORMAL_TYPEFACE =
            Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);

    private static final String DATE_FORMAT = "EEEE, MMMM dd";
    private static SimpleDateFormat mDayOfWeekFormat = new SimpleDateFormat(DATE_FORMAT);

    /**
     * Update rate in milliseconds for interactive mode. We update once a second since seconds are
     * displayed in interactive mode.
     */
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);

    /**
     * Handler message id for updating the time periodically in interactive mode.
     */
    private static final int MSG_UPDATE_TIME = 0;

    public static final String TAG = "SunshineWatchFace";

    private static final int ICON_SIZE = 75;
    private static final int ICON_SIZE_SQUARE = 70;
    private static final int BG_SIZE = 320;

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private static class EngineHandler extends Handler {
        private final WeakReference<SunshineWatchFace.Engine> mWeakReference;

        public EngineHandler(SunshineWatchFace.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            SunshineWatchFace.Engine engine = mWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }

    private class Engine extends CanvasWatchFaceService.Engine implements DataApi.DataListener,
            GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener,
            ResultCallback<DataItemBuffer> {
        final Handler mUpdateTimeHandler = new EngineHandler(this);
        boolean mRegisteredTimeZoneReceiver = false;
        Paint mBackgroundPaint;
        Paint mTextPaintHour;
        Paint mTextPaintMinute;

        Paint mTextPaintRegular;
        Paint mTextPaintGreySmall;
        Paint mTextPaintGreyRegular;

        Date mDate;
        float mLineHeight;
        float mLineOffset;
        float mLineOffsetY;
        float mMargin8Dp;
        float mMargin4Dp;
        float mMargin24Dp;

        boolean mAmbient;
        Time mTime;
        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mTime.clear(intent.getStringExtra("time-zone"));
                mTime.setToNow();
            }
        };
        int mTapCount;

        float mXOffset;
        float mYOffset;

        String minTemperature;
        String maxTemperature;
        Bitmap weatherBitmap;
        String locale;
        int weatherId;
        String weatherDescription;
        Bitmap backgroundImage;

        /**
         * Whether the display supports fewer bits for each color in ambient mode. When true, we
         * disable anti-aliasing in ambient mode.
         */
        boolean mLowBitAmbient;
        boolean mRound;
        GoogleApiClient mGoogleApiClient;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(SunshineWatchFace.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_VARIABLE)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .setAcceptsTapEvents(true)
                    .build());
            Resources resources = SunshineWatchFace.this.getResources();
            mYOffset = resources.getDimension(R.dimen.digital_y_offset);
            mLineHeight = resources.getDimension(R.dimen.digital_line_height);
            mLineOffset = resources.getDimension(R.dimen.digital_line_offset);
            mLineOffsetY = resources.getDimension(R.dimen.digital_line_offset_y);
            mMargin8Dp = resources.getDimension(R.dimen.margin_8dp);
            mMargin4Dp = resources.getDimension(R.dimen.margin_4dp);
            mMargin24Dp = resources.getDimension(R.dimen.margin_24dp);

            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(resources.getColor(R.color.black_overlay));

            mTextPaintHour = createHourTextPaint(resources.getColor(R.color.digital_text),
                    resources.getDimension(R.dimen.text_hour));

            mTextPaintMinute = createMinuteTextPaint(resources.getColor(R.color.digital_text),
                    resources.getDimension(R.dimen.text_minute_info));

            mTextPaintRegular = createMinuteTextPaint(resources.getColor(R.color.digital_text),
                    resources.getDimension(R.dimen.text_regular));

            mTextPaintGreySmall = createMinuteTextPaint(resources.getColor(R.color.date_on_watch),
                    resources.getDimension(R.dimen.text_small));

            mTextPaintGreyRegular = createMinuteTextPaint(resources.getColor(R.color.date_on_watch),
                    resources.getDimension(R.dimen.text_regular));

            mDate = new Date();
            mTime = new Time();

            mGoogleApiClient = new GoogleApiClient.Builder(SunshineWatchFace.this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Wearable.API)
                    .build();

            Log.d(TAG, "connect mGoogleApiClient XXX");
            mGoogleApiClient.connect();
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();
        }

        private Paint createHourTextPaint(int textColor, float textSize) {
            Paint paint = createTextPaint(textColor, textSize);
            paint.setTypeface(BOLD_TYPEFACE);
            return paint;
        }

        private Paint createMinuteTextPaint(int textColor, float textSize) {
            Paint paint = createTextPaint(textColor, textSize);
            paint.setTypeface(NORMAL_TYPEFACE);
            return paint;
        }

        private Paint createTextPaint(int textColor, float textSize) {
            Paint paint = new Paint();
            paint.setColor(textColor);
            paint.setTextSize(textSize);
            paint.setAntiAlias(true);
            return paint;
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                //Log.d(TAG, "connect mGoogleApiClient");
                //mGoogleApiClient.connect();

                registerReceiver();

                // Update time zone in case it changed while we weren't visible.
                mTime.clear(TimeZone.getDefault().getID());
                mTime.setToNow();
            } else {
                unregisterReceiver();

                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    //Wearable.DataApi.removeListener(mGoogleApiClient, this);
                    //mGoogleApiClient.disconnect();
                    //Log.d(TAG, "disconnect mGoogleApiClient");
                }
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            SunshineWatchFace.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            SunshineWatchFace.this.unregisterReceiver(mTimeZoneReceiver);
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);

            // Load resources that have alternate values for round watches.
            Resources resources = SunshineWatchFace.this.getResources();
            mRound = insets.isRound();

            mXOffset = resources.getDimension(mRound
                    ? R.dimen.digital_x_offset_round : R.dimen.digital_x_offset);
            mYOffset = resources.getDimension(mRound
                    ? R.dimen.digital_y_offset_round : R.dimen.digital_y_offset);
            float textSize = resources.getDimension(mRound
                    ? R.dimen.text_regular : R.dimen.text_regular_square);

            mTextPaintRegular.setTextSize(textSize);
            mTextPaintGreyRegular.setTextSize(textSize);
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            if (mAmbient != inAmbientMode) {
                mAmbient = inAmbientMode;
                if (mLowBitAmbient) {
                    mTextPaintHour.setAntiAlias(!inAmbientMode);
                }
                invalidate();
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        /**
         * Captures tap event (and tap type) and toggles the background color if the user finishes
         * a tap.
         */
        @Override
        public void onTapCommand(int tapType, int x, int y, long eventTime) {
            Resources resources = SunshineWatchFace.this.getResources();
            switch (tapType) {
                case TAP_TYPE_TOUCH:
                    // The user has started touching the screen.
                    break;
                case TAP_TYPE_TOUCH_CANCEL:
                    // The user has started a different gesture or otherwise cancelled the tap.
                    break;
                case TAP_TYPE_TAP:
                    // The user has completed the tap gesture.
                    mTapCount++;
                    /*
                    mBackgroundPaint.setColor(resources.getColor(mTapCount % 2 == 0 ?
                            R.color.background : R.color.background2));
                            */
                    break;
            }
            invalidate();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            if(TextUtils.isEmpty(locale)) {
                return;
            }

            // Draw the background.
            if (isInAmbientMode()) {
                canvas.drawColor(Color.BLACK);
            } else {
                if(backgroundImage != null) {
                    Log.d(TAG, "draw background image");
                    canvas.drawBitmap(backgroundImage, 0, 0, mBackgroundPaint);
                } else {
                    //canvas.drawRect(0, 0, bounds.width(), bounds.height(), mBackgroundPaint);
                }
            }

            // Draw H:MM in ambient mode or H:MM:SS in interactive mode.
            mTime.setToNow();

            long now = System.currentTimeMillis();
            mDate.setTime(now);

            /*
            String text = mAmbient
                    ? String.format("%d:%02d", mTime.hour, mTime.minute)
                    : String.format("%d:%02d:%02d", mTime.hour, mTime.minute, mTime.second);

            String text = String.format("%d:%02d", mTime.hour, mTime.minute);
            canvas.drawText(text, bounds.centerX() - (mTextPaintHour.measureText(text) / 2),
                    mYOffset, mTextPaintRegular);
                    */

            float x = mXOffset;
            float y = mYOffset;
            String hourString = String.format("%d", mTime.hour);
            hourString = getNumberWithLocale(hourString, locale);

            String minuteString = String.format(":%02d", mTime.minute);
            minuteString = getNumberWithLocale(minuteString, locale);

            //canvas.drawText(hourString, x, mYOffset, mTextPaintHour);
            x = bounds.centerX() - ((mTextPaintHour.measureText(hourString) / 2) + (mTextPaintRegular.measureText(minuteString)) + mMargin8Dp);
            canvas.drawText(hourString, x, y, mTextPaintHour);

            x += mTextPaintHour.measureText(hourString);
            canvas.drawText(minuteString, x, y - mMargin4Dp, mTextPaintMinute);

            float bitmapX = x + mTextPaintMinute.measureText(minuteString) + (mRound ? mMargin8Dp : mMargin8Dp * 2);
            float bitmapY = y - mMargin8Dp - (mRound ? mMargin4Dp : mMargin8Dp + mMargin4Dp);

            String dateString = mDayOfWeekFormat.format(mDate).toUpperCase();
            canvas.drawText(
                    dateString,
                    mXOffset,
                    mYOffset + mLineHeight,
                    mTextPaintGreySmall);

            canvas.drawLine(bounds.centerX() - mLineOffset,
                    mYOffset + (mLineHeight + mMargin8Dp),
                    bounds.centerX() + mLineOffset,
                    mYOffset + (mLineHeight + mMargin8Dp),
                    mTextPaintGreySmall);

            if (!TextUtils.isEmpty(weatherDescription)) {
                x = bounds.centerX() - (mTextPaintRegular.measureText(weatherDescription) / 2);
                y = mYOffset + (mLineHeight + mLineOffsetY * 2) + mMargin4Dp;

                canvas.drawText(weatherDescription,
                        x,
                        y,
                        mTextPaintRegular);
            }

            if (!TextUtils.isEmpty(minTemperature) && !TextUtils.isEmpty(maxTemperature)) {
                x = bounds.centerX() - ((mTextPaintRegular.measureText(maxTemperature) / 2) + (mTextPaintGreyRegular.measureText(minTemperature) / 2));
                y = mYOffset + (mLineHeight + mLineOffsetY * 3) + mMargin24Dp;

                canvas.drawText(maxTemperature,
                        x,
                        y,
                        mTextPaintRegular);
                x += mTextPaintRegular.measureText(maxTemperature);

                canvas.drawText(minTemperature,
                        x + mMargin8Dp,
                        y,
                        mTextPaintGreyRegular);
                x -= mTextPaintRegular.measureText(maxTemperature);

                if (weatherBitmap != null) {
                    canvas.drawBitmap(weatherBitmap,
                            bitmapX,
                            bitmapY,
                            null);
                }
            }


        }

        /**
         * Starts the {@link #mUpdateTimeHandler} timer if it should be running and isn't currently
         * or stops it if it shouldn't be running but currently is.
         */
        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        /**
         * Returns whether the {@link #mUpdateTimeHandler} timer should be running. The timer should
         * only run when we're visible and in interactive mode.
         */
        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        private void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS
                        - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
            }
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Wearable.DataApi.addListener(mGoogleApiClient, Engine.this);
            Wearable.DataApi.getDataItems(mGoogleApiClient).setResultCallback(this);
            Log.d(TAG, "mGoogleApiClient - onConnected");
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.d(TAG, "mGoogleApiClient - onConnectionSuspended");
        }

        @Override
        public void onDataChanged(DataEventBuffer dataEvents) {
            Log.d(TAG, "mGoogleApiClient - onDataChanged");
            for (DataEvent dataEvent : dataEvents) {
                if (dataEvent.getType() != DataEvent.TYPE_CHANGED) {
                    continue;
                }

                DataItem dataItem = dataEvent.getDataItem();
                processDataMapItem(dataItem);
            }
            dataEvents.release();
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.d(TAG, "mGoogleApiClient - onConnectionFailed");
        }

        @Override
        public void onResult(@NonNull DataItemBuffer dataItems) {
            Log.d(TAG, "ResultCallback<DataItemBuffer> - onResult");
            for (DataItem dataItem : dataItems) {
                processDataMapItem(dataItem);
            }

            dataItems.release();
        }

        private void processDataMapItem(DataItem dataItem) {
            if (!TextUtils.equals(dataItem.getUri().getPath(), SharedConstants.DATA_PATH)) {
                return;
            }

            DataMapItem dataMapItem = DataMapItem.fromDataItem(dataItem);
            DataMap weatherData = dataMapItem.getDataMap();
            Log.d(TAG, "weather data : " + weatherData.toString());

            minTemperature = weatherData.getString(SharedConstants.MIN_TEMPERATURE);
            maxTemperature = weatherData.getString(SharedConstants.MAX_TEMPERATURE);

            locale = weatherData.getString(SharedConstants.LOCALE);
            if (!TextUtils.isEmpty(locale)) {
                mDayOfWeekFormat = new SimpleDateFormat(DATE_FORMAT, new Locale(locale));

                forceUpdateLocale(locale);
                WeatherDataUtils.loadWeatherDescMap();
            }
            invalidate();

            weatherId = weatherData.getInt(SharedConstants.WEATHER_ID);
            if(TextUtils.equals(locale, "my")) {
                weatherDescription = SunshineWearApp.getContext().getString(R.string.format_weather_description, WeatherDataUtils.getWeatherDescription(weatherId));
            } else {
                weatherDescription = WeatherDataUtils.getWeatherDescription(weatherId);
            }

            new LoadArtResourceTask().execute(weatherId);
            new LoadBackgroundImageTask().execute(weatherId);
        }

        private class LoadArtResourceTask extends AsyncTask<Integer, Void, Bitmap> {
            @Override
            protected Bitmap doInBackground(Integer... params) {
                int weatherId = params[0];
                int artResourceId = WeatherDataUtils.getArtResourceForWeatherCondition(weatherId);
                try {
                    weatherBitmap = Glide.with(getApplicationContext())
                            .load(artResourceId)
                            .asBitmap()
                            .into(mRound ? ICON_SIZE : ICON_SIZE_SQUARE, mRound ? ICON_SIZE : ICON_SIZE_SQUARE)
                            .get();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                return weatherBitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                Log.d(TAG, "Bitmap decoding finish.");
                if (weatherBitmap != null) {
                    invalidate();
                }
            }

        }

        private class LoadBackgroundImageTask extends AsyncTask<Integer, Void, Bitmap> {
            @Override
            protected Bitmap doInBackground(Integer... params) {
                int weatherId = params[0];
                int resourceIdForBackground = WeatherDataUtils.getBackgroundResourceIdForWeatherCondition(weatherId);
                try {
                    backgroundImage = Glide.with(getApplicationContext())
                            .load(resourceIdForBackground)
                            .asBitmap()
                            .into(BG_SIZE, BG_SIZE + 80)
                            .get();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                return backgroundImage;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (backgroundImage != null) {
                    Log.d(TAG, "Background decoding finish.");
                    invalidate();
                } else {
                    Log.d(TAG, "Background decoding result null.");
                }
            }

        }
    }

    private String getNumberWithLocale(String number, String locale) {
        Log.d(TAG, "Number of format : " + number);
        if (TextUtils.equals(locale, "my")) {
            StringBuffer numberBuffer = new StringBuffer();
            for (char eachDigit : number.toCharArray()) {
                switch (eachDigit) {
                    case '1':
                        numberBuffer.append("၁");
                        break;
                    case '2':
                        numberBuffer.append("၂");
                        break;
                    case '3':
                        numberBuffer.append("၃");
                        break;
                    case '4':
                        numberBuffer.append("၄");
                        break;
                    case '5':
                        numberBuffer.append("၅");
                        break;
                    case '6':
                        numberBuffer.append("၆");
                        break;
                    case '7':
                        numberBuffer.append("၇");
                        break;
                    case '8':
                        numberBuffer.append("၈");
                        break;
                    case '9':
                        numberBuffer.append("၉ ");
                        break;
                    case '0':
                        numberBuffer.append("၀");
                        break;
                    default:
                        numberBuffer.append(eachDigit);
                        break;
                }
            }
            return numberBuffer.toString();
        }
        return number;
    }

    private void forceUpdateLocale(String language) {
        if (TextUtils.equals(language, "my")) {
            Resources res = getResources();
            // Change locale settings in the app.
            DisplayMetrics dm = res.getDisplayMetrics();
            android.content.res.Configuration conf = res.getConfiguration();
            conf.locale = new Locale("my");
            res.updateConfiguration(conf, dm);
        } else {
            Resources res = getResources();
            // Change locale settings in the app.
            DisplayMetrics dm = res.getDisplayMetrics();
            android.content.res.Configuration conf = res.getConfiguration();
            conf.locale = new Locale("en");
            res.updateConfiguration(conf, dm);
        }
    }
}
