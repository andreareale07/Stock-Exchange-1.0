/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stock_exchange;

import java.awt.Color;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.UnsupportedLookAndFeelException;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

/**
 *
 * @author Andrea
 */
public class StockExchange {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
       // Stock stock = YahooFinance.get("GOOG");
        //System.out.println("name : "+stock.getName());
        //stock.print();
        //provaMenu();
        try {
            MainWindow m = new MainWindow();
            //  List<HistoricalQuote> storico = stock.getHistory();
            // stock.print();
            //  for(HistoricalQuote c : storico) System.out.println(""+c.toString());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(StockExchange.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(StockExchange.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(StockExchange.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(StockExchange.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private static void provaMenu(){
        DatabaseStock db = new DatabaseStock();
        JTextField f = new JTextField(10);
        f.setSize(100, 20);
        f.setLocation(150,50);
        JFrame frame = new JFrame();
        frame.setLayout(null);
        frame.setSize(400, 200);
        AutoSuggestor autoSuggestor = new AutoSuggestor(f, frame, db.getArray(), Color.WHITE, Color.BLACK, Color.RED, 1.0f);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(f);
        frame.show();
    }
    private String buildURI(String simbolo, Date start, Date end) {
		StringBuilder uri = new StringBuilder();
		uri.append("http://ichart.finance.yahoo.com/table.csv");
		uri.append("?s=").append(simbolo);
		uri.append("&a=").append(start.getMonth());
		uri.append("&b=").append(start.getDay());
		uri.append("&c=").append(start.getYear());
		uri.append("&d=").append(end.getMonth());
		uri.append("&e=").append(end.getDay());
		uri.append("&f=").append(end.getYear());
		uri.append("&g=d");
 
		return uri.toString();
	}
    
}
