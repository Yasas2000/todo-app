import { useState } from 'react';
import './TaskCard.css';

/**
 * Card component for displaying individual tasks.
 * Shows task information and completion button.
 */
const TaskCard = ({ task, onComplete }) => {
  const [isCompleting, setIsCompleting] = useState(false);

  const handleComplete = async () => {
    setIsCompleting(true);
    try {
      await onComplete(task.id);
    } catch (error) {
      console.error('Failed to complete task:', error);
      setIsCompleting(false);
    }
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return new Intl.DateTimeFormat('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    }).format(date);
  };

  return (
    <div className="task-card">
      <div className="task-content">
        <h3 className="task-title">{task.title}</h3>
        <p className="task-description">{task.description}</p>
        <span className="task-date">Created: {formatDate(task.createdAt)}</span>
      </div>
      <button
        onClick={handleComplete}
        disabled={isCompleting}
        className="done-button"
      >
        {isCompleting ? 'Completing...' : 'Done'}
      </button>
    </div>
  );
};

export default TaskCard;