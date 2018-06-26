package berlin.yuna.natsserver.logic;

import berlin.yuna.natsserver.config.NatsServerConfig;
import berlin.yuna.natsserver.util.StreamGobbler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static berlin.yuna.natsserver.util.SystemUtil.executeCommand;
import static berlin.yuna.natsserver.util.SystemUtil.getOsType;
import static java.util.Arrays.stream;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NatsServerConfigComponentTest {

    @Test
    public void compareNatsServerConfig() throws IOException, InterruptedException {
        Path natsServerPath = new NatsServer().getNatsServerPath(getOsType());

        Process process = executeCommand(natsServerPath.toString() + " --help");

        List<String> consoleConfigKeys = readConfigKeys(consoleOutput(process));
        List<String> javaConfigKeys = stream(NatsServerConfig.values()).map(Enum::name).collect(Collectors.toList());

        Set<String> missingConfigInJava = getNotMatchingEntities(consoleConfigKeys, javaConfigKeys);
        assertThat(missingConfigInJava, is(empty()));

        Set<String> missingConfigInConsole = getNotMatchingEntities(javaConfigKeys, consoleConfigKeys);
        assertThat(missingConfigInConsole, is(empty()));
    }

    @Test
    public void getKey_WithOneMinus_ShouldBeSuccessful() {
        assertThat(NatsServerConfig.SECURE.getKey(), is(equalTo("-secure ")));
    }

    @Test
    public void getKey_WithBoolean_ShouldAddOneEqualSign() {
        assertThat(NatsServerConfig.CLUSTERED.getKey(), is(equalTo("--clustered=")));
    }

    private Set<String> getNotMatchingEntities(List<String> list1, List<String> list2) {
        Set<String> noMatches = new HashSet<>();
        for (String entity : list1) {
            if (!list2.contains(entity)) {
                noMatches.add(entity);
            }
        }
        return noMatches;
    }

    private List<String> readConfigKeys(String console) {
        List<String> allMatches = new ArrayList<>();
        Matcher m = Pattern.compile("-([a-z_]*)(\\s+|=)<[^,]").matcher(console);
        while (m.find()) {
            allMatches.add(m.group(1).toUpperCase());
        }
        return allMatches;
    }

    private String consoleOutput(Process process) throws InterruptedException {
        StringBuilder console = new StringBuilder();
        Executors.newSingleThreadExecutor().submit(new StreamGobbler(process.getInputStream(), line -> console.append(line).append("\n")));
        Executors.newSingleThreadExecutor().submit(new StreamGobbler(process.getErrorStream(), line -> console.append(line).append("\n")));

        int count;
        do {
            count = console.length();
            Thread.sleep(256);
        } while (count == 0 || count != console.length());
        return console.toString();
    }
}