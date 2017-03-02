package SuperQuadrant.Bloecke;

import SuperQuadrant.Element;
import SuperQuadrant.Fenster;
import SuperQuadrant.Kamera;
import SuperQuadrant.Welt;

import java.awt.*;
import java.awt.Graphics;

/**
 * Verwaltet die Spielsteine
 *
 * @author Helbig Christian www.krummbeine.de
 * @version 1.2
 */
public class Spieler extends Element
{
    public float leben;
    public int muenzen;
    private int zaehleFallDauer;
    private boolean zuletztLinks;
    private int hoehe;
    public int unsichtbarFuerGegner;
    public  boolean istAufWolke;
    public static Color[] spielerFarben = {
            new Color(80, 140, 150), // Spieler 1
            new Color(155, 55, 110)  // Spieler 2
    };
    private boolean ichBinSpieler2;

    /**
     * Konstruktor
     * Wird von Konstruktor und Methode starten() der Klasse Welt aufgerufen
     * Initialisiert und setzt die Geschwindigkeit abhaengig vom Typ
     * @param kamera Die Kamera
     * @param mitKameraVerfolgen Legt fest, ob die Kamera dem Spieler folgen soll
     * @param startPosition Die Startposition des Spielers
     * @param typ Der Typ, den der Spieler hat (spieler1: -1, spieler2: -2)
     * @param sollGespeichertWerden Gibt an, ob das Element beim Speichern gesichert werden soll.
     */
    public Spieler(Point startPosition, int typ, Kamera kamera, boolean mitKameraVerfolgen, boolean sollGespeichertWerden) {
        super(
                new Point(startPosition.x, startPosition.y),
                typ,
                0.3f,
                kamera,
                mitKameraVerfolgen,
                sollGespeichertWerden
        );
        this.leben = 1;
        this.muenzen = 0;
        this.zaehleFallDauer = 0;
        this.hoehe = Fenster.GITTERGROESSE * 2;
        this.unsichtbarFuerGegner = 0;
        this.ichBinSpieler2 = false;
        this.istAufWolke = false;
    }

    /**
     * Aktualisiert den Spieler, solange dieser lebt
     * Wird von der Methode aktualisieren der Klasse Welt aufgerufen.
     * @param delta  Staerke der Veraenderung bezueglich der verstrichenen Systemzeit
     */
    @Override
    public void aktualisieren(int delta){
        // Info: Die Ansteuerung mit der Tastatur erfolgt durch die Methode tasteBetaetigt() der Klasse Welt.
        // Diese setzt in der übergeordneten Klasse Element sollLinksGehen z.B. auf true.
        // Da der Spieler nur über Tastatur-Eingaben reagieren soll, wird hier nur eine Aktualisierung aufgerufen.
        if(leben != 0) {
            super.springen(delta);
            super.gehen(delta);
            if(!istAufWolke) {
                super.fallen(delta);
                fallTod();
            }
            else{
                faelltGerade = false;
            }
        }
        if (verfolgtVonKamera)
            kamera.aktualisieren(this, delta);

        if(unsichtbarFuerGegner > 0)
            unsichtbarFuerGegner--;
    }

    /**
     * Toetet den Spieler, wenn dieser zu lange faellt
     */
    private void fallTod(){
        if(faelltGerade && !springtGerade){
            zaehleFallDauer++;
            if(zaehleFallDauer == 500) {
                leben = 0f;
                Spieler.spielerKameraWechsel();
            }
        }
        else
            zaehleFallDauer = 0;
    }

    /**
     * Waehlt die zu zeichende Farbe bezueglich des Typs aus.
     * Ueberschreibt Methode zeichnen in Element.
     * typ -2 und -1 sind die beiden Spieler
     * Ruft eine Methode auf, um die Spieler-Leichen zu zeichnen.
     * Zeichnet einen Spieler.
     * @param g Die grafische Oberflaeche, auf der gezeichnet wird
     */
    @Override
    public void zeichnen(Graphics g) {
        int xx = (int)(x - kamera.x);
        int yy = (int)(y - kamera.y);

        zeichneKoerper(g, xx, yy);
        zeichneBlut(g);
    }

