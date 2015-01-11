package application;
	
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.HashMap;

import javafx.util.Pair;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.AnchorPane;

/**
 * @author Andrej Morlang (2000435)
 * @author Robert Laatsch ()
 */

public class GUIController implements Initializable {

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

	}

	@FXML
	private Button dateiLesen, auswertung;

	@FXML
	private TextField pfad, argumente;

	@FXML
	private AnchorPane pane;

	@FXML
	private Label lesen;

	@FXML
	private TableView tabelle;

	@FXML
	private BarChart<?, ?> diagramm;

	List<String> dateiinhalt = new ArrayList<String>();

	private static String alteSpaltennamen = null;

	@FXML
	private void inhalteUebergeben(ActionEvent event) {

		//- Tabelle darstellen -
		if (tabelle.isVisible() == false) {
			tabelle.setVisible(true);
		}

		//-- Inhalte der vorherigen Datei entfernen --
		if (alteSpaltennamen != null) {
			String spaltennamen[] = alteSpaltennamen.split(",");
			tabelle.getColumns().remove(0, spaltennamen.length);
			alteSpaltennamen = null;
		}

		String dokumentpfad = pfad.getText();
		InhaltVerwaltung inhalt = new InhaltVerwaltung();

		//-- Datei als Liste speichern --
		dateiinhalt = inhalt.dateiEinlesen(dokumentpfad);
		if (dateiinhalt == null) {
			lesen.setText("Datei existiert nicht!");
			tabelle.setPlaceholder(new Label("Keine Inhalte vorhanden"));
			diagramm.setVisible(false);
			//auswertung.setDisable(true); // nullpointer warum?
			return;
		}
		lesen.setText("Datei erfolgreich gelesen.");
		//-- Hoehe/Breite/Daten/Waren ermitteln --
		int groessen[] = inhalt.hoehebreite(dateiinhalt);
		int waren = 0, daten = 0, hoehe = 0, breite = 0;
		hoehe = groessen[0];
		breite = groessen[1];
		waren = groessen[2];
		daten = groessen[3];

		LinkedList<String> warenNamen = new LinkedList<String>();
		warenNamen = inhalt.warenNamenErmitteln(dateiinhalt, breite, waren, daten);

		LinkedList<String> datenNamen = new LinkedList<String>();
		datenNamen = inhalt.datenNamenErmitteln(dateiinhalt, breite, waren, daten);

		LinkedList<HashMap<String, Integer>> datenWerte = new LinkedList<HashMap<String, Integer>>();
		datenWerte = inhalt.datenErmitteln(dateiinhalt, breite, waren, daten);
		
	    for (int i = 0;i < datenWerte.size(); i++) 
	    {
		    for( String wert: datenWerte.get(i).keySet() )
		    {
		       System.out.println(datenNamen.get(i) + " " + wert + " "+ datenWerte.get(i).get(wert));    
		    }
	    }
		//- Testausgabe -
		System.out.println("Waren: " + waren);
		System.out.println("Daten: " + daten);
		System.out.println("Hoehe = " + hoehe + " Breite = " + breite);

		//-- Spalten der Tabelle erstllen und mit Inhlten fuellen --
		alteSpaltennamen = dateiinhalt.get(2);
		String spaltennamen[] = dateiinhalt.get(2).split(",");
		//- Pruefen ob die Breite uebereinstimmt - 
		if (breite == spaltennamen.length) {
			String mapKey = null;
			for (int i = 0; i < breite; i++) {
				mapKey = String.valueOf(i);
				TableColumn<Map, String> column = new TableColumn<>(spaltennamen[i]);
				column.setCellValueFactory(new MapValueFactory(mapKey));
				tabelle.getColumns().add(column);
			}
			tabelle.setItems(inhalt.tabellenInhalt(dateiinhalt, breite));
		}
		// auswertung.setDisable(false); // nullpointer warum?
	}

	@FXML
	private void auswerten(ActionEvent event) {

		//- setDisable = nullpointer -
		if (dateiinhalt == null || dateiinhalt.isEmpty()) {
			return;
		}

		//- Diagramm darstellen -
		if (diagramm.isVisible() == false) {
			diagramm.setVisible(true);
		}

		InhaltVerwaltung inhalt = new InhaltVerwaltung();

		//-- Hoehe/Breite/Daten/Waren ermitteln --
		int groessen[] = inhalt.hoehebreite(dateiinhalt);
		int waren = 0, daten = 0, hoehe = 0, breite = 0;
		int hoeheTatsaechlich = 0;
		hoehe = groessen[0];
		breite = groessen[1];
		waren = groessen[2];
		daten = groessen[3];

		//-- Array mit Werten fuellen. --
		String inhalte[][] = new String[hoehe][breite];
		hoeheTatsaechlich = inhalt.inhalteUebergeben(dateiinhalt, breite, inhalte, waren, daten,0,"Male");

		//- Testausgabe -
		int a = 0;
		System.out.println("Elemente im Array: ");
		for (int i = 0; i < hoeheTatsaechlich; i++) {
			for (int j = 0; j < breite; j++) {
				System.out.print(inhalte[i][j] + " ");
			}
			System.out.println("");
			a = i;
		}
		a++; // wegen nullten Eintrag.
		System.out.println("Es gibt " + a + " Eintraege.");

	    Double minSup = new Double(0.20);
	    Double minConf = new Double(0.50);
		LinkedList<LinkedList<LinkedList<Integer>>> regeln = new LinkedList<LinkedList<LinkedList<Integer>>>();
	    regeln = inhalt.apriori(inhalte,minSup,minConf,hoeheTatsaechlich,waren);
	}

}
