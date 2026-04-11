import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register'; 
import AdminDashboard from './pages/AdminDashboard';
import PayrollProcessing from './pages/PayrollProcessing';
import Rates from './pages/Rates'; // Ensure you have created this page
import ProtectedRoute from './components/ProtectedRoute';
import './App.css';
import './index.css';

function App() {
  return (
    <Router>
      <Routes>
        {/* --- PUBLIC ROUTES --- */}
        {/* Root redirect to login */}
        <Route path="/" element={<Navigate to="/login" />} />
        
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        
        {/* --- PROTECTED ADMIN ROUTES --- 
            Wraps the Admin features in a security gate that only allows 'ROLE_ADMIN'
        */}
        <Route 
          path="/dashboard" 
          element={
            <ProtectedRoute allowedRoles={['ROLE_ADMIN']}>
              <AdminDashboard />
            </ProtectedRoute>
          } 
        />

        <Route 
          path="/employee-dashboard" 
          element={
            <ProtectedRoute allowedRoles={['ROLE_ADMIN']}>
              <AdminDashboard />
            </ProtectedRoute>
          } 
        />

        <Route 
          path="/payroll" 
          element={
            <ProtectedRoute allowedRoles={['ROLE_ADMIN']}>
              <PayrollProcessing />
            </ProtectedRoute>
          } 
        />

        <Route 
          path="/rates" 
          element={
            <ProtectedRoute allowedRoles={['ROLE_ADMIN']}>
              <Rates />
            </ProtectedRoute>
          } 
        />
        
        {/* --- CATCH-ALL REDIRECT --- 
            Prevents "White Screens" by sending invalid URLs back to Login
        */}
        <Route path="*" element={<Navigate to="/login" />} />
      </Routes>
    </Router>
  );
}

export default App;