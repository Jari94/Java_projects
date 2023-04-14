//import java.sql.;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.io.FileFilter;
import java.io.FileOutputStream;
//import com.itextpdf.text.;
//import com.itextpdf.text.pdf.;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Tuotetaulukko extends javax.swing.JFrame {
 public Tuotetaulukko() {
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
        con = DriverManager.getConnection("jdbc:mysql://localhost/javatuotetaulu","USER","PASSWORD");
        
        }catch (ClassNotFoundException ex){
           Logger.getLogger(Tuotetaulukko.class.getName()).log(Level.SEVERE,null, ex);
        }catch (SQLException ex){
           Logger.getLogger(Tuotetaulukko.class.getName()).log(Level.SEVERE,null, ex);
        }
    }
    public void LoadProductNo(){//Kutsutaan metodia, joka lataa halutun tiedon tietokannasta
        
        try {
            pst = con.prepareStatement("SELECT ID FROM tuotetaulu");//PreparedStatement on esikäännetty SQL-käsky. Se on Statementin alirajapinta. Prepared Statement -objekteissa on joitain 				
                                                                          //hyödyllisiä lisäominaisuuksia kuin Statement-objekteilla. Kovakoodauskyselyiden sijaan PreparedStatement-objekti tarjoaa 			
                                                                          //ominaisuuden parametroidun kyselyn suorittamiseen 
            rs = pst.executeQuery();
            cmbID.removeAllItems();
            while(rs.next()){
             
                cmbID.addItem(rs.getString(1));     
        }
        } catch (SQLException ex) {
            Logger.getLogger(Tuotetaulukko.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void Fetch(){
        try {
            int q;
            pst = con.prepareStatement("SELECT * FROM tuotetaulu");
            rs = pst.executeQuery();
            ResultSetMetaData rss = rs.getMetaData();//ResultSetMetaData on objekti, jonka avulla voidaan saada tietoa objektin sarakkeiden tyypeistä ja ominaisuuksista ResultSet.
            q = rss.getColumnCount();
            
            DefaultTableModel df = (DefaultTableModel)tblTaulukko.getModel();
            df.setRowCount(0);
            while(rs.next()){
                Vector v2 = new Vector();//*Vector-luokka toteuttaa kasvavan objektijoukon. Se löytyy java.util-paketista ja toteuttaa List- rajapinnan. 
                for(int a=1; a <= q;a++){
                    v2.add(rs.getString("ID"));
                    v2.add(rs.getString("tuotenimi"));
                    v2.add(rs.getString("tuotehinta"));
                    v2.add(rs.getString("tuotemaara"));
                }
                df.addRow(v2);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Tuotetaulukko.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
	
	 //Lisää napilla lisätään tuotetiedot tietokantaan
        try{
        String pnimi = txtMerkki.getText();
        String phinta = txtHinta.getText();
        String pmaara = txtMaara.getText();
        
        pst = con.prepareStatement("INSERT INTO tuotetaulu(tuotenimi, tuotehinta, tuotemaara) VALUES(?, ?, ?)");
        pst.setString(1, pnimi);
        pst.setString(2, phinta);
        pst.setString(3, pmaara);
        
        /* Kun olet luonut lausekeobjektin, voit suorittaa sen jollakin Statement-rajapinnan suoritustavoista, 
           nimittäin execute(), executeUpdate() ja executeQuery().
        
           execute()-menetelmä: Tätä menetelmää käytetään SQL DDL -käskyjen suorittamiseen. 
           Se palauttaa loogisen arvon, joka määrittää sään, jonka ResultSet-objekti voidaan noutaa.*/
        
        int k = pst.executeUpdate();
        
        if(k ==1){
            JOptionPane.showMessageDialog(this,"Tuote on lisätty onnistuneesti!");
            txtMerkki.setText("");
            txtHinta.setText("");
            txtMaara.setText("");
            Fetch();
            LoadProductNo();
            
        }else{
            JOptionPane.showMessageDialog(this,"Tuotteen lisääminen ei onnistunut!");
        }
        }catch(SQLException ex){
            Logger.getLogger(Tuotetaulukko.class.getName()).log(Level.SEVERE,null, ex);
        }
	
	
	 //Etsi napilla ohjelma hakee kyseisen tuotteen tiedot, comboboxista valitaan tuotteen ID numero
        
        try {
            String pID = cmbID.getSelectedItem().toString();
            
            pst = con.prepareStatement("SELECT * FROM tuotetaulu WHERE ID=?");
            pst.setString(1, pID);
            rs=pst.executeQuery();  //Tätä menetelmää käytetään lausekkeiden suorittamiseen, jotka palauttavat taulukkotietoja (esimerkkivalinta). Se palauttaa luokan 	
                                   //ResultSet objektin.
            
            if(rs.next() == true){//HUOM! Numerot alkaa 2:sta, koska tuotteen ID:lle on varattuna sarake 1
                txtMerkki.setText(rs.getString(2));
                txtHinta.setText(rs.getString(3));
                txtMaara.setText(rs.getString(4));
            }else{
                JOptionPane.showMessageDialog(this,"Tuloksia ei saatavilla!");
            }
            
            
        } catch (SQLException ex) {
            Logger.getLogger(Tuotetaulukko.class.getName()).log(Level.SEVERE, null, ex);
        }
		
		 //Päivitä napilla päivitetään tuotteiden nimet, hinnat ja määrät. Lopussa ohjelma tyhjentää tekstikentät
        try {
            String pID = cmbID.getSelectedItem().toString();
            String pnimi = txtMerkki.getText();
            String phinta = txtHinta.getText();
            String pmaara = txtMaara.getText();
            
            pst = con.prepareStatement("UPDATE tuotetaulu SET tuotenimi=?,tuotehinta=?,tuotemaara=? WHERE ID=?");
            
            pst.setString(1, pnimi);
            pst.setString(2, phinta);
            pst.setString(3, pmaara);
            pst.setString(4, pID);
            
            int k=pst.executeUpdate(); //executeUpdate()palauttaa niiden rivien määrän, joihin sen suorittama SQL-komento vaikuttaa.
            if(k==1){
                JOptionPane.showMessageDialog(this,"Päivitys onnistui!");
                txtMerkki.setText("");
                txtHinta.setText("");
                txtMaara.setText("");
                txtMerkki.requestFocus();
                Fetch();
                LoadProductNo();
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(Tuotetaulukko.class.getName()).log(Level.SEVERE, null, ex);
        }
		
		
	//Poista napilla ohjelma poistaa valitun tuotteen tiedot taulukosta
        try {
            String pID = cmbID.getSelectedItem().toString();
            pst = con.prepareStatement("DELETE FROM tuotetaulu WHERE ID=?");
            pst.setString(1, pID);
             int k=pst.executeUpdate();
             if(k==1){
                JOptionPane.showMessageDialog(this,"Poistaminen onnistui!");
                txtMerkki.setText("");
                txtHinta.setText("");
                txtMaara.setText("");
                txtMerkki.requestFocus();
                Fetch();
                LoadProductNo();
            }else{
                 JOptionPane.showMessageDialog(this,"Poistaminen ei onnistunut!");
             }
        } catch (SQLException ex) {
            Logger.getLogger(Tuotetaulukko.class.getName()).log(Level.SEVERE, null, ex);
        }
	
	//Tallentaa tietokannassa olevat tiedot CSV tiedostomuotoon
	
	String tiedostonimi = "C:\\Users\\PCUSER\\JavaTiedostot\\csvJavaTiedosto.csv"; //Ei voida suoraan tallentaa C juureen, järjestelmän suojauksen/käyttöoikeuksien takia
        try {
            FileWriter fw = new FileWriter(tiedostonimi);
            pst = con.prepareStatement("SELECT * FROM tuotetaulu");
            rs = pst.executeQuery();
            
            while(rs.next()){
                fw.append(rs.getString(1));
                fw.append(',');
                fw.append(rs.getString(2));
                fw.append(',');
                fw.append(rs.getString(3));
                fw.append(',');
                fw.append(rs.getString(4));
                fw.append('\n');  
            }
            JOptionPane.showMessageDialog(this,"CSV tiedoston luonti onnistui!");
            fw.flush();
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(Tuotetaulukko.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Tuotetaulukko.class.getName()).log(Level.SEVERE, null, ex);
        }
	
	
	
	public static void main(String args[]) {
       
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Kirjautumisikkuna().setVisible(true);
            }
        });
    }
	
	//Variables Decladration - Don't modify
	//Buttons, JLabels etc.
}
	
	