package SuperQuadrant.Bloecke;
import SuperQuadrant.Element;
import SuperQuadrant.Fenster;
import SuperQuadrant.Kamera;
import SuperQuadrant.Welt;

import java.awt.*;
import java.awt.Graphics;
import java.util.Random;

/**
 * Klasse zum Experimentieren
 *
 * @author Helbig Christian www.krummbeine.de
 * @version 1.2
 */
public class VaterMine extends Element
{
    protected int durchmesser;
    private boolean durchmesserVerkleinern;
    private Point startPosition;
    private int warten;
    Random random;
    /**
     * Konstruktor
     * @param kamera Die Kamera
     * @param mitKameraVerfolgen Legt fest, ob die Kamera dem Spieler folgen soll
     * @param startPosition Die Startposition des Spielers
     * @param typ Der Typ, den der Spieler hat (spieler1: -1, spieler2: -2)
     * @param geschwindigkeit Die Geschwindigkeit des Elements
     * @param sollGespeichertWerden Gibt an, ob das Element beim Speichern gesichert werden soll.
     */
    public VaterMine(Point startPosition, int typ, float geschwindigkeit, Kamera kamera, boolean mitKameraVerfolgen, boolean sollGespeichertWerden) {
        super(
                new Point(startPosition.x, startPosition.y),
                typ,
                geschwindigkeit,
                kamera,
                mitKameraVerfolgen,
                sollGespeichertWerden
        );
        this.startPosition = startPosition;
        this.random = new Random();
        sollRechtsGehen = true;
        this.durchmesser = Fenster.GITTERGROESSE * 4;
        this.durchmesserVerkleinern = true;
        this.warten = 0;
    }

    /**
     * Da sich das VaterMine bewegt, muss zum Speichern die StartPosition extra gesichert werden !!
     * Ansonsten wird das VaterMine nach dem Laden an einer anderen Position erstellt
     * @return Gibt die StartPosition zurueck
     */
    @Override
    public Point getStartPosition(){
        return startPosition;
    }

    /**
     * Aktualisiert und animiert das VaterMine
     * Wird von der Methode aktualisieren der Klasse Welt aufgerufen.
     * Laesst das Monster gehen und wenn es auf ein Hindernis trifft die Gehrichtung aendern.
     * Trifft das Monster auf einen Spieler, zieht es ihm Leben ab.
     * Ueberschreibt die aktualisieren-Methode aus Element
     * @param delta  Staerke der Veraenderung bezueglich der verstrichenen Systemzeit
     */
    @Override
    public void aktualisieren(int delta){
        // Animation des TestMonsters
        if(durchmesserVerkleinern) {
            durchmesser--;
            if(durchmesser < Fenster.GITTERGROESSE * 3)
                durchmesserVerkleinern = !durchmesserVerkleinern;
        } else{
            durchmesser++;
            if(durchmesser > Fenster.GITTERGROESSE * 5){
                durchmesserVerkleinern = !durchmesserVerkleinern;
            }
        }

        super.fallen(delta);
        super.gehen(delta);

        if(!gehtGerade) {
            // Das Monster stieß auf ein Hindernis
            // Die Gehrichtung soll nun geändert werden
            sollLinksGehen = sollRechtsGehen;
            sollRechtsGehen = !sollLinksGehen;
        }

        beruehrung();
    }

    /**
     * Erstellt KindMinen bei Beruehrung
     */
    protected void beruehrung(){
        Point elementQuadrant = Fenster.posToPosMitteQuadrant((int)x, (int)y);
        Point spieler1Quadrant = Fenster.posToPosMitteQuadrant((int)Welt.spieler1.x, (int)Welt.spieler1.y);
        Point spieler2Quadrant = Fenster.posToPosMitteQuadrant((int)Welt.spieler2.x, (int)Welt.spieler2.y);

        if(elementQuadrant.equals(spieler1Quadrant) && Welt.spieler1.leben > 0){
            warten = 205;
        }

        if(elementQuadrant.equals(spieler2Quadrant) && Welt.spieler2.leben > 0) {
            warten = 205;
        }

        if(warten > 0){
            warten--;
            if(warten % 50 == 0){
                Element.hinzufuegen(
                        new KindMine(
                                new Point((int)(x + random.nextInt(Fenster.GITTERGROESSE) - Fenster.GITTERGROESSE / 2), (int)(elementQuadrant.y - Fenster.GITTERGROESSE)), typ, 0, kamera, false, false
                        )
                );
            }
            this.geschwindigkeit = 0.8f;
        }
        else
            this.geschwindigkeit = 0.3f;
    }



    /**
     * Ueberschreibt Methode zeichnen in Element.
     * Zeichnet ein VaterMine.
     * @param g Die grafische Oberflaeche, auf der gezeichnet wird
     */
    @Override
    public void zeichnen(Graphics g) {
        g.setColor(new Color(0, 0, 0, 55));

        int xx = (int) (x - kamera.x);
        int yy = (int) (y - kamera.y);

        g.fillRect(
                xx + Fenster.GITTERGROESSE / 2 - durchmesser / 8,           // x
                yy + Fenster.GITTERGROESSE  - durchmesser / 16,           // y
                durchmesser / 4,                                                      // Breite
                durchmesser / 16                                                       // Hoehe
        );
    }
}