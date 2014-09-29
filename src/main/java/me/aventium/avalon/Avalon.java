package me.aventium.avalon;

import me.aventium.avalon.editing.Editors;
import me.aventium.avalon.koth.CapturePointModule;
import me.aventium.avalon.koth.command.KOTHCommandHandler;
import me.aventium.avalon.regions.RegionModule;
import me.aventium.avalon.regions.command.RegionCommandHandler;
import me.aventium.avalon.team.TeamCommandHandler;
import me.aventium.avalon.team.TeamManager;
import me.aventium.avalon.team.listeners.Damage;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Avalon extends JavaPlugin {

    // §

    /** Instance **/
    private static Avalon instance;

    public static Avalon get() {
        return instance;
    }

    /** data management **/
    private TeamManager teamManager;
    private PlayerManager playerManager;

    public TeamManager getTeamManager() { return teamManager; }
    public PlayerManager getPlayerManager() { return playerManager; }

    /** Command handling **/
    private TeamCommandHandler teamCommandHandler;
    private KOTHCommandHandler kothCommandHandler;
    private RegionCommandHandler regionCommandHandler;

    /** Modules **/
    private CapturePointModule capturePointModule;
    private RegionModule regionModule;

    public CapturePointModule getCapturePointModule() { return capturePointModule; }
    public RegionModule getRegionModule() { return regionModule; }

    /** Mechanics **/
    private Deathban deathban;

    public Deathban getDeathban() { return deathban; }

    /** Jedis **/
    private JedisPool pool = null;

    public JedisPool getJedisPool() {
        return pool;
    }

    public void onEnable() {
        instance = this;

        getConfig().options().copyDefaults(true);
        saveConfig();

        this.deathban = new Deathban();

        try {
            this.pool = new JedisPool(getConfig().getString("redis.host"), getConfig().getInt("redis.port"));

            Jedis jedis = getJedisPool().getResource();
            jedis.ping();
            getJedisPool().returnResource(jedis);
        } catch(JedisConnectionException ex) {
            System.out.println("Error connecting to Redis. Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
        }

        teamManager = new TeamManager();
        getServer().getPluginManager().registerEvents(new Damage(), this);

        playerManager = new PlayerManager();
        getServer().getPluginManager().registerEvents(playerManager, this);

        loadModules();

        registerCommands();

        Editors.loadEditors();
    }

    public void onDisable() {
        getTeamManager().save();
        getJedisPool().destroy();
        capturePointModule.disable();
        //capturePointModule.unload();
    }

    private void loadModules() {
        capturePointModule = new CapturePointModule(null);
        capturePointModule.load();
        capturePointModule.enable();

        regionModule = new RegionModule(null);
        regionModule.load();
    }

    private void registerCommands() {
        teamCommandHandler = new TeamCommandHandler(this);
        kothCommandHandler = new KOTHCommandHandler(this);
        regionCommandHandler = new RegionCommandHandler(this);
        getCommand("team").setExecutor(teamCommandHandler);
        getCommand("koth").setExecutor(kothCommandHandler);
        getCommand("region").setExecutor(regionCommandHandler);
        getCommand("removedeathban").setExecutor(deathban);
    }

    public void registerEvents(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    public void callEvent(Event event) { getServer().getPluginManager().callEvent(event); }

    /**
     * Gets all of the classes in a package
     *
     * @param pkgname
     *            the package to get from
     * @return list of classes
     */
    public ArrayList<Class<?>> getClassesInPackage(String pkgname) {
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        CodeSource codeSource = getClass().getProtectionDomain().getCodeSource();
        URL resource = codeSource.getLocation();

        String relPath = pkgname.replace('.', '/');
        String resPath = resource.getPath().replace("%20", " ");

        String jarPath = resPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
        JarFile jFile;

        try {
            jFile = new JarFile(jarPath);
        } catch(IOException e) {
            throw new RuntimeException("Unexpected IOException reading JAR File '" + jarPath + "'", e);
        }

        Enumeration<JarEntry> entries = jFile.entries();

        while(entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();
            String className = null;

            if (entryName.endsWith(".class") && entryName.startsWith(relPath) && entryName.length() > (relPath.length() + "/".length())) {
                className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
            }

            if(className != null) {
                Class<?> c = null;
                try {
                    c = Class.forName(className);
                } catch(ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if(c != null && !c.isAnonymousClass()) classes.add(c);
            }
        }

        try {
            jFile.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return classes;
    }
}
