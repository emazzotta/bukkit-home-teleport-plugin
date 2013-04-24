package org.crumbleworks.mcdonnough.minecraft.bukkit.plugin.home;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BukHomeCommandExecutor implements CommandExecutor {

    public static final int NEEDED_INVITE_COMMAND_PARAMETERS = 3;
    private BukHome bukHome;
    private BukHomeDatabase bukHomeDatabase;

	public BukHomeCommandExecutor(BukHome bukHome) {
		this.bukHome = bukHome;
        this.bukHomeDatabase = BukHomeDatabase.getInstance(bukHome);
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
                        player.sendMessage(Constants.INVALID_PARAMETER);
                }
            } else {
                executeHelpCommand(player);
            }
			
			return true;
		} else {
			sender.sendMessage(Constants.MUST_BE_INVOKED_BY_A_PLAYER);
			return false;
		}
	}

    private void executeHelpCommand(Player player) {
        player.sendMessage("help about le home");
    }

    private void executeSetCommand(Player player, String[] args) {
		String homeName = getHomeName(args, 1);

        performSetNewHomeInsert(player, homeName);
        bukHome.getLogger().info("set home named '" + homeName + "' for player '" + player.getName() + "'");
        player.sendMessage("set home named '" + homeName + "'");
	}

    private void performSetNewHomeInsert(Player player, String homeName) {
        bukHomeDatabase.performQuery("INSERT OR REPLACE INTO home(playername, homename, x, y, z) VALUES('" + player.getName() + "', '" + homeName + "', "+ player.getLocation().getX() +", "+ player.getLocation().getY() +", "+ player.getLocation().getZ() +");");
    }

    private void executeGotoCommand(Player player, String[] args) {
        String homeName = getHomeName(args, 1);

        float x = 0f;
        float y = 0f;
        float z = 0f;

        try {
            ResultSet resultSet = bukHomeDatabase.performQuery("SELECT x, y, z FROM home WHERE playername = '" + player.getName() + "' AND homename = '" + homeName + "'");
			bukHome.getLogger().info("Selected home named '" + homeName + "' for player '" + player.getName() + "'");
			
			while(resultSet.next()) {
				x = resultSet.getFloat("x");
				y = resultSet.getFloat("y");
				z = resultSet.getFloat("z");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        player.teleport(new Location(player.getWorld(), x, y, z));
		bukHome.getLogger().info("Ported player '" + player.getName() + "' to home named '" + homeName + "'");
		player.sendMessage("Ported to home named '" + homeName + "'");
	}

    // TODO
	private void executeInviteCommand(Player player, String[] args) {
		String homeName = getHomeName(args, 2);
		String inviteeName = null;

		if(args.length < NEEDED_INVITE_COMMAND_PARAMETERS) {
			player.sendMessage("Usage: /home inivte <player>");
			return;
		} else {
			inviteeName = args[1];
		}
		
        bukHomeDatabase.performQuery("INSERT OR REPLACE INTO invitee(playername) VALUES('" + player.getName() + "');");
        bukHome.getLogger().info("added invitee named '" + player.getName() + "'");

        bukHomeDatabase.performQuery("INSERT INTO home_invitee(playername) VALUES('" + player.getName() + "');");
        bukHome.getLogger().info("added invitee named '" + player.getName() + "'");
	}
	
	private String getHomeName(String[] args, int position) {
		if(args.length < (position + 1)) {
			return "home";
		} else {
			return args[position];
		}
	}
}
