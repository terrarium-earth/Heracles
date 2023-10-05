package earth.terrarium.heracles.api.client.settings.rewards;

import earth.terrarium.heracles.api.client.settings.CustomizableQuestElementSettings;
import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.base.TextSetting;
import earth.terrarium.heracles.api.rewards.defaults.CommandReward;
import net.minecraft.Optionull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CommandRewardSettings implements SettingInitializer<CommandReward>, CustomizableQuestElementSettings<CommandReward> {

    public static final CommandRewardSettings INSTANCE = new CommandRewardSettings();

    @Override
    public CreationData create(@Nullable CommandReward object) {
        CreationData settings = CustomizableQuestElementSettings.super.create(object);
        settings.put("command", TextSetting.INSTANCE, getDefaultCommand(object));
        return settings;
    }

    @Override
    public CommandReward create(String id, @Nullable CommandReward object, Data data) {
        return create(object, data, (title, icon) -> new CommandReward(
            id,
            title,
            icon,
            Optionull.mapOrDefault(data.get("command", TextSetting.INSTANCE), Optional::get, getDefaultCommand(object))
        ));
    }

    private static String getDefaultCommand(@Nullable CommandReward object) {
        return Optionull.mapOrDefault(object, CommandReward::command, "tellraw @s \"Hello World!\"");
    }
}

