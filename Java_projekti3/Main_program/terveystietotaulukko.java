package sairaalatietokanta;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;

public class terveystietotaulukko extends javax.swing.JFrame {

 
    public terveystietotaulukko() {
        initComponents();
    }
    
    String TohtoriTunniste;
    String Kctyyppi;
    String uusiTunniste; 
    String ptunnus;
    String Ktyyppi;
    
    
    
    public terveystietotaulukko(String tunniste, String tunnus, String tyyppi) {
        initComponents();
        
        this.ptunnus = tunnus;
        lblKayttajatunnus.setText(ptunnus);
        this.Ktyyppi = tyyppi;
        lblKayttajatyyppi.setText(Ktyyppi);
       
        this.Kctyyppi = Ktyyppi;
        this.uusiTunniste = tunniste;
        
        TohtoriTunniste = uusiTunniste;
        
        Kctyyppi = lblKayttajatyyppi.getText();
        
        if(Kctyyppi.equals("Lääkäri")){
            
            btnPotilaat.setVisible(true);
            btnTohtorit.setVisible(true);
            btnLuoRaportti.setVisible(true);
            btnTarkista.setVisible(true);
            btnApuri.setVisible(false);
            btnKohde.setVisible(false);
            btnUusitunnus.setVisible(false);
            btnTohtoriTarkistus.setVisible(true);
            btnKirjauduUlos.setVisible(true);
        }
        else if(Kctyyppi.equals("Vastaanottoapulainen")){
            
            btnPotilaat.setVisible(false);
            btnTohtorit.setVisible(false);
            btnLuoRaportti.setVisible(false);
            btnTarkista.setVisible(false);
            btnApuri.setVisible(true);
            btnKohde.setVisible(true);
            btnUusitunnus.setVisible(false);
            btnTohtoriTarkistus.setVisible(true);
            btnKirjauduUlos.setVisible(true);
            
        }
        
        else if(Kctyyppi.equals("Farmaseutti")){
            
            btnPotilaat.setVisible(false);
            btnTohtorit.setVisible(false);
            btnLuoRaportti.setVisible(false);
            btnTarkista.setVisible(false);
            btnApuri.setVisible(true);
            btnKohde.setVisible(true);
            btnUusitunnus.setVisible(false);
            btnTohtoriTarkistus.setVisible(true);
            btnKirjauduUlos.setVisible(true);
            
        }
        
    }
	
	
	/*------------
	|btnUusiTunnus| 
	|			  | 	
	--------------*/{
		Kayttaja k = new Kayttaja();// kutsuu aliohjelman Kayttajan
        
        k.setVisible(true);
		
	}
	
	/*------------
	|btnPotilaat  | 
	|			  | 	
	--------------*/{
		Potilaat p = new Potilaat(); //kutsuu aliohjelman Potilaat
        p.setVisible(true);
		
	}
	/*------------
	|btnTohtorit  | 
	|			  | 	
	--------------*/{
	if(Kctyyppi.equals("Lääkäri")){ //Jos käyttäjätyyppinä on valittu "Lääkäri"
            
           Tohtori t = new Tohtori(); //kutsuu aliohjelman Tohtori
           t.setVisible(true);
        }
		
	
	
	/*------------
	|btnTohtorit  | 
	|			  | 	
	--------------*/{
		Raportit r = new Raportit();//kutsuu aliohjelman Raportit
        r.setVisible(true);
        
		
	}
	
	
	
	
	
	
	
	
	