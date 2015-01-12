import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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
		ObservableList<Double> list = FXCollections.observableArrayList();
		for (double i = 0.00; i <= 1.00; i += 0.01) {
			i = Math.rint(i * 100) / 100;
			list.add(i);
		}
		minSup.setItems(list);
		minConf.setItems(list);
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
	private ListView<String> regelListe;

	@FXML
	private ComboBox<Double> minSup, minConf;

	@FXML
	private ComboBox<String> kategorie, limitierung;

	@FXML
	private BarChart<String, Number> diagramm;

	@FXML
	private NumberAxis yAxe;

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

		//-- Kategorisierung --
		LinkedList<String> namen = new LinkedList<String>();
		namen = inhalt.namenErmitteln(dateiinhalt, breite);

		//- Statistik-ComboBox mit Kategorien fuellen - 
		ObservableList<String> kategorieListe = FXCollections.observableArrayList();
		//		for (int i = 0; i < namen.size(); i++) {
		//			if (i >= waren) {
		//				kategorieListe.add(namen.get(i));
		//			}
		//		}
		kategorieListe.add("Informationen zur Person");
		kategorieListe.add("Gekaufte Waren");
		kategorieListe.add(namen.getLast());
		kategorie.setItems(kategorieListe);

		//-- Einschraenkungen --
		LinkedList<HashMap<String, Integer>> werte = new LinkedList<HashMap<String, Integer>>();
		werte = inhalt.werteErmitteln(dateiinhalt, breite, waren);

		//- Einschraenkungen-ComboBox mit Kategorien fuellen -
		//werte.get(i) fuer i=daten bis	breite
		ObservableList<String> limitListe = FXCollections.observableArrayList();
		for (int i = waren; i < breite; i++) {
			for (String wert : werte.get(i).keySet()) {
				limitListe.add(namen.get(i) + ": " + wert);
			}
		}
		limitierung.setItems(limitListe);

	}

	@FXML
	private void statistik(ActionEvent event) {
		InhaltVerwaltung inhalt = new InhaltVerwaltung();

		if ((kategorie.getValue() == null) || (dateiinhalt == null)) {
			return;
		}

		//- Diagramm darstellen -
		if (diagramm.isVisible() == false) {
			diagramm.setVisible(true);
		}

		//-- Hoehe/Breite/Daten/Waren ermitteln --
		int groessen[] = inhalt.hoehebreite(dateiinhalt);
		int waren = 0, daten = 0, hoehe = 0, breite = 0;
		hoehe = groessen[0];
		breite = groessen[1];
		waren = groessen[2];
		daten = groessen[3];

		LinkedList<String> namen = new LinkedList<String>();
		namen = inhalt.namenErmitteln(dateiinhalt, breite);

		LinkedList<HashMap<String, Integer>> werte = new LinkedList<HashMap<String, Integer>>();
		werte = inhalt.werteErmitteln(dateiinhalt, breite, waren);

		//-- Statistik darstellen --
		diagramm.getData().clear();
		for (int i = 0; i < werte.size(); i++) {
			for (String wert : werte.get(i).keySet()) {
				//System.out.println(namen.get(i) + " " + wert + " " + werte.get(i).get(wert));
				XYChart.Series datensatz = new XYChart.Series();

				//- Summe der zusammengekauten Waren -
				if (kategorie.getValue().equals("Warensumme") && (i == werte.size() - 1)) {
					datensatz.getData().add(new XYChart.Data(wert, werte.get(i).get(wert)));
					diagramm.setTitle("Menge der zusammen gekauften Waren");
					yAxe.setLabel("Anzahl");
					datensatz.setName(wert);
					diagramm.getData().add(datensatz);
				}

				//- Informationen zu den Peronen -
				if ((kategorie.getValue().equals("Informationen zur Person") && (i > waren) && (i < werte.size() - 1))) {
					double prozent = (double) werte.get(i).get(wert) / hoehe;
					datensatz.getData().add(new XYChart.Data(wert, prozent));
					diagramm.setTitle("Informationen zu den einkaufenden Personen");
					yAxe.setLabel("Prozent");
					datensatz.setName(namen.get(i) + ": " + wert);
					diagramm.getData().add(datensatz);
				}

				//- Uebersicht der Waren -
				if ((kategorie.getValue().equals("Gekaufte Waren")) && (i <= waren) && (wert.equals("1"))) {
					datensatz.getData().add(new XYChart.Data(namen.get(i), werte.get(i).get(wert)));
					diagramm.setTitle("Anzahl der gekauften Waren");
					yAxe.setLabel("Anzahl");
					datensatz.setName(namen.get(i));
					diagramm.getData().add(datensatz);
				}
			}
		}
	}

	@FXML
	private void auswerten(ActionEvent event) {

		//- setDisable = nullpointer -
		if (dateiinhalt == null || dateiinhalt.isEmpty()) {
			return;
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

		if (limitierung.getValue() == null) {
			hoeheTatsaechlich = inhalt.inhalteUebergeben(dateiinhalt, breite, inhalte, waren, daten, -1, "");
		} else {
			String limit[] = limitierung.getValue().split(": ");
			int datenFeld = -1;
			for (int i = 0; i < daten; i++) {
				LinkedList<String> namen = new LinkedList<String>();
				namen = inhalt.namenErmitteln(dateiinhalt, breite);

				if (namen.get(waren + i).equals(limit[0])) {
					datenFeld = i;
				}
				System.out.println(datenFeld + " " + limit[1]);

			}
			hoeheTatsaechlich = inhalt.inhalteUebergeben(dateiinhalt, breite, inhalte, waren, daten, datenFeld, limit[1]);
		}

		//- Sup und Conf Werte zuweisen -
		Double minSupWert = new Double(0.20);
		Double minConfWert = new Double(0.50);

		if (minSup.getValue() != null) {
			minSupWert = (Double) minSup.getValue();
		}
		if (minConf.getValue() != null) {
			minConfWert = (Double) minConf.getValue();
		}

		//--- Regeln auf den Datensatz anwenden ---
		LinkedList<LinkedList<LinkedList<Integer>>> regeln = new LinkedList<LinkedList<LinkedList<Integer>>>();
		regeln = inhalt.apriori(inhalte, minSupWert, minConfWert, hoeheTatsaechlich, waren);

		//-- Liste der gefundenen Regeln darstellen -- 
		LinkedList<String> namen = new LinkedList<String>();
		namen = inhalt.namenErmitteln(dateiinhalt, breite);
		ObservableList<String> rListe = FXCollections.observableArrayList();
		rListe.add("Anhand der Einstellungen treffen " + regeln.size() + " Regeln zu.");
		for (int i = 0; i < regeln.size(); i++) {
			double conf = ((double) regeln.get(i).get(3).get(0) / (double) regeln.get(i).get(2).get(0));
			conf = Math.round(conf * 100);
			double sup = ((double) regeln.get(i).get(3).get(0) / (double) hoeheTatsaechlich);
			sup = Math.round(sup * 100);
			String rListeString = new String("Sup: " + sup / 100);
			if (sup % 10 == 0) {
				rListeString = rListeString + "0";
			}
			rListeString = rListeString + " Conf: " + conf / 100;
			if (conf % 10 == 0) {
				rListeString = rListeString + "0";
			}
			rListeString = rListeString + " [ ";
			for (int j = 0; j < regeln.get(i).get(0).size(); j++) {
				rListeString = rListeString + "'" + namen.get(regeln.get(i).get(0).get(j)) + "' ";
			}
			rListeString = rListeString + "] -> [ ";
			for (int j = 0; j < regeln.get(i).get(1).size(); j++) {
				rListeString = rListeString + "'" + namen.get(regeln.get(i).get(1).get(j)) + "' ";
			}
			rListeString = rListeString + "]";
			rListe.add(rListeString);
		}
		regelListe.setItems(rListe);

	}

}
