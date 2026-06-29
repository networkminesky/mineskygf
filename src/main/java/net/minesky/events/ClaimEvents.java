package net.minesky.events;

import me.ryanhamshire.GriefPrevention.events.ClaimCreatedEvent;
import me.ryanhamshire.GriefPrevention.events.ClaimDeletedEvent;
import net.minesky.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static net.minesky.utils.Utils.isNearStronghold;

public class ClaimEvents implements Listener {
    @EventHandler
    public void onClaimCreate(ClaimCreatedEvent event) {
        Player player = (Player) event.getCreator();
        Location claimCenter = event.getClaim().getLesserBoundaryCorner().clone()
                .add(event.getClaim().getGreaterBoundaryCorner())
                .multiply(0.5);

        if (isNearStronghold(claimCenter)) {
            event.setCancelled(true);
            player.sendMessage(Utils.c("&cVocê não pode fazer claim perto de uma stronghold!"));
            return;
        }
        player.sendTitle("", Utils.c("&a\uD83E\uDE93 Seu terreno foi criado!"));
    }

    @EventHandler
    public void onClaimDelete(ClaimDeletedEvent event) {
        Player player = Bukkit.getPlayer(event.getClaim().getOwnerID());
        assert player != null;
        player.sendTitle("", Utils.c("&c\uD83E\uDE93 Seu terreno foi deletado!"));
    }
}
