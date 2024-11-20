package latte.redis.clients.jedis3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import latte.redis.clients.jedis3.Protocol.Keyword;
import latte.redis.clients.jedis3.util.SafeEncoder;

public class ZParams {
  public enum Aggregate {
    SUM, MIN, MAX;

    /**
     * @deprecated This will be private in future. Use {@link #getRaw()}.
     */
    @Deprecated
    public final byte[] raw;

    Aggregate() {
      raw = SafeEncoder.encode(name());
    }

    public byte[] getRaw() {
      return raw;
    }
  }

  private final List<byte[]> params = new ArrayList<>();

  /**
   * Set weights.
   * @param weights weights.
   * @return
   */
  public ZParams weights(final double... weights) {
    params.add(Keyword.WEIGHTS.getRaw());
    for (final double weight : weights) {
      params.add(Protocol.toByteArray(weight));
    }

    return this;
  }

  public Collection<byte[]> getParams() {
    return Collections.unmodifiableCollection(params);
  }

  public ZParams aggregate(final Aggregate aggregate) {
    params.add(Keyword.AGGREGATE.getRaw());
    params.add(aggregate.raw);
    return this;
  }
}
