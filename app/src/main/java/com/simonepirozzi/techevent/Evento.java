package com.simonepirozzi.techevent;

import java.util.ArrayList;
import java.util.UUID;

public class Evento {
    private String email,titolo,data,orarioI,orarioF,
            posizione,costo,organizzatore,citta,foto,dataPubb,descrizione, id,stato,provincia;
    private int priorità;

    public Evento(){}


    public Evento(String email, String titolo, String data, String orarioI, String orarioF,
                  String posizione, String costo, String organizzatore, String citta, String provincia,
                  String foto, int prio, String dp, String descr, String i, String stato) {
        this.email=email;
        this.titolo = titolo;
        this.data = data;
        this.orarioI = orarioI;
        this.orarioF = orarioF;
        this.provincia=provincia;
        priorità=prio;
        this.posizione = posizione;
        this.costo = costo;
        this.organizzatore = organizzatore;
        this.citta=citta;
        this.foto=foto;
        dataPubb=dp;
        descrizione=descr;
        id=i;
        id=UUID.randomUUID().toString();
        this.stato=stato;

    }


    public String getDataPubb() {
        return dataPubb;
    }

    public void setDataPubb(String dataPubb) {
        this.dataPubb = dataPubb;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public int getPriorità() {
        return priorità;
    }

    public void setPriorità(int priorità) {
        this.priorità = priorità;
    }
    public String getTitolo() {
        return titolo;
    }

    @Override
    public String toString() {
        return
                "email='" + email +"\n"+
                "titolo='" + titolo + "\n" +
                "data='" + data + "\n" +
                "orarioI='" + orarioI + "\n"+
                "orarioF='" + orarioF + "\n" +
                "posizione='" + posizione + "\n" +
                "costo='" + costo + "\n" +
                "organizzatore='" + organizzatore + "\n" +
                "citta='" + citta + "\n" ;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCitta() {
        return citta;
    }

    public void setCitta(String citta) {
        this.citta = citta;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getOrarioI() {
        return orarioI;
    }

    public void setOrarioI(String orarioI) {
        this.orarioI = orarioI;
    }

    public String getOrarioF() {
        return orarioF;
    }

    public void setOrarioF(String orarioF) {
        this.orarioF = orarioF;
    }

    public String getPosizione() {
        return posizione;
    }

    public void setPosizione(String posizione) {
        this.posizione = posizione;
    }

    public String getCosto() {
        return costo;
    }

    public void setCosto(String costo) {
        this.costo = costo;
    }


    public String getOrganizzatore() {
        return organizzatore;
    }

    public void setOrganizzatore(String organizzatore) {
        this.organizzatore = organizzatore;
    }


}
