package latte.redis.clients.jedis3.args;

import latte.redis.clients.jedis3.util.SafeEncoder;

/**
 * Direction for {@code LMOVE} and {@code BLMOVE} command.
 */
public enum ListDirection implements Rawable {
  LEFT, RIGHT;

  private final byte[] raw;

  ListDirection() {
    raw = SafeEncoder.encode(this.name());
  }

  @Override
  public byte[] getRaw() {
    return raw;
  }
}
