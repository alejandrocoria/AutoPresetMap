package games.alejandrocoria.autopresetmap.platform;

import games.alejandrocoria.autopresetmap.Constants;
import games.alejandrocoria.autopresetmap.platform.services.IPlatformHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ServiceLoader;

@ParametersAreNonnullByDefault
public class Services {
    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

    public static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Constants.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
