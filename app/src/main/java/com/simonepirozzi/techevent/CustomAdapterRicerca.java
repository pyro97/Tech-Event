package com.simonepirozzi.techevent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.appcompat.widget.AppCompatImageView;

public class CustomAdapterRicerca extends ArrayAdapter<Evento> {
    private int resource;
    private LayoutInflater inflater;
    private FirebaseFirestore db;

    Evento evento;
    String giorno,mese;
    int numero;

    public CustomAdapterRicerca(Context context, int resourceId, List<Evento> objects) {
            super(context, resourceId, objects);
            resource = resourceId;
            inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View v, ViewGroup parent) {
    		if (v == null) {
    			v = inflater.inflate(R.layout.list_element_ricerca, null);
    		}
    		
             evento = getItem(position);
       

            final TextView titolo,data,luogo,prezzo;
            final LinearLayout linearLayout;
            final AppCompatImageView imageView;

            linearLayout=v.findViewById(R.id.eventoElementR);
            titolo=v.findViewById(R.id.titoloElementR);
            data=v.findViewById(R.id.dateElementR);
            luogo=v.findViewById(R.id.luogoElementR);
            imageView=v.findViewById(R.id.fotoElementR);
        prezzo=v.findViewById(R.id.prezzoElementR);




        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd:MM:yyyy");
        try {
            Date date=simpleDateFormat.parse(evento.getData());
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(date);
            int day= calendar.get(Calendar.DAY_OF_WEEK);
            int month=calendar.get(Calendar.MONTH);
            numero=calendar.get(Calendar.DATE);
            if(day==1)  giorno="dom";
            else if(day==2)  giorno="lun";
            else if(day==3)  giorno="mar";
            else if(day==4)  giorno="mer";
            else if(day==5)  giorno="gio";
            else if(day==6)  giorno="ven";
            else if(day==7)  giorno="sab";


            if(month==0)    mese="gen";
            else if(month==1)   mese="feb";
            else if(month==2)   mese="mar";
            else if(month==3)   mese="apr";
            else if(month==4)   mese="mag";
            else if(month==5)   mese="giu";
            else if(month==6)   mese="lug";
            else if(month==7)   mese="ago";
            else if(month==8)   mese="set";
            else if(month==9)   mese="ott";
            else if(month==10)   mese="nov";
            else if(month==11)   mese="dic";




        } catch (ParseException e) {
            e.printStackTrace();
        }

        titolo.setText(evento.getTitolo());
         data.setText(giorno+", "+numero+" "+mese+" - "+evento.getOrarioI());
         luogo.setText(evento.getCitta()+","+evento.getProvincia()+" - "+evento.getPosizione());
        if(evento.getCosto().equalsIgnoreCase("0")){
            prezzo.setText("Gratuito");
        }else{
            prezzo.setText("Costo: € "+evento.getCosto());
        }



        if(evento.getFoto().length()>0){
             try {
                 byte [] encodeByte= Base64.decode(evento.getFoto(),Base64.DEFAULT);
                 Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                 imageView.setImageBitmap(bitmap);

             } catch(Exception e) {
                 imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_ph));
                 e.getMessage();
                 return null;
             }
         }else{
             imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_ph));

         }


        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                evento = getItem(position);
                Intent intent=new Intent(v.getContext(),EventoActivity.class);
                intent.putExtra("chiamante","ricerca");
                intent.putExtra("id",evento.getId());
                intent.putExtra("descrizioneExtra",evento.getDescrizione());
                intent.putExtra("titoloExtra",evento.getTitolo());
                intent.putExtra("luogoExtra",evento.getCitta()+","+evento.getProvincia()+" - "+evento.getPosizione());
                intent.putExtra("dataExtra",giorno+", "+numero+" "+mese);
                intent.putExtra("orarioExtra",evento.getOrarioI()+" - "+evento.getOrarioF());
                intent.putExtra("costoExtra",evento.getCosto());
                intent.putExtra("organizzExtra",evento.getOrganizzatore());
                intent.putExtra("contattoExtra",evento.getEmail());

                v.getContext().startActivity(intent);
            }
        });


        titolo.setTag(position);
        linearLayout.setTag(position);
            luogo.setTag(position);
            data.setTag(position);
            prezzo.setTag(position);
            return v;
    }
}

