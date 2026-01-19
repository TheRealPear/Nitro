package tc.oc.occ.nitro.discord.listener;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.NotNull;
import tc.oc.occ.nitro.NitroConfig;
import tc.oc.occ.nitro.discord.DiscordBot;

public class NitroList extends NitroListener  {
  public NitroList(DiscordBot api, NitroConfig config) { super(api, config);}

    private final String[] listTypes = new String[]{"boosters", "commands"};

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        EmbedBuilder boostersList = new EmbedBuilder()
                .setTitle("Nitro Boosters")
                .setDescription("List of active Nitro boosters found in the configuration file.")
                .addField("Format", "`discriminated user:discord id:minecraft username`", false);
        EmbedBuilder commandsList = new EmbedBuilder()
                .setTitle("Nitro redemption/removal commands")
                .setDescription("List of redemption/removal commands found in the configuration file.");
        if (event.getName().equals("list")) {
            if (event.getChannelId() != config.getStaffChannel()) {
                event.reply(":warning: Run this command in staff channel!").queue();
            }
            OptionMapping optionMapping = event.getOption("list");
            if (optionMapping == null) return;
            String messageInput = optionMapping.getAsString();

            switch (messageInput.toLowerCase()) {
                case "boosters":
                    boostersList.addField("Boosters", config.getUsers().stream().map(user -> "`- " + user.getDiscordUsername() + ":" + user.getDiscordId() + ":" + user.getMinecraftUsername() + "`").collect(Collectors.joining("\n")), false);
                    event.replyEmbeds(boostersList.build()).queue();
                case "commands":
                    if (config.getRedemptionCommands().isEmpty()) {
                        commandsList.addField("Redemption commands", "_No redemption commands found_", false);
                    } else {
                        commandsList.addField(
                                "Redemption commands",
                                config.getRedemptionCommands().stream()
                                        .map(command -> "`- " + command + "`")
                                        .collect(Collectors.joining("\n")), false);
                    }
                    if (config.getRemovalCommands().isEmpty()) {
                        commandsList.addField("Removal commands", "_No removal commands found_", false);
                    } else {
                        commandsList.addField(
                                "Removal commands",
                                config.getRemovalCommands().stream()
                                        .map(command -> "`- " + command + "`")
                                        .collect(Collectors.joining("\n")), false);
                    }
                    event.replyEmbeds(commandsList.build()).queue();
                default:
                    event.reply(":no_entry_sign: Invalid list provided! Options are boosters or commands").setEphemeral(true).queue();
            }
        }
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
      if (event.getName().equals("list") && event.getFocusedOption().getName().equals("list")) {
          List<Command.Choice> options = Stream.of(listTypes)
                  .filter(word -> word.startsWith(event.getFocusedOption().getValue()))
                  .map(word -> new Command.Choice(word, word))
                  .collect(Collectors.toList());
          event.replyChoices(options).queue();
      }
    }

//    @Override
//    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
//        EmbedBuilder boostersList = new EmbedBuilder()
//                .setTitle("Nitro Boosters")
//                .setDescription("List of active Nitro boosters found in the configuration file.")
//                .addField("Format", "`discriminated user:discord id:minecraft username`", false)
//                .setFooter("Requested by " + event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
//        EmbedBuilder bansList = new EmbedBuilder()
//                        .setTitle("Nitro banned users")
//                        .setDescription("List of banned users found in the configuration file.")
//                        .addField("Format", "`discriminated user:discord id`", false)
//                        .setFooter("Requested by " + event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
//
//        EmbedBuilder commandsList = new EmbedBuilder()
//                        .setTitle("Nitro redemption/removal commands")
//                        .setDescription("List of redemption/removal commands found in the configuration file.")
//                        .setFooter("Requested by " + event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
//
//        if (event.getChannel().getId().equals(config.getStaffChannel())) {
//            if (event.getMessage().getContentDisplay().startsWith("!nitro-list")) {
//                String[] parts = event.getMessage().getContentDisplay().split(" ");
//                if (parts.length == 2) {
//                    if (parts[1].equalsIgnoreCase("boosters")) {
//                        if (config.getUsers().isEmpty()) {
//                            boostersList.addField("Boosters", "_No nitro boosters found_", false);
//                        } else {
//                            boostersList.addField(
//                                    "Boosters",
//                                    config.getUsers().stream()
//                                            .map(
//                                                    user ->
//                                                            "`- "
//                                                                    + user.getDiscordUsername()
//                                                                    + ":"
//                                                                    + user.getDiscordId()
//                                                                    + ":"
//                                                                    + user.getMinecraftUsername()
//                                                                    + "`")
//                                            .collect(Collectors.joining("\n")), false);
//                        }
//                        event.getChannel().sendMessageEmbeds(boostersList.build()).queue();
//                    } else if (parts[1].equalsIgnoreCase("bans")) {
//                        if (config.getBannedUsers().isEmpty()) {
//                            bansList.addField("Banned users", "_No banned users found_", false);
//                        } else {
//                            bansList.addField(
//                                    "Banned users",
//                                    config.getBannedUsers().stream()
//                                            .map(
//                                                    user ->
//                                                            "`- "
//                                                                    + user.getDiscordUsername()
//                                                                    + ":"
//                                                                    + user.getDiscordId()
//                                                                    + "`")
//                                            .collect(Collectors.joining("\n")), false);
//                        }
//                        event.getChannel().sendMessageEmbeds(bansList.build()).queue();
//                    } else if (parts[1].equalsIgnoreCase("commands")) {
//                        if (config.getRedemptionCommands().isEmpty()) {
//                            commandsList.addField("Redemption commands", "_No redemption commands found_", false);
//                        } else {
//                            commandsList.addField(
//                                    "Redemption commands",
//                                    config.getRedemptionCommands().stream()
//                                            .map(command -> "`- " + command + "`")
//                                            .collect(Collectors.joining("\n")), false);
//                        }
//                        if (config.getRemovalCommands().isEmpty()) {
//                            commandsList.addField("Removal commands", "_No removal commands found_", false);
//                        } else {
//                            commandsList.addField(
//                                    "Removal commands",
//                                    config.getRemovalCommands().stream()
//                                            .map(command -> "`- " + command + "`")
//                                            .collect(Collectors.joining("\n")), false);
//                        }
//                        event.getChannel().sendMessageEmbeds(commandsList.build()).queue();
//                    } else {
//                        event.getChannel().sendMessage(new MessageCreateBuilder().addContent(":warning: List not found! Please use `!nitro-list <boosters|bans|commands>`").build()).queue();
//                    }
//                } else {
//                    event.getChannel().sendMessage(new MessageCreateBuilder().addContent(":warning: Incorrect syntax! Please use `!nitro-list <boosters|bans|commands>`").build()).queue();
//                }
//            }
//        }
//    }
}
