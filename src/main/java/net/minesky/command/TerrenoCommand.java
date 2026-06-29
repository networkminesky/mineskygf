package net.minesky.command;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.PlayerData;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minesky.MineSkyGriefPrevention;
import net.minesky.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Vector;

public class TerrenoCommand implements CommandExecutor, TabCompleter {

    public static Vector<Claim> getPlayerClaimList(Player player) {
        return MineSkyGriefPrevention.dataStore.getPlayerData(player.getUniqueId()).getClaims();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cApenas jogadores podem usar este comando.");
            return true;
        }

        if (args.length == 0) {
            displayClaims(player, player);
            return true;
        }

        if (args[0].equalsIgnoreCase("tp")) {
            return handlerTeleport(player, args);
        }

        if (!player.hasPermission("mineskygf.others")) {
            player.sendMessage("§cVocê não tem permissão para ver os terrenos de outros jogadores.");
            return true;
        }

        Player target = player.getServer().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage("§cJogador não encontrado ou offline.");
            return true;
        }

        displayClaims(player, target);
        return true;
    }

    private void displayClaims(Player visualizador, Player dono) {
        Vector<Claim> claims = getPlayerClaimList(dono);

        if (claims.isEmpty()) {
            visualizador.sendMessage("§c" + (visualizador.equals(dono) ? "Você não possui terrenos." : "Esse jogador não possui terrenos."));
            return;
        }

        PlayerData data = MineSkyGriefPrevention.dataStore.getPlayerData(dono.getUniqueId());
        int total = data.getAccruedClaimBlocks() + data.getBonusClaimBlocks();
        int usados = total - data.getRemainingClaimBlocks();
        int disponiveis = data.getAccruedClaimBlocks() + data.getBonusClaimBlocks() - usados;
        visualizador.sendMessage((visualizador.equals(dono) ? "§eSeus terrenos:" : "§eTerrenos de §6" + dono.getName() + "§e:"));
        visualizador.sendMessage(Utils.c("&eBlocos: &6" + disponiveis + " &edisponíveis &7| &c" + usados + " &eusados &7| &b" + total + " &etotal"));
        for (int i = 0; i < claims.size(); i++) {
            Location loc = claims.get(i).getGreaterBoundaryCorner();
            visualizador.spigot().sendMessage(createOption(
                    "&e- Terreno &6" + (i + 1) + "&e | Coordenadas: &6x" + loc.getX() + " z" + loc.getZ() + " &7[TP]",
                    "&eClique para teleportar-se\naté o terreno " + (i + 1) + (visualizador.equals(dono) ? "" : " de " + dono.getName()),
                    "/terreno tp " + (i + 1) + (visualizador.equals(dono) ? "" : " " + dono.getName())
            ));
        }
    }

    private boolean handlerTeleport(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUso correto: /terreno tp <número> [jogador]");
            return true;
        }

        int index;
        try {
            index = Integer.parseInt(args[1]) - 1;
        } catch (NumberFormatException e) {
            player.sendMessage("§cNúmero inválido do terreno.");
            return true;
        }

        Player alvo = player;
        if (args.length >= 3) {
            if (!player.hasPermission("mineskygf.others")) {
                player.sendMessage("§cVocê não tem permissão para ver os terrenos de outros jogadores.");
                return true;
            }

            alvo = player.getServer().getPlayer(args[2]);
            if (alvo == null) {
                player.sendMessage("§cJogador não encontrado ou offline.");
                return true;
            }
        }

        Vector<Claim> claims = getPlayerClaimList(alvo);
        if (index < 0 || index >= claims.size()) {
            player.sendMessage("§cEsse terreno não existe.");
            return true;
        }

        Location loc = claims.get(index).getGreaterBoundaryCorner();
        Location destino = loc.getWorld().getHighestBlockAt(loc).getRelative(BlockFace.UP).getLocation();
        player.teleport(destino);

        player.sendMessage("§aTeleportado até o terreno " + (index + 1) +
                (alvo.equals(player) ? "!" : " de §e" + alvo.getName() + "§a!"));
        return true;
    }

    private TextComponent createOption(String text, String hover, String command) {
        TextComponent component = new TextComponent(Utils.c(text));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(Utils.c(hover)).create()));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        return component;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return null;
    }
}
