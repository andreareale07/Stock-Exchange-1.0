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
public class MediaValoriLanci implements Runnable{
   
    int numOperazioni, operazioniCompletate;
    
    private ArrayList<double[]> stimeLanciPiù; //Sj+1 = Sj + (Sj * tasso * deltaT) + (Sj * volatilità * deltaW) con z = z;
    private ArrayList<double[]> stimeLanciMeno;//Sj+1 = Sj + (Sj * tasso * deltaT) + (Sj * volatilità * deltaW) con z = (-z);
    
    private double mediaStimeLanci[];
    private boolean antitetica;
    public MediaValoriLanci(ArrayList<double[]> lanciPiù, ArrayList<double[]> lanciMeno, boolean antitetica){
        this.stimeLanciPiù = lanciPiù;
        this.stimeLanciMeno = lanciMeno;
        this.mediaStimeLanci = new double[lanciPiù.size()];
        this.antitetica = antitetica;
        this.operazioniCompletate = 0;
        if(antitetica) 
            this.numOperazioni = (1+((mediaStimeLanci.length-1)*(stimeLanciPiù.get(0).length*3+1)));
        else 
            this.numOperazioni = (1+((mediaStimeLanci.length-1) * (stimeLanciPiù.get(0).length+1)));
    }
    public int getNumberOfOperation(){
        return this.numOperazioni;
    }
    public int getOperationDone(){
        return this.operazioniCompletate;
    }
    public double[] getMedia(){
        return this.mediaStimeLanci;
    }
    public double getAntitetica(int posS, int posArray){
        return (0.5 * (this.stimeLanciPiù.get(posS)[posArray] + this.stimeLanciMeno.get(posS)[posArray]));
    }
    @Override
    public void run() {
        LoadingBar load = new LoadingBar(this.numOperazioni, "Calcolo Media Lanci");
        Thread t = new Thread(load);
        t.start();
        //caso base t0
        this.mediaStimeLanci[0] = this.stimeLanciPiù.get(0)[0];
        
        this.operazioniCompletate +=1;
        load.setOperation(this.operazioniCompletate);
        //passo iterativo.
        for(int i = 1; i<mediaStimeLanci.length; i++){
            double media = 0.0;
            if(antitetica){
                for(int j = 0; j< stimeLanciPiù.get(i).length;j++){
                    media += this.getAntitetica(i, j);
                }
                this.operazioniCompletate += (stimeLanciPiù.get(i).length)*3;
                load.setOperation(this.operazioniCompletate);
            }
            else {
                for(int j = 0; j< stimeLanciPiù.get(i).length;j++){
                    media += stimeLanciPiù.get(i)[j];
                } 
                this.operazioniCompletate += stimeLanciPiù.get(i).length;
                load.setOperation(this.operazioniCompletate);
            }
            
            this.mediaStimeLanci[i] = (media/stimeLanciPiù.get(i).length);
            this.operazioniCompletate +=1;
            load.setOperation(this.operazioniCompletate);
        }
    }
    
}
