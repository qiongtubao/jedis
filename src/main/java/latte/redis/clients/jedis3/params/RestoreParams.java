package latte.redis.clients.jedis3.params;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import latte.redis.clients.jedis3.Protocol;
import latte.redis.clients.jedis3.Protocol.Keyword;

public class RestoreParams extends Params {

  private boolean replace;

  private boolean absTtl;

  private Long idleTime;

  private Long frequency;

  public static RestoreParams restoreParams() {
    return new RestoreParams();
  }

  public RestoreParams replace() {
    this.replace = true;
    return this;
  }

  public RestoreParams absTtl() {
    this.absTtl = true;
    return this;
  }

  public RestoreParams idleTime(long idleTime) {
    this.idleTime = idleTime;
    return this;
  }

  public RestoreParams frequency(long frequency) {
    this.frequency = frequency;
    return this;
  }

  public byte[][] getByteParams(byte[] key, byte[]... args) {
    List<byte[]> byteParams = new ArrayList<>();
    byteParams.add(key);
    Collections.addAll(byteParams, args);

    if (replace) {
      byteParams.add(Keyword.REPLACE.getRaw());
    }

    if (absTtl) {
      byteParams.add(Keyword.ABSTTL.getRaw());
    }

    if (idleTime != null) {
      byteParams.add(Keyword.IDLETIME.getRaw());
      byteParams.add(Protocol.toByteArray(idleTime));
    }

    if (frequency != null) {
      byteParams.add(Keyword.FREQ.getRaw());
      byteParams.add(Protocol.toByteArray(frequency));
    }
    return byteParams.toArray(new byte[byteParams.size()][]);
  }
}
