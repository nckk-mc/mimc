package mimc;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import mimc.event.player.PlayerNonLocalTeleportEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class MigratePlugin extends JavaPlugin implements PluginMessageListener, Listener {
    private final String CHANNEL_NAME = "mimc:channel";

    @Override
    public void onEnable() {
        this.getServer().getMessenger().registerIncomingPluginChannel(this, CHANNEL_NAME, this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, CHANNEL_NAME);
        this.getServer().getPluginManager().registerEvents(this, this);
        getLogger().warning("Plugin was enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().warning("Plugin was disabled!");
    }

    Set<String> playersQueuedForMigration = new CopyOnWriteArraySet<>();
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        String playerName = p.getName();
        playersQueuedForMigration.remove(playerName);
        playersQueuedForNonLocalTeleport.remove(playerName);
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        String playerName = p.getName();
        if (playersQueuedForMigration.contains(playerName)
            && !playersQueuedForNonLocalTeleport.contains(playerName)) {
            MimcServer server = (MimcServer)getServer();
            server.savePlayerData(p.getName());
        }
    }
    Set<String> playersQueuedForNonLocalTeleport = new CopyOnWriteArraySet<>();
    @EventHandler
    public void onPlayerNonLocalTeleport(PlayerNonLocalTeleportEvent e) {
        Player p = e.getPlayer();
        String playerName = p.getName();
        playersQueuedForNonLocalTeleport.add(playerName);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("non-local-teleport");
        out.writeUTF(playerName);
        out.writeInt(e.toDimension);
        out.writeDouble(e.getTo().getX());
        out.writeDouble(e.getTo().getY());
        out.writeDouble(e.getTo().getZ());
        out.writeFloat(e.getTo().getYaw());
        out.writeFloat(e.getTo().getPitch());

        getLogger().warning(String.format( "Non-local teleport request sent to bungee for player %s", playerName ));

        p.sendPluginMessage(this, CHANNEL_NAME, out.toByteArray());
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equalsIgnoreCase(CHANNEL_NAME)) {
            return;
        }

        getLogger().warning(String.format( "MIMC | Got a message over the MIMC channel" ));

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();

        if (subchannel.equalsIgnoreCase("prepare-migration")) {
            MimcServer server = (MimcServer)getServer();

            String serverName = in.readUTF();
            String playerName = in.readUTF();

            getLogger().warning(String.format( "Migration request received for player %s to node %s", playerName, serverName ));

            if (server.preparePlayerForMigration(playerName)) {
                this.playersQueuedForMigration.add(playerName);

                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("migration-prepared");
                out.writeUTF(serverName);
                out.writeUTF(playerName);

                getLogger().warning(String.format( "Migration preparation succeeded for player %s", playerName ));

                player.sendPluginMessage(this, CHANNEL_NAME, out.toByteArray());
            } else {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("migration-preparation-failed");
                out.writeUTF(serverName);
                out.writeUTF(playerName);

                getLogger().warning(String.format( "Migration preparation failed for player %s", playerName ));

                player.sendPluginMessage(this, CHANNEL_NAME, out.toByteArray());
            }
        } else if (subchannel.equalsIgnoreCase("confirm-teleport")) {
            int dimension = in.readInt();
            double x = in.readDouble();
            double y = in.readDouble();
            double z = in.readDouble();
            float yaw = in.readFloat();
            float pitch = in.readFloat();

            World world = getServer().getWorld(mapDimension(dimension));
            Location location = new Location(world, x, y, z, yaw, pitch);

            getLogger().warning(String.format( "Teleporting player '%s' to %s", player.getName(), location));

            player.teleport(location);

            MimcServer server = (MimcServer)getServer();
            server.setPlayerReadOnly(player.getName(), false);
        }
    }

    String mapDimension(int dimension) {
        switch (dimension) {
            case -1: return "world_nether";
            case 1: return "world_the_end";
        }
        return "world";
    }
}
