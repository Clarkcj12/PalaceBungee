package network.palace.bungee.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import network.palace.bungee.PalaceBungee;
import network.palace.bungee.handlers.Player;
import network.palace.bungee.handlers.ProtocolConstants;
import network.palace.bungee.handlers.Rank;
import network.palace.bungee.messages.packets.ProxyReloadPacket;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("MismatchedReadAndWriteOfArray")
@Getter
public class ConfigUtil {
    private Favicon favicon;
    private String motd, motdTemp;
    private ServerPing.PlayerInfo[] motdInfo;
    private String maintenanceMotd, maintenanceMotdTemp;
    private boolean maintenance;
    private int chatDelay;
    private boolean parkChatMuted;
    private boolean dmEnabled;
    private boolean strictChat;
    private double strictThreshold;

    public String getDashboardURL() {
        try {
            Configuration config = getConfig();
            return config.getString("dashboardURL");
        } catch (IOException e) {
            e.printStackTrace();
            return "null";
        }
    }

    public void reload() throws IOException {
        BungeeConfig config = getBungeeConfig();
        this.favicon = config.favicon;
        this.motdTemp = config.motd;
        this.motd = this.motdTemp.replaceAll("%n%", System.getProperty("line.separator"));
        this.motdInfo = new ServerPing.PlayerInfo[config.motdInfo.length];
        for (int i = 0; i < config.motdInfo.length; i++) {
            this.motdInfo[i] = new ServerPing.PlayerInfo(ChatColor.translateAlternateColorCodes('&', config.motdInfo[i]), "");
        }
        this.maintenanceMotdTemp = config.maintenanceMotd;
        this.maintenanceMotd = this.maintenanceMotdTemp.replaceAll("%n%", System.getProperty("line.separator"));
        this.maintenance = config.maintenance;
        this.chatDelay = config.chatDelay;
        this.parkChatMuted = config.parkChatMuted;
        this.dmEnabled = config.dmEnabled;
        this.strictChat = config.strictChat;
        this.strictThreshold = config.strictThreshold;

        ProtocolConstants.setHighVersion(config.maxVersion, config.maxVersionString);
        ProtocolConstants.setLowVersion(config.minVersion, config.minVersionString);

        if (this.maintenance) {
            for (Player tp : PalaceBungee.getOnlinePlayers()) {
                try {
                    if (tp.getRank().getRankId() < Rank.DEVELOPER.getRankId()) {
                        tp.kickPlayer(ChatColor.AQUA + "Palace Network has entered a period of maintenance!\nFollow " +
                                ChatColor.BLUE + "@PalaceDev " + ChatColor.AQUA + "on Twitter for updates.", false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public DatabaseConnection getRabbitMQInfo() {
        try {
            Configuration config = getConfig().getSection("rabbitmq");
            return new DatabaseConnection(config.getString("host"), config.getString("username"), config.getString("password"), config.getString("virtualhost"), 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new DatabaseConnection("", "", "", "", 0);
        }
    }

    public DatabaseConnection getMongoDBInfo() {
        try {
            Configuration config = getConfig().getSection("mongodb");
            return new DatabaseConnection(config.getString("hostname"), config.getString("username"), config.getString("password"), null, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new DatabaseConnection("", "", "", "", 0);
        }
    }

    public BungeeConfig getBungeeConfig() {
        try {
            return PalaceBungee.getMongoHandler().getBungeeConfig();
        } catch (Exception e) {
            e.printStackTrace();
            return new BungeeConfig(null, "", new String[0], "", false, 2, true, false, false, 0.8, 0, 0, "", "");
        }
    }

    public Configuration getConfig() throws IOException {
        return ConfigurationProvider.getProvider(YamlConfiguration.class).load(getConfigFile());
    }

    public File getConfigFile() throws IOException {
        File folder = new File("plugins/PalaceBungee");
        if (!folder.exists()) folder.mkdir();

        File file = new File(folder, "config.yml");
        if (!file.exists()) {
            file.createNewFile();
        }

        return file;
    }

    public void setChatDelay(int seconds) throws Exception {
        this.chatDelay = seconds;
        saveConfigChanges();
    }

    public void setMaintenanceMode(boolean maintenance) throws Exception {
        this.maintenance = maintenance;
        saveConfigChanges();
    }

    private void saveConfigChanges() throws Exception {
        BungeeConfig config = new BungeeConfig(null, null, null, null,
                maintenance, chatDelay, parkChatMuted, dmEnabled, strictChat, strictThreshold,
                0, 0, null, null);
        PalaceBungee.getMongoHandler().setBungeeConfig(config);
        PalaceBungee.getMessageHandler().sendMessage(new ProxyReloadPacket(), PalaceBungee.getMessageHandler().ALL_PROXIES);
    }

    @Getter
    @AllArgsConstructor
    public static class DatabaseConnection {
        private final String host, username, password, database;
        private final int port;
    }

    @Getter
    @AllArgsConstructor
    public static class BungeeConfig {
        private final Favicon favicon;
        private final String motd;
        private final String[] motdInfo;
        private final String maintenanceMotd;
        private final boolean maintenance;
        private final int chatDelay;
        private final boolean parkChatMuted;
        private final boolean dmEnabled;
        private final boolean strictChat;
        private final double strictThreshold;
        private final int maxVersion;
        private final int minVersion;
        private final String maxVersionString;
        private final String minVersionString;
    }
}
