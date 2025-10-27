import { defineConfig } from 'cypress';

export default defineConfig({
  e2e: {
    baseUrl: 'http://localhost:5173',
    setupNodeEvents(on, config) {
      // implement node event listeners here
    },
    supportFile: 'cypress/support/e2e.js',
    video: false,
    screenshotOnRunFailure: true,
    viewportWidth: 1280,
    viewportHeight: 720,
    
    // Timeout configurations
    defaultCommandTimeout: 10000, // 10 seconds for most commands
    requestTimeout: 10000,         // 10 seconds for API requests
    responseTimeout: 10000,        // 10 seconds for API responses
    pageLoadTimeout: 30000,        // 30 seconds for page loads
    
    // Retry configuration for flaky tests
    retries: {
      runMode: 2,    // Retry 2 times in CI
      openMode: 0,   // Don't retry in interactive mode
    },
  },
});
