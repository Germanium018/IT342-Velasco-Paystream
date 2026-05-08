import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './features/auth/Login';
import Register from './features/auth/Register'; 
import AdminDashboard from './features/employee/AdminDashboard';
import EmployeeDashboard from './features/employee/EmployeeDashboard'; 
import PayrollProcessing from './features/payroll/PayrollProcessing';
import Payslips from './features/payroll/Payslips'; 
import Rates from './features/rates/Rates';
import OAuth2RedirectHandler from './features/auth/OAuth2RedirectHandler';
import ProtectedRoute from './common/ProtectedRoute';
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