package armameeldoparti.controllers;

import armameeldoparti.interfaces.Controller;
import armameeldoparti.models.Player;
import armameeldoparti.utils.Main;
import armameeldoparti.views.NamesInputView;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * Names input view controller class.
 *
 * @author Bonino, Francisco Ignacio.
 *
 * @version 0.0.1
 *
 * @since 26/07/2022
 */
public class NamesInputController implements Controller {

  // ---------------------------------------- Private constants ---------------------------------

  /**
   * Possible players distribution methods.
   */
  private static final String[] OPTIONS_MIX = {
    "Aleatoriamente",
    "Por puntuaciones"
  };

  // ---------------------------------------- Private fields ------------------------------------

  private NamesInputView namesInputView;

  // ---------------------------------------- Constructor ---------------------------------------

  /**
   * Builds the names input view controller.
   *
   * @param namesInputView View to control.
   */
  public NamesInputController(NamesInputView namesInputView) {
    this.namesInputView = namesInputView;
  }

  // ---------------------------------------- Public methods ------------------------------------

  /**
   * Makes the controlled view visible.
   *
   * <p>Updates the view state according to the combobox
   * initial state, and makes it visible.
   */
  @Override
  public void showView() {
    updateTextFields(namesInputView.getComboBox()
                                   .getSelectedItem()
                                   .toString());

    namesInputView.setVisible(true);
  }

  /**
   * Makes the controlled view invisible.
   */
  @Override
  public void hideView() {
    namesInputView.setVisible(false);
    Controller.centerView(namesInputView);
  }

  /**
   * Resets the controlled view to its default values and
   * makes it invisible
   */
  @Override
  public void resetView() {
    hideView();
    clearPlayersNames();

    namesInputView.getAnchoragesCheckBox()
                  .setSelected(false);

    namesInputView.getComboBox()
                  .setSelectedIndex(0);

    namesInputView.getTextArea()
                  .setText(null);

    updateTextFields(namesInputView.getComboBox()
                                   .getItemAt(0));
  }

  /**
   * 'Back' button event handler.
   *
   * <p>Resets the controlled view to its default values, makes
   * it invisible and shows the main menu view.
   */
  public void backButtonEvent() {
    resetView();

    Main.getMainMenuController()
        .showView();
  }

  /**
   * 'Mix' button event handler.
   *
   * <p>Asks the user for the players distribution method, makes
   * the controlled view invisible and shows the corresponding
   * following view.
   */
  public void mixButtonEvent() {
    int distribution = JOptionPane.showOptionDialog(
        null, "Seleccione el criterio de distribución de jugadores",
        "Antes de continuar...", 2, JOptionPane.QUESTION_MESSAGE,
        Main.SCALED_ICON, OPTIONS_MIX, OPTIONS_MIX[0]
    );

    if (distribution == JOptionPane.CLOSED_OPTION) {
      return;
    }

    Main.setDistribution(distribution);

    if (Main.thereAreAnchorages()) {
      Main.getAnchoragesController()
          .updateCheckBoxesText();

      Main.getAnchoragesController()
          .showView();
    } else if (Main.getDistribution() == Main.RANDOM_MIX) {
      // Random distribution
      Main.getResultsController()
          .setUp();

      Main.getResultsController()
          .showView();
    } else {
      // By skill points distribution
      Main.getSkillsInputController()
          .updateNameLabels();

      Main.getSkillsInputController()
          .showView();
    }

    hideView();
  }

