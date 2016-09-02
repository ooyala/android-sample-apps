package com.ooyala.sample.utils.youbora;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ooyala.sample.R;

import java.util.HashMap;
import java.util.Map;

/**
 * This Activity displays all of the configuration available for the Youbora plugin
 */
public class YouboraConfigActivity extends Activity {

    private Map<String, Object> youboraPreferences;

    private HashMap<Button, View> buttonToViewMap;
    private HashMap<View, Button> viewToButtonMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youbora_config);

        youboraPreferences = YouboraConfigManager.getYouboraConfig(getApplicationContext());

        buttonToViewMap = new HashMap<>();
        viewToButtonMap = new HashMap<>();
        createViews(youboraPreferences, null, null);

    }

    @Override
    protected void onPause() {
        super.onPause();
        YouboraConfigManager.saveYouboraConfig(getApplicationContext(), youboraPreferences);
    }

    /**
     * This method will analyze the youboraPreferences Map and programatically
     * create the necessary views to bind all the info.
     */
    private void createViews(Map<String, Object> map, Map<String, Object> parentMap, String accMapPath) {

        if (accMapPath == null) {
            accMapPath = "";
        }

        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.configViewLayout);

        int pointCount = 0;
        for (int i = 0 ; i< accMapPath.length();i++){
            if (accMapPath.charAt(i) == '.') {
                pointCount++;
            }
        }
        int margin = pointCount * 20 * (int) getResources().getDisplayMetrics().density;

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            RelativeLayout rowLayout = new RelativeLayout(getApplicationContext());
            rowLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            if (value instanceof Map) {

                createTextViewInGroup(rowLayout, accMapPath + key, margin);
                mainLayout.addView(rowLayout);

                createViews((Map) value, map, key + ".");

            } else if (value instanceof String || value instanceof Double || value instanceof Integer || value instanceof Long) {

                String str = accMapPath + key;

                //createTextViewInGroup(rowLayout, str);
                Button but = createButtonInGroup(rowLayout, str, margin);
                EditText et = createEditTextInGroup(rowLayout, value.toString());

                buttonToViewMap.put(but, et);
                viewToButtonMap.put(et, but);

                mainLayout.addView(rowLayout);

            } else if (value instanceof Boolean) {

                String str = accMapPath + key;
                Button but = createButtonInGroup(rowLayout, str, margin);
                CheckBox cb = createCheckBoxInGroup(rowLayout, (Boolean) value);

                buttonToViewMap.put(but, cb);
                viewToButtonMap.put(cb, but);

                mainLayout.addView(rowLayout);
            } else if (value == null) {
                // value null, look for the default value type
                Object defaultValue = getValueFromMap(YouboraConfigManager.getDefaultPreferences(), accMapPath + key);
                if (defaultValue instanceof Boolean) {
                    String str = accMapPath + key;
                    Button but = createButtonInGroup(rowLayout, str, margin);
                    CheckBox cb = createCheckBoxInGroup(rowLayout, false);

                    buttonToViewMap.put(but, cb);
                    viewToButtonMap.put(cb, but);
                    mainLayout.addView(rowLayout);
                } else if (defaultValue instanceof String || defaultValue instanceof Double || defaultValue instanceof Integer || defaultValue instanceof Long || defaultValue == null) {
                    String str = accMapPath + key;

                    Button but = createButtonInGroup(rowLayout, str, margin);
                    EditText et = createEditTextInGroup(rowLayout, defaultValue == null? "" : defaultValue.toString());

                    if (defaultValue == null) {
                        et.setEnabled(false);
                    }

                    buttonToViewMap.put(but, et);
                    viewToButtonMap.put(et, but);

                    mainLayout.addView(rowLayout);

                }
            }
        }

    }

    // Listeners
    private final View.OnClickListener buttonClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            Button button = (Button) v;
            View view = buttonToViewMap.get(v);
            view.setEnabled(!view.isEnabled());
            updatePreferencesFromViewState(view);
        }
    };

    private final CompoundButton.OnCheckedChangeListener checkedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            updatePreferencesFromViewState(buttonView);
        }
    };

    private void createTextViewInGroup(ViewGroup parentView, String text, int marginLeft) {

        TextView tv = new TextView(getApplicationContext());
        tv.setText(text);
        parentView.addView(tv);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tv.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.setMargins(marginLeft,0,0,0);
        tv.setLayoutParams(params);
    }

    private Button createButtonInGroup(ViewGroup parentView, String title, int marginLeft) {
        Button but = new Button(getApplicationContext());
        but.setText(title);
        parentView.addView(but);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) but.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.setMargins(marginLeft,0,0,0);
        but.setLayoutParams(params);
        but.setOnClickListener(buttonClickListener);

        return but;
    }

    private CheckBox createCheckBoxInGroup(ViewGroup parentView, boolean checked) {

        CheckBox cb = new CheckBox(getApplicationContext());
        cb.setChecked(checked);
        parentView.addView(cb);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cb.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        cb.setLayoutParams(params);
        cb.setOnCheckedChangeListener(checkedListener);

        return cb;
    }

    private EditText createEditTextInGroup(ViewGroup parentView, String text) {
        EditText et = new EditText(getApplicationContext());
        et.setText(text);
        et.setMaxLines(1);
        parentView.addView(et);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) et.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        et.setLayoutParams(params);

        et.addTextChangedListener(new TextWatcher() {

            private EditText editText;

            public TextWatcher setEditText(EditText e) {
                editText = e;
                return this;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updatePreferencesFromViewState(editText);
            }
        }.setEditText(et));

        return et;
    }

    private Object getValueFromMap(Map<String, Object> map, String fullPath) {
        String[] paths = fullPath.split("\\.");
        for (int i = 0; i<paths.length-1;i++){
            map = (Map<String, Object>) map.get(paths[i]);
        }
        return map.get(paths[paths.length-1]);
    }

    private void setValueInMap(Map<String, Object> map, String fullPath, Object value) {
        String[] paths = fullPath.split("\\.");
        for (int i = 0; i<paths.length-1;i++){
            map = (Map<String, Object>) map.get(paths[i]);
        }
        map.put(paths[paths.length-1], value);
        Log.d("Youbora", "setting field " + paths[paths.length-1] + " = " + value);
    }

    private void updatePreferencesFromViewState(View view) {
        boolean enabled = view.isEnabled();

        Button button = viewToButtonMap.get(view);

        Object value = null;
        if (enabled) {
            // is disabled, depending on the type of widget store the value
            if (view instanceof EditText) {
                // get default type and cast
                Object defaultValue = getValueFromMap(YouboraConfigManager.getDefaultPreferences(), button.getText().toString());
                String viewText = ((EditText) view).getText().toString();
                if (defaultValue instanceof String || defaultValue == null) {
                    value = viewText;
                } else if (defaultValue instanceof Long) {
                    value = Long.parseLong(viewText);
                } else if (defaultValue instanceof Integer) {
                    value = Integer.parseInt(viewText);
                } else if (defaultValue instanceof Double) {
                    value = Double.parseDouble(viewText);
                }
            } else if (view instanceof CheckBox) {
                value = ((CheckBox) view).isChecked();
            }
        }

        // store value
        setValueInMap(youboraPreferences, button.getText().toString(), value);
    }
}
