package latte.redis.clients.jedis3;

import static latte.redis.clients.jedis3.Protocol.toByteArray;

import java.util.*;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;

import latte.redis.clients.jedis3.Protocol.Command;
import latte.redis.clients.jedis3.Protocol.Keyword;
import latte.redis.clients.jedis3.Protocol.SentinelKeyword;
import latte.redis.clients.jedis3.args.ClientPauseMode;
import latte.redis.clients.jedis3.args.ClientType;
import latte.redis.clients.jedis3.args.FlushMode;
import latte.redis.clients.jedis3.args.SaveMode;
import latte.redis.clients.jedis3.args.UnblockType;
import latte.redis.clients.jedis3.params.ClientKillParams;
import latte.redis.clients.jedis3.params.FailoverParams;
import latte.redis.clients.jedis3.params.GeoAddParams;
import latte.redis.clients.jedis3.params.GeoRadiusParam;
import latte.redis.clients.jedis3.params.GeoRadiusStoreParam;
import latte.redis.clients.jedis3.params.GetExParams;
import latte.redis.clients.jedis3.params.LPosParams;
import latte.redis.clients.jedis3.params.MigrateParams;
import latte.redis.clients.jedis3.params.RestoreParams;
import latte.redis.clients.jedis3.params.SetParams;
import latte.redis.clients.jedis3.params.StrAlgoLCSParams;
import latte.redis.clients.jedis3.params.XAddParams;
import latte.redis.clients.jedis3.params.XAutoClaimParams;
import latte.redis.clients.jedis3.params.XClaimParams;
import latte.redis.clients.jedis3.params.XPendingParams;
import latte.redis.clients.jedis3.params.XReadGroupParams;
import latte.redis.clients.jedis3.params.XReadParams;
import latte.redis.clients.jedis3.args.ListDirection;
import latte.redis.clients.jedis3.params.XTrimParams;
import latte.redis.clients.jedis3.params.ZAddParams;
import latte.redis.clients.jedis3.params.ZIncrByParams;
import latte.redis.clients.jedis3.params.*;
import latte.redis.clients.jedis3.util.SafeEncoder;

/**
 * @deprecated This class will be removed in next major release.
 */
@Deprecated
public class BinaryClient extends Connection {

  private boolean isInMulti;

  @Deprecated
  private String user;
  @Deprecated
  private String password;

  private int db;

  private boolean isInWatch;

  public BinaryClient() {
    super();
  }

  /**
   * @param host
   * @deprecated This constructor will be removed in future. It can be replaced with
   * {@link #BinaryClient(java.lang.String, int)} with the host and {@link Protocol#DEFAULT_PORT}.
   */
  @Deprecated
  public BinaryClient(final String host) {
    super(host);
  }

  public BinaryClient(final String host, final int port) {
    super(host, port);
  }

  /**
   * @deprecated This constructor will be removed in future. Use
   * {@link #BinaryClient(HostAndPort, JedisClientConfig)}.
   */
  @Deprecated
  public BinaryClient(final String host, final int port, final boolean ssl) {
    super(host, port, ssl);
  }

  /**
   * @deprecated This constructor will be removed in future. Use
   * {@link #BinaryClient(HostAndPort, JedisClientConfig)}.
   */
  @Deprecated
  public BinaryClient(final String host, final int port, final boolean ssl,
      final SSLSocketFactory sslSocketFactory, final SSLParameters sslParameters,
      final HostnameVerifier hostnameVerifier) {
    super(host, port, ssl, sslSocketFactory, sslParameters, hostnameVerifier);
  }

  public BinaryClient(final HostAndPort hostPort, final JedisClientConfig clientConfig) {
    super(hostPort, clientConfig);
  }

  public BinaryClient(final JedisSocketFactory jedisSocketFactory) {
    super(jedisSocketFactory);
  }

  public boolean isInMulti() {
    return isInMulti;
  }

  public boolean isInWatch() {
    return isInWatch;
  }

  /**
   * @param user
   * @deprecated This method will be removed in future. Because this class will be restricted from
   * holding any user data.
   */
  @Deprecated
  public void setUser(final String user) {
    this.user = user;
  }

  /**
   * @param password
   * @deprecated This method will be removed in future. Because this class will be restricted from
   * holding any user data.
   */
  @Deprecated
  public void setPassword(final String password) {
    this.password = password;
  }

  /**
   * This method should be called only after a successful SELECT command.
   * @param db
   */
  public void setDb(int db) {
    this.db = db;
  }

  public int getDB() {
    return db;
  }

  @Override
  public void connect() {
    if (!isConnected()) {
      super.connect();
      if (user != null) {
        auth(user, password);
        getStatusCodeReply();
      } else if (password != null) {
        auth(password);
        getStatusCodeReply();
      }
      if (db > 0) {
        select(db);
        getStatusCodeReply();
      }
    }
  }

  /**
   * Closing the socket will disconnect the server connection.
   */
  @Override
  public void disconnect() {
    db = 0;
    super.disconnect();
  }

  @Override
  public void close() {
    db = 0;
    super.close();
  }

  public void resetState() {
    if (isInWatch()) {
      unwatch();
      getStatusCodeReply();
    }
  }

  public void copy(byte[] srcKey, byte[] dstKey, boolean replace) {
    if (replace) {
      sendCommand(Command.COPY, srcKey, dstKey, Keyword.REPLACE.getRaw());
    } else {
      sendCommand(Command.COPY, srcKey, dstKey);
    }
  }

  public void copy(byte[] srcKey, byte[] dstKey, int db, boolean replace) {
    if (replace) {
      sendCommand(Command.COPY, srcKey, dstKey, Keyword.DB.getRaw(), Protocol.toByteArray(db), Keyword.REPLACE.getRaw());
    } else {
      sendCommand(Command.COPY, srcKey, dstKey, Keyword.DB.getRaw(), Protocol.toByteArray(db));
    }
  }

  public void ping() {
    sendCommand(Command.PING);
  }

  public void ping(final byte[] message) {
    sendCommand(Command.PING, message);
  }

  public void set(final byte[] key, final byte[] value) {
    sendCommand(Command.SET, key, value);
  }

  public void set(final byte[] key, final byte[] value, final SetParams params) {
    sendCommand(Command.SET, params.getByteParams(key, value));
  }

  public void get(final byte[] key) {
    sendCommand(Command.GET, key);
  }

  public void getDel(final byte[] key) {
    sendCommand(Command.GETDEL, key);
  }

  public void getEx(final byte[] key, final GetExParams params) {
    sendCommand(Command.GETEX, params.getByteParams(key));
  }

  /**
   * @deprecated The QUIT command is deprecated, see <a href="https://github.com/redis/redis/issues/11420">#11420</a>.
   * {@link BinaryClient#disconnect()} can be used instead.
   */
  @Deprecated
  public void quit() {
    db = 0;
    sendCommand(Command.QUIT);
  }

  public void exists(final byte[]... keys) {
    sendCommand(Command.EXISTS, keys);
  }

  public void del(final byte[]... keys) {
    sendCommand(Command.DEL, keys);
  }

  public void unlink(final byte[]... keys) {
    sendCommand(Command.UNLINK, keys);
  }

  public void type(final byte[] key) {
    sendCommand(Command.TYPE, key);
  }

  public void flushDB() {
    sendCommand(Command.FLUSHDB);
  }

  public void flushDB(FlushMode flushMode) {
    sendCommand(Command.FLUSHDB, flushMode.getRaw());
  }

  public void keys(final byte[] pattern) {
    sendCommand(Command.KEYS, pattern);
  }

  public void randomKey() {
    sendCommand(Command.RANDOMKEY);
  }

  public void rename(final byte[] oldkey, final byte[] newkey) {
    sendCommand(Command.RENAME, oldkey, newkey);
  }

  public void renamenx(final byte[] oldkey, final byte[] newkey) {
    sendCommand(Command.RENAMENX, oldkey, newkey);
  }

  public void dbSize() {
    sendCommand(Command.DBSIZE);
  }

  /**
   * @deprecated Use {@link #expire(byte[], long)}.
   */
  @Deprecated
  public void expire(final byte[] key, final int seconds) {
    sendCommand(Command.EXPIRE, key, Protocol.toByteArray(seconds));
  }

  public void expire(final byte[] key, final long seconds) {
    sendCommand(Command.EXPIRE, key, Protocol.toByteArray(seconds));
  }

  public void expireAt(final byte[] key, final long unixTime) {
    sendCommand(Command.EXPIREAT, key, Protocol.toByteArray(unixTime));
  }

  public void ttl(final byte[] key) {
    sendCommand(Command.TTL, key);
  }

  public void touch(final byte[]... keys) {
    sendCommand(Command.TOUCH, keys);
  }

  public void select(final int index) {
    sendCommand(Command.SELECT, Protocol.toByteArray(index));
  }

  public void swapDB(final int index1, final int index2) {
    sendCommand(Command.SWAPDB, Protocol.toByteArray(index1), Protocol.toByteArray(index2));
  }

  public void move(final byte[] key, final int dbIndex) {
    sendCommand(Command.MOVE, key, Protocol.toByteArray(dbIndex));
  }

  public void flushAll() {
    sendCommand(Command.FLUSHALL);
  }

  public void flushAll(FlushMode flushMode) {
    sendCommand(Command.FLUSHALL, flushMode.getRaw());
  }

  public void getSet(final byte[] key, final byte[] value) {
    sendCommand(Command.GETSET, key, value);
  }

  public void mget(final byte[]... keys) {
    sendCommand(Command.MGET, keys);
  }

  public void setnx(final byte[] key, final byte[] value) {
    sendCommand(Command.SETNX, key, value);
  }

