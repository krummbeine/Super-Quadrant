package SuperQuadrant.Bloecke;
import SuperQuadrant.Element;
import SuperQuadrant.Fenster;
import SuperQuadrant.Kamera;
import SuperQuadrant.Welt;

import java.awt.*;

/**
 * Klasse zum Experimentieren
 *
 * @author Helbig Christian www.krummbeine.de
 * @version 1.2
 */
public class WiederbelebungsBlock extends Element
{
    private int durchmesser;
    private boolean durchmesserVerkleinern;
    private int aufladen;
    private static int warteZeit = 255;

    /**
     * Konstruktor
     * @param kamera Die Kamera
     * @param mitKameraVerfolgen Legt fest, ob die Kamera dem Spieler folgen soll
     * @param startPosition Die Startposition des Spielers
     * @param typ Der Typ, den der Spieler hat (spieler1: -1, spieler2: -2)
     * @param geschwindigkeit Die Geschwindigkeit des Elements
     * @param sollGespeichertWerden Gibt an, ob das Element beim Speichern gesichert werden soll.
     */
    public WiederbelebungsBlock(Point startPosition, int typ, float geschwindigkeit, Kamera kamera, boolean mitKameraVerfolgen, boolean sollGespeichertWerden) {
        super(
                new Point(startPosition.x, startPosition.y),
                typ,
                geschwindigkeit,
                kamera,
                mitKameraVerfolgen,
                sollGespeichertWerden
        );
        this.durchmesser = Fenster.GITTERGROESSE * 2;
        this.durchmesserVerkleinern = true;
        this.aufladen = warteZeit;
    }

    /**
     * Aktualisiert und animiert den Startblock
     * Bei Beruehrung durch einen Spieler, werden die Leben wieder auf 1 (100%) gesetzt und sie fallen
     * erneut aus diesem Block. Der WiederbelebungsBlock kann nur einmal ausgeloest werden.
     * @param delta  Staerke der Veraenderung bezueglich der verstrichenen Systemzeit
     */
    @Override
    public void aktualisieren(int delta){
        // Animation des StartBlocks
        if(durchmesserVerkleinern) {
            durchmesser--;
            if(durchmesser < Fenster.GITTERGROESSE * 2)
                durchmesserVerkleinern = !durchmesserVerkleinern;
        } else{
            durchmesser++;
            if(durchmesser > Fenster.GITTERGROESSE * 4){
                durchmesserVerkleinern = !durchmesserVerkleinern;
            }
        }

        if(Welt.spieler1.leben == 0 || Welt.spieler2.leben == 0) {
            if(aufladen == 0) {
                // Leben bei Beruehrung wieder auf 1 (100%) setzen
                Point elementQuadrant = Fenster.posToPosMitteQuadrant((int)x, (int)y);
                Point spieler1Quadrant = Fenster.posToPosMitteQuadrant((int)Welt.spieler1.x, (int)Welt.spieler1.y);
                Point spieler2Quadrant = Fenster.posToPosMitteQuadrant((int)Welt.spieler2.x, (int)Welt.spieler2.y);

                if (elementQuadrant.equals(spieler1Quadrant) || elementQuadrant.equals(spieler2Quadrant)) {

                    // Zwei Muenzen Belohnung für den Spieler, der wiederbelebt
                    Element.hinzufuegen(
                            new MuenzenBlock(
                                    new Point((int)x, elementQuadrant.y - Fenster.GITTERGROESSE * 1), 5, 0f, kamera, false, false
                            )
                    );
                    Element.hinzufuegen(
                            new MuenzenBlock(
                                    new Point((int)x, elementQuadrant.y - Fenster.GITTERGROESSE * 2), 5, 0f, kamera, false, false
                            )
                    );

                    aufladen = warteZeit;
                    // Die Positionen der Spieler werden NUR zum WiederbelebungsBlock gesetzt, wenn diese tot waren.
                    // Spieler 1 belebt Spieler 2 wieder ..
                    if(Welt.spieler2.leben == 0) {
                        Welt.spieler2.x = x;
                        Welt.spieler2.y = y;
                        Welt.spieler2.sprungkraft = 0f;
                        Welt.spieler2.gehkraft = 0f;
                    }
                    // Spieler 2 belebt Spieler 1 wieder
                    if(Welt.spieler1.leben == 0) {
                        Welt.spieler1.x = x;
                        Welt.spieler1.y = y;
                        Welt.spieler1.sprungkraft = 0f;
                        Welt.spieler1.gehkraft = 0f;
                    }
                    // Sicherstellen, dass vergangene Tastatur-Eingaben nicht mehr aktiv sind (zuruecksetzen)
                    Welt.spieler1.sollLinksGehen = Welt.spieler1.sollRechtsGehen = Welt.spieler1.sollSpringen = false;
                    Welt.spieler2.sollLinksGehen = Welt.spieler2.sollRechtsGehen = Welt.spieler2.sollSpringen = false;
                    // Beide Spieler erhalten volles Leben
                    Welt.spieler2.leben = 1f;
                    Welt.spieler1.leben = 1f;
                }
                Spieler.spielerKameraWechsel();
            }

            if(aufladen > 0){
                aufladen--;
            }
        }
        else
            aufladen = warteZeit;
    }

