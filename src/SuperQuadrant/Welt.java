package SuperQuadrant;
import SuperQuadrant.Bloecke.*;

import java.awt.*;
import java.awt.Graphics;
import java.io.*;

/**
 *
 * Nimmt Tastatur- und Mauseingaben entgegen und aendert entsprechend die Welt
 * Enthaelt alle Zeichen-Methoden der Welt und die GUI des Typauswahlmenus
 * Ruft die Zeichen-Methoden aller Elemente auf und der beiden Spieler
 *
 * @author Helbig Christian www.krummbeine.de
 * @version 1.2
 */
public class Welt
{
    // Experimentell StachelmonsterBlock
    private StachelmonsterBlock monsterTest1;

    public static Spieler spieler1, spieler2;
    public static int zustand;
    public int ausgewaehlterTyp;
    public boolean typauswahlmenuAnzeigen;
    public int selektierterTyp;
    public static Point wiedereinstiegsPunkt;
    protected Kamera kamera;
    public static boolean einzelSpieler;
    public static int anzMuenzenGesamt = 0;
    public static boolean alleMuenzenEingesammelt = false;
    private int spielBeendetWarten;
    private String dateiName;
    public static int levelNummer = 0;
    private boolean ersterStart = true;


    /**
     * Konstruktor
     *  Wird von Konstruktor der Klasse Fenster aufgerufen
     * Bei Aufruf wird die Welt geladen
     */
    public Welt(String dateiName) {
        this.dateiName = dateiName;
        kamera = new Kamera(new Point(0, 0));
        spieler1 = new Spieler(new Point(Fenster.BREITE / 3, 20), -1, kamera, true, true);
        spieler2 = new Spieler(new Point(Fenster.BREITE - Fenster.BREITE / 3, 20), -2, kamera, false, true);

        ausgewaehlterTyp = 0;
        typauswahlmenuAnzeigen = false;
        selektierterTyp = -1;
        this.einzelSpieler = true;
        zustand = 0; // Zustand: Editor
        starten(false, Fenster.SKALIERUNG);
        this.spielBeendetWarten = 255;
        this.wiedereinstiegsPunkt = new Point(-1, -1);
    }

    /**
     * Aktualisiert die bewegten Elemente in der Welt, wenn das Spiel im Zustand 1 (spielen) ist.
     * Wird von der Methode update() der Klasse Fenster aufgerufen.
     * z.B. die beiden Spieler (Gehen, Fallen, Springen,..)
     * @param delta Staerke der Veraenderung bezueglich der verstrichenen Systemzeit
     */
    public void aktualisieren(int delta) {
        if(zustand == 1) {
            // Zuruecksetzen
            spieler1.istAufWolke = spieler2.istAufWolke = false;
            // Setzt istAufWolke moeglicherweise wieder auf true
            Element.aktualisiereAlle(delta);
            // istAufWolke-Wert ist aktuell
            spieler1.aktualisieren(delta);
            spieler2.aktualisieren(delta);
        }
    }

    /**
     * Zeichnet die Welt und ruft die einzelnen Zeichen-Methoden auf.
     * Wird von der Methode render() der Klasse Fenster aufgerufen.
     * Die Reihenfolge der Aufrufe der Methoden bestimmt, was verdeckt wird bzw. im Vordergrund ist.
     * Die zuletzt aufgerufene Zeichen-Methode ist z.B. ganz im Vordergrund.
     * @param g Die Oberflaeche, auf die gezeichnet werden soll
     */
    public void zeichnen(Graphics g) {
        // Groesse der Schriften festlegen
        g.setFont(g.getFont().deriveFont(30F));

        zeichneHintergrund(g);
        // zeichneWeltBegrenzung(g);
        zeichneSpieler(g);
        Element.zeichneAlle(g);
        zeichneAnzahlMuenzen(g);
        if(zustand != 1) {
            // Zustand ist nicht: es wird gespielt
            zeichnHebeQuadrantenHervorInDemSichDieMausBefindet(g);
            zeichneFarbAuswahlMenu(g);
            spieler1.leben = 0f;
            spieler2.leben = 0f;
        }
        else {
            if(einzelSpieler) {
                zeichneSpielerPositionsInfo(g, 1, Spieler.spielerFarben[0]);
                zeichneSpielerPositionsInfo(g, 2, Spieler.spielerFarben[0]);
            }
            else{
                zeichneSpielerPositionsInfo(g, 1, Spieler.spielerFarben[0]);
                zeichneSpielerPositionsInfo(g, 2, Spieler.spielerFarben[1]);
            }
        }
        zeichneTastaturbelegung(g);
    }