  /**
   * @deprecated Use {@link #setex(byte[], long, byte[])}.
   */
  @Deprecated
  public void setex(final byte[] key, final int seconds, final byte[] value) {
    sendCommand(Command.SETEX, key, Protocol.toByteArray(seconds), value);
  }

  public void setex(final byte[] key, final long seconds, final byte[] value) {
    sendCommand(Command.SETEX, key, Protocol.toByteArray(seconds), value);
  }

  public void mset(final byte[]... keysvalues) {
    sendCommand(Command.MSET, keysvalues);
  }

  public void msetnx(final byte[]... keysvalues) {
    sendCommand(Command.MSETNX, keysvalues);
  }

  public void decrBy(final byte[] key, final long decrement) {
    sendCommand(Command.DECRBY, key, Protocol.toByteArray(decrement));
  }

  public void decr(final byte[] key) {
    sendCommand(Command.DECR, key);
  }

  public void incrBy(final byte[] key, final long increment) {
    sendCommand(Command.INCRBY, key, Protocol.toByteArray(increment));
  }

  public void incrByFloat(final byte[] key, final double increment) {
    sendCommand(Command.INCRBYFLOAT, key, Protocol.toByteArray(increment));
  }

  public void incr(final byte[] key) {
    sendCommand(Command.INCR, key);
  }

  public void append(final byte[] key, final byte[] value) {
    sendCommand(Command.APPEND, key, value);
  }

  public void substr(final byte[] key, final int start, final int end) {
    sendCommand(Command.SUBSTR, key, Protocol.toByteArray(start), Protocol.toByteArray(end));
  }

  public void hset(final byte[] key, final byte[] field, final byte[] value) {
    sendCommand(Command.HSET, key, field, value);
  }

  public void hset(final byte[] key, final Map<byte[], byte[]> hash) {
    final byte[][] params = new byte[1 + hash.size() * 2][];

    int index = 0;
    params[index++] = key;
    for (final Entry<byte[], byte[]> entry : hash.entrySet()) {
      params[index++] = entry.getKey();
      params[index++] = entry.getValue();
    }
    sendCommand(Command.HSET, params);
  }

  public void hget(final byte[] key, final byte[] field) {
    sendCommand(Command.HGET, key, field);
  }

  public void hsetnx(final byte[] key, final byte[] field, final byte[] value) {
    sendCommand(Command.HSETNX, key, field, value);
  }

  public void hmset(final byte[] key, final Map<byte[], byte[]> hash) {
    final List<byte[]> params = new ArrayList<>();
    params.add(key);

    for (final Entry<byte[], byte[]> entry : hash.entrySet()) {
      params.add(entry.getKey());
      params.add(entry.getValue());
    }
    sendCommand(Command.HMSET, params.toArray(new byte[params.size()][]));
  }

  public void hmget(final byte[] key, final byte[]... fields) {
    sendCommand(Command.HMGET, joinParameters(key, fields));
  }

  public void hincrBy(final byte[] key, final byte[] field, final long value) {
    sendCommand(Command.HINCRBY, key, field, Protocol.toByteArray(value));
  }

  public void hexists(final byte[] key, final byte[] field) {
    sendCommand(Command.HEXISTS, key, field);
  }

  public void hdel(final byte[] key, final byte[]... fields) {
    sendCommand(Command.HDEL, joinParameters(key, fields));
  }

  public void hlen(final byte[] key) {
    sendCommand(Command.HLEN, key);
  }

  public void hkeys(final byte[] key) {
    sendCommand(Command.HKEYS, key);
  }

  public void hvals(final byte[] key) {
    sendCommand(Command.HVALS, key);
  }

  public void hgetAll(final byte[] key) {
    sendCommand(Command.HGETALL, key);
  }

  public void hrandfield(final byte[] key) {
    sendCommand(Command.HRANDFIELD, key);
  }

  public void hrandfield(final byte[] key, final long count) {
    sendCommand(Command.HRANDFIELD, key, Protocol.toByteArray(count));
  }

  public void hrandfieldWithValues(final byte[] key, final long count) {
    sendCommand(Command.HRANDFIELD, key, Protocol.toByteArray(count), Keyword.WITHVALUES.getRaw());
  }

  public void rpush(final byte[] key, final byte[]... strings) {
    sendCommand(Command.RPUSH, joinParameters(key, strings));
  }

  public void lpush(final byte[] key, final byte[]... strings) {
    sendCommand(Command.LPUSH, joinParameters(key, strings));
  }

  public void llen(final byte[] key) {
    sendCommand(Command.LLEN, key);
  }

  public void lrange(final byte[] key, final long start, final long stop) {
    sendCommand(Command.LRANGE, key, Protocol.toByteArray(start), Protocol.toByteArray(stop));
  }

  public void ltrim(final byte[] key, final long start, final long stop) {
    sendCommand(Command.LTRIM, key, Protocol.toByteArray(start), Protocol.toByteArray(stop));
  }

  public void lindex(final byte[] key, final long index) {
    sendCommand(Command.LINDEX, key, Protocol.toByteArray(index));
  }

  public void lset(final byte[] key, final long index, final byte[] value) {
    sendCommand(Command.LSET, key, Protocol.toByteArray(index), value);
  }

  public void lrem(final byte[] key, final long count, final byte[] value) {
    sendCommand(Command.LREM, key, Protocol.toByteArray(count), value);
  }

  public void lpop(final byte[] key) {
    sendCommand(Command.LPOP, key);
  }

  public void lpop(final byte[] key, final int count) {
    sendCommand(Command.LPOP, key, Protocol.toByteArray(count));
  }

  public void lpos(final byte[] key, final byte[] element) {
    sendCommand(Command.LPOS, key, element);
  }

  public void lpos(final byte[] key, final byte[] element, LPosParams params) {
    sendCommand(Command.LPOS, joinParameters(key, element, params.getByteParams()));
  }

  public void lpos(final byte[] key, final byte[] element, final LPosParams params, final long count) {
    sendCommand(
      Command.LPOS,
      joinParameters(key, element, params.getByteParams(Keyword.COUNT.getRaw(), Protocol.toByteArray(count))));
  }

  public void rpop(final byte[] key) {
    sendCommand(Command.RPOP, key);
  }

  public void rpop(final byte[] key, final int count) {
    sendCommand(Command.RPOP, key, Protocol.toByteArray(count));
  }

  public void rpoplpush(final byte[] srckey, final byte[] dstkey) {
    sendCommand(Command.RPOPLPUSH, srckey, dstkey);
  }

  public void sadd(final byte[] key, final byte[]... members) {
    sendCommand(Command.SADD, joinParameters(key, members));
  }

  public void smembers(final byte[] key) {
    sendCommand(Command.SMEMBERS, key);
  }

  public void srem(final byte[] key, final byte[]... members) {
    sendCommand(Command.SREM, joinParameters(key, members));
  }

  public void spop(final byte[] key) {
    sendCommand(Command.SPOP, key);
  }

  public void spop(final byte[] key, final long count) {
    sendCommand(Command.SPOP, key, Protocol.toByteArray(count));
  }

  public void smove(final byte[] srckey, final byte[] dstkey, final byte[] member) {
    sendCommand(Command.SMOVE, srckey, dstkey, member);
  }

  public void scard(final byte[] key) {
    sendCommand(Command.SCARD, key);
  }

  public void sismember(final byte[] key, final byte[] member) {
    sendCommand(Command.SISMEMBER, key, member);
  }

  public void smismember(final byte[] key, final byte[]... members) {
    sendCommand(Command.SMISMEMBER, joinParameters(key, members));
  }

  public void sinter(final byte[]... keys) {
    sendCommand(Command.SINTER, keys);
  }

  public void sinterstore(final byte[] dstkey, final byte[]... keys) {
    sendCommand(Command.SINTERSTORE, joinParameters(dstkey, keys));
  }

  public void sunion(final byte[]... keys) {
    sendCommand(Command.SUNION, keys);
  }

  public void sunionstore(final byte[] dstkey, final byte[]... keys) {
    sendCommand(Command.SUNIONSTORE, joinParameters(dstkey, keys));
  }

  public void sdiff(final byte[]... keys) {
    sendCommand(Command.SDIFF, keys);
  }

  public void sdiffstore(final byte[] dstkey, final byte[]... keys) {
    sendCommand(Command.SDIFFSTORE, joinParameters(dstkey, keys));
  }

  public void srandmember(final byte[] key) {
    sendCommand(Command.SRANDMEMBER, key);
  }

  public void zadd(final byte[] key, final double score, final byte[] member) {
    sendCommand(Command.ZADD, key, Protocol.toByteArray(score), member);
  }

  public void zadd(final byte[] key, final double score, final byte[] member,
      final ZAddParams params) {
    sendCommand(Command.ZADD, params.getByteParams(key, Protocol.toByteArray(score), member));
  }

  public void zadd(final byte[] key, final Map<byte[], Double> scoreMembers) {
    ArrayList<byte[]> args = new ArrayList<>(scoreMembers.size() * 2 + 1);
    args.add(key);
    args.addAll(convertScoreMembersToByteArrays(scoreMembers));

    byte[][] argsArray = new byte[args.size()][];
    args.toArray(argsArray);

    sendCommand(Command.ZADD, argsArray);
  }

  public void zadd(final byte[] key, final Map<byte[], Double> scoreMembers, final ZAddParams params) {
    ArrayList<byte[]> args = convertScoreMembersToByteArrays(scoreMembers);
    byte[][] argsArray = new byte[args.size()][];
    args.toArray(argsArray);

    sendCommand(Command.ZADD, params.getByteParams(key, argsArray));
  }

  public void zdiff(final byte[]... keys) {
    sendCommand(Command.ZDIFF, joinParameters(Protocol.toByteArray(keys.length), keys));
  }

