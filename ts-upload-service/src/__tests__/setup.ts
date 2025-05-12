import fs from 'fs';
import path from 'path';

const testUploadDir = path.join(__dirname, '../../test-uploads');

// Create test upload directory before tests
beforeAll(() => {
  if (!fs.existsSync(testUploadDir)) {
    fs.mkdirSync(testUploadDir, { recursive: true });
  }
});

// Clean up test upload directory after tests
afterAll(() => {
  if (fs.existsSync(testUploadDir)) {
    fs.rmSync(testUploadDir, { recursive: true, force: true });
  }
}); 