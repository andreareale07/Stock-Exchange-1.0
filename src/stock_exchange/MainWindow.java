/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stock_exchange;

import yahoofinance.histquotes.HistQuotesRequest;
import yahoofinance.histquotes.HistoricalQuote;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Date;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.jdatepicker.DateModel;
import org.jdatepicker.JDatePicker;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
/**
 *
 * @author Andrea
 */
public class MainWindow extends JFrame implements ActionListener, Runnable, MouseListener{
    
    private JMenuBar barra;
    private JMenu menu[];//0 : File - 1 : Visualizza - 2 : Modifica - 3 : info
    private JMenuItem fileItem[], visualizzaItem[], modificaItem[], aboutItem[];
    private Stock stock;
    private JTextField ricerca, deltaT, lanci, alpha;
    private JCheckBox boxAntitetica;
    private AutoSuggestor autoSuggestor;
    private DatabaseStock dataBase;
    private JSeparator divBarra, divIndice, divParametri, divRange;
    private JLabel lRicerca, lName, lBid, lAsk, lPrice, lPrev, lRange, lStart, 
                   lEnd, lParametri, lStartSim, lEndSim, lDeltaT, lLanci, lAlpha;
    private JButton bRicerca, bSimulazione;
    private String stockSelected = "";
    
    //calendario
    private UtilDateModel modelStart, modelEnd, modelStartSim, modelEndSim;
    private Properties proprietaStart, proprietaEnd, proprietaStartSim, proprietaEndSim;
    private JDatePanelImpl datePanelStart, datePanelEnd,  datePanelStartSim, datePanelEndSim;
    private JDatePickerImpl datePickerStart, datePickerEnd, datePickerStartSim, datePickerEndSim;
    // fine calendario
    private ChartPanel pChart;
    private boolean zoomChart;
    private DataSetQuote dataSet;
    PaintGraf graf;
    public MainWindow() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException{
        barra = new JMenuBar();
        setupMenu();
        dataSet = new DataSetQuote();
        zoomChart = false;

        
        divRange = new JSeparator(JSeparator.HORIZONTAL);
        divBarra = new JSeparator(JSeparator.HORIZONTAL);
        divIndice = new JSeparator(JSeparator.VERTICAL);
        
        this.setDatePicker();
 
        dataBase = new DatabaseStock();
        lRicerca = new JLabel("Ricerca Indice");
        lName = new JLabel("Nome  : ");
        lAsk = new JLabel("Ask : ");
        lBid = new JLabel("Bid : ");
        lPrice = new JLabel("Prezzo : ");
        lPrev = new JLabel("Prev. chiusura : ");
        lRange = new JLabel("Range Temporale (History)");
        lStart = new JLabel("Data Inizio");
        lEnd = new JLabel("Data Fine");
        ricerca = new JTextField(10);
        ricerca.setToolTipText("Inserire il simbolo dell'indice fonte https://it.finance.yahoo.com - "
                + "Esempio : SPY indica SPDR S&P 500 ETF Trust ");
        autoSuggestor = new AutoSuggestor(ricerca, this, dataBase.getArray(), Color.WHITE, Color.BLACK, Color.RED, 1.0f);
        
        bRicerca = new JButton("Cerca");
        bSimulazione = new JButton("Simula");
        //**********Simulazione******************************
        lParametri = new JLabel("Parametri Simulazione");
        lStartSim = new JLabel("Data Inizio");
        lEndSim = new JLabel("Data Fine");
        lDeltaT = new JLabel("Delta T");
        deltaT = new JTextField("30");
        deltaT.setToolTipText("Indica il delta T espresso in giorni tra una stima e la successiva");
        lLanci = new JLabel("Lanci");
        lanci = new JTextField("2");
        lanci.setToolTipText("Indica il numero di lanci effettuati per stimare un singolo S(t)");
        lAlpha = new JLabel("Alpha");
        alpha = new JTextField("3");
        alpha.setToolTipText("Indica il grado di fiducia dell'intervallo di confidenza. Esempio :"
                + " 3 indica un grado di fiducia del 99.7%");
        boxAntitetica = new JCheckBox("Antitetica");        
        divParametri = new JSeparator(JSeparator.HORIZONTAL);
        //**************************************************
        this.setLayout(null);
        int y = 80;
        int x = 80;
        lRicerca.setBounds(10, 20, 200, 20);
        ricerca.setBounds(10, 50, 100, 20);
        bRicerca.setBounds(130, 50, 80, 20);
        lName.setBounds(10, y, 150+x, 20);
        lAsk.setBounds(10, y+=25, 150+x, 20);
        lBid.setBounds(10, y+=25, 150+x, 20);
        lPrice.setBounds(10, y+=25, 150+x, 20);
        lPrev.setBounds(10, y+=25, 150+x, 20);
        divRange.setBounds(0, y+=25, 160+x, 2);
        lRange.setBounds(40, y+=3, 160, 20);
        lStart.setBounds(10, y+=30, 100, 20);
        lEnd.setBounds(125,y,100,20);
        datePickerStart.setBounds(5, y+=20, 110, 28);
        datePickerEnd.setBounds(125, y, 110, 28);

        divParametri.setBounds(0, y+=40, 160+x, 2);
        lParametri.setBounds(50, y+=3, 130, 20);
        lDeltaT.setBounds(10, y+=30, 60, 20);
        deltaT.setBounds(70, y, 100, 20);
        lLanci.setBounds(10, y+=30, 60, 20);
        lanci.setBounds(70, y, 100, 20);
        lAlpha.setBounds(10, y+=30, 60, 20);
        alpha.setBounds(70,y,100,20);
        boxAntitetica.setBounds(10, y+=30, 100, 20);
        lStartSim.setBounds(10, y+=30, 100, 20);
        lEndSim.setBounds(125, y, 100, 20);  
        datePickerStartSim.setBounds(5, y+=20, 110, 28);
        datePickerEndSim.setBounds(125, y, 110, 28);
        bSimulazione.setBounds(60, y+=40, 120, 25);
        
        divBarra.setBounds(0,0,1067,2);
        divIndice.setBounds(160+x,0,2,600);
        //graf.setBounds(300, 20);
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        
        KeySearch listener = new KeySearch();
        ricerca.addKeyListener(listener);
        bRicerca.addActionListener(this);
        bSimulazione.addActionListener(this);
        
        /***grafico*****/
        JFreeChart chart = ChartFactory.createTimeSeriesChart("Stock", "Data", "Prezzo",null);
        pChart = new ChartPanel(chart);
        //disabilita lo zoom 
        pChart.setMouseWheelEnabled(zoomChart);
        pChart.setDomainZoomable(zoomChart);
        pChart.setRangeZoomable(zoomChart);
        //abilita gli assi di riferimento 
        pChart.setHorizontalAxisTrace(false);
        pChart.setVerticalAxisTrace(false);
        //********************************
        pChart.setBounds(250, 20, 800, 500);
        
        this.getContentPane().add(pChart);
        
        /*****fine grafico****/
        
        this.ricerca.requestFocus();
        this.setJMenuBar(barra);
        this.add(divBarra);
        this.add(divRange);
        this.add(divIndice);
        this.add(divParametri);
        getContentPane().add(lRicerca);
        getContentPane().add(bRicerca);
        getContentPane().add(lName);
        getContentPane().add(lAsk);
        getContentPane().add(lBid);
        getContentPane().add(lPrice);
        getContentPane().add(lPrev);
        getContentPane().add(ricerca);
        getContentPane().add(lRange);
        getContentPane().add(lStart);
        getContentPane().add(lEnd);
        getContentPane().add(datePickerStart);
        getContentPane().add(datePickerEnd);
        getContentPane().add(lParametri);
        getContentPane().add(lDeltaT);
        getContentPane().add(deltaT);
        getContentPane().add(lLanci);
        getContentPane().add(lanci);
        getContentPane().add(lAlpha);
        getContentPane().add(alpha);
        getContentPane().add(boxAntitetica);
        getContentPane().add(lStartSim);
        getContentPane().add(lEndSim);
        getContentPane().add(datePickerStartSim);
        getContentPane().add(datePickerEndSim);    
        getContentPane().add(bSimulazione);
        
       // getContentPane().add(graf);
        this.addMouseListener(this);
        this.setSize(1067,600);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setTitle("Stock Exchange 1.0");
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Thread t = new Thread(this);
        t.start();
     
    }
    void setupMenu(){
        menu = new JMenu[4];//0: File - 1: Visualizza - 2: Modifica - 3: info
        fileItem = new JMenuItem[1];
        visualizzaItem = new JMenuItem[3];
        modificaItem = new JMenuItem[5];
        aboutItem = new JMenuItem[1];
        menu[0] = new JMenu("File");
        menu[1] = new JMenu("Visualizza");
        menu[2] = new JMenu("Modifica");
        menu[3] = new JMenu("Aiuto");
        
    /****************Menu file**************/
        fileItem[0] = new JMenuItem("Chiudi");
        for(int i = 0; i< fileItem.length; i++){
            fileItem[i].addActionListener(this);
            menu[0].add(fileItem[i]);
        }
    /*************fine Menu file**************/
    /***************Menu Visualizza**************/
        visualizzaItem[0] = new JMenuItem("Yahoo Finance");
        visualizzaItem[1] = new JMenuItem("Calcolatrice");
        visualizzaItem[2] = new JMenuItem("Elenco Stock Cercati");
        for(int i = 0; i<visualizzaItem.length; i++){
            visualizzaItem[i].addActionListener(this);
            menu[1].add(visualizzaItem[i]);
        }
    /**********fine Menu Visualizza**************/
    /**************Menu Modifica**************/
        modificaItem[0] = new JMenuItem("Visualizza Assi (x,y)");
        modificaItem[1] = new JMenuItem("Nascondi Assi (x,y)");
        modificaItem[2] = new JMenuItem("ON/OFF asse X");
        modificaItem[3] = new JMenuItem("ON/OFF asse Y");
        modificaItem[4] = new JMenuItem("ON/OFF zoom grafico");
        for(int i = 0; i< modificaItem.length; i++){
            modificaItem[i].addActionListener(this);
            menu[2].add(modificaItem[i]);
        }
    /**********fine Menu Modifica**************/
    /****************Menu About******************/
        aboutItem[0] = new JMenuItem("Info");
        for(int i = 0; i<aboutItem.length; i++){
            aboutItem[i].addActionListener(this);
            menu[3].add(aboutItem[i]);
        }
    /***********fine Menu About******************/
        for(int i = 0; i<menu.length; i++){
            menu[i].addActionListener(this);
            barra.add(menu[i]);
        } 
    }
    void setDatePicker(){
        modelStart = new UtilDateModel();
        proprietaStart = new Properties();
        proprietaStart.put("text.today", "Today");
        proprietaStart.put("text.month", "Month");
        proprietaStart.put("text.year", "Year");
        modelStart.setYear(modelStart.getYear()-1);
        datePanelStart = new JDatePanelImpl(modelStart, proprietaStart);
        datePickerStart = new JDatePickerImpl(datePanelStart, new DateLabelFormatter());
        
        modelEnd = new UtilDateModel();
        proprietaEnd = new Properties();
        proprietaEnd.put("text.today", "Today");
        proprietaEnd.put("text.month", "Month");
        proprietaEnd.put("text.year", "Year");
        datePanelEnd = new JDatePanelImpl(modelEnd, proprietaEnd);
        datePickerEnd = new JDatePickerImpl(datePanelEnd, new DateLabelFormatter());
        
        modelStartSim = new UtilDateModel();
        proprietaStartSim = new Properties();
        proprietaStartSim.put("text.today", "Today");
        proprietaStartSim.put("text.month", "Month");
        proprietaStartSim.put("text.year", "Year");
        datePanelStartSim = new JDatePanelImpl(modelStartSim, proprietaStartSim);
        datePickerStartSim = new JDatePickerImpl(datePanelStartSim, new DateLabelFormatter());
        
        modelEndSim = new UtilDateModel();
        proprietaEndSim = new Properties();
        proprietaEndSim.put("text.today", "Today");
        proprietaEndSim.put("text.month", "Month");
        proprietaEndSim.put("text.year", "Year");
        modelEndSim.setYear(modelStartSim.getYear()+1);
        datePanelEndSim = new JDatePanelImpl(modelEndSim, proprietaEndSim);
        datePickerEndSim = new JDatePickerImpl(datePanelEndSim, new DateLabelFormatter());
        
        //attiva data 
        modelStart.setSelected(true);
        modelEnd.setSelected(true);
        modelStartSim.setSelected(true);
        modelEndSim.setSelected(true);
    }
    Calendar getDate(UtilDateModel model) throws ParseException{
        Calendar tmp = GregorianCalendar.getInstance();
        
        int anno = model.getYear();
        int mese = model.getMonth();
        int giorno = model.getDay();
        Date d = new Date(anno-1900, mese, giorno);
        
        tmp.setTime(d);
        return tmp;
    }
    void addChart(ChartPanel p){
        p.setBounds(300, 20, 600, 600);
        this.add(p);
        this.revalidate();
        this.repaint();
    }
    void setStockDetail(){
        lName.setText("Nome  : "+stock.getName());
        lAsk.setText("Richiesta : "+stock.getQuote().getAsk());
        lBid.setText("Bid : "+stock.getQuote().getBid());
        lPrice.setText("Prezzo : "+stock.getQuote().getPrice());
        BigDecimal stima = stock.getQuote().getChange();
        BigDecimal prezzo = stock.getQuote().getPrice();
        float stima1 = Float.parseFloat(stima.toString());
        float prezzo1 = Float.parseFloat(prezzo.toString());
        stima1+= prezzo1;
        //System.out.println("Stima "+stima);
        lPrev.setText("Prev. chiusura : "+stima1);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if(ae.getActionCommand().equals("Cerca")){
            searchStock();
            try {
                if(!stockSelected.equals("")) setChart();
            } catch (IOException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParseException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if(ae.getActionCommand().equals("Simula")){
            try {
                dataSet.setCalendar(getDate(modelStart), getDate(modelEnd));
                Calendar c = getDate(modelStart);
                //System.out.println("Anno Start main : "+c.getTime().getYear());
                dataSet.testForecast(getDate(modelStartSim), getDate(modelEndSim), this.deltaT.getText());
                dataSet.showForecast(Integer.parseInt(lanci.getText()), Integer.parseInt(deltaT.getText()),
                        boxAntitetica.isSelected(), Integer.parseInt(alpha.getText()));
            } catch (IOException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParseException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if(ae.getActionCommand().equals("Visualizza Assi (x,y)")){
            pChart.setHorizontalAxisTrace(true);
            pChart.setVerticalAxisTrace(true);
        }
        else if(ae.getActionCommand().equals("Nascondi Assi (x,y)")){
            pChart.setHorizontalAxisTrace(false);
            pChart.setVerticalAxisTrace(false);
            pChart.repaint();
        }
        else if(ae.getActionCommand().equals("ON/OFF asse X")){
            pChart.setVerticalAxisTrace(!pChart.getVerticalAxisTrace());
            pChart.repaint();
        }
        else if(ae.getActionCommand().equals("ON/OFF asse Y")){
            pChart.setHorizontalAxisTrace(!pChart.getHorizontalAxisTrace());
            pChart.repaint();
        }
        else if(ae.getActionCommand().equals("ON/OFF zoom grafico")){
            zoomChart = !zoomChart;
            pChart.setMouseWheelEnabled(zoomChart);
            pChart.setDomainZoomable(zoomChart);
            pChart.setRangeZoomable(zoomChart);
        }
        else if(ae.getActionCommand().equals("Info")){
            JOptionPane.showMessageDialog(null, "Software sviluppato da Andrea Reale !"
                    + "\nOgni diritto sul software è da ritenersi proprietà dello sviluppatore.");
        }
        else if(ae.getActionCommand().equals("Elenco Stock Cercati")){
            try {
                ShowTrace sh = new ShowTrace();
                sh.showChart(false);
                sh.showParametri(false);
                sh.setSize(300, 400);
                sh.setAlwaysOnTop(true);
                ArrayList<String> msg = this.dataBase.getArray();
                sh.addReport("***************************");
                sh.addReport("\nElenco Stock Ricercati");
                sh.addReport("\n***************************");
                for(int i = 0; i<msg.size(); i++) sh.addReport("\n"+i+") "+msg.get(i));
                sh.setVisible(true);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedLookAndFeelException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if(ae.getActionCommand().equals("Chiudi")){
            System.exit(0);
        }
        else if(ae.getActionCommand().equals("Yahoo Finance")){
            Desktop desk = Desktop.getDesktop();
            try {
                desk.browse(new URI("https://it.finance.yahoo.com/"));
            } catch (IOException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            } catch (URISyntaxException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if(ae.getActionCommand().endsWith("Calcolatrice")){
            Runtime run = Runtime.getRuntime();   
            try {
                run.exec("calc");
            } catch (IOException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    private void searchStock(){
        dataBase.addSymbol(ricerca.getText());
        autoSuggestor.setDictionary(dataBase.getArray());
        try {
            stock = YahooFinance.get(ricerca.getText());
            this.autoSuggestor.getAutoSuggestionPopUpWindow().setVisible(false);
            this.stockSelected = this.ricerca.getText();
            setStockDetail();
            if(stock.getName().equals("N/A")){
                JOptionPane.showMessageDialog(null, "Simbolo non trovato riprovare !","Errore", JOptionPane.ERROR_MESSAGE);
                stockSelected = "";
            }
        } catch (IOException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    void setChart() throws IOException, ParseException{
        JFreeChart chart = null;
        
                
        try {
            dataSet.setStock(stockSelected);
        } catch (IOException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        dataSet.setCalendar(getDate(modelStart), getDate(modelEnd));
        try {
                    
            chart = ChartFactory.createTimeSeriesChart(stock.getName(), "Data", "Prezzo",dataSet.getCollection());
            if(pChart == null) pChart = new ChartPanel(chart);
            else {
                //System.out.println("else pChart");
                pChart.setChart(chart);
            }

        } catch (IOException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        XYPlot plot = chart.getXYPlot();
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("dd-MM-yyyy")); 
        
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setBaseShapesVisible(true);
        NumberFormat currency = NumberFormat.getCurrencyInstance();
        currency.setMaximumFractionDigits(0);
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setNumberFormatOverride(currency);
        
        pChart.updateUI();
        pChart.revalidate();
        pChart.repaint();
        this.revalidate();
        this.repaint();
        
    }
    @Override
    public void run() {
        while(true){
            try {
               
                Thread.sleep(10000);
                if(stockSelected != "") stock = YahooFinance.get(stockSelected);
                if(stock!=null)
                    if(!stock.getName().equals("N/A")){
                        setStockDetail();
                        
                    }
            } catch (InterruptedException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        
    }

    @Override
    public void mousePressed(MouseEvent me) {
        /*if(){
            JButton b = (JButton) datePickerStart.getComponent(1);
            
            b.doClick();
        }
        datePanelEnd.setVisible(false);
        datePanelEnd.revalidate();
        datePanelEnd.repaint();
        datePanelEndSim.setVisible(false);
        datePanelEndSim.revalidate();
        datePanelEndSim.repaint();*/
        //getContentPane().revalidate();
        //getContentPane().repaint();
        
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseEntered(MouseEvent me) {
       // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(MouseEvent me) {
       // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private class KeySearch implements KeyListener{

        @Override
        public void keyTyped(KeyEvent ke) {
            if(ke.getKeyChar() == KeyEvent.VK_ENTER){
                searchStock();
                ricerca.requestFocus();
                try {
                    setChart();
                } catch (IOException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ParseException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        }

        @Override
        public void keyPressed(KeyEvent ke) {
        }
        @Override
        public void keyReleased(KeyEvent ke) {
        }
   
   }
}
