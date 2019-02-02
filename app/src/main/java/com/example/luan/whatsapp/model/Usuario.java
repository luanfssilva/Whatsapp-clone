package com.example.luan.whatsapp.model;

import com.example.luan.whatsapp.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

/**
 * Created by @luanfssilva on 29/01/2019.
 */


public class Usuario {

    private String nome;
    private String email;
    private String senha;
    private String uid;

    public Usuario() {
    }

    public void salvar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuario = firebaseRef.child("usuarios").child(getUid());

        //Poderia passsar o objeto como parametro, mas ao
        // utilizar o this vai salvar o objeto inteiro no Firebase
        usuario.setValue(this);

    }

    @Exclude //Remove o id na hora de salvar no Firebase
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude //Remove a senha na hora de salvar no Firebase
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