    /**
     * Zeichnet die Verbindungslinie zwischen den Spielern beim Mehrspieler-Modus
     * @param g Die Oberflaeche, auf die gezeichnet werden soll
     */
    private void zeichneSpielerVerbindungsLinie(Graphics g){
        int[] x = {
                (int)(spieler1.x - kamera.x + Fenster.GITTERGROESSE / 4),
                (int)(spieler1.x - kamera.x + Fenster.GITTERGROESSE - Fenster.GITTERGROESSE / 4),
                (int)(spieler2.x - kamera.x + Fenster.GITTERGROESSE - Fenster.GITTERGROESSE / 4),
                (int)(spieler2.x - kamera.x + Fenster.GITTERGROESSE / 4)
        };
        int[] y = {
                (int)(spieler1.y - kamera.y + Fenster.GITTERGROESSE / 4),
                (int)(spieler1.y - kamera.y + Fenster.GITTERGROESSE - Fenster.GITTERGROESSE / 4),
                (int)(spieler2.y - kamera.y + Fenster.GITTERGROESSE - Fenster.GITTERGROESSE / 4),
                (int)(spieler2.y - kamera.y + Fenster.GITTERGROESSE / 4)

        };

        // g.setColor(new Color(0, 0, 0, 50));
        g.fillPolygon(x, y, 4);
    }

    /**
     * Zeichnet die Weltbegrenzung
     * @param g Die Oberflaeche, auf die gezeichnet werden soll
     */
    private void zeichneWeltBegrenzung(Graphics g){
        g.setColor(new Color(0, 0, 0, 100));
        g.drawRect(
                0 - (int)kamera.x,
                0 - (int)kamera.y,
                5000,
                1
        );
        g.drawRect(
                0 - (int)kamera.x,
                0 - (int)kamera.y,
                1,
                5000
        );
    }

    /**
     * Zeichnet eine Anleitung zur Tastaturbelegung, wenn das
     * Spiel noch nie gestartet wurde
     * @param g Die Oberflaeche, auf die gezeichnet werden soll
     */
    private void zeichneTastaturbelegung(Graphics g){
        if(ersterStart){
            String[] anleitung = {
                    "Spieler1 steuern:               a w s d",
                    "Spieler2 steuern:               j i k l",
                    "",
                    "Einzelspieler:                    1",
                    "Mehrspieler:                      2",
                    "Level hoch/runter:             8 9",
                    "",
                    "Zurueck zu Editor:             x",
                    "Im Editor Karte bewegen:  a w s d",
                    "Element in Karte setzen:   Links-Klick",
                    "Element-Typ aendern:      Rechts-Klick"
            };
            for(int i = 0; i < anleitung.length; i++) {
                if(i % 2 == 0)
                    g.setColor(new Color(100, 34, 43, 200));
                else
                    g.setColor(new Color(39, 100, 69, 200));

                g.drawString(
                        anleitung[i],
                        10,
                        30 + i * 30
                );
            }
        }
    }

    /**
     * Zeichnet den Punktestand als Balken an den oberen Bildschirmrand.
     * @param g Die Oberflaeche, auf die gezeichnet werden soll
     */
    public void zeichneAnzahlMuenzen(Graphics g) {
        if(anzMuenzenGesamt > 0) {
            int anz1 = Fenster.BREITE * spieler1.muenzen / anzMuenzenGesamt;
            int anz2 = Fenster.BREITE * spieler2.muenzen / anzMuenzenGesamt;
            if(einzelSpieler){
                anz1 += anz2;
                anz2 = 0;
            }

            // Spieler 1
            g.setColor(new Color(
                    Spieler.spielerFarben[0].getRed(),
                    Spieler.spielerFarben[0].getGreen(),
                    Spieler.spielerFarben[0].getBlue(),
                    150)
            );
            g.fillRect(0, Fenster.HOEHE - Fenster.GITTERGROESSE / 2, anz1, Fenster.GITTERGROESSE / 2);

            // Spieler 2
            g.setColor(new Color(
                    Spieler.spielerFarben[1].getRed(),
                    Spieler.spielerFarben[1].getGreen(),
                    Spieler.spielerFarben[1].getBlue(),
                    150)
            );
            g.fillRect(anz1, Fenster.HOEHE - Fenster.GITTERGROESSE / 2, anz2, Fenster.GITTERGROESSE / 2);
        }
    }

