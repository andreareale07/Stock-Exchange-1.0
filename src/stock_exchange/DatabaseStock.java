/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stock_exchange;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

/**
 *
 * @author Andrea
 */
public class DatabaseStock {
    ArrayList<Symbol> simboli;
    String tmp;
    boolean statusSave = false;
    public DatabaseStock(){
        simboli = new ArrayList<Symbol>();
        load();
        for(Symbol s : simboli) System.out.println("nome : "+s.getSymbol());
    }
    ArrayList<String> getArray(){
        ArrayList<String> tmp = new ArrayList<String>();
        for(Symbol s : simboli) tmp.add(s.getSymbol());
        return tmp;
    }
/***********AGGIUNGE UN SIMBOLO VALIDO AL DATABASE*****************/
    boolean addSymbol(String s){
        s = s.toUpperCase();
        try {
            Stock stock = YahooFinance.get(""+s+"");
            if(!stock.getName().equals("N/A") && checkExistSymbol(s)){
                simboli.add(new Symbol(s));
                save();
                return true;
            }
        } catch (IOException ex) {}
        
        return false;
    }
/*****************FINE ADDSYMBOL************************************/
/*********CONTROLLA SE UNA STRINGA E' GIA' PRESENTE NELL DATABASE***/
    boolean checkExistSymbol(String s){
        for(Symbol tmp : simboli) if(tmp.getSymbol().equals(s)) return false;
        return true;
    }
/*********************FINE CONTROLLO********************************/
/***************SALVA SU FILE L'ARRAY LIST CONTENENTE IL DATABASE DEI SYMBOL***/
    protected void save(){
        try {
            try (ObjectOutputStream OUT = new ObjectOutputStream(new FileOutputStream("symbol.dat"))) {
                OUT.writeObject(simboli);
                OUT.flush();
            }
        }
        catch(Exception e){
        }
    }
 /*******************FINE SAVE*************************************************/
 /***************CARICA DA FILE L'ARRAY LIST CONTENENTE IL DATABASE DEI SYMBOL***/
    protected void load(){
        try {
            ObjectInputStream IN = new ObjectInputStream(new FileInputStream("symbol.dat"));
            simboli=(ArrayList<Symbol>) IN.readObject();  //CAST su array
            IN.close();
        }
        catch(Exception e){
        }
    }
 /*******************FINE LOAD*************************************************/
    
}
