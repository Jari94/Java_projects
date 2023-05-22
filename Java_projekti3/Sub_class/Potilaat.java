package sairaalatietokanta;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


public class Potilaat extends javax.swing.JFrame {

 
    public Potilaat() {
        initComponents();
        Connect();
        Automaattitunniste();
        potilas_taulukko();
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
           Logger.getLogger(Potilaat.class.getName()).log(Level.SEVERE,null, ex);
        }catch (SQLException ex){
           Logger.getLogger(Potilaat.class.getName()).log(Level.SEVERE,null, ex);
        }
    }
    
    public void Automaattitunniste(){
        
        try {
            Statement s = con.createStatement(); 
            rs = s.executeQuery("SELECT MAX(potilasnumero) FROM potilastaulukko"); //Valitaan potilasnumero potilastaulukosta
            rs.next();
            rs.getString("MAX(potilasnumero)");
            
            if(rs.getString("MAX(potilasnumero)")== null){
              lblNumero.setText("PN001");
              
            }
            else{
                
                Long id = Long.parseLong(rs.getString("MAX(potilasnumero)").substring(2,rs.getString("MAX(potilasnumero)").length()));
                id++;
                
                lblNumero.setText("PN"+ String.format("%03d", id));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Potilaat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void potilas_taulukko(){
        try {
            pst = con.prepareStatement("SELECT * from potilastaulukko");
            rs = pst.executeQuery();
            ResultSetMetaData Rsm = rs.getMetaData(); 
            int c;
            c = Rsm.getColumnCount();
            
            DefaultTableModel df = (DefaultTableModel)taulukko1.getModel();
            df.setRowCount(0);
            
            while(rs.next()){
                Vector v2 = new Vector();
                
                for(int i = 1; i<= c; i++){
                
                v2.add(rs.getString("potilasnumero"));
                v2.add(rs.getString("nimi"));
                v2.add(rs.getString("puhelin"));
                v2.add(rs.getString("osoite"));     
                }
                df.addRow(v2);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Potilaat.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }
	
	/*----------
	|btnLisaa   |
	|           |
	------------*/{
		String pnumero = lblNumero.getText();
        String pnimi = txtNimi.getText();
        String ppuhelin = txtPuhelin.getText();  
        String ptiedot = txtOsoite.getText();
        
        
        try {
            pst = con.prepareStatement("INSERT INTO potilastaulukko(potilasnumero, nimi, puhelin, osoite) VALUES(?, ?, ?, ?)");
            
            pst.setString(1, pnumero);
            pst.setString(2, pnimi);
            pst.setString(3, ppuhelin);
            pst.setString(4, ptiedot);
            pst.executeUpdate();
            
            JOptionPane.showMessageDialog(this,"Potilaan tiedot on lisätty! ");
            
            Automaattitunniste();
            
            txtNimi.setText("");
            txtPuhelin.setText("");
            txtOsoite.setText("");
            txtNimi.requestFocus();
            potilas_taulukko();
                    
        } catch (SQLException ex) {
            Logger.getLogger(Potilaat.class.getName()).log(Level.SEVERE, null, ex);
        }
		
	}
	
	/*------------------------
	|taulukko1MouseClicked   |
	|                        |
	--------------------------*/{
		DefaultTableModel d1 = (DefaultTableModel)taulukko1.getModel();
        int SelectIndex = taulukko1.getSelectedRow();
        
        lblNumero.setText(d1.getValueAt(SelectIndex, 0).toString());
        txtNimi.setText(d1.getValueAt(SelectIndex, 1).toString());
        txtPuhelin.setText(d1.getValueAt(SelectIndex, 2).toString());
        txtOsoite.setText(d1.getValueAt(SelectIndex, 3).toString());
        
        btnLisaa.setEnabled(false);// Kun hiirellä klikkaa taulukossa ole tietoa, 
								   //Lisää nappi menee false tilaan
		
	}
	
	/*-------------
	|btnPaivita   |
	|             |
	---------------*/{
		
		String pnimi = txtNimi.getText();
        String ppuhelin = txtPuhelin.getText();  
        String ptiedot = txtOsoite.getText();
        String pnumero = lblNumero.getText();
        
        try {
            pst = con.prepareStatement("UPDATE potilastaulukko SET nimi = ? , puhelin = ? , osoite = ? WHERE potilasnumero = ?");
                        
            pst.setString(1, pnimi);
            pst.setString(2, ppuhelin);
            pst.setString(3, ptiedot);
            pst.setString(4, pnumero);
            pst.executeUpdate();
            
            JOptionPane.showMessageDialog(this,"Potilaan tiedot on päivitetty onnistuneesti! ");
            
            Automaattitunniste();
            
            txtNimi.setText("");
            txtPuhelin.setText("");
            txtOsoite.setText("");
            txtNimi.requestFocus();
            potilas_taulukko();
            btnLisaa.setEnabled(true);
            
        } catch (SQLException ex) {
            Logger.getLogger(Potilaat.class.getName()).log(Level.SEVERE, null, ex);
        }
		
	}
	/*-------------
	|btnPoista    |
	|             |
	---------------*/{
		
		String pnumero = lblNumero.getText();
        
        try {
            pst = con.prepareStatement("DELETE FROM potilastaulukko WHERE potilasnumero = ?");
                        
       
            pst.setString(1, pnumero);
            
            pst.executeUpdate();
            
            JOptionPane.showMessageDialog(this,"Potilaan tiedot on poistettu onnistuneesti! ");
            
            Automaattitunniste();
            
            txtNimi.setText("");
            txtPuhelin.setText("");
            txtOsoite.setText("");
            potilas_taulukko();
            btnLisaa.setEnabled(true);
            
        } catch (SQLException ex) {
            Logger.getLogger(Potilaat.class.getName()).log(Level.SEVERE, null, ex);
        }	
	}
	
	/*-------------
	|btnSulje     |
	|             |
	---------------*/{
	
	this.setVisible(false);
	
	}
	