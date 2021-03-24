/**
 * Clase correspondiente a la ventana de resultados
 * en base a los jugadores ingresados y el criterio
 * de distribución elegido por el usuario.
 * 
 * @author Bonino, Francisco Ignacio.
 * 
 * @version 1.0.0
 * 
 * @since 06/03/2021
 */

import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ResultFrame extends JFrame {

    // Campos privados.
    private JPanel panel;

    /**
     * Se crea la ventana de resultados.
     * 
     * @param distribution Tipo de distribución elegida.
     * @param icon         Ícono para la ventana.
     * @param sets         Conjuntos de jugadores a mezclar.
     */
    public ResultFrame(int distribution, ImageIcon icon, List<Player[]> sets) {
        setSize(400, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setIconImage(icon.getImage());

        panel = new JPanel();

        if (distribution == 0)
            randomMix();
        else {
            RatingFrame ratingFrame = new RatingFrame();

            ratingFrame.addWindowListener(new WindowEventsHandler(this));
            ratingFrame.setVisible(true);
        }

        add(panel);
    }

    // ----------------------------------------Métodos privados---------------------------------

    /**
     * Este método se encarga de armar los equipos de manera completamente
     * aleatoria.
     */
    private void randomMix() {
        setTitle("MEZCLA ALEATORIA");
    }
}