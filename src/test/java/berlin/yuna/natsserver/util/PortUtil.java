package berlin.yuna.natsserver.util;

import java.io.IOException;
import java.net.Socket;

import static java.util.concurrent.TimeUnit.SECONDS;

public class PortUtil {

    public static void waitForPortShutdown(int... portList) {
        for (Integer port : portList) {
            if (!shutDownPort(port)) {
                throw new RuntimeException("Unable to shutdown port [" + port + "]");
            }
        }
    }

    private static boolean shutDownPort(Integer port) {
        final long start = System.currentTimeMillis();
        long timeout = SECONDS.toMillis(10);
        while (System.currentTimeMillis() - start < timeout) {
            if (isPortAvailable(port)) {
                return true;
            }
            Thread.yield();
        }
        return false;
    }

    private static boolean isPortAvailable(int port) {
        try {
            new Socket("localhost", port).close();
            return false;
        } catch (IOException e) {
            return true;
        }

    }
}
