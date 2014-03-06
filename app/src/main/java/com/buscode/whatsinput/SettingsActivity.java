package com.buscode.whatsinput;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import java.util.List;

public class SettingsActivity extends PreferenceActivity {

	private Preference add_to_list, enable_airinput;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.settings);
	}

	@Override
	protected void onResume() {
		super.onResume();
		initAddTolist();
		initEnableAirInput();
	}
	
	private void initEnableAirInput() {
		enable_airinput = findPreference("enable_airinput");
		enable_airinput.setEnabled(isAirInputInTheList());
		enable_airinput.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			    inputMethodManager.showInputMethodPicker();
				return false;
			}
		});
	}

	private void initAddTolist() {
		add_to_list = findPreference("add_to_list");
		
		add_to_list.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				return false;
			}
		});
	}

	boolean isAirInputInTheList() {
		InputMethodManager imm = getInputMethodManager();
		List<InputMethodInfo> list = imm.getEnabledInputMethodList();
		for (InputMethodInfo info : list) {
			if (getPackageName().equals(info.getPackageName())) {
				return true;
			}
		}
		return false;
	}

	boolean isCurrentAirInput() {
		String current = Settings.Secure.getString(getContentResolver(),
				Settings.Secure.DEFAULT_INPUT_METHOD);
		return !TextUtils.isEmpty(current)
				&& current.startsWith(getPackageName());
	}

	InputMethodManager getInputMethodManager() {
		return (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	}

}
