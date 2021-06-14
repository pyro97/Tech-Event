package com.simonepirozzi.techevent.ui.event;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Service;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.simonepirozzi.techevent.R;
import com.simonepirozzi.techevent.data.db.TinyDB;
import com.simonepirozzi.techevent.data.db.model.Event;
import com.simonepirozzi.techevent.data.db.model.User;
import com.simonepirozzi.techevent.ui.main.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import cn.pedant.SweetAlert.SweetAlertDialog;


public class GestioneEvento extends Activity {
    private FirebaseAuth mAuth;
   private FirebaseUser firebaseUser;
    private DatabaseReference mDatabase;
    private FirebaseFirestore db;
    SweetAlertDialog dialogo;
    List<String> cittaList=new ArrayList<>();
    private EditText mDisplayDate,orarioInizio,orarioFine,titolo,costo,indirizzo,descrizione;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener onTimeSetListener,onTimeSetListenerFine;
    String dateInput,orarioInizioInput,orarioFineInput;
    AutoCompleteTextView citta;
    private Button selectFoto,pubblica,rimuovi;
    String temp="",idPubb;
    User user;
    String cittaNew,provincia;
    Event ev;
    TinyDB tinyDB;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestione_evento);
        mAuth=FirebaseAuth.getInstance();
         db = FirebaseFirestore.getInstance();
        titolo=findViewById(R.id.titGestione);
        descrizione=findViewById(R.id.descrGestione);
        mDisplayDate=findViewById(R.id.dataGestione);
        costo=findViewById(R.id.costoGestione);
        orarioInizio=findViewById(R.id.oraIGestione);
        orarioFine=findViewById(R.id.oraFGestione);
        citta=findViewById(R.id.cittaGestione);
        indirizzo=findViewById(R.id.indirizzoGestione);
        selectFoto=findViewById(R.id.fotoGestione);
        pubblica=findViewById(R.id.pubblica_evento_gestione);
        rimuovi=findViewById(R.id.rimuovi_evento_gestione);

        idPubb=getIntent().getStringExtra("idPubb");

        tinyDB=new TinyDB(GestioneEvento.this);

        if(dialogo!=null)   cancelDialogo(dialogo);
        dialogo=startDialogo("Caricamento","caricamento",SweetAlertDialog.PROGRESS_TYPE);


        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                mDisplayDate.setText(dateInput);
            }
        };

        orarioInizio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog=new TimePickerDialog(GestioneEvento.this
                        ,android.R.style.Theme_Holo_Light_Dialog_MinWidth,onTimeSetListener,hour,minute,true);
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.show();
            }
        });

        onTimeSetListener = new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String minuteNew="";
                String hourNew="";
                if(minute==0)  minuteNew="00";
                else if(minute==1)  minuteNew="01";
                else if(minute==2)  minuteNew="02";
                else if(minute==3)  minuteNew="03";
                else if(minute==4)  minuteNew="04";
                else if(minute==5)  minuteNew="05";
                else if(minute==6)  minuteNew="06";
                else if(minute==7)  minuteNew="07";
                else if(minute==8)  minuteNew="08";
                else if(minute==9)  minuteNew="09";
                else    minuteNew=minute+"";

                if(hourOfDay==0)  hourNew="00";
                else if(hourOfDay==1)  hourNew="01";
                else if(hourOfDay==2)  hourNew="02";
                else if(hourOfDay==3)  hourNew="03";
                else if(hourOfDay==4)  hourNew="04";
                else if(hourOfDay==5)  hourNew="05";
                else if(hourOfDay==6)  hourNew="06";
                else if(hourOfDay==7)  hourNew="07";
                else if(hourOfDay==8)  hourNew="08";
                else if(hourOfDay==9)  hourNew="09";
                else    hourNew=hourOfDay+"";

                orarioInizioInput=hourNew+":"+minuteNew;
                orarioInizio.setText(orarioInizioInput);
            }
        };

        orarioFine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog=new TimePickerDialog(GestioneEvento.this
                        ,android.R.style.Theme_Holo_Light_Dialog_MinWidth,onTimeSetListenerFine,hour,minute,true);
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.show();
            }
        });

        onTimeSetListenerFine = new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String minuteNewF="";
                String hourNew="";

                if(minute==0)  minuteNewF="00";
                else if(minute==1)  minuteNewF="01";
                else if(minute==2)  minuteNewF="02";
                else if(minute==3)  minuteNewF="03";
                else if(minute==4)  minuteNewF="04";
                else if(minute==5)  minuteNewF="05";
                else if(minute==6)  minuteNewF="06";
                else if(minute==7)  minuteNewF="07";
                else if(minute==8)  minuteNewF="08";
                else if(minute==9)  minuteNewF="09";
                else    minuteNewF=minute+"";

                if(hourOfDay==0)  hourNew="00";
                else if(hourOfDay==1)  hourNew="01";
                else if(hourOfDay==2)  hourNew="02";
                else if(hourOfDay==3)  hourNew="03";
                else if(hourOfDay==4)  hourNew="04";
                else if(hourOfDay==5)  hourNew="05";
                else if(hourOfDay==6)  hourNew="06";
                else if(hourOfDay==7)  hourNew="07";
                else if(hourOfDay==8)  hourNew="08";
                else if(hourOfDay==9)  hourNew="09";
                else    hourNew=hourOfDay+"";
                orarioFineInput=hourNew+":"+minuteNewF;
                orarioFine.setText(orarioFineInput);
            }
        };


        getJson();

        ArrayAdapter<String> adapter=new ArrayAdapter<>(GestioneEvento.this,R.layout.custom_list_item_view,R.id.text_view_list_item,cittaList);

        citta.setAdapter(adapter);

        selectFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 200);
            }
        });

        descrizione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GestioneEvento.this);
                builder.setTitle("Descrizione");

