import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register'; 
import AdminDashboard from './pages/AdminDashboard';
import EmployeeDashboard from './pages/EmployeeDashboard'; // NEW IMPORT
import PayrollProcessing from './pages/PayrollProcessing';
import Payslips from './pages/Payslips'; 
import Rates from './pages/Rates';
import OAuth2RedirectHandler from './pages/OAuth2RedirectHandler';
import ProtectedRoute from './components/ProtectedRoute';
import './App.css';
import './index.css';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Navigate to="/login" />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        
        <Route path="/oauth2/redirect" element={<OAuth2RedirectHandler />} />
        
        <Route 
          path="/dashboard" 
          element={
            <ProtectedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_EMPLOYEE']}>
              <AdminDashboard />
            </ProtectedRoute>
          } 
        />

        <Route 
          path="/employee-dashboard" 
          element={
            <ProtectedRoute allowedRoles={['ROLE_EMPLOYEE']}>
              <EmployeeDashboard />
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
          path="/payslips" 
          element={
            <ProtectedRoute allowedRoles={['ROLE_ADMIN']}>
              <Payslips />
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
        
        <Route path="*" element={<Navigate to="/login" />} />
      </Routes>
    </Router>
  );
}

export default App;