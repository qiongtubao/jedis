package latte.redis.clients.jedis3;

import latte.redis.clients.jedis3.util.SafeEncoder;

public enum ListPosition {
  BEFORE, AFTER;
  public final byte[] raw;

  private ListPosition() {
    raw = SafeEncoder.encode(name());
  }
}
