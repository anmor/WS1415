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

	public LinkedList<LinkedList<LinkedList<Integer>>> apriori(String inhalte[][], double minSup, double minConf, int hoehe, int waren) {
		int itemcount[] = new int[waren];
		for (int j = 0; j < waren; j++) {
			itemcount[j]=0;
		}
		for (int i = 0; i < hoehe; i++) {
			for (int j = 0; j < waren; j++) {
				if (inhalte[i][j].equals(String.valueOf(1))) {
					itemcount[j]++;
				}
			}
		}
		LinkedList<LinkedList<LinkedList<Integer>>> kandidaten = new LinkedList<LinkedList<LinkedList<Integer>>>();
		LinkedList<LinkedList<Integer>> kandidatenSup = new LinkedList<LinkedList<Integer>>();
		kandidaten.add(new LinkedList<LinkedList<Integer>>());
		kandidatenSup.add(new LinkedList<Integer>());
		
		Double sup = new Double(0);
		for (int j = 0; j < waren; j++) {
			sup=((double) itemcount[j]/(double) hoehe);
			if (sup>=minSup) {
				kandidaten.get(0).add(new LinkedList<Integer>());
				kandidatenSup.get(0).add(itemcount[j]);
				kandidaten.get(0).getLast().add(j);
//				System.out.println(j + " ist Frequent Item");
			}
		}
		
		for (int kandidatenlaenge=2;kandidatenlaenge<=waren;kandidatenlaenge++) {
			kandidaten.add(new LinkedList<LinkedList<Integer>>());
			kandidatenSup.add(new LinkedList<Integer>());
			/* Kandidaten Generierung */
			LinkedList<LinkedList<Integer>> frequentItemKMinus1 = kandidaten.get(kandidatenlaenge-2);
			if (frequentItemKMinus1.size()>0) {				
				for (int frequentItemsI=0;frequentItemsI<frequentItemKMinus1.size()-1;frequentItemsI++) {
					LinkedList<Integer> frequentItemPre = frequentItemKMinus1.get(frequentItemsI);
					for (int frequentItemsJ=frequentItemsI+1;frequentItemsJ<frequentItemKMinus1.size();frequentItemsJ++) {
						boolean sameStartItems = true;
						LinkedList<Integer> frequentItemThis = frequentItemKMinus1.get(frequentItemsJ);
						for (int checkItem=0;checkItem<frequentItemThis.size()-1;checkItem++) {
							if (frequentItemThis.get(checkItem)!=frequentItemPre.get(checkItem)) {
								sameStartItems = false;
							}
						}
						if (sameStartItems) {
							kandidaten.getLast().add(new LinkedList<Integer>());
							kandidatenSup.getLast().add(0);
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
//							System.out.println(kandidaten.getLast().getLast()+" ist Kandidat");
							/* "Pruning" behalte den Kandiaten nur wenn alle k-1-elementigen Teilmenge FIs sind */
							int ohneItem = kandidaten.getLast().getLast().size()-3;
							for (int frequentItemsK=0;frequentItemsK<frequentItemKMinus1.size();frequentItemsK++) {
								LinkedList<Integer> frequentItemCheck = frequentItemKMinus1.get(frequentItemsK);
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
//								System.out.println("...doch nicht");
							}
						}
					}
				}
			}
			/* Kandidaten Generierung beendet */
			/* zaehlen */
			for (int i = 0; i < hoehe; i++) {
				for (int j = 0; j< kandidaten.getLast().size(); j++) {
					boolean matches=true;
					int k = 0;
					while (k<kandidaten.getLast().get(j).size() & matches) {
						if (!inhalte[i][kandidaten.getLast().get(j).get(k)].equals(String.valueOf(1))) {
							matches=false;
						}
						k++;
					}
					if (matches) {
						kandidatenSup.getLast().set(j,kandidatenSup.getLast().get(j)+1);
//						System.out.println("Kandidat " + kandidaten.getLast().get(j) + " gefunden " + kandidatenSup.getLast().get(j));						
					}
				}
			}
			for (int j = 0; j< kandidaten.getLast().size(); j++) {
				sup=((double) kandidatenSup.getLast().get(j)/(double) hoehe);
				if (sup<minSup) {
//					System.out.println(kandidaten.getLast().get(j) + " ist kein Frequent Itemset");
					kandidaten.getLast().remove(j);
					kandidatenSup.getLast().remove(j);
					j--;
				} else {
					System.out.println(kandidaten.getLast().get(j) + ": " + kandidatenSup.getLast().get(j));					
				}
			}
		}		
		LinkedList<LinkedList<LinkedList<Integer>>> regeln = new LinkedList<LinkedList<LinkedList<Integer>>>();

		/* moegliche Regeln aufstellen */
		int anzahlRegeln=0;
		for (int i = 1; i< kandidaten.size(); i++) {
			for (int j = 0; j< kandidaten.get(i).size(); j++) {
				for (int k = 0; k< kandidaten.get(i).get(j).size(); k++) {
					regeln.add(new LinkedList<LinkedList<Integer>>());
					anzahlRegeln++;
					regeln.getLast().add(new LinkedList<Integer>());
					regeln.getLast().getLast().add(kandidaten.get(i).get(j).get(k));
					regeln.getLast().add(new LinkedList<Integer>());
					for (int l = 0; l< kandidaten.get(i).get(j).size(); l++) {
						if (l!=k) {
							regeln.getLast().getLast().add(kandidaten.get(i).get(j).get(l));
						}
					}
					/* zaehler fuer den linken Teil der Regel*/
					regeln.getLast().add(new LinkedList<Integer>());
					regeln.getLast().getLast().add(0);
					/* zaehler fuer die Regel, schon bekannt */
					regeln.getLast().add(new LinkedList<Integer>());
					regeln.getLast().getLast().add(kandidatenSup.get(i).get(j));
					System.out.println(regeln.getLast().get(0) + " " + regeln.getLast().get(1));					
				}				
			}			
		}
		for (int i = 0; i< regeln.size(); i++) {
			if (regeln.get(i).get(1).size()>1) {				
				for (int j = 0; j< regeln.get(i).get(1).size(); j++) {
					regeln.add(new LinkedList<LinkedList<Integer>>());
					anzahlRegeln++;
					regeln.getLast().add(new LinkedList<Integer>());
					for (int k = 0; k< regeln.get(i).get(0).size(); k++) {
						regeln.getLast().getLast().add(regeln.get(i).get(0).get(k));
					}
					regeln.getLast().getLast().add(regeln.get(i).get(1).get(j));
					regeln.getLast().add(new LinkedList<Integer>());
					for (int k = 0; k< regeln.get(i).get(1).size(); k++) {
						if (j!=k) {
							regeln.getLast().getLast().add(regeln.get(i).get(1).get(k));						
						}
					}				
					/* zaehler fuer den linken Teil der Regel*/
					regeln.getLast().add(new LinkedList<Integer>());
					regeln.getLast().getLast().add(0);
					/* zaehler fuer die Regel, schon bekannt */
					regeln.getLast().add(new LinkedList<Integer>());
					regeln.getLast().getLast().add(regeln.get(i).get(3).get(0));
				}		
			}
		}
		System.out.println(anzahlRegeln + " moegliche Regeln ");
		/* linke Seite der Regeln ermitteln */
		for (int i = 0; i< regeln.size(); i++) {
			boolean matches=false;
			int j = 0;
			while (j< kandidaten.get(regeln.get(i).get(0).size()-1).size() & !matches) {
				matches = true;
				for (int k = 0; k< regeln.get(i).get(0).size(); k++) {
					if (kandidaten.get(regeln.get(i).get(0).size()-1).get(j).get(k)!=regeln.get(i).get(0).get(k)) {
						matches=false;
					}
				}
				if (matches) {
					regeln.get(i).get(2).set(0,kandidatenSup.get(regeln.get(i).get(0).size()-1).get(j));					
				}
				j++;
			}
			
		}

		/* Konfidenz berechnen */

		for (int i = 0; i< regeln.size(); i++) {
			double conf=((double) regeln.get(i).get(3).get(0)/(double) regeln.get(i).get(2).get(0));
			if (conf<minConf) {
				regeln.remove(i);
				i--;
			} else {
				System.out.println(regeln.get(i).get(0) + " " + regeln.get(i).get(1) + " " + regeln.get(i).get(2) + " " + regeln.get(i).get(3));
			}			
		}	
		System.out.println(regeln.size() + " Regeln gefunden");
		return regeln;
	}

	//--- Ermittle die moeglichen Werte in den Datenfeldern ---
	public LinkedList<HashMap<String, Integer>> datenErmitteln(List<String> dateiinhalt, int breite, int waren, int daten) {
		LinkedList<HashMap<String, Integer>> datenWerte = new LinkedList<HashMap<String, Integer>>();
		for (int i = 0; i < daten; i++) {
			datenWerte.add(new HashMap<String, Integer>());
		}

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
					for (int horizontal = waren; horizontal < breite; horizontal++) {
						if (datenWerte.get(horizontal-waren).containsKey(x[horizontal])) {
							datenWerte.get(horizontal-waren).put(x[horizontal], datenWerte.get(horizontal-waren).get(x[horizontal])+1);							
						} else {
							datenWerte.get(horizontal-waren).put(x[horizontal], 1);							
						}
					}
					vertikal++;
				}
			}
		}
		return datenWerte;
	}

	//--- Inhalte in ein zweidimensionales Array speichern ---
	public int inhalteUebergeben(List<String> dateiinhalt, int breite, String inhalte[][], int waren, int daten, int filterFeld, String filterWert) {

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
					boolean nutzeDatensatz = true;
					if (filterFeld>-1 & filterFeld<daten) {
						if (!x[waren+filterFeld].equals(filterWert)) {
							nutzeDatensatz = false;
						}
					}
					if (nutzeDatensatz) {						
						for (int horizontal = 0; horizontal < breite; horizontal++) {
							inhalte[vertikal][horizontal] = x[horizontal];
						}
						vertikal++;
					}
				}
			}
		}
		System.out.println("Es gibt " + fehlerhaft + " fehlerhafte Dateninhalte.");
		System.out.println(vertikal + " Datensaetze wurden uebergeben.");
		return vertikal;
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

			//-- Speicher den gesammten Dateiinhalt als Liste --
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
