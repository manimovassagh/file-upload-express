# File Upload Services

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.x-green.svg)](https://spring.io/projects/spring-boot)
[![TypeScript](https://img.shields.io/badge/TypeScript-4.x-blue.svg)](https://www.typescriptlang.org/)
[![Docker](https://img.shields.io/badge/Docker-20.x-blue.svg)](https://www.docker.com/)

This project provides two independent file upload services implemented in **Spring Boot** (Java) and **TypeScript/Express**, both fully dockerized.

## 📑 Table of Contents
- [Architecture](#-architecture)
- [Quick Start](#-quick-start)
- [Project Structure](#-project-structure)
- [API Endpoints](#️-api-endpoints)
- [Development](#-development)
- [Security Features](#-security-features)
- [Testing](#-testing)
- [Cleanup](#-cleanup)
- [License](#-license)
- [Contributing](#-contributing)

## 🖼️ Demo

![Demo Screenshot](demo-screenshot.png)

_Note: This project is for demonstration purposes._

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

## 🛠️ Tech Stack

- Java 17 & Spring Boot 2.7.x
- Node.js 16+ & TypeScript 4.x
- Express.js
- Docker

## 📋 Prerequisites

Before you begin, ensure you have the following installed:
- [Docker](https://docs.docker.com/get-docker/) (version 20.x or higher)
- [Git](https://git-scm.com/downloads) (version 2.x or higher)
- [Java 17](https://adoptium.net/) (for local Spring Boot development)
- [Node.js](https://nodejs.org/) (version 16.x or higher, for local TypeScript development)

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

#### Spring Boot Service (<http://localhost:8080>):

```bash
# Upload files
curl -X POST -F "files=@test.txt" -F "files=@test.pdf" http://localhost:8080/api/upload

# List files
curl http://localhost:8080/api/files

# Download a file
curl -O http://localhost:8080/api/files/<filename>
```

#### TypeScript Service (<http://localhost:3000>):

```bash
# Upload files
curl -X POST -F "files=@test.txt" -F "files=@test.pdf" http://localhost:3000/upload

# List files
curl http://localhost:3000/files

# Download a file
curl -O http://localhost:3000/files/<filename>
```

## 📦 Project Structure

```plaintext
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

- `uploader/`: Java Spring Boot file upload service
- `ts-upload-service/`: Node.js/TypeScript file upload service
- `README.md`: Project documentation

## 🛠️ API Endpoints

Below are the main API endpoints for each service:

### Spring Boot Service (<http://localhost:8080>)

- `POST /api/upload` — Upload files (max 5 files, 5MB each)
- `GET /api/files` — List files
- `GET /api/files/:filename` — Download file

### TypeScript Service (<http://localhost:3000>)

- `POST /upload` — Upload files (max 5 files, 5MB each)
- `GET /files` — List files
- `GET /files/:filename` — Download file

## 📝 Development

To run tests and start the development server for each service:

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

Both services implement several security measures:

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

This project is licensed under the MIT License. See the [LICENSE](https://opensource.org/licenses/MIT) file for details.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📬 Contact

For questions or support, contact [your-email@example.com](mailto:your-email@example.com).

## ❓ FAQ

**Q: Can I upload files larger than 5MB?**
A: No, the maximum file size is 5MB per file.

**Q: What file types are allowed?**
A: Images, PDFs, and documents only.
