import { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const Login = () => {
  const [credentials, setCredentials] = useState({ email: '', password: '' });
  const navigate = useNavigate();

  const handleChange = (e) => {
    setCredentials({ ...credentials, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post('http://localhost:8080/api/v1/auth/login', credentials);
      if (response.data.success) {
        // For Phase 1, we just store the user object in localStorage
        localStorage.setItem('user', JSON.stringify(response.data.user));
        alert("Login Successful!");
        navigate('/dashboard');
      }
    } catch (error) {
      alert("Invalid Credentials");
    }
  };

  return (
    <div style={{ maxWidth: '400px', margin: '50px auto' }}>
      <h2>PayStream Login</h2>
      <form onSubmit={handleSubmit}>
        <input type="email" name="email" placeholder="Email" onChange={handleChange} required />
        <input type="password" name="password" placeholder="Password" onChange={handleChange} required />
        <button type="submit">Login</button>
      </form>
      <hr />
      <button style={{ backgroundColor: '#fff', color: '#000' }}>Sign in with Google</button>
    </div>
  );
};

export default Login;