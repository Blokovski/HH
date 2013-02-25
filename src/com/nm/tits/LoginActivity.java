package com.nm.tits;

import java.net.URL;

import com.nm.tits.CreateUserActivity.BackgroundGetData;

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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;


public class LoginActivity extends Activity
{
	GetData gd;
	EditText etEmail;
	EditText etPass;
	String email;
	String pass;
	String sex;
	boolean hasErrors;
	String result;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		etEmail = (EditText) findViewById(R.id.etUserName);
		etPass = (EditText) findViewById(R.id.etPassword);
		hasErrors = false;
			
		gd = App.getGetData();
		
		if ( App.appSharedPrefs.getString("email", "") != null && !App.appSharedPrefs.getString("email", "").equals(""))
		{
			etEmail.setText(App.appSharedPrefs.getString("email", ""));
			etPass.setText(App.appSharedPrefs.getString("pass", ""));						
		}
		App.tracker.trackPageView("/HHLoginScreen");
	}
	
	public void onClickLogin(View view)
	{
		email = etEmail.getText().toString();
		pass = etPass.getText().toString();
		

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

		if (hasErrors)
		{
			hasErrors = false;
			Toast.makeText(LoginActivity.this, getString(R.string.please_correct_errors), Toast.LENGTH_LONG).show();
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
				Toast.makeText(LoginActivity.this, getString(R.string.login_ok), Toast.LENGTH_SHORT).show();
				
				App.prefsEditor.putString("email", email);
				App.prefsEditor.putString("pass", pass);
				App.prefsEditor.commit();
				
				Intent i = new Intent(LoginActivity.this, MainMenuActivity.class);
				startActivity(i);	
				finish();
			}else
			{
				Toast.makeText(LoginActivity.this, "Error: " + result, Toast.LENGTH_LONG).show();
			}
		} catch (Exception e)
		{
			Log.e("LoginActivity.setResult()", e.toString());
		}
	}
	
	public class BackgroundGetData extends AsyncTask<URL, Integer, Long>
	{

		ProgressDialog dialog = ProgressDialog.show(LoginActivity.this, "", "Talking with server...", true);
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
			result = gd.login(email, pass);
			}
			return null;
		}

		@Override
		public void onProgressUpdate(Integer... progress)
		{

		}
	}
}
