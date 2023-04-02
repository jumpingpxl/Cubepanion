package org.cubecraftutilities.core.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import net.labymod.api.client.chat.command.Command;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.event.ClickEvent;
import net.labymod.api.client.component.event.HoverEvent;
import net.labymod.api.client.component.format.TextDecoration;
import org.cubecraftutilities.core.CCU;
import org.cubecraftutilities.core.config.imp.GameStatsTracker;
import org.cubecraftutilities.core.config.subconfig.CommandSystemSubConfig;
import org.cubecraftutilities.core.config.subconfig.StatsTrackerSubConfig;
import org.cubecraftutilities.core.i18nNamespaces;
import org.cubecraftutilities.core.managers.CCUManager;
import org.cubecraftutilities.core.utils.Colours;

public class StatCommands extends Command {

  private final CCU addon;
  private final Pattern timeFormat = Pattern.compile("\\b(20[0-9]{2})-([1-9]|1[1-2])-(1[0-9]|2[0-9]|3[0-1]|[1-9])\\b");

  private final Function<String, Component> errorComponent;
  private final Function<String, String> errorKey;
  private final Function<String, Component> helpComponent;
  public StatCommands(CCU addon) {
    super("stats");

    this.addon = addon;
    this.messagePrefix = addon.prefix();

    this.errorComponent = i18nNamespaces.commandNamespaceTransformer("StatCommands.error");
    this.errorKey = i18nNamespaces.commandNameSpaceMaker("StatCommands.error");
    this.helpComponent = i18nNamespaces.commandNamespaceTransformer("StatCommands.helpCommand");
  }

  @Override
  public boolean execute(String prefix, String[] arguments) {
    if (!this.addon.getManager().onCubeCraft()) {
      return false;
    }
    CommandSystemSubConfig commandSystemSubConfig = this.addon.configuration().getCommandSystemSubConfig();

    if (!commandSystemSubConfig.getStatsCommand().get() || !commandSystemSubConfig.getEnabled().get()) {
      return false;
    }

    if (arguments.length > 0
    && arguments[0].equalsIgnoreCase("help")) {
      this.helpCommand();
      return true;
    }


    CCUManager manager = this.addon.getManager();
    StatsTrackerSubConfig config = this.addon.configuration().getStatsTrackerSubConfig();
    HashMap<String, GameStatsTracker> allGameStatsTrackers = config.getGameStatsTrackers();

    String gameName = this.getGameString(arguments);

    GameStatsTracker gameStatsTracker;
    if (gameName == null) {
       gameStatsTracker = allGameStatsTrackers.get(manager.getDivisionName());
    } else {
      gameStatsTracker = allGameStatsTrackers.get(gameName);
    }

    if (gameStatsTracker == null) {
      this.displayMessage(this.errorComponent.apply("gameNotFound.text").color(Colours.Error)
          .hoverEvent(HoverEvent.showText(this.errorComponent.apply("gameNotFound.text").color(Colours.Hover)))
          .append(this.gamesList().color(Colours.Error)));
      return false;
    }

    if (gameName != null) {
      arguments = this.removeGameNameFromArguments(arguments, gameName);
    }

    switch (arguments.length) {
      case 0: {
        this.displayMessage(gameStatsTracker.getDisplayComponent());
        return true;
      }
      case 1: {
        if (this.timeFormat.matcher(arguments[0]).matches()) {
          GameStatsTracker snapShot = gameStatsTracker.getHistoricalData(arguments[0]);
          if (snapShot == null) {
            this.displayMessage(Component.translatable(
                    this.errorKey.apply("statsNotFoundOnDate"),
                    Component.text(gameStatsTracker.getGame()),
                    Component.text(arguments[0]))
                    .color(Colours.Error));
          } else {
            this.displayMessage(snapShot.getDisplayComponent());
          }
        } else {
          this.displayMessage(gameStatsTracker.getUserStatsDisplayComponent(arguments[0]));
        }
        return true;
      }
      case 2: {

        String date;
        String userName;

        if (this.timeFormat.matcher(arguments[0]).matches()) {
          date = arguments[0];
          userName = arguments[1];
        } else if (this.timeFormat.matcher(arguments[1]).matches()) {
          date = arguments[1];
          userName = arguments[0];
        } else {
          break;
        }

        GameStatsTracker snapShot = gameStatsTracker.getHistoricalData(date);
        if (snapShot == null) {
          this.displayMessage(Component.translatable(
                  this.errorKey.apply("statsNotFoundOnDate"),
                  Component.text(gameStatsTracker.getGame()),
                  Component.text(date))
              .color(Colours.Error));
        } else {
          this.displayMessage(snapShot.getUserStatsDisplayComponent(userName));
        }
      }
    }
    return false;
  }

