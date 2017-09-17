package com.example.garbage;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

/**
 * The configuration screen for the {@link GarbageCustomizableWidget GarbageCustomizableWidget} AppWidget.
 */
public class GarbageCustomizableWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "com.example.garbage.GarbageCustomizableWidget";
    private static final String WIDGET_TEXT = "widget_text_";
    private static final String WIDGET_COLOR = "widget_color_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    EditText mAppWidgetText;
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            int selectedRadioButton = ((RadioGroup) findViewById(R.id.rgColor)).getCheckedRadioButtonId();
            int widgetColor;
            switch (selectedRadioButton) {
                case R.id.rbGreen:
                    widgetColor = Color.GREEN;
                    break;
                case R.id.rbBlue:
                    widgetColor = Color.BLUE;
                    break;
                default:
                    widgetColor = Color.RED;
                    break;
            }

            final Context context = GarbageCustomizableWidgetConfigureActivity.this;

            // When the button is clicked, store the string locally
            String widgetText = mAppWidgetText.getText().toString();
            saveWidgetPref(context, mAppWidgetId, widgetText, widgetColor);

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            GarbageCustomizableWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    public GarbageCustomizableWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveWidgetPref(Context context, int appWidgetId, String text, int color) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(WIDGET_TEXT + appWidgetId, text);
        prefs.putInt(WIDGET_COLOR + appWidgetId, color);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(WIDGET_TEXT + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_text);
        }
    }

    static int loadColorPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getInt(WIDGET_COLOR + appWidgetId, Color.RED);
    }

    static void deleteWidgetPref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(WIDGET_TEXT + appWidgetId);
        prefs.remove(WIDGET_COLOR + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.garbage_customizable_widget_configure);
        mAppWidgetText = (EditText) findViewById(R.id.garbage_customizable_configure_text);
        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        mAppWidgetText.setText(loadTitlePref(GarbageCustomizableWidgetConfigureActivity.this, mAppWidgetId));

        RadioGroup radioGroup = ((RadioGroup) findViewById(R.id.rgColor));
        radioGroup.check(R.id.rbGreen);
    }
}

