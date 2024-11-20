package latte.redis.clients.jedis3;

public abstract class Builder<T> {
  public abstract T build(Object data);
}
