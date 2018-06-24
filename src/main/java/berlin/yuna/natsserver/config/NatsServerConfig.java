package berlin.yuna.natsserver.config;

import java.util.UUID;

public enum NatsServerConfig {

    //Streaming Server Options
    CLUSTER_ID("test-cluster", "[STRING] Cluster ID (default: test-cluster)"),
    STORE("MEMORY", "[STRING] Store type: MEMORY|FILE (default: MEMORY)"),
    DIR(null, "[STRING] For FILE store type, this is the root directory"),
    MAX_CHANNELS("0", "[INT] Max number of channels (0 for unlimited)"),
    MAX_SUBS("0", "[INT] Max number of subscriptions per channel (0 for unlimited)"),
    MAX_MSGS("0", "[INT] Max number of messages per channel (0 for unlimited)"),
    MAX_BYTES("0", "[SIZE] Max messages total size per channel (0 for unlimited)"),
    MAX_AGE("0s", "[DURATION] Max duration a message can be stored (\"0s\" for unlimited)"),
    MAX_INACTIVITY("0", "[DURATION] Max inactivity (no new message, no subscription) after which a channel can be garbage collected (0 for unlimited)"),
    NATS_SERVER(null, "[STRING] Connect to this external NATS Server URL (embedded otherwise)"),
    STAN_CONFIG(null, "[STRING] Streaming server configuration file"),
    HB_INTERVAL(null, "[DURATION] Interval at which server sends heartbeat to a client"),
    HB_TIMEOUT(null, "[DURATION] How long server waits for a heartbeat response"),
    HB_FAIL_COUNT(null, "[INT] Number of failed heartbeats before server closes the client connection"),
    FT_GROUP(null, "[STRING] Name of the FT Group. A group can be 2 or more servers with a single active server and all sharing the same datastore."),

    //Streaming Server Clustering Options
    CLUSTERED("false", "[BOOL] Run the server in a clustered configuration (default: false)"),
    CLUSTER_NODE_ID(UUID.randomUUID().toString(), "[STRING] ID of the node within the cluster if there is no stored ID (default: random UUID)"),
    CLUSTER_BOOTSTRAP("false", "[BOOL] Bootstrap the cluster if there is no existing state by electing self as leader (default: false)"),
    CLUSTER_PEERS(null, "[STRING] List of cluster peer node IDs to bootstrap cluster state."),
    CLUSTER_LOG_PATH(null, "[STRING] Directory to store log replication data"),
    CLUSTER_LOG_CACHE_SIZE("512", "[INT] Number of log entries to cache in memory to reduce disk IO (default: 512)"),
    CLUSTER_LOG_SNAPSHOTS("2", "[INT] Number of log snapshots to retain (default: 2)"),
    CLUSTER_TRAILING_LOGS(null, "[INT] Number of log entries to leave after a snapshot and compaction"),
    CLUSTER_SYNC(null, "[BOOL] Do a file sync after every write to the replication log and message store"),
    CLUSTER_RAFT_LOGGING(null, "[BOOL] Enable logging from the Raft library (disabled by default)"),

    //Streaming Server File Store Options
    FILE_COMPACT_ENABLED(null, "[BOOL] Enable file compaction"),
    FILE_COMPACT_FRAG(null, "[INT] File fragmentation threshold for compaction"),
    FILE_COMPACT_INTERVAL(null, "[INT] Minimum interval (in seconds) between file compactions"),
    FILE_COMPACT_MIN_SIZE(null, "[SIZE] Minimum file size for compaction"),
    FILE_BUFFER_SIZE(null, "[SIZE] File buffer size (in bytes)"),
    FILE_CRC(null, "[BOOL] Enable file CRC-32 checksum"),
    FILE_CRC_POLY(null, "[INT] Polynomial used to make the table used for CRC-32 checksum"),
    FILE_SYNC(null, "[BOOL] Enable File.Sync on Flush"),
    FILE_SLICE_MAX_MSGS(null, "[INT] Maximum number of messages per file slice (subject to channel limits)"),
    FILE_SLICE_MAX_BYTES(null, "[SIZE] Maximum file slice size - including index file (subject to channel limits)"),
    FILE_SLICE_MAX_AGE(null, "[DURATION] Maximum file slice duration starting when the first message is stored (subject to channel limits)"),
    FILE_SLICE_ARCHIVE_SCRIPT(null, "[STRING] Path to script to use if you want to archive a file slice being removed"),
    FILE_FDS_LIMIT(null, "[INT] Store will try to use no more file descriptors than this given limit"),
    FILE_PARALLEL_RECOVERY(null, "[INT] On startup, number of channels that can be recovered in parallel"),

