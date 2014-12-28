import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Andrej Morlang (2000435)
 * @author Robert Laatsch ()
 */

public class InhaltVerwaltung {

	//TODO ?
	public int Konvertieren(String inhalt) {
		return 0;
	}

	//--- Inhalte in ein zweidimensionales Array speichern ---
	public String[][] inhalteUebergeben(List<String> dateiinhlat, int breite, String inhalte[][]) {

		int vertikal = 0, fehlerhaft = 0;

		//-- Inhalte pruefen und uebergben --
		for (String information : dateiinhlat) {

			//- Betrachte nur die gefuellten Zeilen mit Dateninhalten.  -
			if (information.startsWith("0") || information.startsWith("1")) {
				String x[] = information.split(",");

				//- Pruefe ob der Datensatz vollständig ist. -
				if (x.length != breite) {
					System.out.println(information);
					System.out.println("Falscher Datensatz");
					fehlerhaft++;
				} else {
					for (int horizontal = 0; horizontal < breite; horizontal++) {
						inhalte[vertikal][horizontal] = x[horizontal];
					}
					vertikal++;
				}
			}
		}
		System.out.println("Es gibt " + fehlerhaft + " fehlerhafte Dateninhalte.");
		return inhalte;
	}

	//--- Inhalte Zeilenweise uebergeben ---
	public ObservableList<Map> tabellenInhalt(List<String> dateiinhalt, int breite) {
		ObservableList<Map> allData = FXCollections.observableArrayList();

		//-- Fuer jede Zeiel... --
		for (int vertikal = 3; vertikal < dateiinhalt.size(); vertikal++) {
			Map<String, String> dataRow = new HashMap<>();
			String spaltenAttribute[] = dateiinhalt.get(vertikal).split(",");
			//-- ...gehe einmal die Spalten durch --
			for (int horizontal = 0; horizontal < breite; horizontal++) {
				String mapKey = String.valueOf(horizontal);
				dataRow.put(mapKey, spaltenAttribute[horizontal]);
			}
			//- Speichere die vollstaendige Zeile -
			allData.add(dataRow);
		}
		return allData;
	}

	//--- Hoehe und Breite ermitteln ---
	public int[] hoehebreite(List<String> dateiinhlat) {
		int hoehe = 0;
		int groessen[] = new int[4];

		//- Gehe den gesamte Liste durch -
		for (String information : dateiinhlat) {
			//- Zeilen mit zwei Argumenten und ":" betrachten-
			if (information.contains(":")) {
				String[] info = information.toLowerCase().split(":");
				if (info.length == 2) {
					info[1] = info[1].replaceAll(" ", "");
					if (info[0].equals("waren")) {
						groessen[2] = new Integer(info[1]);
					}

					if (info[0].equals("daten")) {
						groessen[3] = Integer.parseInt(info[1]);
					}
				}
			}

			//- Betrachte nur die mit Attributen gefuellten Zeilen -
			if (information.startsWith("0") || information.startsWith("1")) {
				hoehe++;
				//- Testausgabe -
				System.out.println(information);
			}
		}
		groessen[0] = hoehe;
		groessen[1] = groessen[2] + groessen[3];
		return groessen;
	}

	//--- Lese die Datei ein ---
	public List<String> dateiEinlesen(String pfad) {
		try {
			File dokument = new File(pfad);
			if (!dokument.exists()) {
				return null;
			}

			FileReader datei = new FileReader(pfad);
			//die eingabe der datei wird an den string s übergeben
			BufferedReader input = new BufferedReader(datei);
			String output = null;
			List<String> dateiinhalt = new ArrayList<String>();

			//- Testausgabe -
			System.out.println("Eingelesene Inhalte: ");

			//-- Speicher den gesammten Dateiinhalt als Lsite --
			while ((output = input.readLine()) != null) {
				//- Leere Zeilen werden nicht gespeichert. -
				if (output.trim().equals("") == false) {
					dateiinhalt.add(output);
					//- Testausgabe -
					System.out.println(output);
				}
			}
			input.close();
			return dateiinhalt;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}
}
