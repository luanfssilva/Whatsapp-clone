package com.example.luan.whatsapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.luan.whatsapp.R;
import com.example.luan.whatsapp.config.ConfiguracaoFirebase;
import com.example.luan.whatsapp.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText campoEmail, campoSenha;
    private TextView btnCadastrase;
    private Button btnLogin;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        campoEmail = findViewById(R.id.editLogarEmailId);
        campoSenha = findViewById(R.id.editLogarSenhaId);
        btnLogin   = findViewById(R.id.btnLoginId);
        btnCadastrase = findViewById(R.id.textCadastraseId);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validarAutenticacaoUsuario();
            }
        });

        //Abrir tela de cadastro
        btnCadastrase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnCadastrase.setEnabled(false);
                startActivity(new Intent(LoginActivity.this,CadastroActivity.class));
            }
        });
    }

    public void logarUsuario(Usuario usuario){

        btnLogin.setEnabled(false);
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                //Verifica se teve sucesso ao autenticar usuario
                if( task.isSuccessful()){

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));

                }else {

                    String excecao = "";
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        excecao = "Usuário não está cadastrado.";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "E-mail e senha não correspondem a um usuario cadastrado";
                    }catch (Exception e){
                        excecao = "Erro ao logar usuário: " + e.getMessage();
                        e.getStackTrace();
                    }
                    btnLogin.setEnabled(true);
                    Toast.makeText(LoginActivity.this,
                            excecao, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        btnLogin.setEnabled(true);
        btnCadastrase.setEnabled(true);
        //Para recuperar o usuário atual
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        if(usuarioAtual != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
    }

    public void validarAutenticacaoUsuario(){

        //Recupear textos dos campos
        String textoEmail = campoEmail.getText().toString();
        String textoSenha = campoSenha.getText().toString();

        if(!textoEmail.isEmpty()){
            if(!textoSenha.isEmpty()){

                Usuario usuario = new Usuario();
                usuario.setEmail(textoEmail);
                usuario.setSenha(textoSenha);

                logarUsuario(usuario);

            }else{
                Toast.makeText(LoginActivity.this,
                    "Preencha a senha!", Toast.LENGTH_SHORT).show();
            }
        }else {
        Toast.makeText(LoginActivity.this,
                "Preencha o email!", Toast.LENGTH_SHORT).show();
        }
    }




}
