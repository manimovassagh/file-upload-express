# File Upload Services

This project provides two independent file upload services implemented in **Spring Boot** (Java) and **TypeScript/Express**, both fully dockerized.

## 🏗️ Architecture

Two independent services that provide similar functionality:
- **Spring Boot Service**: Java-based file upload service (port 8080)
- **TypeScript Service**: Node.js/Express-based file upload service (port 3000)

Both services support:
- Multiple file uploads
- File type validation
- File size limits
- File listing
- File downloads

## 🚀 Quick Start

### 1. **Clone the repository**
```bash
git clone https://github.com/manimovassagh/file-upload-express.git
cd file-upload-express
```

### 2. **Build & Run Spring Boot Service**
```bash
cd uploader
docker build -t file-upload-spring .
docker run -d -p 8080:8080 --name spring-upload file-upload-spring
```

### 3. **Build & Run TypeScript Service**
```bash
cd ts-upload-service
docker build -t file-upload-ts .
docker run -d -p 3000:3000 --name ts-upload file-upload-ts
```

### 4. **Test the Services**

#### Spring Boot Service (port 8080):
```bash
# Upload files
curl -X POST -F "files=@test.txt" -F "files=@test.pdf" http://localhost:8080/api/upload

# List files
curl http://localhost:8080/api/files

# Download a file
curl -O http://localhost:8080/api/files/<filename>
```

#### TypeScript Service (port 3000):
```bash
# Upload files
curl -X POST -F "files=@test.txt" -F "files=@test.pdf" http://localhost:3000/upload

# List files
curl http://localhost:3000/files

# Download a file
curl -O http://localhost:3000/files/<filename>
```

## 📦 Project Structure

```
file-upload/
├── uploader/                # Spring Boot service
│   ├── src/
│   │   ├── main/
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
├── ts-upload-service/       # TypeScript service
│   ├── src/
│   │   ├── index.ts
│   │   └── __tests__/
│   ├── Dockerfile
│   └── package.json
└── README.md
```

## 🛠️ API Endpoints

### Spring Boot Service (http://localhost:8080)
- `POST /api/upload` — Upload files (max 5 files, 5MB each)
- `GET /api/files` — List files
- `GET /api/files/:filename` — Download file

### TypeScript Service (http://localhost:3000)
- `POST /upload` — Upload files (max 5 files, 5MB each)
- `GET /files` — List files
- `GET /files/:filename` — Download file

## 📝 Development

### Spring Boot Service
```bash
cd uploader
./mvnw clean test    # Run tests
./mvnw spring-boot:run   # Run locally
```

### TypeScript Service
```bash
cd ts-upload-service
npm install
npm test         # Run tests
npm start        # Run locally
```

## 🔒 Security Features

Both services implement:
- File type validation (images, PDFs, documents only)
- File size limits (5MB per file)
- Maximum file count (5 files per upload)
- Secure file storage
- Error handling

## 🧪 Testing

Both services include:
- Unit tests
- Integration tests
- File upload/download tests
- Error handling tests

## 🧹 Cleanup
```bash
# Stop and remove containers
docker rm -f spring-upload ts-upload

# Remove images
docker rmi file-upload-spring file-upload-ts
```

## 📄 License
MIT 