  public void zdiffWithScores(final byte[]... keys) {
    final List<byte[]> args = new ArrayList<>(keys.length + 2);
    args.add(Protocol.toByteArray(keys.length));
    Collections.addAll(args, keys);
    args.add(Keyword.WITHSCORES.getRaw());
    sendCommand(Command.ZDIFF, args.toArray(new byte[args.size()][]));
  }

  public void zaddIncr(final byte[] key, final double score, final byte[] member, final ZAddParams params) {
    sendCommand(Command.ZADD, params.getByteParams(key, Command.INCR.getRaw(), Protocol.toByteArray(score), member));
  }

  public void zdiffStore(final byte[] dstkey, final byte[]... keys) {
    sendCommand(Command.ZDIFFSTORE, joinParameters(dstkey, Protocol.toByteArray(keys.length), keys));
  }

  public void zrange(final byte[] key, final long start, final long stop) {
    sendCommand(Command.ZRANGE, key, Protocol.toByteArray(start), Protocol.toByteArray(stop));
  }

  public void zrem(final byte[] key, final byte[]... members) {
    sendCommand(Command.ZREM, joinParameters(key, members));
  }

  public void zincrby(final byte[] key, final double increment, final byte[] member) {
    sendCommand(Command.ZINCRBY, key, Protocol.toByteArray(increment), member);
  }

  public void zincrby(final byte[] key, final double increment, final byte[] member,
      final ZIncrByParams params) {
    // Note that it actually calls ZADD with INCR option, so it requires Redis 3.0.2 or upper.
    sendCommand(Command.ZADD, params.getByteParams(key, Protocol.toByteArray(increment), member));
  }

  public void zrank(final byte[] key, final byte[] member) {
    sendCommand(Command.ZRANK, key, member);
  }

  public void zrevrank(final byte[] key, final byte[] member) {
    sendCommand(Command.ZREVRANK, key, member);
  }

  public void zrevrange(final byte[] key, final long start, final long stop) {
    sendCommand(Command.ZREVRANGE, key, Protocol.toByteArray(start), Protocol.toByteArray(stop));
  }

  public void zrangeWithScores(final byte[] key, final long start, final long stop) {
    sendCommand(Command.ZRANGE, key, Protocol.toByteArray(start), Protocol.toByteArray(stop), Keyword.WITHSCORES.getRaw());
  }

  public void zrevrangeWithScores(final byte[] key, final long start, final long stop) {
    sendCommand(Command.ZREVRANGE, key, Protocol.toByteArray(start), Protocol.toByteArray(stop), Keyword.WITHSCORES.getRaw());
  }

  public void zrandmember(final byte[] key) {
    sendCommand(Command.ZRANDMEMBER, key);
  }

  public void zrandmember(final byte[] key, final long count) {
    sendCommand(Command.ZRANDMEMBER, key, Protocol.toByteArray(count));
  }

  public void zrandmemberWithScores(final byte[] key, final long count) {
    sendCommand(Command.ZRANDMEMBER, key, Protocol.toByteArray(count), Keyword.WITHSCORES.getRaw());
  }

  public void zcard(final byte[] key) {
    sendCommand(Command.ZCARD, key);
  }

  public void zscore(final byte[] key, final byte[] member) {
    sendCommand(Command.ZSCORE, key, member);
  }

  public void zmscore(final byte[] key, final byte[]... members) {
    sendCommand(Command.ZMSCORE, joinParameters(key, members));
  }

  public void zpopmax(final byte[] key) {
    sendCommand(Command.ZPOPMAX, key);
  }

  public void zpopmax(final byte[] key, final int count) {
    sendCommand(Command.ZPOPMAX, key, Protocol.toByteArray(count));
  }

  public void zpopmin(final byte[] key) {
    sendCommand(Command.ZPOPMIN, key);
  }

  public void zpopmin(final byte[] key, final long count) {
    sendCommand(Command.ZPOPMIN, key, Protocol.toByteArray(count));
  }

  public void multi() {
    sendCommand(Command.MULTI);
    isInMulti = true;
  }

  public void discard() {
    sendCommand(Command.DISCARD);
    isInMulti = false;
    isInWatch = false;
  }

  public void exec() {
    sendCommand(Command.EXEC);
    isInMulti = false;
    isInWatch = false;
  }

  public void watch(final byte[]... keys) {
    sendCommand(Command.WATCH, keys);
    isInWatch = true;
  }

  public void unwatch() {
    sendCommand(Command.UNWATCH);
    isInWatch = false;
  }

  public void sort(final byte[] key) {
    sendCommand(Command.SORT, key);
  }

  public void sort(final byte[] key, final SortingParams sortingParameters) {
    final List<byte[]> args = new ArrayList<>();
    args.add(key);
    args.addAll(sortingParameters.getParams());
    sendCommand(Command.SORT, args.toArray(new byte[args.size()][]));
  }

  public void sort(final byte[] key, final SortingParams sortingParameters, final byte[] dstkey) {
    final List<byte[]> args = new ArrayList<>();
    args.add(key);
    args.addAll(sortingParameters.getParams());
    args.add(Keyword.STORE.getRaw());
    args.add(dstkey);
    sendCommand(Command.SORT, args.toArray(new byte[args.size()][]));
  }

  public void sort(final byte[] key, final byte[] dstkey) {
    sendCommand(Command.SORT, key, Keyword.STORE.getRaw(), dstkey);
  }

  public void lmove(byte[] srcKey, byte[] dstKey, ListDirection from, ListDirection to) {
    sendCommand(Command.LMOVE, srcKey, dstKey, from.getRaw(), to.getRaw());
  }

  public void blmove(byte[] srcKey, byte[] dstKey, ListDirection from, ListDirection to, double timeout) {
    sendCommand(Command.BLMOVE, srcKey, dstKey, from.getRaw(), to.getRaw(), Protocol.toByteArray(timeout));
  }

  public void blpop(final byte[][] args) {
    sendCommand(Command.BLPOP, args);
  }

  public void blpop(final int timeout, final byte[]... keys) {
    blpop(getKeysAndTimeout(timeout, keys));
  }

  public void blpop(final double timeout, final byte[]... keys) {
    blpop(getKeysAndTimeout(timeout, keys));
  }

  public void brpop(final byte[][] args) {
    sendCommand(Command.BRPOP, args);
  }

  public void brpop(final int timeout, final byte[]... keys) {
    brpop(getKeysAndTimeout(timeout, keys));
  }

  public void brpop(final double timeout, final byte[]... keys) {
    brpop(getKeysAndTimeout(timeout, keys));
  }

  public void bzpopmax(final double timeout, final byte[]... keys) {
    sendCommand(Command.BZPOPMAX, getKeysAndTimeout(timeout, keys));
  }

  public void bzpopmin(final double timeout, final byte[]... keys) {
    sendCommand(Command.BZPOPMIN, getKeysAndTimeout(timeout, keys));
  }

  private static byte[][] getKeysAndTimeout(final int timeout, final byte[]... keys) {
    int numKeys = keys.length;
    byte[][] args = new byte[numKeys + 1][];
    System.arraycopy(keys, 0, args, 0, numKeys);
    args[numKeys] = Protocol.toByteArray(timeout);
    return args;
  }

  private static byte[][] getKeysAndTimeout(final double timeout, final byte[]... keys) {
    int numKeys = keys.length;
    byte[][] args = new byte[numKeys + 1][];
    System.arraycopy(keys, 0, args, 0, numKeys);
    args[numKeys] = Protocol.toByteArray(timeout);
    return args;
  }

  public void auth(final String password) {
    setPassword(password);
    sendCommand(Command.AUTH, password);
  }

  public void auth(final String user, final String password) {
    setUser(user);
    setPassword(password);
    sendCommand(Command.AUTH, user, password);
  }

  public void subscribe(final byte[]... channels) {
    sendCommand(Command.SUBSCRIBE, channels);
  }

  public void publish(final byte[] channel, final byte[] message) {
    sendCommand(Command.PUBLISH, channel, message);
  }

  public void unsubscribe() {
    sendCommand(Command.UNSUBSCRIBE);
  }

  public void unsubscribe(final byte[]... channels) {
    sendCommand(Command.UNSUBSCRIBE, channels);
  }

  public void psubscribe(final byte[]... patterns) {
    sendCommand(Command.PSUBSCRIBE, patterns);
  }

  public void punsubscribe() {
    sendCommand(Command.PUNSUBSCRIBE);
  }

  public void punsubscribe(final byte[]... patterns) {
    sendCommand(Command.PUNSUBSCRIBE, patterns);
  }

  public void pubsub(final byte[]... args) {
    sendCommand(Command.PUBSUB, args);
  }

  public void zcount(final byte[] key, final double min, final double max) {
    sendCommand(Command.ZCOUNT, key, Protocol.toByteArray(min), Protocol.toByteArray(max));
  }

  public void zcount(final byte[] key, final byte[] min, final byte[] max) {
    sendCommand(Command.ZCOUNT, key, min, max);
  }

  public void zrangeByScore(final byte[] key, final double min, final double max) {
    sendCommand(Command.ZRANGEBYSCORE, key, Protocol.toByteArray(min), Protocol.toByteArray(max));
  }

  public void zrangeByScore(final byte[] key, final byte[] min, final byte[] max) {
    sendCommand(Command.ZRANGEBYSCORE, key, min, max);
  }

  public void zrevrangeByScore(final byte[] key, final double max, final double min) {
    sendCommand(Command.ZREVRANGEBYSCORE, key, Protocol.toByteArray(max), Protocol.toByteArray(min));
  }

  public void zrevrangeByScore(final byte[] key, final byte[] max, final byte[] min) {
    sendCommand(Command.ZREVRANGEBYSCORE, key, max, min);
  }

