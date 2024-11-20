package latte.redis.clients.jedis3.exceptions;

/**
 * @deprecated This exception will be removed in next major release.
 */
@Deprecated
public class JedisNoReachableClusterNodeException extends JedisConnectionException {
    private static final long serialVersionUID = 3878122572474110407L;

    public JedisNoReachableClusterNodeException(String message) {
        super(message);
    }

    public JedisNoReachableClusterNodeException(Throwable cause) {
        super(cause);
    }

    public JedisNoReachableClusterNodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
