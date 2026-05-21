import React, { useState } from 'react';
import axios from 'axios'; 
import { useNavigate, Link } from 'react-router-dom';
import { Mail, Lock, Eye, EyeOff, ArrowRight, LayoutDashboard, Github } from 'lucide-react';
import '../../App.css';

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post('http://localhost:8080/api/v1/auth/login', {
        email: email,
        password: password
      });

      if (response.data.success) {
        localStorage.setItem('token', response.data.token);
        localStorage.setItem('role', response.data.role);
        localStorage.setItem('user', JSON.stringify(response.data.user));
        
        if (response.data.role === 'ROLE_ADMIN') {
          navigate('/dashboard');
        } else {
          navigate('/employee-dashboard');
        }
      }
    } catch (error) {
      console.error("Login Error:", error);
      alert('Login Failed: ' + (error.response?.data?.message || "Invalid Email or Password"));
    }
  };

  // Logic to trigger GitHub OAuth Flow
  const handleGithubLogin = () => {
    // This redirects the entire browser to the Spring Boot OAuth2 entry point
    window.location.href = 'http://localhost:8080/oauth2/authorization/github';
  };

  return (
    <div className="auth-container">
      

      <main className="auth-content">
        <div className="auth-card">
          <h1>Welcome to PayStream</h1>
          

          <form onSubmit={handleLogin}>
            <div className="form-group">
              <label>Email Address</label>
              <div className="input-wrapper">
                <Mail className="input-icon" size={18} />
                <input 
                  type="email" 
                  placeholder="Enter your email" 
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required 
                />
              </div>
            </div>

            <div className="form-group">
              
              <div className="input-wrapper">
                <Lock className="input-icon" size={18} />
                <input 
                  type={showPassword ? "text" : "password"} 
                  placeholder="Enter your password" 
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required 
                />
                <div className="eye-icon" onClick={() => setShowPassword(!showPassword)}>
                  {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                </div>
              </div>
            </div>

            <button type="submit" className="btn-primary">
              Sign In <ArrowRight size={18} />
            </button>
          </form>

          <div className="divider">
            <span>Or continue with</span>
          </div>

          {/* UPDATED GITHUB BUTTON */}
          <button 
            className="btn-google" 
            type="button" 
            onClick={handleGithubLogin}
            style={{ backgroundColor: '#24292F', color: 'white', border: 'none' }}
          >
            <Github size={20} />
            <span>Sign in with GitHub</span>
          </button>

          <div className="register-footer">
            New to PayStream? <Link to="/register">Create an account</Link>
          </div>
        </div>
      </main>

      <footer className="main-footer">
        <p>© 2026 PayStream Inc. All rights reserved.</p>
        
      </footer>
    </div>
  );
};

export default Login;