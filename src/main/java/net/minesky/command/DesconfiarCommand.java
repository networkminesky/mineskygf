package net.minesky.command;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import net.minesky.MineSkyGriefPrevention;
import net.minesky.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.List;

public class DesconfiarCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (!(s instanceof Player player)) return true;

        if (args.length != 1) {
            player.sendMessage(Utils.c("&cUso correto: /desconfiar <jogador>"));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        PlayerData playerData = MineSkyGriefPrevention.dataStore.getPlayerData(player.getUniqueId());
        Claim claim = MineSkyGriefPrevention.dataStore.getClaimAt(player.getLocation(), true, null);

        if (claim == null) {
            int removed = 0;
            for (Claim c : playerData.getClaims()) {
                 c.dropPermission(target.getUniqueId().toString());
                 removed++;
            }
            player.sendMessage(Utils.c(removed > 0
                    ? "&c" + target.getName() + " foi removido de " + removed + " terreno(s)."
                    : "&cVocê não tem terrenos."));
            return true;
        }

        if (!Utils.isAdminClaim(claim, player) && !playerData.ignoreClaims) {
            player.sendMessage(Utils.c("&cVocê não tem permissão para remover confiança nesse terreno."));
            return true;
        }

        claim.dropPermission(target.getUniqueId().toString());
        player.sendMessage(Utils.c("&cAcesso de " + target.getName() + " a este terreno foi revogado. "
                + "Para definir permissões para todos os seus terrenos, fique fora deles."));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command cmd, String label, String[] args) {
        return Utils.onlineWithoutVanished();
    }
}
