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


public class Tohtori extends javax.swing.JFrame {

 
    public Tohtori() {
        initComponents();
        Connect();
        Automaattitunniste();
        tohtori_taulukko();
    }
    
    Connection con;
    PreparedStatement pst;
    ResultSet rs;
    
    String tunniste;
    String uusiTunniste;
    
    public Tohtori(String tunniste, String ptunnus) {//Vastaanottaa pääohjelman terveystietotaulukko kyselyyn
        initComponents();
        
        this.tunniste = ptunnus; 
        this.uusiTunniste = tunniste;
        
        JOptionPane.showMessageDialog(this, uusiTunniste);
        
        
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
           Logger.getLogger(Tohtori.class.getName()).log(Level.SEVERE,null, ex);
        }catch (SQLException ex){
           Logger.getLogger(Tohtori.class.getName()).log(Level.SEVERE,null, ex);
        }
    }
    
    public void Automaattitunniste(){
        
        try {
            Statement s = con.createStatement(); // Tämä kanssa tuli piti tehdä niin, että käydä itse lisäämässä kirjasto: java.sql.Statement
            rs = s.executeQuery("SELECT MAX(tohtori_id) FROM tohtoritaulukko"); //Valitaan tohtorin id tohtoritaulukosta
            rs.next();
            rs.getString("MAX(tohtori_id)");
            
            if(rs.getString("MAX(tohtori_id)")== null){
              lblTNumero.setText("LN001");
              
            }
            else{
                
                Long id = Long.parseLong(rs.getString("MAX(tohtori_id)").substring(2,rs.getString("MAX(tohtori_id)").length()));
                id++;
                
                lblTNumero.setText("LN"+ String.format("%03d", id));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Tohtori.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void tohtori_taulukko(){
        try {
            pst = con.prepareStatement("SELECT * from tohtoritaulukko");
            //pst.setString(1, tunniste); WHERE tohtori_id = ?
            rs = pst.executeQuery();
            ResultSetMetaData Rsm = rs.getMetaData(); // Tätäkään ei automaattisesti ehdotettu, joten piti manuaalisesti käydä lisäämässä java.sql.ResultSetMetaData
            int c;
            c = Rsm.getColumnCount();
            
            DefaultTableModel df = (DefaultTableModel)taulukko1.getModel();
            df.setRowCount(0);
            
            while(rs.next()){
                Vector v2 = new Vector();
                
                for(int i = 1; i<= c; i++){
                
                v2.add(rs.getString("tohtori_id"));
                v2.add(rs.getString("tohtorin_nimi"));
                v2.add(rs.getString("erikoisala"));
                v2.add(rs.getString("patevyys"));    
                v2.add(rs.getString("potilaan_numero")); 
                v2.add(rs.getString("puhelin_numero")); 
                v2.add(rs.getString("huoneen_numero")); 
                
                }
                df.addRow(v2);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Tohtori.class.getName()).log(Level.SEVERE, null, ex);
        }  
		
    }
	/*----------
	|btnLisaa   |
	|           |
	------------*/{
		String ptunniste = lblTNumero.getText();
        String pnimi = txtNimi.getText(); 
        String posaaja = txtSpesialisti.getText(); 
        String ppatevyys = txtPatevyys.getText();
        String phoito = txtHoitava.getText();
        String pnumero = txtPuhelin.getText();
        String phuone =  spnHuone.getValue().toString();
        
        try {
            pst = con.prepareStatement("INSERT INTO tohtoritaulukko(tohtori_id, tohtorin_nimi, erikoisala, patevyys, potilaan_numero, puhelin_numero, huoneen_numero) VALUES(?, ?, ?, ?, ?, ?, ?)");
            
            pst.setString(1, ptunniste);
            pst.setString(2, pnimi);
            pst.setString(3, posaaja);
            pst.setString(4, ppatevyys);
            pst.setString(5, phoito);
            pst.setString(6, pnumero);
            pst.setString(7, phuone);
            pst.executeUpdate();
            
            JOptionPane.showMessageDialog(this,"Lääkärin tiedot on lisätty! ");
            
            Automaattitunniste();
            
            txtNimi.setText("");
            txtSpesialisti.setText("");
            txtPatevyys.setText("");
            txtHoitava.setText("");
            txtPuhelin.setText("");
            spnHuone.setValue(0);
            txtNimi.requestFocus();
            tohtori_taulukko();
                    
        } catch (SQLException ex) {
            Logger.getLogger(Tohtori.class.getName()).log(Level.SEVERE, null, ex);
        }
		
	}
	
	/*------------------------
	|taulukko1MouseClicked   |
	|                        |
	--------------------------*/{
		DefaultTableModel d1 = (DefaultTableModel)taulukko1.getModel();
        int SelectIndex = taulukko1.getSelectedRow();
        
        lblTNumero.setText(d1.getValueAt(SelectIndex, 0).toString());
        txtNimi.setText(d1.getValueAt(SelectIndex, 1).toString());
        txtSpesialisti.setText(d1.getValueAt(SelectIndex, 2).toString());
        txtPatevyys.setText(d1.getValueAt(SelectIndex, 3).toString());
        txtHoitava.setText(d1.getValueAt(SelectIndex, 4).toString());
        txtPuhelin.setText(d1.getValueAt(SelectIndex, 5).toString());
        spnHuone.setValue(Integer.parseInt(d1.getValueAt(SelectIndex, 6).toString()));
        
        btnLisaa.setEnabled(false);// Kun hiirellä klikkaa taulukossa ole tietoa, 
								   //Lisää nappi menee false tilaan
		
	}
	
	/*-------------
	|btnPaivita   |
	|             |
	---------------*/{
		
		String ptunniste = lblTNumero.getText();
        String pnimi = txtNimi.getText(); 
        String posaaja = txtSpesialisti.getText(); 
        String ppatevyys = txtPatevyys.getText();
        String phoito = txtHoitava.getText();
        String pnumero = txtPuhelin.getText();
        String phuone =  spnHuone.getValue().toString();
        
        try {
            pst = con.prepareStatement("UPDATE tohtoritaulukko SET tohtorin_nimi = ?, erikoisala = ?, patevyys = ?, potilaan_numero = ?, puhelin_numero = ?, huoneen_numero = ? WHERE tohtori_id = ? ");            
            
            pst.setString(1, ptunniste);
            pst.setString(2, pnimi);
            pst.setString(3, posaaja);
            pst.setString(4, ppatevyys);
            pst.setString(5, phoito);
            pst.setString(6, pnumero);
            pst.setString(7, phuone);
            pst.executeUpdate();
            
            JOptionPane.showMessageDialog(this,"Lääkäri tiedot on päivitetty! ");
            
            Automaattitunniste();
            
            lblTNumero.setText("");
            txtNimi.setText("");
            txtSpesialisti.setText("");
            txtPatevyys.setText("");
            txtHoitava.setText("");
            txtPuhelin.setText("");
            spnHuone.setValue(0);
            lblTNumero.requestFocus();
            tohtori_taulukko();
            btnLisaa.setEnabled(true);
            
        } catch (SQLException ex) {
            Logger.getLogger(Tohtori.class.getName()).log(Level.SEVERE, null, ex);
        }
		
	}
	/*-------------
	|btnPoista    |
	|             |
	---------------*/{
		
		String pnumero = lblTNumero.getText();
        
        try {
            pst = con.prepareStatement("DELETE FROM tohtoritaulukko WHERE tohtori_id = ?");
                        
       
            pst.setString(1, pnumero);
            
            pst.executeUpdate();
            
            JOptionPane.showMessageDialog(this,"Lääkäri tiedot on poistettu onnistuneesti! ");
            
            Automaattitunniste();
            
            txtNimi.setText("");
            txtSpesialisti.setText("");
            txtPatevyys.setText("");
            txtHoitava.setText("");
            txtPuhelin.setText("");
            spnHuone.setValue(0);
            tohtori_taulukko();
            btnLisaa.setEnabled(true);
            
        } catch (SQLException ex) {
            Logger.getLogger(Tohtori.class.getName()).log(Level.SEVERE, null, ex);
        }
	}
	
	/*-------------
	|btnSulje     |
	|             |
	---------------*/{
	
	this.setVisible(false);
	
	}
	