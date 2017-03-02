package SuperQuadrant;

import SuperQuadrant.Bloecke.Spieler;

import java.awt.*;

/**
 * Die Verwaltung der Elemente des Spiels (Bloecke, Spieler, Monster,..).
 * Bietet Methoden zum Hinzufuegen, Finden, Entfernen, Zeichnen,.. von Elementen
 *
 * @author Helbig Christian www.krummbeine.de
 * @version 1.2
 */
public class Element
{
    public float geschwindigkeit;
    public float sprungkraft;
    public float gehkraft;
    protected boolean faelltGerade;
    protected boolean springtGerade;
    protected boolean gehtGerade;
    public boolean verfolgtVonKamera;
    public boolean sollLinksGehen;
    public boolean sollRechtsGehen;
    public boolean sollSpringen;
    //
    protected boolean ausgeloest;
    public float x, y;
    public int typ;
    public static Kamera kamera;
    public static Element kopf;
    public Element naechstes;
    private Point startPosition;
    public boolean sollGespeichertWerden;

    // Die Farben der einzelnen Typen
    public static Color typFarben[] = new Color[]{
            new Color(0, 0, 0),       // 0: Fester-Block
            new Color(91, 153, 127),    // 1: Fester-Block
            new Color(0, 0, 255),     // 2: Start-Block
            new Color(255, 0, 0),     // 3: StachelmonsterBlock-Block
            new Color(0, 205, 120),   // 4: Ziel-Block
            new Color(250, 201, 108), // 5: Muenzen-Block
            new Color(0, 0, 255),     // 6: Regenerierungs-Block
            new Color(255, 150, 155), // 7: Zufalls-Block
            new Color(255, 0, 255),   // 8: Trampolin-Block
            new Color(255, 160, 0),   // 9: Feuer-Block
            new Color(25, 50, 120),   // 10: KopfmonsterBlock-Block
            new Color(250, 105, 70),  // 11: Goldregen-Block
            new Color(250, 105, 70),  // 12: MuenzenRot-Block
            new Color(110, 220, 170), // 13: Speicherpunkt-Block
            new Color(0, 0, 0),       // 14: Spielermonster-Block folgt Spieler 1
            new Color(0, 0, 0),       // 15: Spielermonster-Block folgt Spieler 2
            new Color(66, 165, 49),   // 16: Gras-Block
            new Color(255, 250, 255), // 17: Wolke
            new Color(0, 0, 0),   // 18: VaterMine
            new Color(184, 73, 72)    // 19: MutterMine
    };

    public static String typFarbenBeschreibungen[] = new String[]{
            "Fest",
            "Fest",
            "Start",
            "Stachel",
            "Ziel",
            "G Muenze",
            "Regeneri",
            "Zufall",
            "Trampolin",
            "Feuer",
            "Kopfmo",
            "Goldregen",
            "R Muenze",
            "Speichpunkt",
            "Schattenmonster 1",
            "Schattenmonster 2",
            "Gras",
            "Wolke",
            "VaterMine",
            "MutterMine"
    };

    /**
     * Konstruktor.
     * Wird von Konstruktor der Klasse Spieler aufgerufen (Spieler extends Element).
     * Wird von den Methoden linkeMaustasteGeklickt() und laden() der Klasse Welt aufgerufen.
     * @param kamera Die Kamera
     * @param mitKameraVerfolgen Gibt an, ob das Element von der Kamera verfolgt werden soll
     * @param typ Der Typ des Elements
     * @param startPosition Die StartPosition des Elements
     * @param geschwindigkeit Die Geschwindigkeit des Elements
     * @param sollGespeichertWerden Elemente, die z.B. durch ZufallsBlock erstellt wurden, sollen nicht gespeichert werden.
     */
    public Element(Point startPosition, int typ, float geschwindigkeit, Kamera kamera, boolean mitKameraVerfolgen, boolean sollGespeichertWerden) {
        this.startPosition = startPosition;
        this.x = startPosition.x;
        this.y = startPosition.y;
        this.typ = typ;
        this.geschwindigkeit = geschwindigkeit;
        this.kamera = kamera;
        this.verfolgtVonKamera = mitKameraVerfolgen;
        this.sollGespeichertWerden = sollGespeichertWerden;
        this.faelltGerade = false;
        this.gehtGerade = false;
        this.springtGerade = false;
        this.sollLinksGehen = false;
        this.sollRechtsGehen = false;
        this.sollSpringen = false;
        this.ausgeloest = false;
        typFarben[14] = Spieler.spielerFarben[0];
        typFarben[15] = Spieler.spielerFarben[1];
    }

