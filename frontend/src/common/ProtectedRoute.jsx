import { Navigate } from 'react-router-dom';

const ProtectedRoute = ({ children, allowedRoles }) => {
  // Retrieve the role we saved in Step 1
  const userRole = localStorage.getItem('role');
  const token = localStorage.getItem('token');

  // If there is no token, they aren't even logged in
  if (!token) {
    return <Navigate to="/login" replace />;
  }

  // If their role is not in the allowed list, block them
  if (!allowedRoles.includes(userRole)) {
    // You can redirect them to a "Not Authorized" page or back to their own dashboard
    return <Navigate to="/login" replace />;
  }

  // If they pass the check, show the page
  return children;
};

export default ProtectedRoute;