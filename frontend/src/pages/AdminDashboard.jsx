import { useState, useEffect } from 'react';
import axios from 'axios';
import Header from '../components/Header';
import { Search, Edit2, X, Loader2, Info, Wallet } from 'lucide-react';

const AdminDashboard = () => {
  const [employees, setEmployees] = useState([]);
  const [rates, setRates] = useState([]); 
  const [searchTerm, setSearchTerm] = useState('');
  const [editingEmployee, setEditingEmployee] = useState(null);
  const [loading, setLoading] = useState(true);

  const getAuthHeader = () => ({
    headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
  });

  const fetchDashboardData = async () => {
    try {
      const [empRes, rateRes] = await Promise.all([
        axios.get('http://localhost:8080/api/v1/employees', getAuthHeader()),
        axios.get('http://localhost:8080/api/v1/rates', getAuthHeader())
      ]);
      setEmployees(Array.isArray(empRes.data) ? empRes.data : []);
      setRates(Array.isArray(rateRes.data) ? rateRes.data : []);
    } catch (error) {
      console.error("Failed to fetch dashboard data:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchDashboardData(); }, []);

  const handleUpdate = async (e) => {
    e.preventDefault();
    try {
      await axios.put(
        `http://localhost:8080/api/v1/employees/${editingEmployee.id}`,
        editingEmployee,
        getAuthHeader()
      );
      alert("Employee profile updated!");
      setEditingEmployee(null);
      fetchDashboardData(); 
    } catch (error) {
      alert("Update failed.");
    }
  };

  const filteredEmployees = employees.filter(emp => {
    const fullName = `${emp.user?.firstname || ''} ${emp.user?.lastname || ''}`.toLowerCase();
    return fullName.includes(searchTerm.toLowerCase()) || emp.id.toString().includes(searchTerm);
  });

  if (loading) return (
    <div className="dashboard-container">
      <Header />
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '300px' }}>
        <Loader2 className="animate-spin" size={32} color="#3b82f6" />
      </div>
    </div>
  );

  return (
    <div className="dashboard-container">
      <Header />
      <main className="dashboard-content">
        <header className="action-header">
          <div>
            <h1 style={{ fontSize: '2rem', color: '#0f172a', fontWeight: 700 }}>Employee Directory</h1>
            <p style={{ color: '#64748b' }}>Connected to PayStream Live Database</p>
          </div>
          <div style={{ display: 'flex', gap: '16px', flex: 1, justifyContent: 'flex-end' }}>
            <div className="search-wrapper">
              <Search className="input-icon" size={20} />
              <input 
                type="text" 
                placeholder="Search staff..." 
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
          </div>
        </header>

        <div style={{ backgroundColor: '#eff6ff', padding: '12px 20px', borderRadius: '8px', marginBottom: '24px', display: 'flex', alignItems: 'center', gap: '12px', border: '1px solid #bfdbfe', color: '#1e40af', fontSize: '0.9rem' }}>
          <Info size={18} />
          <span><strong>System Sync:</strong> Outstanding balances are pulled directly from the employee ledger.</span>
        </div>

        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Position</th>
                <th>Outstanding Debt</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredEmployees.length > 0 ? filteredEmployees.map((emp) => (
                <tr key={emp.id}>
                  <td>
                    <span style={{ fontWeight: 600 }}>{emp.user?.firstname} {emp.user?.lastname}</span>
                    <span className="employee-id">ID: EMP-{emp.id}</span>
                  </td>
                  <td>{emp.position || 'UNASSIGNED'}</td>
                  <td style={{ fontWeight: 700, color: emp.debt > 0 ? '#ef4444' : '#10b981' }}>
                    ₱{(emp.debt || 0).toLocaleString()}
                    <span style={{ fontSize: '0.7rem', color: '#94a3b8', display: 'block' }}>Current Balance</span>
                  </td>
                  <td>
                    <span style={{ 
                      padding: '4px 12px', borderRadius: '20px', fontSize: '0.85rem', fontWeight: 700,
                      backgroundColor: emp.status === 'ACTIVE' ? '#d1fae5' : '#fee2e2',
                      color: emp.status === 'ACTIVE' ? '#065f46' : '#991b1b',
                      textTransform: 'uppercase'
                    }}>
                      {emp.status}
                    </span>
                  </td>
                  <td>
                    <button className="btn-icon" onClick={() => setEditingEmployee(emp)}>
                      <Edit2 size={18} />
                    </button>
                  </td>
                </tr>
              )) : (
                <tr><td colSpan="5" style={{ textAlign: 'center', padding: '40px', color: '#94a3b8' }}>No employees found.</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </main>

      {editingEmployee && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="label-row" style={{ marginBottom: '24px' }}>
              <h2 style={{ fontSize: '1.25rem', fontWeight: 700 }}>Update Profile & Debt</h2>
              <X style={{ cursor: 'pointer' }} onClick={() => setEditingEmployee(null)} />
            </div>
            <form onSubmit={handleUpdate}>
              <div className="form-group">
                <label>Staff Name</label>
                <input type="text" disabled value={`${editingEmployee.user?.firstname} ${editingEmployee.user?.lastname}`} />
              </div>

              <div className="form-group">
                <label>Outstanding Balance / Debt (Php)</label>
                <div style={{ position: 'relative' }}>
                  <Wallet size={16} style={{ position: 'absolute', left: '12px', top: '14px', color: '#94a3b8' }} />
                  <input 
                    type="number" 
                    style={{ paddingLeft: '36px' }}
                    value={editingEmployee.debt || 0} 
                    onChange={(e) => setEditingEmployee({...editingEmployee, debt: parseFloat(e.target.value) || 0})} 
                  />
                </div>
              </div>

              <div className="grid-2-col">
                <div className="form-group">
                  <label>Position</label>
                  <select value={editingEmployee.position} onChange={(e) => setEditingEmployee({...editingEmployee, position: e.target.value})}>
                    <option value="DRIVER">DRIVER</option>
                    <option value="HELPER">HELPER</option>
                  </select>
                </div>
                <div className="form-group">
                  <label>Account Status</label>
                  <select value={editingEmployee.status} onChange={(e) => setEditingEmployee({...editingEmployee, status: e.target.value})}>
                    <option value="ACTIVE">ACTIVE</option>
                    <option value="INACTIVE">INACTIVE</option>
                  </select>
                </div>
              </div>

              <div className="modal-actions" style={{ display: 'flex', gap: '12px', marginTop: '24px' }}>
                <button type="button" className="btn-secondary" onClick={() => setEditingEmployee(null)}>Cancel</button>
                <button type="submit" className="btn-primary" style={{ flex: 2 }}>Update Profile</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminDashboard;