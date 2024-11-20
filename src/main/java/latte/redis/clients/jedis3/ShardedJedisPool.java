package latte.redis.clients.jedis3;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import latte.redis.clients.jedis3.util.Hashing;
import latte.redis.clients.jedis3.util.Pool;

/**
 * @deprecated This class will be removed in next major release.
 */
@Deprecated
public class ShardedJedisPool extends Pool<ShardedJedis> {

  private static final Logger logger = LoggerFactory.getLogger(ShardedJedisPool.class);

  public ShardedJedisPool(final GenericObjectPoolConfig<ShardedJedis> poolConfig,
      List<JedisShardInfo> shards) {
    this(poolConfig, shards, Hashing.MURMUR_HASH);
  }

  public ShardedJedisPool(final GenericObjectPoolConfig<ShardedJedis> poolConfig,
      List<JedisShardInfo> shards, Hashing algo) {
    this(poolConfig, shards, algo, null);
  }

  public ShardedJedisPool(final GenericObjectPoolConfig<ShardedJedis> poolConfig,
      List<JedisShardInfo> shards, Pattern keyTagPattern) {
    this(poolConfig, shards, Hashing.MURMUR_HASH, keyTagPattern);
  }

  public ShardedJedisPool(final GenericObjectPoolConfig<ShardedJedis> poolConfig,
      List<JedisShardInfo> shards, Hashing algo, Pattern keyTagPattern) {
    this(poolConfig, new ShardedJedisFactory(shards, algo, keyTagPattern));
  }

  public ShardedJedisPool(final GenericObjectPoolConfig<ShardedJedis> poolConfig,
      PooledObjectFactory<ShardedJedis> shardedJedisPooledObjectFactory) {
    super(poolConfig, shardedJedisPooledObjectFactory);
  }

  @Override
  public ShardedJedis getResource() {
    ShardedJedis jedis = super.getResource();
    jedis.setDataSource(this);
    return jedis;
  }

  @Override
  public void returnResource(final ShardedJedis resource) {
    if (resource != null) {
      resource.resetState();
      returnResourceObject(resource);
    }
  }

  /**
   * PoolableObjectFactory custom impl.
   */
  public static class ShardedJedisFactory implements PooledObjectFactory<ShardedJedis> {

    private final List<JedisShardInfo> shards;
    private final Hashing algo;
    private final Pattern keyTagPattern;

    public ShardedJedisFactory(List<JedisShardInfo> shards, Hashing algo, Pattern keyTagPattern) {
      this.shards = shards;
      this.algo = algo;
      this.keyTagPattern = keyTagPattern;
    }

    @Override
    public PooledObject<ShardedJedis> makeObject() throws Exception {
      ShardedJedis jedis = new ShardedJedis(shards, algo, keyTagPattern);
      return new DefaultPooledObject<>(jedis);
    }

    @Override
    public void destroyObject(PooledObject<ShardedJedis> pooledShardedJedis) throws Exception {
      final ShardedJedis shardedJedis = pooledShardedJedis.getObject();
      for (Jedis jedis : shardedJedis.getAllShards()) {
        if (jedis.isConnected()) {
          try {
            // need a proper test, probably with mock
            if (!jedis.isBroken()) {
              jedis.quit();
            }
          } catch (RuntimeException e) {
            logger.warn("Error while QUIT", e);
          }
          try {
            jedis.disconnect();
          } catch (RuntimeException e) {
            logger.warn("Error while disconnect", e);
          }
        }
      }
    }

    @Override
    public boolean validateObject(PooledObject<ShardedJedis> pooledShardedJedis) {
      try {
        ShardedJedis jedis = pooledShardedJedis.getObject();
        for (Jedis shard : jedis.getAllShards()) {
          if (!shard.ping().equals("PONG")) {
            return false;
          }
        }
        return true;
      } catch (RuntimeException ex) {
        return false;
      }
    }

    @Override
    public void activateObject(PooledObject<ShardedJedis> p) throws Exception {

    }

    @Override
    public void passivateObject(PooledObject<ShardedJedis> p) throws Exception {

    }
  }
}
