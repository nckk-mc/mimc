package mimc.paper.info;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Info extends JavaPlugin implements Listener {
    public final static String CHANNEL_NAME = "mimc:channel";
    public static Plugin id;

    @Override
    public void onEnable() {

        getServer().getMessenger().registerOutgoingPluginChannel(this, CHANNEL_NAME);
		getServer().getPluginManager().registerEvents(this, this);

        id = this;

        getLogger().warning("MIMC | Starting Info");

        BukkitRunnable runnable = (new BukkitRunnable() {
            @Override
            public void run() {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();

                out.writeUTF("tps-report");
                out.writeDouble(Bukkit.getServer().getTPS()[0]);

                getLogger().warning("Sending TPS");

                Bukkit.getServer().sendPluginMessage(Info.id, CHANNEL_NAME, out.toByteArray());
            }
        });

        runnable.runTaskTimerAsynchronously(this, 0, 5 * 20);
    }
	
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        e.setJoinMessage(null);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        e.setQuitMessage(null);
    }
}
