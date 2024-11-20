package latte.redis.clients.jedis3.commands;

import java.util.List;

import latte.redis.clients.jedis3.args.ClientPauseMode;
import latte.redis.clients.jedis3.args.ClientType;
import latte.redis.clients.jedis3.args.UnblockType;
import latte.redis.clients.jedis3.AccessControlUser;
import latte.redis.clients.jedis3.params.MigrateParams;
import latte.redis.clients.jedis3.params.ClientKillParams;
import latte.redis.clients.jedis3.params.FailoverParams;

public interface AdvancedBinaryJedisCommands {

  List<Object> roleBinary();

  List<byte[]> configGet(byte[] pattern);

  /**
   * @param parameter
   * @param value
   * @return OK
   * @deprecated The return type will be changed to {@link String}, representing {@code OK} response,
   * in next major release. If you are not checking you continue using this method. Otherwise, you
   * can choose to use either {@link AdvancedBinaryJedisCommands#configSet(byte[], byte[]) this
   * method} or {@link AdvancedBinaryJedisCommands#configSetBinary(byte[], byte[])}.
   */
  @Deprecated
  byte[] configSet(byte[] parameter, byte[] value);

  /**
   * @deprecated This method will be removed in next major release. You may consider using
   * {@link AdvancedBinaryJedisCommands#configSet(byte[], byte[])}.
   */
  @Deprecated
  String configSetBinary(byte[] parameter, byte[] value);

  String slowlogReset();

  Long slowlogLen();

  List<Object> slowlogGetBinary();

  List<Object> slowlogGetBinary(long entries);

  Long objectRefcount(byte[] key);

  byte[] objectEncoding(byte[] key);

  Long objectIdletime(byte[] key);

  List<byte[]> objectHelpBinary();

  Long objectFreq(byte[] key);

  String migrate(String host, int port, byte[] key, int destinationDB, int timeout);

  String migrate(String host, int port, int destinationDB, int timeout, MigrateParams params,
      byte[]... keys);

  String clientKill(byte[] ipPort);

  String clientKill(String ip, int port);

  Long clientKill(ClientKillParams params);

  Long clientUnblock(long clientId, UnblockType unblockType);

  byte[] clientGetnameBinary();

  byte[] clientListBinary();

  byte[] clientListBinary(ClientType type);

  byte[] clientListBinary(long... clientIds);

  byte[] clientInfoBinary();

  String clientSetname(byte[] name);

  Long clientId();

  String clientPause(long timeout);

  String clientPause(long timeout, ClientPauseMode mode);

  byte[] memoryDoctorBinary();

  Long memoryUsage(byte[] key);

  Long memoryUsage(byte[] key, int samples);

  String failover();

  String failover(FailoverParams failoverParams);

  String failoverAbort();

  byte[] aclWhoAmIBinary();

  byte[] aclGenPassBinary();

  List<byte[]> aclListBinary();

  List<byte[]> aclUsersBinary();

  AccessControlUser aclGetUser(byte[] name);

  String aclSetUser(byte[] name);

  String aclSetUser(byte[] name, byte[]... keys);

  Long aclDelUser(byte[] name);

  List<byte[]> aclCatBinary();

  List<byte[]> aclCat(byte[] category);

  List<byte[]> aclLogBinary();

  List<byte[]> aclLogBinary(int limit);

  /**
   * @deprecated Use {@link AdvancedBinaryJedisCommands#aclLogReset()}.
   */
  @Deprecated
  byte[] aclLog(byte[] options);

  String aclLogReset();

  String aclLoad();

  String aclSave();
}
