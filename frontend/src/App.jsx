import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register'; 
import AdminDashboard from './pages/AdminDashboard';
import PayrollProcessing from './pages/PayrollProcessing';
import './App.css';
import './index.css';

function App() {
  return (
    <Router>
      <Routes>
        {/* 1. Redirect empty path to login */}
        <Route path="/" element={<Navigate to="/login" />} />
        
        {/* 2. Authentication Routes */}
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        
        {/* 3. Dashboard Routes (Mapped both to be safe) */}
        <Route path="/dashboard" element={<AdminDashboard />} />
        <Route path="/employee-dashboard" element={<AdminDashboard />} />
        
        {/* 4. Payroll Processing Route */}
        <Route path="/payroll" element={<PayrollProcessing />} />
        
        {/* 5. Catch-all: If a URL doesn't exist, send them back to login */}
        <Route path="*" element={<Navigate to="/login" />} />
      </Routes>
    </Router>
  );
}

export default App;