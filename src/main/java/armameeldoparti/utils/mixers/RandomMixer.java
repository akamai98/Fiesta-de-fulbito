package armameeldoparti.utils.mixers;

import armameeldoparti.models.Error;
import armameeldoparti.models.Player;
import armameeldoparti.models.Position;
import armameeldoparti.models.Team;
import armameeldoparti.utils.common.CommonFields;
import armameeldoparti.utils.common.CommonFunctions;
import armameeldoparti.utils.common.Constants;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.NonNull;

/**
 * Random distribution class.
 *
 * @author Bonino, Francisco Ignacio.
 *
 * @version 0.0.1
 *
 * @since 12/07/2022
 */
public class RandomMixer implements PlayersMixer {

  // ---------------------------------------- Private fields ------------------------------------

  private int randomTeam1;
  private int randomTeam2;

  private final Random randomGenerator;

  // ---------------------------------------- Constructor ---------------------------------------

  /**
   * Builds the random distributor.
   */
  public RandomMixer() {
    randomGenerator = new Random();
  }

  // ---------------------------------------- Public methods ------------------------------------

  /**
   * Distributes the players randomly without considering anchorages.
   *
   * <p>Half of the players of each players-set are randomly assigned a team number.
   *
   * <p>The rest of the players in the group without team (team == 0) are assigned
   * the opposing team number.
   *
   * @param teams List that contains the two teams.
   *
   * @return The updated teams with the players distributed randomly
   *         without considering anchorages.
   */
  @Override
  @NonNull
  public List<Team> withoutAnchorages(@NonNull List<Team> teams) {
    Player chosenPlayer;

    shuffleTeamNumbers(teams.size());

    for (Position position : Position.values()) {
      List<Player> playersSet = CommonFields.getPlayersSets()
                                            .get(position);

      for (int i = 0; i < playersSet.size() / 2; i++) {
        chosenPlayer = playersSet.get(
          playersSet.indexOf(
            getRandomUnassignedPlayer(playersSet.stream()
                                                .filter(p -> p.getTeamNumber() == 0)
                                                .collect(Collectors.toList()))
          )
        );

        chosenPlayer.setTeamNumber(randomTeam1 + 1);

        teams.get(randomTeam1)
             .getTeamPlayers()
             .get(chosenPlayer.getPosition())
             .add(chosenPlayer);
      }

      // Remaining unassigned players
      playersSet.stream()
                .filter(p -> p.getTeamNumber() == 0)
                .forEach(p -> {
                  p.setTeamNumber(randomTeam2 + 1);

                  teams.get(randomTeam2)
                       .getTeamPlayers()
                       .get(p.getPosition())
                       .add(p);
                });
    }

    return teams;
  }

  /**
   * Distributes the players randomly considering anchorages.
   *
   * <p>First, the anchored players are grouped in different lists by their
   * anchorage number, and they are distributed randomly starting with the
   * sets with most anchored players in order to avoid inconsistencies.
   * If a set of anchored players cannot be added to one team, it will be
   * added to the other.
   *
   * <p>Then, the players that are not anchored are distributed
   * randomly. They will be added to a team only if the players
   * per position or the players per team amounts are not exceeded.
   *
   * @param teams List that contains the two teams.
   * @return The updated teams with the players distributed randomly considering anchorages.
   */
  @Override
  @NonNull
  public List<Team> withAnchorages(@NonNull List<Team> teams) {
    List<List<Player>> anchoredPlayers = CommonFunctions.getAnchoredPlayers();

    for (List<Player> aps : anchoredPlayers) {
      shuffleTeamNumbers(teams.size());

      int teamNumber = -1;

      if (anchorageCanBeAdded(teams.get(randomTeam1), aps)) {
        teamNumber = randomTeam1;
      } else if (anchorageCanBeAdded(teams.get(randomTeam2), aps)) {
        teamNumber = randomTeam2;
      } else {
        CommonFunctions.exitProgram(Error.FATAL_INTERNAL_ERROR);
      }

      for (Player p : aps) {
        p.setTeamNumber(teamNumber + 1);

        teams.get(teamNumber)
             .getTeamPlayers()
             .get(p.getPosition())
             .add(p);
      }
    }

    CommonFields.getPlayersSets()
                .values()
                .stream()
                .flatMap(List::stream)
                .filter(p -> p.getTeamNumber() == 0)
                .forEach(p -> {
                  shuffleTeamNumbers(teams.size());

                  int teamNumber = randomTeam1;

                  if (teams.get(teamNumber)
                           .isPositionFull(p.getPosition())
                      || teams.get(teamNumber)
                              .getPlayersCount() + 1 > Constants.PLAYERS_PER_TEAM) {
                    teamNumber = randomTeam2;
                  }

                  p.setTeamNumber(teamNumber + 1);

                  teams.get(teamNumber)
                       .getTeamPlayers()
                       .get(p.getPosition())
                       .add(p);
                });

    return teams;
  }

