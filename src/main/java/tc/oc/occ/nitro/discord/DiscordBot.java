package tc.oc.occ.nitro.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import tc.oc.occ.nitro.NitroConfig;
import tc.oc.occ.nitro.discord.listener.*;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class DiscordBot {

  private JDA api;

  private final NitroConfig config;
  private final Logger logger;

  public DiscordBot(NitroConfig config, Logger logger) {
    this.config = config;
    this.logger = logger;
    reload();
  }

  public NitroConfig getConfig() {
    return config;
  }

  public void enable() {
    if (config.isEnabled()) {
      logger.info("Enabling Nitro DiscordBot...");
      try {
          this.api = JDABuilder.createDefault(config.getToken())
                  .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES)
                  .build();
          this.api.awaitReady();
          api.addEventListener(new NitroRedeemer(this, getConfig()));
          api.addEventListener(new NitroRemover(this, getConfig()));
          api.addEventListener(new NitroList(this, getConfig()));
          api.addEventListener(new NitroHelp(this, getConfig()));
          api.addEventListener(new NitroReload(this, getConfig()));
          api.addEventListener(new NitroAddAlert(this, getConfig()));
          api.addEventListener(new NitroRemoveAlert(this, getConfig()));
          logger.info("Discord Bot (NitroCloudy) is now active!");
          api.getGuildById(config.getServer()).updateCommands().addCommands(
                  Commands.slash("help", "List commands for the bot"),
                  Commands.slash("redeem", "Redeem nitro perks for your account")
                          .addOption(OptionType.STRING, "username", "Your Minecraft username", true),
                  Commands.slash("config-reload", "Reload the config"),
                  Commands.slash("list", "display list of active nitro boosters or the current commands in config")
                          .addOption(OptionType.STRING, "list", "Which list to display", true, true),
                  Commands.slash("remove", "Remove nitro perks"),
                  Commands.slash("force-remove", "Remove nitro perk from user")
                          .addOption(OptionType.USER, "user", "User to remove nitro perks from", true)
          ).queue();
      } catch (Exception e) {
          logger.info("Failed to login to Discord:" + e.getMessage());
      }
    }
  }

  public Guild getServer() {
      return api.getGuildById(config.getServer());
  }

  private void setAPI(JDA api) {
    this.api = api;
  }

  public void disable() {
    if (this.api != null) {
      this.api.shutdown();
    }
    this.api = null;
  }

  public void alert(String message) {
    sendMessage(message, true);
  }

  public void sendMessage(String message, boolean alert) {
    if (api != null) {
        Guild guild = api.getGuildById(config.getServer());
        if (guild != null) {
            TextChannel textChannel = alert ? guild.getTextChannelById(config.getAlertChannel()) : guild.getTextChannelById(config.getMainChannel());
            if (textChannel != null) {
                if (alert) {
                    textChannel.sendMessage(message).queue();
                } else {
                    textChannel.sendMessage(message).queue(m -> m.delete().queueAfter(15, TimeUnit.SECONDS));
                }
            }
        }
    }
  }

  public void reload() {
    if (this.api != null && !config.isEnabled()) {
      disable();
    } else if (this.api == null && config.isEnabled()) {
      enable();
    }
  }
}
