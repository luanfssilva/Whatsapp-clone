package com.example.luan.whatsapp.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.luan.whatsapp.R;
import com.example.luan.whatsapp.config.ConfiguracaoFirebase;
import com.example.luan.whatsapp.helper.Base64Custom;
import com.example.luan.whatsapp.helper.UsuarioFirebase;
import com.example.luan.whatsapp.model.Mensagem;
import com.example.luan.whatsapp.model.Usuario;
import com.google.firebase.database.DatabaseReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private TextView textNomeChat;
    private CircleImageView imageFotoChat;
    private Usuario usuarioDestinatario;
    private EditText editMsg;
    private FloatingActionButton fabEnviarMsg;

    //identificador usuarios remetente e destinatario
    private String idUsuarioRemetente;
    private String idUsuarioDestinatario;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textNomeChat = findViewById(R.id.textNomeChat);
        imageFotoChat = findViewById(R.id.circleImageFotoChat);
        editMsg = findViewById(R.id.editMensagem);
        fabEnviarMsg = findViewById(R.id.fabEnviarMsg);

        //recuperar dados do usuario remetente
        idUsuarioRemetente = UsuarioFirebase.getIdUsuario();

        //Recuperar dados do usuário destinatario
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){

            usuarioDestinatario = (Usuario) bundle.getSerializable("chatContato");
            textNomeChat.setText(usuarioDestinatario.getNome());

            String foto = usuarioDestinatario.getFoto();
            if(foto != null){
                Uri url = Uri.parse(usuarioDestinatario.getFoto());
                Glide.with(ChatActivity.this)
                        .load(url)
                        .into(imageFotoChat);
            }else {
                imageFotoChat.setImageResource(R.drawable.padrao);
            }

            //Recuperar dados usuario destinatario
            idUsuarioDestinatario = Base64Custom.codificadorBase64(usuarioDestinatario.getEmail());
        }


        fabEnviarMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarMensagem();
            }
        });
    }

    private void enviarMensagem(){
        String textoMensagem = editMsg.getText().toString();

        if(!textoMensagem.isEmpty()){

            Mensagem mensagem = new Mensagem();

            mensagem.setIdUsuario( idUsuarioRemetente );
            mensagem.setMensagem( textoMensagem );

            //Salvar mensagem para o remetente
            salvarMensagem(idUsuarioRemetente,idUsuarioDestinatario,mensagem);

        }else{
            Toast.makeText(ChatActivity.this,"Digite uma" +
                    "mensagem para enviar!",Toast.LENGTH_LONG).show();
        }

    }

    private void salvarMensagem(String idRemetente, String idDestinario, Mensagem msg){

        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference mensagemRef = database.child("mensagens");

        mensagemRef.child(idRemetente)
                .child(idDestinario)
                .push()
                .setValue(msg);

        //Limpar texto
        editMsg.setText("");
    }

}
