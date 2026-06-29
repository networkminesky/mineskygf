package net.minesky.command;

import me.ryanhamshire.GriefPrevention.*;
import net.minesky.MineSkyGriefPrevention;
import net.minesky.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.List;

public class RemoverGerenteCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (!(s instanceof Player player)) return true;

        if (args.length != 1) {
            player.sendMessage(Utils.c("&cUso correto: /removergerente <jogador>"));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        PlayerData playerData = MineSkyGriefPrevention.dataStore.getPlayerData(player.getUniqueId());
        Claim claim = MineSkyGriefPrevention.dataStore.getClaimAt(player.getLocation(), true, null);

        if (claim == null) {
            int count = 0;
            for (Claim c : playerData.getClaims()) {
                c.dropPermission(target.getUniqueId().toString());
                count++;
            }
            player.sendMessage(Utils.c(count > 0
                    ? "&a" + target.getName() + " não é mais gerente de nenhum dos seus terrenos."
                    : "&cVocê não possui terrenos."));
            return true;
        }

        if (!Utils.isOwnerClaim(claim, player) && !playerData.ignoreClaims) {
            player.sendMessage(Utils.c("&cVocê não tem permissão para remover gerência deste terreno."));
            return true;
        }

        claim.dropPermission(target.getUniqueId().toString());
        player.sendMessage(Utils.c("&a" + target.getName() + " não é mais gerente deste terreno."));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command cmd, String label, String[] args) {
        return Utils.onlineWithoutVanished();
    }
}
