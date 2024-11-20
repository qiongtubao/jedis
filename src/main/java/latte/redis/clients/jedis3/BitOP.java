package latte.redis.clients.jedis3;

import latte.redis.clients.jedis3.util.SafeEncoder;

public enum BitOP {
  AND, OR, XOR, NOT;

  public final byte[] raw;

  private BitOP() {
    this.raw = SafeEncoder.encode(name());
  }
}
