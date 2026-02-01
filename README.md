# Task Flow

> **Elevate your productivity.**
> A premium, modern To-Do application built with a custom Java HTTP server and a Progressive Web App (PWA) frontend.

![Project Banner](screenshots/banner.png)

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
    %% Styles
    classDef neonBlue fill:#1a1a1a,stroke:#00f3ff,stroke-width:2px,color:#fff,shadow:0 0 10px #00f3ff;
    classDef neonPink fill:#1a1a1a,stroke:#bc13fe,stroke-width:2px,color:#fff;
    classDef neonGreen fill:#1a1a1a,stroke:#0aff0a,stroke-width:2px,color:#fff;
    classDef highlight fill:#252525,stroke:#fff,stroke-width:1px,color:#fff;

    User([User / Browser]):::neonBlue

    subgraph "Frontend (PWA)"
        direction TB
        UI[HTML/CSS/JS]:::neonPink
        SW[Service Worker]:::neonGreen
        Cache[Local Cache]:::highlight
    end

    subgraph "Backend (Java)"
        direction TB
        Server[SimpleTaskServer]:::neonBlue
        Handler[Request Handler]:::highlight
        DB[(tasks.dat)]:::neonPink
    end

    User ==>|HTTP Request| Server
    User -.->|Interacts| UI
    UI ==>|"API Calls (fetch)"| Server
    SW -.->|Caches Assets| Cache
    Server ==>|"Routes /api/*"| Handler
    Handler ==>|"Reads/Writes"| DB
    Server -.->|"Serves Static Files"| UI
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
    Visit [http://localhost:8000](http://localhost:8080)

## 🤝 Contributing

Contributions are welcome! Please fork the repository and open a pull request.

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).
