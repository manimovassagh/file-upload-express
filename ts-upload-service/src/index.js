const express = require('express');
const multer = require('multer');
const path = require('path');
const fs = require('fs');
const cors = require('cors');
const axios = require('axios');
const FormData = require('form-data');

const app = express();
const port = process.env.PORT || 3000;

// Middleware
app.use(cors());
app.use(express.json());

// Configure multer for file upload
const storage = multer.diskStorage({
  destination: (req, file, cb) => {
    const uploadDir = process.env.UPLOAD_DIR || 'uploads';
    if (!fs.existsSync(uploadDir)) {
      fs.mkdirSync(uploadDir, { recursive: true });
    }
    cb(null, uploadDir);
  },
  filename: (req, file, cb) => {
    cb(null, Date.now() + '-' + file.originalname);
  }
});

const upload = multer({ storage });

// Routes
app.post('/api/upload', upload.array('files'), async (req, res) => {
  try {
    if (!req.files || req.files.length === 0) {
      return res.status(400).json({ message: 'No files uploaded' });
    }

    const files = req.files.map(file => ({
      filename: file.filename,
      originalName: file.originalname,
      size: file.size,
      mimeType: file.mimetype
    }));

    // Forward to Spring Boot service using form-data
    const springServiceUrl = process.env.SPRING_SERVICE_URL || 'http://spring-boot-service:8080';
    const form = new FormData();
    req.files.forEach(file => {
      form.append('files', fs.createReadStream(file.path), file.originalname);
    });

    const response = await axios.post(`${springServiceUrl}/api/upload`, form, {
      headers: form.getHeaders(),
      maxContentLength: Infinity,
      maxBodyLength: Infinity
    });

    res.json({
      message: 'Files uploaded successfully',
      files,
      springResponse: response.data
    });
  } catch (error) {
    console.error('Upload error:', error);
    res.status(500).json({ message: 'Error uploading files', error: error.message });
  }
});

app.get('/api/files', async (_req, res) => {
  try {
    const springServiceUrl = process.env.SPRING_SERVICE_URL || 'http://spring-boot-service:8080';
    const response = await axios.get(`${springServiceUrl}/api/files`);
    res.json(response.data);
  } catch (error) {
    console.error('List files error:', error);
    res.status(500).json({ message: 'Error listing files', error: error.message });
  }
});

app.get('/api/files/:filename', async (req, res) => {
  try {
    const springServiceUrl = process.env.SPRING_SERVICE_URL || 'http://spring-boot-service:8080';
    const response = await axios.get(`${springServiceUrl}/api/files/${req.params.filename}`, {
      responseType: 'stream'
    });
    response.data.pipe(res);
  } catch (error) {
    console.error('Download error:', error);
    res.status(500).json({ message: 'Error downloading file', error: error.message });
  }
});

// Health check endpoint
app.get('/health', (req, res) => {
  res.json({ status: 'UP' });
});

app.listen(port, () => {
  console.log(`Express service listening on port ${port}`);
}); 