package kalenteri;
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


public class AjanVaraaja extends javax.swing.JFrame {

    public AjanVaraaja() {
        initComponents(); /*Initcomponents() alustaa poikkeuksetta kaikki Java swing -komponenttiobjektit, joita käyttöliittymäsi graafinen käyttöliittymä käyttää NetBeans GUI Builderin avulla.*/
        Connect();
        LoadProductNo();
        Fetch();
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
        con = DriverManager.getConnection("jdbc:mysql://localhost/varauskalenteri","USERNAME","PASSWORD");
        
        }catch (ClassNotFoundException ex){
           Logger.getLogger(AjanVaraaja.class.getName()).log(Level.SEVERE,null, ex);
        }catch (SQLException ex){
           Logger.getLogger(AjanVaraaja.class.getName()).log(Level.SEVERE,null, ex);
        }
    }
    
    public void LoadProductNo(){//Kutsutaan metodia, joka lataa halutun tiedon tietokannasta
        
        try {
            pst = con.prepareStatement("SELECT ID FROM varaustaulukko");//PreparedStatement on esikäännetty SQL-käsky. Se on Statementin alirajapinta. Prepared Statement -objekteissa on joitain 				
                                                                          //hyödyllisiä lisäominaisuuksia kuin Statement-objekteilla. Kovakoodauskyselyiden sijaan PreparedStatement-objekti tarjoaa 			
                                                                         //ominaisuuden parametroidun kyselyn suorittamiseen 
            rs = pst.executeQuery();
            cmbID.removeAll();
            while(rs.next()){
             
                cmbID.addItem(rs.getString(1));  
                
        }
        } catch (SQLException ex) {
            Logger.getLogger(AjanVaraaja.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     
    private void Fetch(){
        try {
            int q;
            pst = con.prepareStatement("SELECT * FROM varaustaulukko");
            rs = pst.executeQuery();
            ResultSetMetaData rss = rs.getMetaData();//ResultSetMetaData on objekti, jonka avulla voidaan saada tietoa objektin sarakkeiden tyypeistä ja ominaisuuksista ResultSet.
            q = rss.getColumnCount();
            
            DefaultTableModel df = (DefaultTableModel)tblTaulukko.getModel();
            df.setRowCount(0);
            while(rs.next()){
                Vector v2 = new Vector();//*Vector-luokka toteuttaa kasvavan objektijoukon. Se löytyy java.util-paketista ja toteuttaa List- rajapinnan. 
                for(int a=1; a <= q;a++){
                    v2.add(rs.getString("ID"));
                    v2.add(rs.getString("pvm"));
                    v2.add(rs.getString("kellonaika"));
                    v2.add(rs.getString("varaaja"));
                    v2.add(rs.getString("huomiot"));
                }
                df.addRow(v2);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AjanVaraaja.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
	
	//Lisää päiväys napin koodi:
	
	 String valitsepaivays = ((JTextField)DValitsija.getDateEditor().getUiComponent()).getText();
        
        txtTulostus.setText(valitsepaivays);
		
		
	//Lisää ajan varaus napin koodi: 
		
		 try{
        String pTulostus = txtTulostus.getText();
        String pAika = txtAika.getText();
        String pVaraaja = txtVaraaja.getText();
        String pHuomiot = txtHuomiot.getText();
        
        pst = con.prepareStatement("INSERT INTO varaustaulukko(pvm, kellonaika, varaaja, huomiot) VALUES(?, ?, ?, ?)");
        pst.setString(1, pTulostus);
        pst.setString(2, pAika);
        pst.setString(3, pVaraaja);
        pst.setString(4, pHuomiot);
        
        /* Kun olet luonut lausekeobjektin, voit suorittaa sen jollakin Statement-rajapinnan suoritustavoista, 
           nimittäin execute(), executeUpdate() ja executeQuery().
        
           execute()-menetelmä: Tätä menetelmää käytetään SQL DDL -käskyjen suorittamiseen. 
           Se palauttaa loogisen arvon, joka määrittää sään, jonka ResultSet-objekti voidaan noutaa.*/
        
        int k = pst.executeUpdate();
        
        if(k ==1){
            JOptionPane.showMessageDialog(this,"Ajan varaus lisätty!");
            txtTulostus.setText("");
            txtAika.setText("");
            txtVaraaja.setText("");
            txtHuomiot.setText("");
            Fetch();
            LoadProductNo();
        }else{
            JOptionPane.showMessageDialog(this,"Virhe ajan varauksessa!");
        }
        }catch(SQLException ex){
            Logger.getLogger(AjanVaraaja.class.getName()).log(Level.SEVERE,null, ex);
        }
		
		//Lataa tiedot napin koodi: Download files button code:
		
		try {
            
            String pID = cmbID.getSelectedItem().toString();
            pst = con.prepareStatement("SELECT * FROM varaustaulukko WHERE ID=?");
            pst.setString(1, pID);
            rs=pst.executeQuery();  //Tätä menetelmää käytetään lausekkeiden suorittamiseen, jotka palauttavat taulukkotietoja (esimerkkivalinta). Se palauttaa luokan 	
                                   //ResultSet objektin.
            
            if(rs.next() == true){
                txtTulostus.setText(rs.getString(2));
                txtAika.setText(rs.getString(3));
                txtVaraaja.setText(rs.getString(4));
                txtHuomiot.setText(rs.getString(5));  
            }else{
                JOptionPane.showMessageDialog(this,"Tuloksia ei saatavilla!");
            }
            
            
        } catch (SQLException ex) {
            Logger.getLogger(AjanVaraaja.class.getName()).log(Level.SEVERE, null, ex);
        }
		
		//Poista tiedot napin koodi: Delete files button code:
		
		try {
            String pID = cmbID.getSelectedItem().toString();
            pst = con.prepareStatement("DELETE FROM varaustaulukko WHERE ID=?");
            pst.setString(1, pID);
             int k=pst.executeUpdate();
             if(k==1){
                JOptionPane.showMessageDialog(this,"Poistaminen onnistui!");
                txtTulostus.setText("");
                txtAika.setText("");
                txtVaraaja.setText("");
                txtHuomiot.setText("");
                txtTulostus.requestFocus();
                Fetch();
                LoadProductNo();
            }else{
                 JOptionPane.showMessageDialog(this,"Poistaminen ei onnistunut!");
             }
        } catch (SQLException ex) {
            Logger.getLogger(AjanVaraaja.class.getName()).log(Level.SEVERE, null, ex);
        }
		
		//Päivitä napin koodi: Update button code:
		
		//Päivitä napilla päivitetään tuotteiden nimet, hinnat ja määrät. Lopussa ohjelma tyhjentää tekstikentät
        try {
            String pID = cmbID.getSelectedItem().toString();
            String pTulostus = txtTulostus.getText();
            String pAika = txtAika.getText();
            String pVaraaja = txtVaraaja.getText();
            String pHuomiot = txtHuomiot.getText();
            
            pst = con.prepareStatement("UPDATE varaustaulukko SET pvm=?,kellonaika=?,varaaja=?, huomiot=? WHERE ID=?");
            
            pst.setString(1, pTulostus);
            pst.setString(2, pAika);
            pst.setString(3, pVaraaja);
            pst.setString(4, pHuomiot);
            pst.setString(5, pID);
            
            int k=pst.executeUpdate(); //executeUpdate()palauttaa niiden rivien määrän, joihin sen suorittama SQL-komento vaikuttaa.
            if(k==1){
                JOptionPane.showMessageDialog(this,"Päivitys onnistui!");
                txtTulostus.setText("");
                txtAika.setText("");
                txtVaraaja.setText("");
                txtHuomiot.setText("");
                txtTulostus.requestFocus();
                Fetch();
                LoadProductNo();
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(AjanVaraaja.class.getName()).log(Level.SEVERE, null, ex);
        }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	