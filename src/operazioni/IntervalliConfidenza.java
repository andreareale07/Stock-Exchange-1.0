/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operazioni;

import java.util.ArrayList;

/**
 *
 * @author Andrea
 */
public class IntervalliConfidenza implements Runnable {
    int numOperazioni, operazioniCompletate;
    
    private ArrayList<double[]> stimeLanciPiù; //Sj+1 = Sj + (Sj * tasso * deltaT) + (Sj * volatilità * deltaW) con z = z;
    private ArrayList<double[]> stimeLanciMeno;//Sj+1 = Sj + (Sj * tasso * deltaT) + (Sj * volatilità * deltaW) con z = (-z);
    
    private double intervalloValoriMedi[];
    
    int alpha; 
    boolean antitetica;
    public IntervalliConfidenza(ArrayList<double[]> lanciSpiù, ArrayList<double[]> lanciSmeno, int alpha, boolean antitetica){
        this.stimeLanciPiù = lanciSpiù;
        this.stimeLanciMeno = lanciSmeno;
        this.alpha = alpha;
        this.antitetica = antitetica;
        this.intervalloValoriMedi = new double[lanciSpiù.size()];
        
        this.operazioniCompletate = 0;
        if(antitetica)
            this.numOperazioni = (intervalloValoriMedi.length-1)*((stimeLanciPiù.get(0).length*2*3)+5);
        else
            this.numOperazioni = (intervalloValoriMedi.length-1)*((stimeLanciPiù.get(0).length*2)+5);
    }
    @Override
    public void run() {
        //caso base
        this.intervalloValoriMedi[0] = this.stimeLanciPiù.get(0)[0];
        
        //passo iterativo.
        for(int i = 1; i<this.intervalloValoriMedi.length; i++){
            this.intervalloValoriMedi[i] = this.getIntervalloConfidenza(alpha, i, antitetica);
        }
    }
    public double getIntervalloConfidenza(int alpha, int indice, boolean var){
        double delta = 0.0;
        int size = this.stimeLanciPiù.size();
        delta = alpha * (Math.sqrt((getMediaQuadrati(indice, var)-getMediaQuadrata(indice, var))/size));  
        this.operazioniCompletate += 5;
        return delta;
    }
    private double getMediaQuadrata(int indice, boolean var){
        double media = 0.0;
        int size = this.stimeLanciPiù.get(indice).length;
        if(var){//calcola l'intervallo con la variabile antitetica
            for(int i = 0; i < size; i++){
                double ant = getAntitetica(indice, i);
                media += ant;
                //System.out.println("Media quadrata : Antitetica = "+ant+" Somma = "+media);
            }
            this.operazioniCompletate += size*3; 
        }
        else {
            for(int i = 0; i < size; i++){
                media += this.stimeLanciPiù.get(indice)[i];
               // System.out.println("Media quadrata : Spiù = "+stimeLanciPiù.get(indice)[i]+" Somma = "+media);
            }
            this.operazioniCompletate += size;
        }
        double ris = media/size;
        double pow = Math.pow((ris), 2);
        //System.out.println("Media quadrata : Somma = "+media+" Size = "+size+" Media = "+ris+"  pow = "+pow);
        return pow;
    }
    private double getMediaQuadrati(int indice, boolean var){
        double media = 0.0;
        int size = this.stimeLanciPiù.get(indice).length;
        if(var){//calcola l'intervallo con la variabile antitetica
            for(int i = 0; i < size; i++){
                double ris = getAntitetica(indice, i);
                double pow =  Math.pow(ris, 2);
                media += pow;
                //System.out.println("Media quadrati : Antitetica = "+ris+" Antitetica^2  = "+pow+"  Somma = "+media);
            }
            this.operazioniCompletate += size*3; 
        }
        else { 
            for(int i = 0; i < size; i++){
                double ris = this.stimeLanciPiù.get(indice)[i];
                double spiù= Math.pow(ris, 2);
                media += spiù;
                //System.out.println("Media quadrati : S+ ="+ris+" S+^2 = "+spiù+" Somma = "+media);
            }
            this.operazioniCompletate += size; 
        }
        //System.out.println("Somma totale Media Quadrati = "+media);
        //System.out.println("Media Quadrati = "+(media/size)+"  Size = "+size);
        return (media/size);
    }
    public double getAntitetica(int posS, int posArray){
        double ant = (0.5 * (this.stimeLanciPiù.get(posS)[posArray] + this.stimeLanciMeno.get(posS)[posArray]));
        //System.out.println("Antitetica : "+stimeLanciPiù.get(posS)[posArray]+" + "+stimeLanciMeno.get(posS)[posArray]+"/2 = "+ant);
        return ant;
    }
}
