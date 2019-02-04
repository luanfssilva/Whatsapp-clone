package com.example.luan.whatsapp.model;

import com.example.luan.whatsapp.activity.ConfiguracoesActivity;
import com.example.luan.whatsapp.config.ConfiguracaoFirebase;
import com.example.luan.whatsapp.helper.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by @luanfssilva on 29/01/2019.
 */


public class Usuario {

    private String uid;
    private String nome;
    private String email;
    private String senha;
    private String foto;


    public Usuario() {
    }

    public void salvar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuario = firebaseRef.child("usuarios").child(getUid());

        //Poderia passsar o objeto como parametro, mas ao
        // utilizar o this vai salvar o objeto inteiro no Firebase
        usuario.setValue(this);

    }

    public void atualizar(){

        String idUsuario = UsuarioFirebase.getIdUsuario();
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference usuariosRef = database.child("usuarios")
                .child(idUsuario);

        Map<String, Object> valoresUsuario = converterParaMap();

        usuariosRef.updateChildren( valoresUsuario );
    }

    @Exclude
    public Map<String, Object> converterParaMap(){
        HashMap<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("email", getEmail());
        usuarioMap.put("nome", getNome());
        usuarioMap.put("foto", getFoto());

        return usuarioMap;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
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
