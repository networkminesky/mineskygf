package net.minesky.command;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.PlayerData;
import net.minesky.MineSkyGriefPrevention;
import net.minesky.utils.Utils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;

public class AbandonarTodosCommand implements CommandExecutor, TabCompleter {

    private final Map<UUID, Long> confirmacoes = new HashMap<>();
    private static final long TIMEOUT = 15_000; // 15 segundos

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (!(s instanceof Player player)) return true;

        UUID id = player.getUniqueId();
        PlayerData data = MineSkyGriefPrevention.dataStore.getPlayerData(id);
        List<Claim> claims = new ArrayList<>(data.getClaims());

        if (claims.isEmpty()) {
            player.sendMessage(Utils.c("&cVocê não possui terrenos."));
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("confirmar")) {
            if (!confirmacoes.containsKey(id)) {
                player.sendMessage(Utils.c("&7Use &b/abandonartodos &7antes."));
                return true;
            }

            if (System.currentTimeMillis() - confirmacoes.get(id) > TIMEOUT) {
                confirmacoes.remove(id);
                player.sendMessage(Utils.c("&7Tempo expirado. Use &b/abandonartodos &7novamente."));
                return true;
            }

            confirmacoes.remove(id);

            for (Claim claim : claims) {
                MineSkyGriefPrevention.dataStore.deleteClaim(claim, true);
            }

            data.getClaims().clear();

            player.sendMessage(Utils.c("&aTodos os seus terrenos foram abandonados."));
            return true;
        }

        confirmacoes.put(id, System.currentTimeMillis());
        player.sendMessage(Utils.c("&7Tem certeza? Use &b/abandonartodos confirmar &7em até 15 segundos."));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command cmd, String label, String[] args) {
        return (args.length == 1 && "confirmar".startsWith(args[0].toLowerCase()))
                ? Collections.singletonList("confirmar")
                : Collections.emptyList();
    }
}
