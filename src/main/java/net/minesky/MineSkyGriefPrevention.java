package net.minesky;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.minesky.command.*;
import net.minesky.events.ClaimEvents;
import net.minesky.hooks.PlaceholderAPIHook;
import net.minesky.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class MineSkyGriefPrevention extends JavaPlugin {
    public static DataStore dataStore;
    private final Set<UUID> PlayerTitle = new HashSet<>();

    private Claim getClaimAtPlayer(Player p) {
        return dataStore.getClaimAt(p.getLocation(), false, (dataStore.getPlayerData(p.getUniqueId())).lastClaim);
    }

    public void onEnable() {
        getCommand("terreno").setExecutor(new TerrenoCommand());
        getCommand("confiar").setExecutor(new ConfiarCommand());
        getCommand("desconfiar").setExecutor(new DesconfiarCommand());
        getCommand("gerente").setExecutor(new GerenteCommand());
        getCommand("removergerente").setExecutor(new RemoverGerenteCommand());
        getCommand("abandonar").setExecutor(new AbandonarCommand());
        getCommand("abandonartodos").setExecutor(new AbandonarTodosCommand());
        getCommand("lista").setExecutor(new ListaCommand());
        getServer().getPluginManager().registerEvents(new ClaimEvents(), this);
        getServer().getGlobalRegionScheduler().runAtFixedRate(this, task -> {
            onCheckFly();
        }, 1L, 20L);
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPIHook().register();
        }
        dataStore = GriefPrevention.instance.dataStore;
    }

    public void onCheckFly() {
        for (Player b : Bukkit.getOnlinePlayers()) {
            Claim c = getClaimAtPlayer(b);
            if (c == null) {
                if (PlayerTitle.contains(b.getUniqueId())) {
                    PlayerTitle.remove(b.getUniqueId());
                    b.sendTitle(Utils.c("&c⚠ Zona Desprotegida"), Utils.c("&7Você saiu de um terreno protegido."), 10, 20, 10);
                }
                if (b.getAllowFlight() && b.hasPermission("mineskygf.fly") && !b.hasPermission("mineskygf.fly.bypass")) {
                    b.setAllowFlight(false);
                    b.sendMessage(Utils.c("&cSeu fly foi desativado pois você saiu do terreno."));
                }
                continue;
            }

            if (!PlayerTitle.contains(b.getUniqueId())) {
                PlayerTitle.add(b.getUniqueId());
                b.sendTitle(Utils.c("&a&l⛏ Zona Protegida"), Utils.c("&7Terreno de &a" + (c.getOwnerName() != null ? c.getOwnerName() : c.getOwnerID())), 10, 20, 10);
            }
            if (b.hasPermission("mineskygf.fly.bypass") || !b.getWorld().getName().equals("world")) continue;
            if (b.hasPermission("mineskygf.fly")) {
                if ((c.getOwnerID().equals(b.getUniqueId()) ||
                        c.hasExplicitPermission(b.getUniqueId(), ClaimPermission.Manage) ||
                        c.hasExplicitPermission(b.getUniqueId(), ClaimPermission.Build))) {
                    if (!b.getAllowFlight()) {
                        b.setAllowFlight(true);
                        b.sendMessage(Utils.c("&aFly ativado! Você tem permissão por estar no seu terreno ou ser gerente/construtor com VIP."));
                    }
                    continue;
                }
            }
        }
    }

    public static MineSkyGriefPrevention getInstance() {
        return MineSkyGriefPrevention.getPlugin(MineSkyGriefPrevention.class);
    }
}
