# Task Flow

> **Elevate your productivity.**
> A premium, modern To-Do application built with a custom Java HTTP server and a Progressive Web App (PWA) frontend.

![Project Banner](screenshots/banner.png)
_(Replace `screenshots/banner.png` with a screenshot of your app)_

## 🌟 Features

- **Premium UI/UX**: Glassmorphism design, neon glow effects, and smooth animations.
- **Progressive Web App**: Installable on devices, offline-capable (Service Worker caching).
- **Custom Backend**: Lightweight Java HTTP server (No external frameworks like Spring/Tomcat needed).
- **Task Management**: Create, Edit, Delete, and Toggle status of tasks.
- **Smart Filtering**: Filter by All, Ongoing, and Completed tasks.
- **Responsive**: Fully optimized for Desktop and Mobile.

## 🎨 Visual Representation

### System Architecture

```mermaid
graph TD
    User[User / Browser]
    subgraph "Frontend (PWA)"
        UI[HTML/CSS/JS]
        SW[Service Worker]
        Cache[Local Cache]
    end
    subgraph "Backend (Java)"
        Server[SimpleTaskServer]
        Handler[Request Handler]
        DB[(tasks.dat)]
    end

    User -->|HTTP Request| Server
    User -->|Interacts| UI
    UI -->|"API Calls (fetch)"| Server
    SW -->|Caches Assets| Cache
    Server -->|"Routes /api/*"| Handler
    Handler -->|"Reads/Writes"| DB
    Server -->|"Serves Static Files"| UI
```

### Request Flow

```mermaid
sequenceDiagram
    participant Client as Browser
    participant Server as SimpleTaskServer
    participant Data as tasks.dat

    Client->>Server: GET /index.html
    Server-->>Client: Returns HTML/CSS/JS

    Client->>Server: GET /api/tasks
    Server->>Data: Load Tasks
    Data-->>Server: JSON Data
    Server-->>Client: Returns JSON List ([...])

    Client->>Server: POST /api/tasks (New Task)
    Server->>Data: Append Task
    Server-->>Client: 200 OK
```

## 🛠️ Tech Stack

- **Backend**: Java (JDK 8+), `com.sun.net.httpserver`
- **Frontend**: HTML5, CSS3 (Variables, Flexbox, Animations), JavaScript (ES6+)
- **Storage**: Local binary file storage (`tasks.dat`)

## 🚀 Getting Started

### Prerequisites

- Java Development Kit (JDK 8 or higher)
- Git

### Installation

1.  **Clone the repository**

    ```bash
    git clone https://github.com/10Niranjan/ToDo_List-.git
    cd ToDo_List-
    ```

2.  **Compile the Server**

    ```bash
    javac -sourcepath src -d out src/SimpleTaskServer.java
    ```

3.  **Run the Application**

    ```bash
    java -cp out SimpleTaskServer
    ```

4.  **Open in Browser**
    Visit [http://localhost:8000](http://localhost:8000)

## 📸 Screenshots

|             Light/Dark Mode             |                 Mobile View                 |
| :-------------------------------------: | :-----------------------------------------: |
| ![Main View](screenshots/main-view.png) | ![Mobile View](screenshots/mobile-view.png) |
|               _Dashboard_               |             _Responsive Design_             |

## 🤝 Contributing

Contributions are welcome! Please fork the repository and open a pull request.

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).
