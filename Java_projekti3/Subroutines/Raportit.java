package sairaalatietokanta;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Raportit extends javax.swing.JFrame {

    
    public Raportit() {
        initComponents();
        Connect();
        Automaattitunniste();
        LataaTohtori();
        LataaPotilaat();
        raportti_taulukko();
    }

    Connection con;
    PreparedStatement pst;
    ResultSet rs;
    
    public class Tohtori{
        
        String tunniste;
        String nimi;
        
        public Tohtori(String ptunniste, String pnimi){
            
            this.tunniste = ptunniste;
            this.nimi = pnimi;
                    
            
        }
        
        public String toString()
        {   
            return nimi;
        }    
    }
    
    public class Potilaat{
        
        String tunniste;
        String nimi;
        
        public Potilaat(String ptunniste, String pnimi){
            
            this.tunniste = ptunniste;
            this.nimi = pnimi;
                    
            
        }
        
        public String toString()
        {   
            return nimi;
        }    
    }
    
    public void LataaPotilaat()//Lataa tiedot MySQL-tietokannasta potilastaulukosta
    {
        try {
            pst = con.prepareStatement("select * from potilastaulukko");
            rs = pst.executeQuery();
            txtPotilasnimi.removeAll();
            
            while(rs.next())
            {
                txtPotilasnimi.addItem(new Potilaat(rs.getString(1),rs.getString(2)));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Raportit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public void LataaTohtori()//Lataa tiedot MySQL-tietokannasta tohtoritaulukosta
    {
        try {
            pst = con.prepareStatement("select * from tohtoritaulukko");
            rs = pst.executeQuery();
            txtTohtorinimi.removeAll();
            
            while(rs.next())
            {
                txtTohtorinimi.addItem(new Tohtori(rs.getString(1),rs.getString(2)));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Raportit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void Connect(){ //Yhteysrajapinta on Statement-, PreparedStatement- ja DatabaseMetaData-tehdas, eli Connection-objektin avulla voidaan 	
                            //saada Statement- ja DatabaseMetaData-objektit. Yhteysliittymä tarjoaa monia menetelmiä tapahtumien hallintaan, 		
                            //kuten commit(), rollback(), setAutoCommit(), setTransactionIsolation() jne. 
        try{
            /*Java.lang.Class-luokan menetelmää forName() 
    käytetään tämän luokan esiintymän saamiseksi määritetyllä luokan nimellä.*/
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost/terveystietokanta","root","SALASANA");
        
        }catch (ClassNotFoundException ex){
           Logger.getLogger(Raportit.class.getName()).log(Level.SEVERE,null, ex);
        }catch (SQLException ex){
           Logger.getLogger(Raportit.class.getName()).log(Level.SEVERE,null, ex);
        }
    }
    
    public void Automaattitunniste(){
        
        try {
            Statement s = con.createStatement(); // Tämä kanssa tuli piti tehdä niin, että käydä itse lisäämässä kirjasto: java.sql.Statement
            rs = s.executeQuery("SELECT MAX(raporttinumero) FROM raporttitaulukko"); //Valitaan potilasnumero potilastaulukosta
            rs.next();
            rs.getString("MAX(raporttinumero)");
            
            if(rs.getString("MAX(raporttinumero)")== null){
              lblRnum.setText("RN001");
              
            }
            else{
                
                Long id = Long.parseLong(rs.getString("MAX(raporttinumero)").substring(2,rs.getString("MAX(raporttinumero)").length()));
                id++;
                
                lblRnum.setText("RN"+ String.format("%03d", id));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Raportit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void raportti_taulukko(){
        try {
            pst = con.prepareStatement("SELECT * from raporttitaulukko");
            rs = pst.executeQuery();
            ResultSetMetaData Rsm = rs.getMetaData(); 
            int c;
            c = Rsm.getColumnCount();
            
            DefaultTableModel df = (DefaultTableModel)taulukko1.getModel();
            df.setRowCount(0);
            
            while(rs.next()){
                Vector v2 = new Vector();
                
                for(int i = 1; i<= c; i++){
                
                v2.add(rs.getString("raporttinumero"));
                v2.add(rs.getString("tohtorinnimi"));  
                v2.add(rs.getString("potilaannimi")); 
                v2.add(rs.getString("huonenumero")); 
                v2.add(rs.getString("pvm")); 
                
                }
                df.addRow(v2);
            }
        } catch (SQLException ex) {
            Logger.getLogger(sairaalatietokanta.Raportit.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }
	
	/*----------
	|btnLuo   |
	|           |
	------------*/{
		String ptunniste = lblRnum.getText();
        Tohtori t = (Tohtori)txtTohtorinimi.getSelectedItem();
        Potilaat p = (Potilaat)txtPotilasnimi.getSelectedItem(); 
        String phuone =  txtHnro.getValue().toString();
        
        SimpleDateFormat kalenteri = new SimpleDateFormat("dd-MM-yyyy");
        String pvm = kalenteri.format(pvmRaportti.getDate());
        
        try {
            pst = con.prepareStatement("INSERT INTO raporttitaulukko(raporttinumero, tohtorinnimi, potilaannimi, huonenumero, pvm) VALUES(?, ?, ?, ?, ?)");
            pst.setString(1, ptunniste);
            pst.setString(2, t.nimi);
            pst.setString(3, p.nimi);
            pst.setString(4, phuone);
            pst.setString(5, pvm);
            pst.executeUpdate();
            
            JOptionPane.showMessageDialog(this,"Uusi raportti on lisätty onnistuneesti! ");
            
            Automaattitunniste();
            
           
            txtTohtorinimi.setSelectedIndex(-1);
            txtPotilasnimi.setSelectedIndex(-1);
            txtHnro.setValue(0);
            lblRnum.requestFocus();
            
            raportti_taulukko();
                    
        } catch (SQLException ex) {
            Logger.getLogger(Raportit.class.getName()).log(Level.SEVERE, null, ex);
        }	
	}
	/*------------------------
	|taulukko1MouseClicked   |
	|                        |
	--------------------------*/{
		 DefaultTableModel d1 = (DefaultTableModel)taulukko1.getModel();
        int SelectIndex = taulukko1.getSelectedRow();
        
        lblRnum.setText(d1.getValueAt(SelectIndex, 0).toString());
        txtTohtorinimi.setSelectedItem(d1.getValueAt(SelectIndex, 1).toString());
        txtPotilasnimi.setSelectedItem(d1.getValueAt(SelectIndex, 2).toString());
        txtHnro.setValue(Integer.parseInt(d1.getValueAt(SelectIndex, 3).toString()));
        btnLuo.setEnabled(false);// Kun hiirellä klikkaa taulukossa ole tietoa, Lisää nappi menee false tilaan  
		
	}
	
	/*-------------
	|btnPoista    |
	|             |
	---------------*/{
		
		String ptunniste = lblRnum.getText();
       
        try {
            pst = con.prepareStatement("DELETE FROM raporttitaulukko WHERE raporttinumero = ?");
           
            pst.setString(1, ptunniste);
            
            pst.executeUpdate();
            
            JOptionPane.showMessageDialog(this,"Raportti on poistettu! ");
            
            Automaattitunniste();    
            
            txtTohtorinimi.setSelectedIndex(-1);
            txtPotilasnimi.setSelectedIndex(-1);
            txtHnro.setValue(0); 
            raportti_taulukko();
            btnLuo.setEnabled(true);
                    
        } catch (SQLException ex) {
            Logger.getLogger(Raportit.class.getName()).log(Level.SEVERE, null, ex);
        }
        
	}
	
	/*-------------
	|btnSulje     |
	|             |
	---------------*/{
	
	this.setVisible(false);
	
	}