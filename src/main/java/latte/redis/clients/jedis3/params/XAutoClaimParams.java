package latte.redis.clients.jedis3.params;

import latte.redis.clients.jedis3.Protocol;
import latte.redis.clients.jedis3.Protocol.Keyword;

import java.util.ArrayList;
import java.util.List;

public class XAutoClaimParams extends Params {

  private Integer count;

  public XAutoClaimParams() {
  }

  public static XAutoClaimParams xAutoClaimParams() {
    return new XAutoClaimParams();
  }

  /**
   * Set the count of stream entries/ids to return as part of the command output.
   * @param count COUNT
   * @return XAutoClaimParams
   */
  public XAutoClaimParams count(int count) {
    this.count = count;
    return this;
  }

  @Override
  public byte[][] getByteParams() {
    List<byte[]> byteParams = new ArrayList<>();

    if (count != null) {
      byteParams.add(Keyword.COUNT.getRaw());
      byteParams.add(Protocol.toByteArray(count));
    }

    return byteParams.toArray(new byte[byteParams.size()][]);
  }

}
