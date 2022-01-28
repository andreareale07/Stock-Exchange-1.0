/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stock_exchange;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.UnsupportedLookAndFeelException;
import operazioni.GeneraLanci;
import operazioni.IntervalliConfidenza;
import operazioni.MediaValoriLanci;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author Andrea
 */
public class Forecast {
    private Calendar t0;
    private int deltaT, lanci;
    private double tassoCrescita, volatilita, s0; //mu
    private double prezzo[];
    private Calendar date[];
    private Calendar start, end, startSim, endSim;
    private ArrayList<double[]> stimeLanciPiù; //Sj+1 = Sj + (Sj * tasso * deltaT) + (Sj * volatilità * deltaW) con z = z;
    private ArrayList<double[]> stimeLanciMeno;//Sj+1 = Sj + (Sj * tasso * deltaT) + (Sj * volatilità * deltaW) con z = (-z);
    private Day[] giornoStima;
    private double mediaStimeLanci[];
    private double mediaStimeLanciIntervallo[];
    public Forecast(Calendar start1, Calendar end1, Calendar start2, Calendar end2) throws InstantiationException, ClassNotFoundException, IllegalAccessException, UnsupportedLookAndFeelException{
        this.start = start1;
        this.end = end1;
        this.startSim = start2;
        this.endSim = end2;
        
        this.stimeLanciPiù = new ArrayList<double[]>();
        this.stimeLanciMeno = new ArrayList<double[]>();
        
        this.t0 = GregorianCalendar.getInstance();
        this.giornoStima = null;
    }
    public void setCalendar(Calendar start1, Calendar end1, Calendar start2, Calendar end2){
        this.start = start1;
        this.end = end1;
        this.startSim = start2;
        this.endSim = end2;
    }
    
    /**
     * 
     * @param p
     * Imposta l'elenco dei prezzi dell'indice. 
     */   
    public void setPrice(double p[]){ this.prezzo = p; } 
    /**
     * 
     * @param d array di calendar
     * Imposta le date dei prezzi dell'array prezzo.
     */
    public void setDate(Calendar d[]){ this.date = d; }
    
    public void setPrice(BigDecimal p[]){ 
        for(int i=0; i<p.length; i++) prezzo[i] = Double.parseDouble(p[i].toString()); 
    }
    
  
    private void setHour(Calendar c, int ore){
        c.set(Calendar.HOUR, ore);
    }
    /**
     * 
     * @param n
     * Setta il paramentro deltaT pari al valore del parametro n
     */
    public void setDeltaT(int n){ this.deltaT = n; }
    /**
     * Considera il periodo di inizio simulazione e calcola il prezzo s0 e t0.
     */
    public void setPriceZero(){ 
        int giorno  = startSim.getTime().getDate();
        int mese = startSim.getTime().getMonth();
        int anno = startSim.getTime().getYear();
        int i = date.length-1;
        
        while(i >= 0 && date[i].getTime().getYear() < anno) i--;
        if(i > 0)
            while(i >= 0 && date[i].getTime().getMonth() < mese) i--;
        if(i>=0){
            if(i >= 0 && date[i].getTime().getDate() > giorno){
                this.s0 = this.calculatePrice(i+1, i);
            }  
            else if(i > 0 && date[i].getTime().getDate() < giorno){
                this.s0 = this.calculatePrice(i, i-1);
            }
            else if(date[i].getTime().getDate() == giorno){
                s0 = prezzo[i];
                System.out.println("Prezzo else "+s0);
            }
            else {
            startSim = date[0];
            s0 = prezzo[0];
            System.out.println("prezzo 0 : "+s0);
            }
        }
        else {
            startSim = date[0];
            s0 = prezzo[0];
            System.out.println("prezzo 0 : "+s0);
            }
        t0 = startSim;
        
    }
    /**
     * 
     * @param i
     * @param j
     * @return 
     * Ritorna la tendenza del prezzo dato in input l'indice i e j 
     * che corrispondono a 2 date dell'array Prezzo[].
     * Consente di ricavare il valore discreto del prezzo al t che non sia presente 
     * nell'elenco delle date dei prezzi.
     */
    private double calculatePrice(int i, int j){
        double prezzo = 0.0;
        
        int giorni = (int) ((date[j].getTimeInMillis()/ (1000*60*60*24)) - (date[i].getTimeInMillis()/ (1000*60*60*24)));
        
        int giorniScarto = (int) ((startSim.getTimeInMillis()/ (1000*60*60*24))- (date[i].getTimeInMillis()/ (1000*60*60*24)));
        giorniScarto++;
       
        double prezzoMesePrima = this.prezzo[i];
        double prezzoMeseDopo = this.prezzo[j];
        
        prezzo = ((Math.abs(prezzoMeseDopo - prezzoMesePrima)/giorni)*giorniScarto);
        if(prezzoMesePrima >= prezzoMeseDopo ) prezzo = prezzoMesePrima - prezzo;
        else prezzo = prezzoMesePrima + prezzo;
        return prezzo;
    }
    /**
     * 
     * @param inizio
     * @param fine
     * @return
     * Ritorna il numero di giorni che ci tra le due date prese in input.
     */
    public int getDay(Calendar inizio, Calendar fine){
        int giorni =  (int)(((fine.getTimeInMillis()/ (1000*60*60*24)) - (inizio.getTimeInMillis()/ (1000*60*60*24))));
        return giorni;
    }
    /**
     * Calcola la dimensione che contiene le date dei lanci e lo popola 
     * con le date corrispondenti partendo da t0 e incrementando il tempo di 
     * un volare pari a deltaT.
     */
    public void setDateLanci(){
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(t0.getTime());
        int size = 1;
        
        while(cal.before(endSim)){
            cal.add(Calendar.DATE, deltaT);//incrementa i giorni in base al deltaT 
            size++;
        }
        System.out.println("SIZE "+size);
        Day giornoStimaTmp[] = new Day[size];
        cal.setTime(t0.getTime());
        for(int i = 0; i<size; i++){
            giornoStimaTmp[i] = new Day(cal.getTime());
            cal.add(Calendar.DATE, deltaT);//incrementa i giorni in base al deltaT 
        }
        this.giornoStima = giornoStimaTmp;
    }
    
    
    
