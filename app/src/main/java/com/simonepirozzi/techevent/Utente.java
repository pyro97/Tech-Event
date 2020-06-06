package com.simonepirozzi.techevent;

import java.util.ArrayList;

public class Utente {
    private ArrayList<Evento> preferiti;
    private String nome,cognome,città,mail,ruolo,provincia;

    public Utente(){}


    public Utente(String n, String c, String ci, String m, String ruo,String pro){
        nome=n;
        cognome=c;
        città=ci;
        mail=m;
        ruolo=ruo;
        provincia=pro;
        preferiti=new ArrayList<>();
    }




    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }







    public String getRuolo() {
        return ruolo;
    }
    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getCittà() {
        return città;
    }

    public ArrayList<Evento> getPreferiti() {
        return preferiti;
    }

    public void setPreferiti(ArrayList<Evento> preferiti) {
        this.preferiti = preferiti;
    }

    public void setCittà(String città) {
        this.città = città;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }



    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }
}
