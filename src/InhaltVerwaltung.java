package application;
	
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;

/**
 * @author Andrej Morlang (2000435)
 * @author Robert Laatsch ()
 */

public class InhaltVerwaltung {

	//TODO ?
	public int Konvertieren(String inhalt) {
		return 0;
	}

	public void apriori(String inhalte[][], double minSup, double minConf, int hoehe, int waren) {
		int itemcount[] = new int[waren];
		for (int j = 0; j < waren; j++) {
			itemcount[j]=0;
		}
		for (int i = 0; i < hoehe; i++) {
			for (int j = 0; j < waren; j++) {
				if (inhalte[i][j].equals(String.valueOf(1))) {
					itemcount[j]++;
					System.out.println(j + " : " + itemcount[j]);
				}
			}
		}
		LinkedList<LinkedList<LinkedList<Integer>>> kandidaten = new LinkedList<LinkedList<LinkedList<Integer>>>();
		kandidaten.add(new LinkedList<LinkedList<Integer>>());
		
		Double sup = new Double(0);
		for (int j = 0; j < waren; j++) {
			sup=((double) itemcount[j]/(double) hoehe);
			if (sup>minSup) {
				kandidaten.get(0).add(new LinkedList<Integer>());
				kandidaten.get(0).getLast().add(j);
				System.out.println(j + " ist Frequent Item");
			}
		}
		System.out.println(kandidaten.get(0).get(1).get(0));
		
/*		for (int kandidatenlaenge=2;kandidatenlaenge<=waren;kandidatenlaenge++) {*/
		for (int kandidatenlaenge=2;kandidatenlaenge<=2;kandidatenlaenge++) {
			kandidaten.add(new LinkedList<LinkedList<Integer>>());
			/* Kandidaten Generierung */
			LinkedList<LinkedList<Integer>> frequentItemKMinus1 = kandidaten.get(kandidatenlaenge-2);
			System.out.println(kandidaten.get(0).get(2).get(0));
			System.out.println(frequentItemKMinus1.size());
			if (frequentItemKMinus1.size()>0) {				
				LinkedList<Integer> frequentItemPre = frequentItemKMinus1.get(0);
				for (int frequentItemsI=1;frequentItemsI<frequentItemKMinus1.size();frequentItemsI++) {
					boolean sameStartItems = true;
					LinkedList<Integer> frequentItemThis = frequentItemKMinus1.get(frequentItemsI);
					for (int checkItem=0;checkItem<frequentItemThis.size()-2;checkItem++) {
						if (frequentItemThis.get(checkItem)==frequentItemPre.get(checkItem)) {
							sameStartItems = false;
						}
					}
					if (sameStartItems) {
						kandidaten.getLast().add(new LinkedList<Integer>());
						for (int checkItem=0;checkItem<frequentItemThis.size()-1;checkItem++) {
							kandidaten.getLast().getLast().add(frequentItemThis.get(checkItem));
						}
						if (frequentItemThis.get(frequentItemThis.size()-1)>frequentItemPre.get(frequentItemPre.size()-1)) {
							kandidaten.getLast().getLast().add(frequentItemPre.get(frequentItemPre.size()-1));
							kandidaten.getLast().getLast().add(frequentItemThis.get(frequentItemThis.size()-1));
						} else {
							kandidaten.getLast().getLast().add(frequentItemThis.get(frequentItemThis.size()-1));
							kandidaten.getLast().getLast().add(frequentItemPre.get(frequentItemPre.size()-1));
						}
						for (int printI=0;printI<kandidaten.getLast().getLast().size();printI++) {
							System.out.print(kandidaten.getLast().getLast().get(printI) + " ");
						}
						System.out.println(" ist Kandidat");
						/* "Pruning" behalte den Kandiaten nur wenn alle k-1-elementigen Teilmenge FIs sind */
						int ohneItem = kandidaten.getLast().getLast().size()-3;
						for (int frequentItemsJ=0;frequentItemsJ<frequentItemKMinus1.size();frequentItemsJ++) {
							LinkedList<Integer> frequentItemCheck = frequentItemKMinus1.get(frequentItemsJ);
							for (int itemsCheck=0;itemsCheck<frequentItemCheck.size();itemsCheck++) {
								if (ohneItem>0) {
									if (itemsCheck<ohneItem) {
										if (frequentItemCheck.get(itemsCheck)==kandidaten.getLast().getLast().get(itemsCheck)) {
											ohneItem--;
										}
									} else {
										if (frequentItemCheck.get(itemsCheck)==kandidaten.getLast().getLast().get(itemsCheck+1)) {
											ohneItem--;
										}
									}
								}
							}
						}
						if (ohneItem>0) {
							kandidaten.getLast().getLast().removeLast();
							System.out.println("...doch nicht");
						}
					}
					frequentItemPre = frequentItemThis;
				}
			}
			/* Kandidaten Generierung beendet */
			/* zaehlen */
			for (int i = 0; i < hoehe; i++) {
				for (LinkedList<Integer> kandidat : kandidaten.getLast()) {
					for (Integer kandidatItems : kandidat) {
						if (inhalte[i][kandidatItems].equals(String.valueOf(1))) {
							System.out.println("Kandidat gefunden ");
						}
					}
				}
			}
		}		
	}

	//--- Inhalte in ein zweidimensionales Array speichern ---
	public String[][] inhalteUebergeben(List<String> dateiinhalt, int breite, String inhalte[][], HashMap<Pair<Integer,String>, Integer> datenwerte, int waren, int daten) {

		int vertikal = 0, fehlerhaft = 0;

		//-- Inhalte pruefen und uebergben --
		for (String information : dateiinhalt) {

			//- Betrachte nur die gefuellten Zeilen mit Dateninhalten.  -
			if (information.startsWith("0") || information.startsWith("1")) {
				String x[] = information.split(",");

				//- Pruefe ob der Datensatz vollst�ndig ist. -
				if (x.length != breite) {
					System.out.println(information);
					System.out.println("Falscher Datensatz");
					fehlerhaft++;
				} else {
					for (int horizontal = 0; horizontal < breite; horizontal++) {
						inhalte[vertikal][horizontal] = x[horizontal];
						if (horizontal>=waren){
							Pair tempPair = new Pair(Integer.valueOf(horizontal-waren),x[horizontal]);
							if (datenwerte.containsKey(tempPair)) {
								datenwerte.put(tempPair, datenwerte.get(tempPair)+1);
							} else {
								datenwerte.put(tempPair, Integer.valueOf(1));
							}
						}							
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
	public int[] hoehebreite(List<String> dateiinhalt) {
		int hoehe = 0;
		int groessen[] = new int[4];

		//- Gehe den gesamte Liste durch -
		for (String information : dateiinhalt) {
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
			//die eingabe der datei wird an den string s �bergeben
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
