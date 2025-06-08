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
        this.registry.put(taskId, info);
    }

    public Future<OptimizeTask> get(Long taskId) {
        return this.registry.get(taskId);
    }

    public void remove(Long taskId) {
        if (taskId == null) return;
        this.registry.remove(taskId);
    }

    public int getFutureSize() {
        return this.registry.size();
    }

}