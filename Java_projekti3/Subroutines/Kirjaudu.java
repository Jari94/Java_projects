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

public class Kirjaudu extends javax.swing.JFrame {


    public Kirjaudu() {
        initComponents();
        Connect();
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
           Logger.getLogger(Kirjaudu.class.getName()).log(Level.SEVERE,null, ex);
        }catch (SQLException ex){
           Logger.getLogger(Kirjaudu.class.getName()).log(Level.SEVERE,null, ex);
        }
    }
	
	/*------------ 
	|btnKirjaudu |
	|		     |	
	-------------*/{
		
		 String ptunnus = txtTunnus.getText();
        String psalasana = txtSalasana.getText();  
        String ptyyppi = cmbTyyppi.getSelectedItem().toString();
        
        
         try {
            pst = con.prepareStatement("SELECT * FROM sairaalataulukko WHERE tunnus = ? AND salasana = ? AND tyyppi = ?");
            pst.setString(1, ptunnus);
            pst.setString(2, psalasana);
            pst.setString(3, ptyyppi);
            
            rs = pst.executeQuery();
            
            if(rs.next()){
                
                String knimi = rs.getString("nimi");
                this.setVisible(false);
                new terveystietotaulukko(knimi,ptunnus,ptyyppi).setVisible(true);
                
            }
            else{
                JOptionPane.showMessageDialog(this,"Tunnus tai salasana on virheellinen! "); 
                txtTunnus.setText("");
                txtSalasana.setText("");
                cmbTyyppi.setSelectedIndex(-1);
                txtTunnus.requestFocus();
            }
               
        } catch (SQLException ex) {
            Logger.getLogger(Kirjaudu.class.getName()).log(Level.SEVERE, null, ex);
        }
	}
	
	/*------------ 
	|btnSulje    |
	|		     |	
	-------------*/{
		
		this.setVisible(false);
	
	}