    /**
     * Diese Methode ueberschreiben wie z.B. im StachelmonsterBlock, um die StartPosition speichern zu koennen bei
     * Elementen, die sich bewegten. x/y ist bei bewegten Elementen nicht mehr die StartPosition!
     * @return Gibt die StartPosition zurueck
     */
    public Point getStartPosition(){
        return startPosition;
    }

    /**
     * Waehlt die zu zeichende Farbe bezueglich des Typs aus
     * Zeichnet unbewegte Elemente
     * Methode wird durch z.B. Spieler.zeichnen oder StachelmonsterBlock.zeichnen ueberschrieben
     * @param g Die grafische Oberflaeche, auf der gezeichnet wird
     */
    public void zeichnen(Graphics g) {
        g.setColor(new Color(
                Element.typFarben[typ].getRed(),    // r
                Element.typFarben[typ].getGreen(),  // g
                Element.typFarben[typ].getBlue(),   // b
                255)                                // a
        );

        // Element zeichnen
        g.fillRect(
                (int) (x - kamera.x),              // x
                (int) (y - kamera.y),              // y
                Fenster.GITTERGROESSE - 1,  // Breite
                Fenster.GITTERGROESSE - 1   // Hoehe
        );
    }

    /**
     * Funktion, die durch z.B. aktualisierne-Methode im StachelmonsterBlock ueberschrieben wird
     * @param delta  Staerke der Veraenderung bezueglich der verstrichenen Systemzeit
     */
    public void aktualisieren(int delta){
        // nichts tun
        // wird durch z.B. aktualisieren-Methode im StachelmonsterBlock ueberschrieben
    }

    /**
     * Fuegt ein Element der Liste hinzu
     * @param element Das hinzuzufuegende Element
     */
    public static void hinzufuegen(Element element) {
        if(Element.kopf == null){
            // Fuege erstes Element der Liste hinzu
            Element.kopf = element;
        }
        else{
            // Fuege ein weiteres Element der Liste hinzu
            element.naechstes = Element.kopf;
            Element.kopf = element;
        }
    }

    /**
     * Prueft, ob ein Element an einer Position(x, y) existiert
     * @param quadrant Der Quadrant, der geprueft werden soll
     * @param typBeachten Gibt an, ob der Typ bei der Erkennung beachtet werden soll
     * @return Gibt zurueck, ob das Element an der Position existiert
     */
    public static boolean existiert(Point quadrant, boolean typBeachten){
        Element aktuelles = kopf;
        // Gehe gesamte Liste durch
        while(aktuelles != null){
            if(quadrant.x == (int)aktuelles.x && quadrant.y == (int)aktuelles.y) {
                // Quadrant ist der selbe, den auch das aktuelle Element belegt
                if(!typBeachten)
                    return true;
                else{
                    if(aktuelles.typ < 2 || aktuelles.typ == 7 || aktuelles.typ == 11) {
                        // Der Startblock, Monsterbloecke, usw. koennen durchquert werden
                        // Feste Bloecke vom Typ 0 und Typ 1 nicht.
                        // Auch nicht der Zufalls-Block und GoldregenBlock.
                        return true;
                    }
                }
            }
            aktuelles = aktuelles.naechstes;
        }
        return false;
    }

    /**
     * Entfernt ein Element, wenn es einen uebergebenen Quadranten belegt
     * @param quadrant Der Quadrant(x, y)
     */
    public static void entfernen(Point quadrant){
        Element aktuelles = kopf;
        Element previous = null;
        while(aktuelles != null && (quadrant.x != aktuelles.x || quadrant.y != aktuelles.y)){
            previous = aktuelles;
            aktuelles = aktuelles.naechstes;
        }
        if (aktuelles != null) {
            if (previous == null)
                kopf = aktuelles.naechstes;
            else
                previous.naechstes = aktuelles.naechstes;
        }
    }

