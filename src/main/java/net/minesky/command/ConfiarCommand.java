package net.minesky.command;

import me.ryanhamshire.GriefPrevention.*;
import net.minesky.MineSkyGriefPrevention;
import net.minesky.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.List;

public class ConfiarCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (!(s instanceof Player player)) return true;

        if (args.length != 1) {
            player.sendMessage(Utils.c("&cUso correto: /confiar <jogador>"));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        PlayerData playerData = MineSkyGriefPrevention.dataStore.getPlayerData(player.getUniqueId());
        Claim claim = MineSkyGriefPrevention.dataStore.getClaimAt(player.getLocation(), true, null);

        if (claim == null) {
            int count = 0;
            for (Claim c : playerData.getClaims()) {
                c.setPermission(target.getUniqueId().toString(), ClaimPermission.Build);
                count++;
            }
            player.sendMessage(Utils.c(count > 0
                    ? "&a" + target.getName() + " agora pode construir em todos os seus terrenos."
                    : "&cVocê não tem terrenos."));
            return true;
        }

        if (!Utils.isAdminClaim(claim, player) && !playerData.ignoreClaims) {
            player.sendMessage(Utils.c("&cVocê não tem permissão para setar confiança nesse terreno."));
            return true;
        }

        claim.setPermission(target.getUniqueId().toString(), ClaimPermission.Build);
        player.sendMessage(Utils.c("&a" + target.getName() + " agora pode construir neste terreno."));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command cmd, String label, String[] args) {
        return Utils.onlineWithoutVanished();
    }
}
