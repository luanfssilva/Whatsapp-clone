package com.example.luan.whatsapp.helper;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by @luanfssilva on 02/02/2019.
 */


public class Permissao {

    public static boolean validarPermissoes(String[] permissoes, Activity activity, int requestCode){

        /*Verificar a versao do Android para só solicitar aos usuarios
        * que tenha a versão 23 (marshmallow) ou superior.
        */
        if (Build.VERSION.SDK_INT >= 23){

            List<String> listaPermissoes = new ArrayList<>();

            /*Percorre as permissões passadas, verificando uma a uma
             * se já tem a permissão liberada
             */
            for(String permissao : permissoes){
                /* Recuperamos pra ver se temos a permissao e em seguida comparar com a
                * permissão salva pelo proprio Android.
                */
                Boolean temPermissao = ContextCompat.checkSelfPermission(activity, permissao) == PackageManager.PERMISSION_GRANTED;
                if(!temPermissao)
                    listaPermissoes.add(permissao);
            }

            //Caso a lista esteja vazia, não é necessário solicitar permissão
            if(listaPermissoes.isEmpty())
                return true;

            String[] novasPermissoes = new String [listaPermissoes.size()];
            listaPermissoes.toArray(novasPermissoes);

            //Solicita Permissão
            ActivityCompat.requestPermissions(activity, novasPermissoes, requestCode );
        }

        return true;
    }

}

