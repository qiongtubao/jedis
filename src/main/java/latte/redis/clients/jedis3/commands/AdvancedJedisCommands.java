package latte.redis.clients.jedis3.commands;

import java.util.List;

import latte.redis.clients.jedis3.args.ClientPauseMode;
import latte.redis.clients.jedis3.args.ClientType;
import latte.redis.clients.jedis3.args.UnblockType;
import latte.redis.clients.jedis3.AccessControlLogEntry;
import latte.redis.clients.jedis3.AccessControlUser;
import latte.redis.clients.jedis3.params.MigrateParams;
import latte.redis.clients.jedis3.params.ClientKillParams;
import latte.redis.clients.jedis3.params.FailoverParams;
import latte.redis.clients.jedis3.util.Slowlog;

public interface AdvancedJedisCommands {

  List<Object> role();

  List<String> configGet(String pattern);

  String configSet(String parameter, String value);

  String slowlogReset();

  Long slowlogLen();

  List<Slowlog> slowlogGet();

  List<Slowlog> slowlogGet(long entries);

  Long objectRefcount(String key);

  String objectEncoding(String key);

  Long objectIdletime(String key);

  List<String> objectHelp();

  Long objectFreq(String key);

  String migrate(String host, int port, String key, int destinationDB, int timeout);

  String migrate(String host, int port, int destinationDB, int timeout, MigrateParams params,
      String... keys);

  String clientKill(String ipPort);

  String clientKill(String ip, int port);

  Long clientKill(ClientKillParams params);

  String clientGetname();

  String clientList();

  String clientList(ClientType type);

  String clientList(long... clientIds);

  String clientInfo();

  String clientSetname(String name);

  Long clientId();

  Long clientUnblock(long clientId, UnblockType unblockType);

  String clientPause(long timeout);

  String clientPause(long timeout, ClientPauseMode mode);

  String memoryDoctor();

  Long memoryUsage(String key);

  Long memoryUsage(String key, int samples);

  String failover();

  String failover(FailoverParams failoverParams);

  String failoverAbort();

  String aclWhoAmI();

  String aclGenPass();

  List<String> aclList();

  List<String> aclUsers();

  AccessControlUser aclGetUser(String name);

  String aclSetUser(String name);

  String aclSetUser(String name, String... keys);

  Long aclDelUser(String name);

  List<String> aclCat();

  List<String> aclCat(String category);

  List<AccessControlLogEntry> aclLog();

  List<AccessControlLogEntry> aclLog(int limit);

  /**
   * @deprecated Use {@link AdvancedJedisCommands#aclLogReset()}.
   */
  @Deprecated
  String aclLog(String options);

  String aclLogReset();

  String aclLoad();

  String aclSave();
}