    //Streaming Server SQL Store Options
    SQL_DRIVER(null, "[STRING] Name of the SQL Driver (\"mysql\" or \"postgres\")"),
    SQL_DATA_SOURCE(null, "[STRING] Datasource used when opening an SQL connection to the database"),
    SQL_NO_CACHING(null, "[BOOL] Enable/Disable caching for improved performance"),
    SQL_MAX_OPEN_CONNS(null, "[INT] Maximum number of opened connections to the database"),

    //Streaming Server TLS Options
    SECURE(null, "-[BOOL] Use a TLS connection to the NATS server without verification; weaker than specifying certificates."),
    TLS_CLIENT_KEY(null, "-[STRING] Client key for the streaming server"),
    TLS_CLIENT_CERT(null, "-[STRING] Client certificate for the streaming server"),
    TLS_CLIENT_CACERT(null, "-[STRING] Client certificate CA for the streaming server"),

    //Streaming Server Logging Options
    STAN_DEBUG("false", "[BOOL] Enable STAN debugging output"),
    STAN_TRACE("false", "[BOOL] Trace the raw STAN protocol"),

    //NATS Server Options
    ADDR("0.0.0.0", "[STRING] Bind to host address (default: 0.0.0.0)"),
    PORT("4222", "[INT] Use port for clients (default: 4222)"),
    PID(null, "[STRING] File to store PID"),
    HTTP_PORT(null, "[INT] Use port for http monitoring"),
    HTTPS_PORT(null, "[INT] Use port for https monitoring"),
    CONFIG(null, "[STRING] Configuration file"),

    //NATS Server Logging Options
    LOG(null, "[STRING] File to redirect log output"),
    LOGTIME("true", "[BOOL] Timestamp log entries (default: true)"),
    SYSLOG(null, "[STRING] Enable syslog as log method"),
    REMOTE_SYSLOG(null, "[STRING] Syslog server addr (udp://localhost:514)"),
    DEBUG("false", "[BOOL] Enable debugging output"),
    TRACE("false", "[BOOL] Trace the raw protocol"),

    //NATS Server Authorization Options
    USER(null, "[STRING] User required for connections"),
    PASS(null, "[STRING] Password required for connections"),
    AUTH(null, "[STRING] Authorization token required for connections"),

    //TLS Options
    TLS("false", "[BOOL] Enable TLS, do not verify clients (default: false)"),
    TLSCERT(null, "[STRING] Server certificate file"),
    TLSVERIFY(null, "[BOOL] Enable TLS, verify client certificates"),
    TLSCACERT(null, "[STRING] Client certificate CA for verification"),

    //NATS Clustering Options
    ROUTES(null, "[STRING] Routes to solicit and connect"),
    CLUSTER(null, "[STRING] Cluster URL for solicited routes");

    private final String defaultValue;
    private final String description;

    NatsServerConfig(String defaultValue, String description) {
        this.defaultValue = defaultValue;
        this.description = description;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Command line property key
     *
     * @return key for command line
     */
    public String getKey() {
        String key = name().toLowerCase();
        key = getDescription().startsWith("-") ? "-" + key : "--" + key;
        key = getDescription().startsWith("[BOOL]") ? key + "=" : key + " ";
        return key;
    }
}