  public void zrangeByScore(final byte[] key, final double min, final double max, final int offset,
      final int count) {
    sendCommand(Command.ZRANGEBYSCORE, key, Protocol.toByteArray(min), Protocol.toByteArray(max), Keyword.LIMIT.getRaw(),
      Protocol.toByteArray(offset), Protocol.toByteArray(count));
  }

  public void zrevrangeByScore(final byte[] key, final double max, final double min,
      final int offset, final int count) {
    sendCommand(Command.ZREVRANGEBYSCORE, key, Protocol.toByteArray(max), Protocol.toByteArray(min), Keyword.LIMIT.getRaw(),
      Protocol.toByteArray(offset), Protocol.toByteArray(count));
  }

  public void zrangeByScoreWithScores(final byte[] key, final double min, final double max) {
    sendCommand(Command.ZRANGEBYSCORE, key, Protocol.toByteArray(min), Protocol.toByteArray(max), Keyword.WITHSCORES.getRaw());
  }

  public void zrevrangeByScoreWithScores(final byte[] key, final double max, final double min) {
    sendCommand(Command.ZREVRANGEBYSCORE, key, Protocol.toByteArray(max), Protocol.toByteArray(min), Keyword.WITHSCORES.getRaw());
  }

  public void zrangeByScoreWithScores(final byte[] key, final double min, final double max,
      final int offset, final int count) {
    sendCommand(Command.ZRANGEBYSCORE, key, Protocol.toByteArray(min), Protocol.toByteArray(max), Keyword.LIMIT.getRaw(),
      Protocol.toByteArray(offset), Protocol.toByteArray(count), Keyword.WITHSCORES.getRaw());
  }

  public void zrevrangeByScoreWithScores(final byte[] key, final double max, final double min,
      final int offset, final int count) {
    sendCommand(Command.ZREVRANGEBYSCORE, key, Protocol.toByteArray(max), Protocol.toByteArray(min), Keyword.LIMIT.getRaw(),
      Protocol.toByteArray(offset), Protocol.toByteArray(count), Keyword.WITHSCORES.getRaw());
  }

  public void zrangeByScore(final byte[] key, final byte[] min, final byte[] max, final int offset,
      final int count) {
    sendCommand(Command.ZRANGEBYSCORE, key, min, max, Keyword.LIMIT.getRaw(), Protocol.toByteArray(offset),
      Protocol.toByteArray(count));
  }

  public void zrevrangeByScore(final byte[] key, final byte[] max, final byte[] min,
      final int offset, final int count) {
    sendCommand(
        Command.ZREVRANGEBYSCORE, key, max, min, Keyword.LIMIT.getRaw(), Protocol.toByteArray(offset),
      Protocol.toByteArray(count));
  }

  public void zrangeByScoreWithScores(final byte[] key, final byte[] min, final byte[] max) {
    sendCommand(Command.ZRANGEBYSCORE, key, min, max, Keyword.WITHSCORES.getRaw());
  }

  public void zrevrangeByScoreWithScores(final byte[] key, final byte[] max, final byte[] min) {
    sendCommand(Command.ZREVRANGEBYSCORE, key, max, min, Keyword.WITHSCORES.getRaw());
  }

  public void zrangeByScoreWithScores(final byte[] key, final byte[] min, final byte[] max,
      final int offset, final int count) {
    sendCommand(Command.ZRANGEBYSCORE, key, min, max, Keyword.LIMIT.getRaw(), Protocol.toByteArray(offset),
      Protocol.toByteArray(count), Keyword.WITHSCORES.getRaw());
  }

  public void zrevrangeByScoreWithScores(final byte[] key, final byte[] max, final byte[] min,
      final int offset, final int count) {
    sendCommand(
        Command.ZREVRANGEBYSCORE, key, max, min, Keyword.LIMIT.getRaw(), Protocol.toByteArray(offset),
      Protocol.toByteArray(count), Keyword.WITHSCORES.getRaw());
  }

  public void zremrangeByRank(final byte[] key, final long start, final long stop) {
    sendCommand(Command.ZREMRANGEBYRANK, key, Protocol.toByteArray(start), Protocol.toByteArray(stop));
  }

  public void zremrangeByScore(final byte[] key, final double min, final double max) {
    sendCommand(Command.ZREMRANGEBYSCORE, key, Protocol.toByteArray(min), Protocol.toByteArray(max));
  }

  public void zremrangeByScore(final byte[] key, final byte[] min, final byte[] max) {
    sendCommand(Command.ZREMRANGEBYSCORE, key, min, max);
  }

  public void zunion(final ZParams params, final byte[]... keys) {
    sendCommand(Command.ZUNION, buildByteZParams(params, false, keys));
  }

  public void zunionWithScores(final ZParams params, final byte[]... keys) {
    sendCommand(Command.ZUNION, buildByteZParams(params, true, keys));
  }

  private byte[][] buildByteZParams(final ZParams params, final boolean withScores, final byte[]... keys) {
    final List<byte[]> args = new ArrayList<>();
    args.add(Protocol.toByteArray(keys.length));
    Collections.addAll(args, keys);

    args.addAll(params.getParams());
    if (withScores) {
      args.add(Keyword.WITHSCORES.getRaw());
    }
    return args.toArray(new byte[args.size()][]);
  }

  public void zunionstore(final byte[] dstkey, final byte[]... sets) {
    sendCommand(Command.ZUNIONSTORE, joinParameters(dstkey, Protocol.toByteArray(sets.length), sets));
  }

  public void zunionstore(final byte[] dstkey, final ZParams params, final byte[]... sets) {
    final List<byte[]> args = new ArrayList<>();
    args.add(dstkey);
    args.add(Protocol.toByteArray(sets.length));
    Collections.addAll(args, sets);

    args.addAll(params.getParams());
    sendCommand(Command.ZUNIONSTORE, args.toArray(new byte[args.size()][]));
  }

  public void zinter(final ZParams params, final byte[]... keys) {
    sendCommand(Command.ZINTER, buildByteZParams(params, false, keys));
  }

  public void zinterWithScores(final ZParams params, final byte[]... keys) {
    sendCommand(Command.ZINTER, buildByteZParams(params, true, keys));
  }

  public void zinterstore(final byte[] dstkey, final byte[]... sets) {
    sendCommand(Command.ZINTERSTORE, joinParameters(dstkey, Protocol.toByteArray(sets.length), sets));
  }

  public void zinterstore(final byte[] dstkey, final ZParams params, final byte[]... sets) {
    final List<byte[]> args = new ArrayList<>();
    args.add(dstkey);
    args.add(Protocol.toByteArray(sets.length));
    Collections.addAll(args, sets);

    args.addAll(params.getParams());
    sendCommand(Command.ZINTERSTORE, args.toArray(new byte[args.size()][]));
  }

  public void zlexcount(final byte[] key, final byte[] min, final byte[] max) {
    sendCommand(Command.ZLEXCOUNT, key, min, max);
  }

  public void zrangeByLex(final byte[] key, final byte[] min, final byte[] max) {
    sendCommand(Command.ZRANGEBYLEX, key, min, max);
  }

  public void zrangeByLex(final byte[] key, final byte[] min, final byte[] max, final int offset,
      final int count) {
    sendCommand(Command.ZRANGEBYLEX, key, min, max, Keyword.LIMIT.getRaw(), Protocol.toByteArray(offset), Protocol.toByteArray(count));
  }

  public void zrevrangeByLex(final byte[] key, final byte[] max, final byte[] min) {
    sendCommand(Command.ZREVRANGEBYLEX, key, max, min);
  }

  public void zrevrangeByLex(final byte[] key, final byte[] max, final byte[] min,
      final int offset, final int count) {
    sendCommand(Command.ZREVRANGEBYLEX, key, max, min, Keyword.LIMIT.getRaw(), Protocol.toByteArray(offset),
      Protocol.toByteArray(count));
  }

  public void zremrangeByLex(final byte[] key, final byte[] min, final byte[] max) {
    sendCommand(Command.ZREMRANGEBYLEX, key, min, max);
  }

  public void save() {
    sendCommand(Command.SAVE);
  }

  public void bgsave() {
    sendCommand(Command.BGSAVE);
  }

  public void bgrewriteaof() {
    sendCommand(Command.BGREWRITEAOF);
  }

  public void lastsave() {
    sendCommand(Command.LASTSAVE);
  }

  public void shutdown() {
    sendCommand(Command.SHUTDOWN);
  }

  public void shutdown(SaveMode saveMode) {
    if (saveMode == null) {
      sendCommand(Command.SHUTDOWN);
    } else {
      sendCommand(Command.SHUTDOWN, saveMode.getRaw());
    }
  }

  public void info() {
    sendCommand(Command.INFO);
  }

  public void info(final String section) {
    sendCommand(Command.INFO, section);
  }

  public void monitor() {
    sendCommand(Command.MONITOR);
  }

  public void slaveof(final String host, final int port) {
    sendCommand(Command.SLAVEOF, host, String.valueOf(port));
  }

  public void slaveofNoOne() {
    sendCommand(Command.SLAVEOF, Keyword.NO.getRaw(), Keyword.ONE.getRaw());
  }

  public void role() {
    sendCommand(Command.ROLE);
  }

  public void configGet(final byte[] pattern) {
    sendCommand(Command.CONFIG, Keyword.GET.getRaw(), pattern);
  }

  public void configSet(final byte[] parameter, final byte[] value) {
    sendCommand(Command.CONFIG, Keyword.SET.getRaw(), parameter, value);
  }

  public void strlen(final byte[] key) {
    sendCommand(Command.STRLEN, key);
  }

  public void strAlgoLCSKeys(final byte[] keyA, final byte[] keyB, final StrAlgoLCSParams params) {
    sendCommand(Command.STRALGO, params.getByteParams(Keyword.KEYS, keyA, keyB));
  }

