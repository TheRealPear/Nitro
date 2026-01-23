package tc.oc.occ.nitro.discord.listener;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;
import tc.oc.occ.nitro.NitroCloudy;
import tc.oc.occ.nitro.NitroConfig;
import tc.oc.occ.nitro.WebUtils;
import tc.oc.occ.nitro.data.NitroUser;
import tc.oc.occ.nitro.discord.DiscordBot;
import tc.oc.occ.nitro.events.NitroUserAddEvent;

public class NitroRedeemer extends NitroListener  {

  public NitroRedeemer(DiscordBot api, NitroConfig config) {
    super(api, config);
  }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("redeem")) {
            OptionMapping messageOption = event.getOption("username");
            if (messageOption == null ) return; // Don't need bot to respond because Discord forces input from user.
            User user = event.getUser();
            if (!isNitro(user)) {
                event.reply(":no_entry_sign: You are not allowed to use this command! If you believe this is a mistake, contact a staff member.").setEphemeral(true).queue();
                return;
            }
            String discordUsername = user.getName();
            String discordID = user.getId();
            if (config.getUser(discordID).isPresent()) {
                NitroUser nitroUser = config.getUser(discordID).get();
                event.reply(":no_entry_sign: Your Nitro Boosting privileges have already been claimed for "
                        + nitroUser.getMinecraftUsername()
                        + "(`" + nitroUser.getPlayerId().toString()
                        + "`). If you wish to change this, use `/remove` or contact a staff member.").setEphemeral(true).queue();
                return;
            }
            String minecraftUsername = messageOption.getAsString();
            WebUtils.getUUID(minecraftUsername).thenAcceptAsync(uuid -> {
                if (uuid == null) {
                    event.reply(":warning: Unable to find UUID for provided Minecraft username!").setEphemeral(true).setEphemeral(true).queue();
                    return;
                }
                NitroUser nitro = config.addNitro(discordUsername, discordID, minecraftUsername, uuid);
                NitroCloudy.get().callSyncEvent(new NitroUserAddEvent(nitro));
                event.reply(":white_check_mark: "
                        + " Your Nitro Boosting privileges have been claimed for `"
                        + nitro.getMinecraftUsername()
                        + "` (`" + nitro.getPlayerId().toString()
                        + "`). If something went wrong, or you're missing in-game perks, contact a staff member. Thanks for boosting the server!").setEphemeral(true).queue();

            });
        }
    }
}
