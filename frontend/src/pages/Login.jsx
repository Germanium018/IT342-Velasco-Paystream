import React, { useState } from 'react';
import axios from 'axios'; // Added for backend communication
import { useNavigate } from 'react-router-dom'; // Added for redirection
import { Mail, Lock, Eye, EyeOff, ArrowRight, LayoutDashboard } from 'lucide-react';
import '../App.css';

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const navigate = useNavigate(); // Initialize navigation

  const handleLogin = async (e) => {
    e.preventDefault();
    
    try {
      // Sending credentials to the backend
      const response = await axios.post('http://localhost:8080/api/v1/auth/login', {
        email: email,
        password: password
      });

      if (response.data.success) {
        // Store user data (like role) in localStorage for session management
        localStorage.setItem('user', JSON.stringify(response.data.user));
        
        // Role-based redirection logic
        const role = response.data.user.role;
        if (role === 'ROLE_ADMIN') {
          navigate('/admin-dashboard');
        } else {
          navigate('/employee-dashboard');
        }
      }
    } catch (error) {
      console.error("Login Error:", error);
      alert('Login Failed: ' + (error.response?.data?.message || "Invalid Email or Password"));
    }
  };

  return (
    <div className="auth-container">
      <header className="auth-header">
        <div className="logo-container">
          <div style={{ backgroundColor: '#3B82F6', padding: '6px', borderRadius: '6px', display: 'flex' }}>
            <LayoutDashboard size={20} color="white" />
          </div>
          PayStream
        </div>
        <a href="#" className="support-link">Support</a>
      </header>

      <main className="auth-content">
        <div className="auth-card">
          <h1>Welcome back</h1>
          <p className="auth-subtitle">Secure access to your business finances</p>

          <form onSubmit={handleLogin}>
            <div className="form-group">
              <label>Email Address</label>
              <div className="input-wrapper">
                <Mail className="input-icon" size={18} />
                <input 
                  type="email" 
                  placeholder="name@company.com" 
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required 
                />
              </div>
            </div>

            <div className="form-group">
              <div className="label-row">
                <label>Password</label>
                <a href="#" className="forgot-password">Forgot password?</a>
              </div>
              <div className="input-wrapper">
                <Lock className="input-icon" size={18} />
                <input 
                  type={showPassword ? "text" : "password"} 
                  placeholder="••••••••" 
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required 
                />
                <div className="eye-icon" onClick={() => setShowPassword(!showPassword)}>
                  {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                </div>
              </div>
            </div>

            <div className="remember-me">
              <input type="checkbox" id="remember" />
              <label htmlFor="remember">Remember this device</label>
            </div>

            <button type="submit" className="btn-primary">
              Sign In <ArrowRight size={18} />
            </button>
          </form>

          <div className="divider">
            <span>Or continue with</span>
          </div>

          <button className="btn-google" type="button">
            <img src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/action/google.svg" alt="Google" width="18" />
            Google
          </button>

          <div className="register-footer">
            New to PayStream? <a href="/register">Create an account</a>
          </div>
        </div>
      </main>

      <footer className="main-footer">
        <p>© 2024 PayStream Inc. All rights reserved.</p>
        <div className="footer-links">
          <a href="#">Privacy Policy</a>
          <a href="#">Terms of Service</a>
          <a href="#">Cookies</a>
        </div>
        <p style={{ marginTop: '12px', fontSize: '10px', opacity: 0.6 }}>
          🔒 End-to-end encrypted connection
        </p>
      </footer>
    </div>
  );
};

export default Login;