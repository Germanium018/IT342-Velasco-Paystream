import { LayoutDashboard, Banknote, Bell, Settings, LogOut, User } from 'lucide-react';
import { useNavigate, Link } from 'react-router-dom';

const Header = () => {
  // CORRECTED: It must be useNavigate(), not navigate()
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  return (
    <nav style={{ backgroundColor: '#1e293b', padding: '0 40px', height: '70px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', color: 'white' }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '40px' }}>
        <h2 style={{ fontWeight: 700, fontSize: '1.5rem', margin: 0 }}>PayStream</h2>
        <div style={{ display: 'flex', gap: '24px' }}>
          <Link to="/dashboard" style={{ color: 'white', display: 'flex', alignItems: 'center', gap: '8px', fontWeight: 600 }}>
            <LayoutDashboard size={20} /> Dashboard
          </Link>
          <Link to="/payroll" style={{ color: '#94a3b8', display: 'flex', alignItems: 'center', gap: '8px' }}>
            <Banknote size={20} /> Payroll
          </Link>
        </div>
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
        <Bell size={20} style={{ cursor: 'pointer', color: '#94a3b8' }} />
        <Settings size={20} style={{ cursor: 'pointer', color: '#94a3b8' }} />
        <div style={{ width: '36px', height: '36px', borderRadius: '50%', backgroundColor: '#3b82f6', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <User size={20} />
        </div>
        <LogOut size={20} onClick={handleLogout} style={{ cursor: 'pointer', color: '#ef4444' }} />
      </div>
    </nav>
  );
};

export default Header;