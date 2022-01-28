/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stock_exchange;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author Andrea
 */
public class ShowTrace {
    private JFrame frame;
    private JFreeChart chart;
    private XYDataset dataset;
    private TimeSeriesCollection timeCollection;
    private ChartPanel pChart;
    private JPanel pMain, pParametri;
    private JLabel lLanci, lDeltaT, lVolatilita, lTassoCrescita, lS0, lT0;
    private JTextArea report; 
    private JSplitPane corpo;
    private JScrollPane scroll,scrollChart;
    
    public ShowTrace() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException{
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        this.frame = new JFrame();
        pMain = new JPanel(new BorderLayout());
        FlowLayout fl = new FlowLayout();
        fl.setAlignment(FlowLayout.CENTER);
        pParametri = new JPanel(fl);
        timeCollection = new TimeSeriesCollection();
        dataset = timeCollection;
        chart = ChartFactory.createTimeSeriesChart("", "Data", "Prezzo",dataset);
        pChart = new ChartPanel(chart);
        scrollChart = new JScrollPane(pChart);
        
        lLanci = new JLabel("Numero Lanci :");
        lS0 = new JLabel("s0 :");
        lT0 = new JLabel("t0 :");
        lDeltaT = new JLabel("DeltaT :");
        lVolatilita = new JLabel("Volatilita :");
        lTassoCrescita = new JLabel("Tasso di Crescita :");
        
        report = new JTextArea();
        //report.setPreferredSize(new Dimension(150,600));
        report.setEditable(false);
        //report.setEnabled(true);
        report.setAutoscrolls(false);
        scroll = new JScrollPane(report);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        corpo = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        corpo.setDividerLocation(715);
        
        corpo.add(scrollChart);
        corpo.add(scroll);
                
        pParametri.add(lLanci);
        pParametri.add(lS0);
        pParametri.add(lT0);
        pParametri.add(lDeltaT);
        pParametri.add(lTassoCrescita);
        pParametri.add(lVolatilita);
        
        XYPlot plot = chart.getXYPlot();
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("dd-MM-yyyy")); 
        
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setBaseShapesVisible(true);
        NumberFormat currency = NumberFormat.getCurrencyInstance();
        currency.setMaximumFractionDigits(0);
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setNumberFormatOverride(currency);
        this.setZoom(true);
        
        pMain.add(pParametri, BorderLayout.PAGE_START);
        pMain.add(corpo, BorderLayout.CENTER);
        this.frame.getContentPane().add(pMain);
        this.frame.setSize(1100, 600);
        this.frame.setVisible(false);
        this.frame.setLocationRelativeTo(null);
        
    }
    public void setAlwaysOnTop(boolean b){
        this.frame.setAlwaysOnTop(b);
    }
    public void setSize(int x, int y){
        this.frame.setSize(x, y);
        this.frame.setLocationRelativeTo(null);
        this.frame.revalidate();
        this.frame.repaint();
    }
    public void setZoom(boolean zoom){
        pChart.setMouseWheelEnabled(zoom);
        pChart.setDomainZoomable(zoom);
        pChart.setRangeZoomable(zoom);
    }
    /**
     *
     * @param b
     * Abilita o disabilita la visualizzazione del pannello 
     * che visualizza variabilità, tasso di crescita, deltaT ecc..
     * True = visualizza, False = nascondi.
     */
    public void showParametri(boolean b){
        this.pParametri.setVisible(b);
        pMain.revalidate();
        pMain.repaint();
    }
    /**
     * 
     * @param b
     * Mostra o nasconde il pannello che contiene il chart.
     */
    public void showChart(boolean b){
        this.scrollChart.setVisible(b);
        this.corpo.revalidate();
        this.corpo.repaint();
    }
    /**
     *
     * @param l numero lanci() 
     * @param s0 prezzo al tempo zero
     * @param t0 tempo tzero 
     * @param d deltaT 
     * @param tasso tasso di crescita
     * @param vol volatilit&agrave; 
     * Consente di settare la visualizzazione dei parametri.
     * Il pannello che visualizza i paramentri 
     * (lanci, prezzo S0, tempo t0, tasso crescita, volatilia)
     * verrà aggiornato con i valori passati a questo metodo.
     */
    public void setParametri(int l, double s0, String t0,int d, double tasso, double vol){
        this.lLanci.setText("Numero Lanci : "+l);
        this.lS0.setText("| s0 : "+s0);
        this.lT0.setText("| t0 : "+t0);
        this.lDeltaT.setText("| DeltaT : "+d);
        //String s = ""+tasso;
        //s = s.substring(0, 7);
        this.lTassoCrescita.setText("| Tasso di Crescita : "+tasso);
        //s = ""+vol;
        //s = s.substring(0, 7);
        this.lVolatilita.setText("| Volatilita : "+vol);
        this.chart.removeLegend();
        this.pParametri.revalidate();
        this.pParametri.repaint();
        
    }

    /**
     *
     * @param time
     * Aggiunge una TimeSeries al Chart della finestra
     */
    public void addSerie(TimeSeries time){
        timeCollection.addSeries(time);
        pChart.revalidate();
        pChart.repaint();
        this.frame.revalidate();
        this.frame.repaint();
    }

    /**
     *
     * @param s
     * Aggiunge la stringa s passata al metodo all'area di testo 
     * aggiornando testo contenuto al suo interno.
     */
    public void addReport(String s){
        this.report.append(s);
        report.revalidate();
        report.repaint();
        this.scroll.revalidate();
        this.scroll.repaint();
        
    }
    
    /**
     * 
     * @param b 
     * Mostra o nasconde il componente in base al volere booleano 
     * presente nel parametro b. 
     */
    public void setVisible(boolean b){
        this.frame.setVisible(b);
    }
}
