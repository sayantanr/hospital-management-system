from flask import Flask, render_template, request, redirect, url_for, flash
import requests
import json
import os
import smtplib
from email.mime.text import MIMEText
from datetime import datetime

app = Flask(__name__)
app.secret_key = "super_secret_hms_key"

API_BASE_URL = "http://127.0.0.1:8080/api"

def fetch_data(endpoint):
    try:
        response = requests.get(f"{API_BASE_URL}/{endpoint}")
        if response.status_code == 200:
            return response.json()
    except requests.exceptions.ConnectionError:
        pass
    return []

def post_data(endpoint, data):
    try:
        response = requests.post(f"{API_BASE_URL}/{endpoint}", json=data)
        return response.status_code in [200, 201]
    except Exception:
        return False

SETTINGS_FILE = os.path.join(os.path.dirname(os.path.abspath(__file__)), "settings.json")

def load_settings():
    if os.path.exists(SETTINGS_FILE):
        with open(SETTINGS_FILE, "r") as f:
            return json.load(f)
    return {
        "smtp_email": "",
        "smtp_password": "",
        "patient_subject": "Appointment Confirmation",
        "patient_body": "Hello [PatientName],\n\nYour appointment with [DoctorName] is confirmed for [Date] at [Time].\n\nThank you.",
        "doctor_subject": "New Appointment Scheduled",
        "doctor_body": "Hello [DoctorName],\n\nYou have a new appointment with [PatientName] scheduled for [Date] at [Time]."
    }

def save_settings(settings):
    with open(SETTINGS_FILE, "w") as f:
        json.dump(settings, f, indent=4)

def send_email(to_address, subject, body, from_address=None):
    if not to_address:
        return False, "Recipient email is missing."
    settings = load_settings()
    if not settings.get("smtp_email") or not settings.get("smtp_password"):
        return False, "Gmail settings are not configured."
        
    try:
        msg = MIMEText(body)
        msg["Subject"] = subject
        msg["From"] = from_address if from_address else settings["smtp_email"]
        msg["To"] = to_address
        
        # Use a longer timeout and explicit EHLO
        server = smtplib.SMTP("smtp.gmail.com", 587, timeout=15)
        server.ehlo()
        server.starttls()
        server.ehlo()
        server.login(settings["smtp_email"], settings["smtp_password"])
        server.send_message(msg)
        server.quit()
        return True, "Success"
    except smtplib.SMTPAuthenticationError:
        return False, "Authentication Failed. Please ensure you are using a 'Gmail App Password', not your regular password."
    except Exception as e:
        print(f"Failed to send email: {e}")
        return False, str(e)

@app.route("/")
def dashboard():
    patients = fetch_data("patients")
    doctors = fetch_data("doctors")
    appointments = fetch_data("appointments")
    
    # Sort appointments to get recent ones
    recent_appointments = sorted(appointments, key=lambda x: x.get('id', 0), reverse=True)[:5] if appointments else []
    
    return render_template("dashboard.html", 
                           patients_count=len(patients), 
                           doctors_count=len(doctors), 
                           appointments_count=len(appointments),
                           recent_appointments=recent_appointments)

@app.route("/patients", methods=["GET", "POST"])
def patients():
    if request.method == "POST":
        data = {
            "firstName": request.form.get("firstName") or None,
            "lastName": request.form.get("lastName") or None,
            "dateOfBirth": request.form.get("dateOfBirth") or None,
            "gender": request.form.get("gender") or None,
            "contactNumber": request.form.get("contactNumber") or None,
            "email": request.form.get("email") or None,
            "address": request.form.get("address") or None,
            "medicalHistory": request.form.get("medicalHistory") or None
        }
        if post_data("patients", data):
            flash("Patient registered successfully!", "success")
        else:
            flash("Failed to register patient. Backend might be down.", "error")
        return redirect(url_for("patients"))
        
    patients_list = fetch_data("patients")
    return render_template("patients.html", patients=patients_list)

@app.route("/doctors", methods=["GET", "POST"])
def doctors():
    if request.method == "POST":
        data = {
            "firstName": request.form.get("firstName") or None,
            "lastName": request.form.get("lastName") or None,
            "specialization": request.form.get("specialization") or None,
            "contactNumber": request.form.get("contactNumber") or None,
            "email": request.form.get("email") or None,
            "dutySchedule": request.form.get("dutySchedule") or None
        }
        if post_data("doctors", data):
            flash("Doctor added successfully!", "success")
        else:
            flash("Failed to add doctor.", "error")
        return redirect(url_for("doctors"))
        
    doctors_list = fetch_data("doctors")
    return render_template("doctors.html", doctors=doctors_list)

