package org.cubecraftutilities.core.config.subconfig;


import net.labymod.api.client.entity.LivingEntity.EquipmentSpot;
import net.labymod.api.client.gui.screen.widget.widgets.input.SliderWidget.SliderSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget.TextFieldSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.annotation.ParentSwitch;
import net.labymod.api.configuration.loader.annotation.SpriteSlot;
import net.labymod.api.configuration.loader.annotation.SpriteTexture;
import net.labymod.api.configuration.loader.property.ConfigProperty;

@SpriteTexture("setting_icons.png")
public class ArmourBreakWarningSubConfig extends Config {

  @ParentSwitch
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(false);

  @SpriteSlot(x = 7, y = 2)
  @SliderSetting(min = 0, max = 20)
  private final ConfigProperty<Integer> durabilityWarning = new ConfigProperty<>(10);

  private final ArmourBreakSoundsSubConfig armourBreakSoundsSubConfig = new ArmourBreakSoundsSubConfig();

  @SwitchSetting
  private final ConfigProperty<Boolean> chat = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> actionbar = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> notification = new ConfigProperty<>(true);


  public ConfigProperty<Boolean> getEnabled() {return enabled;}
  public ConfigProperty<Integer> getDurabilityWarning() {return durabilityWarning;}
  public ConfigProperty<Boolean> getActionbar() {return actionbar;}
  public ConfigProperty<Boolean> getChat() {return chat;}
  public ConfigProperty<Boolean> getNotification() {return notification;}
  public ConfigProperty<String> getSoundId(EquipmentSpot spot) {
    switch (spot) {
      case FEET:
        return armourBreakSoundsSubConfig.getSoundIdBoots();
      case LEGS:
        return armourBreakSoundsSubConfig.getSoundIdLeggings();
      case CHEST:
        return armourBreakSoundsSubConfig.getSoundIdChestplate();
      case HEAD:
        return armourBreakSoundsSubConfig.getSoundIdHelmet();
    }
    return armourBreakSoundsSubConfig.getSoundIdHelmet();
  }
}
