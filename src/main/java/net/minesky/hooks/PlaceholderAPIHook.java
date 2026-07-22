package net.minesky.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.minesky.MineSkyGriefPrevention;
import net.minesky.utils.Utils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIHook extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "mineskygriefprevention";
    }

    @Override
    public @NotNull String getAuthor() {
        return "zBrunoC (Bruno C.)";
    }

    @Override
    public @NotNull String getVersion() {
        return MineSkyGriefPrevention.getInstance().getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) return "";

        switch (identifier) {
            case "tag": {
                Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), true, null);
                if (claim == null) {
                    return "...";
                }

                if (claim.ownerID.equals(player.getUniqueId())) {
                    return Utils.c("&f⌓");
                }

                if (claim.hasExplicitPermission(player, ClaimPermission.Build)) {
                    return Utils.c("&f⌕");
                }

                return Utils.c("&f⌮");
            }
            default: return "";
        }
    }
}