  public void strAlgoLCSStrings(final byte[] strA, final byte[] strB, final StrAlgoLCSParams params) {
    sendCommand(Command.STRALGO, params.getByteParams(Keyword.STRINGS, strA, strB));
  }

  /**
   * @deprecated This method will be removed in next major release.
   */
  @Deprecated
  public void sync() {
    sendCommand(Command.SYNC);
  }

  public void lpushx(final byte[] key, final byte[]... string) {
    sendCommand(Command.LPUSHX, joinParameters(key, string));
  }

  public void persist(final byte[] key) {
    sendCommand(Command.PERSIST, key);
  }

  public void rpushx(final byte[] key, final byte[]... string) {
    sendCommand(Command.RPUSHX, joinParameters(key, string));
  }

  public void echo(final byte[] string) {
    sendCommand(Command.ECHO, string);
  }

  public void linsert(final byte[] key, final ListPosition where, final byte[] pivot,
      final byte[] value) {
    sendCommand(Command.LINSERT, key, where.raw, pivot, value);
  }

  /**
   * @deprecated This method will be removed in next major release.
   */
  @Deprecated
  public void debug(final DebugParams params) {
    sendCommand(Command.DEBUG, params.getCommand());
  }

  public void brpoplpush(final byte[] source, final byte[] destination, final int timeout) {
    sendCommand(Command.BRPOPLPUSH, source, destination, Protocol.toByteArray(timeout));
  }

  public void configResetStat() {
    sendCommand(Command.CONFIG, Keyword.RESETSTAT.getRaw());
  }

  public void configRewrite() {
    sendCommand(Command.CONFIG, Keyword.REWRITE.getRaw());
  }

  public void setbit(final byte[] key, final long offset, final byte[] value) {
    sendCommand(Command.SETBIT, key, Protocol.toByteArray(offset), value);
  }

  public void setbit(final byte[] key, final long offset, final boolean value) {
    sendCommand(Command.SETBIT, key, Protocol.toByteArray(offset), Protocol.toByteArray(value));
  }

  public void getbit(final byte[] key, final long offset) {
    sendCommand(Command.GETBIT, key, Protocol.toByteArray(offset));
  }

  public void bitpos(final byte[] key, final boolean value, final BitPosParams params) {
    final List<byte[]> args = new ArrayList<>();
    args.add(key);
    args.add(Protocol.toByteArray(value));
    args.addAll(params.getParams());
    sendCommand(Command.BITPOS, args.toArray(new byte[args.size()][]));
  }

  public void setrange(final byte[] key, final long offset, final byte[] value) {
    sendCommand(Command.SETRANGE, key, Protocol.toByteArray(offset), value);
  }

  public void getrange(final byte[] key, final long startOffset, final long endOffset) {
    sendCommand(Command.GETRANGE, key, Protocol.toByteArray(startOffset), Protocol.toByteArray(endOffset));
  }

  public void eval(final byte[] script, final byte[] keyCount, final byte[][] params) {
    sendCommand(Command.EVAL, joinParameters(script, keyCount, params));
  }

  public void eval(final byte[] script, final int keyCount, final byte[]... params) {
    sendCommand(Command.EVAL, joinParameters(script, Protocol.toByteArray(keyCount), params));
  }

  public void evalsha(final byte[] sha1, final byte[] keyCount, final byte[]... params) {
    sendCommand(Command.EVALSHA, joinParameters(sha1, keyCount, params));
  }

  public void evalsha(final byte[] sha1, final int keyCount, final byte[]... params) {
    sendCommand(Command.EVALSHA, joinParameters(sha1, Protocol.toByteArray(keyCount), params));
  }

  public void scriptFlush() {
    sendCommand(Command.SCRIPT, Keyword.FLUSH.getRaw());
  }

  public void scriptFlush(FlushMode flushMode) {
    sendCommand(Command.SCRIPT, Keyword.FLUSH.getRaw(), flushMode.getRaw());
  }

  public void scriptExists(final byte[]... sha1) {
    sendCommand(Command.SCRIPT, joinParameters(Keyword.EXISTS.getRaw(), sha1));
  }

  public void scriptLoad(final byte[] script) {
    sendCommand(Command.SCRIPT, Keyword.LOAD.getRaw(), script);
  }

  public void scriptKill() {
    sendCommand(Command.SCRIPT, Keyword.KILL.getRaw());
  }

  public void slowlogGet() {
    sendCommand(Command.SLOWLOG, Keyword.GET.getRaw());
  }

  public void slowlogGet(final long entries) {
    sendCommand(Command.SLOWLOG, Keyword.GET.getRaw(), Protocol.toByteArray(entries));
  }

  public void slowlogReset() {
    sendCommand(Command.SLOWLOG, Keyword.RESET.getRaw());
  }

  public void slowlogLen() {
    sendCommand(Command.SLOWLOG, Keyword.LEN.getRaw());
  }

  public void objectRefcount(final byte[] key) {
    sendCommand(Command.OBJECT, Keyword.REFCOUNT.getRaw(), key);
  }

  public void objectIdletime(final byte[] key) {
    sendCommand(Command.OBJECT, Keyword.IDLETIME.getRaw(), key);
  }

  public void objectEncoding(final byte[] key) {
    sendCommand(Command.OBJECT, Keyword.ENCODING.getRaw(), key);
  }

  public void objectHelp() {
    sendCommand(Command.OBJECT, Keyword.HELP.getRaw());
  }

  public void objectFreq(final byte[] key) {
    sendCommand(Command.OBJECT, Keyword.FREQ.getRaw(), key);
  }

  public void bitcount(final byte[] key) {
    sendCommand(Command.BITCOUNT, key);
  }

  public void bitcount(final byte[] key, final long start, final long end) {
    sendCommand(Command.BITCOUNT, key, Protocol.toByteArray(start), Protocol.toByteArray(end));
  }

  public void bitop(final BitOP op, final byte[] destKey, final byte[]... srcKeys) {
    sendCommand(Command.BITOP, joinParameters(op.raw, destKey, srcKeys));
  }

  public void sentinel(final byte[]... args) {
    sendCommand(Command.SENTINEL, args);
  }

  public void sentinel(SentinelKeyword subcommand, final byte[]... args) {
    sendCommand(Command.SENTINEL, joinParameters(subcommand.getRaw(), args));
  }

  public void sentinel(SentinelKeyword subcommand) {
    sendCommand(Command.SENTINEL, subcommand.getRaw());
  }

  public void dump(final byte[] key) {
    sendCommand(Command.DUMP, key);
  }

  /**
   * @deprecated Use {@link #restore(byte[], long, byte[])}.
   */
  @Deprecated
  public void restore(final byte[] key, final int ttl, final byte[] serializedValue) {
    sendCommand(Command.RESTORE, key, Protocol.toByteArray(ttl), serializedValue);
  }

  public void restore(final byte[] key, final long ttl, final byte[] serializedValue) {
    sendCommand(Command.RESTORE, key, Protocol.toByteArray(ttl), serializedValue);
  }

  /**
   * @deprecated Use {@link #restore(byte[], long, byte[], RestoreParams)}.
   */
  @Deprecated
  public void restoreReplace(final byte[] key, final int ttl, final byte[] serializedValue) {
    sendCommand(Command.RESTORE, key, Protocol.toByteArray(ttl), serializedValue, Keyword.REPLACE.getRaw());
  }

  /**
   * @deprecated Use {@link #restore(byte[], long, byte[], RestoreParams)}.
   */
  @Deprecated
  public void restoreReplace(final byte[] key, final long ttl, final byte[] serializedValue) {
    sendCommand(Command.RESTORE, key, Protocol.toByteArray(ttl), serializedValue, Keyword.REPLACE.getRaw());
  }

  public void restore(final byte[] key, final long ttl, final byte[] serializedValue, final RestoreParams params) {
    if (params == null) {
      sendCommand(Command.RESTORE, key, Protocol.toByteArray(ttl), serializedValue);
    } else {
      sendCommand(
          Command.RESTORE, params.getByteParams(key, Protocol.toByteArray(ttl), serializedValue));
    }
  }

  public void pexpire(final byte[] key, final long milliseconds) {
    sendCommand(Command.PEXPIRE, key, Protocol.toByteArray(milliseconds));
  }

  public void pexpireAt(final byte[] key, final long millisecondsTimestamp) {
    sendCommand(Command.PEXPIREAT, key, Protocol.toByteArray(millisecondsTimestamp));
  }

  public void pttl(final byte[] key) {
    sendCommand(Command.PTTL, key);
  }

  public void psetex(final byte[] key, final long milliseconds, final byte[] value) {
    sendCommand(Command.PSETEX, key, Protocol.toByteArray(milliseconds), value);
  }

  public void srandmember(final byte[] key, final int count) {
    sendCommand(Command.SRANDMEMBER, key, Protocol.toByteArray(count));
  }

  public void memoryDoctor() {
    sendCommand(Command.MEMORY, Keyword.DOCTOR.getRaw());
  }

  public void memoryUsage(final byte[] key) {
    sendCommand(Command.MEMORY, Keyword.USAGE.getRaw(), key);
  }

  public void memoryUsage(final byte[] key, final int samples) {
    sendCommand(
        Command.MEMORY, Keyword.USAGE.getRaw(), key, Keyword.SAMPLES.getRaw(), Protocol.toByteArray(samples));
  }

  public void failover(FailoverParams failoverParams) {
    if (failoverParams == null) {
      sendCommand(Command.FAILOVER);
    } else {
      sendCommand(Command.FAILOVER, failoverParams.getByteParams());
    }
  }

  public void failoverAbort() {
    sendCommand(Command.FAILOVER, Keyword.ABORT.getRaw());
  }