    /**
     * Zeichnet den Hintergrund
     * @param g Die Oberflaeche, auf die gezeichnet werden soll
     */
    private void zeichneHintergrund(Graphics g){
        // Waehle die Hintergrundfarbe aus
        switch (zustand) {
            case 0:
                // Editor
                g.setColor(new Color(235, 230, 225));
                break;
            case 1:
                // Spielen
                g.setColor(new Color(245, 240, 235));
                break;
            case 2:
                // Gewonnen
                g.setColor(new Color(185, 240, 175, spielBeendetWarten));
                if(spielBeendetWarten > 0)
                    spielBeendetWarten -= 5;
                else {
                    spielBeendetWarten = 255;
                    dateiName = "level" + ++levelNummer;
                    zustand = 1; // Zustand: Spielen
                    starten(false, Fenster.SKALIERUNG);
                }
                break;
            case 3:
                // Verloren
                g.setColor(new Color(255, 60, 100, spielBeendetWarten));
                if(spielBeendetWarten > 0)
                    spielBeendetWarten -= 5;
                else{
                    spielBeendetWarten = 255;
                    if(wiedereinstiegsPunkt.x == -1) {
                        // Kein GoldregenBlock wurde aktiviert --> vollstaendig verloren
                        zustand = 1;
                        starten(true, Fenster.SKALIERUNG);
                    }
                    else {
                        // Ein GoldregenBlock wurde aktiviert --> Dort wiederbeleben
                        spieler1.x = wiedereinstiegsPunkt.x;
                        spieler2.x = wiedereinstiegsPunkt.x;
                        spieler1.y = wiedereinstiegsPunkt.y;
                        spieler2.y = wiedereinstiegsPunkt.y;
                        // Sicherstellen, dass vergangene Tastatur-Eingaben nicht mehr aktiv sind (zuruecksetzen)
                        Welt.spieler1.sollLinksGehen = Welt.spieler1.sollRechtsGehen = Welt.spieler1.sollSpringen = false;
                        Welt.spieler2.sollLinksGehen = Welt.spieler2.sollRechtsGehen = Welt.spieler2.sollSpringen = false;
                        Welt.spieler1.gehkraft = Welt.spieler1.sprungkraft = 0f;
                        Welt.spieler2.gehkraft = Welt.spieler2.sprungkraft = 0f;
                        // Beide Spieler erhalten volles Leben
                        Welt.spieler2.leben = 1f;
                        Welt.spieler1.leben = 1f;
                        zustand = 1;
                        kamera.x = (int)(wiedereinstiegsPunkt.x - Fenster.BREITE / 2);
                        kamera.y = (int)(wiedereinstiegsPunkt.y - Fenster.HOEHE / 2);
                    }
                }
                break;
            case 4:
                // Level manuell zurueck schalten
                g.setColor(new Color(185, 240, 175, spielBeendetWarten));
                if(spielBeendetWarten > 0)
                    spielBeendetWarten -= 5;
                else {
                    spielBeendetWarten = 255;
                    dateiName = "level" + --levelNummer;
                    zustand = 1; // Zustand: Spielen
                    starten(false, Fenster.SKALIERUNG);
                }
                break;
        }
        // Zeichne ein Rechteck ueber das gesamte Fenster
        g.fillRect(
                0,                  // x
                0,                  // y
                Fenster.BREITE,     // Breite
                Fenster.HOEHE       // Hoehe
        );
    }

    /**
     * Hebt den Quadranten leicht hervor, in dem sich die Maus befindet
     * @param g Die Oberflaeche, auf die gezeichnet werden soll
     */
    private void zeichnHebeQuadrantenHervorInDemSichDieMausBefindet(Graphics g){
        // Waehle Farbe aus
        g.setColor(new Color(
                Element.typFarben[ausgewaehlterTyp].getRed(),
                Element.typFarben[ausgewaehlterTyp].getGreen(),
                Element.typFarben[ausgewaehlterTyp].getBlue(),
                200
        ));

        // Definiere den Quadranten, in dem sich die Maus befindet
        Point quadrant = new Point(
                Fenster.posToGitterPos((int)(Maus.x + kamera.x)),
                Fenster.posToGitterPos((int)(Maus.y + kamera.y))
        );

        // Zeichne den Quadranten, in dem sich die Maus befindet
        g.fillRect(
                (int)(quadrant.x - kamera.x),  // x
                (int)(quadrant.y - kamera.y),  // y
                Fenster.GITTERGROESSE - 1,  // Breite
                Fenster.GITTERGROESSE - 1  // Hoehe
        );
    }

