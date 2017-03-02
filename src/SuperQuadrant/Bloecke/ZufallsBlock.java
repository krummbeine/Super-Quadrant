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
public class ZufallsBlock extends Element
{
    private int durchmesser;
    public boolean ausgeloest;
    /**
     * Konstruktor
     * @param kamera Die Kamera
     * @param mitKameraVerfolgen Legt fest, ob die Kamera dem Spieler folgen soll
     * @param startPosition Die Startposition des Spielers
     * @param typ Der Typ, den der Spieler hat (spieler1: -1, spieler2: -2)
     * @param geschwindigkeit Die Geschwindigkeit des Elements
     * @param sollGespeichertWerden Gibt an, ob das Element beim Speichern gesichert werden soll.
     */
    public ZufallsBlock(Point startPosition, int typ, float geschwindigkeit, Kamera kamera, boolean mitKameraVerfolgen, boolean sollGespeichertWerden) {
        super(
                new Point(startPosition.x, startPosition.y),
                typ,
                geschwindigkeit,
                kamera,
                mitKameraVerfolgen,
                sollGespeichertWerden
        );
        this.durchmesser = Fenster.GITTERGROESSE * 2;
        this.ausgeloest = false;
    }

    /**
     * Ueberschreibt Methode in Element .. wird benoetigt, um auf ausgeloest im ZufallsBlock zugreifen zu koennen.
     * @return Gibt zurueck, ob der Block ausgeloest wurde.
     */
    @Override
    protected boolean getAusgeloest(){
        return  ausgeloest;
    }

    /**
     * Aktualisiert und animiert den ZufallsBlock
     * Bei Beruehrung wird ueber dem Element eine MuenzenBlock oder Monster erstellt
     * @param delta  Staerke der Veraenderung bezueglich der verstrichenen Systemzeit
     */
    @Override
    public void aktualisieren(int delta){
        // Animation des ZufallsBlocks
        if(ausgeloest)
            if(durchmesser > 1)
                durchmesser--;

        Point elementQuadrant = Fenster.posToPosMitteQuadrant((int)x, (int)y);
        // Wird ausgeloest, wenn Spieler unter den Zufalls-Block springt
        elementQuadrant.y += Fenster.GITTERGROESSE;
        // Quadranten der Spieler ermitteln
        Point spieler1Quadrant = Fenster.posToPosMitteQuadrant((int)Welt.spieler1.x, (int)Welt.spieler1.y);
        Point spieler2Quadrant = Fenster.posToPosMitteQuadrant((int)Welt.spieler2.x, (int)Welt.spieler2.y);
        // Sind Spieler im auszuloesenden Bereich direkt unter dem Zufalls-Block --> ausloesen!
        if(elementQuadrant.equals(spieler1Quadrant) || elementQuadrant.equals(spieler2Quadrant)) {
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
                // MuenzenBlock
                Element.hinzufuegen(
                        new MuenzenBlock(
                                new Point((int)x, elementQuadrant.y - Fenster.GITTERGROESSE * 2), 5, 0f, kamera, false, false
                        )
                );
            }
        }
    }

    /**
     * Ueberschreibt Methode zeichnen in Element.
     * Zeichnet den ZufallsBlock.
     * @param g Die grafische Oberflaeche, auf der gezeichnet wird
     */
    @Override
    public void zeichnen(Graphics g) {
        int xx = (int)(x - kamera.x);
        int yy = (int)(y - kamera.y);

        if(!ausgeloest)
            g.setColor(new Color(255, 150, 110, 100));
        else
            g.setColor(new Color(255, 150, 155, 100));

        g.fillRect(
            xx,                   // x
            yy,                   // y
                Fenster.GITTERGROESSE - 1,      // Breite
                Fenster.GITTERGROESSE - 1       // Hoehe
        );

        g.fillRect(
                xx + Fenster.GITTERGROESSE / 2 - durchmesser / 4,                   // x
                yy ,                   // y
                durchmesser / 2 - 1,      // Breite
                durchmesser / 2 - 1      // Hoehe
        );
    }
}