    public Day[] getDateStime(){
        return this.giornoStima;
    }
    //Calcola il tasso di crescita dell'indice.

    //calcola la Volatilità dell'indice.
    public void setRateAndVolatility(){
        //(prezzo[i+1] - prezzo[i]) / prezzo[i]
        double scartoPercentuale[] = new double[prezzo.length-1]; 
        for(int i = 0, j = prezzo.length-1; i<prezzo.length-1; i++, j--){
            scartoPercentuale[i] = (prezzo[j-1]- prezzo[j])/prezzo[j];
            System.out.println("prezzo "+i+"+1 - prezzo "+i+" : "+prezzo[j-1]+" - "+prezzo[j]+"  : "+scartoPercentuale[i]);
        }
        /*Calcolo del tasso di crescita come la media degli scarti percentuali*/
        double media = this.getMediaAritmetica(scartoPercentuale);
        this.tassoCrescita = media;
        /********************************/
        
        /*deviazione standard sullo scarto percentuale dei prezzi*/
        double distanza = 0.0;
        for(double d : scartoPercentuale) distanza += Math.pow((d-media), 2);
        this.volatilita = (Math.sqrt(distanza/scartoPercentuale.length));
        //this.volatilita = volatilita*Math.sqrt(260);
        /*********************************************************/
    }
    public double getMediaAritmetica(double array[]){
        double media = 0.0;
        for(int i = 0; i<array.length; i++) media += array[i];
        media /= array.length; 
        return media;
    }
    /**
     * 
     * @return int 
     * Calcola il delta come numero di giorni tra la data del primo prezzo e la 
     * data dell'ultimo prezzo diviso il numero di valori discreti dei prezzi.
     */
    public int getMediumDelta(){
        int giorni = (int) Math.abs((date[date.length-1].getTimeInMillis()/ (1000*60*60*24)) - (date[0].getTimeInMillis()/ (1000*60*60*24)));
        int delta = (int)giorni/prezzo.length;
        return delta;
    }
    public void prepareShow(int lanci, int delta){
        this.lanci = lanci;
        this.setDeltaT(delta);
        this.setRateAndVolatility();  
    }
    