    /**
     * Ruft die Zeichen-Methoden beider Spieler auf
     * @param g Die Oberflaeche, auf die gezeichnet werden soll
     */
    private void zeichneSpieler(Graphics g){
        spieler1.zeichnen(g);
        spieler2.zeichnen(g);
    }

    /**
     * Zeichnet das Farbauswahlmenu
     * Zeichnet eine Liste von in Element definierten verfuegbaren Element-Typen
     * @param g Die Oberflaeche, auf die gezeichnet werden soll
     */
    private void zeichneFarbAuswahlMenu(Graphics g){
        if(typauswahlmenuAnzeigen){

            // Standardmaessig ist kein Typ selektiert
            selektierterTyp = -1;

            // Zeichne alle Element-Typen untereinander
            for(int i = 0; i < Element.typFarben.length; i++){
                Rectangle rec = new Rectangle(
                        0,                          // x
                        i * Fenster.GITTERGROESSE,  // y
                        Fenster.GITTERGROESSE * 6,  // Breite
                        Fenster.GITTERGROESSE       // Hoehe
                );

                // Waehle die Farbe des Typs aus mit Alpha-Wert 100 (transparent)
                g.setColor(new Color(
                        Element.typFarben[i].getRed(),     // r
                        Element.typFarben[i].getGreen(),   // g
                        Element.typFarben[i].getBlue(),    // b
                        100                             // a
                ));

                if(inRect(new Point(Maus.x, Maus.y), rec)){
                    // Maus befindet sich auf einem Element-Typ in der Liste
                    selektierterTyp = i;
                    // Waehle die Farbe des Typs aus, aber mit Alpha-Wert 255 (nicht transparent, also hervorgehoben)
                    g.setColor(new Color(
                            Element.typFarben[i].getRed(),     // r
                            Element.typFarben[i].getGreen(),   // g
                            Element.typFarben[i].getBlue(),    // b
                            255                             // a
                    ));
                }

                // Zeichne ein Rechteck mit der ausgewaehlten Farbe
                g.fillRect(
                        rec.x,          // x
                        rec.y,          // y
                        rec.width,      // Breite
                        rec.height      // Hoehe
                );

                g.setColor(new Color(255, 255, 255));
                g.drawString(
                        Element.typFarbenBeschreibungen[i],
                        rec.x,
                        rec.y + rec.height
                        );
            }
        }
    }

