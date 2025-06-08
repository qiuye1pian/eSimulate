package org.esimulate.core.component;

import org.esimulate.core.model.task.OptimizeTask;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

public class TaskRegistry {

    private static final TaskRegistry INSTANCE = new TaskRegistry();

    // key = taskId, value = TaskInfo
    private final ConcurrentHashMap<Long, Future<OptimizeTask>> registry = new ConcurrentHashMap<>();

    private TaskRegistry() {
    }

    public static TaskRegistry getInstance() {
        return INSTANCE;
    }

    public void register(Long taskId, Future<OptimizeTask> info) {
        registry.put(taskId, info);
    }

    public Future<OptimizeTask> get(Long taskId) {
        return registry.get(taskId);
    }

    public void remove(Long taskId) {
        if (taskId == null) return;
        registry.remove(taskId);
    }

    public int getSize() {
        return registry.size();
    }
}