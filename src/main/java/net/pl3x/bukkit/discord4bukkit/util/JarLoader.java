package net.pl3x.bukkit.discord4bukkit.util;

import net.pl3x.bukkit.discord4bukkit.D4BPlugin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;

/*
 * Adapted from AntiVPN https://github.com/egg82/AntiVPN/blob/master/Bukkit/src/main/java/me/egg82/antivpn/utils/JarUtil.java
 */
public class JarLoader {
    private static final Method ADD_URL_METHOD;

    static {
        try {
            ADD_URL_METHOD = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            ADD_URL_METHOD.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private final D4BPlugin plugin;

    private URLClassLoader classLoader;

    public JarLoader(D4BPlugin plugin) {
        this.plugin = plugin;

        classLoader = ((URLClassLoader) plugin.getClass().getClassLoader());
    }

    public boolean loadJar(String url, File file) {
        try {
            if (!file.getParentFile().exists()) {
                if (!file.getParentFile().mkdirs()) {
                    plugin.getLogger().warning("Could not create directory: " + file.getParentFile().getAbsolutePath());
                }
            }

            if (file.exists() && file.isDirectory()) {
                Files.delete(file.toPath());
            }

            if (!file.exists()) {
                plugin.getLogger().info("Jar not found! (" + file.getName() + ")");
                plugin.getLogger().info("Downloading jar from " + url);
                downloadJar(url, file);
            }

            ADD_URL_METHOD.invoke(classLoader, file.toPath().toUri().toURL());
            return true;
        } catch (IOException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void downloadJar(String url, File file) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setInstanceFollowRedirects(true);

        boolean redirect;

        do {
            int status = conn.getResponseCode();
            redirect = status == HttpURLConnection.HTTP_MOVED_TEMP ||
                    status == HttpURLConnection.HTTP_MOVED_PERM ||
                    status == HttpURLConnection.HTTP_SEE_OTHER;

            if (redirect) {
                String newUrl = conn.getHeaderField("Location");
                String cookies = conn.getHeaderField("Set-Cookie");

                conn = (HttpURLConnection) new URL(newUrl).openConnection();
                conn.setRequestProperty("Cookie", cookies);
                conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
            }
        } while (redirect);

        Progress progress = new Progress(conn.getContentLength());
        progress.start();

        try (BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
             FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                progress.current += bytesRead;
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        }

        progress.interrupt();
    }

    private class Progress extends Thread {
        private final long total;
        private long current;

        private Progress(long total) {
            this.total = total;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    logProgress();
                    sleep(1000);
                }
            } catch (InterruptedException ignore) {
            }
        }

        @Override
        public void interrupt() {
            logProgress();
            super.interrupt();
        }

        private void logProgress() {
            plugin.getLogger().info(
                    String.format("     progress: %s%% (%s/%s)",
                            (int) ((((double) current) / ((double) total)) * 100D),
                            current, total));
        }
    }
}