    /**
     * Zeichnet eine Information, wo sich der Spieler2 befindet, wenn er außerhalb des sichtbaren Bereichs befindet
     * und die Kamera Spieler1 folgt
     * @param g Die Oberflaeche, auf die gezeichnet werden soll
     */
    private void zeichneSpielerPositionsInfo(Graphics g, int spieler, Color spielerFarbe){
        if(zustand == 1) {
            // Zustand: es wird gespielt

            if (spieler1.leben != 0 && spieler2.leben != 0) {
                // Kamera verfolgt Spieler1.. daher kann Spieler2 aus dem sichtbaren Ausschnitt laufen
                // Es ist eine Positions-Info erforderlich, um zu wissen, wo außerhalb des Ausschnitts
                // er sich aufhaelt

                // Die Spielerposition im Ausschnitt
                Point spielerImAusschnitt = new Point(0, 0);

                if(spieler == 1){
                    spielerImAusschnitt = new Point(
                            (int)(spieler1.x - kamera.x),
                            (int)(spieler1.y - kamera.y)
                    );
                }
                else {
                    if (spieler == 2) {
                        spielerImAusschnitt = new Point(
                                (int) (spieler2.x - kamera.x),
                                (int) (spieler2.y - kamera.y)
                        );
                    }
                }

                Point infoPos = new Point(0, 0);
                int anzImAusschnitt = 0;

                // x // // // // // // // // // // // // // // // // // //
                if(spielerImAusschnitt.x < 0) {
                    // Spieler2.x ist links außerhalb des Ausschnitts
                    infoPos.x = 0;
                }
                else if(spielerImAusschnitt.x > Fenster.BREITE - Fenster.GITTERGROESSE){
                    // Spieler2.x ist rechts außerhalb des Ausschnitts
                    infoPos.x = Fenster.BREITE - Fenster.GITTERGROESSE;
                }
                else {
                    // Spieler2.x ist im Ausschnitt
                    infoPos.x = spielerImAusschnitt.x;
                    anzImAusschnitt++;
                }

                // Y // // // // // // // // // // // // // // // // // //
                if(spielerImAusschnitt.y < 0) {
                    // Spieler2.x ist oben außerhalb des Ausschnitts
                    infoPos.y = 0;
                }
                else if(spielerImAusschnitt.y > Fenster.HOEHE - Fenster.GITTERGROESSE){
                    // Spieler2.x ist unten außerhalb des Ausschnitts
                    infoPos.y = Fenster.HOEHE - Fenster.GITTERGROESSE;
                }
                else {
                    // Spieler2.x ist im Ausschnitts
                    infoPos.y = spielerImAusschnitt.y;
                    anzImAusschnitt++;
                }

                if(anzImAusschnitt != 2) {
                    // Die Positions-Info soll nur angezeigt werden, wenn der Spieler entweder
                    // horizontal oder vertikal außerhalb des sichtbaren Ausschnitts liegt.

                    g.setColor(new Color(0, 0, 0, 200));
                    g.fillRect(
                            infoPos.x - 8,
                            infoPos.y - 8,
                            Fenster.GITTERGROESSE + 16,
                            Fenster.GITTERGROESSE + 16
                    );

                    g.setColor(new Color(255, 255, 255, 255));
                    g.fillRect(
                            infoPos.x - 6,
                            infoPos.y - 6,
                            Fenster.GITTERGROESSE + 12,
                            Fenster.GITTERGROESSE + 12
                    );

                    g.setColor(spielerFarbe);
                    g.fillRect(
                            infoPos.x,
                            infoPos.y,
                            Fenster.GITTERGROESSE - 1,
                            Fenster.GITTERGROESSE - 1
                    );
                }
            }
        }
    }

    /**
     * @param pos Die Position, die es zu pruefen gilt
     * @param rect Das Rechteck, in dem die Position sich befinden oder nicht
     * @return gibt zurueck, ob sich ein Punkt pos in einem Rechteck rect befindet
     */
    static boolean inRect(Point pos, Rectangle rect){
        if(pos.x > rect.x && pos.x < rect.x + rect.width){
            if(pos.y > rect.y && pos.y < rect.y + rect.height){
                return true;
            }
        }
        return false;
    }

