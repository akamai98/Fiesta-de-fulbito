package armameeldoparti;

import static org.junit.jupiter.api.Assertions.assertEquals;

import armameeldoparti.models.Player;
import armameeldoparti.models.Position;
import java.util.stream.Stream;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

/**
 * Players unit tests class.
 *
 * @author Bonino, Francisco Ignacio.
 *
 * @version 1.0.0
 *
 * @since 23/07/2022
 */
@TestInstance(Lifecycle.PER_CLASS)
class PlayersTests implements ArgumentsProvider {

  /**
   * Simple unit tests for players creation.
   */
  @ParameterizedTest
  @ArgumentsSource(PlayersTests.class)
  void creationTest(String name, Position position) {
    Player player = new Player(name, position);

    assertEquals(0, player.getAnchorageNumber());
    assertEquals(0, player.getSkill());
    assertEquals(0, player.getTeam());
    assertEquals(name, player.getName());
    assertEquals(position, player.getPosition());
  }

  /**
   * Generates values for the tests.
   *
   * @return An array with parameters to be used in the unit tests.
   */
  @Override
  public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
    return Stream.of(
      Arguments.of("player a", Position.CENTRAL_DEFENDER),
      Arguments.of("player b", Position.LATERAL_DEFENDER),
      Arguments.of("player c", Position.MIDFIELDER),
      Arguments.of("player d", Position.FORWARD),
      Arguments.of("player e", Position.GOALKEEPER)
    );
  }
}