package latte.redis.clients.jedis3.params;

import java.util.ArrayList;
import java.util.List;

import latte.redis.clients.jedis3.Protocol;
import latte.redis.clients.jedis3.Protocol.Keyword;
import latte.redis.clients.jedis3.StreamEntryID;
import latte.redis.clients.jedis3.util.SafeEncoder;

public class XPendingParams extends Params {

  private Long idle;

  private String consumer;

  private StreamEntryID start;

  private StreamEntryID end;

  private Integer count;

  public static XPendingParams xPendingParams() {
    return new XPendingParams();
  }

  public XPendingParams idle(long idle) {
    this.idle = idle;
    return this;
  }

  public XPendingParams start(StreamEntryID start) {
    this.start = start;
    return this;
  }

  public XPendingParams end(StreamEntryID end) {
    this.end = end;
    return this;
  }

  public XPendingParams count(int count) {
    this.count = count;
    return this;
  }

  public XPendingParams consumer(String consumer) {
    this.consumer = consumer;
    return this;
  }

  @Override
  public byte[][] getByteParams() {
    List<byte[]> byteParams = new ArrayList<>();

    if (idle != null) {
      byteParams.add(Keyword.IDLE.getRaw());
      byteParams.add(Protocol.toByteArray(idle));
    }

    if (start == null) {
      byteParams.add(SafeEncoder.encode("-"));
    } else {
      byteParams.add(SafeEncoder.encode(start.toString()));
    }

    if (end == null) {
      byteParams.add(SafeEncoder.encode("+"));
    } else {
      byteParams.add(SafeEncoder.encode(end.toString()));
    }

    if (count != null) {
      byteParams.add(Protocol.toByteArray(count));
    }

    if (consumer != null) {
      byteParams.add(SafeEncoder.encode(consumer));
    }
    return byteParams.toArray(new byte[byteParams.size()][]);
  }
}
