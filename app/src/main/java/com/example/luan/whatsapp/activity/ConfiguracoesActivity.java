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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.luan.whatsapp.R;
import com.example.luan.whatsapp.config.ConfiguracaoFirebase;
import com.example.luan.whatsapp.helper.Base64Custom;
import com.example.luan.whatsapp.helper.Permissao;
import com.example.luan.whatsapp.helper.UsuarioFirebase;
import com.example.luan.whatsapp.model.Usuario;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

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
    private EditText editNome;
    private ImageButton btnEditNome;
    private StorageReference storageReference;
    private String idUsuario;
    private Usuario usuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Configurações Iniciais
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        idUsuario = UsuarioFirebase.getIdUsuario();
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        //Validar permissões
        Permissao.validarPermissoes(permissoesNecessarias, this, 1);

        camera = findViewById(R.id.imageCamera);
        galeria = findViewById(R.id.imageGaleria);
        circleImageView = findViewById(R.id.profile_image);
        editNome = findViewById(R.id.editNome);
        btnEditNome = findViewById(R.id.btnEditarNome);

        //Recuperar dados do usuário
        FirebaseUser usuario = UsuarioFirebase.getUsuarioAtual();
        Uri url = usuario.getPhotoUrl();

        if(url != null ){
            Glide.with(ConfiguracoesActivity.this)
                    .load(url)
                    .into(circleImageView);
        }else {
            circleImageView.setImageResource(R.drawable.padrao);
        }

        editNome.setText(usuario.getDisplayName());

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

        btnEditNome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nome = editNome.getText().toString();
                boolean retorno = UsuarioFirebase.atualizarNomeUsuario( nome );
                if( retorno ){

                    usuarioLogado.setNome( nome );
                    usuarioLogado.atualizar();

                    Toast.makeText(ConfiguracoesActivity.this,
                            "Nome alterado com sucesso!",
                            Toast.LENGTH_SHORT).show();
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

                    //Recuperando dados da imagem para o firebase
                    ByteArrayOutputStream  baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos );
                    byte[] dadosImagem = baos.toByteArray();

                    //Salvar imagem no firebase
                    final StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("perfil")
                            .child(idUsuario)
                            .child("perfil.jpeg");

                    //Verificar caso não seja possível fazer o upload dessa imagem
                    UploadTask uploadTask = imagemRef.putBytes( dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfiguracoesActivity.this,
                                    "Erro ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    atualizaFotoUsuario(uri);
                                }
                            });
                        }
                    });
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private void atualizaFotoUsuario(Uri url) {
        boolean retorno = UsuarioFirebase.atualizarFotoUsuario(url);

        if(retorno){
            usuarioLogado.setFoto( url.toString());
            usuarioLogado.atualizar();

            Toast.makeText(ConfiguracoesActivity.this,
                    "Foto alterada!",
                    Toast.LENGTH_SHORT).show();
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