  private void helpCommand() {
    Component helpComponent = this.helpComponent.apply("title")
        .color(Colours.Title)
        .decorate(TextDecoration.BOLD)

        .append(Component.text("\n/stats <game>", Colours.Primary)
            .clickEvent(ClickEvent.suggestCommand("/stats ")))
        .append(this.helpComponent.apply("displayGlobalStats").color(Colours.Secondary))

        .append(Component.text("\n/stats <game> [username]", Colours.Primary)
            .clickEvent(ClickEvent.suggestCommand("/stats ")))
        .append(this.helpComponent.apply("displayPlayerStats").color(Colours.Secondary))

        .append(Component.text("\n/stats <game> <YYYY-MM-DD>", Colours.Primary)
            .clickEvent(ClickEvent.suggestCommand("/stats ")))
        .append(this.helpComponent.apply("displayGlobalStatsOnDate").color(Colours.Secondary))

        .append(Component.text("\n/stats <game> <YYYY-MM-DD> <username>", Colours.Primary)
            .clickEvent(ClickEvent.suggestCommand("/stats "))
            .hoverEvent(HoverEvent.showText(this.helpComponent.apply("requiredSetting").color(Colours.Hover))))
        .append(this.helpComponent.apply("displayPlayerStatsOnDate").color(Colours.Secondary)
            .hoverEvent(HoverEvent.showText(this.helpComponent.apply("requiredSetting").color(Colours.Hover))))

        .append(Component.text("\n/stats help", Colours.Primary)
            .clickEvent(ClickEvent.suggestCommand("/stats help")))
        .append(this.helpComponent.apply("this").color(Colours.Secondary))

        .append(this.helpComponent.apply("tracking").color(Colours.Primary))
        .append(this.gamesList().color(Colours.Secondary));

    this.displayMessage(helpComponent);
  }

  private Component gamesList() {
    Component comp = Component.empty();
    Set<String> keySet = this.addon.configuration().getStatsTrackerSubConfig().getGameStatsTrackers().keySet();
    List<String> keys = new ArrayList<>(keySet);
    for (int i = 0; i < keys.size(); i++) {
      String game = keys.get(i);
      comp = comp
          .append(Component.text(game)
              .clickEvent(ClickEvent.suggestCommand("/stats " + game + " ")));
      if (i != keys.size() - 1) {
        comp = comp.append(Component.text(", "));
      }
    }
    return comp;
  }

  private String[] removeGameNameFromArguments(String[] arguments, String gameName) {
    String[] gameComponents = gameName.split(" ");

    for (String comp : gameComponents) {
      arguments = this.removeFromArguments(arguments, comp);
    }
    return arguments;
  }

  private String[] removeFromArguments(String[] arguments, String member) {
    String[] newArguments = new String[arguments.length - 1];

    int j = 0;
    for (String argument : arguments) {
      if (!argument.equals(member)) {
        newArguments[j] = argument;
        j++;
      }
    }
    return newArguments;
  }

  private String getGameString(String[] arguments) {
    String game = "";

    for (String arg : arguments) {
      game = (game + " " + arg).trim();

      GameStatsTracker tracker = this.addon.configuration().getStatsTrackerSubConfig().getGameStatsTrackers()
          .get(game);

      if (tracker != null) {
        return game;
      }
    }

    return null;
  }
}
