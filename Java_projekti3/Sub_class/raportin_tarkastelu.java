package sairaalatietokanta;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.*;
import java.sql.Statement;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


public class Raportin_tarkastelu extends javax.swing.JFrame {

   
    public Raportin_tarkastelu() {
        initComponents();
         Connect();
      
        raportti_taulukko();
    }
    
    Connection con;
    PreparedStatement pst;
    ResultSet rs;
    
   String tunniste;
   String uusiTunniste;
   
   
   
    public Raportin_tarkastelu(String tunniste) {
        initComponents();
       
        this.tunniste = tunniste;
        
        uusiTunniste = tunniste;
        Connect();     
        raportti_taulukko();
    }  
    
    public void Connect(){ //Yhteysrajapinta on Statement-, PreparedStatement- ja DatabaseMetaData-tehdas, eli Connection-objektin avulla voidaan 	
                            //saada Statement- ja DatabaseMetaData-objektit. Yhteysliittymä tarjoaa monia menetelmiä tapahtumien hallintaan, 		
                            //kuten commit(), rollback(), setAutoCommit(), setTransactionIsolation() jne. 
        try{
            /*Java.lang.Class-luokan menetelmää forName() 
    käytetään tämän luokan esiintymän saamiseksi määritetyllä luokan nimellä.*/
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost/terveystietokanta","root","OMASALASANA");
        
        }catch (ClassNotFoundException ex){
           Logger.getLogger(Raportit.class.getName()).log(Level.SEVERE,null, ex);
        }catch (SQLException ex){
           Logger.getLogger(Raportit.class.getName()).log(Level.SEVERE,null, ex);
        }
    }
    
    public void raportti_taulukko(){ //MySQL kyselyillä kutsutaan tarvittavia tietoja tietokannassa olevista taulukoista ja tulostetaan raportin tarkastelu ikkunaan
        try {
            pst = con.prepareStatement("SELECT raporttitaulukko.raporttinumero, tohtoritaulukko.tohtorin_nimi, potilastaulukko.nimi, 
												raporttitaulukko.huonenumero, raporttitaulukko.pvm from tohtoritaulukko INNER JOIN raporttitaulukko 
												ON raporttitaulukko.raporttinumero = 'RN002' and raporttitaulukko.tohtorinnimi = tohtoritaulukko.tohtorin_nimi 
												INNER JOIN potilastaulukko ON raporttitaulukko.tohtorinnimi = tohtoritaulukko.tohtorin_nimi and potilastaulukko.nimi = raporttitaulukko.potilaannimi");
            
           
            rs = pst.executeQuery();
            ResultSetMetaData Rsm = rs.getMetaData(); // Tätäkään ei automaattisesti ehdotettu, joten piti manuaalisesti käydä lisäämässä java.sql.ResultSetMetaData
            int c;
            c = Rsm.getColumnCount();
            
            DefaultTableModel df = (DefaultTableModel)taulukko1.getModel();
            df.setRowCount(0);
            
            while(rs.next()){
                Vector v2 = new Vector();
                
                for(int i = 1; i<= c; i++){
                
                v2.add(rs.getString(1));
                v2.add(rs.getString(2));  
                v2.add(rs.getString(3)); 
                v2.add(rs.getString(4)); 
                v2.add(rs.getString(5)); 
                 
                
                }
                df.addRow(v2);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Raportit.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }
