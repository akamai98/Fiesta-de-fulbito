package armameeldoparti.views;

import armameeldoparti.controllers.HelpController;
import armameeldoparti.models.ProgramView;
import armameeldoparti.utils.common.CommonFunctions;
import armameeldoparti.utils.common.Constants;
import armameeldoparti.utils.common.graphical.CustomScrollPane;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import lombok.Getter;
import net.miginfocom.layout.CC;

/**
 * Help view class.
 *
 * @author Bonino, Francisco Ignacio.
 *
 * @version 0.0.1
 *
 * @since v3.0
 */
@Getter
public class HelpView extends View {

  // ---------------------------------------- Private constants ---------------------------------

  private static final int TEXT_AREA_ROWS = 20;
  private static final int TEXT_AREA_COLUMNS = 30;

  // ---------------------------------------- Private fields ------------------------------------

  private JButton previousPageButton;
  private JButton nextPageButton;
  private JButton backButton;

  private JLabel pagesCounter;

  private JScrollPane scrollPane;

  private JTextArea textArea;

  // ---------------------------------------- Constructor ---------------------------------------

  /**
   * Builds the help view.
   */
  public HelpView() {
    super("Ayuda", "wrap");

    initializeInterface();
  }

  // ---------------------------------------- Protected methods ---------------------------------

  /**
   * Initializes the view and makes it visible.
   */
  @Override
  protected void initializeInterface() {
    addTextArea();
    addPagesLabel();
    addButtons();
    add(getMasterPanel());
    setTitle(getFrameTitle());
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setIconImage(Constants.ICON
                          .getImage());
    setResizable(false);
    pack();
  }

  /**
   * Adds the buttons to their corresponding panel.
   */
  @Override
  protected void addButtons() {
    previousPageButton = new JButton("Anterior");
    previousPageButton.addActionListener(e ->
        ((HelpController) CommonFunctions.getController(ProgramView.HELP))
        .previousPageButtonEvent()
    );

    nextPageButton = new JButton("Siguiente");
    nextPageButton.addActionListener(e ->
        ((HelpController) CommonFunctions.getController(ProgramView.HELP))
        .nextPageButtonEvent()
    );

    backButton = new JButton("Volver al menú principal");
    backButton.addActionListener(e ->
        ((HelpController) CommonFunctions.getController(ProgramView.HELP))
        .backButtonEvent()
    );

    previousPageButton.setEnabled(false);

    getMasterPanel().add(previousPageButton, new CC().width("50%")
                                                     .split());
    getMasterPanel().add(nextPageButton, new CC().width("50%")
                                                 .wrap());
    getMasterPanel().add(backButton, "growx, span");
  }

  // ---------------------------------------- Private methods -----------------------------------

  /**
   * Adds the text area where to display the instructions of the program.
   */
  private void addTextArea() {
    textArea = new JTextArea(TEXT_AREA_ROWS, TEXT_AREA_COLUMNS);

    textArea.setBackground(Constants.GREEN_LIGHT);
    textArea.setEditable(false);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);

    scrollPane = new CustomScrollPane(textArea);

    getMasterPanel().add(scrollPane);
  }

  /**
   * Adds the reading progress label.
   */
  private void addPagesLabel() {
    pagesCounter = new JLabel();

    pagesCounter.setBorder(BorderFactory.createLoweredSoftBevelBorder());
    pagesCounter.setHorizontalAlignment(SwingConstants.CENTER);

    getMasterPanel().add(pagesCounter, "growx");
  }
}