package latte.redis.clients.jedis3.commands;

import latte.redis.clients.jedis3.Response;
import latte.redis.clients.jedis3.StreamEntryID;
import latte.redis.clients.jedis3.args.ListDirection;
import latte.redis.clients.jedis3.params.GeoRadiusParam;
import latte.redis.clients.jedis3.params.GeoRadiusStoreParam;
import latte.redis.clients.jedis3.params.MigrateParams;
import latte.redis.clients.jedis3.params.StrAlgoLCSParams;
import latte.redis.clients.jedis3.params.XReadGroupParams;
import latte.redis.clients.jedis3.params.XReadParams;
import latte.redis.clients.jedis3.BitOP;
import latte.redis.clients.jedis3.GeoUnit;
import latte.redis.clients.jedis3.SortingParams;
import latte.redis.clients.jedis3.StreamEntry;
import latte.redis.clients.jedis3.Tuple;
import latte.redis.clients.jedis3.ZParams;
import latte.redis.clients.jedis3.resps.KeyedListElement;
import latte.redis.clients.jedis3.resps.KeyedZSetElement;
import latte.redis.clients.jedis3.resps.LCSMatchResult;
import latte.redis.clients.jedis3.args.*;
import latte.redis.clients.jedis3.params.*;
import latte.redis.clients.jedis3.resps.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Multikey related commands (these are split out because they are non-shardable)
 */
public interface MultiKeyCommandsPipeline {
  Response<Boolean> copy(String srcKey, String dstKey, int db, boolean replace);

  Response<Boolean> copy(String srcKey, String dstKey, boolean replace);

  Response<Long> del(String... keys);

  Response<Long> unlink(String... keys);

  Response<Long> exists(String... keys);

  Response<String> lmove(String srcKey, String dstKey, ListDirection from, ListDirection to);

  Response<String> blmove(String srcKey, String dstKey, ListDirection from, ListDirection to,
      double timeout);

  Response<List<String>> blpop(String... args);

  Response<List<String>> blpop(int timeout, String... args);

  Response<KeyedListElement> blpop(double timeout, String... args);

  Response<List<String>> brpop(String... args);

  Response<List<String>> brpop(int timeout, String... args);

  Response<KeyedListElement> brpop(double timeout, String... args);

  Response<KeyedZSetElement> bzpopmax(double timeout, String... keys);

  Response<KeyedZSetElement> bzpopmin(double timeout, String... keys);

  Response<Set<String>> keys(String pattern);

  Response<List<String>> mget(String... keys);

  Response<String> mset(String... keysvalues);

  Response<Long> msetnx(String... keysvalues);

  Response<String> rename(String oldkey, String newkey);

  Response<Long> renamenx(String oldkey, String newkey);

  Response<String> rpoplpush(String srckey, String dstkey);

  Response<Set<String>> sdiff(String... keys);

  Response<Long> sdiffstore(String dstkey, String... keys);

  Response<Set<String>> sinter(String... keys);

  Response<Long> sinterstore(String dstkey, String... keys);

  Response<Long> smove(String srckey, String dstkey, String member);

  Response<Long> sort(String key, SortingParams sortingParameters, String dstkey);

  Response<Long> sort(String key, String dstkey);

  Response<Set<String>> sunion(String... keys);

  Response<Long> sunionstore(String dstkey, String... keys);

  Response<String> watch(String... keys);

  /**
   * @deprecated This method will be removed in next major release.
   */
  @Deprecated
  Response<String> unwatch();

  Response<Set<String>> zdiff(String... keys);

  Response<Set<Tuple>> zdiffWithScores(String... keys);

  Response<Long> zdiffStore(String dstkey, String... keys);

  Response<Set<String>> zinter(ZParams params, String... keys);

  Response<Set<Tuple>> zinterWithScores(ZParams params, String... keys);

  Response<Long> zinterstore(String dstkey, String... sets);

  Response<Long> zinterstore(String dstkey, ZParams params, String... sets);

  Response<Set<String>> zunion(ZParams params, String... keys);

  Response<Set<Tuple>> zunionWithScores(ZParams params, String... keys);

  Response<Long> zunionstore(String dstkey, String... sets);

  Response<Long> zunionstore(String dstkey, ZParams params, String... sets);

  Response<String> brpoplpush(String source, String destination, int timeout);

  Response<Long> publish(String channel, String message);

  Response<String> randomKey();

  Response<Long> bitop(BitOP op, String destKey, String... srcKeys);

  Response<String> pfmerge(String destkey, String... sourcekeys);

  Response<Long> pfcount(String... keys);

  Response<Long> touch(String... keys);

  Response<String> migrate(String host, int port, int destinationDB, int timeout,
      MigrateParams params, String... keys);

  Response<Long> georadiusStore(String key, double longitude, double latitude, double radius,
      GeoUnit unit, GeoRadiusParam param, GeoRadiusStoreParam storeParam);

  Response<Long> georadiusByMemberStore(String key, String member, double radius, GeoUnit unit,
      GeoRadiusParam param, GeoRadiusStoreParam storeParam);

  /**
   * @deprecated Use {@link #xread(XReadParams, java.util.Map)}.
   */
  @Deprecated
  Response<List<Map.Entry<String, List<StreamEntry>>>> xread(int count, long block,
      Map.Entry<String, StreamEntryID>... streams);

  Response<List<Map.Entry<String, List<StreamEntry>>>> xread(XReadParams xReadParams,
      Map<String, StreamEntryID> streams);

  /**
   * @deprecated Use {@link #xreadGroup(java.lang.String, java.lang.String, XReadGroupParams, java.util.Map)}.
   */
  @Deprecated
  Response<List<Map.Entry<String, List<StreamEntry>>>> xreadGroup(String groupname, String consumer,
      int count, long block, boolean noAck, Map.Entry<String, StreamEntryID>... streams);

  Response<List<Map.Entry<String, List<StreamEntry>>>> xreadGroup(String groupname, String consumer,
      XReadGroupParams xReadGroupParams, Map<String, StreamEntryID> streams);

  Response<LCSMatchResult> strAlgoLCSKeys(final String keyA, final String keyB, final StrAlgoLCSParams params);
}
