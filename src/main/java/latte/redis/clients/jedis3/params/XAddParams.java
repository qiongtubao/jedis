package latte.redis.clients.jedis3.params;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import latte.redis.clients.jedis3.Protocol;
import latte.redis.clients.jedis3.Protocol.Keyword;
import latte.redis.clients.jedis3.util.SafeEncoder;

public class XAddParams extends Params {

  private String id;

  private Long maxLen;

  private boolean approximateTrimming;

  private boolean exactTrimming;

  private boolean nomkstream;

  private String minId;

  private Long limit;

  public static XAddParams xAddParams() {
    return new XAddParams();
  }

  public XAddParams noMkStream() {
    this.nomkstream = true;
    return this;
  }

  public XAddParams id(String id) {
    this.id = id;
    return this;
  }

  public XAddParams maxLen(long maxLen) {
    this.maxLen = maxLen;
    return this;
  }

  public XAddParams minId(String minId) {
    this.minId = minId;
    return this;
  }

  public XAddParams approximateTrimming() {
    this.approximateTrimming = true;
    return this;
  }

  public XAddParams exactTrimming() {
    this.exactTrimming = true;
    return this;
  }

  public XAddParams limit(long limit) {
    this.limit = limit;
    return this;
  }

  public byte[][] getByteParams(byte[] key, byte[]... args) {
    List<byte[]> byteParams = new ArrayList<>();
    byteParams.add(key);

    if (nomkstream) {
      byteParams.add(Keyword.NOMKSTREAM.getRaw());
    }
    if (maxLen != null) {
      byteParams.add(Keyword.MAXLEN.getRaw());

      if (approximateTrimming) {
        byteParams.add(Protocol.BYTES_TILDE);
      } else if (exactTrimming) {
        byteParams.add(Protocol.BYTES_EQUAL);
      }

      byteParams.add(Protocol.toByteArray(maxLen));
    } else if (minId != null) {
      byteParams.add(Keyword.MINID.getRaw());

      if (approximateTrimming) {
        byteParams.add(Protocol.BYTES_TILDE);
      } else if (exactTrimming) {
        byteParams.add(Protocol.BYTES_EQUAL);
      }

      byteParams.add(SafeEncoder.encode(minId));
    }

    if (limit != null) {
      byteParams.add(Keyword.LIMIT.getRaw());
      byteParams.add(Protocol.toByteArray(limit));
    }

    if (id != null) {
      byteParams.add(SafeEncoder.encode(id));
    } else {
      byteParams.add(Protocol.BYTES_ASTERISK);
    }

    Collections.addAll(byteParams, args);
    return byteParams.toArray(new byte[byteParams.size()][]);
  }
}
