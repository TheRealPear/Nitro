package tc.oc.occ.nitro.discord.listener;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import org.jetbrains.annotations.NotNull;
import tc.oc.occ.nitro.NitroCloudy;
import tc.oc.occ.nitro.NitroConfig;
import tc.oc.occ.nitro.discord.DiscordBot;
import tc.oc.occ.nitro.events.NitroUserRemoveEvent;

public class NitroRemoveAlert extends NitroListener {

  public NitroRemoveAlert(DiscordBot api, NitroConfig config) {
    super(api, config);
  }

    @Override
    public void onGuildMemberRoleRemove(@NotNull GuildMemberRoleRemoveEvent event) {
        for(Role r : event.getRoles()) {
            if(isNitro(r)) {
                config.getUser(event.getUser().getId())
                        .ifPresent(nitro -> {
                            NitroCloudy.get().callSyncEvent(new NitroUserRemoveEvent(nitro));
                        });
            }
        }
    }
}