    /**
     * Ueberschreibt Methode zeichnen in Element.
     * Zeichnet den WiederbelebungsBlock
     * @param g Die grafische Oberflaeche, auf der gezeichnet wird
     */
    @Override
    public void zeichnen(Graphics g) {
        g.setColor(new Color(0, 60, 205, 55));

        // WiederbelebungsBlock zeichnen
        g.fillRect(
                (int)(x - kamera.x),                   // x
                (int)(y - kamera.y),                   // y
                Fenster.GITTERGROESSE - 1,      // Breite
                Fenster.GITTERGROESSE - 1       // Hoehe
        );


        // Animation zeichnen
        int dreisatzAufladen = (warteZeit - aufladen) * Fenster.GITTERGROESSE / warteZeit;
        int dreisatzSichtbarkeit = durchmesser * 255 / (Fenster.GITTERGROESSE * 4 + 1);

        if (Welt.spieler1.leben == 0 && Welt.spieler2.leben != 0)
            g.setColor(new Color(
                    Spieler.spielerFarben[0].getRed(),
                    Spieler.spielerFarben[0].getGreen(),
                    Spieler.spielerFarben[0].getBlue(),
                    dreisatzSichtbarkeit
            ));
        else if (Welt.spieler2.leben == 0)
            spieler2FarbeWaehlen(g, dreisatzSichtbarkeit);

        if (durchmesser > 0) {
            g.fillRect(
                    (int)(x - kamera.x + Fenster.GITTERGROESSE / 2 - dreisatzAufladen / 2 - 1),           // x
                    (int)(y - kamera.y + Fenster.GITTERGROESSE / 2 - durchmesser / 8 - 1),           // y
                    dreisatzAufladen,                                                  // Breite
                    durchmesser / 4                                                    // Hoehe
            );
        }
    }

    /**
     * Waehlt die Farbe des 2. Spielers bezueglich des Einzel- oder Mehrspieler-Modus.
     * Beim Einzelspielermodus sollen beide Spieler die selbe Farbe haben.
     * @param g Die grafische Oberflaeche, auf der gezeichnet wird
     * @param sichtbarkeit Die Sichtbarkeit der gewaehlten Farbe
     */
    private void spieler2FarbeWaehlen(Graphics g, int sichtbarkeit) {
        if (Welt.einzelSpieler) {
            // Einzelspieler.. beide Spielsteine haben selbe Farbe!
            // Damit wird vermieden, dass man aus Versehen im Einzelspieler zu zwei spielt
            g.setColor(new Color(
                    Spieler.spielerFarben[0].getRed(),
                    Spieler.spielerFarben[0].getGreen(),
                    Spieler.spielerFarben[0].getBlue(),
                    sichtbarkeit
            ));
        } else {
            // 2 Spieler .. zum Unterscheiden der Spieler, zwei Farben

            g.setColor(new Color(
                    Spieler.spielerFarben[1].getRed(),
                    Spieler.spielerFarben[1].getGreen(),
                    Spieler.spielerFarben[1].getBlue(),
                    sichtbarkeit
            ));
        }
    }
}