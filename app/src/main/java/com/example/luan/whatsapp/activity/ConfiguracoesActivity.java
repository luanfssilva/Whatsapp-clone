package com.example.luan.whatsapp.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

import com.example.luan.whatsapp.R;
import com.example.luan.whatsapp.helper.Permissao;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfiguracoesActivity extends AppCompatActivity {

    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private ImageButton galeria, camera;
    private static final int SELECAO_CAMERA  = 100;
    private static final int SELECAO_GALERIA = 200;
    private CircleImageView circleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Validar permissões
        Permissao.validarPermissoes(permissoesNecessarias, this, 1);

        camera = findViewById(R.id.imageCamera);
        galeria = findViewById(R.id.imageGaleria);
        circleImageView = findViewById(R.id.profile_image);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(intent.resolveActivity(getPackageManager()) != null){
                    //Abri uma activity e espera por um resultado dela.
                    startActivityForResult(intent, SELECAO_CAMERA );
                }
            }
        });

        galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(intent.resolveActivity(getPackageManager()) != null){
                    //Abri uma activity e espera por um resultado dela.
                    startActivityForResult(intent, SELECAO_GALERIA );
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Bitmap imagem = null;

            try{

                switch (requestCode){
                    case SELECAO_CAMERA:
                        imagem = (Bitmap) data.getExtras().get("data");
                        break;

                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                        break;
                }

                if (imagem != null){
                    circleImageView.setImageBitmap( imagem );
                }

            }catch(Exception e){
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for ( int permissaoResultado: grantResults ){
            if (permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }
        }
    }

    private void alertaValidacaoPermissao(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage(R.string.alertPermission);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(ConfiguracoesActivity.this, ConfiguracoesActivity.class));
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
