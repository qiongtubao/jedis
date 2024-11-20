package latte.redis.clients.jedis3;

import static latte.redis.clients.jedis3.Protocol.Keyword.MESSAGE;
import static latte.redis.clients.jedis3.Protocol.Keyword.PMESSAGE;
import static latte.redis.clients.jedis3.Protocol.Keyword.PONG;
import static latte.redis.clients.jedis3.Protocol.Keyword.PSUBSCRIBE;
import static latte.redis.clients.jedis3.Protocol.Keyword.PUNSUBSCRIBE;
import static latte.redis.clients.jedis3.Protocol.Keyword.SUBSCRIBE;
import static latte.redis.clients.jedis3.Protocol.Keyword.UNSUBSCRIBE;

import java.util.Arrays;
import java.util.List;

import latte.redis.clients.jedis3.exceptions.JedisException;

public abstract class BinaryJedisPubSub {
  private int subscribedChannels = 0;
  private Client client;

  public void onMessage(byte[] channel, byte[] message) {
  }

  public void onPMessage(byte[] pattern, byte[] channel, byte[] message) {
  }

  public void onSubscribe(byte[] channel, int subscribedChannels) {
  }

  public void onUnsubscribe(byte[] channel, int subscribedChannels) {
  }

  public void onPUnsubscribe(byte[] pattern, int subscribedChannels) {
  }

  public void onPSubscribe(byte[] pattern, int subscribedChannels) {
  }

  public void onPong(byte[] pattern) {
  }

  public void unsubscribe() {
    client.unsubscribe();
    client.flush();
  }

  public void unsubscribe(byte[]... channels) {
    client.unsubscribe(channels);
    client.flush();
  }

  public void subscribe(byte[]... channels) {
    client.subscribe(channels);
    client.flush();
  }

  public void psubscribe(byte[]... patterns) {
    client.psubscribe(patterns);
    client.flush();
  }

  public void punsubscribe() {
    client.punsubscribe();
    client.flush();
  }

  public void punsubscribe(byte[]... patterns) {
    client.punsubscribe(patterns);
    client.flush();
  }

  public void ping() {
    client.ping();
    client.flush();
  }

  public void ping(byte[] argument) {
    client.ping(argument);
    client.flush();
  }

  public boolean isSubscribed() {
    return subscribedChannels > 0;
  }

  public void proceedWithPatterns(Client client, byte[]... patterns) {
    this.client = client;
    client.psubscribe(patterns);
    client.flush();
    process(client);
  }

  public void proceed(Client client, byte[]... channels) {
    this.client = client;
    client.subscribe(channels);
    client.flush();
    process(client);
  }

  private void process(Client client) {
    do {
      List<Object> reply = client.getUnflushedObjectMultiBulkReply();
      final Object firstObj = reply.get(0);
      if (!(firstObj instanceof byte[])) {
        throw new JedisException("Unknown message type: " + firstObj);
      }
      final byte[] resp = (byte[]) firstObj;
      if (Arrays.equals(SUBSCRIBE.getRaw(), resp)) {
        subscribedChannels = ((Long) reply.get(2)).intValue();
        final byte[] bchannel = (byte[]) reply.get(1);
        onSubscribe(bchannel, subscribedChannels);
      } else if (Arrays.equals(UNSUBSCRIBE.getRaw(), resp)) {
        subscribedChannels = ((Long) reply.get(2)).intValue();
        final byte[] bchannel = (byte[]) reply.get(1);
        onUnsubscribe(bchannel, subscribedChannels);
      } else if (Arrays.equals(MESSAGE.getRaw(), resp)) {
        final byte[] bchannel = (byte[]) reply.get(1);
        final byte[] bmesg = (byte[]) reply.get(2);
        onMessage(bchannel, bmesg);
      } else if (Arrays.equals(PMESSAGE.getRaw(), resp)) {
        final byte[] bpattern = (byte[]) reply.get(1);
        final byte[] bchannel = (byte[]) reply.get(2);
        final byte[] bmesg = (byte[]) reply.get(3);
        onPMessage(bpattern, bchannel, bmesg);
      } else if (Arrays.equals(PSUBSCRIBE.getRaw(), resp)) {
        subscribedChannels = ((Long) reply.get(2)).intValue();
        final byte[] bpattern = (byte[]) reply.get(1);
        onPSubscribe(bpattern, subscribedChannels);
      } else if (Arrays.equals(PUNSUBSCRIBE.getRaw(), resp)) {
        subscribedChannels = ((Long) reply.get(2)).intValue();
        final byte[] bpattern = (byte[]) reply.get(1);
        onPUnsubscribe(bpattern, subscribedChannels);
      } else if (Arrays.equals(PONG.getRaw(), resp)) {
        final byte[] bpattern = (byte[]) reply.get(1);
        onPong(bpattern);
      } else {
        throw new JedisException("Unknown message type: " + firstObj);
      }
    } while (isSubscribed());
  }

  public int getSubscribedChannels() {
    return subscribedChannels;
  }
}
