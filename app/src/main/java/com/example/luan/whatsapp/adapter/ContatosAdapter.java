package com.example.luan.whatsapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.luan.whatsapp.R;
import com.example.luan.whatsapp.model.Usuario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by @luanfssilva on 08/02/2019.
 */


public class ContatosAdapter extends RecyclerView.Adapter<ContatosAdapter.MyViewHoldder> {

    private List<Usuario> contatos;
    private Context context;

    public ContatosAdapter(List<Usuario> listaContatos, Context c) {
        this.contatos = listaContatos;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHoldder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_contatos, parent, false);
        return new MyViewHoldder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHoldder holder, int position) {

        Usuario usuario = contatos.get(position);

        holder.nome.setText(usuario.getNome());
        holder.email.setText(usuario.getEmail());

        if (usuario.getFoto() != null){
            Uri uri = Uri.parse(usuario.getFoto());
            Glide.with(context).load(uri).into(holder.foto);
        }else{
            holder.foto.setImageResource(R.drawable.padrao);
        }

    }

    @Override
    public int getItemCount() {
        return contatos.size();
    }

    public class MyViewHoldder extends RecyclerView.ViewHolder{

        private TextView nome,email;
        private CircleImageView foto;

        public MyViewHoldder(View itemView) {
            super(itemView);

            foto = itemView.findViewById(R.id.imageFotoContato);
            nome = itemView.findViewById(R.id.textNomeContato);
            email = itemView.findViewById(R.id.textEmailContato);

        }
    }


}
