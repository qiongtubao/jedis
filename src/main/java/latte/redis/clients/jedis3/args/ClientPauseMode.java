package latte.redis.clients.jedis3.args;

import latte.redis.clients.jedis3.util.SafeEncoder;

public enum ClientPauseMode implements Rawable {

  ALL, WRITE;

  private final byte[] raw;

  private ClientPauseMode() {
    raw = SafeEncoder.encode(name());
  }

  @Override
  public byte[] getRaw() {
    return raw;
  }
}
