package tc.oc.occ.nitro.discord.listener;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import org.jetbrains.annotations.NotNull;
import tc.oc.occ.nitro.NitroConfig;
import tc.oc.occ.nitro.discord.DiscordBot;

public class NitroAddAlert extends NitroListener {

  public NitroAddAlert(DiscordBot api, NitroConfig config) {
    super(api, config);
  }

    @Override
    public void onGuildMemberRoleAdd(@NotNull GuildMemberRoleAddEvent event) {
      for(Role r : event.getRoles()) {
          if(isNitro(r)) {
              api.sendMessage(
                      ":tada: Thanks for boosting the server, "
                              + event.getMember().getUser().getAsMention()
                              + "! Use `/redeem <Minecraft username>` to claim your in-game privileges. If you need assistance, use `/help` or contact a staff member.",
                      false);
          }
      }
  }
}
