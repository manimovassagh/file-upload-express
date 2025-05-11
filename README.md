# File Upload Service (Spring Boot & Express)

This project demonstrates a dual-backend file upload system using **Spring Boot** (Java) and **Express** (Node.js), both fully dockerized and networked via Docker Compose.

## 🏗️ Architecture

- **spring-boot-service**: Handles file uploads, storage, listing, and download (Java, port 8080)
- **express-service**: Accepts file uploads, proxies requests to Spring Boot, and exposes a Node.js API (port 3000)
- **Docker Compose**: Orchestrates both services and their networking

```
[Client] ──► [Express (3000)] ──► [Spring Boot (8080)]
```

## 🚀 Quick Start

### 1. **Clone the repository**
```bash
git clone https://github.com/manimovassagh/file-upload-express.git
cd file-upload-express
```

### 2. **Build & Run with Docker Compose**
```bash
docker compose -f uploader/docker-compose.yml up --build -d
```

### 3. **Check Health**
- Express: [http://localhost:3000/health](http://localhost:3000/health)
- Spring Boot: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

### 4. **Test File Upload (via Express)**
```bash
curl -i -X POST -F "files=@uploader/src/test/http/test-files/test1.jpg" http://localhost:3000/api/upload
```

### 5. **List Files**
```bash
curl -i http://localhost:3000/api/files
```

### 6. **Download a File**
```bash
curl -i http://localhost:3000/api/files/<filename>
```

### 7. **Stop Services**
```bash
docker compose -f uploader/docker-compose.yml down
```

---

## 📦 Project Structure

```
file-upload-express/
├── uploader/                # Spring Boot service (Java)
│   ├── src/
│   ├── Dockerfile
│   └── ...
├── express/                 # Express service (Node.js)
│   ├── src/
│   ├── Dockerfile
│   └── ...
├── uploader/docker-compose.yml
└── README.md
```

---

## 🛠️ API Endpoints

### Express Service (http://localhost:3000)
- `POST /api/upload` — Upload files (forwards to Spring Boot)
- `GET /api/files` — List files
- `GET /api/files/:filename` — Download file
- `GET /health` — Health check

### Spring Boot Service (http://localhost:8080)
- `POST /api/upload` — Upload files
- `GET /api/files` — List files
- `GET /api/files/:filename` — Download file
- `GET /actuator/health` — Health check

---

## 📝 Development

- **Spring Boot**: See `uploader/` for Java code, tests, and configs
- **Express**: See `express/` for Node.js code
- **Docker Compose**: See `uploader/docker-compose.yml`

### Build Spring Boot JAR (if needed)
```bash
cd uploader
./mvnw clean package -DskipTests
```

### Run Spring Boot Locally
```bash
cd uploader
./mvnw spring-boot:run
```

### Run Express Locally
```bash
cd express
npm install
npm run dev
```

---

## 🤝 Contributing
- Fork, branch, and PR welcome!
- Please add tests for new features.

---

## 🧹 Cleanup
To remove all containers, networks, and volumes:
```bash
docker compose -f uploader/docker-compose.yml down -v
```

---

## 📄 License
MIT 