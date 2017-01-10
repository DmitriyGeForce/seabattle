package com.example.dmitriygeforce.mytestapplication;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    TextView tvResult;
    Button btnCheck;
    EditText etDomain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        tvResult = (TextView) findViewById(R.id.tvResult);
        btnCheck = (Button) findViewById(R.id.btnCheck);
        etDomain = (EditText) findViewById(R.id.etDomain);
        btnCheck.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (!TextUtils.isEmpty(etDomain.getText().toString().trim())) {
            tvResult.setText(Parcer.get("https://sprinthost.ru/whois.html?domain=http%3A%2F%2Fstackoverflow.com"));
        }
    }
}
