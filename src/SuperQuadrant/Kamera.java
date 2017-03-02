package SuperQuadrant;

import java.awt.*;

/**
 * Im Fenster ist ein Ausschnitt der Welt zu sehen.
 * Die x- und y-Position dieses Ausschnitts werden durch die Kamera bestimmt.
 * Der Ausschnitt selbst ist so breit und hoch wie das Fenster.
 *
 * Bewegt sich der verfolgte Spieler an den Rand des Ausschnitts, wird dieser entsprechend verschoben,
 * damit dieser im Fenster sichtbar bleibt.
 *
 * @author Helbig Christian www.krummbeine.de
 * @version 1.1
 */
public class Kamera {
    public float x;
    public float y;
    private float horizontaleGeschwindigkeit;
    private float vertikaleGeschwindigkeit;
    static public Point pp;

    /**
     * Konstruktor
     *  Wird von Konstruktor der Klasse Welt aufgerufen
     * @param startPosition Die Startposition der Kamera
     */
    public Kamera(Point startPosition) {
        this.x = startPosition.x;
        this.y = startPosition.y;
        this.horizontaleGeschwindigkeit = this.vertikaleGeschwindigkeit = 0f;
        this.pp = new Point(0,0);
    }

    /**
     * Aktualisiert die Kamera-Position, wenn der Spieler einem Rand des sichtbaren Ausschnittes zu nahe kommt
     * @param verfolgtesElement Der Spieler, dem die Kamera folgt
     * @param delta Staerke der Veraenderung bezueglich der verstrichenen Systemzeit
     */
    public void aktualisieren(Element verfolgtesElement, float delta) {
        // Die Position des Spielers im Ausschnitt (Umrechnung von Welt-Position zur Fenster-Position)
        Point zuVerfolgendePositionImAusschnitt = new Point(0, 0);

        if(!Welt.einzelSpieler && Welt.spieler1.leben > 0 && Welt.spieler2.leben > 0) {
            // Im Mehrspielermodus soll der Punkt zwischen den Spielern verfolgt werden
            Point zuVerfolgen = new Point(0, 0);

            if(Welt.spieler1.x < Welt.spieler2.x)
                zuVerfolgen.x = (int) (Welt.spieler1.x + (Welt.spieler2.x - Welt.spieler1.x) / 2);
            else
                zuVerfolgen.x = (int) (Welt.spieler2.x + (Welt.spieler1.x - Welt.spieler2.x) / 2);

            if(Welt.spieler1.y < Welt.spieler2.y)
                zuVerfolgen.y = (int) (Welt.spieler1.y + (Welt.spieler2.y - Welt.spieler1.y) / 2);
            else
                zuVerfolgen.y = (int) (Welt.spieler2.y + (Welt.spieler1.y - Welt.spieler2.y) / 2);

            zuVerfolgendePositionImAusschnitt = new Point(
                    (int) (zuVerfolgen.x - this.x + Fenster.GITTERGROESSE / 2),
                    (int) (zuVerfolgen.y - this.y + Fenster.GITTERGROESSE / 2)
            );
        }
        else {
            zuVerfolgendePositionImAusschnitt = new Point(
                    (int) (verfolgtesElement.x - this.x),
                    (int) (verfolgtesElement.y - this.y)
            );
        }
        pp = new Point(zuVerfolgendePositionImAusschnitt.x, zuVerfolgendePositionImAusschnitt.y);


        // Die Drittel des Fenster-Ausschnitts, in denen sich der Spieler befindet
        int horizontalesDrittel = posAufBildschirm(zuVerfolgendePositionImAusschnitt.x, Fenster.BREITE);
        int vertikalesDrittel = posAufBildschirm(zuVerfolgendePositionImAusschnitt.y, Fenster.HOEHE);

        // Fuer den Fall, dass der Spieler schon außerhalb des sichtbaren Ausschnitts liegt
        // Was eintritt, wenn Spieler1 stirbt und die Kamera zu Spieler2 wechselt, dieser aber außerhalb lag
        boolean ausserhalb = false;
        if (zuVerfolgendePositionImAusschnitt.x < 0) {
            x -= delta * verfolgtesElement.geschwindigkeit * 2;
            ausserhalb = true;
        }
        if (zuVerfolgendePositionImAusschnitt.x > Fenster.BREITE - Fenster.GITTERGROESSE) {
            x += delta * verfolgtesElement.geschwindigkeit * 2;
            ausserhalb = true;
        }
        if (zuVerfolgendePositionImAusschnitt.y < 0) {
            y -= delta * verfolgtesElement.geschwindigkeit * 2;
            ausserhalb = true;
        }
        if (zuVerfolgendePositionImAusschnitt.y > Fenster.HOEHE - Fenster.GITTERGROESSE) {
            y += delta * verfolgtesElement.geschwindigkeit * 2;
            ausserhalb = true;
        }

        if (!ausserhalb) {
            // Befindet sich der Spieler am Rand des Ausschnitts, muss die Kamera bewegt werden,
            // damit der Spieler im Bild bleibt. Die Geschwindigkeit soll zunehmen bis sie genauso schnell
            // wie der Spieler ist. Somit wird ein Aus-Dem-Sichtbereich-Rennen des Spielers verhindert
            if (horizontalesDrittel != 1) {
                // Spieler befindet sich an einem horizontalen Randbereich des Ausschnitts
                if (horizontaleGeschwindigkeit < verfolgtesElement.geschwindigkeit)
                    // horizontale Geschwindigkeit der Kamera-Bewegung erhoehen
                    horizontaleGeschwindigkeit += 0.01f;
            } else {
                // Spieler befindet sich im horizontal mittleren Drittel des Ausschnitts
                if (horizontaleGeschwindigkeit > 0)
                    // horizontale Geschwindigkeit der Kamera-Bewegung soll auf 0 fallen
                    horizontaleGeschwindigkeit -= 0.05f;
                else
                    // Sicherstellen, dass die Geschwindigkeit nicht kleiner 0 wird
                    horizontaleGeschwindigkeit = 0f;
            }

            if (vertikalesDrittel != 1) {
                // Spieler befindet sich an einem vertikalen Randbereich des Ausschnitts
                if (vertikaleGeschwindigkeit < verfolgtesElement.geschwindigkeit)
                    // vertikale Geschwindigkeit der Kamera-Bewegung erhoehen
                    vertikaleGeschwindigkeit += 0.01f;
            } else {
                // Spieler befindet sich im vertikal mittleren Drittel des Ausschnitts
                if (vertikaleGeschwindigkeit > 0)
                    // vertikale Geschwindigkeit der Kamera-Bewegung soll auf 0 fallen
                    vertikaleGeschwindigkeit -= 0.05f;
                else
                    // Sicherstellen, dass die Geschwindigkeit nicht kleiner 0 wird
                    vertikaleGeschwindigkeit = 0f;
            }


            // Bewegen der Kamera
            if (horizontalesDrittel == 2)
                // Spieler im rechten Drittel des Ausschnitts
                // Kamera nach rechts bewegen
                x += delta * horizontaleGeschwindigkeit;
            if (vertikalesDrittel == 2)
                // Spieler im unterem Drittel des Ausschnitts
                // Kamera nach unten bewegen
                y += delta * vertikaleGeschwindigkeit;
            if (vertikalesDrittel == 0)
                // Spieler im oberen Drittel des Ausschnitts
                // Kamera nach oben bewegen
                y -= delta * vertikaleGeschwindigkeit;
            if (horizontalesDrittel == 0)
                // Spieler im linken Drittel des Ausschnitts
                // Kamera nach links bewegen
                x -= delta * horizontaleGeschwindigkeit;
        }
    }

    /**
     * Ermittelt, in welchem Drittel des sichtbaren Ausschnitts sich der mit der Kamera verfolgte Spieler befindet.
     * @param position Die eindimensionale Position des Spielers
     * @param maximum Die eindimensionale maximale Position des Spielers
     * @return Gibt zurueck, in welchem Drittel sich der Spieler bezueglich des Maxiumums (z.B. Fensterbreite) befindet.
     * 0: Drittel bei 0 z.B. links nahe am Ausschnittrand
     * 1: Drittel in der Mitte des Ausschnitts
     * 2: Drittel bei Maxium z.B. rechts nahe am Ausschnittrand
     */
    private int posAufBildschirm(int position, int maximum) {
        if(position < maximum / 3)
            // erstes Drittel nahe der 0
            return 0;
        else if(position > maximum - maximum / 3)
            // Drittel nahe dem Maximum
            return 2;
        else
            // mittleres Drittel dazwischen
            return 1;
    }
}
