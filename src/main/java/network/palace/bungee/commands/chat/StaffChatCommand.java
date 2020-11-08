package network.palace.bungee.commands.chat;

import net.md_5.bungee.api.ChatColor;
import network.palace.bungee.PalaceBungee;
import network.palace.bungee.handlers.PalaceCommand;
import network.palace.bungee.handlers.Player;
import network.palace.bungee.handlers.Rank;
import network.palace.bungee.handlers.RankTag;

public class StaffChatCommand extends PalaceCommand {

    public StaffChatCommand() {
        super("sc", Rank.TRAINEE);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/sc [Message]");
            return;
        }
        try {
            PalaceBungee.getMessageHandler().sendStaffMessage(RankTag.format(player.getTags()) + player.getRank().getFormattedName() +
                    " " + ChatColor.GRAY + player.getUsername() + ": " + ChatColor.GOLD +
                    ChatColor.translateAlternateColorCodes('&', String.join(" ", args)));
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "There was an error executing this command!");
        }
    }
}