  public void clientKill(final byte[] ipPort) {
    sendCommand(Command.CLIENT, Keyword.KILL.getRaw(), ipPort);
  }

  public void clientKill(final String ip, final int port) {
    sendCommand(Command.CLIENT, Keyword.KILL.name(), ip + ':' + port);
  }

  public void clientKill(ClientKillParams params) {
    sendCommand(Command.CLIENT, joinParameters(Keyword.KILL.getRaw(), params.getByteParams()));
  }

  public void clientGetname() {
    sendCommand(Command.CLIENT, Keyword.GETNAME.getRaw());
  }

  public void clientList() {
    sendCommand(Command.CLIENT, Keyword.LIST.getRaw());
  }

  public void clientList(ClientType type) {
    sendCommand(Command.CLIENT, Keyword.LIST.getRaw(), Keyword.TYPE.getRaw(), type.getRaw());
  }

  public void clientList(final long... clientIds) {
    final byte[][] params = new byte[2 + clientIds.length][];
    int index = 0;
    params[index++] = Keyword.LIST.getRaw();
    params[index++] = Keyword.ID.getRaw();
    for (final long clientId : clientIds) {
      params[index++] = Protocol.toByteArray(clientId);
    }
    sendCommand(Command.CLIENT, params);
  }

  public void clientInfo() {
    sendCommand(Command.CLIENT, Command.INFO.getRaw());
  }

  public void clientSetname(final byte[] name) {
    sendCommand(Command.CLIENT, Keyword.SETNAME.getRaw(), name);
  }

  public void clientPause(final long timeout) {
    sendCommand(Command.CLIENT, Keyword.PAUSE.getRaw(), Protocol.toByteArray(timeout));
  }

  public void clientId() {
    sendCommand(Command.CLIENT, Keyword.ID.getRaw());
  }

  public void clientUnblock(final long clientId, final UnblockType unblockType) {
    if (unblockType == null) {
      sendCommand(Command.CLIENT, Keyword.UNBLOCK.getRaw(), Protocol.toByteArray(clientId));
    } else {
      sendCommand(Command.CLIENT, Keyword.UNBLOCK.getRaw(), Protocol.toByteArray(clientId), unblockType.getRaw());
    }
  }

  public void clientPause(final long timeout, final ClientPauseMode mode) {
    sendCommand(Command.CLIENT, Keyword.PAUSE.getRaw(), Protocol.toByteArray(timeout), mode.getRaw());
  }

  public void time() {
    sendCommand(Command.TIME);
  }

  public void migrate(final String host, final int port, final byte[] key, final int destinationDb,
      final int timeout) {
    sendCommand(Command.MIGRATE, SafeEncoder.encode(host), Protocol.toByteArray(port), key,
      Protocol.toByteArray(destinationDb), Protocol.toByteArray(timeout));
  }

  public void migrate(final String host, final int port, final int destinationDB,
      final int timeout, final MigrateParams params, final byte[]... keys) {
    byte[][] bparams = params.getByteParams();
    int len = 5 + bparams.length + 1 + keys.length;
    byte[][] args = new byte[len][];
    int i = 0;
    args[i++] = SafeEncoder.encode(host);
    args[i++] = Protocol.toByteArray(port);
    args[i++] = new byte[0];
    args[i++] = Protocol.toByteArray(destinationDB);
    args[i++] = Protocol.toByteArray(timeout);
    System.arraycopy(bparams, 0, args, i, bparams.length);
    i += bparams.length;
    args[i++] = Keyword.KEYS.getRaw();
    System.arraycopy(keys, 0, args, i, keys.length);
    sendCommand(Command.MIGRATE, args);
  }

  public void hincrByFloat(final byte[] key, final byte[] field, final double increment) {
    sendCommand(Command.HINCRBYFLOAT, key, field, Protocol.toByteArray(increment));
  }

  public void scan(final byte[] cursor, final ScanParams params) {
    scan(cursor, params, (byte[]) null);
  }

  public void scan(final byte[] cursor, final ScanParams params, final byte[] type) {
    final List<byte[]> args = new ArrayList<>();
    args.add(cursor);
    args.addAll(params.getParams());
    if (type != null) {
      args.add(Keyword.TYPE.getRaw());
      args.add(type);
    }
    sendCommand(Command.SCAN, args.toArray(new byte[args.size()][]));
  }

  public void hscan(final byte[] key, final byte[] cursor, final ScanParams params) {
    final List<byte[]> args = new ArrayList<>();
    args.add(key);
    args.add(cursor);
    args.addAll(params.getParams());
    sendCommand(Command.HSCAN, args.toArray(new byte[args.size()][]));
  }

  public void sscan(final byte[] key, final byte[] cursor, final ScanParams params) {
    final List<byte[]> args = new ArrayList<>();
    args.add(key);
    args.add(cursor);
    args.addAll(params.getParams());
    sendCommand(Command.SSCAN, args.toArray(new byte[args.size()][]));
  }

  public void zscan(final byte[] key, final byte[] cursor, final ScanParams params) {
    final List<byte[]> args = new ArrayList<>();
    args.add(key);
    args.add(cursor);
    args.addAll(params.getParams());
    sendCommand(Command.ZSCAN, args.toArray(new byte[args.size()][]));
  }

  public void waitReplicas(final int replicas, final long timeout) {
    sendCommand(Command.WAIT, Protocol.toByteArray(replicas), Protocol.toByteArray(timeout));
  }

  public void cluster(final byte[]... args) {
    sendCommand(Command.CLUSTER, args);
  }

  public void cluster(Protocol.ClusterKeyword keyword, final byte[]... args) {
    sendCommand(Command.CLUSTER, joinParameters(keyword.getRaw(), args));
  }

  public void asking() {
    sendCommand(Command.ASKING);
  }

  public void pfadd(final byte[] key, final byte[]... elements) {
    sendCommand(Command.PFADD, joinParameters(key, elements));
  }

  public void pfcount(final byte[] key) {
    sendCommand(Command.PFCOUNT, key);
  }

  public void pfcount(final byte[]... keys) {
    sendCommand(Command.PFCOUNT, keys);
  }

  public void pfmerge(final byte[] destkey, final byte[]... sourcekeys) {
    sendCommand(Command.PFMERGE, joinParameters(destkey, sourcekeys));
  }

  public void readonly() {
    sendCommand(Command.READONLY);
  }

  public void readwrite() {
    sendCommand(Command.READWRITE);
  }

  public void geoadd(final byte[] key, final double longitude, final double latitude,
      final byte[] member) {
    sendCommand(Command.GEOADD, key, Protocol.toByteArray(longitude), Protocol.toByteArray(latitude), member);
  }

  public void geoadd(final byte[] key, final Map<byte[], GeoCoordinate> memberCoordinateMap) {
    geoadd(key, GeoAddParams.geoAddParams(), memberCoordinateMap);
  }

  public void geoadd(final byte[] key, final GeoAddParams params, final Map<byte[], GeoCoordinate> memberCoordinateMap) {
    List<byte[]> args = new ArrayList<>(memberCoordinateMap.size() * 3);
    args.addAll(convertGeoCoordinateMapToByteArrays(memberCoordinateMap));

    byte[][] argsArray = new byte[args.size()][];
    args.toArray(argsArray);

    sendCommand(Command.GEOADD, params.getByteParams(key, argsArray));
  }

  public void geodist(final byte[] key, final byte[] member1, final byte[] member2) {
    sendCommand(Command.GEODIST, key, member1, member2);
  }

  public void geodist(final byte[] key, final byte[] member1, final byte[] member2,
      final GeoUnit unit) {
    sendCommand(Command.GEODIST, key, member1, member2, unit.raw);
  }

  public void geohash(final byte[] key, final byte[]... members) {
    sendCommand(Command.GEOHASH, joinParameters(key, members));
  }

  public void geopos(final byte[] key, final byte[][] members) {
    sendCommand(Command.GEOPOS, joinParameters(key, members));
  }

  public void georadius(final byte[] key, final double longitude, final double latitude,
      final double radius, final GeoUnit unit) {
    sendCommand(Command.GEORADIUS, key, Protocol.toByteArray(longitude), Protocol.toByteArray(latitude), Protocol.toByteArray(radius),
      unit.raw);
  }

  public void georadiusReadonly(final byte[] key, final double longitude, final double latitude,
      final double radius, final GeoUnit unit) {
    sendCommand(Command.GEORADIUS_RO, key, Protocol.toByteArray(longitude), Protocol.toByteArray(latitude),
      Protocol.toByteArray(radius), unit.raw);
  }

  public void georadius(final byte[] key, final double longitude, final double latitude,
      final double radius, final GeoUnit unit, final GeoRadiusParam param) {
    sendCommand(
        Command.GEORADIUS, param.getByteParams(key, Protocol.toByteArray(longitude), Protocol.toByteArray(latitude),
      Protocol.toByteArray(radius), unit.raw));
  }

  public void georadiusStore(final byte[] key, final double longitude, final double latitude,
      final double radius, final GeoUnit unit, final GeoRadiusParam param,
      final GeoRadiusStoreParam storeParam) {
    sendCommand(
        Command.GEORADIUS, param.getByteParams(key, Protocol.toByteArray(longitude), Protocol.toByteArray(latitude),
      Protocol.toByteArray(radius), unit.raw, storeParam.getOption(), storeParam.getKey()));
  }

  public void georadiusReadonly(final byte[] key, final double longitude, final double latitude,
      final double radius, final GeoUnit unit, final GeoRadiusParam param) {
    sendCommand(Command.GEORADIUS_RO, param.getByteParams(key, Protocol.toByteArray(longitude),
      Protocol.toByteArray(latitude), Protocol.toByteArray(radius), unit.raw));
  }