  /**
   * Text fields input event handler.
   *
   * <p>Validates the user input with a regular expression that checks if the string
   * contains only latin characters from A to Z including Ñ, uppercase or lowercase,
   * with or without accent mark, with or without spaces.
   * If the input is not valid or already exists, the program asks for a new input.
   *
   * <p>If the input is valid, it will be applied as a player name in the players set
   * corresponding to the combobox selected option.
   */
  public void textFieldEvent(List<JTextField> textFieldSet, List<Player> playersSet,
                             JTextField tf, JTextField source) {
    if (!(Pattern.matches(Main.NAMES_VALIDATION_REGEX, tf.getText()))) {
      JOptionPane.showMessageDialog(null,
                                    "El nombre del jugador debe estar formado por letras"
                                    + " de la A a la Z", "¡Error!",
                                    JOptionPane.ERROR_MESSAGE, null);
      tf.setText(null);
    } else {
      String name = tf.getText()
                      .trim()
                      .toUpperCase()
                      .replace(" ", "_");

      if ((name.length() > Main.MAX_NAME_LEN) || name.isBlank()
          || name.isEmpty() || alreadyExists(name)) {
        JOptionPane.showMessageDialog(null,
                                      "El nombre del jugador no puede estar vacío,"
                                      + " tener más de " + Main.MAX_NAME_LEN
                                      + " caracteres, o estar repetido",
                                      "¡Error!", JOptionPane.ERROR_MESSAGE, null);
        tf.setText(null);
      } else {
        playersSet.get(textFieldSet.indexOf(source))
                  .setName(name);

        updateTextArea();

        // The mix button is enabled only when every player has a name
        namesInputView.getMixButton()
                      .setEnabled(!alreadyExists(""));
      }
    }
  }

  /**
   * Combobox option change event handler.
   *
   * <p>Updates the shown text field according to the selected combobox option.
   *
   * @param selectedOption Combobox selected option.
   */
  public void comboBoxEvent(String selectedOption) {
    updateTextFields(selectedOption);
  }

  // ---------------------------------------- Private methods -----------------------------------

  /**
   * Updates the text displayed in the read-only text area.
   *
   * <p>The players names are shown in the order they are positioned in their respective list.
   * The order is the same of the positions enum.
   */
  private void updateTextArea() {
    var wrapperCounter = new Object() {
      private int counter;
    };

    namesInputView.getTextArea()
                  .setText(null);

    Main.getPlayersSets()
        .entrySet()
        .forEach(ps -> ps.getValue()
                         .stream()
                         .filter(p -> !p.getName()
                                        .equals(""))
                         .forEach(p -> {
                           if (wrapperCounter.counter != 0
                               && Main.PLAYERS_PER_TEAM * 2 - wrapperCounter.counter != 0) {
                             namesInputView.getTextArea()
                                           .append(System.lineSeparator());
                           }

                           wrapperCounter.counter++;

                           namesInputView.getTextArea()
                                         .append(wrapperCounter.counter + " - " + p.getName());
                         }));
  }

  /**
   * Toggles the text fields visibility.
   *
   * @param selectedOption Combobox selected option.
   */
  private void updateTextFields(String selectedOption) {
    clearLeftPanel();

    for (int i = 0; i < namesInputView.getComboBoxOptions()
                                      .length; i++) {
      if (selectedOption.equals(namesInputView.getComboBoxOptions()[i])) {
        namesInputView.getTextFieldsMap()
                      .get(Main.getCorrespondingPosition(Main.getPositionsMap(),
                                                         selectedOption.toUpperCase()))
                      .forEach(tf -> namesInputView.getLeftPanel()
                                                   .add(tf, "growx"));
        break;
      }
    }

    namesInputView.getLeftPanel()
                  .revalidate();

    namesInputView.getLeftPanel()
                  .repaint();
  }

  /**
   * Clears the players names and text fields.
   */
  private void clearPlayersNames() {
    namesInputView.getTextFieldsMap()
                  .values()
                  .stream()
                  .flatMap(List::stream)
                  .forEach(tf -> tf.setText(null));

    for (List<Player> playersSet : Main.getPlayersSets()
                                       .values()) {
      for (Player player : playersSet) {
        player.setName("");
      }
    }
  }

  /**
   * Removes the text fields from the view's left panel.
   */
  private void clearLeftPanel() {
    namesInputView.getTextFieldsMap()
                  .values()
                  .stream()
                  .flatMap(Collection::stream)
                  .filter(tf -> tf.getParent() == namesInputView.getLeftPanel())
                  .forEach(tf -> namesInputView.getLeftPanel()
                                               .remove(tf));
  }

  /**
   * Checks if there is already a player with the specified name.
   *
   * @param name Name to validate.
   *
   * @return Whether there is already a player with the specified name or not.
   */
  private boolean alreadyExists(String name) {
    return Main.getPlayersSets()
               .values()
               .stream()
               .flatMap(Collection::stream)
               .anyMatch(p -> p.getName()
                               .equals(name));
  }
}