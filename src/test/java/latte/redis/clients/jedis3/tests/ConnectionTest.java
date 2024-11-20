package latte.redis.clients.jedis3.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import latte.redis.clients.jedis3.Connection;
import latte.redis.clients.jedis3.Protocol;
import latte.redis.clients.jedis3.exceptions.JedisConnectionException;
import org.junit.After;
import org.junit.Test;

public class ConnectionTest {
  private Connection client;

  @After
  public void tearDown() throws Exception {
    if (client != null) {
      client.close();
    }
  }

  @Test(expected = JedisConnectionException.class)
  public void checkUnkownHostBackwardCompatible() {
    client = new Connection();
    client.setHost("someunknownhost");
    client.connect();
  }

  @Test(expected = JedisConnectionException.class)
  public void checkUnkownHost() {
    client = new Connection("someunknownhost", Protocol.DEFAULT_PORT);
    client.connect();
  }

  @Test(expected = JedisConnectionException.class)
  public void checkWrongPortBackwardCompatible() {
    client = new Connection();
    client.setHost("localhost");
    client.setPort(55665);
    client.connect();
  }

  @Test(expected = JedisConnectionException.class)
  public void checkWrongPort() {
    client = new Connection(Protocol.DEFAULT_HOST, 55665);
    client.connect();
  }

  @Test
  public void connectIfNotConnectedWhenSettingTimeoutInfinite() {
    client = new Connection("localhost", 6379);
    client.setTimeoutInfinite();
  }

  @Test
  public void checkCloseable() {
    client = new Connection("localhost", 6379);
    client.connect();
    client.close();
  }

  @Test
  public void readWithBrokenConnection() {
    class BrokenConnection extends Connection {
      private BrokenConnection() {
        super("nonexistinghost", 0);
        try {
          connect();
          fail("Client should fail connecting to nonexistinghost");
        } catch (JedisConnectionException ignored) {
        }
      }

      private Object read() {
        return readProtocolWithCheckingBroken();
      }
    }

    BrokenConnection conn = new BrokenConnection();
    try {
      conn.read();
      fail("Read should fail as connection is broken");
    } catch (JedisConnectionException jce) {
      assertEquals("Attempting to read from a broken connection", jce.getMessage());
    }
  }
}
