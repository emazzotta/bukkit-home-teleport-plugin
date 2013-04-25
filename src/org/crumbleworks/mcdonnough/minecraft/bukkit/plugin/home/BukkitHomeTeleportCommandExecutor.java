package org.crumbleworks.mcdonnough.minecraft.bukkit.plugin.home;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BukkitHomeTeleportCommandExecutor implements CommandExecutor {

    public static final int NEEDED_INVITE_COMMAND_PARAMETERS = 2;
    private BukkitHomeTeleport bukkitHomeTeleport;
    private BukkitHomeTeleportDatabase bukkitHomeTeleportDatabase;

	public BukkitHomeTeleportCommandExecutor(BukkitHomeTeleport bukkitHomeTeleport) {
		this.bukkitHomeTeleport = bukkitHomeTeleport;
        this.bukkitHomeTeleportDatabase = BukkitHomeTeleportDatabase.getInstance(bukkitHomeTeleport);
	}
	
	@Override
	public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;

            if(args.length >= 1) {
                switch(args[0]) {
                    case "help":
                        executeHelpCommand(player);
                        break;
                    case "set":
                        executeSetCommand(player, args);
                        break;
                    case "goto":
                        executeGotoCommand(player, args);
                        break;
                    case "invite":
                        executeInviteCommand(player, args);
                        break;
                    default:
                        player.sendMessage(ChatColor.RED + "Invalid parameter");
                }
            } else {
                executeHelpCommand(player);
            }
			
			return true;
		} else {
			sender.sendMessage("Must be invoked by a player");
			return false;
		}
	}

    private void executeHelpCommand(Player player) {
        player.sendMessage(ChatColor.YELLOW + "Usage: /home [help | set <homename> | goto <homename> | goto <player> | invite <player>]\n");
    }

    private void executeSetCommand(Player player, String[] args) {
		String homeName = getHomeName(args, 1);

        performSetNewHomeInsert(player, homeName);
        bukkitHomeTeleport.getLogger().info("set home named '" + homeName + "' for player '" + player.getName() + "'");
        player.sendMessage("set home named '" + homeName + "'");
	}

    private void performSetNewHomeInsert(Player player, String homeName) {
        bukkitHomeTeleportDatabase.performQuery("INSERT OR REPLACE INTO home(playername, homename, x, y, z) VALUES('" + player.getName() + "', '" + homeName + "', "+ player.getLocation().getX() +", "+ player.getLocation().getY() +", "+ player.getLocation().getZ() +");");
    }

    private void executeGotoCommand(Player player, String[] args) {
        String homeName = getHomeName(args, 1);

        float x = 0f;
        float y = 0f;
        float z = 0f;

        try {
            ResultSet resultSet = bukkitHomeTeleportDatabase.performQuery("SELECT x, y, z FROM home WHERE playername = '" + player.getName() + "' AND homename = '" + homeName + "'");
			bukkitHomeTeleport.getLogger().info("Selected home named '" + homeName + "' for player '" + player.getName() + "'");
			
			while(resultSet.next()) {
				x = resultSet.getFloat("x");
				y = resultSet.getFloat("y");
				z = resultSet.getFloat("z");
            }

            player.teleport(new Location(player.getWorld(), x, y, z));
            bukkitHomeTeleport.getLogger().info("Ported player '" + player.getName() + "' to home named '" + homeName + "'");
            player.sendMessage("Ported to home named '" + homeName + "'");
        } catch (SQLException e) {
            player.sendMessage(ChatColor.RED + "Error teleporting, home " + homeName + " might not exist");
        }
	}

    // TODO
	private void executeInviteCommand(Player player, String[] args) {
		if(args.length < NEEDED_INVITE_COMMAND_PARAMETERS) {
			player.sendMessage(ChatColor.YELLOW + "Usage: /home inivte <player>");
			return;
        }

        String inviteeName = args[1];

        bukkitHomeTeleportDatabase.performQuery("INSERT OR REPLACE INTO invitee(playername) VALUES('" + player.getName() + "');");
        bukkitHomeTeleport.getLogger().info("added invitee named '" + player.getName() + "'");

        bukkitHomeTeleportDatabase.performQuery("INSERT INTO home_invitee(playername) VALUES('" + player.getName() + "');");
        bukkitHomeTeleport.getLogger().info("added invitee named '" + player.getName() + "'");
	}
	
	private String getHomeName(String[] args, int position) {
		if(args.length < (position + 1)) {
			return "home";
		} else {
			return args[position];
		}
	}
}
