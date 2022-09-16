package berlin.yuna.natsserver.streaming.embedded;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static berlin.yuna.natsserver.config.NatsStreamingConfig.NATS_STREAMING_VERSION;
import static java.util.Objects.requireNonNull;

@Tag("UnitTest")
class VersionTextUpdaterTest {

    @Test
    void updateVersionTxtTest() throws IOException {
        final Path versionFile = Paths.get(System.getProperty("user.dir"), "version.txt");
        final String versionText = readFile(versionFile);
        final String natsVersion = NATS_STREAMING_VERSION.value();
        if (!requireNonNull(natsVersion).equals(versionText)) {
            Files.write(versionFile, (natsVersion.startsWith("v") ? natsVersion.substring(1) : natsVersion).getBytes());
        }
    }

    private static String readFile(final Path versionFile) {
        try {
            return "v" + new String(Files.readAllBytes(versionFile));
        } catch (IOException e) {
            return "";
        }
    }
}
