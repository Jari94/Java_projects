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
import javax.swing.JFrame;
import javax.swing.JTextField;

public class Kayttaja extends javax.swing.JFrame {


    public Kayttaja() {
        initComponents();       
    }

    Connection con;
    PreparedStatement pst;
    ResultSet rs;
    
    public void Connect(){ //Yhteysrajapinta on Statement-, PreparedStatement- ja DatabaseMetaData-tehdas, eli Connection-objektin avulla voidaan 	
                            //saada Statement- ja DatabaseMetaData-objektit. Yhteysliittymä tarjoaa monia menetelmiä tapahtumien hallintaan, 		
                            //kuten commit(), rollback(), setAutoCommit(), setTransactionIsolation() jne. 
        try{
            /*Java.lang.Class-luokan menetelmää forName() 
    käytetään tämän luokan esiintymän saamiseksi määritetyllä luokan nimellä.*/
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost/terveystietokanta","root","SALASANA");
        
        }catch (ClassNotFoundException ex){
           Logger.getLogger(Kayttaja.class.getName()).log(Level.SEVERE,null, ex);
        }catch (SQLException ex){
           Logger.getLogger(Kayttaja.class.getName()).log(Level.SEVERE,null, ex);
        }
    }
	
	/*---------
	|btnLuonti |	
	|		   | 	
	-----------*/{
		
		String pnimi = txtNimi.getText();
        String ptunnus = txtTunnus.getText();
        String psalasana = txtSalasana.getText();  
        String ptyyppi = cmbTyyppi.getSelectedItem().toString();
        
        Connect();
        try {
            pst = con.prepareStatement("INSERT INTO sairaalataulukko(nimi, tunnus, salasana, tyyppi) VALUES(?, ?, ?, ?)");
            
            pst.setString(1, pnimi);
            pst.setString(2, ptunnus);
            pst.setString(3, psalasana);
            pst.setString(4, ptyyppi);
            pst.executeUpdate();
            
            JOptionPane.showMessageDialog(this,"Tiedot on syötetty onnistuneesti! ");
            
            txtNimi.setText("");
            txtTunnus.setText("");
            txtSalasana.setText("");
            cmbTyyppi.setSelectedIndex(-1);
            txtNimi.requestFocus();
            
        } catch (SQLException ex) {
            Logger.getLogger(Kayttaja.class.getName()).log(Level.SEVERE, null, ex);
        }
		
	}
	
	/*---------
	|btnPeruuta|	
	|		   | 	
	-----------*/{
		JFrame frame = new JFrame("Sulje ohjelma"); 
        //Ponnahdus ikkunassa kysytään, että haluatko todellakin sulkea ohjelman, voidaan vastata YES tai NO. NO ei sulje ohjelmaa, YES sulkee ohjelman
        if (JOptionPane.showConfirmDialog(frame, "Haluatko Sulkea ohjelman, ohjelma ei tallenna tietoja silloin?","MySQL yhteys", //ConfirmDialogilla vaarmistaa käyttäjän valinnan
                JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) //optionType vahvistaa käyttäjän valinnan
        {
		this.setVisible(false);
		
	}