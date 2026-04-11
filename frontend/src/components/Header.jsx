import { LayoutDashboard, Banknote, Zap, Bell, Settings, LogOut, User } from 'lucide-react';
import { useNavigate, Link, useLocation } from 'react-router-dom';

const Header = () => {
  const navigate = useNavigate();
  const location = useLocation(); // Used to highlight the active tab

  const handleLogout = () => {
    // SECURITY: Clear all three keys to prevent unauthorized session persistence
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    localStorage.removeItem('role');
    navigate('/login');
  };

  // Helper to determine if a link is active based on the URL
  const isActive = (path) => location.pathname === path;

  return (
    <nav style={{ backgroundColor: '#1e293b', padding: '0 40px', height: '70px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', color: 'white' }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '40px' }}>
        <h2 style={{ fontWeight: 700, fontSize: '1.5rem', margin: 0 }}>PayStream</h2>
        
        <div style={{ display: 'flex', gap: '24px' }}>
          <Link 
            to="/dashboard" 
            style={{ 
              color: isActive('/dashboard') ? 'white' : '#94a3b8', 
              display: 'flex', 
              alignItems: 'center', 
              gap: '8px', 
              fontWeight: isActive('/dashboard') ? 600 : 400 
            }}
          >
            <LayoutDashboard size={20} /> Dashboard
          </Link>
          
          <Link 
            to="/payroll" 
            style={{ 
              color: isActive('/payroll') ? 'white' : '#94a3b8', 
              display: 'flex', 
              alignItems: 'center', 
              gap: '8px',
              fontWeight: isActive('/payroll') ? 600 : 400 
            }}
          >
            <Banknote size={20} /> Payroll
          </Link>

          <Link 
            to="/rates" 
            style={{ 
              color: isActive('/rates') ? 'white' : '#94a3b8', 
              display: 'flex', 
              alignItems: 'center', 
              gap: '8px',
              fontWeight: isActive('/rates') ? 600 : 400 
            }}
          >
            <Zap size={20} /> Rates
          </Link>
        </div>
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
        <Bell size={20} style={{ cursor: 'pointer', color: '#94a3b8' }} title="Notifications" />
        <Settings size={20} style={{ cursor: 'pointer', color: '#94a3b8' }} title="Settings" />
        
        <div style={{ 
          width: '36px', 
          height: '36px', 
          borderRadius: '50%', 
          backgroundColor: '#3b82f6', 
          display: 'flex', 
          alignItems: 'center', 
          justifyContent: 'center' 
        }}>
          <User size={20} />
        </div>

        <button 
          onClick={handleLogout} 
          style={{ 
            background: 'none', 
            border: 'none', 
            padding: 0, 
            cursor: 'pointer', 
            display: 'flex', 
            alignItems: 'center', 
            color: '#ef4444' 
          }}
          title="Logout"
        >
          <LogOut size={22} />
        </button>
      </div>
    </nav>
  );
};

export default Header;