  public void georadiusByMember(final byte[] key, final byte[] member, final double radius,
      final GeoUnit unit) {
    sendCommand(Command.GEORADIUSBYMEMBER, key, member, Protocol.toByteArray(radius), unit.raw);
  }

  public void georadiusByMemberReadonly(final byte[] key, final byte[] member, final double radius,
      final GeoUnit unit) {
    sendCommand(Command.GEORADIUSBYMEMBER_RO, key, member, Protocol.toByteArray(radius), unit.raw);
  }

  public void georadiusByMember(final byte[] key, final byte[] member, final double radius,
      final GeoUnit unit, final GeoRadiusParam param) {
    sendCommand(Command.GEORADIUSBYMEMBER, param.getByteParams(key, member, Protocol.toByteArray(radius), unit.raw));
  }

  public void georadiusByMemberStore(final byte[] key, final byte[] member, final double radius,
      final GeoUnit unit, final GeoRadiusParam param, final GeoRadiusStoreParam storeParam) {
    sendCommand(Command.GEORADIUSBYMEMBER, param.getByteParams(key, member, Protocol.toByteArray(radius), unit.raw,
      storeParam.getOption(), storeParam.getKey()));
  }

  public void georadiusByMemberReadonly(final byte[] key, final byte[] member, final double radius,
      final GeoUnit unit, final GeoRadiusParam param) {
    sendCommand(Command.GEORADIUSBYMEMBER_RO,
      param.getByteParams(key, member, Protocol.toByteArray(radius), unit.raw));
  }

  public void moduleLoad(final byte[] path) {
    sendCommand(Command.MODULE, Keyword.LOAD.getRaw(), path);
  }

  public void moduleList() {
    sendCommand(Command.MODULE, Keyword.LIST.getRaw());
  }

  public void moduleUnload(final byte[] name) {
    sendCommand(Command.MODULE, Keyword.UNLOAD.getRaw(), name);
  }

  private ArrayList<byte[]> convertScoreMembersToByteArrays(final Map<byte[], Double> scoreMembers) {
    ArrayList<byte[]> args = new ArrayList<>(scoreMembers.size() * 2);

    for (Map.Entry<byte[], Double> entry : scoreMembers.entrySet()) {
      args.add(Protocol.toByteArray(entry.getValue()));
      args.add(entry.getKey());
    }

    return args;
  }

  public void aclWhoAmI() {
    sendCommand(Command.ACL, Keyword.WHOAMI.getRaw());
  }

  public void aclGenPass() {
    sendCommand(Command.ACL, Keyword.GENPASS.getRaw());
  }

  public void aclList() {
    sendCommand(Command.ACL, Keyword.LIST.getRaw());
  }

  public void aclUsers() {
    sendCommand(Command.ACL, Keyword.USERS.getRaw());
  }

  public void aclCat() {
    sendCommand(Command.ACL, Keyword.CAT.getRaw());
  }

  public void aclCat(final byte[] category) {
    sendCommand(Command.ACL, Keyword.CAT.getRaw(), category);
  }

  public void aclLog() {
    sendCommand(Command.ACL, Keyword.LOG.getRaw());
  }

  public void aclLog(int limit) {
    sendCommand(Command.ACL, Keyword.LOG.getRaw(), Protocol.toByteArray(limit));
  }

  public void aclLog(final byte[] option) {
    sendCommand(Command.ACL, Keyword.LOG.getRaw(), option);
  }

  public void aclLogReset() {
    sendCommand(Command.ACL, Keyword.LOG.getRaw(), Keyword.RESET.getRaw());
  }

  public void aclSetUser(final byte[] name) {
    sendCommand(Command.ACL, Keyword.SETUSER.getRaw(), name);
  }

  public void aclGetUser(final byte[] name) {
    sendCommand(Command.ACL, Keyword.GETUSER.getRaw(), name);
  }

  public void aclSetUser(final byte[] name, byte[][] parameters) {
    sendCommand(Command.ACL, joinParameters(Keyword.SETUSER.getRaw(), name, parameters));
  }

  public void aclDelUser(final byte[] name) {
    sendCommand(Command.ACL, Keyword.DELUSER.getRaw(), name);
  }

  public void aclLoad() {
    sendCommand(Command.ACL, Keyword.LOAD.getRaw());
  }

  public void aclSave() {
    sendCommand(Command.ACL, Keyword.SAVE.getRaw());
  }

  private List<byte[]> convertGeoCoordinateMapToByteArrays(
      final Map<byte[], GeoCoordinate> memberCoordinateMap) {
    List<byte[]> args = new ArrayList<>(memberCoordinateMap.size() * 3);

    for (Entry<byte[], GeoCoordinate> entry : memberCoordinateMap.entrySet()) {
      GeoCoordinate coordinate = entry.getValue();
      args.add(Protocol.toByteArray(coordinate.getLongitude()));
      args.add(Protocol.toByteArray(coordinate.getLatitude()));
      args.add(entry.getKey());
    }

    return args;
  }

  public void bitfield(final byte[] key, final byte[]... value) {
    sendCommand(Command.BITFIELD, joinParameters(key, value));
  }

  public void bitfieldReadonly(final byte[] key, final byte[]... arguments) {
    sendCommand(Command.BITFIELD_RO, joinParameters(key, arguments));
  }

  public void hstrlen(final byte[] key, final byte[] field) {
    sendCommand(Command.HSTRLEN, key, field);
  }

  public void xadd(final byte[] key, final byte[] id, final Map<byte[], byte[]> hash, long maxLen,
      boolean approximateLength) {
    int maxLexArgs = 0;
    if (maxLen < Long.MAX_VALUE) { // optional arguments
      if (approximateLength) {
        maxLexArgs = 3; // e.g. MAXLEN ~ 1000
      } else {
        maxLexArgs = 2; // e.g. MAXLEN 1000
      }
    }

    final byte[][] params = new byte[2 + maxLexArgs + hash.size() * 2][];
    int index = 0;
    params[index++] = key;
    if (maxLen < Long.MAX_VALUE) {
      params[index++] = Keyword.MAXLEN.getRaw();
      if (approximateLength) {
        params[index++] = Protocol.BYTES_TILDE;
      }
      params[index++] = Protocol.toByteArray(maxLen);
    }

    params[index++] = id;
    for (final Entry<byte[], byte[]> entry : hash.entrySet()) {
      params[index++] = entry.getKey();
      params[index++] = entry.getValue();
    }
    sendCommand(Command.XADD, params);
  }

  public void xadd(final byte[] key, final Map<byte[], byte[]> hash, final XAddParams xAddParams) {
    final byte[][] params = new byte[hash.size() * 2][];
    int index = 0;
    for (final Entry<byte[], byte[]> entry : hash.entrySet()) {
      params[index++] = entry.getKey();
      params[index++] = entry.getValue();
    }
    sendCommand(Command.XADD, xAddParams.getByteParams(key, params));
  }

  public void xlen(final byte[] key) {
    sendCommand(Command.XLEN, key);
  }

  public void xrange(final byte[] key, final byte[] start, final byte[] end) {
    sendCommand(Command.XRANGE, key, start, end);
  }

  /**
   * @deprecated Use {@link #xrange(byte[], byte[], byte[], int)}.
   */
  @Deprecated
  public void xrange(final byte[] key, final byte[] start, final byte[] end, final long count) {
    sendCommand(Command.XRANGE, key, start, end, Keyword.COUNT.getRaw(), Protocol.toByteArray(count));
  }

  public void xrange(final byte[] key, final byte[] start, final byte[] end, final int count) {
    sendCommand(Command.XRANGE, key, start, end, Keyword.COUNT.getRaw(), Protocol.toByteArray(count));
  }

  public void xrevrange(final byte[] key, final byte[] end, final byte[] start) {
    sendCommand(Command.XREVRANGE, key, end, start);
  }

  public void xrevrange(final byte[] key, final byte[] end, final byte[] start, final int count) {
    sendCommand(Command.XREVRANGE, key, end, start, Keyword.COUNT.getRaw(), Protocol.toByteArray(count));
  }

  /**
   * @deprecated This method will be removed due to bug regarding {@code block} param. Use
   * {@link #xread(XReadParams, java.util.Map.Entry...)}.
   */
  @Deprecated
  public void xread(final int count, final long block, final Map<byte[], byte[]> streams) {
    final byte[][] params = new byte[3 + streams.size() * 2 + (block > 0 ? 2 : 0)][];

    int streamsIndex = 0;
    params[streamsIndex++] = Keyword.COUNT.getRaw();
    params[streamsIndex++] = Protocol.toByteArray(count);
    if (block > 0) {
      params[streamsIndex++] = Keyword.BLOCK.getRaw();
      params[streamsIndex++] = Protocol.toByteArray(block);
    }

    params[streamsIndex++] = Keyword.STREAMS.getRaw();
    int idsIndex = streamsIndex + streams.size();

    for (final Entry<byte[], byte[]> entry : streams.entrySet()) {
      params[streamsIndex++] = entry.getKey();
      params[idsIndex++] = entry.getValue();
    }

    sendCommand(Command.XREAD, params);
  }

  public void xread(final XReadParams params, final Entry<byte[], byte[]>... streams) {
    final byte[][] bparams = params.getByteParams();
    final int paramLength = bparams.length;

    final byte[][] args = new byte[paramLength + 1 + streams.length * 2][];
    System.arraycopy(bparams, 0, args, 0, paramLength);

    args[paramLength] = Keyword.STREAMS.raw;
    int keyIndex = paramLength + 1;
    int idsIndex = keyIndex + streams.length;
    for (final Entry<byte[], byte[]> entry : streams) {
      args[keyIndex++] = entry.getKey();
      args[idsIndex++] = entry.getValue();
    }

    sendCommand(Command.XREAD, args);
  }

