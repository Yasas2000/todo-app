import api from './api';

/**
 * Task API service layer.
 * Abstracts all task-related API calls from components.
 */
const taskService = {
  /**
   * Fetches the 5 most recent non-completed tasks
   * @returns {Promise} List of tasks
   */
  getRecentTasks: async () => {
    try {
      const response = await api.get('/tasks');
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch tasks');
    }
  },

  /**
   * Creates a new task
   * @param {Object} taskData - Task data with title and description
   * @returns {Promise} Created task
   */
  createTask: async (taskData) => {
    try {
      const response = await api.post('/tasks', taskData);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to create task');
    }
  },

  /**
   * Marks a task as completed
   * @param {number} taskId - Task ID
   * @returns {Promise} Updated task
   */
  completeTask: async (taskId) => {
    try {
      const response = await api.put(`/tasks/${taskId}/complete`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to complete task');
    }
  },
};

export default taskService;