/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stock_exchange;

import java.awt.List;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.UnsupportedLookAndFeelException;
import org.jfree.data.time.Day;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Year;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;

/**
 *
 * @author Andrea
 */
public class DataSetQuote {
    private Stock stock;
    private Calendar start;
    private Calendar end;
    private Forecast forecast;
    private Calendar date[];
    private double prezzo[];
    private ArrayList<HistoricalQuote> list;
    public DataSetQuote(){
        
    }
    void setStock(String s) throws IOException{
        this.stock = YahooFinance.get(s);
    }
    void setHistoryQuote(Calendar c1, Calendar c2) throws IOException{
        try{
            list = (ArrayList<HistoricalQuote>) stock.getHistory(c1,c2);
        }catch(Exception e){
           try{
            list = (ArrayList<HistoricalQuote>) stock.getHistory();
           }catch(Exception e1){
               JOptionPane.showMessageDialog(null, "Nessun Stock Rilevato ! \nSelezionare uno stock e riprovare !", "Errore", 0);
           }
        }
    }
    void setHistoryQuote() throws IOException{
        list = (ArrayList<HistoricalQuote>) stock.getHistory(start,end);
    }
    ArrayList<HistoricalQuote> getHistory(){
        return list;
    }
    void setCalendar(Calendar c1, Calendar c2) throws IOException{
        this.start = c1;
        this.end = c2;
        setHistoryQuote(start, end);
    }

    BigDecimal [] getPrices() throws IOException{
        ArrayList<HistoricalQuote> list = getHistory();
        BigDecimal prices[] = new BigDecimal[list.size()];
        for(int i = 0; i<list.size(); i++){
            prices[i] = list.get(i).getClose();
        }  
        this.setPrice(prices);
        return prices;
    }
    private void setPrice(BigDecimal p[]){
        double temp[] = new double[p.length];
        for(int i = 0; i<p.length; i++) temp[i] = Double.parseDouble(p[i].toString());
        this.prezzo = temp;
    }
    Calendar [] getDates() throws IOException{
        return date;
    }
    void setDates(ArrayList<HistoricalQuote> list){
        Calendar dates[] = new Calendar[list.size()];
        for(int i = 0; i<list.size(); i++){
            dates[i] = list.get(i).getDate();
        }  
        this.date = dates;
    }
    public XYDataset getCollection() throws IOException, ParseException{
        TimeSeriesCollection timeCollection = new TimeSeriesCollection(); //istanzia il TimeSeriesCollection
        
        TimeSeries time = new TimeSeries(stock.getName(), Day.class);
        ArrayList<HistoricalQuote> list = this.getHistory();
        this.setDates(list);
        for(HistoricalQuote h : list){
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            int day = h.getDate().getTime().getDate();
            int month = h.getDate().getTime().getMonth()+1;
            int year = h.getDate().getTime().getYear()+1900;
            //Date date = format.parse(h.getDate().toString());
            double value = Double.parseDouble(h.getClose().toString());
            String s = ""+day+month+year+" 06:00:00";
            Day d = new Day(h.getDate().getTime());
            //System.out.println("day :"+d.toString()+"  mese : "+d.getMonth());
            time.add(d, value);
        }       
        
        timeCollection.addSeries(time); //inserisce il timeSeries

        return timeCollection;
    }
    public TimeSeries getSerieStock(){
        TimeSeries time = new TimeSeries(stock.getName(), Day.class);
        ArrayList<HistoricalQuote> list = this.getHistory();
        this.setDates(list);
        for(HistoricalQuote h : list){
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            int day = h.getDate().getTime().getDate();
            int month = h.getDate().getTime().getMonth()+1;
            int year = h.getDate().getTime().getYear()+1900;
            //Date date = format.parse(h.getDate().toString());
            double value = Double.parseDouble(h.getClose().toString());
            String s = ""+day+month+year+" 06:00:00";
            Day d = new Day(h.getDate().getTime());
            time.add(d, value);
        }    
        return time;
    }
    public XYDataset getCollectionWithForecast() throws IOException, ParseException{
        XYDataset timeCollection = this.getCollection();
        
        return timeCollection;
        
    }
    void testForecast(Calendar c1, Calendar c2, String delta) throws IOException{
        int del = Integer.parseInt(delta);
        try {
            this.forecast = new Forecast(start, end, c1,c2);
        } catch (InstantiationException ex) {
            Logger.getLogger(DataSetQuote.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DataSetQuote.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(DataSetQuote.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(DataSetQuote.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.forecast.setCalendar(start, end, c1, c2);
        
        this.getPrices();
        this.forecast.setDate(date);
        this.forecast.setPrice(prezzo);
        this.forecast.setPriceZero();
        
        
    }  
    void showForecast(int n, int delta, boolean antitetica, int alpha) throws IOException, ParseException{
        try {
            this.forecast.prepareShow(n, delta);
            this.forecast.generaLanci(antitetica);
            this.forecast.showMedia(antitetica, alpha, this.getSerieStock());
        } catch (InterruptedException ex){}      
    }
}
