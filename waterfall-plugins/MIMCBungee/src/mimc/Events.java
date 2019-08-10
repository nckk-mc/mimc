package mimc;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class Events implements Listener {

    @EventHandler
    public void onChat(ChatEvent e) {
        ProxiedPlayer p = (ProxiedPlayer) e.getSender();

        p.getServer();

        if (!e.isCommand()) {
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                if (!player.getServer().getInfo().getName().equals(p.getServer().getInfo().getName())) {
                    player.sendMessage(new TextComponent("<" + p.getName() + "> " + e.getMessage()));
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PostLoginEvent e) {
        ProxiedPlayer p = e.getPlayer();

        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            player.sendMessage(ChatColor.YELLOW + p.getName() + " joined the game");
        }
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent e) {
        ProxiedPlayer p = e.getPlayer();

        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            player.sendMessage(ChatColor.YELLOW + p.getName() + " left the game");
        }
    }

}