    /**
     * Verwaltet, wie die Welt auf Tastatureingaben reagiert
     *
     * Tasten fuer spieler1 (a: links d: rechts w: springen)
     * Tasten fuer spieler2 (j: links l: rechts i: springen)
     * Wird z.B. die Taste [a] gedrueckt, wird im spieler1 sollLinksgehen auf true gesetzt,
     * wird sie losgelassen, wird sollLinksgehen auf false gesetzt.
     *
     * Tasten fuer die welt (m: speichern y: laden)
     * Wird die Taste [m] betaetigt, wird die Methode speichern aufgerufen,
     * mit der Taste [y] wird die Methode laden aufgerufen.
     *
     * @param taste Die Taste, die betaetigt wurde
     * @param gedrueckt Taste wurde gedrueckt (true), Taste wurde losgelassen (false)
     */
    public void tasteBetaetigt(char taste, boolean gedrueckt) throws IOException {
        Spieler awsdGesteuerterSpieler = spieler1;
        if(einzelSpieler){
            if(spieler1.leben == 0) {
                awsdGesteuerterSpieler = spieler2;
                spieler1.verfolgtVonKamera = false;
                spieler2.verfolgtVonKamera = !spieler1.verfolgtVonKamera;
            } else{
                spieler1.verfolgtVonKamera = true;
                spieler2.verfolgtVonKamera = !spieler1.verfolgtVonKamera;
            }
        }
        switch (taste) {
            case 'a': // Links
                if (zustand == 1)
                    awsdGesteuerterSpieler.sollLinksGehen = gedrueckt;
                else
                    kamera.x -= Fenster.GITTERGROESSE;
                break;
            case 'd': // Rechts
                if (zustand == 1)
                    awsdGesteuerterSpieler.sollRechtsGehen = gedrueckt;
                else
                    kamera.x += Fenster.GITTERGROESSE;
                break;
            case 'w': // Hoch
                if (zustand == 1)
                    awsdGesteuerterSpieler.sollSpringen = gedrueckt;
                else
                    kamera.y -= Fenster.GITTERGROESSE;
                break;
            case 's': // Runter
                if (zustand != 1)
                    kamera.y += Fenster.GITTERGROESSE;
                break;
            case 'j': // Links
                spieler2.sollLinksGehen = gedrueckt;
                break;
            case 'l': // Rechts
                spieler2.sollRechtsGehen = gedrueckt;
                break;
            case 'i': // Oben
                spieler2.sollSpringen = gedrueckt;
                break;
            case 'm': // Speichern
                speichern(dateiName);
                break;
            case 't': // Tod
                spieler1.leben = 0;
                spieler2.leben = 0;
                break;
            case 'x': // Zurueck in den Editor wechseln
                laden(dateiName);
                zustand = 0; // Zustand: Editor
                break;
            case '1': // Einzelspieler
                einzelSpieler = true;
                zustand = 1; // Zustand: Spielen
                starten(true, Fenster.SKALIERUNG);
                ersterStart = false;
                break;
            case '2': // Zwei Spieler
                einzelSpieler = false;
                zustand = 1; // Zustand: Spielen
                starten(true, Fenster.SKALIERUNG);
                ersterStart = false;
                break;
            case '8': // Vorheriges Level manuell schalten
                zustand = 4;
                break;
            case '9': // Naechstes Level manuell schalten
                zustand = 2;
                break;
            /* // Nicht funktionsfähig bisher
            case '+': // Reinzoomen
                if(Fenster.SKALIERUNG < 3f)
                starten(true, Fenster.SKALIERUNG += 0.5f);
                break;
            case '-': // Rausuoomen
                if(Fenster.SKALIERUNG > 0.5f)
                starten(true, Fenster.SKALIERUNG -= 0.5f);
                break;
            */
        }
    }

    /**
     * Positioniert die Kamera so, dass das Start-Element in der Mitte vom Fenster liegt
     * Positioniert die Spieler so, dass sie aus dem Start-Element fallen
     * @param sollSpeichern Gibt an, ob vor dem starten die Welt gespeichert werden soll.
     */
    private void starten(boolean sollSpeichern, float neueSkalierung) {
        if(sollSpeichern)
            speichern(dateiName);

        laden(dateiName);

        Point startBlockPos = Element.findeStartBlock();
        if (startBlockPos.x == -1000) {
            // Kein StartBlock existiert - moeglicherweise leere Welt
            kamera = new Kamera(new Point(5000, 5000));
        } else {
            // StartBlock existiert
            kamera = new Kamera(new Point(
                    startBlockPos.x - Fenster.BREITE / 2,
                    startBlockPos.y - Fenster.HOEHE / 2
            ));
        }
        spieler1 = new Spieler(Element.findeStartBlock(), -1, kamera, true, true);
        spieler2 = new Spieler(Element.findeStartBlock(), -2, kamera, false, true);
        punktestandAktualisieren();
        wiedereinstiegsPunkt = new Point(-1, -1);
    }

    /**
     * Aktualisiert den Punktestand.
     * Wird aufgerufen, wenn eine Muenze eingesammelt wird innherlab der Klasse MuenzenBlock
     */
    public static void punktestandAktualisieren(){
        anzMuenzenGesamt = Element.anzMuenzenBloeckeGesamt();
        alleMuenzenEingesammelt = Spieler.anzMuenzenGesamt() == anzMuenzenGesamt;
    }

