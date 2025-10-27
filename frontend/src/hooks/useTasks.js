import { useState, useEffect, useCallback } from 'react';
import taskService from '../services/taskService';

/**
 * Custom hook for managing task operations.
 * Encapsulates all task-related state and logic.
 */
const useTasks = () => {
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  /**
   * Fetches tasks from the API
   */
  const fetchTasks = useCallback(async () => {
    setLoading(true);
    setError(null);
    
    try {
      const data = await taskService.getRecentTasks();
      setTasks(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }, []);

  /**
   * Creates a new task
   */
  const createTask = async (taskData) => {
    setError(null);
    
    try {
      const newTask = await taskService.createTask(taskData);
      // Refresh task list after creation
      await fetchTasks();
      return newTask;
    } catch (err) {
      setError(err.message);
      throw err;
    }
  };

  /**
   * Marks a task as completed
   */
  const completeTask = async (taskId) => {
    setError(null);
    
    try {
      await taskService.completeTask(taskId);
      // Remove completed task from list immediately (optimistic update)
      setTasks(prevTasks => prevTasks.filter(task => task.id !== taskId));
    } catch (err) {
      setError(err.message);
      // Refresh on error to sync with server state
      await fetchTasks();
    }
  };

  /**
   * Load tasks on component mount
   */
  useEffect(() => {
    fetchTasks();
  }, [fetchTasks]);

  return {
    tasks,
    loading,
    error,
    createTask,
    completeTask,
    refreshTasks: fetchTasks,
  };
};

export default useTasks;