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
public class SpeicherpunktBlock extends Element
{
    private int durchmesser;
    private boolean ausgeloest;
    private boolean umkehren;
    Random rd;
    /**
     * Konstruktor
     * @param kamera Die Kamera
     * @param mitKameraVerfolgen Legt fest, ob die Kamera dem Spieler folgen soll
     * @param startPosition Die Startposition des Spielers
     * @param typ Der Typ, den der Spieler hat (spieler1: -1, spieler2: -2)
     * @param geschwindigkeit Die Geschwindigkeit des Elements
     * @param sollGespeichertWerden Gibt an, ob das Element beim Speichern gesichert werden soll.
     */
    public SpeicherpunktBlock(Point startPosition, int typ, float geschwindigkeit, Kamera kamera, boolean mitKameraVerfolgen, boolean sollGespeichertWerden) {
        super(
                new Point(startPosition.x, startPosition.y),
                typ,
                geschwindigkeit,
                kamera,
                mitKameraVerfolgen,
                sollGespeichertWerden
        );
        rd = new Random();
        this.durchmesser = Fenster.GITTERGROESSE;
        this.ausgeloest = false;
        this.umkehren = false;
    }

    /**
     * Ueberschreibt Methode in Element .. wird benoetigt, um auf ausgeloest im SpeicherpunktBlock zugreifen zu koennen.
     * @return Gibt zurueck, ob der Block ausgeloest wurde.
     */
    @Override
    protected boolean getAusgeloest(){
        return  ausgeloest;
    }

    /**
     * Aktualisiert und animiert den SpeicherpunktBlock
     * Bei Beruehrung wird ueber dem Element eine MuenzenBlock oder Monster erstellt
     * @param delta  Staerke der Veraenderung bezueglich der verstrichenen Systemzeit
     */
    @Override
    public void aktualisieren(int delta){
        // Animation des SpeicherpunktBlockss
        if(ausgeloest) {
                if (umkehren) {
                    if (durchmesser > -Fenster.GITTERGROESSE / 5)
                        durchmesser--;
                    else
                        umkehren = !umkehren;
                } else {
                    if (durchmesser < Fenster.GITTERGROESSE * 2)
                        durchmesser++;
                    else
                        umkehren = !umkehren;
                }
        }

        ausloesen();
    }

    /**
     * Loest den Block aus, wenn Spieler 1 oder 2 ihn beruehren oder sein Umfeld 2 Bloecke darueber / darunter.
     */
    private void ausloesen() {
        if (!ausgeloest) {
            Point spieler1Quadrant = Fenster.posToPosMitteQuadrant((int) Welt.spieler1.x, (int) Welt.spieler1.y);
            Point spieler2Quadrant = Fenster.posToPosMitteQuadrant((int) Welt.spieler2.x, (int) Welt.spieler2.y);

            for (int i = -2; i <= 2; i++) {
                // Wird ausgeloest, wenn Spieler in den SpeicherpunktBlock geht oder zwei Blöcke oberhalb / unterhalb
                Point ausloeseQuadrant = Fenster.posToPosMitteQuadrant((int) x, (int) y + Fenster.GITTERGROESSE * i);

                if (ausloeseQuadrant.equals(spieler1Quadrant) || ausloeseQuadrant.equals(spieler2Quadrant)) {
                    // Spieler 1 oder 2 im AusloeseBereich
                    ausgeloest = true;
                    Welt.wiedereinstiegsPunkt = new Point(
                            (int) x,
                            (int) y
                    );
                    durchmesser = Fenster.GITTERGROESSE;
                }
            }
        }
    }

    /**
     * Ueberschreibt Methode zeichnen in Element.
     * Zeichnet den SpeicherpunktBlock.
     * @param g Die grafische Oberflaeche, auf der gezeichnet wird.
     */
    @Override
    public void zeichnen(Graphics g) {
        int xx = (int)(x - kamera.x);
        int yy = (int)(y - kamera.y);

        if(!ausgeloest)
            g.setColor(new Color(110, 220, 170, 100));
        else
            g.setColor(new Color(100, 220, 150, 200));

        g.fillRect(
                xx,                   // x
                yy,                   // y
                Fenster.GITTERGROESSE - 1,      // Breite
                Fenster.GITTERGROESSE - 1       // Hoehe
        );

        g.fillRect(
                xx + durchmesser / 2 - Fenster.GITTERGROESSE / 2,                   // x
                yy,                   // y
                Fenster.GITTERGROESSE,      // Breite
                Fenster.GITTERGROESSE - 1     // Hoehe
        );
    }
}