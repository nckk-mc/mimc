package mimc;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class MigratePlugin extends JavaPlugin implements PluginMessageListener {
    void checkIfBungee() {
        // we check if the server is Spigot/Paper (because of the spigot.yml file)
        if ( !getServer().getVersion().contains( "Spigot" ) || !getServer().getVersion().contains( "Paper" ) )
        {
            getLogger().severe( "You probably run CraftBukkit... Please update atleast to spigot for this to work..." );
            getLogger().severe( "Plugin disabled!" );
            getServer().getPluginManager().disablePlugin( this );
            return;
        }
        if ( getServer().spigot().getConfig().getBoolean( "settings.bungeecord" ) )
        {
            getLogger().severe( "This server is not BungeeCord." );
            getLogger().severe( "If the server is already hooked to BungeeCord, please enable it into your spigot.yml aswell." );
            getLogger().severe( "Plugin disabled!" );
            getServer().getPluginManager().disablePlugin( this );
        }
    }

    @Override
    public void onEnable() {
        checkIfBungee();
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "mimc", this);
        getLogger().warning("MIMC | MigratePlugin enabled successfully.");
    }

    @Override
    public void onDisable() {
        getLogger().warning("MIMC | MigratePlugin was disabled!");
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equalsIgnoreCase("mimc")) {
            return;
        }

        getLogger().warning(String.format( "MIMC | Got a message over the MIMC channel" ));

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();

        try {
            if (subchannel.equalsIgnoreCase("Migrate")) {
                short len = in.readShort();
                byte[] msgbytes = new byte[len];
                in.readFully(msgbytes);

                DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));

                String serverName = msgin.readUTF();
                String playerName = msgin.readUTF();

                getLogger().warning(String.format( "MIMC | Migration request received for player %s to node %s", playerName, serverName ));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