    /**
     * Zeichnet alle Elemente
     * @param g Die grafische Oberflaeche, auf der gezeichnet wird
     */
    public static void zeichneAlle(Graphics g){
        Element elem = kopf;
        while(elem != null){
            elem.zeichnen(g);
            elem = elem.naechstes;
        }
    }

    /**
     * Aktualisiert alle Elemente
     * @param delta  Staerke der Veraenderung bezueglich der verstrichenen Systemzeit
     */
    public static void aktualisiereAlle(int delta){
        Element elem = kopf;
        while(elem != null){
            elem.aktualisieren(delta);
            elem = elem.naechstes;
        }
    }

    /**
     * @return Gibt den Quadranten zurueck, den das Start-Element (typ 2) belegt
     */
    public static Point findeStartBlock(){
        Element aktuelles = kopf;
        while(aktuelles != null){
            if(aktuelles.typ == 2)
                return new Point((int)aktuelles.x, (int)aktuelles.y);
            aktuelles = aktuelles.naechstes;
        }
        // Kein StartBlock gefunden!
        return new Point(-1000, -1000);
    }

    /**
     * Wird benoetigt, um im ZufallsBlock ueberschrieben zu werden, damit der Zustand ausgelesen werden kann.
     * @return Gibt zurueck, ob der Block ausgeloest wurde.
     */
    protected boolean getAusgeloest(){
        return  ausgeloest;
    }

    /**
     * Berechnet die insgesamt einsammelbaren Muenzen.
     * Typ5: MuenzenBlock, Typ7:Zufallsblock (erzeugt Muenze)
     * @return Gibt die Anzahl der einsammelbaren Muenzen zurueck
     */
    public static int anzMuenzenBloeckeGesamt(){
        Element elem = kopf;
        int anz = 0;
        while(elem != null){
            if(elem.typ == 5) // Muenze gefunden (einfacher Wert)
                anz++;
            if(elem.typ == 12) // MuenzeRot gefunden (doppelter Wert)
                anz+= 2;
            if(elem.typ == 7 && !elem.getAusgeloest()) // unausgeloester ZufallsBlock gefunden, der Muenze enthaelt!
                anz++;
            if(elem.typ == 11 && !elem.getAusgeloest()) // unausgeloester GoldregenBlock gefunden, der 5 RedMuenzen enthaelt!
                anz+= 10;
            elem = elem.naechstes;
        }
        return anz;
    }


    // Kollisionserkennung // // // // // // // // // // // // // // // // // // // // // // // // // // // // //

    /**
     * Laesst das Element nach unten fallen, wenn es kein Element unter ihm beruehrt.
     * Setzt das Element nahtlos auf ein moegliches unteres Element
     * @param delta Staerke der Veraenderung bezueglich der verstrichenen Systemzeit
     */
    protected void fallen(int delta){
        // Die Quadranten-Position des derzeitigen Elements, das fallen soll
        // y-Ausgangs-Position für Kollisionserkennung muss am oberen Rand des Elements liegen
        // und nicht in der Mitte (- Fenster.GITTERGROESSE / 2) ! Wir schauen, was nathlos unter dem Element liegt,
        // indem wir + Fenster.GITTERGROESSE rechnen. Durch diese Ausgangs-Position stimmt die Rechnung
        // Die Quadranten-Position des Elementes unter dem fallendem Element
        Point quadrantUnterElement = Fenster.posToPosMitteQuadrant((int)x, (int)y + Fenster.GITTERGROESSE / 2);

        // Dieser Quadrant unter dem Element MUSS frei sein, sonst kann das Element nicht fallen!
        if(Element.existiert(quadrantUnterElement, true)){
            // Element unter fallendem Element <=> Fallen nicht möglich
            faelltGerade = false;
            // Element nahtlos auf Boden (unteres Element) ansetzen
            //y = quadrantElement.y;
        }
        else{
            // Kein Element unter fallendem Element <=> Fallen moeglich.
            if(typ != 14 && typ != 15)
                y += 1f * Fenster.SKALIERUNG_KORREKTUR * delta;
            else
                y += 0.3f * Fenster.SKALIERUNG_KORREKTUR * delta;

            faelltGerade = true;
        }
    }

