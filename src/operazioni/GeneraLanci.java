/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operazioni;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andrea
 */
public class GeneraLanci implements Runnable {
    
    int numOperazioni, operazioniCompletate;
    
    private ArrayList<double[]> stimeLanciPiù; //Sj+1 = Sj + (Sj * tasso * deltaT) + (Sj * volatilità * deltaW) con z = z;
    private ArrayList<double[]> stimeLanciMeno;//Sj+1 = Sj + (Sj * tasso * deltaT) + (Sj * volatilità * deltaW) con z = (-z);
    
    private double tassoCrescita, volatilita, s0; //mu
    private int deltaT, lanci, giorniStime;
    /**
     * 
     * @param lanci
     * @param deltaT
     * @param tasso
     * @param vol
     * @param s0
     * @param giorni 
     */
    public GeneraLanci(int lanci, int deltaT, double tasso, double vol, double s0, int giorni){
        this.lanci = lanci;
        this.deltaT = deltaT;
        this.tassoCrescita = tasso;
        this.volatilita = vol;
        this.s0 = s0;
        this.giorniStime = giorni;
        this.numOperazioni = 0;
        this.operazioniCompletate = 0;
        this.stimeLanciPiù = new ArrayList<double[]>();
        this.stimeLanciMeno = new ArrayList<double[]>();
        this.numOperazioni = (giorniStime*2)+(lanci*2)+(giorniStime*lanci*2);
    }
    public int getNumberOfOperation(){
        return this.numOperazioni;
    }
    public int getOperationDone(){
        return this.operazioniCompletate;
    }
    public ArrayList<double[]> getLanciPiù(){
        return this.stimeLanciPiù;
    }
    public ArrayList<double[]> getLanciMeno(){
        return this.stimeLanciMeno;
    }
    @Override
    public void run() {
        LoadingBar load = new LoadingBar(this.numOperazioni, "Generazione Lanci");
        Thread t = new Thread(load);
        t.start();
        //inizializzo gli array.
        for(int i = 0; i<giorniStime; i++){
            this.stimeLanciPiù.add(new double[lanci]);
            this.stimeLanciMeno.add(new double[lanci]);
        }
        double sj = s0;
        //caso base t0
        for(int i = 0; i<lanci; i++){
            this.stimeLanciPiù.get(0)[i] = sj;
            this.stimeLanciMeno.get(0)[i] = sj;
        }
        this.operazioniCompletate += (lanci*2);
        load.setOperation(this.operazioniCompletate);
        //passo iterativo.
        double sqrtDelta = Math.sqrt(deltaT);
        this.operazioniCompletate += (lanci*2)+(giorniStime*2);
        load.setOperation(this.operazioniCompletate);
        Random rand = new Random();
        for(int i = 1; i<giorniStime; i++){
            for(int j = 0; j <lanci; j++){
                double z = rand.nextGaussian();
                sj = stimeLanciPiù.get(i-1)[j];
                double deltaW = z * sqrtDelta;
                stimeLanciPiù.get(i)[j] = sj + (tassoCrescita*sj*deltaT)+(volatilita*sj*deltaW); 
                //System.out.println("deltaW = "+deltaW);
                //System.out.println("Lancio "+j+" - Sj: "+sj+" - z = "+z+"  Mu = "+tassoCrescita+" Sigma = "+volatilita+ " T= "+deltaT);
                deltaW = sqrtDelta*(-z);
                //System.out.println("deltaW = "+deltaW);
                //sj = stimeLanciMeno.get(i-1)[j];
                stimeLanciMeno.get(i)[j] = sj+(tassoCrescita*sj*deltaT)+(volatilita*sj*deltaW); 
            }
            this.operazioniCompletate += (lanci*2);
            load.setOperation(this.operazioniCompletate);
        }
    }
}
