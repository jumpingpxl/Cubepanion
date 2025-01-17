package org.cubepanion.core.config.subconfig;

import net.labymod.api.client.chat.ChatExecutor;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget.TextFieldSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.DropdownWidget.DropdownSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.annotation.ParentSwitch;
import net.labymod.api.configuration.loader.annotation.SpriteSlot;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingRequires;

public class EndGameSubConfig extends Config {

  @ParentSwitch
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);
  @SwitchSetting
  @SpriteSlot(x = 4, y = 2)
  @SettingRequires(value = "enabled")
  private final ConfigProperty<Boolean> onElimination = new ConfigProperty<>(false);
  @DropdownSetting
  @SpriteSlot(x = 3, y = 2)
  @SettingRequires(value = "enabled")
  private final ConfigProperty<GameEndMessage> gameEndMessage = new ConfigProperty<>(
      GameEndMessage.GG);
  @TextFieldSetting
  @SpriteSlot(x = 3, y = 2)
  @SettingRequires(value = "enabled")
  private final ConfigProperty<String> customMessage = new ConfigProperty<>("");


  public ConfigProperty<Boolean> isEnabled() {
    return this.enabled;
  }

  public ConfigProperty<Boolean> getOnElimination() {
    return this.onElimination;
  }

  public ConfigProperty<GameEndMessage> getGameEndMessage() {
    return this.gameEndMessage;
  }

  public ConfigProperty<String> getCustomMessage() {
    return this.customMessage;
  }

  public enum GameEndMessage {
    GG, WP, GOOD_GAME, WELL_PLAYED, NONE;

    public void send(ChatExecutor chat, EndGameSubConfig config, boolean party) {
      if (this != GameEndMessage.NONE) {
        chat.chat(this.toReadable(), false);
      }
      if (!config.getCustomMessage().isDefaultValue()) {
        chat.chat((party ? "!" : "") + config.getCustomMessage().get(), false);
      }
    }

    private String toReadable() {
        switch (this) {
            case GG -> {
                return "gg";
            }
            case WP -> {
                return "wp";
            }
            case GOOD_GAME -> {
                return "Good game";
            }
            case WELL_PLAYED -> {
                return "Well played";
            }
        }
      return "";
    }
  }
}

