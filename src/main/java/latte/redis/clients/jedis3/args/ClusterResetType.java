package latte.redis.clients.jedis3.args;

import latte.redis.clients.jedis3.util.SafeEncoder;

public enum ClusterResetType implements Rawable {

  SOFT, HARD;

  private final byte[] raw;

  private ClusterResetType() {
    this.raw = SafeEncoder.encode(name());
  }

  @Override
  public byte[] getRaw() {
    return raw;
  }
}
