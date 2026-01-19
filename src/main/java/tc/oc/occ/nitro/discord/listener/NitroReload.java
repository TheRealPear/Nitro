package tc.oc.occ.nitro.discord.listener;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.NotNull;
import tc.oc.occ.nitro.NitroCloudy;
import tc.oc.occ.nitro.NitroConfig;
import tc.oc.occ.nitro.discord.DiscordBot;

public class NitroReload extends NitroListener {

  public NitroReload(DiscordBot api, NitroConfig config) {
    super(api, config);
  }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("config-reload")) {
            if (!event.getChannel().getId().equals(config.getStaffChannel())) {
                event.reply(":warning: Run this command in staff channel!").setEphemeral(true).queue();
                return;
            }
            event.reply(":white_check_mark: The configuration has been reloaded!").queue();
            api.alert(":arrows_clockwise: `" + event.getUser().getName() + "` (`" + event.getUser().getId() + "`) has reloaded the configuration.");
            NitroCloudy.get().reloadBotConfig();
        }
    }
}