    public void generaLanci(boolean antitetica) throws InterruptedException{
        this.setDateLanci();
        GeneraLanci gen = new GeneraLanci(lanci, deltaT, tassoCrescita, volatilita, s0, this.giornoStima.length);
        Thread t = new Thread(gen);
        t.start();
        t.join();
        this.stimeLanciPiù = gen.getLanciPiù();
        this.stimeLanciMeno = gen.getLanciMeno();
        showLanci(antitetica);
    }
    private void calcolaMediaLanci(boolean antitetica) throws InterruptedException{
        MediaValoriLanci media = new MediaValoriLanci(this.stimeLanciPiù, this.stimeLanciMeno, antitetica);
        Thread t = new Thread(media);
        t.start();
        t.join();
        this.mediaStimeLanci = media.getMedia();
    }
    public double[] getMedia(boolean antitetica){
        try {
            this.calcolaMediaLanci(antitetica);
        } catch (InterruptedException ex) {
            Logger.getLogger(Forecast.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this.mediaStimeLanci;
    }
    public double getConfidenza(int indice, boolean antitetica, int alpha){
        IntervalliConfidenza intConf;
        intConf = new IntervalliConfidenza(this.stimeLanciPiù, this.stimeLanciMeno, alpha,antitetica);
        Thread t = new Thread(intConf);
        t.start();
        try {
            t.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(Forecast.class.getName()).log(Level.SEVERE, null, ex);
        }
        return intConf.getIntervalloConfidenza(3, indice, antitetica);
    }
    private void showLanci(boolean antitetica){
        ShowTrace frameLanci = null;
        try {
            frameLanci = new ShowTrace();
            String data = ""+t0.getTime().getDate()+"/"+t0.getTime().getMonth()+"/"+(t0.getTime().getYear()+1900);
            frameLanci.setParametri(lanci, this.s0, data,this.deltaT, this.tassoCrescita, this.volatilita);
            if(lanci<=500){
                for(int j = 0; j<this.stimeLanciPiù.get(0).length; j++){
                    TimeSeries time = new TimeSeries("", Day.class);
                    for(int i = 0; i<this.stimeLanciPiù.size(); i++){
                        time.add(this.giornoStima[i], this.stimeLanciPiù.get(i)[j]);
                    }   
                    frameLanci.addSerie(time);
                }
                if(antitetica){
                    for(int j = 0; j<this.stimeLanciMeno.get(0).length; j++){
                        TimeSeries time = new TimeSeries("", Day.class);
                        for(int i = 0; i<this.stimeLanciMeno.size(); i++){
                            time.add(this.giornoStima[i], this.stimeLanciMeno.get(i)[j]);
                        }   
                        frameLanci.addSerie(time);
                    }
                }
            }
            else frameLanci.showChart(false);
            int indice = 1;
            for(int i = 0; i<stimeLanciPiù.size(); i++){
                frameLanci.addReport("\n*************************");
                frameLanci.addReport("\nStime tempo t"+i);
                frameLanci.addReport("\n*************************");
                for(int j = 0; j<stimeLanciPiù.get(i).length; j++){
                    frameLanci.addReport("\n"+indice+")(S+) "+this.stimeLanciPiù.get(i)[j]);
                    indice++;
                    if(antitetica){
                        frameLanci.addReport("\n"+indice+")(S-) "+this.stimeLanciMeno.get(i)[j]);
                        indice++;
                    }
                    if(i == 0) j = stimeLanciPiù.get(i).length;
                }
            }
            frameLanci.setVisible(true);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Forecast.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(Forecast.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Forecast.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Forecast.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void showMedia(boolean antitetica, int alpha, TimeSeries stock){
        ShowTrace media = null;
        try {
            media = new ShowTrace();
            TimeSeries time = new TimeSeries("Stime", Day.class);
            TimeSeries deltaPiu = new TimeSeries("Più delta", Day.class);
            TimeSeries deltaMeno = new TimeSeries("Meno delta", Day.class);
            double tmpPrezzo[] = getMedia(antitetica);
            Day tmpGiorni[] = getDateStime();

            for(int i = 0; i<tmpPrezzo.length; i++) time.add(tmpGiorni[i], tmpPrezzo[i]);
            for(int i = 1; i<tmpPrezzo.length; i++) deltaPiu.add(tmpGiorni[i], 
                    (tmpPrezzo[i]+this.getConfidenza(i, antitetica, alpha)));
            for(int i = 1; i<tmpPrezzo.length; i++) deltaMeno.add(tmpGiorni[i], 
                    (tmpPrezzo[i]-this.getConfidenza(i, antitetica, alpha)));
            media.addSerie(stock);
            media.addSerie(time);
            media.addSerie(deltaPiu);
            media.addSerie(deltaMeno);
            media.addReport("**********************");
            media.addReport("\nValori reali indice");
            media.addReport("\n**********************");
            int indice = 1;
            for(int i = 0; i< this.prezzo.length; i++) media.addReport("\n"+(indice++)+") "+prezzo[i]);
            media.addReport("\n**********************");
            media.addReport("\nValori Stimati");
            media.addReport("\n**********************");
            for(int i = 0; i<tmpPrezzo.length; i++) media.addReport("\n"+(indice++)+") "+tmpPrezzo[i]);

            /*******intervalli confidenza****/
            media.addReport("\n**********************");
            media.addReport("\nDelta confidenza Stime");
            media.addReport("\n**********************");
            for(int i = 1; i<tmpPrezzo.length; i++) 
                media.addReport("\n"+(indice++)+") "+getConfidenza(i, antitetica, alpha));
            /********************************/

            media.showParametri(false);
            media.setVisible(true);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Forecast.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(Forecast.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Forecast.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Forecast.class.getName()).log(Level.SEVERE, null, ex);
        }   
    }
}
