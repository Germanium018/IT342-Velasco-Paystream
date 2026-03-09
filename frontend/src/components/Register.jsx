import { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const Register = () => {
  const [formData, setFormData] = useState({
    firstname: '',
    lastname: '',
    email: '',
    password: '',
    role: 'ROLE_EMPLOYEE' // Default role
  });
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post('http://localhost:8080/api/v1/auth/register', formData);
      if (response.data.success) {
        alert("Registration Successful!");
        navigate('/login');
      }
    } catch (error) {
      alert("Registration Failed: " + (error.response?.data?.message || "Server Error"));
    }
  };

  return (
    <div style={{ maxWidth: '400px', margin: '50px auto' }}>
      <h2>PayStream Registration</h2>
      <form onSubmit={handleSubmit}>
        <input type="text" name="firstname" placeholder="First Name" onChange={handleChange} required block />
        <input type="text" name="lastname" placeholder="Last Name" onChange={handleChange} required block />
        <input type="email" name="email" placeholder="Email" onChange={handleChange} required block />
        <input type="password" name="password" placeholder="Password (min 8 chars)" onChange={handleChange} required block />
        <select name="role" onChange={handleChange}>
          <option value="ROLE_EMPLOYEE">Employee</option>
          <option value="ROLE_ADMIN">Admin</option>
        </select>
        <button type="submit">Sign Up</button>
      </form>
    </div>
  );
};

export default Register;