    /**
     * Zeichnet die Koerper der Spieler
     * @param g Die grafische Oberflaeche, auf der gezeichnet wird
     * @param xx Die x-Position des Spielers
     * @param yy Die y-Position des Spielers
     */
    private void zeichneKoerper(Graphics g, int xx, int yy) {

        // Wenn Monster springt, soll sich die Koeper-Form veraendern..
        if (springtGerade) {
            if (hoehe > Fenster.GITTERGROESSE)
                hoehe--;
        } else if (!faelltGerade) {
            if (hoehe < Fenster.GITTERGROESSE * 2)
                hoehe++;
        }

        if (typ == -1) {
            int sichtbarkeit = (int) (255 * Welt.spieler1.leben - 0.5f);
            if (sichtbarkeit < 0)
                sichtbarkeit = 0;
            // Farbe von spieler1 auswaehlen
            g.setColor(new Color(
                    spielerFarben[0].getRed(),
                    spielerFarben[0].getGreen(),
                    spielerFarben[0].getBlue(),
                    sichtbarkeit
            ));
        }
        if (typ == -2) {
            int sichtbarkeit = (int) (255 * Welt.spieler2.leben - 0.5f);
            if (sichtbarkeit < 0)
                sichtbarkeit = 0;
            // Farbe von Spieler 2 auswaehlen
            spieler2FarbeWaehlen(g, sichtbarkeit);
        }

        if(unsichtbarFuerGegner != 0) {
            // Unsichtbar im Gras gewesen..
            g.setColor(new Color(
                    Element.typFarben[16].getRed(),
                    Element.typFarben[16].getGreen(),
                    Element.typFarben[16].getBlue(),
                    255 - unsichtbarFuerGegner
            ));
        }

        if(!ichBinSpieler2) {
            // Koeper zeichnen
            g.fillRect(
                    xx,              // x
                    yy + Fenster.GITTERGROESSE - hoehe / 2,              // y
                    Fenster.GITTERGROESSE - 1,  // Breite
                    hoehe / 2 - 1   // Hoehe
            );
        }
        else{
            g.fillOval(
                    xx -Fenster.GITTERGROESSE / 4,              // x
                    yy + Fenster.GITTERGROESSE - hoehe / 2 - Fenster.GITTERGROESSE / 4,              // y
                    Fenster.GITTERGROESSE - 1+Fenster.GITTERGROESSE / 2,  // Breite
                    hoehe / 2 - 1  +Fenster.GITTERGROESSE / 2 // Hoehe
            );
        }

        zeichneAugen(g, xx, yy + Fenster.GITTERGROESSE - hoehe / 2);
        zeichneLeiche(g, xx, yy, hoehe);
    }

    /**
     * Zeichnet die Leichen der Spieler, wenn diese gestorben sind
     * @param g Die grafische Oberflaeche, auf der gezeichnet wird
     * @param xx Die x-Position des Spielers
     * @param yy Die y-Position des Spielers
     * @param hoehe Die Hoehe des Spielers
     */
    private void zeichneLeiche(Graphics g, int xx, int yy, int hoehe){

        if(typ == -1 && Welt.spieler1.leben < 1){
            g.setColor(spielerFarben[0]);
            g.drawRect(
                    xx,               // x
                    yy + Fenster.GITTERGROESSE - hoehe / 2,               // y
                    Fenster.GITTERGROESSE - 1,  // Breite
                    hoehe / 2- 1   // Hoehe
            );
        }
        if(typ == -2 && Welt.spieler2.leben < 1){
            spieler2FarbeWaehlen(g, 255);

            g.drawOval(
                    xx -Fenster.GITTERGROESSE / 4,              // x
                    yy + Fenster.GITTERGROESSE - hoehe / 2 - Fenster.GITTERGROESSE / 4,              // y
                    Fenster.GITTERGROESSE - 1+Fenster.GITTERGROESSE / 2,  // Breite
                    hoehe / 2 - 1  +Fenster.GITTERGROESSE / 2 // Hoehe
            );
        }
    }

    /**
     * Waehlt die Farbe des 2. Spielers bezueglich des Einzel- oder Mehrspieler-Modus.
     * Beim Einzelspielermodus sollen beide Spieler die selbe Farbe haben.
     * @param g Die grafische Oberflaeche, auf der gezeichnet wird
     * @param sichtbarkeit Die Sichtbarkeit der Farbe
     */
    private void spieler2FarbeWaehlen(Graphics g, int sichtbarkeit){
        if(Welt.einzelSpieler) {
            // Einzelspieler.. beide Spielsteine haben selbe Farbe!
            // Damit wird vermieden, dass man aus Versehen im Einzelspieler zu zwei spielt
            g.setColor(new Color(
                    spielerFarben[0].getRed(),
                    spielerFarben[0].getGreen(),
                    spielerFarben[0].getBlue(),
                    sichtbarkeit
            ));
        }
        else {
            // 2 Spieler .. zum Unterscheiden der Spieler, zwei Farben
            g.setColor(new Color(
                    spielerFarben[1].getRed(),
                    spielerFarben[1].getGreen(),
                    spielerFarben[1].getBlue(),
                    sichtbarkeit
            ));
            ichBinSpieler2 = true;
        }
    }

