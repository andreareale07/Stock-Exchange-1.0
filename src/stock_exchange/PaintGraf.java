/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stock_exchange;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Andrea
 */
public class PaintGraf extends JPanel {
    JLabel left, down;
    String xWord, yWord;
    int rangeX, rangeY, width, height, posX, posY; 
    public PaintGraf(int w, int h){
        this.width = w;
        this.height = h;
        this.setBackground(new Color(255,255,255,100));
        
    }
    
    void setBounds(int x,int y){
        this.setBounds(x,y,width, height);
    }
    
    public void paintComponent (Graphics g){
        super.paintComponent(g);
            
        int w = getWidth();
        int h = getHeight();
        g.drawRect(0, w, 0, h);
        g.drawLine(50, 50, 50, h-20);
        g.drawLine(10, h-50, w-50, h-50);
    
    }
    
}
