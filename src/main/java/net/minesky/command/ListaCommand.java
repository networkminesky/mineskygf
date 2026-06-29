package net.minesky.command;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.PlayerData;
import net.minesky.MineSkyGriefPrevention;
import net.minesky.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public class ListaCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (!(s instanceof Player player)) return true;

        Claim claim = MineSkyGriefPrevention.dataStore.getClaimAt(player.getLocation(), true, null);
        PlayerData data = MineSkyGriefPrevention.dataStore.getPlayerData(player.getUniqueId());

        if (claim == null || !Utils.isAdminClaim(claim, player) && !data.ignoreClaims) {
            player.sendMessage(Utils.c("&cVocê precisa estar em um terreno que você possui."));
            return true;
        }

        ArrayList<String> manage = new ArrayList<>();
        ArrayList<String> build = new ArrayList<>();
        ArrayList<String> containers = new ArrayList<>();
        ArrayList<String> access = new ArrayList<>();

        claim.getPermissions(build, containers, access, manage);

        if (build.isEmpty() && containers.isEmpty() && access.isEmpty() && manage.isEmpty()) {
            player.sendMessage(Utils.c("&cNinguém tem permissão neste terreno."));
            return true;
        }


        player.sendMessage(Utils.c("&6Permissões neste terreno:"));

        printPermList(player, "&7Construir:", build);
        printPermList(player, "&7Acessar baús:", containers);
        printPermList(player, "&7Acessar portas/botões:", access);
        printPermList(player, "&7Gerenciar terreno:", manage);

        return true;
    }

    private void printPermList(Player player, String label, List<String> ids) {
        if (ids.isEmpty()) return;

        List<String> nomes = new ArrayList<>();
        for (String id : ids) {
            try {
                UUID uuid = UUID.fromString(id);
                OfflinePlayer offline = Bukkit.getOfflinePlayer(uuid);
                nomes.add(offline.getName() != null ? offline.getName() : id);
            } catch (IllegalArgumentException e) {
                nomes.add(id);
            }
        }

        player.sendMessage(Utils.c(label + " &b" + String.join("&7, &b", nomes)));
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command cmd, String label, String[] args) {
        return null;
    }
}
