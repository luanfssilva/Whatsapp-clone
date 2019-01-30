package com.example.luan.whatsapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.luan.whatsapp.R;

public class LoginActivity extends AppCompatActivity {

    private TextView textCadastrase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textCadastrase = findViewById(R.id.textCadastraseId);

        textCadastrase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(LoginActivity.this,CadastroActivity.class));
            }
        });
    }
}