  public void xack(final byte[] key, final byte[] group, final byte[]... ids) {
    final byte[][] params = new byte[2 + ids.length][];
    int index = 0;
    params[index++] = key;
    params[index++] = group;
    for (final byte[] id : ids) {
      params[index++] = id;
    }
    sendCommand(Command.XACK, params);
  }

  public void xgroupCreate(final byte[] key, final byte[] groupname, final byte[] id,
      boolean makeStream) {
    if (makeStream) {
      sendCommand(Command.XGROUP, Keyword.CREATE.getRaw(), key, groupname, id, Keyword.MKSTREAM.getRaw());
    } else {
      sendCommand(Command.XGROUP, Keyword.CREATE.getRaw(), key, groupname, id);
    }
  }

  public void xgroupSetID(final byte[] key, final byte[] groupname, final byte[] id) {
    sendCommand(Command.XGROUP, Keyword.SETID.getRaw(), key, groupname, id);
  }

  public void xgroupDestroy(final byte[] key, final byte[] groupname) {
    sendCommand(Command.XGROUP, Keyword.DESTROY.getRaw(), key, groupname);
  }

  public void xgroupDelConsumer(final byte[] key, final byte[] groupname, final byte[] consumerName) {
    sendCommand(Command.XGROUP, Keyword.DELCONSUMER.getRaw(), key, groupname, consumerName);
  }

  public void xdel(final byte[] key, final byte[]... ids) {
    final byte[][] params = new byte[1 + ids.length][];
    int index = 0;
    params[index++] = key;
    for (final byte[] id : ids) {
      params[index++] = id;
    }
    sendCommand(Command.XDEL, params);
  }

  public void xtrim(byte[] key, long maxLen, boolean approximateLength) {
    if (approximateLength) {
      sendCommand(
          Command.XTRIM, key, Keyword.MAXLEN.getRaw(), Protocol.BYTES_TILDE, Protocol.toByteArray(maxLen));
    } else {
      sendCommand(Command.XTRIM, key, Keyword.MAXLEN.getRaw(), Protocol.toByteArray(maxLen));
    }
  }

  public void xtrim(byte[] key, XTrimParams params) {
    sendCommand(Command.XTRIM, params.getByteParams(key));
  }

  /**
   * @deprecated This method will be removed due to bug regarding {@code block} param. Use
   * {@link BinaryClient#xreadGroup(byte..., byte..., XReadGroupParams, java.util.Map.Entry...)}.
   */
  @Deprecated
  public void xreadGroup(byte[] groupname, byte[] consumer, int count, long block, boolean noAck,
      Map<byte[], byte[]> streams) {

    int optional = 0;
    if (count > 0) {
      optional += 2;
    }
    if (block > 0) {
      optional += 2;
    }
    if (noAck) {
      optional += 1;
    }

    final byte[][] params = new byte[4 + optional + streams.size() * 2][];

    int streamsIndex = 0;
    params[streamsIndex++] = Keyword.GROUP.getRaw();
    params[streamsIndex++] = groupname;
    params[streamsIndex++] = consumer;
    if (count > 0) {
      params[streamsIndex++] = Keyword.COUNT.getRaw();
      params[streamsIndex++] = Protocol.toByteArray(count);
    }
    if (block > 0) {
      params[streamsIndex++] = Keyword.BLOCK.getRaw();
      params[streamsIndex++] = Protocol.toByteArray(block);
    }
    if (noAck) {
      params[streamsIndex++] = Keyword.NOACK.getRaw();
    }
    params[streamsIndex++] = Keyword.STREAMS.getRaw();

    int idsIndex = streamsIndex + streams.size();
    for (final Entry<byte[], byte[]> entry : streams.entrySet()) {
      params[streamsIndex++] = entry.getKey();
      params[idsIndex++] = entry.getValue();
    }

    sendCommand(Command.XREADGROUP, params);
  }

  public void xreadGroup(byte[] groupname, byte[] consumer, final XReadGroupParams params,
      final Entry<byte[], byte[]>... streams) {
    final byte[][] bparams = params.getByteParams();
    final int paramLength = bparams.length;

    final byte[][] args = new byte[3 + paramLength + 1 + streams.length * 2][];
    int index = 0;
    args[index++] = Keyword.GROUP.raw;
    args[index++] = groupname;
    args[index++] = consumer;
    System.arraycopy(bparams, 0, args, index, paramLength);
    index += paramLength;

    args[index++] = Keyword.STREAMS.raw;
    int keyIndex = index;
    int idsIndex = keyIndex + streams.length;
    for (final Entry<byte[], byte[]> entry : streams) {
      args[keyIndex++] = entry.getKey();
      args[idsIndex++] = entry.getValue();
    }

    sendCommand(Command.XREADGROUP, args);
  }

  public void xpending(final byte[] key, final byte[] groupname) {
    sendCommand(Command.XPENDING, key, groupname);
  }

  public void xpending(byte[] key, byte[] groupname, byte[] start, byte[] end, int count,
      byte[] consumername) {
    if (consumername == null) {
      sendCommand(Command.XPENDING, key, groupname, start, end, Protocol.toByteArray(count));
    } else {
      sendCommand(Command.XPENDING, key, groupname, start, end, Protocol.toByteArray(count), consumername);
    }
  }

  public void xpending(byte[] key, byte[] groupname, XPendingParams params) {
    sendCommand(Command.XPENDING, joinParameters(key, groupname, params.getByteParams()));
  }

  public void xclaim(byte[] key, byte[] groupname, byte[] consumername, long minIdleTime,
      long newIdleTime, int retries, boolean force, byte[][] ids) {

    List<byte[]> arguments = new ArrayList<>(10 + ids.length);

    arguments.add(key);
    arguments.add(groupname);
    arguments.add(consumername);
    arguments.add(Protocol.toByteArray(minIdleTime));

    Collections.addAll(arguments, ids);

    if (newIdleTime > 0) {
      arguments.add(Keyword.IDLE.getRaw());
      arguments.add(Protocol.toByteArray(newIdleTime));
    }
    if (retries > 0) {
      arguments.add(Keyword.RETRYCOUNT.getRaw());
      arguments.add(Protocol.toByteArray(retries));
    }
    if (force) {
      arguments.add(Keyword.FORCE.getRaw());
    }
    sendCommand(Command.XCLAIM, arguments.toArray(new byte[arguments.size()][]));
  }

  private void xclaim(byte[] key, byte[] groupname, byte[] consumername, long minIdleTime,
                           XClaimParams params, byte[][] ids, boolean justId) {
    final byte[][] bparams = params.getByteParams();
    final int paramLength = bparams.length;
    final int idsLength = ids.length;
    final byte[][] args = new byte[4 + paramLength + idsLength + (justId ? 1 : 0)][];
    int index = 0;
    args[index++] = key;
    args[index++] = groupname;
    args[index++] = consumername;
    args[index++] = Protocol.toByteArray(minIdleTime);
    System.arraycopy(ids, 0, args, index, idsLength);
    index += idsLength;
    System.arraycopy(bparams, 0, args, index, paramLength);
    index += paramLength;
    if (justId) {
      args[index++] = Keyword.JUSTID.getRaw();
    }
    sendCommand(Command.XCLAIM, args);
  }

  public void xclaim(byte[] key, byte[] groupname, byte[] consumername, long minIdleTime,
      XClaimParams params, byte[]... ids) {
    xclaim(key, groupname, consumername, minIdleTime, params, ids, false);
  }

  public void xclaimJustId(byte[] key, byte[] groupname, byte[] consumername, long minIdleTime,
      XClaimParams params, byte[]... ids) {
    xclaim(key, groupname, consumername, minIdleTime, params, ids, true);
  }

  public void xautoclaim(byte[] key, byte[] groupName, byte[] consumerName,
      long minIdleTime, byte[] start, XAutoClaimParams params) {
    xautoclaim(key, groupName, consumerName, minIdleTime, start, params, false);
  }

  private void xautoclaim(byte[] key, byte[] groupName, byte[] consumerName,
      long minIdleTime, byte[] start, XAutoClaimParams params, boolean justId) {
    List<byte[]> arguments = new ArrayList<>();

    arguments.add(key);
    arguments.add(groupName);
    arguments.add(consumerName);
    arguments.add(Protocol.toByteArray(minIdleTime));
    arguments.add(start);
    Collections.addAll(arguments, params.getByteParams());

    if (justId) {
      arguments.add(Keyword.JUSTID.getRaw());
    }

    sendCommand(Command.XAUTOCLAIM, arguments.toArray(new byte[arguments.size()][]));
  }

  public void xautoclaimJustId(byte[] key, byte[] groupName, byte[] consumerName,
      long minIdleTime, byte[] start, XAutoClaimParams params) {
    xautoclaim(key, groupName, consumerName, minIdleTime, start, params, true);
  }

  public void xinfoStream(byte[] key) {
    sendCommand(Command.XINFO, Keyword.STREAM.getRaw(), key);
  }

  public void xinfoGroup(byte[] key) {
    sendCommand(Command.XINFO, Keyword.GROUPS.getRaw(), key);
  }

  public void xinfoConsumers(byte[] key, byte[] group) {
    sendCommand(Command.XINFO, Keyword.CONSUMERS.getRaw(), key, group);
  }

  private static byte[][] joinParameters(byte[] first, byte[][] rest) {
    byte[][] result = new byte[rest.length + 1][];
    result[0] = first;
    System.arraycopy(rest, 0, result, 1, rest.length);
    return result;
  }

  private static byte[][] joinParameters(byte[] first, byte[] second, byte[][] rest) {
    byte[][] result = new byte[rest.length + 2][];
    result[0] = first;
    result[1] = second;
    System.arraycopy(rest, 0, result, 2, rest.length);
    return result;
  }
}
