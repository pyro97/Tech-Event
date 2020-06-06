package com.simonepirozzi.techevent;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;

import android.app.Service;
import android.app.TimePickerDialog;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class AggiungiEventoFragment extends Fragment {
    List<String> cittaList=new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    SweetAlertDialog dialogo;
    private EditText mDisplayDate,orarioInizio,orarioFine,titolo,costo,indirizzo,descrizione;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener onTimeSetListener,onTimeSetListenerFine;
    String dateInput,orarioInizioInput,orarioFineInput;
    AutoCompleteTextView citta;
    private Button selectFoto,pubblica;
    String temp="";
    Utente utente;
    String cittaNew,provincia;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        final View view=inflater.inflate(R.layout.fragment_add_evento, container, false);
        db = FirebaseFirestore.getInstance();
        mDisplayDate = (EditText) view.findViewById(R.id.addEventoData);
        orarioInizio = (EditText) view.findViewById(R.id.addEventoOrarioI);
        orarioFine = (EditText) view.findViewById(R.id.addEventoOrarioF);
        citta=view.findViewById(R.id.addEventoCitta);
        selectFoto=view.findViewById(R.id.addEventoFoto);
        pubblica=view.findViewById(R.id.pubblica_evento);
        titolo=view.findViewById(R.id.addEventoTit);
        costo=view.findViewById(R.id.addEventoCosto);
        indirizzo=view.findViewById(R.id.addEventoIndirizzo);
        descrizione=view.findViewById(R.id.addEventoDescr);





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
                TimePickerDialog timePickerDialog=new TimePickerDialog(view.getContext()
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
                TimePickerDialog timePickerDialog=new TimePickerDialog(view.getContext()
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

        getJson(view);

        ArrayAdapter<String> adapter=new ArrayAdapter<>(view.getContext(),R.layout.custom_list_item_view,R.id.text_view_list_item,cittaList);

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
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Descrizione");

// Set up the input
                final EditText input = new EditText(view.getContext());
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
                builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        pubblica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetwork(view.getContext())){

                    if(titolo.getText().toString().length()!=0
                            && citta.getText().toString().length()!=0 && mDisplayDate.getText().toString().length()!=0
                            && costo.getText().toString().length()!=0
                            && orarioInizio.getText().toString().length()!=0 && orarioFine.getText().toString().length()!=0
                            && indirizzo.getText().toString().length()!=0 && descrizione.getText().toString().length()!=0){


                        DocumentReference docRef = db.collection("/utenti").document(currentUser.getEmail());

                        if(isNetwork(view.getContext())){
                            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    utente=documentSnapshot.toObject(Utente.class);
                                    SimpleDateFormat simpleDateFormat1=new SimpleDateFormat("dd:MM:yyyy HH:mm:ss");
                                    String date=simpleDateFormat1.format(new Date());


                                    if(citta.getText().toString().contains(",")){
                                        cittaNew=citta.getText().toString().substring(0,citta.getText().toString().indexOf(","));
                                        provincia=citta.getText().toString().substring(citta.getText().toString().indexOf(",")+1);

                                    }else{
                                        cittaNew=citta.getText().toString();
                                        provincia="";
                                    }


                                    Evento e=new Evento(utente.getMail(),titolo.getText().toString()
                                            ,mDisplayDate.getText().toString(),orarioInizio.getText().toString()
                                            ,orarioFine.getText().toString(),indirizzo.getText().toString()
                                            ,costo.getText().toString(),utente.getNome()+" "+utente.getCognome()
                                            ,cittaNew,provincia,temp,0,date,descrizione.getText().toString(),"","attesa");
                                    if(dialogo!=null)   cancelDialogo(dialogo);
                                    dialogo=startDialogo(view.getContext(),"Caricamento","caricamento",SweetAlertDialog.PROGRESS_TYPE);

                                    if(isNetwork(view.getContext())){

                                        db.collection("/eventi").document(date).set(e, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    if(dialogo!=null)   cancelDialogo(dialogo);
                                                    dialogo=startDialogo(view.getContext(),"Evento in fase di controllo","L'evento è in attesa di essere confermato. Verrai avvisato attraverso una mail",SweetAlertDialog.SUCCESS_TYPE);
                                                }else{
                                                    if(dialogo!=null)   cancelDialogo(dialogo);
                                                    dialogo=startDialogo(view.getContext(),"Attenzione","L'evento non è stato aggiunto. Riprova",SweetAlertDialog.ERROR_TYPE);
                                                }

                                            }
                                        });
                                    }else{
                                        if(dialogo!=null)   cancelDialogo(dialogo);
                                        dialogo=startDialogo(view.getContext(),"Attenzione","Problemi di connessione",SweetAlertDialog.ERROR_TYPE);
                                    }
                                }
                            });
                        }else{
                            if(dialogo!=null)   cancelDialogo(dialogo);
                            dialogo=startDialogo(view.getContext(),"Attenzione","Problemi di connessione",SweetAlertDialog.ERROR_TYPE);
                        }

                    }else{
                        if(dialogo!=null)   cancelDialogo(dialogo);
                        dialogo=startDialogo(view.getContext(),"Attenzione","Tutti i campi devono essere riempiti!",SweetAlertDialog.WARNING_TYPE);
                    }


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
            if(message.equalsIgnoreCase("L'evento è in attesa di essere confermato. Verrai avvisato attraverso una mail")){
                pDialog.setContentText(message);
                pDialog.setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        if(dialogo!=null)   cancelDialogo(dialogo);

                        //getFragmentManager().beginTransaction().replace(R.id.contenitore,new AggiungiEventoFragment(),"aggiungiEvento").addToBackStack("addEvento").commit();
                        titolo.setText("");
                        descrizione.setText("");
                        mDisplayDate.setText("");
                        costo.setText("");
                        orarioInizio.setText("");
                        orarioFine.setText("");
                        citta.setText("");
                        indirizzo.setText("");
                        temp="";
                        selectFoto.setText("");
                        selectFoto.setHint("Seleziona Foto");

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

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
            try {
                final Uri imageUri = data.getData();
                String nomeFoto=getFileName(imageUri);
                final InputStream imageStream = getView().getContext().getContentResolver().openInputStream(imageUri);
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
                dialogo=startDialogo(getView().getContext(),"Foto caricata","La foto è stata caricata con successo!",SweetAlertDialog.SUCCESS_TYPE);

            } catch (Exception e) {
                e.printStackTrace();
                if(dialogo!=null)   cancelDialogo(dialogo);
                dialogo=startDialogo(getView().getContext(),"Caricamento fallito","Il caricamento della foto è fallito",SweetAlertDialog.ERROR_TYPE);
            }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getView().getContext().getContentResolver().query(uri, null, null, null, null);
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

}
