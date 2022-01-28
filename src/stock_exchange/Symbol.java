/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stock_exchange;

import java.io.Serializable;

/**
 *
 * @author Andrea
 */
public class Symbol implements Serializable {
    
    String s;
    public Symbol(String a){
        s = a;
    }
    
    public String getSymbol(){
        return s;
    }  
    public void setSymbol(String a){
        this.s = a;
    }
    
}
