package bungeepluginmanager;

import java.io.File;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import org.yaml.snakeyaml.Yaml;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;

public class Commands extends Command {

	public Commands() {
		super("bungeepluginmanager", "bungeepluginmanager.cmds", "bpm");
	}

	//TODO: split to subcommands
	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length < 1) {
			sender.sendMessage(textWithColor("Not enough args", ChatColor.RED));
			return;
		}
		switch (args[0].toLowerCase()) {
			case "list": {
				sender.sendMessage(textWithColor(ProxyServer.getInstance().getPluginManager().getPlugins().stream().map(plugin -> plugin.getDescription().getName()).collect(Collectors.joining(", ")), ChatColor.GREEN));
				return;
			}
			case "unload": {
				if (args.length < 2) {
					sender.sendMessage(textWithColor("Not enough args", ChatColor.RED));
					return;
				}

				Plugin plugin = findPlugin(args[1]);
				if (plugin == null) {
					sender.sendMessage(textWithColor("Plugin not found", ChatColor.RED));
					return;
				}

				Exception unloadError = PluginUtils.unloadPlugin(plugin);
				sender.sendMessage(textWithColor("Plugin unloaded", ChatColor.YELLOW));
				if (unloadError != null) {
					sender.sendMessage(textWithColor("Errors occured while disabling plugin, see console for more details", ChatColor.RED));
					unloadError.printStackTrace();
				}
				return;
			}
			case "load": {
				if (args.length < 2) {
					sender.sendMessage(textWithColor("Not enough args", ChatColor.RED));
					return;
				}

				Plugin plugin = findPlugin(args[1]);
				if (plugin != null) {
					sender.sendMessage(textWithColor("Plugin is already loaded", ChatColor.RED));
					return;
				}
				File file = findFile(args[1]);
				if (!file.exists()) {
					sender.sendMessage(textWithColor("Plugin not found", ChatColor.RED));
					return;
				}

				try {
					PluginUtils.loadPlugin(file);
					sender.sendMessage(textWithColor("Plugin loaded", ChatColor.YELLOW));
				} catch (Throwable t) {
					sender.sendMessage(textWithColor("Error occured while loading plugin, see console for more details", ChatColor.RED));
					t.printStackTrace();
				}
				return;
			}
			case "reload": {
				if (args.length < 2) {
					sender.sendMessage(textWithColor("Not enough args", ChatColor.RED));
					return;
				}

				Plugin plugin = findPlugin(args[1]);
				if (plugin == null) {
					sender.sendMessage(textWithColor("Plugin not found", ChatColor.RED));
					return;
				}
				File pluginfile = plugin.getFile();

				Exception unloadError = PluginUtils.unloadPlugin(plugin);
				if (unloadError != null) {
					sender.sendMessage(textWithColor("Errors occured while disabling plugin, see console for more details", ChatColor.RED));
					unloadError.printStackTrace();
				}
				try {
					PluginUtils.loadPlugin(pluginfile);
					sender.sendMessage(textWithColor("Plugin reloaded", ChatColor.YELLOW));
				} catch (Throwable t) {
					sender.sendMessage(textWithColor("Error occured while loading plugin, see console for more details", ChatColor.RED));
					t.printStackTrace();
				}
				return;
			}
		}
	}

	private static Plugin findPlugin(String pluginname) {
		for (Plugin plugin : ProxyServer.getInstance().getPluginManager().getPlugins()) {
			if (plugin.getDescription().getName().equalsIgnoreCase(pluginname)) {
				return plugin;
			}
		}
		return null;
	}

	private static File findFile(String pluginname) {
		File folder = ProxyServer.getInstance().getPluginsFolder();
		if (folder.exists()) {
			for (File file : folder.listFiles()) {
				if (file.isFile() && file.getName().endsWith(".jar")) {
					try (JarFile jar = new JarFile(file)) {
						JarEntry pdf = jar.getJarEntry("bungee.yml");
						if (pdf == null) {
							pdf = jar.getJarEntry("plugin.yml");
						}
						try (InputStream in = jar.getInputStream(pdf)) {
							final PluginDescription desc = new Yaml().loadAs(in, PluginDescription.class);
							if (desc.getName().equalsIgnoreCase(pluginname)) {
								return file;
							}
						}
					} catch (Throwable ex) {
					}
				}
			}
		}
		return new File(folder, pluginname+".jar");
	}

	private static TextComponent textWithColor(String message, ChatColor color) {
		TextComponent text = new TextComponent(message);
		text.setColor(color);
		return text;
	}

}
