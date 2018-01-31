package it.unive.dais.cevid.datadroid.template;

/**
 * Created by buzdu on 1/30/2018.
 */

public class Autovelox {
    private String Comune;
    private String Provincia;
    private String Regione;
    private String Nome;
    private String Annoinserito;
    private String Dataeorainserimento;
    private double IdentificatoreinOpenStreetMap;
    private double Longitudine;
    private double Latitudine;

    public Autovelox(String Comune,String Provincia,String Regione,String Nome,String Annoinserito,String Dataeorainserimento,double IdentificatoreinOpenStreetMap,double Longitudine,double Latitudine){
        this.Comune=Comune;
        this.Provincia=Provincia;
        this.Regione=Regione;
        this.Nome=Nome;
        this.Annoinserito=Annoinserito;
        this.Dataeorainserimento=Dataeorainserimento;
        this.IdentificatoreinOpenStreetMap=IdentificatoreinOpenStreetMap;
        this.Longitudine=Longitudine;
        this.Latitudine=Latitudine;
    }

    public void setComune(String Comune){
        this.Comune=Comune;
    }

    public String getComune(){
        return Comune;
    }

    public void setProvincia(String Provincia){
        this.Provincia=Provincia;
    }

    public String getProvincia(){
        return Provincia;
    }

    public void setRegione(String Regione){
        this.Regione=Regione;
    }

    public String getRegione(){
        return Regione;
    }

    public void setNome(String Nome){
        this.Nome=Nome;
    }

    public String getNome(){
        return Nome;
    }

    public void setAnnoinserito(String Annoinserito){
        this.Annoinserito=Annoinserito;
    }

    public String getAnnoinserito(){
        return Annoinserito;
    }

    public void setDataeorainserimento(String Dataeorainserimento){
        this.Dataeorainserimento=Dataeorainserimento;
    }

    public String getDataeorainserimento(){
        return Nome;
    }

    public void setIdentificatoreinOpenStreetMap(double IdentificatoreinOpenStreetMap){
        this.IdentificatoreinOpenStreetMap=IdentificatoreinOpenStreetMap;
    }

    public double getIdentificatoreinOpenStreetMap(){
        return IdentificatoreinOpenStreetMap;
    }

    public void setLongitudine(double Longitudine){
        this.Longitudine=Longitudine;
    }

    public double getLongitudine(){
        return Longitudine;
    }

    public void setLatitudine(double Latitudine){
        this.Latitudine=Latitudine;
    }

    public double getLatitudine(){
        return Latitudine;
    }

    @Override
    public String toString() {
        return "Student [Comune=" + Comune
                + ", Provincia=" + Provincia
                + ", Regione=" + Regione
                + ", Nome=" + Nome
                + ", Annoinserito=" + Annoinserito
                + ", Dataeorainserimento=" + Dataeorainserimento
                + ", IdentificatoreinOpenStreetMap=" + IdentificatoreinOpenStreetMap
                + ", Longitudine=" + Longitudine
                + ", Latitudine=" + Latitudine + "]";
    }

}