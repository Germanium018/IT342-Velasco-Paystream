import { useState, useEffect } from 'react';
import Header from '../components/Header';
import { Search, Edit2, Trash2, X } from 'lucide-react';

const AdminDashboard = () => {
  const [employees, setEmployees] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [editingEmployee, setEditingEmployee] = useState(null);

  // Fetch Employees from Backend
  useEffect(() => {
    // In actual implementation: axios.get('/api/v1/employees', { headers: { Authorization: `Bearer ${token}` } })
    const mockData = [
      { id: 1, firstname: 'Sarah', lastname: 'Chen', position: 'Driver', baseSalary: 142500, status: 'Active' },
      { id: 2, firstname: 'Marcus', lastname: 'Rodriguez', position: 'Helper', baseSalary: 158000, status: 'Active' },
    ];
    setEmployees(mockData);
  }, []);

  const filteredEmployees = employees.filter(emp => 
    `${emp.firstname} ${emp.lastname}`.toLowerCase().includes(searchTerm.toLowerCase()) ||
    emp.id.toString().includes(searchTerm)
  );

  return (
    <div className="dashboard-container">
      <Header />
      
      <main className="dashboard-content">
        <header className="action-header">
          <div>
            <h1 style={{ fontSize: '2rem', color: '#0f172a' }}>Employee Directory</h1>
            <p style={{ color: '#64748b' }}>Manage your workforce and payroll details in one place.</p>
          </div>

          <div className="search-wrapper">
            <Search className="input-icon" size={20} />
            <input 
              type="text" 
              placeholder="Search by Name or ID" 
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
        </header>

        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Position</th>
                <th>Base Salary</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredEmployees.map((emp) => (
                <tr key={emp.id}>
                  <td>
                    <span style={{ fontWeight: 600 }}>{emp.firstname} {emp.lastname}</span>
                    <span className="employee-id">ID: {emp.id}</span>
                  </td>
                  <td>{emp.position}</td>
                  <td style={{ fontWeight: 600 }}>₱{emp.baseSalary.toLocaleString()}</td>
                  <td>
                    <span style={{ 
                      padding: '4px 12px', 
                      borderRadius: '20px', 
                      fontSize: '0.85rem',
                      backgroundColor: emp.status === 'Active' ? '#d1fae5' : '#fee2e2',
                      color: emp.status === 'Active' ? '#065f46' : '#991b1b'
                    }}>
                      {emp.status}
                    </span>
                  </td>
                  <td>
                    <button className="btn-icon" onClick={() => setEditingEmployee(emp)}>
                      <Edit2 size={18} color="#64748b" />
                    </button>
                    <button className="btn-icon">
                      <Trash2 size={18} color="#ef4444" />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </main>

      {/* Edit Modal */}
      {editingEmployee && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="label-row" style={{ marginBottom: '24px' }}>
              <h2 style={{ fontSize: '1.25rem' }}>Edit Employee</h2>
              <X style={{ cursor: 'pointer' }} onClick={() => setEditingEmployee(null)} />
            </div>
            
            <form onSubmit={(e) => { e.preventDefault(); setEditingEmployee(null); }}>
              <div className="form-group">
                <label>First Name</label>
                <input type="text" defaultValue={editingEmployee.firstname} />
              </div>
              <div className="form-group">
                <label>Position</label>
                <select defaultValue={editingEmployee.position}>
                  <option value="Driver">Driver</option>
                  <option value="Helper">Helper</option>
                </select>
              </div>
              <div className="form-group">
                <label>Base Salary</label>
                <input type="number" defaultValue={editingEmployee.baseSalary} />
              </div>

              <div className="modal-actions">
                <button type="button" className="btn-primary" style={{ backgroundColor: '#f1f5f9', color: '#1e293b' }} onClick={() => setEditingEmployee(null)}>Cancel</button>
                <button type="submit" className="btn-primary">Save Changes</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminDashboard;