package network.palace.bungee.utils;

import net.md_5.bungee.api.ChatColor;
import network.palace.bungee.PalaceBungee;
import network.palace.bungee.handlers.Player;
import network.palace.bungee.handlers.Rank;
import network.palace.bungee.handlers.moderation.*;

import java.util.UUID;

public class ModerationUtil {

    public void announceBan(Ban ban) throws Exception {
        sendMessage(ChatColor.GREEN + ban.getName() + ChatColor.RED + " was banned by " + ChatColor.GREEN +
                verifySource(ban.getSource()) + ChatColor.RED + " Reason: " + ChatColor.GREEN + ban.getReason() +
                ChatColor.RED + " Expires: " + ChatColor.GREEN + (ban.isPermanent() ? "Permanent" :
                DateUtil.formatDateDiff(ban.getExpires())));
    }

    public void announceBan(AddressBan ban) {
        sendRestrictedMessage(
                ChatColor.WHITE + "[" + ChatColor.RED + "STAFF" + ChatColor.WHITE + "] " + ChatColor.GREEN +
                        "IP " + ban.getAddress() + ChatColor.RED + " was banned by " + ChatColor.GREEN + verifySource(ban.getSource())
                        + ChatColor.RED + " Reason: " + ChatColor.GREEN + ban.getReason(),
                ChatColor.WHITE + "[" + ChatColor.RED + "STAFF" + ChatColor.WHITE + "] " + ChatColor.GREEN +
                        "IP X.X.X.X" + ChatColor.RED + " was banned by " + ChatColor.GREEN + verifySource(ban.getSource())
                        + ChatColor.RED + " Reason: " + ChatColor.GREEN + ban.getReason(),
                Rank.LEAD
        );
    }

    public void announceBan(ProviderBan ban) throws Exception {
        sendMessage(ChatColor.GREEN + "ISP " + ban.getProvider() + ChatColor.RED + " was banned by " + ChatColor.GREEN +
                verifySource(ban.getSource()));
    }

    public void announceUnban(AddressBan ban) {
        sendRestrictedMessage(
                ChatColor.WHITE + "[" + ChatColor.RED + "STAFF" + ChatColor.WHITE + "] " + ChatColor.GREEN +
                        "IP " + ban.getAddress() + ChatColor.RED + " has been unbanned by " + ChatColor.GREEN + verifySource(ban.getSource()),
                ChatColor.WHITE + "[" + ChatColor.RED + "STAFF" + ChatColor.WHITE + "] " + ChatColor.GREEN +
                        "IP X.X.X.X" + ChatColor.RED + " has been unbanned by " + ChatColor.GREEN + verifySource(ban.getSource()),
                Rank.LEAD
        );
    }

    public void announceUnban(String name, String source) throws Exception {
        sendMessage(ChatColor.GREEN + name + ChatColor.RED + " has been unbanned by " + ChatColor.GREEN + source);
    }

    public void announceKick(String name, String reason, String source) throws Exception {
        sendMessage(ChatColor.GREEN + name + ChatColor.RED + " was kicked by " + ChatColor.GREEN + source +
                ChatColor.RED + " Reason: " + ChatColor.GREEN + reason);
    }

    public void announceMute(Mute mute, String name) throws Exception {
        sendMessage(ChatColor.GREEN + name + ChatColor.RED + " was muted by " + ChatColor.GREEN +
                verifySource(mute.getSource()) + ChatColor.RED + " Reason: " + ChatColor.GREEN + mute.getReason() + ChatColor.RED +
                " Expires: " + ChatColor.GREEN + DateUtil.formatDateDiff(mute.getExpires()));
    }

    public void announceUnmute(String name, String source) throws Exception {
        sendMessage(ChatColor.GREEN + name + ChatColor.RED + " has been unmuted by " + ChatColor.RED + source);
    }

    public void announceWarning(String name, String reason, String source) throws Exception {
        sendMessage(ChatColor.GREEN + name + ChatColor.RED + " was issued a warning by " + ChatColor.GREEN + source +
                ChatColor.RED + " Reason: " + ChatColor.GREEN + reason);
    }

    public void sendMessage(String message) throws Exception {
        PalaceBungee.getMessageHandler().sendStaffMessage(message);
        PalaceBungee.getProxyServer().getLogger().info(ChatColor.WHITE + "[" + ChatColor.RED + "STAFF" + ChatColor.WHITE + "] " + message);
    }

    public void sendRestrictedMessage(String fullMessage, String blockedMessage, Rank cutoffRank) {
        for (Player player : PalaceBungee.getOnlinePlayers()) {
            int id = player.getRank().getRankId();
            if (id < Rank.TRAINEE.getRankId()) continue;
            if (id < cutoffRank.getRankId()) {
                player.sendMessage(blockedMessage);
            } else {
                player.sendMessage(fullMessage);
            }
        }
        PalaceBungee.getProxyServer().getLogger().info(fullMessage);
    }

//    public void displayServerMute(String name, boolean muted) throws Exception {
//        ChatColor prefix = muted ? ChatColor.RED : ChatColor.GREEN;
//        String message = name + " will " + (muted ? "not" : "now") + " announce connects and disconnects to/from Dashboard.";
//        sendMessage(prefix + message);
////        SlackMessage slackMessage = new SlackMessage("");
////        SlackAttachment attachment = new SlackAttachment(message);
////        attachment.color(muted ? "danger" : "good");
////        Launcher.getDashboard().getSlackUtil().sendDashboardMessage(slackMessage, Collections.singletonList(attachment));
//    }

    public static String verifySource(String source) {
        source = source.trim();
        if (source.length() == 36) {
            try {
                UUID sourceUUID = UUID.fromString(source);
                source = PalaceBungee.getUsername(sourceUUID);
            } catch (Exception ignored) {
            }
        }
        return source;
    }

    public void announceSpamWhitelistAdd(SpamIPWhitelist whitelist) {
        sendRestrictedMessage(
                ChatColor.GREEN + whitelist.getAddress() + " is now whitelisted from Spam IP protection with a limit of " +
                        ChatColor.YELLOW + whitelist.getLimit() + ChatColor.GREEN + " accounts",
                ChatColor.GREEN + "X.X.X.X is now whitelisted from Spam IP protection with a limit of " +
                        ChatColor.YELLOW + whitelist.getLimit() + ChatColor.GREEN + " accounts",
                Rank.LEAD
        );
    }

    public void announceSpamWhitelistRemove(String ip) {
        sendRestrictedMessage(
                ChatColor.GREEN + ip + " is no longer whitelisted from Spam IP protection",
                ChatColor.GREEN + "X.X.X.X is no longer whitelisted from Spam IP protection",
                Rank.LEAD
        );
    }

    public void announceSpamIPConnect(int limit, String address) {
        sendRestrictedMessage(
                ChatColor.RED + "IP " + ChatColor.GREEN + address + ChatColor.RED +
                        " reached its maximum allowed player count of " + ChatColor.GREEN + limit + ChatColor.RED + " players",
                ChatColor.RED + "IP " + ChatColor.GREEN + "X.X.X.X" + ChatColor.RED +
                        " reached its maximum allowed player count of " + ChatColor.GREEN + limit + ChatColor.RED + " players",
                Rank.LEAD
        );
    }

    public void announceSpamMessage(String username, String message) throws Exception {
        sendMessage(ChatColor.GREEN + username + "'s " + ChatColor.RED + "message " + ChatColor.AQUA + message + ChatColor.GREEN + " was marked as potential spam.");
    }
}