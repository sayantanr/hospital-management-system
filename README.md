# Hospital Management System (HMS)

A professional, full-stack Hospital Management System featuring a high-performance Java backend, a sleek Python frontend, and integrated automated Gmail notifications.

## 🚀 Quick Start

1. **Launch the System**: Run the `run_all.bat` file in the root directory. This will automatically start:
   - The Java Spring Boot Backend (Port 8080)
   - The Python Flask Frontend (Port 8501)
   - A Chrome browser window pointing to the system.

2. **Access the UI**: Open [http://127.0.0.1:8501](http://127.0.0.1:8501) in your browser.

## 🛠️ Tech Stack

- **Backend**: Java 8 with Spring Boot 2.7.x
- **Frontend**: Python 3.x with Flask and Vanilla CSS (Glassmorphism Design)
- **Database**: SQLite (Persistent storage via `hmsdb.db`)
- **API**: RESTful architecture

## 📧 Automated Gmail Setup

To enable automated email notifications for appointments, you must configure a **Google App Password**:

1. Enable **2-Step Verification** on your Google Account.
2. Go to [App Passwords](https://myaccount.google.com/apppasswords).
3. Generate a password for **"Mail"** on **"Windows Computer"**.
4. Copy the **16-character code**.
5. Enter your Gmail Address and this code in the **Gmail Automation Settings** at the bottom of the **Appointments** page in HMS.

## ✨ Key Features

- **Dashboard**: Real-time statistics and recent appointment tracking.
- **Patient Management**: Register patients and view full medical history in sleek glassmorphic modals.
- **Doctor Directory**: Manage doctor details and specializations.
- **Appointment Scheduling**: Book appointments with automated email notifications sent to both Doctor and Patient.
- **Contextual Search**: Real-time filtering across all tables.
- **One-Click Mailing**: Resend appointment details directly from the scheduling list.

## 📂 Project Structure

- `/backend`: Spring Boot source code and Maven configuration.
- `/frontend`: Flask application, HTML templates, and static CSS/JS assets.
- `run_all.bat`: Master script to initialize the full environment.
- `hmsdb.db`: Persistent SQLite database file.
