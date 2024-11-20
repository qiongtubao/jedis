package latte.redis.clients.jedis3.exceptions;

public class AbortedTransactionException extends JedisDataException {

  public AbortedTransactionException(final String message) {
    super(message);
  }

  public AbortedTransactionException(final Throwable cause) {
    super(cause);
  }

  public AbortedTransactionException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
