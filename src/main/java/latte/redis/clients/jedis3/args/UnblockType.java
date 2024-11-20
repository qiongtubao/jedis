package latte.redis.clients.jedis3.args;

import latte.redis.clients.jedis3.util.SafeEncoder;

/**
 * Unblock type for {@code CLIENT UNBLOCK} command.
 */
public enum UnblockType implements Rawable {
  TIMEOUT, ERROR;

  private final byte[] raw;

  UnblockType() {
    raw = SafeEncoder.encode(this.name());
  }

  @Override
  public byte[] getRaw() {
    return raw;
  }
}