    /**
     * Zeichnet die Augen der Spieler
     * @param g Die grafische Oberflaeche, auf der gezeichnet wird
     * @param xx Die x-Position des Spielers
     * @param yy Die y-Position des Spielers
     */
    private void zeichneAugen(Graphics g, int xx, int yy){
        if(leben != 0) {
            // Augen
            if (sollLinksGehen)
                zuletztLinks = true;
            if (sollRechtsGehen)
                zuletztLinks = false;

            g.setColor(new Color(255, 255, 255, 255));
            int augenHoehe = Fenster.GITTERGROESSE / 4;
            if(istAufWolke)
                augenHoehe = Fenster.GITTERGROESSE / 6;

            if(ichBinSpieler2) {
                // Runde Augen
                if (zuletztLinks) {
                    g.fillOval(
                            xx,               // x
                            yy + Fenster.GITTERGROESSE / 4,               // y
                            Fenster.GITTERGROESSE / 4,  // Breite
                            augenHoehe   // Hoehe
                    );
                } else {
                    g.fillOval(
                            xx + Fenster.GITTERGROESSE - Fenster.GITTERGROESSE / 4,               // x
                            yy + Fenster.GITTERGROESSE / 4,               // y
                            Fenster.GITTERGROESSE / 4,  // Breite
                            augenHoehe   // Hoehe
                    );
                }
            }
            else{
                // Rechteckige Augen
                if (zuletztLinks) {
                    g.fillRect(
                            xx,               // x
                            yy + Fenster.GITTERGROESSE / 4,               // y
                            Fenster.GITTERGROESSE / 4,  // Breite
                            augenHoehe   // Hoehe
                    );
                } else {
                    g.fillRect(
                            xx + Fenster.GITTERGROESSE - Fenster.GITTERGROESSE / 4,               // x
                            yy + Fenster.GITTERGROESSE / 4,               // y
                            Fenster.GITTERGROESSE / 4,  // Breite
                            augenHoehe   // Hoehe
                    );
                }
            }
        }
    }

    /**
     * Zieht einem Spieler Leben ab.
     * Stirbt Spieler1, verfolgt die Kamera Spieler2.
     * Sterben beide Spieler, ist das Spiel beendet.
     * @param spielerNummer Der Spieler, dem Leben abgezogen werden soll
     */
    public static void spielerLebenAbziehen(int spielerNummer){
        if(spielerNummer == 1)
            Welt.spieler1.leben -= 0.02f;
        if(spielerNummer == 2)
            Welt.spieler2.leben -= 0.02f;

        if(Welt.spieler1.leben < 0)
            Welt.spieler1.leben = 0;
        if(Welt.spieler2.leben < 0)
            Welt.spieler2.leben = 0;

        spielerKameraWechsel();
    }

    /**
     *  Wechselt die Kamera, wenn ein Spieler stirbt bzw. setzt den Zustand der Welt auf "verloren" (3)
     */
    static void spielerKameraWechsel(){
        if(Welt.spieler1.leben > 0){
            Welt.spieler1.verfolgtVonKamera = true;
            Welt.spieler2.verfolgtVonKamera = !Welt.spieler1.verfolgtVonKamera;
        }
        else  if(Welt.spieler2.leben > 0){
            Welt.spieler1.verfolgtVonKamera = false;
            Welt.spieler2.verfolgtVonKamera = !Welt.spieler1.verfolgtVonKamera;
        }
        else{
            Welt.spieler1.verfolgtVonKamera = false;
            Welt.spieler2.verfolgtVonKamera = false;
            Welt.zustand = 3; // Zustand: Verloren
        }
    }

    /**
     * @return Gibt die Anzahl der Muenzen beider Spieler zurueck
     */
    public static int anzMuenzenGesamt(){
        return Welt.spieler1.muenzen + Welt.spieler2.muenzen;
    }

    /**
     * Zeichnet Blut, wenn der Spieler verletzt wird oder stirbt
     * @param g Die grafische Oberflaeche, auf der gezeichnet wird
     */
    private void zeichneBlut(Graphics g){
        //
    }
}