# File Upload API

A simple and robust file upload API built with Express.js and TypeScript. This API allows you to upload multiple files, list uploaded files, and download them.

## Features

- Multiple file upload support
- File type restrictions (images, PDFs, documents)
- File size limits (5MB per file)
- Maximum file count (5 files per upload)
- List all uploaded files
- Download files
- Error handling
- TypeScript support
- Jest testing

## Prerequisites

- Node.js (v16.0.0 or higher)
- npm or yarn

## Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/file-upload-api.git
cd file-upload-api
```

2. Install dependencies:
```bash
npm install
```

3. Start the server:
```bash
npm start
```

The server will start on http://localhost:3000

## API Endpoints

### Upload Files
- **POST** `/upload`
- Accepts multiple files
- Form-data key: `files`
- Maximum file size: 5MB
- Maximum files: 5

### List Files
- **GET** `/files`
- Returns array of uploaded filenames

### Download File
- **GET** `/files/:filename`
- Downloads the specified file

## Testing

Run the test suite:
```bash
npm test
```

## License

ISC 