    /**
     * Laesst das Element nach oben springen, wenn ueber ihm kein Element im Weg ist
     * @param delta Staerke der Veraenderung bezueglich der verstrichenen Systemzeit
     */
    protected void springen(int delta){
        if (sprungkraft > 0) {
            // Die Quadranten-Position des derzeitigen Elements, das springen soll
            // y-Ausgangs-Position für Kollisionserkennung muss am unteren Rand des Elements liegen
            // und nicht in der Mitte (+ Fenster.GITTERGROESSE / 2) ! Wir schauen, was nathlos ueber dem Element liegt,
            // indem wir - Fenster.GITTERGROESSE rechnen. Durch diese Ausgangs-Position stimmt die Rechnung
            Point quadrantElement = Fenster.posToPosMitteQuadrant((int)x, (int)y +  Fenster.GITTERGROESSE / 2);
            // Die Quadranten-Position des Elementes ueber dem springendem Element
            Point quadrantUeberElement = new Point(
                    quadrantElement.x,
                    quadrantElement.y - Fenster.GITTERGROESSE
            );
            // Dieser Quadrant ueber dem Element MUSS frei sein, sonst kann das Element nicht springen!
            if (Element.existiert(quadrantUeberElement, true)) {
                // Element ueber springendem Element <=> Springen nicht möglich
                sprungkraft = 0;
            } else {
                // Kein Element ueber springendem Element <=> Springen moeglich.
                y -= 2.5f * sprungkraft * Fenster.SKALIERUNG_KORREKTUR * delta;
                springtGerade = true;
            }

            sprungkraft -= 0.002f * Fenster.SKALIERUNG_KORREKTUR * delta;
        }
        else
            springtGerade = false;

        if(sollSpringen && !faelltGerade) {
            // (Element faellt nicht <=> beruehrt Boden) => Springen moeglich
            sprungkraft = 1;
        }
    }

    /**
     * Laesst das Element sich horizontal bewegen
     * @param delta Staerke der Veraenderung bezueglich der verstrichenen Systemzeit
     */
    protected void gehen(int delta) {
        int vorzeichen = 1;
        if (sollRechtsGehen)
            vorzeichen = -1;
        float xneu = x + gehkraft * geschwindigkeit * Fenster.SKALIERUNG_KORREKTUR * delta;

        if (gehkraft < 0.1f || gehkraft > 0.1f) {
            // Die Quadranten-Position des derzeitigen Elements, das gehen soll
            // y-Ausgangs-Position für Kollisionserkennung muss am linken bzw. rechten Rand des Elements liegen
            // und nicht in der Mitte (+/- Fenster.GITTERGROESSE / 2) ! Wir schauen, was nathlos links/rechts vom
            // Element liegt, indem wir +/- Fenster.GITTERGROESSE rechnen. Durch diese Ausgangs-Position
            // stimmt die Rechnung. Das Vorzeichen legt fest, ob es der linke oder rechte Element-Rand ist.
            Point quadrantElement = Fenster.posToPosMitteQuadrant((int)xneu + vorzeichen * Fenster.GITTERGROESSE / 2, (int)y);

            // Die Quadranten-Position des Elementes links/rechts neben dem springendem Element
            Point quadrantNebenElement = new Point(
                    quadrantElement.x - vorzeichen * Fenster.GITTERGROESSE,
                    quadrantElement.y
            );

            // Dieser Quadrant links/rechts neben dem Element MUSS frei sein, sonst kann das Element nicht gehen!
            if (Element.existiert(quadrantNebenElement, true)) {
                // Element ist im Weg <=> Gehen nicht möglich
                gehkraft = 0;
                gehtGerade = false;
                x = quadrantElement.x;
            } else {
                // Kein Element im Weg <=> Gehen moeglich.
                x = xneu;
                gehtGerade = true;
            }

            if (gehkraft > 0)
                gehkraft -= 0.5f;
            if (gehkraft < 0)
                gehkraft += 0.5f;
        }
        else
            gehtGerade = false;

        if (sollLinksGehen)
            gehkraft = -1f;
        if (sollRechtsGehen)
            gehkraft = 1f;
    }
}