  // ---------------------------------------- Private methods -----------------------------------

  /**
   * Randomly shuffles the team numbers.
   *
   * @param range Upper limit (exclusive) for the random number generator.
   */
  private void shuffleTeamNumbers(int range) {
    randomTeam1 = randomGenerator.nextInt(range);
    randomTeam2 = 1 - randomTeam1;
  }

  /**
   * Checks if a set of anchored players can be added to a team.
   *
   * <p>It checks if any of the positions of the anchored players
   * in the destination team is already complete, and it checks
   * if adding them does not exceed the number of players allowed
   * per position per team. This is done in order to avoid more
   * than half of the registered players of the same position
   * remaining on the same team.
   *
   * @param team            Team where the anchored players should be added.
   * @param anchoredPlayers List containing the players with the same anchorage number.
   *
   * @return If a set of anchored players can be added to a team.
   */
  private boolean anchorageCanBeAdded(@NonNull Team team,
                                      @NonNull List<Player> anchoredPlayers) {
    return !(anchorageOverflowsTeamSize(team, anchoredPlayers)
             || anchorageOverflowsAnyPositionSet(team, anchoredPlayers));
  }

  /**
   * Checks if the amount of anchored players to be added to a
   * team would exceed the maximum allowed amount of players
   * per team.
   *
   * @param team Team to check if the anchored players can be added.
   * @param anchoredPlayers Anchored players to check.
   *
   *  @return If the amount of anchored players to be added to a team
   *          would exceed the maximum allowed amount of players per team.
   */
  private boolean anchorageOverflowsTeamSize(@NonNull Team team,
                                             @NonNull List<Player> anchoredPlayers) {
    return team.getPlayersCount() + anchoredPlayers.size() > Constants.PLAYERS_PER_TEAM;
  }

  /**
   * Checks if the amount of anchored players to be added to a
   * team would exceed the maximum allowed amount of players
   * per team in any position set.
   *
   * @param team Team to check if the anchored players can be added.
   * @param anchoredPlayers Anchored players to check.
   *
   *  @return If the amount of anchored players to be added to a
   *          team would exceed the maximum allowed amount of players
   *          per team in any position set.
   */
  private boolean anchorageOverflowsAnyPositionSet(@NonNull Team team,
                                                   @NonNull List<Player> anchoredPlayers) {
    return anchoredPlayers.stream()
                          .anyMatch(p -> team.isPositionFull(p.getPosition())
                                         || anchorageOverflowsPositionSet(team, anchoredPlayers,
                                                                          p.getPosition()));
  }

  /**
   * Checks if the amount of anchored players to be added to a
   * position set in a team would exceed the maximum allowed
   * amount of players per team for that particular position.
   *
   * @param team Team to check if the anchored players can be added.
   * @param anchoredPlayers Anchored players to check.
   * @param position Anchored players position.
   *
   * @return If the amount of anchored players to be added to a
   *         position set in a team would exceed the maximum allowed
   *         amount of players per team for that particular position.
   */
  private boolean anchorageOverflowsPositionSet(@NonNull Team team,
                                                @NonNull List<Player> anchoredPlayers,
                                                @NonNull Position position) {
    return team.getTeamPlayers()
               .get(position)
               .size()
           + anchoredPlayers.stream()
                            .filter(ap -> ap.getPosition() == position)
                            .count()
           > CommonFields.getPlayersAmountMap()
                         .get(position);
  }

  /**
   * Gets a random player from a list containing players that have not been assigned a team yet.
   *
   * @param unassignedPlayersList List containing players without any team assigned yet.
   *
   * @return A random player that has not been assigned a team yet.
   */
  @NonNull
  private Player getRandomUnassignedPlayer(@NonNull List<Player> unassignedPlayersList) {
    return unassignedPlayersList.get(randomGenerator.nextInt(unassignedPlayersList.size()));
  }
}