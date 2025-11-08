package net.momirealms.sparrow.common.locale;

import dev.dejvokep.boostedyaml.YamlDocument;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.Translator;
import net.momirealms.sparrow.common.helper.AdventureHelper;
import net.momirealms.sparrow.common.plugin.SparrowPlugin;
import net.momirealms.sparrow.common.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TranslationManager {

    public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
    private static final List<String> locales = List.of("en", "zh_tw");
    private static Locale configuredLocale = Locale.TRADITIONAL_CHINESE; // 默认繁体中文

    private final SparrowPlugin plugin;
    private final Set<Locale> installed = ConcurrentHashMap.newKeySet();
    private MiniMessageTranslationRegistry registry;
    private final Path translationsDirectory;

    public TranslationManager(SparrowPlugin plugin) {
        this.plugin = plugin;
        this.translationsDirectory = this.plugin.getBootstrap().getConfigDirectory().resolve("translations");
    }

    public void reload() {
        // remove any previous registry
        if (this.registry != null) {
            MiniMessageTranslator.translator().removeSource(this.registry);
            this.installed.clear();
        }

        for (String lang : locales) {
            this.plugin.getConfigManager().loadConfig("translations/" + lang + ".yml");
        }

        // Get default locale from config
        Locale defaultLocale = getConfiguredDefaultLocale();
        configuredLocale = defaultLocale; // 保存配置的语言
        
        this.registry = MiniMessageTranslationRegistry.create(Key.key("sparrow", "main"), AdventureHelper.getMiniMessage());
        this.registry.defaultLocale(defaultLocale);
        this.loadFromFileSystem(this.translationsDirectory, false);
        MiniMessageTranslator.translator().addSource(this.registry);
        
        plugin.getBootstrap().getPluginLogger().info("Language set to: " + defaultLocale);
    }
    
    private Locale getConfiguredDefaultLocale() {
        try {
            var config = plugin.getConfigManager().loadConfig("config.yml");
            String localeStr = config.getString("default-locale", "zh_tw"); // 默认繁体中文
            if (localeStr != null && !localeStr.equalsIgnoreCase("auto")) {
                Locale locale = parseLocale(localeStr);
                if (locale != null) {
                    return locale;
                }
            }
        } catch (Exception e) {
            plugin.getBootstrap().getPluginLogger().warn("Failed to load default locale from config, using Traditional Chinese", e);
        }
        return Locale.TRADITIONAL_CHINESE; // 默认繁体中文
    }

    public static Component render(Component component) {
        return render(component, null);
    }

    public static Component render(Component component, @Nullable Locale locale) {
        if (locale == null) {
            // 使用配置的语言，而不是系统语言
            locale = configuredLocale;
        }
        return MiniMessageTranslator.render(component, locale);
    }

    public void loadFromFileSystem(Path directory, boolean suppressDuplicatesError) {
        List<Path> translationFiles;
        try (Stream<Path> stream = Files.list(directory)) {
            translationFiles = stream.filter(TranslationManager::isTranslationFile).collect(Collectors.toList());
        } catch (IOException e) {
            translationFiles = Collections.emptyList();
        }

        if (translationFiles.isEmpty()) {
            return;
        }

        Map<Locale, Map<String, String>> loaded = new HashMap<>();
        for (Path translationFile : translationFiles) {
            try {
                Pair<Locale, Map<String, String>> result = loadTranslationFile(translationFile);
                loaded.put(result.left(), result.right());
            } catch (Exception e) {
                if (!suppressDuplicatesError || !isAdventureDuplicatesException(e)) {
                    this.plugin.getBootstrap().getPluginLogger().warn("Error loading locale file: " + translationFile.getFileName(), e);
                }
            }
        }

        // try registering the locale without a country code - if we don't already have a registration for that
        loaded.forEach((locale, bundle) -> {
            Locale localeWithoutCountry = new Locale(locale.getLanguage());
            if (!locale.equals(localeWithoutCountry) && !localeWithoutCountry.equals(DEFAULT_LOCALE) && this.installed.add(localeWithoutCountry)) {
                try {
                    this.registry.registerAll(localeWithoutCountry, bundle);
                } catch (IllegalArgumentException e) {
                    // ignore
                }
            }
        });
    }

    public static boolean isTranslationFile(Path path) {
        return path.getFileName().toString().endsWith(".yml");
    }

    private static boolean isAdventureDuplicatesException(Exception e) {
        return e instanceof IllegalArgumentException && (e.getMessage().startsWith("Invalid key") || e.getMessage().startsWith("Translation already exists"));
    }

    private Pair<Locale, Map<String, String>> loadTranslationFile(Path translationFile) {
        String fileName = translationFile.getFileName().toString();
        String localeString = fileName.substring(0, fileName.length() - ".yml".length());
        Locale locale = parseLocale(localeString);

        if (locale == null) {
            throw new IllegalStateException("Unknown locale '" + localeString + "' - unable to register.");
        }

        Map<String, String> bundle = new HashMap<>();
        YamlDocument document = plugin.getConfigManager().loadConfig("translations" + "\\" + translationFile.getFileName(), '@');
        Map<String, Object> map = document.getStringRouteMappedValues(false);
        map.remove("config-version");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof List<?> list) {
                List<String> strList = (List<String>) list;
                StringJoiner stringJoiner = new StringJoiner("<reset><newline>");
                for (String str : strList) {
                    stringJoiner.add(str);
                }
                bundle.put(entry.getKey(), stringJoiner.toString());
            } else if (entry.getValue() instanceof String str) {
                bundle.put(entry.getKey(), str);
            }
        }

        this.registry.registerAll(locale, bundle);
        this.installed.add(locale);

        return Pair.of(locale, bundle);
    }

    public static @Nullable Locale parseLocale(@Nullable String locale) {
        return locale == null ? null : Translator.parseLocale(locale);
    }
}
