import React, { useState } from 'react';
import axios from 'axios'; // Essential for API communication
import { useNavigate } from 'react-router-dom'; // Essential for page redirection
import { 
  User, 
  Mail, 
  Lock, 
  Eye, 
  EyeOff, 
  ChevronDown, 
  ArrowLeft, 
  LayoutDashboard 
} from 'lucide-react';
import '../App.css';

const Register = () => {
  const [showPassword, setShowPassword] = useState(false);
  const navigate = useNavigate(); // Hook to redirect after success
  
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    role: 'Employee'
  });

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    console.log('Attempting to register:', formData);
    
    try {
      // Mapping frontend state to the exact keys your Java Controller expects
      const response = await axios.post('http://localhost:8080/api/v1/auth/register', {
        firstname: formData.firstName,
        lastname: formData.lastName,
        email: formData.email,
        password: formData.password,
        role: "ROLE_" + formData.role.toUpperCase() // Formats to 'ROLE_ADMIN' or 'ROLE_EMPLOYEE'
      });

      if (response.data.success) {
        alert('Account created successfully!');
        navigate('/login'); // Moves user to login page
      }
    } catch (error) {
      console.error("Registration Error:", error);
      // Extracts the error message from the backend if available
      alert('Registration Failed: ' + (error.response?.data?.message || "Check if Backend is running"));
    }
  };

  return (
    <div className="auth-container">
      <main className="auth-content">
        <div className="auth-card">
          <div className="logo-container" style={{ justifyContent: 'center', marginBottom: '24px' }}>
            <div style={{ backgroundColor: 'var(--secondary-action)', padding: '8px', borderRadius: '8px', display: 'flex' }}>
              <LayoutDashboard size={24} color="white" />
            </div>
            <span style={{ fontSize: '1.5rem' }}>PayStream</span>
          </div>

          <h1>Create your account</h1>
          <p className="auth-subtitle">Start managing your streams with PayStream</p>

          <form onSubmit={handleSubmit}>
            <div style={{ display: 'flex', gap: '16px', marginBottom: '20px' }}>
              <div className="form-group" style={{ flex: 1, marginBottom: 0 }}>
                <label>First Name</label>
                <div className="input-wrapper">
                  <User className="input-icon" size={18} />
                  <input 
                    type="text" 
                    name="firstName"
                    placeholder="John" 
                    value={formData.firstName}
                    onChange={handleChange}
                    required 
                  />
                </div>
              </div>
              <div className="form-group" style={{ flex: 1, marginBottom: 0 }}>
                <label>Last Name</label>
                <div className="input-wrapper">
                  <User className="input-icon" size={18} />
                  <input 
                    type="text" 
                    name="lastName"
                    placeholder="Doe" 
                    value={formData.lastName}
                    onChange={handleChange}
                    required 
                  />
                </div>
              </div>
            </div>

            <div className="form-group">
              <label>Email Address</label>
              <div className="input-wrapper">
                <Mail className="input-icon" size={18} />
                <input 
                  type="email" 
                  name="email"
                  placeholder="name@company.com" 
                  value={formData.email}
                  onChange={handleChange}
                  required 
                />
              </div>
            </div>

            <div className="form-group">
              <label>Password</label>
              <div className="input-wrapper">
                <Lock className="input-icon" size={18} />
                <input 
                  type={showPassword ? "text" : "password"} 
                  name="password"
                  placeholder="••••••••" 
                  minLength="8"
                  value={formData.password}
                  onChange={handleChange}
                  required 
                />
                <div className="eye-icon" onClick={() => setShowPassword(!showPassword)}>
                  {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                </div>
              </div>
            </div>

            <button type="submit" className="btn-primary" style={{ marginTop: '10px' }}>
              Sign Up
            </button>
          </form>

          <div className="divider">
            <span>OR</span>
          </div>

          <a href="/login" className="back-link" style={{ 
            display: 'flex', 
            alignItems: 'center', 
            justifyContent: 'center', 
            gap: '8px', 
            fontWeight: '600',
            fontSize: '0.9rem' 
          }}>
            <ArrowLeft size={18} /> Back to Login
          </a>
        </div>
      </main>

      <footer className="main-footer">
        <p>© 2024 PayStream Inc. All rights reserved.</p>
        <div className="footer-links">
          <a href="#">Terms of Service</a>
          <a href="#">Privacy Policy</a>
        </div>
      </footer>
    </div>
  );
};

export default Register;