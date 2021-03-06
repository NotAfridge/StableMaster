package me.robotoraccoon.stablemaster.commands;

import me.robotoraccoon.stablemaster.LangString;
import me.robotoraccoon.stablemaster.StableMaster;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract superclass for every sub-command
 * @author RobotoRaccoon
 */
public abstract class SubCommand {

    /** The name of the command */
    private String name;
    /** The permission needed for the command */
    private String permission;

    /** Can console run the command */
    private boolean consoleAllowed = false;
    /** Minimum arguments needed */
    private int minArgs = 0;

    /**
     * Default constructor, must be called by the sub-class
     * @param name Name of the command
     */
    public SubCommand(String name) {
        setName(name);
    }

    /**
     * Run the sub-command specific code
     * @param commandInfo CommandInfo
     */
    public abstract void handle(CommandInfo commandInfo);

    /**
     * Run the command
     * @param info CommandInfo supplied
     */
    public final void execute(CommandInfo info) {
        CommandSender sender = info.getSender();

        // Sender is console and command does not allow console access.
        if (!(sender instanceof Player) && !isConsoleAllowed()) {
            new LangString("error.no-console").send(sender);
            return;
        }

        // Player does not have permission to use the command.
        if (!sender.hasPermission(getPermission())) {
            new LangString("error.no-permission").send(sender);
            return;
        }

        // Not enough arguments have been supplied.
        if (info.getArgs().length < getMinArgs()) {
            new LangString("error.arguments").send(sender);
            new LangString().append("/" + info.getLabel() + " " + getUsage()).send(sender);
            return;
        }

        // Run the command.
        StableMaster.getPlugin().getServer().getScheduler().runTaskAsynchronously(
                StableMaster.getPlugin(),
                () -> handle(info)
        );
    }

    /**
     * Can the player bypass and act as the owner for this command
     * @param player Player to test
     * @return True if the player has bypass permission
     */
    public final boolean canBypass(CommandSender player) {
        return player.hasPermission("stablemaster.bypass.command." + getName());
    }

    /**
     * What this command does
     * @return Description string
     */
    public final String getDescription() {
        return new LangString("command." + getName() + ".description").getMessage();
    }

    /**
     * How to use this command
     * @return Help string
     */
    public final String getUsage() {
        return new LangString("command." + getName() + ".usage").getMessage();
    }

    /**
     * get if this command be run from console
     * @return consoleAllowed Console can use this command
     */
    public final boolean isConsoleAllowed() {
        return consoleAllowed;
    }

    /**
     * Set if this command be run from console
     * @param consoleAllowed Console can use this command
     */
    protected final void setConsoleAllowed(boolean consoleAllowed) {
        this.consoleAllowed = consoleAllowed;
    }

    /**
     * Get the minimum arguments needed to run the command
     * @return minArgs Minimum arguments
     */
    public final int getMinArgs() {
        return minArgs;
    }

    /**
     * Set the minimum arguments needed to run the command
     * @param minArgs Minimum arguments
     */
    protected final void setMinArgs(int minArgs) {
        this.minArgs = minArgs;
    }

    /**
     * Get the base name for the command
     * @return Base name
     */
    public final String getName() {
        return name;
    }

    /**
     * Set the name for this command
     * @param name Base name
     */
    private final void setName(String name) {
        this.name = name;
        setPermission("stablemaster." + name);
    }

    /**
     * Get the permission needed to run the command
     * @return Permission string
     */
    public final String getPermission() {
        return permission;
    }

    /**
     * Set the permission
     * @param permission Permission string
     */
    protected final void setPermission(String permission) {
        this.permission = permission;
    }

    /**
     * Get all the aliases of this command as defined in the config
     * @return Aliases for this command
     */
    public final List<String> getAliases() {
        ConfigurationSection config = StableMaster.getPlugin().getConfig().getConfigurationSection("aliases");
        if (config.isList(getName())) {
            return config.getStringList(getName());
        } else {
            return new ArrayList<>();
        }
    }
}
