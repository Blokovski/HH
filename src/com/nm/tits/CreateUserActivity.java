package com.nm.tits;

import java.net.URL;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class CreateUserActivity extends Activity
{
	GetData gd;
	EditText etEmail;
	EditText etPass;
	EditText etUserName;
	EditText etRePass;
	String username;
	String rePass;	
	String email;
	String pass;
	String sex;
	String country;
	RadioGroup radioSexGroup;
	RadioButton radioSexButton;
	boolean hasErrors;
	String result;
	Spinner spin;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_user);
		etUserName  = (EditText) findViewById(R.id.etUserName);
		etEmail = (EditText) findViewById(R.id.etEmail);
		etRePass = (EditText) findViewById(R.id.etRetypePassword);
		etPass = (EditText) findViewById(R.id.etPassword);
		radioSexGroup = (RadioGroup) findViewById(R.id.radioSex);
		hasErrors = false;
		country = App.appSharedPrefs.getString("country", "Worlwide");
		gd = App.getGetData();
		
		spin = (Spinner) findViewById(R.id.spinnerCountry);

		ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.countries, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin.setAdapter(adapter);
		spin.setSelection(adapter.getPosition(country));
		
		spin.setOnItemSelectedListener(new OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				country = spin.getSelectedItem().toString();
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0)
			{
				// TODO Auto-generated method stub
				
			}
			
		});
	}

	public void onClickCreateUser(View view)
	{

		username = etUserName.getText().toString();
		email = etEmail.getText().toString();
		pass = etPass.getText().toString();
		rePass = etRePass.getText().toString();
		int selectedId = radioSexGroup.getCheckedRadioButtonId();
		radioSexButton = (RadioButton) findViewById(selectedId);
		sex = (String) radioSexButton.getText();

		if (email.length() < 3)
		{
			hasErrors = true;
			etEmail.setBackgroundColor(Color.YELLOW);
		}
		if (pass.length() < 3)
		{
			hasErrors = true;
			etPass.setBackgroundColor(Color.YELLOW);
		}
		
		if (rePass.length() < 3)
		{
			hasErrors = true;
			etRePass.setBackgroundColor(Color.YELLOW);
		}

		if (hasErrors)
		{
			hasErrors = false;
			Toast.makeText(CreateUserActivity.this, getString(R.string.please_correct_errors), Toast.LENGTH_LONG).show();
			return;			
		}
		
		if (!email.contains("@") &&!email.contains("."))
		{
			Toast.makeText(CreateUserActivity.this, getString(R.string.email_not_good), Toast.LENGTH_LONG).show();
			return;
		}
		
		if (!pass.equals(rePass))
		{
			Toast.makeText(CreateUserActivity.this, getString(R.string.password_match), Toast.LENGTH_LONG).show();
			return;
		}
		new BackgroundGetData().execute();

	}
	
	private void setResult()
	{
		try
		{
			if (result.equals("OK"))
			{
				Toast.makeText(CreateUserActivity.this, getString(R.string.account_ok), Toast.LENGTH_SHORT).show();
		
				App.prefsEditor.putString("username", username);
				App.prefsEditor.putString("email", email);
				App.prefsEditor.putString("pass", pass);
				App.prefsEditor.commit();
				
				Intent i = new Intent(CreateUserActivity.this, LoginActivity.class);
				startActivity(i);		
				finish();
			}else
			{
				Toast.makeText(CreateUserActivity.this, "Error: " + result, Toast.LENGTH_LONG).show();
			}
		} catch (Exception e)
		{
			Log.e("CreateUSerActivity.setResult()", e.toString());
		}
	}
	
	public class BackgroundGetData extends AsyncTask<URL, Integer, Long>
	{

		ProgressDialog dialog = ProgressDialog.show(CreateUserActivity.this, "", "Talking with server...", true);
		private AsyncTask<URL, Integer, Long> updateTask = null;

		@Override
		protected void onPostExecute(Long result)
		{
			try
			{
				dialog.dismiss();
				dialog = null;
			} catch (Exception e)
			{
			}
			setResult();
		}

		@Override
		protected void onPreExecute()
		{
			updateTask = this;
			dialog.setCancelable(true);
			dialog.setOnDismissListener(new OnDismissListener()
			{
				@Override
				public void onDismiss(DialogInterface dialog)
				{
					updateTask.cancel(true);
				}
			});
			dialog.show();
		}

		@Override
		protected Long doInBackground(URL... params)
		{

			if (isCancelled()) 
			{
			   return null;
			}
			else
			{
			result = gd.createUser(username, country, email, pass, sex);
			}
			return null;
		}

		@Override
		public void onProgressUpdate(Integer... progress)
		{

		}
	}
}
