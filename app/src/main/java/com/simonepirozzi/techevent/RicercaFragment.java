package com.simonepirozzi.techevent;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.Service;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.simonepirozzi.techevent.data.db.TinyDB;
import com.simonepirozzi.techevent.data.db.model.Event;
import com.simonepirozzi.techevent.data.db.model.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class RicercaFragment extends Fragment {
    List<String> cittaList=new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    SweetAlertDialog dialogo;
    Spinner spinner2;
    private EditText nome,orarioInizio,orarioFine,titolo,costo,indirizzo,descrizione;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener onTimeSetListener,onTimeSetListenerFine;
    String dateInput,orarioInizioInput,orarioFineInput;
    AutoCompleteTextView citta;
    private Button ricerca;
    String temp="";
    User user;
    String cittaNew;
    String dataScelta,nomeScelto,luogoScelto,catScelta,periodo;
    TinyDB tinyDB;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        final View view=inflater.inflate(R.layout.fragment_ricerca, container, false);
        db = FirebaseFirestore.getInstance();

        spinner2=view.findViewById(R.id.dataRicerca);
        ricerca=view.findViewById(R.id.ricercaButton);
        citta=view.findViewById(R.id.luogoRicerca);
        nome=view.findViewById(R.id.nomeRicerca);

        dataScelta="";
        periodo="";
        tinyDB=new TinyDB(view.getContext());


        getJson(view);
        ArrayAdapter<String> adapterCitta=new ArrayAdapter<>(view.getContext(),R.layout.custom_list_item_view,R.id.text_view_list_item,cittaList);

        citta.setAdapter(adapterCitta);




        final ArrayList<String> data=new ArrayList<>();
        data.add("Qualsiasi data");
        data.add("Oggi");
        data.add("Questa settimana");
        data.add("Questo mese");
        data.add("Personalizzata");
        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, data);
        spinner2.setAdapter(arrayAdapter1);

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    periodo="";
                    dataScelta="";
                }
                else if(position==1){
                    periodo="";
                    Calendar cal = Calendar.getInstance();
                    int month=cal.get(Calendar.MONTH);
                    int day=cal.get(Calendar.DAY_OF_MONTH);
                    int year=cal.get(Calendar.YEAR);
                    month = month + 1;

                    String dayNew="";
                    if(day==0)  dayNew="00";
                    else if(day==1)  dayNew="01";
                    else if(day==2)  dayNew="02";
                    else if(day==3)  dayNew="03";
                    else if(day==4)  dayNew="04";
                    else if(day==5)  dayNew="05";
                    else if(day==6)  dayNew="06";
                    else if(day==7)  dayNew="07";
                    else if(day==8)  dayNew="08";
                    else if(day==9)  dayNew="09";
                    else    dayNew=day+"";

                    String monthNew="";
                    if(month==0)  monthNew="00";
                    else if(month==1)  monthNew="01";
                    else if(month==2)  monthNew="02";
                    else if(month==3)  monthNew="03";
                    else if(month==4)  monthNew="04";
                    else if(month==5)  monthNew="05";
                    else if(month==6)  monthNew="06";
                    else if(month==7)  monthNew="07";
                    else if(month==8)  monthNew="08";
                    else if(month==9)  monthNew="09";
                    else    monthNew=month+"";
                    dateInput = dayNew + ":" + monthNew + ":" + year;
                    dataScelta=dateInput;
                }
                else if(position==2){
                    Calendar cal = Calendar.getInstance();
                    periodo="si";
                    cal.add(Calendar.DAY_OF_MONTH,7);
                    int month=cal.get(Calendar.MONTH);
                    int day=cal.get(Calendar.DAY_OF_MONTH);
                    int year=cal.get(Calendar.YEAR);
                    month = month + 1;

                    String dayNew="";
                    if(day==0)  dayNew="00";
                    else if(day==1)  dayNew="01";
                    else if(day==2)  dayNew="02";
                    else if(day==3)  dayNew="03";
                    else if(day==4)  dayNew="04";
                    else if(day==5)  dayNew="05";
                    else if(day==6)  dayNew="06";
                    else if(day==7)  dayNew="07";
                    else if(day==8)  dayNew="08";
                    else if(day==9)  dayNew="09";
                    else    dayNew=day+"";

                    String monthNew="";
                    if(month==0)  monthNew="00";
                    else if(month==1)  monthNew="01";
                    else if(month==2)  monthNew="02";
                    else if(month==3)  monthNew="03";
                    else if(month==4)  monthNew="04";
                    else if(month==5)  monthNew="05";
                    else if(month==6)  monthNew="06";
                    else if(month==7)  monthNew="07";
                    else if(month==8)  monthNew="08";
                    else if(month==9)  monthNew="09";
                    else    monthNew=month+"";
                    dateInput = dayNew + ":" + monthNew + ":" + year;
                    dataScelta=dateInput;
                }
                else if(position==3){
                    Calendar cal = Calendar.getInstance();
                    periodo="si";
                    int day=-1;
                    if(cal.get(Calendar.MONTH)==3 || cal.get(Calendar.MONTH)==5 || cal.get(Calendar.MONTH)==8 || cal.get(Calendar.MONTH)==10 ){
                        day=30;
                    }
                    else if(cal.get(Calendar.MONTH)==0 || cal.get(Calendar.MONTH)==2 || cal.get(Calendar.MONTH)==4 || cal.get(Calendar.MONTH)==6 ||
                            cal.get(Calendar.MONTH)==7 || cal.get(Calendar.MONTH)==9 || cal.get(Calendar.MONTH)==11 ){
                        day=31;
                    }
                    else day=28;
                    int month=cal.get(Calendar.MONTH);
                    int year=cal.get(Calendar.YEAR);
                    month = month + 1;

                    String dayNew="";
                    if(day==0)  dayNew="00";
                    else if(day==1)  dayNew="01";
                    else if(day==2)  dayNew="02";
                    else if(day==3)  dayNew="03";
                    else if(day==4)  dayNew="04";
                    else if(day==5)  dayNew="05";
                    else if(day==6)  dayNew="06";
                    else if(day==7)  dayNew="07";
                    else if(day==8)  dayNew="08";
                    else if(day==9)  dayNew="09";
                    else    dayNew=day+"";

                    String monthNew="";
                    if(month==0)  monthNew="00";
                    else if(month==1)  monthNew="01";
                    else if(month==2)  monthNew="02";
                    else if(month==3)  monthNew="03";
                    else if(month==4)  monthNew="04";
                    else if(month==5)  monthNew="05";
                    else if(month==6)  monthNew="06";
                    else if(month==7)  monthNew="07";
                    else if(month==8)  monthNew="08";
                    else if(month==9)  monthNew="09";
                    else    monthNew=month+"";
                    dateInput = dayNew + ":" + monthNew + ":" + year;
                    dataScelta=dateInput;

                }
                else if(position==4){
                    periodo="";
                    Calendar cal = Calendar.getInstance();
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog dialog = new DatePickerDialog(
                            view.getContext(),
                            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                            mDateSetListener,
                            year,month,day);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                }


                Log.d("cazzData",dataScelta);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;

                String dayNew="";
                if(day==0)  dayNew="00";
                else if(day==1)  dayNew="01";
                else if(day==2)  dayNew="02";
                else if(day==3)  dayNew="03";
                else if(day==4)  dayNew="04";
                else if(day==5)  dayNew="05";
                else if(day==6)  dayNew="06";
                else if(day==7)  dayNew="07";
                else if(day==8)  dayNew="08";
                else if(day==9)  dayNew="09";
                else    dayNew=day+"";

                String monthNew="";
                if(month==0)  monthNew="00";
                else if(month==1)  monthNew="01";
                else if(month==2)  monthNew="02";
                else if(month==3)  monthNew="03";
                else if(month==4)  monthNew="04";
                else if(month==5)  monthNew="05";
                else if(month==6)  monthNew="06";
                else if(month==7)  monthNew="07";
                else if(month==8)  monthNew="08";
                else if(month==9)  monthNew="09";
                else    monthNew=month+"";
                dateInput = dayNew + ":" + monthNew + ":" + year;
                dataScelta=dateInput;
                Log.d("cazzData",dataScelta);

            }
        };

        ricerca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isNetwork(view.getContext())){
                    if(dialogo!=null)   cancelDialogo(dialogo);
                    dialogo=startDialogo(view.getContext(),"Caricamento","caricamento",SweetAlertDialog.PROGRESS_TYPE);
                    CollectionReference collectionReference=db.collection("/eventi");
                    if(nome.getText().toString().length()>0 && citta.getText().toString().length()>0){
                        if(citta.getText().toString().contains(",")){
                            cittaNew=citta.getText().toString().substring(0,citta.getText().toString().indexOf(","));

                        }else{
                            cittaNew=citta.getText().toString();
                        }



                        if(periodo.length()>0){
                            //questa settimana,mese
                            collectionReference.whereEqualTo("citta",cittaNew)
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    List<Event> lista=task.getResult().toObjects(Event.class);
                                    ArrayList<Object> lista2=new ArrayList<>();

                                    for(int i=0;i<lista.size();i++) {
                                        SimpleDateFormat simpleDateFormat1=new SimpleDateFormat("dd:MM:yyyy");
                                        try {
                                            Date dataricerca=simpleDateFormat1.parse(dataScelta);
                                            Date trovata=simpleDateFormat1.parse(lista.get(i).getDate());

                                            if(trovata.before(dataricerca)){
                                                if(lista.get(i).getTitle().toLowerCase().contains(nome.getText().toString().toLowerCase())){
                                                    if(lista.get(i).getState().equalsIgnoreCase("pubblicato")){
                                                        lista2.add(lista.get(i));

                                                    }
                                                    Log.d("cazzoE",lista.get(i).toString());

                                                }

                                            }

                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }


                                    }
                                    if(dialogo!=null)   cancelDialogo(dialogo);

                                    tinyDB.remove("lista");
                                    tinyDB.putListObject("lista",lista2);
                                    Intent intent=new Intent(view.getContext(),RicercaActivity.class);
                                    startActivity(intent);
                                }
                            });


                        }else{
                            //qualsiasi,oggi,personalizzata

                            if(dataScelta.length()>0){
                                //oggi,personalizzata
                                collectionReference.whereEqualTo("data",dataScelta)
                                        .whereEqualTo("citta",cittaNew)
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        List<Event> lista=task.getResult().toObjects(Event.class);
                                        ArrayList<Object> lista2=new ArrayList<>();

                                        Log.d("cazzoE",lista.size()+"");

                                        for(int i=0;i<lista.size();i++) {
                                            if(lista.get(i).getTitle().toLowerCase().contains(nome.getText().toString().toLowerCase())){
                                                if(lista.get(i).getState().equalsIgnoreCase("pubblicato")){
                                                    lista2.add(lista.get(i));

                                                }
                                                Log.d("cazzoE",lista.get(i).toString());

                                            }
                                        }
                                        if(dialogo!=null)   cancelDialogo(dialogo);

                                        tinyDB.remove("lista");
                                        tinyDB.putListObject("lista",lista2);
                                        Intent intent=new Intent(view.getContext(),RicercaActivity.class);
                                        startActivity(intent);
                                    }
                                });
                            }else{
                                //qualsiasi
                                collectionReference.whereEqualTo("citta",cittaNew)
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        List<Event> lista=task.getResult().toObjects(Event.class);
                                        ArrayList<Object> lista2=new ArrayList<>();


                                        Log.d("cazzoE",lista.size()+"");

                                        for(int i=0;i<lista.size();i++) {
                                            if(lista.get(i).getTitle().toLowerCase().contains(nome.getText().toString().toLowerCase())){
                                                if(lista.get(i).getState().equalsIgnoreCase("pubblicato")){
                                                    lista2.add(lista.get(i));

                                                }
                                                Log.d("cazzoE",lista.get(i).toString());

                                            }
                                        }
                                        if(dialogo!=null)   cancelDialogo(dialogo);

                                        tinyDB.remove("lista");
                                        tinyDB.putListObject("lista",lista2);
                                        Intent intent=new Intent(view.getContext(),RicercaActivity.class);
                                        startActivity(intent);
                                    }
                                });
                            }

                        }


                    }
                    else if(nome.getText().toString().length()>0){

                        if(periodo.length()>0){
                            //questa settimana,mese
                            collectionReference
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    List<Event> lista=task.getResult().toObjects(Event.class);
                                    ArrayList<Object> lista2=new ArrayList<>();

                                    for(int i=0;i<lista.size();i++) {
                                        SimpleDateFormat simpleDateFormat1=new SimpleDateFormat("dd:MM:yyyy");
                                        try {
                                            Date dataricerca=simpleDateFormat1.parse(dataScelta);
                                            Date trovata=simpleDateFormat1.parse(lista.get(i).getDate());

                                            if(trovata.before(dataricerca)){
                                                if(lista.get(i).getTitle().toLowerCase().contains(nome.getText().toString().toLowerCase())){
                                                    if(lista.get(i).getState().equalsIgnoreCase("pubblicato")){
                                                        lista2.add(lista.get(i));

                                                    }
                                                    Log.d("cazzoE",lista.get(i).toString());

                                                }

                                            }

                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }


                                    }
                                    if(dialogo!=null)   cancelDialogo(dialogo);

                                    tinyDB.remove("lista");
                                    tinyDB.putListObject("lista",lista2);
                                    Intent intent=new Intent(view.getContext(),RicercaActivity.class);
                                    startActivity(intent);
                                }
                            });


                        }else{
                            //qualsiasi,oggi,personalizzata

                            if(dataScelta.length()>0){
                                //oggi,personalizzata
                                collectionReference.whereEqualTo("data",dataScelta)
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        List<Event> lista=task.getResult().toObjects(Event.class);
                                        ArrayList<Object> lista2=new ArrayList<>();

                                        Log.d("cazzoE",lista.size()+"");

                                        for(int i=0;i<lista.size();i++) {
                                            if(lista.get(i).getTitle().toLowerCase().contains(nome.getText().toString().toLowerCase())){
                                                if(lista.get(i).getState().equalsIgnoreCase("pubblicato")){
                                                    lista2.add(lista.get(i));

                                                }
                                                Log.d("cazzoE",lista.get(i).toString());

                                            }
                                        }
                                        if(dialogo!=null)   cancelDialogo(dialogo);

                                        tinyDB.remove("lista");
                                        tinyDB.putListObject("lista",lista2);
                                        Intent intent=new Intent(view.getContext(),RicercaActivity.class);
                                        startActivity(intent);
                                    }
                                });
                            }else{
                                //qualsiasi
                                collectionReference
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        List<Event> lista=task.getResult().toObjects(Event.class);
                                        ArrayList<Object> lista2=new ArrayList<>();



                                        for(int i=0;i<lista.size();i++) {

                                            if(lista.get(i).getTitle().toLowerCase().contains(nome.getText().toString().toLowerCase())){

                                                if(lista.get(i).getState().equalsIgnoreCase("pubblicato")){
                                                    lista2.add(lista.get(i));

                                                }

                                            }
                                        }
                                        if(dialogo!=null)   cancelDialogo(dialogo);

                                        tinyDB.remove("lista");
                                        tinyDB.putListObject("lista",lista2);
                                        Intent intent=new Intent(view.getContext(),RicercaActivity.class);
                                        startActivity(intent);
                                    }
                                });
                            }

                        }
                    }
                    else if(citta.getText().toString().length()>0){
                        if(citta.getText().toString().contains(",")){
                            cittaNew=citta.getText().toString().substring(0,citta.getText().toString().indexOf(","));

                        }else{
                            cittaNew=citta.getText().toString();
                        }



                        if(periodo.length()>0){
                            //questa settimana,mese
                            collectionReference
                                    .whereEqualTo("citta",cittaNew)
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    List<Event> lista=task.getResult().toObjects(Event.class);
                                    ArrayList<Object> lista2=new ArrayList<>();

                                    for(int i=0;i<lista.size();i++) {
                                        SimpleDateFormat simpleDateFormat1=new SimpleDateFormat("dd:MM:yyyy");
                                        try {
                                            Date dataricerca=simpleDateFormat1.parse(dataScelta);
                                            Date trovata=simpleDateFormat1.parse(lista.get(i).getDate());

                                            if(trovata.before(dataricerca)){
                                                if(lista.get(i).getState().equalsIgnoreCase("pubblicato")){
                                                    lista2.add(lista.get(i));

                                                }
                                                Log.d("cazzoE",lista.get(i).toString());

                                            }

                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }


                                    }
                                    if(dialogo!=null)   cancelDialogo(dialogo);

                                    tinyDB.remove("lista");
                                    tinyDB.putListObject("lista",lista2);
                                    Intent intent=new Intent(view.getContext(),RicercaActivity.class);
                                    startActivity(intent);
                                }
                            });


                        }else{
                            //qualsiasi,oggi,personalizzata

                            if(dataScelta.length()>0){
                                //oggi,personalizzata
                                collectionReference.whereEqualTo("data",dataScelta)
                                        .whereEqualTo("citta",cittaNew)
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        List<Event> lista=task.getResult().toObjects(Event.class);
                                        Log.d("cazzoE",lista.size()+"");

                                        ArrayList<Object> objects=new ArrayList<>();

                                        for(int i=0;i<lista.size();i++) {
                                            if(lista.get(i).getState().equalsIgnoreCase("pubblicato")){
                                                objects.add(lista.get(i));

                                            }                                        }

                                        if(dialogo!=null)   cancelDialogo(dialogo);

                                        tinyDB.remove("lista");
                                        tinyDB.putListObject("lista",objects);
                                        Intent intent=new Intent(view.getContext(),RicercaActivity.class);
                                        startActivity(intent);
                                    }
                                });
                            }else{
                                //qualsiasi
                                collectionReference
                                        .whereEqualTo("citta",cittaNew).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        List<Event> lista=task.getResult().toObjects(Event.class);
                                        Log.d("cazzoE",lista.size()+"");
                                        ArrayList<Object> objects=new ArrayList<>();


                                        for(int i=0;i<lista.size();i++) {
                                            if(lista.get(i).getState().equalsIgnoreCase("pubblicato")){
                                                objects.add(lista.get(i));

                                            }
                                        }

                                        if(dialogo!=null)   cancelDialogo(dialogo);

                                        tinyDB.remove("lista");
                                        tinyDB.putListObject("lista",objects);
                                        Intent intent=new Intent(view.getContext(),RicercaActivity.class);
                                        startActivity(intent);


                                    }
                                });
                            }

                        }
                    }
                    else{

                        if(periodo.length()>0){
                            //questa settimana,mese
                            collectionReference
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    List<Event> lista=task.getResult().toObjects(Event.class);
                                    ArrayList<Object> lista2=new ArrayList<>();

                                    for(int i=0;i<lista.size();i++) {
                                        SimpleDateFormat simpleDateFormat1=new SimpleDateFormat("dd:MM:yyyy");
                                        try {
                                            Date dataricerca=simpleDateFormat1.parse(dataScelta);
                                            Date trovata=simpleDateFormat1.parse(lista.get(i).getDate());

                                            if(trovata.before(dataricerca)){
                                                if(lista.get(i).getState().equalsIgnoreCase("pubblicato")){
                                                    lista2.add(lista.get(i));

                                                }
                                                Log.d("cazzoE",lista.get(i).toString());

                                            }

                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                    if(dialogo!=null)   cancelDialogo(dialogo);

                                    tinyDB.remove("lista");
                                    tinyDB.putListObject("lista",lista2);
                                    Intent intent=new Intent(view.getContext(),RicercaActivity.class);
                                    startActivity(intent);
                                }
                            });


                        }else{
                            //qualsiasi,oggi,personalizzata

                            if(dataScelta.length()>0){
                                //oggi,personalizzata
                                collectionReference.whereEqualTo("data",dataScelta)
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        List<Event> lista=task.getResult().toObjects(Event.class);
                                        ArrayList<Object> objects=new ArrayList<>();

                                        for(int i=0;i<lista.size();i++) {
                                            if(lista.get(i).getState().equalsIgnoreCase("pubblicato")){
                                                objects.add(lista.get(i));

                                            }
                                        }

                                        if(dialogo!=null)   cancelDialogo(dialogo);

                                        tinyDB.remove("lista");
                                        tinyDB.putListObject("lista",objects);
                                        Intent intent=new Intent(view.getContext(),RicercaActivity.class);
                                        startActivity(intent);
                                    }
                                });
                            }else{
                                //qualsiasi
                                collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        List<Event> lista=task.getResult().toObjects(Event.class);
                                        Log.d("cazzoE",lista.size()+"");

                                        ArrayList<Object> objects=new ArrayList<>();

                                        for(int i=0;i<lista.size();i++) {
                                            if(lista.get(i).getState().equalsIgnoreCase("pubblicato")){
                                                objects.add(lista.get(i));

                                            }
                                        }

                                        if(dialogo!=null)   cancelDialogo(dialogo);

                                        tinyDB.remove("lista");
                                        tinyDB.putListObject("lista",objects);
                                        Intent intent=new Intent(view.getContext(),RicercaActivity.class);
                                        startActivity(intent);



                                    }
                                });
                            }

                        }

                    }

                }else{

                    if(dialogo!=null)   cancelDialogo(dialogo);
                    dialogo=startDialogo(view.getContext(),"Attenzione","Problemi di connessione",SweetAlertDialog.ERROR_TYPE);
                }
            }
        });

        return view;
    }

    public SweetAlertDialog startDialogo(Context context, String title, String message, int tipo){


        SweetAlertDialog pDialog = new SweetAlertDialog(context, tipo);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(title);

        if(!message.equalsIgnoreCase("caricamento")){
            if(message.equalsIgnoreCase("L'evento è stato aggiunto con successo")){
                pDialog.setContentText(message);
                pDialog.setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        if(dialogo!=null)   cancelDialogo(dialogo);
                        getFragmentManager().beginTransaction().replace(R.id.frame_container,new RicercaFragment(),"aggiungiEvento").addToBackStack("addEvento").commit();

                    }
                });
            }else{
                pDialog.setContentText(message);
                pDialog.setConfirmText("Ok");
            }

        }

        pDialog.setCancelable(false);
        pDialog.show();
        return pDialog;

    }

    public void cancelDialogo(SweetAlertDialog s){

        s.cancel();
    }

    public boolean isNetwork(Context context){
        ConnectivityManager manager =(ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        if(activeNetwork!=null && activeNetwork.getState()== NetworkInfo.State.CONNECTED)   return true;
        else    return false;
    }

    public void getJson(View view){
        String json;
        try{
            InputStream is=view.getContext().getAssets().open("com.json");
            int size=is.available();
            byte[] buffer=new byte[size];

            is.read(buffer);
            is.close();
            json=new String(buffer,"UTF-8");

            JSONArray jsonArray=new JSONArray(json);
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                cittaList.add(jsonObject.getString("comune")+","+jsonObject.getString("provincia"));
            }

        }catch (Exception e){
            Log.d("cazz",e.getMessage());
        }
    }


}