    /**
     * Verwaltet Links-Klicks der Maus
     *
     * Ist das Typauswahlmenu aktiv, wird es beim Klick geschlossen und falls ein Typ in der Typauswahlmenu-Liste
     * selektiert wurde, wird dieser Typ ausgewaehlt
     *
     * Ist das Typauswahlmenu nicht aktiv, wird im angeklickten Quadranten ein vorhandenes Element entfernt und
     * bei einem leeren Quadranten ein neues Element erstellt des ausgewaehlten Typs
     *
     * @param mausPosition Die Position(x, y) der Maus waehrend des Klicks
     */
    public void linkeMaustasteGeklickt(Point mausPosition){
        ersterStart = false;
        if(zustand != 1) {
            if (typauswahlmenuAnzeigen) {
                if (selektierterTyp != -1)
                    // Ein Typ wurde aus der Liste selektiert UND angeklickt
                    ausgewaehlterTyp = selektierterTyp;
                // Typauswahlmenu nach Klick wieder schliessen
                typauswahlmenuAnzeigen = false;
            } else {
                // Typauswahlmenu nicht aktiv
                Point ausgewaehlterQuadrant = new Point(
                        Fenster.posToGitterPos((int)(mausPosition.x + kamera.x)),
                        Fenster.posToGitterPos((int)(mausPosition.y + kamera.y))
                );
                if (ausgewaehlterQuadrant.x >= 0 && ausgewaehlterQuadrant.y >= 0) {
                    // der Quadrant liegt im positivem Koordinatenbereich

                    if (Element.existiert(ausgewaehlterQuadrant, false))
                        // Loesche angeklicktes Element im ausgewaehlten Quadranten
                        Element.entfernen(new Point(ausgewaehlterQuadrant.x, ausgewaehlterQuadrant.y));
                    else
                        // Fuege Element dem leeren ausgewaehlten Quadranten hinzu
                        elementHinzufuegen(ausgewaehlterQuadrant.x, ausgewaehlterQuadrant.y, ausgewaehlterTyp);
                }
            }
        }
    }

    /**
     * Zeigt das Typauswahlmenu an, wenn mit der rechten Maustaste geklickt wurde
     */
    public void rechteMaustasteGeklickt(){
        typauswahlmenuAnzeigen = true;
        ersterStart = false;
    }

    /**
     * Speichert die Welt in eine Textdatei
     *
     * @param weltName Der Name der Textdatei, in der die Welt gespeichert ist
     */
    public void speichern(String weltName) {
        try {
            FileWriter fw = new FileWriter(weltName + ".txt");
            BufferedWriter bw = new BufferedWriter(fw);

            Element elem = Element.kopf;

            while (elem != null) {
                if(elem.sollGespeichertWerden) {
                    // Element nur speichern, wenn es gespeichertWerdenSoll
                    // Elemente, die durch den ZufallsBlock erstellt wurden, sollen nicht gespeichert werden!
                    int saveX = (int) (elem.x / Fenster.SKALIERUNG);
                    int saveY = (int) (elem.y / Fenster.SKALIERUNG);
                    if (elem.typ == 3 || elem.typ == 10 || elem.typ == 12 || elem.typ == 14 || elem.typ == 15|| elem.typ == 17
                            || elem.typ == 18 || elem.typ == 19) {
                        // Bei bewegten Elementen muss die StartPosition und nicht x/y gesichert werden.
                        // Es hat sich bewegt und wir wollen StartPosition speichern! Wichtig
                        saveX = (int) (elem.getStartPosition().x / Fenster.SKALIERUNG);
                        saveY = (int) (elem.getStartPosition().y / Fenster.SKALIERUNG);
                    }
                    bw.write(saveX + "," + saveY + "," + elem.typ);
                    bw.newLine();
                }
                elem = elem.naechstes;
            }

            bw.close();
        }
        catch (Exception ex) {
            System.out.println(ex);
        }
    }

