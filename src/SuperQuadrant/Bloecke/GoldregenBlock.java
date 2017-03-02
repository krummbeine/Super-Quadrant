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
public class GoldregenBlock extends Element
{
    private int durchmesser;
    public boolean ausgeloest;
    private boolean umkehren;
    private int muenzenGespeichert;
    /**
     * Konstruktor
     * @param kamera Die Kamera
     * @param mitKameraVerfolgen Legt fest, ob die Kamera dem Spieler folgen soll
     * @param startPosition Die Startposition des Spielers
     * @param typ Der Typ, den der Spieler hat (spieler1: -1, spieler2: -2)
     * @param geschwindigkeit Die Geschwindigkeit des Elements
     * @param sollGespeichertWerden Gibt an, ob das Element beim Speichern gesichert werden soll.
     */
    public GoldregenBlock(Point startPosition, int typ, float geschwindigkeit, Kamera kamera, boolean mitKameraVerfolgen, boolean sollGespeichertWerden) {
        super(
                new Point(startPosition.x, startPosition.y),
                typ,
                geschwindigkeit,
                kamera,
                mitKameraVerfolgen,
                sollGespeichertWerden
        );
        this.durchmesser = Fenster.GITTERGROESSE;
        this.ausgeloest = false;
        this.umkehren = false;
        this.muenzenGespeichert = 5;
    }

    /**
     * Ueberschreibt Methode in Element .. wird benoetigt, um auf ausgeloest im GoldregenBlock zugreifen zu koennen.
     * @return Gibt zurueck, ob der Block ausgeloest wurde.
     */
    @Override
    protected boolean getAusgeloest(){
        return  ausgeloest;
    }

    /**
     * Aktualisiert und animiert den GoldregenBlock
     * Bei Beruehrung wird ueber dem Element eine MuenzenBlock oder Monster erstellt
     * @param delta  Staerke der Veraenderung bezueglich der verstrichenen Systemzeit
     */
    @Override
    public void aktualisieren(int delta){
        // Animation des WiedereinstiegsBlocks
        if(ausgeloest) {
            if(muenzenGespeichert > 0) {
                if (durchmesser == Fenster.GITTERGROESSE) {
                    muenzeAusschuetten();
                }
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
        }

        ausloesen();
    }

    /**
     * Loest den Block aus, wenn Spieler 1 oder 2 ihn beruehren
     */
    private void ausloesen(){
        if(!ausgeloest) {
            Point elementQuadrant = Fenster.posToPosMitteQuadrant((int) x, (int) y);
            // Wird ausgeloest, wenn Spieler unter den Zufalls-Block springt
            elementQuadrant.y += Fenster.GITTERGROESSE;
            Point spieler1Quadrant = Fenster.posToPosMitteQuadrant((int) Welt.spieler1.x, (int) Welt.spieler1.y);
            Point spieler2Quadrant = Fenster.posToPosMitteQuadrant((int) Welt.spieler2.x, (int) Welt.spieler2.y);

            if (elementQuadrant.equals(spieler1Quadrant) || elementQuadrant.equals(spieler2Quadrant)) {
                if (!ausgeloest) {
                    ausgeloest = true;

                    Random rd = new Random();
                    if (rd.nextInt(3) == 0) { // Zufallszahl [0;3]
                        // Kopfmonster
                        Element.hinzufuegen(
                                new KopfmonsterBlock(
                                        new Point((int)x, elementQuadrant.y - Fenster.GITTERGROESSE * 2), 3, 0.5f, kamera, false, false
                                )
                        );
                    }

                    Welt.wiedereinstiegsPunkt = new Point(
                            (int)x,
                            (int)y + Fenster.GITTERGROESSE
                    );
                }
            }
        }
    }

    /**
     * Schuettet Muenzen oberhalb des Blocks aus
     */
    private void muenzeAusschuetten(){
        muenzenGespeichert--;
        Point elementQuadrant = Fenster.posToPosMitteQuadrant((int) x, (int) y);
        Element.hinzufuegen(
                new MuenzenRotBlock(
                        new Point((int)x, elementQuadrant.y - Fenster.GITTERGROESSE * 2), 12, 0.5f, kamera, false, false
                )
        );
    }

    /**
     * Ueberschreibt Methode zeichnen in Element.
     * Zeichnet den GoldregenBlock.
     * @param g Die grafische Oberflaeche, auf der gezeichnet wird.
     */
    @Override
    public void zeichnen(Graphics g) {
        int xx = (int)(x - kamera.x);
        int yy = (int)(y - kamera.y);

        if(!ausgeloest)
            g.setColor(new Color(250, 105,70, 100));
        else
            g.setColor(new Color(250, 105,70, 200));

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