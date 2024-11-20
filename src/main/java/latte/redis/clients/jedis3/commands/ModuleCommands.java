package latte.redis.clients.jedis3.commands;

import latte.redis.clients.jedis3.Module;

import java.util.List;

public interface ModuleCommands {

  String moduleLoad(String path);

  String moduleUnload(String name);

  List<Module> moduleList();
}
