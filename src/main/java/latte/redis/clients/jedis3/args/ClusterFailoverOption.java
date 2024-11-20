package latte.redis.clients.jedis3.args;

import latte.redis.clients.jedis3.util.SafeEncoder;

public enum ClusterFailoverOption implements Rawable {

  FORCE, TAKEOVER;

  private final byte[] raw;

  private ClusterFailoverOption() {
    this.raw = SafeEncoder.encode(name());
  }

  @Override
  public byte[] getRaw() {
    return raw;
  }
}
