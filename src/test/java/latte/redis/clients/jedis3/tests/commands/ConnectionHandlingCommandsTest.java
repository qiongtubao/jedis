package latte.redis.clients.jedis3.tests.commands;

import static org.junit.Assert.assertEquals;

import latte.redis.clients.jedis3.BinaryJedis;
import latte.redis.clients.jedis3.HostAndPort;
import latte.redis.clients.jedis3.Jedis;
import org.junit.Test;

import latte.redis.clients.jedis3.tests.HostAndPortUtil;

public class ConnectionHandlingCommandsTest {
  private static HostAndPort hnp = HostAndPortUtil.getRedisServers().get(0);

  @Test
  public void quit() {
    Jedis jedis = new Jedis(hnp);
    assertEquals("OK", jedis.quit());
  }

  @Test
  public void binary_quit() {
    BinaryJedis bj = new BinaryJedis(hnp);
    assertEquals("OK", bj.quit());
  }
}