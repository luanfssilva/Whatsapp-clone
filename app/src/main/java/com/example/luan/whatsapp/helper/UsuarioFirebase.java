package com.example.luan.whatsapp.helper;

import com.example.luan.whatsapp.config.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by @luanfssilva on 04/02/2019.
 */


public class UsuarioFirebase {

    public static String getIdUsuario(){

        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
        String email = usuario.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificadorBase64(email);

        return idUsuario;
    }


}
