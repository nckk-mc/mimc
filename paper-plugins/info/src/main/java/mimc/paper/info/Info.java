package mimc.paper.info;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Info extends JavaPlugin
{
    public final static String CHANNEL_NAME = "mimc:channel";
    public static Plugin id;

    @Override
    public void onEnable() {

        getServer().getMessenger().registerOutgoingPluginChannel(this, CHANNEL_NAME);

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
}
