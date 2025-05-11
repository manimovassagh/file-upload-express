import fs from 'fs';
import path from 'path';
import request from 'supertest';
import app from '../index';

const uploadDir = path.join(__dirname, '../../uploads');

describe('File Upload API', () => {
  // Create a test file before each test
  const createTestFile = (content: string = 'test content', filename: string = 'test.txt') => {
    const filePath = path.join(uploadDir, filename);
    fs.writeFileSync(filePath, content);
    return filePath;
  };

  beforeEach(() => {
    // Clean up upload directory before each test
    if (fs.existsSync(uploadDir)) {
      fs.rmSync(uploadDir, { recursive: true, force: true });
    }
    fs.mkdirSync(uploadDir, { recursive: true });
  });

  describe('POST /upload', () => {
    it('should upload a single file successfully', async () => {
      const filePath = createTestFile();
      
      const response = await request(app)
        .post('/upload')
        .attach('files', filePath);

      expect(response.status).toBe(200);
      expect(response.body.message).toBe('Files uploaded successfully');
      expect(response.body.files).toHaveLength(1);
      expect(response.body.files[0]).toHaveProperty('filename');
      expect(response.body.files[0]).toHaveProperty('originalname', 'test.txt');
    });

    it('should upload multiple files successfully', async () => {
      const file1 = createTestFile('test1', 'test1.txt');
      const file2 = createTestFile('test2', 'test2.txt');

      const response = await request(app)
        .post('/upload')
        .attach('files', file1)
        .attach('files', file2);

      expect(response.status).toBe(200);
      expect(response.body.files).toHaveLength(2);
    });

    it('should reject when no files are uploaded', async () => {
      const response = await request(app)
        .post('/upload');

      expect(response.status).toBe(400);
      expect(response.body.error).toBe('No files uploaded.');
    });

    it('should reject files larger than 5MB', async () => {
      // Create a 6MB file
      const largeContent = 'x'.repeat(6 * 1024 * 1024);
      const filePath = createTestFile(largeContent, 'large.txt');

      const response = await request(app)
        .post('/upload')
        .attach('files', filePath);

      expect(response.status).toBe(400);
      expect(response.body.error).toContain('File too large');
    });
  });

  describe('GET /files', () => {
    it('should list uploaded files', async () => {
      createTestFile('test1', 'test1.txt');
      createTestFile('test2', 'test2.txt');

      const response = await request(app)
        .get('/files');

      expect(response.status).toBe(200);
      expect(response.body.files).toBeInstanceOf(Array);
      expect(response.body.files.length).toBe(2);
    });

    it('should return empty array when no files exist', async () => {
      const response = await request(app)
        .get('/files');

      expect(response.status).toBe(200);
      expect(response.body.files).toEqual([]);
    });
  });

  describe('GET /files/:filename', () => {
    it('should download an existing file', async () => {
      const filePath = createTestFile();
      const filename = path.basename(filePath);

      const response = await request(app)
        .get(`/files/${filename}`);

      expect(response.status).toBe(200);
      expect(response.header['content-disposition']).toContain(filename);
    });

    it('should return 404 for non-existent file', async () => {
      const response = await request(app)
        .get('/files/nonexistent.txt');

      expect(response.status).toBe(404);
      expect(response.body.error).toBe('File not found');
    });
  });
}); 