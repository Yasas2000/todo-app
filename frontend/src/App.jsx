import TaskForm from './components/TaskForm/TaskForm';
import TaskList from './components/TaskList/TaskList';
import useTasks from './hooks/useTasks';
import './App.css';

/**
 * Main App component.
 * Orchestrates the todo application.
 */
function App() {
  const { tasks, loading, error, createTask, completeTask } = useTasks();

  return (
    <div className="app">
      <header className="app-header">
        <h1>📝 Todo Task Manager</h1>
        <p>Manage your tasks efficiently</p>
      </header>
      
      <main className="app-main">
        <TaskForm onSubmit={createTask} loading={loading} />
        <TaskList
          tasks={tasks}
          loading={loading}
          error={error}
          onCompleteTask={completeTask}
        />
      </main>
      
      <footer className="app-footer">
        <p>Built with Spring Boot + React + PostgreSQL</p>
      </footer>
    </div>
  );
}

export default App;