import { useState } from 'react';
import './TaskForm.css';

/**
 * Form component for creating new tasks.
 * Handles user input and validation.
 */
const TaskForm = ({ onSubmit, loading }) => {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [validationError, setValidationError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Client-side validation
    if (!title.trim()) {
      setValidationError('Title is required');
      return;
    }
    
    if (!description.trim()) {
      setValidationError('Description is required');
      return;
    }

    if (title.length > 100) {
      setValidationError('Title must be less than 100 characters');
      return;
    }

    if (description.length > 500) {
      setValidationError('Description must be less than 500 characters');
      return;
    }

    setValidationError('');
    
    try {
      await onSubmit({ title, description });
      // Clear form on success
      setTitle('');
      setDescription('');
    } catch (error) {
      setValidationError(error.message);
    }
  };

  return (
    <div className="task-form-container">
      <h2>Create New Task</h2>
      <form onSubmit={handleSubmit} className="task-form">
        <div className="form-group">
          <label htmlFor="title">Title *</label>
          <input
            type="text"
            id="title"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="Enter task title"
            maxLength={100}
            disabled={loading}
            className="form-input"
          />
          <span className="char-count">{title.length}/100</span>
        </div>

        <div className="form-group">
          <label htmlFor="description">Description *</label>
          <textarea
            id="description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="Enter task description"
            maxLength={500}
            rows={4}
            disabled={loading}
            className="form-textarea"
          />
          <span className="char-count">{description.length}/500</span>
        </div>

        {validationError && (
          <div className="error-message">{validationError}</div>
        )}

        <button 
          type="submit" 
          disabled={loading}
          className="submit-button"
        >
          {loading ? 'Creating...' : 'Create Task'}
        </button>
      </form>
    </div>
  );
};

export default TaskForm;