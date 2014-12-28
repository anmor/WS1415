import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

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
		hoehe = groessen[0];
		breite = groessen[1];
		waren = groessen[2];
		daten = groessen[3];

		//-- Array mit Werten fuellen. --
		String inhalte[][] = new String[hoehe][breite];
		inhalt.inhalteUebergeben(dateiinhalt, breite, inhalte);

		//- Testausgabe -
		int a = 0;
		System.out.println("Elemente im Array: ");
		for (int i = 0; i < hoehe; i++) {
			for (int j = 0; j < breite; j++) {
				System.out.print(inhalte[i][j] + " ");
			}
			System.out.println("");
			a = i;
		}
		a++; // wegen nullten Eintrag.
		System.out.println("Es gibt " + a + " Eintraege.");

	}

}
