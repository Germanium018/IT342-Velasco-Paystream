import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';       // Adjust path if needed
import Register from './pages/Register'; // Adjust path if needed
import './App.css';
import './index.css';

function App() {
  return (
    <Router>
      <Routes>
        {/* Redirect empty path to login */}
        <Route path="/" element={<Navigate to="/login" />} />
        
        {/* Define your routes */}
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        
        {/* Placeholder for Dashboard */}
        <Route path="/dashboard" element={<div style={{padding: '20px'}}><h1>Dashboard Coming Soon</h1></div>} />
      </Routes>
    </Router>
  );
}

export default App;