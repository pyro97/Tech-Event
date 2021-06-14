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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.simonepirozzi.techevent.data.db.model.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.appcompat.widget.AppCompatImageView;

public class CustomAdapterPreferiti extends ArrayAdapter<Event> {
    private int resource;
    private LayoutInflater inflater;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    View view;
    Event event;
    String giorno,mese;
    Button cuorex;
    int numero;


    public CustomAdapterPreferiti(Context context, int resourceId, List<Event> objects) {
            super(context, resourceId, objects);
            resource = resourceId;
            inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View v, ViewGroup parent) {
    		if (v == null) {
    			v = inflater.inflate(R.layout.list_element_pref, null);
    		}
    		view=v;

        mAuth= FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    		
             event = getItem(position);
       

            final TextView titolo,data,luogo,cat,prezzo;
            final LinearLayout linearLayout;
            final AppCompatImageView imageView;

            linearLayout=v.findViewById(R.id.eventoElementP);
            titolo=v.findViewById(R.id.titoloElementP);
            data=v.findViewById(R.id.dateElementP);
            luogo=v.findViewById(R.id.luogoElementP);
            imageView=v.findViewById(R.id.fotoElementP);
            prezzo=v.findViewById(R.id.prezzoElementP);




        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd:MM:yyyy");
        try {
            Date date=simpleDateFormat.parse(event.getDate());
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

        titolo.setText(event.getTitle());
         data.setText(giorno+", "+numero+" "+mese+" - "+ event.getInitalTime());
        luogo.setText(event.getCity()+","+ event.getProvince()+" - "+ event.getPosition());

        if(event.getCost().equalsIgnoreCase("0")){
            prezzo.setText("Gratuito");
        }else{
            prezzo.setText("Costo: € "+ event.getCost());
        }


         if(event.getPhoto().length()>0){
             try {
                 byte [] encodeByte= Base64.decode(event.getPhoto(),Base64.DEFAULT);
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
                event = getItem(position);
                Intent intent=new Intent(v.getContext(),EventoActivity.class);
                intent.putExtra("chiamante","preferiti");
                intent.putExtra("id", event.getId());
                intent.putExtra("descrizioneExtra", event.getDescription());
                intent.putExtra("titoloExtra", event.getTitle());
                intent.putExtra("luogoExtra", event.getCity()+","+ event.getProvince()+" - "+ event.getPosition());
                intent.putExtra("dataExtra",giorno+", "+numero+" "+mese);
                intent.putExtra("orarioExtra", event.getInitalTime()+" - "+ event.getFinalTime());
                intent.putExtra("costoExtra", event.getCost());
                intent.putExtra("organizzExtra", event.getManager());
                intent.putExtra("contattoExtra", event.getEmail());


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

