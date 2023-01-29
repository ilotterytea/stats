package kz.ilotterytea.stats.storage;

import com.google.gson.Gson;
import kz.ilotterytea.stats.models.Target;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author ilotterytea
 * @since 1.0
 */
public class TargetController {
    private final Logger log = LoggerFactory.getLogger(TargetController.class);

    private final Map<String, Target> map;
    private final File folderPath;

    public TargetController(
            String folder_path
    ) {
        this.map = new HashMap<>();
        this.folderPath = new File(folder_path);
        loadFolder();
    }

    private void loadFolder() {
        if (!folderPath.exists()) {
            log.warn("The folder in \"" + folderPath.getPath() + "\" not exist!");
            return;
        }

        if (folderPath.isDirectory()) {
            for (File file : Objects.requireNonNull(folderPath.listFiles())) {
                processFile(file);
            }
        } else {
            log.warn("The path to the folder \"" + folderPath.getPath() + "\" is not a directory. It will be processed as a single file.");
            processFile(folderPath);
        }

        log.info("");
    }

    public Target get(String aliasId) {
        if (map.containsKey(aliasId)) {
            return map.get(aliasId);
        }
        return null;
    }

    public void put(String aliasId, Target target) { map.put(aliasId, target); }

    public Target getOrDefault(String aliasId) {
        if (map.containsKey(aliasId)) {
            return map.get(aliasId);
        }

        return new Target(aliasId);
    }

    private void processFile(File file) {
        try (Reader reader = new FileReader(file)) {
            Target target = new Gson().fromJson(reader, Target.class);

            if (target != null) {
                map.put(target.getAliasId(), target);
                log.debug("Loaded Target ID " + target.getAliasId() + "!");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save() {
        for (Target target : map.values()) {
            saveTarget(target);
        }

        log.info("Saved " + map.keySet().size() + " entries!");
    }

    private void saveTarget(Target target) {
        try (Writer writer = new FileWriter(new File(folderPath.getPath() + "/" + target.getAliasId() + ".json"))) {
            writer.write(new Gson().newBuilder().setPrettyPrinting().create().toJson(target, Target.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Target> getTargets() {
        return map;
    }
}