// Set up the input
                final EditText input = new EditText(GestioneEvento.this);
                if(descrizione.getText().toString().length()>0) input.setText(descrizione.getText().toString());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                builder.setView(input);

// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        descrizione.setText(input.getText().toString());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        db.collection("/eventi").whereEqualTo("id",idPubb).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<Event> eventi=task.getResult().toObjects(Event.class);
                    for(int i=0;i<eventi.size();i++){
                        ev=eventi.get(i);
                        titolo.setText(ev.getTitle());
                        descrizione.setText(ev.getDescription());
                        mDisplayDate.setText(ev.getDate());
                        costo.setText(ev.getCost());
                        orarioInizio.setText(ev.getInitalTime());
                        orarioFine.setText(ev.getFinalTime());
                        citta.setText(ev.getCity()+","+ev.getProvince());
                        indirizzo.setText(ev.getPosition());
                        temp=ev.getPhoto();
                    }
                    if(dialogo!=null)   cancelDialogo(dialogo);

                }
            }
        });



        pubblica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ev.getState().equalsIgnoreCase("attesa")){
                    if(dialogo!=null)   cancelDialogo(dialogo);
                    dialogo=startDialogo("Non puoi modificare l'evento","L'evento è in fase di attesa. Attendi la sua pubblicazione per modificarlo.",SweetAlertDialog.SUCCESS_TYPE);
                }else{

                    if(isNetwork()){

                        if(titolo.getText().toString().length()!=0
                                && citta.getText().toString().length()!=0 && mDisplayDate.getText().toString().length()!=0
                                && costo.getText().toString().length()!=0
                                && orarioInizio.getText().toString().length()!=0 && orarioFine.getText().toString().length()!=0
                                && indirizzo.getText().toString().length()!=0 && descrizione.getText().toString().length()!=0){


                            DocumentReference docRef = db.collection("/utenti").document(mAuth.getCurrentUser().getEmail());

                            if(isNetwork()){
                                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        user =documentSnapshot.toObject(User.class);
                                        SimpleDateFormat simpleDateFormat1=new SimpleDateFormat("dd:MM:yyyy HH:mm:ss");
                                        String date=simpleDateFormat1.format(new Date());
                                        if(citta.getText().toString().contains(",")){
                                            cittaNew=citta.getText().toString().substring(0,citta.getText().toString().indexOf(","));
                                            provincia=citta.getText().toString().substring(citta.getText().toString().indexOf(",")+1);


                                        }else{
                                            cittaNew=citta.getText().toString();
                                        }

                                        if(cittaNew.contains(" ")){
                                            cittaNew=cittaNew.substring(0,cittaNew.indexOf(" "));
                                        }

                                        ev.setTitle(titolo.getText().toString());
                                        ev.setDate(mDisplayDate.getText().toString());
                                        ev.setInitalTime(orarioInizio.getText().toString());
                                        ev.setFinalTime(orarioFine.getText().toString());
                                        ev.setPosition(indirizzo.getText().toString());
                                        ev.setCost(costo.getText().toString());
                                        ev.setDescription(descrizione.getText().toString());
                                        ev.setPhoto(temp);
                                        ev.setCity(cittaNew);
                                        ev.setProvince(provincia);

                                        if(dialogo!=null)   cancelDialogo(dialogo);
                                        dialogo=startDialogo("Caricamento","caricamento",SweetAlertDialog.PROGRESS_TYPE);

                                        if(isNetwork()){

                                            db.collection("/eventi").document(ev.getPublishDate()).set(ev, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                            ev.setState("attesa");
                                                            db.collection("/eventi").document(ev.getPublishDate()).set(ev,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful()){
                                                                        if(dialogo!=null)   cancelDialogo(dialogo);
                                                                        dialogo=startDialogo("Modifica in attesa","La modifica è in fase di attesa. Riceverai una mail di conferma",SweetAlertDialog.SUCCESS_TYPE);

                                                                    }else{
                                                                        if(dialogo!=null)   cancelDialogo(dialogo);
                                                                        dialogo=startDialogo("Attenzione","L'evento non è stato aggiunto. Riprova",SweetAlertDialog.ERROR_TYPE);
                                                                    }
                                                                }
                                                            });

                                                    }else{
                                                        if(dialogo!=null)   cancelDialogo(dialogo);
                                                        dialogo=startDialogo("Attenzione","L'evento non è stato aggiunto. Riprova",SweetAlertDialog.ERROR_TYPE);
                                                    }

                                                }
                                            });
                                        }else{
                                            if(dialogo!=null)   cancelDialogo(dialogo);
                                            dialogo=startDialogo("Attenzione","Problemi di connessione",SweetAlertDialog.ERROR_TYPE);
                                        }
                                    }
                                });
                            }else{
                                if(dialogo!=null)   cancelDialogo(dialogo);
                                dialogo=startDialogo("Attenzione","Problemi di connessione",SweetAlertDialog.ERROR_TYPE);
                            }

                        }else{
                            if(dialogo!=null)   cancelDialogo(dialogo);
                            dialogo=startDialogo("Attenzione","Tutti i campi devono essere riempiti!",SweetAlertDialog.WARNING_TYPE);
                        }


                    }






                }



            }
        });


        rimuovi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialogo!=null)   cancelDialogo(dialogo);
                dialogo=startDialogo("Attenzione","Stai per eliminare l'evento. Sei sicuro?",SweetAlertDialog.WARNING_TYPE);
            }
        });



    }


    public SweetAlertDialog startDialogo(String title, String message, int tipo){


        SweetAlertDialog pDialog = new SweetAlertDialog(GestioneEvento.this, tipo);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(title);

        if(!message.equalsIgnoreCase("caricamento")){
            if(message.equalsIgnoreCase("La modifica è in fase di attesa. Riceverai una mail di conferma")){
                pDialog.setContentText(message);
                pDialog.setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        getFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);                        if(dialogo!=null)   cancelDialogo(dialogo);
                        tinyDB.putString("evento","gestioneEvento");
                        finish();
                        Intent intent=new Intent(GestioneEvento.this, MainActivity.class);
                        startActivity(intent);
                        //getFragmentManager().beginTransaction().replace(R.id.contenitore,new EventiFragment(),"aggiungiEvento").addToBackStack("addEvento").commit();

                    }
                });
            }else if(message.equalsIgnoreCase("Stai per eliminare l'evento. Sei sicuro?")){
                pDialog.setContentText(message);
                pDialog.setCancelButton("Annulla", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        cancelDialogo(dialogo);
                    }
                });

                pDialog.setConfirmButton("Si", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        String DATA=ev.getPublishDate();
                        db.collection("/eventi").document(DATA).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    cancelDialogo(dialogo);
                                    Toast.makeText(GestioneEvento.this,"Evento eliminato",Toast.LENGTH_SHORT).show();
                                    getFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);                        if(dialogo!=null)   cancelDialogo(dialogo);

                                    tinyDB.putString("evento","gestioneEvento");
                                    finish();
                                  Intent intent=new Intent(GestioneEvento.this,MainActivity.class);
                                  startActivity(intent);
                                }else{
                                    cancelDialogo(dialogo);
                                    Toast.makeText(GestioneEvento.this,"Errore. Riprova",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }


            else{
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

    public void getJson(){
        String json;
        try{
            InputStream is=getAssets().open("com.json");
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

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);



        try {
            final Uri imageUri = data.getData();
            String nomeFoto=getFileName(imageUri);
            final InputStream imageStream = getContentResolver().openInputStream(imageUri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            int width = selectedImage.getWidth();
            int height = selectedImage.getHeight();
            float bitmapRatio = (float)width / (float) height;
            if (bitmapRatio > 1) {
                width = 500;
                height = (int) (width / bitmapRatio);
            } else {
                height = 500;
                width = (int) (height * bitmapRatio);
            }
            Bitmap compressed= Bitmap.createScaledBitmap(selectedImage, width, height, true);
            ByteArrayOutputStream baos=new  ByteArrayOutputStream();
            compressed.compress(Bitmap.CompressFormat.PNG,100, baos);
            compressed.recycle();
            byte [] b=baos.toByteArray();
            String imagine= Base64.encodeToString(b, Base64.DEFAULT);
            temp=imagine;
            selectFoto.setText(nomeFoto);
            if(dialogo!=null)   cancelDialogo(dialogo);
            dialogo=startDialogo("Foto caricata","La foto è stata caricata con successo!",SweetAlertDialog.SUCCESS_TYPE);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
            if(dialogo!=null)   cancelDialogo(dialogo);
            dialogo=startDialogo("Caricamento fallito","Il caricamento della foto è fallito",SweetAlertDialog.ERROR_TYPE);
        }

    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public boolean isNetwork(){
        ConnectivityManager manager =(ConnectivityManager) getSystemService(Service.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        if(activeNetwork!=null && activeNetwork.getState()== NetworkInfo.State.CONNECTED)   return true;
        else    return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
            tinyDB.putString("evento","gestioneEvento");

            finish();

          // Intent intent=new Intent(GestioneEvento.this,MainActivity.class);
        //startActivity(intent);
        }

    }


