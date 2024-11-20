package latte.redis.clients.jedis3.tests.commands;

import latte.redis.clients.jedis3.HostAndPort;
import latte.redis.clients.jedis3.Jedis;
import org.junit.After;
import org.junit.Before;

import latte.redis.clients.jedis3.tests.HostAndPortUtil;

public abstract class JedisCommandTestBase {
  protected static final HostAndPort hnp = HostAndPortUtil.getRedisServers().get(0);

  protected Jedis jedis;

  public JedisCommandTestBase() {
    super();
  }

  @Before
  public void setUp() throws Exception {
    jedis = new Jedis(hnp.getHost(), hnp.getPort(), 500);
    jedis.connect();
    jedis.auth("foobared");
    jedis.flushAll();
  }

  @After
  public void tearDown() throws Exception {
    jedis.close();
  }

  protected Jedis createJedis() {
    Jedis j = new Jedis(hnp);
    j.connect();
    j.auth("foobared");
    return j;
  }
}