    /**
     * Laedt die Welt aus einer Textdatei
     *
     * @param weltName Der Name der Textdatei, in der die Welt gespeichert wurde
     */
    private void laden(String weltName){
        try {
            Element.kopf = null;

            FileReader fileReader = new FileReader(weltName + ".txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while((line = bufferedReader.readLine()) != null )
            {
                String[] parts = line.split(",");
                elementHinzufuegen(
                        (int) (Integer.parseInt(parts[0]) * Fenster.SKALIERUNG),
                        (int) (Integer.parseInt(parts[1]) * Fenster.SKALIERUNG),
                        Integer.parseInt(parts[2])
                );
            }
            bufferedReader.close();
        }
        catch (Exception ex) {
            System.out.println(ex);
        }
    }

    /**
     * Fuegt der Welt ein Element (Block) hinzu
     * @param x Die Position, wo der Block erstellt werden soll
     * @param y Die y-Position, wo der Block erstellt werden soll
     * @param typ Der Typ des neuen Blocks
     */
    public void elementHinzufuegen(int x, int y, int typ){
        switch(typ) {
            case 19:
                // MutterMine
                Element.hinzufuegen(
                        new MutterMine(
                                new Point(x, y), typ, 0.3f, kamera, false, true
                        )
                );
                break;
            case 18:
                // VaterMine
                Element.hinzufuegen(
                        new VaterMine(
                                new Point(x, y), typ, 0.3f, kamera, false, true
                        )
                );
                break;
            case 17:
                // Wolke
                Element.hinzufuegen(
                        new Wolke(
                                new Point(x, y), typ, 0.1f, kamera, false, true
                        )
                );
                break;
            case 16:
                // Gras
                Element.hinzufuegen(
                        new GrasBlock(
                                new Point(x, y), typ, 0f, kamera, false, true
                        )
                );
                break;
            case 15:
                // SpielermonsterBlock folgt Spieler 2
                Element.hinzufuegen(
                        new SpielermonsterBlock(
                                new Point(x, y), typ, 0.25f, kamera, false, true, 2
                        )
                );
                break;
            case 14:
                // SpielermonsterBlock folgt Spieler 1
                Element.hinzufuegen(
                        new SpielermonsterBlock(
                                new Point(x, y), typ, 0.25f, kamera, false, true, 1
                        )
                );
                break;
            case 13:
                // SpeicherpunktBlock
                Element.hinzufuegen(
                        new SpeicherpunktBlock(
                                new Point(x, y), typ, 0f, kamera, false, true
                        )
                );
                break;
            case 12:
                // MuenzenRotBlock
                Element.hinzufuegen(
                        new MuenzenRotBlock(
                                new Point(x, y), typ, 0.5f, kamera, false, true
                        )
                );
                break;
            case 11:
                // GoldregenBlock
                Element.hinzufuegen(
                        new GoldregenBlock(
                                new Point(x, y), typ, 0f, kamera, false, true
                        )
                );
                break;
            case 10:
                // KopfmonsterBlock
                Element.hinzufuegen(
                        new KopfmonsterBlock(
                                new Point(x, y), typ, 0.3f, kamera, false, true
                        )
                );
                break;
            case 9:
                // FeuerBlock
                Element.hinzufuegen(
                        new FeuerBlock(
                                new Point(x, y), typ, 0f, kamera, false, true
                        )
                );
                break;
            case 8:
                // TrampolinBlock
                Element.hinzufuegen(
                        new TrampolinBlock(
                                new Point(x, y), typ, 0f, kamera, false, true
                        )
                );
                break;
            case 7:
                // ZufallsBlock
                Element.hinzufuegen(
                        new ZufallsBlock(
                                new Point(x, y), typ, 0f, kamera, false, true
                        )
                );
                break;
            case 6:
                // WiederbelebungsBlock
                Element.hinzufuegen(
                        new WiederbelebungsBlock(
                                new Point(x, y), typ, 0f, kamera, false, true
                        )
                );
                break;
            case 5:
                // MuenzenBlock
                Element.hinzufuegen(
                        new MuenzenBlock(
                                new Point(x, y), typ, 0f, kamera, false, true
                        )
                );
                break;
            case 4:
                // ZielBlock
                Element.hinzufuegen(
                        new ZielBlock(
                                new Point(x, y), typ, 0f, kamera, false, true
                        )
                );
                break;
            case 3:
                // StachelmonsterBlock
                Element.hinzufuegen(
                        new StachelmonsterBlock(
                                new Point(x, y), typ, 0.2f, kamera, false, true
                        )
                );
                break;
            case 2:
                Point startBlockQuadrant = Element.findeStartBlock();
                if (startBlockQuadrant.x != -1000) {
                    // Es existiert ein StartBlock.. soll neu gesetzt werden
                    Element.entfernen(startBlockQuadrant);
                }
                Element.hinzufuegen(
                        new StartBlock(
                                new Point(x, y), typ, 0f, kamera, false, true
                        )
                );
                break;
            default:
                // normales Element (Block)
                Element.hinzufuegen(
                        new Element(
                                new Point(x, y), typ, 0f, kamera, false, true
                        )
                );
                break;
        }
    }
}