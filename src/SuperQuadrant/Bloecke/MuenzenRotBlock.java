package SuperQuadrant.Bloecke;
import SuperQuadrant.Element;
import SuperQuadrant.Fenster;
import SuperQuadrant.Kamera;
import SuperQuadrant.Welt;

import java.awt.*;
import java.util.Random;


/**
 * Klasse zum Experimentieren
 *
 * @author Helbig Christian www.krummbeine.de
 * @version 1.2
 */
public class MuenzenRotBlock extends Element
{
    private int hoehe;
    private boolean breiteVerkleinern;
    private Random random;
    private Point startPosition;

    /**
     * Konstruktor
     * @param kamera Die Kamera
     * @param mitKameraVerfolgen Legt fest, ob die Kamera dem Spieler folgen soll
     * @param startPosition Die Startposition des Spielers
     * @param typ Der Typ, den der Spieler hat (spieler1: -1, spieler2: -2)
     * @param geschwindigkeit Die Geschwindigkeit des Elements
     * @param sollGespeichertWerden Gibt an, ob das Element beim Speichern gesichert werden soll.
     */
    public MuenzenRotBlock(Point startPosition, int typ, float geschwindigkeit, Kamera kamera, boolean mitKameraVerfolgen, boolean sollGespeichertWerden) {
        super(
                new Point(startPosition.x, startPosition.y),
                typ,
                geschwindigkeit,
                kamera,
                mitKameraVerfolgen,
                sollGespeichertWerden
        );
        this.startPosition = startPosition;
        random = new Random();
        if(random.nextInt(2) == 0)
            sollRechtsGehen = true;
        else
            sollLinksGehen = true;
        // Zufallszahl zwischen [Fenster.GITTERGROESSE / 2, Fenster.GITTERGROESSE * 2 ]
        this.hoehe = random.nextInt(Fenster.GITTERGROESSE) + Fenster.GITTERGROESSE;
        this.breiteVerkleinern = true;
    }

    /**
     * Aktualisiert und animiert die MuenzenBlock
     * Erhoeht die Anzahl der Muenzen eines Spielers, wenn er die MuenzenBlock beruehrt und verschwindet dann,
     * damit sie nicht nochmal eingesammelt werden kann. Animiert die MuenzenBlock.
     * @param delta  Staerke der Veraenderung bezueglich der verstrichenen Systemzeit
     */
    @Override
    public void aktualisieren(int delta){
        if(hoehe != 0) {
            // MuenzenBlock noch nicht eingesammelt

            fallen(delta);
            gehen(delta);
            gehrichtungAendern();

            // Animation der MuenzenBlock
            if(breiteVerkleinern) {
                hoehe--;
                if(hoehe < Fenster.GITTERGROESSE / 5)
                    breiteVerkleinern = !breiteVerkleinern;
            } else{
                hoehe++;
                if(hoehe == Fenster.GITTERGROESSE * 2){
                    breiteVerkleinern = !breiteVerkleinern;
                }
            }

            // Einsammeln der MuenzenBlock bei Beruehrung
            Point elementQuadrant = Fenster.posToPosMitteQuadrant((int)x, (int)y);
            Point spieler1Quadrant = Fenster.posToPosMitteQuadrant((int)Welt.spieler1.x, (int)Welt.spieler1.y);
            Point spieler2Quadrant = Fenster.posToPosMitteQuadrant((int)Welt.spieler2.x, (int)Welt.spieler2.y);

            if (elementQuadrant.equals(spieler1Quadrant)) {
                Welt.spieler1.muenzen += 2; // doppelter Wert
                Welt.punktestandAktualisieren();
                hoehe = 0;
            }
            else if (elementQuadrant.equals(spieler2Quadrant)) {
                Welt.spieler2.muenzen += 2; // doppelter Wert
                Welt.punktestandAktualisieren();
                hoehe = 0;
            }
        }
    }

    /**
     * Aendert die Gehrichtung der roten Muenze, wenn sie auf ein Hindernis trifft
     */
    private void gehrichtungAendern() {
        if (!gehtGerade) {
            // Das Monster stieß auf ein Hindernis
            // Die Gehrichtung soll nun geändert werden
            sollLinksGehen = sollRechtsGehen;
            sollRechtsGehen = !sollLinksGehen;
        }
    }

    /**
     * Ueberschreibt Methode zeichnen in Element.
     * Zeichnet eine MuenzenBlock
     * @param g Die grafische Oberflaeche, auf der gezeichnet wird
     */
    @Override
    public void zeichnen(Graphics g) {
        g.setColor(new Color(250, 105,70, 255));

        // MuenzenBlock zeichnen
        g.fillOval(
                (int)(x - kamera.x),              // x
                (int)(y - kamera.y + Fenster.GITTERGROESSE / 2 - hoehe / 4),                // y
                Fenster.GITTERGROESSE - 1,                                                  // Breite
                hoehe / 2                         // Hoehe
        );

        if(hoehe > 0) {
            g.setColor(new Color(250, 250, 250, 255));
            g.drawOval(
                    (int)(x - kamera.x),              // x
                    (int)(y - kamera.y + Fenster.GITTERGROESSE / 2 - hoehe / 4),                // y
                    Fenster.GITTERGROESSE - 1,                                                  // Breite
                    hoehe / 2                         // Hoehe
            );
        }
    }
}