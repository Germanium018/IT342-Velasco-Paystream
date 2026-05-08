import { LogOut, User, Bell, Settings } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

const HeaderEmployee = ({ firstName }) => {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    localStorage.removeItem('role');
    navigate('/login');
  };

  return (
    <nav style={{ backgroundColor: '#1e293b', padding: '0 40px', height: '70px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', color: 'white' }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
        <h2 style={{ fontWeight: 700, fontSize: '1.5rem', margin: 0 }}>PayStream</h2>
        <span style={{ backgroundColor: '#334155', padding: '4px 12px', borderRadius: '4px', fontSize: '0.75rem', color: '#94a3b8', fontWeight: 600 }}>
          EMPLOYEE ACCESS
        </span>
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginRight: '10px' }}>
          <span style={{ fontSize: '0.9rem', color: '#94a3b8' }}>Welcome, {firstName}</span>
        </div>
        <Bell size={20} style={{ cursor: 'pointer', color: '#94a3b8' }} />
        
        <div style={{ width: '36px', height: '36px', borderRadius: '50%', backgroundColor: '#10b981', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <User size={20} />
        </div>

        <button 
          onClick={handleLogout} 
          style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#ef4444', display: 'flex', alignItems: 'center' }}
          title="Logout"
        >
          <LogOut size={22} />
        </button>
      </div>
    </nav>
  );
};

export default HeaderEmployee;