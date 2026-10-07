# 🎓 EduManage: College Administration ERP

![EduManage Dashboard](https://img.shields.io/badge/Status-Active-brightgreen) ![Spring Boot](https://img.shields.io/badge/Backend-Spring_Boot_3.2-6DB33F?logo=spring) ![Java](https://img.shields.io/badge/Java-17-007396?logo=java) ![TailwindCSS](https://img.shields.io/badge/UI-Tailwind_CSS-38B2AC?logo=tailwind-css)

A full-stack College Administration System designed to streamline university operations. This web application provides a modern, responsive dashboard to manage student records, course catalogs, and track system metrics in real-time.

## ✨ Features
* **Interactive Dashboard:** Real-time metrics overview and statistical data visualization using Chart.js (Demographics & Course Credits).
* **Student Directory:** Add and manage student records dynamically.
* **Course Catalog:** Easily register and administer active university courses.
* **Modern UI/UX:** Responsive Single Page Application (SPA) built with Tailwind CSS, featuring modal forms and non-blocking asynchronous data fetching.
* **RESTful Architecture:** Seamless JSON communication between the vanilla JS frontend and the Java Spring Boot backend.

## 🛠️ Tech Stack
* **Backend:** Java 17, Spring Boot 3.2.4 (Spring Web, Spring Data JPA)
* **Frontend:** HTML5, Tailwind CSS, Vanilla JavaScript (ES6 Fetch API)
* **Database:** H2 In-Memory Database (Configured for testing)
* **Data Visualization:** Chart.js, FontAwesome Icons

## 🚀 Getting Started

### Prerequisites
* **Java 17** or higher installed.
* **Maven** installed.

### Installation & Run
1. Clone this repository:
   ```bash
   git clone https://github.com/yourusername/EduManage.git
   ```
2. Navigate to the project directory:
   ```bash
   cd EduManage
   ```
3. Run the Spring Boot server:
   ```bash
   mvn spring-boot:run
   ```
4. Open your browser and navigate to:
   `http://localhost:8080/`

*(Note: Windows users can simply double-click the `Start_College_ERP.bat` script to automate the launch sequence).*

## 📈 Future Enhancements
* Authentication & Role-based Access Control (Admin vs Student portals).
* Persistent database integration (PostgreSQL / MySQL).
* Advanced reporting and CSV exports.

---
*Created as a demonstration of full-stack development bridging robust Java backends with modern frontend design paradigms.*