@app.route("/appointments", methods=["GET", "POST"])
def appointments():
    if request.method == "POST":
        date_str = request.form.get("appointmentDate")
        time_str = request.form.get("appointmentTime")
        
        datetime_str = f"{date_str}T{time_str}" if date_str and time_str else None
        
        data = {
            "patient": {"id": request.form.get("patientId")},
            "doctor": {"id": request.form.get("doctorId")},
            "appointmentDate": datetime_str,
            "status": request.form.get("status") or None,
            "notes": request.form.get("notes") or None
        }
        if post_data("appointments", data):
            flash("Appointment booked successfully!", "success")
            
            # Attempt to send automated emails
            try:
                settings = load_settings()
                patient = next((p for p in fetch_data("patients") if str(p['id']) == request.form.get("patientId")), None)
                doctor = next((d for d in fetch_data("doctors") if str(d['id']) == request.form.get("doctorId")), None)
                
                if patient and doctor:
                    dt = datetime_str.split('T')
                    date_val = dt[0]
                    time_val = dt[1]
                    
                    # Send to Patient
                    p_body = request.form.get("notes") or "New Appointment Scheduled"
                    send_email(patient.get("email"), "Appointment Notification", p_body)
                    
                    # Send to Doctor
                    d_body = request.form.get("notes") or "New Appointment Scheduled"
                    send_email(doctor.get("email"), "New Appointment", d_body)
            except Exception as e:
                print(f"Notification error: {e}")
                
        else:
            flash("Failed to book appointment.", "error")
        return redirect(url_for("appointments"))
        
    appointments_list = fetch_data("appointments")
    patients_list = fetch_data("patients")
    doctors_list = fetch_data("doctors")
    
    return render_template("appointments.html", 
                           appointments=appointments_list,
                           patients=patients_list,
                           doctors=doctors_list,
                           settings=load_settings())

@app.route("/resend-email/<int:appt_id>", methods=["POST"])
def resend_email(appt_id):
    settings = load_settings()
    if not settings.get("smtp_email") or not settings.get("smtp_password"):
        flash("Please configure your Gmail Automation Settings at the bottom of the page first!", "warning")
        return redirect(url_for("appointments"))

    appointments = fetch_data("appointments")
    # Robust ID check (handle both int and string)
    appt = next((a for a in appointments if str(a.get('id')) == str(appt_id)), None)
    
    if not appt:
        flash("Appointment not found.", "error")
        return redirect(url_for("appointments"))
        
    patient_email = appt.get('patient', {}).get('email')
    doctor_email = appt.get('doctor', {}).get('email')
    notes = appt.get('notes') or "Appointment Notification"
    
    patient_sent = False
    doctor_sent = False
    error_msg = ""
    
    if patient_email:
        success, msg = send_email(patient_email, "Appointment Notification", notes)
        if success:
            patient_sent = True
        else:
            error_msg = msg
            
    if doctor_email:
        success, msg = send_email(doctor_email, "New Appointment Details", notes)
        if success:
            doctor_sent = True
        else:
            if not error_msg: error_msg = msg
            
    if patient_sent and doctor_sent:
        flash(f"Emails successfully sent to both Patient and Doctor!", "success")
    elif patient_sent:
        flash(f"Email sent to Patient, but Doctor failed: {error_msg}", "warning")
    elif doctor_sent:
        flash(f"Email sent to Doctor, but Patient failed: {error_msg}", "warning")
    else:
        flash(f"Failed to send emails: {error_msg}", "error")
        
    return redirect(url_for("appointments"))

@app.route("/update-gmail-settings", methods=["POST"])
def update_gmail_settings():
    settings = load_settings()
    settings["smtp_email"] = request.form.get("smtp_email", "").strip()
    settings["smtp_password"] = request.form.get("smtp_password", "").strip()
    save_settings(settings)
    flash("Gmail Automation Settings updated! Whitespace removed.", "success")
    return redirect(url_for("appointments"))

if __name__ == "__main__":
    app.run(debug=True, use_reloader=False, port=8501)
