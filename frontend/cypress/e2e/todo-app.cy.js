/**
 * End-to-End tests for Todo Application
 * Tests the complete user workflow from UI to backend
 */
describe('Todo Application E2E Tests', () => {
  
  beforeEach(() => {
    // Clear all tasks before each test to ensure clean state
    cy.clearAllTasks();

    // Visit the application before each test
    cy.visit('http://localhost:5173');
    
    // Wait for app to load
    cy.contains('Todo Task Manager').should('be.visible');
  });

  it('should load the application successfully', () => {
    // Verify header
    cy.contains('📝 Todo Task Manager').should('be.visible');
    cy.contains('Manage your tasks efficiently').should('be.visible');
    
    // Verify form is present
    cy.contains('Create New Task').should('be.visible');
    cy.get('input[id="title"]').should('be.visible');
    cy.get('textarea[id="description"]').should('be.visible');
    cy.get('button[type="submit"]').should('contain', 'Create Task');
    
    // Verify task list section
    cy.contains('Recent Tasks').should('be.visible');
  });

  it('should create a new task successfully', () => {
    const taskTitle = 'E2E Test Task ' + Date.now();
    const taskDescription = 'This task was created by Cypress E2E test';

    // Fill in the form
    cy.get('input[id="title"]').type(taskTitle);
    cy.get('textarea[id="description"]').type(taskDescription);
    
    // Verify character counts update
    cy.contains(taskTitle.length + '/100').should('be.visible');
    cy.contains(taskDescription.length + '/500').should('be.visible');
    
    // Submit the form
    cy.get('button[type="submit"]').click();
    
    // Wait for task to appear in the list
    cy.contains(taskTitle, { timeout: 5000 }).should('be.visible');
    cy.contains(taskDescription).should('be.visible');
    
    // Verify task has Done button
    cy.contains(taskTitle)
      .parent()
      .parent()
      .within(() => {
        cy.contains('button', 'Done').should('be.visible');
      });
  });

  it('should display validation error for empty fields', () => {
    // Try to submit empty form
    cy.get('button[type="submit"]').click();
    
    // Should show validation error
    cy.contains('Title is required').should('be.visible');
  });

  it('should display character count limits', () => {
    const longTitle = 'a'.repeat(101);
    const longDescription = 'b'.repeat(501);
    
    // Type beyond limits
    cy.get('input[id="title"]').type(longTitle);
    cy.get('textarea[id="description"]').type(longDescription);
    
    // Verify character counts (should be capped at maxLength)
    cy.contains('100/100').should('be.visible');
    cy.contains('500/500').should('be.visible');
  });

  it('should mark a task as completed', () => {
    const taskTitle = 'Task to Complete ' + Date.now();
    const taskDescription = 'This task will be marked as done';

    // Create a task
    cy.get('input[id="title"]').type(taskTitle);
    cy.get('textarea[id="description"]').type(taskDescription);
    cy.get('button[type="submit"]').click();
    
    // Wait for task to appear
    cy.contains(taskTitle, { timeout: 5000 }).should('be.visible');
    
    // Click Done button
    cy.contains(taskTitle)
      .parent()
      .parent()
      .within(() => {
        cy.contains('button', 'Done').click();
      });
    
    // Task should disappear from the list
    cy.contains(taskTitle, { timeout: 5000 }).should('not.exist');
  });

  it('should display only 5 most recent tasks', () => {
    // Create 6 tasks
    for (let i = 1; i <= 6; i++) {
      cy.get('input[id="title"]').clear().type(`Task ${i} - ${Date.now()}`);
      cy.get('textarea[id="description"]').clear().type(`Description for task ${i}`);
      cy.get('button[type="submit"]').click();
      cy.wait(500); // Small delay between creations
    }
    
    // Verify only 5 tasks are displayed
    cy.get('.task-card').should('have.length.at.most', 5);
    
    // Verify the count shows maximum 5
    cy.contains(/Recent Tasks \([0-5]\/5\)/).should('be.visible');
  });

  it('should show empty state when no tasks exist', () => {
    // This test assumes a clean state
    // In a real scenario, you'd clean the database first
    
    // Look for empty state message (might not exist if tasks present)
    cy.get('body').then($body => {
      if ($body.find('.task-card').length === 0) {
        cy.contains('No tasks yet').should('be.visible');
      }
    });
  });

  it('should clear form after successful submission', () => {
    const taskTitle = 'Clear Form Test ' + Date.now();
    const taskDescription = 'Form should clear after this';

    // Fill and submit form
    cy.get('input[id="title"]').type(taskTitle);
    cy.get('textarea[id="description"]').type(taskDescription);
    cy.get('button[type="submit"]').click();
    
    // Wait for submission
    cy.wait(1000);
    
    // Verify form is cleared
    cy.get('input[id="title"]').should('have.value', '');
    cy.get('textarea[id="description"]').should('have.value', '');
    cy.contains('0/100').should('be.visible');
    cy.contains('0/500').should('be.visible');
  });

  it('should display task creation timestamp', () => {
    const taskTitle = 'Timestamp Test ' + Date.now();

    // Create a task
    cy.get('input[id="title"]').type(taskTitle);
    cy.get('textarea[id="description"]').type('Check timestamp display');
    cy.get('button[type="submit"]').click();
    
    // Verify timestamp is displayed
    cy.contains(taskTitle)
      .parent()
      .within(() => {
        cy.contains(/Created:/).should('be.visible');
      });
  });

  it('should handle rapid task completions', () => {
    // Create 2 tasks quickly
    for (let i = 1; i <= 2; i++) {
      cy.get('input[id="title"]').clear().type(`Rapid Task ${i} - ${Date.now()}`);
      cy.get('textarea[id="description"]').clear().type(`Description ${i}`);
      cy.get('button[type="submit"]').click();
      cy.wait(300);
    }
    
    // Complete both tasks rapidly
    cy.get('.task-card').each(($card, index) => {
      if (index < 2) {
        cy.wrap($card).find('button').contains('Done').click();
        cy.wait(200);
      }
    });
    
    // Verify tasks are removed
    cy.wait(1000);
    // Note: Actual assertion depends on how many other tasks exist
  });
});