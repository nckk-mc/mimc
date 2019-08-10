package mimc;

import net.md_5.bungee.api.plugin.Plugin;

public class MIMCBungee extends Plugin {

    @Override
    public void onEnable() {
        getLogger().info("MIMCBungee Plugin enabled");

        getProxy().getPluginManager().registerListener(this, new Events());
    }

}
