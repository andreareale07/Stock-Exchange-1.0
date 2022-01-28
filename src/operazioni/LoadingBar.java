/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operazioni;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

/**
 *
 * @author Andrea
 */
public class LoadingBar extends JFrame implements Runnable{
    int numOperazioni;
    int operazioniCompletate;
    private JProgressBar bar;
    public LoadingBar(int numOperazioni, String msg){
        this.numOperazioni = numOperazioni;
        this.operazioniCompletate = 0;
        bar = new JProgressBar(0, this.numOperazioni);
        bar.setValue(0);
        bar.setStringPainted(true);
        
        this.setTitle(msg);
        this.getContentPane().add(bar);
        this.setSize(300, 60);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
    public void setOperation(int num){
        this.operazioniCompletate = num;
    }
    @Override
    public void run() {
        while(this.operazioniCompletate < this.numOperazioni){
            this.bar.setValue(this.operazioniCompletate);
        }
        
        this.dispose();
        
    }
    
}
