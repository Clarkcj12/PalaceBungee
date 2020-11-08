package network.palace.bungee.dashboard.packets.dashboard;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import network.palace.bungee.dashboard.packets.BasePacket;
import network.palace.bungee.dashboard.packets.PacketID;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 9/3/16
 */
public class PacketTabComplete extends BasePacket {
    private UUID uuid;
    private int transactionId;
    private String command;
    private List<String> args;
    private List<String> results = new ArrayList<>();

    public PacketTabComplete() {
        this(null, 0, "", new ArrayList<>(), new ArrayList<>());
    }

    public PacketTabComplete(UUID uuid, int transactionId, String command, List<String> args, List<String> results) {
        this.id = PacketID.Dashboard.TABCOMPLETE.getID();
        this.uuid = uuid;
        this.transactionId = transactionId;
        this.command = command;
        this.args = args;
        this.results = results;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public String getCommand() {
        return command;
    }

    public List<String> getArgs() {
        return args;
    }

    public List<String> getResults() {
        return results;
    }

    public PacketTabComplete fromJSON(JsonObject obj) {
        try {
            this.uuid = UUID.fromString(obj.get("uuid").getAsString());
        } catch (Exception e) {
            this.uuid = null;
        }
        this.transactionId = obj.get("transactionId").getAsInt();
        this.command = obj.get("command").getAsString();
        JsonArray args = obj.get("args").getAsJsonArray();
        for (JsonElement e : args) {
            this.args.add(e.getAsString());
        }
        JsonArray list = obj.get("results").getAsJsonArray();
        for (JsonElement e : list) {
            this.results.add(e.getAsString());
        }
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("uuid", this.uuid.toString());
            obj.addProperty("transactionId", this.transactionId);
            obj.addProperty("command", this.command);
            Gson gson = new Gson();
            obj.add("args", gson.toJsonTree(this.args).getAsJsonArray());
            obj.add("results", gson.toJsonTree(this.results).getAsJsonArray());
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}