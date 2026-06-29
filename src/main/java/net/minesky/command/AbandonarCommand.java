package net.minesky.command;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.PlayerData;
import net.minesky.MineSkyGriefPrevention;
import net.minesky.utils.Utils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;

public class AbandonarCommand implements CommandExecutor, TabCompleter {

    private final Map<UUID, Long> confirmacoes = new HashMap<>();
    private static final long TIMEOUT = 15_000; // 15 segundos

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (!(s instanceof Player player)) return true;

        UUID id = player.getUniqueId();
        Claim claim = MineSkyGriefPrevention.dataStore.getClaimAt(player.getLocation(), true, null);
        PlayerData data = MineSkyGriefPrevention.dataStore.getPlayerData(id);

        if (claim == null || !Utils.isOwnerClaim(claim, player) && !data.ignoreClaims) {
            player.sendMessage(Utils.c("&cVocê não pode abandonar esse terreno."));
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("confirmar")) {
            if (!confirmacoes.containsKey(id)) {
                player.sendMessage(Utils.c("&7Use &b/abandonar &7antes."));
                return true;
            }

            if (System.currentTimeMillis() - confirmacoes.get(id) > TIMEOUT) {
                confirmacoes.remove(id);
                player.sendMessage(Utils.c("&7Tempo expirado. Use &b/abandonar &7novamente."));
                return true;
            }

            confirmacoes.remove(id);
            MineSkyGriefPrevention.dataStore.deleteClaim(claim, true);
            player.sendMessage(Utils.c("&aTerreno abandonado com sucesso."));
            return true;
        }

        confirmacoes.put(id, System.currentTimeMillis());
        player.sendMessage(Utils.c("&7Tem certeza? Use &b/abandonar confirmar &7em até 15 segundos."));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command cmd, String label, String[] args) {
        return (args.length == 1 && "confirmar".startsWith(args[0].toLowerCase()))
                ? Collections.singletonList("confirmar")
                : Collections.emptyList();
    }
}
