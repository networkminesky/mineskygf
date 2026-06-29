package net.minesky.utils;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import net.md_5.bungee.api.ChatColor;
import net.minesky.hooks.SuperVanishHook;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.generator.structure.StructureType;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static String c(String s) {
        return hex(s);
    }

    private static String createHex(String hexString) {
        hexString = hexString.replace("&", "");
        return net.md_5.bungee.api.ChatColor.of(hexString).toString();
    }

    public static String hex(String message) {
        Pattern hexPattern = Pattern.compile("&#[A-Fa-f0-9]{6}");
        Matcher matcher = hexPattern.matcher(message);

        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            matcher.appendReplacement(result, createHex(matcher.group()));
        }

        matcher.appendTail(result);
        message = result.toString();

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static boolean isAdminClaim(Claim c, Player player) {
        return c.getOwnerID().equals(player.getUniqueId()) || c.hasExplicitPermission(player.getUniqueId(), ClaimPermission.Manage);
    }

    public static boolean isOwnerClaim(Claim c, Player player) {
        return c.getOwnerID().equals(player.getUniqueId());
    }

    public static List<String> onlineWithoutVanished() {
        return Bukkit.getOnlinePlayers().stream()
                .filter(b -> !SuperVanishHook.isPlayerVanished(b))
                .map(Player::getName)
                .toList();
    }

    public static boolean isNearStronghold(Location location) {
        World world = location.getWorld();
        if (world == null || !world.getName().equals("world")) return false;

        Location strongholdLoc = world.locateNearestStructure(location, StructureType.STRONGHOLD, 50, false).getLocation();
        return strongholdLoc != null && strongholdLoc.distance(location) <= 50;
    }
}
