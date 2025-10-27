/**
 * Custom Cypress commands for database operations
 */

/**
 * Clears all tasks from the database via API call.
 * Use this in beforeEach() to ensure clean state.
 */
Cypress.Commands.add('clearAllTasks', () => {
  cy.log('🗑️ Clearing all tasks from database');
  
  cy.request({
    method: 'DELETE',
    url: 'http://localhost:8080/api/tasks',
    failOnStatusCode: false,
  }).then((response) => {
    if (response.status === 204 || response.status === 200) {
      cy.log('✅ All tasks cleared successfully');
    } else {
      cy.log(`⚠️ Clear tasks returned status: ${response.status}`);
    }
  });
});

/**
 * Creates a task via API (faster than UI).
 * @param {string} title - Task title
 * @param {string} description - Task description
 * @returns {Chainable} Created task object
 */
Cypress.Commands.add('createTask', (title, description) => {
  cy.log(`📝 Creating task via API: ${title}`);
  
  return cy.request({
    method: 'POST',
    url: 'http://localhost:8080/api/tasks',
    body: {
      title: title,
      description: description
    },
    headers: {
      'Content-Type': 'application/json'
    }
  }).then((response) => {
    expect(response.status).to.eq(201);
    cy.log(`✅ Task created successfully (ID: ${response.body.id})`);
    return cy.wrap(response.body);
  });
});

/**
 * Marks a task as completed via API.
 * @param {number} taskId - Task ID
 */
Cypress.Commands.add('completeTask', (taskId) => {
  cy.log(`✓ Completing task via API: ${taskId}`);
  
  return cy.request({
    method: 'PUT',
    url: `http://localhost:8080/api/tasks/${taskId}/complete`,
  }).then((response) => {
    expect(response.status).to.eq(200);
    cy.log('✅ Task completed successfully');
    return cy.wrap(response.body);
  });
});

/**
 * Gets all current tasks from API
 * @returns {Chainable} Array of tasks
 */
Cypress.Commands.add('getTasks', () => {
  cy.log('📋 Getting all tasks from API');
  
  return cy.request({
    method: 'GET',
    url: 'http://localhost:8080/api/tasks',
  }).then((response) => {
    expect(response.status).to.eq(200);
    cy.log(`✅ Retrieved ${response.body.length} tasks`);
    return cy.wrap(response.body);
  });
});