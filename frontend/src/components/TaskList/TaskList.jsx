import TaskCard from '../TaskCard/TaskCard';
import './TaskList.css';

/**
 * Component for displaying a list of tasks.
 * Shows up to 5 most recent non-completed tasks.
 */
const TaskList = ({ tasks, loading, error, onCompleteTask }) => {
  if (loading) {
    return (
      <div className="task-list-container">
        <h2>Recent Tasks</h2>
        <div className="loading">Loading tasks...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="task-list-container">
        <h2>Recent Tasks</h2>
        <div className="error">Error: {error}</div>
      </div>
    );
  }

  if (tasks.length === 0) {
    return (
      <div className="task-list-container">
        <h2>Recent Tasks</h2>
        <div className="empty-state">
          No tasks yet. Create your first task above!
        </div>
      </div>
    );
  }

  return (
    <div className="task-list-container">
      <h2>Recent Tasks ({tasks.length}/5)</h2>
      <div className="task-list">
        {tasks.map((task) => (
          <TaskCard
            key={task.id}
            task={task}
            onComplete={onCompleteTask}
          />
        ))}
      </div>
    </div>
  );
};

export default TaskList;