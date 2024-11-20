package latte.redis.clients.jedis3.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import latte.redis.clients.jedis3.Module;
import latte.redis.clients.jedis3.commands.ProtocolCommand;
import latte.redis.clients.jedis3.tests.commands.JedisCommandTestBase;
import org.junit.Test;

import latte.redis.clients.jedis3.util.SafeEncoder;

public class ModuleTest extends JedisCommandTestBase {

  static enum ModuleCommand implements ProtocolCommand {
    SIMPLE("testmodule.simple");

    private final byte[] raw;

    ModuleCommand(String alt) {
      raw = SafeEncoder.encode(alt);
    }

    @Override
    public byte[] getRaw() {
      return raw;
    }
  }

  @Test
  public void testModules() {
    String res = jedis.moduleLoad("/tmp/testmodule.so");
    assertEquals("OK", res);

    List<Module> modules = jedis.moduleList();

    assertEquals("testmodule", modules.get(0).getName());

    jedis.getClient().sendCommand(ModuleCommand.SIMPLE);
    Long out = jedis.getClient().getIntegerReply();
    assertTrue(out > 0);

    res = jedis.moduleUnload("testmodule");
    assertEquals("OK", res);
  }

}