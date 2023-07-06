package armameeldoparti.utils.common;

import armameeldoparti.controllers.Controller;
import armameeldoparti.models.Player;
import armameeldoparti.models.Position;
import armameeldoparti.models.ProgramView;
import java.awt.GraphicsDevice;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 * Common-use fields class.
 *
 * @author Bonino, Francisco Ignacio.
 *
 * @version 0.0.1
 *
 * @since v3.0
 */
public final class CommonFields {

  // ---------------------------------------- Private fields ------------------------------------

  @Getter @Setter private static int distribution;

  @Getter @Setter private static boolean anchoragesEnabled;

  @Getter @Setter private static GraphicsDevice activeMonitor;

  @Getter @Setter private static Map<Position, Integer> playersAmountMap;
  @Getter @Setter private static Map<Position, List<Player>> playersSets;
  @Getter @Setter private static Map<Position, String> positionsMap;
  @Getter @Setter private static Map<ProgramView, Controller> controllersMap;

  // ---------------------------------------- Constructor ---------------------------------------

  /**
   * Empty, private constructor. Not needed.
   */
  private CommonFields() {
    // Not needed
  }
}