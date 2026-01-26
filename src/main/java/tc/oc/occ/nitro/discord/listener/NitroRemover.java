package tc.oc.occ.nitro.discord.listener;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;
import tc.oc.occ.nitro.NitroCloudy;
import tc.oc.occ.nitro.NitroConfig;
import tc.oc.occ.nitro.data.NitroUser;
import tc.oc.occ.nitro.discord.DiscordBot;
import tc.oc.occ.nitro.events.NitroUserRemoveEvent;

public class NitroRemover extends NitroListener{

  public NitroRemover(DiscordBot api, NitroConfig config) {
    super(api, config);
  }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("remove")) {
            Member member = event.getMember();
            String discordID = member.getId();
            if (!config.getUser(discordID).isPresent()) {
                event.reply(":no_entry_sign: You have not yet redeemed your Nitro Boosting perks. Use `/redeem` to claim them! For more information, use `/help`.").setEphemeral(true).queue();
                return;
            }
            NitroUser nitroUser = config.getUser(discordID).get();
            NitroCloudy.get().callSyncEvent(new NitroUserRemoveEvent(nitroUser));
            event.reply(":white_check_mark: " + member.getAsMention() + " You have removed Nitro Boosting privileges from `" + nitroUser.getMinecraftUsername() + "` (`" + nitroUser.getPlayerId().toString() + "`). You may use `/redeem` to redeem them again.").setEphemeral(true).queue();
        } else if (event.getName().equals("revoke")){
            OptionMapping messageOption = event.getOption("user");
            if (messageOption == null) return;
            if (!event.getChannelId().equals(config.getStaffChannel())) {
                event.reply(":warning: Run this command in staff channel!").setEphemeral(true).queue();
                return;
            }
            String discordId = messageOption.getAsUser().getId();
            if (!config.getUser(discordId).isPresent()) {
                event.reply(":no_entry_sign: No active Nitro Booster with the Discord ID `"
                        + discordId
                        + "` was found in the configuration. Use `/list boosters` for a list of active boosters.").queue();
            }
            NitroUser user = config.getUser(discordId).get();
            NitroCloudy.get().callSyncEvent(new NitroUserRemoveEvent(user));
            event.reply(":triangular_flag_on_post: Revoked Nitro Boosting privileges from `"
                    + user.getMinecraftUsername()
                    + "`. Originally claimed by `"
                    + user.getDiscordUsername()
                    + "` (`"
                    + discordId
                    + "`)." ).queue();
            api.alert(
                    ":triangular_flag_on_post: `"
                            + event.getUser().getName()
                            + "` (`"
                            + event.getUser().getId()
                            + "`) has revoked Nitro Boosting privileges from `"
                            + user.getMinecraftUsername()
                            + "`. Originally claimed by `"
                            + user.getDiscordUsername()
                            + "` (`"
                            + discordId
                            + "`).");

        }
    }
}
