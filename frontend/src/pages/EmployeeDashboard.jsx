import { useState, useEffect } from 'react';
import axios from 'axios';
import HeaderEmployee from '../components/HeaderEmployee';
import { Calendar, Eye, Download, Loader2, Wallet, X } from 'lucide-react';
import { generatePayslipPDF } from '../utils/generatePayslipPDF';

const EmployeeDashboard = () => {
  const [profile, setProfile] = useState(null);
  const [history, setHistory] = useState([]);
  const [rates, setRates] = useState([]);
  const [loading, setLoading] = useState(true);
  const [viewingPdf, setViewingPdf] = useState(null); // Stores the PDF URL for the modal

  const user = JSON.parse(localStorage.getItem('user'));
  const getAuthHeader = () => ({ headers: { Authorization: `Bearer ${localStorage.getItem('token')}` } });

  useEffect(() => {
    const fetchPersonalData = async () => {
      try {
        // 1. Get Employee Profile (for debt & ID)
        const profileRes = await axios.get(`http://localhost:8080/api/v1/employees/me/${user.id}`, getAuthHeader());
        const empData = profileRes.data;
        setProfile(empData);

        // 2. Get Rates & Personal Payroll History in parallel
        const [historyRes, ratesRes] = await Promise.all([
          axios.get(`http://localhost:8080/api/v1/payroll/history/${empData.id}`, getAuthHeader()),
          axios.get('http://localhost:8080/api/v1/rates', getAuthHeader())
        ]);
        
        setHistory(historyRes.data);
        setRates(ratesRes.data);
      } catch (err) {
        console.error("Dashboard Load Error:", err);
      } finally {
        setLoading(false);
      }
    };
    fetchPersonalData();
  }, []);

  const handleAction = (transaction, mode) => {
    const pdfUrl = generatePayslipPDF(transaction, rates, mode);
    if (mode === 'view') setViewingPdf(pdfUrl);
  };

  if (loading) return <div className="dashboard-container"><div className="loader-center"><Loader2 className="animate-spin" size={40} color="#3b82f6" /></div></div>;

  return (
    <div className="dashboard-container">
      <HeaderEmployee firstName={user.firstname} />
      
      <main className="dashboard-content">
        <header className="action-header" style={{ marginBottom: '40px' }}>
          <div>
            <h1 style={{ fontSize: '2rem', fontWeight: 700 }}>Welcome back, {user.firstname}!</h1>
            <p style={{ color: '#64748b' }}>Manage your payslips and monitor your balance</p>
          </div>
          
          <div style={{ backgroundColor: 'white', padding: '16px 24px', borderRadius: '12px', border: '1px solid #e2e8f0', display: 'flex', alignItems: 'center', gap: '16px' }}>
            <div style={{ backgroundColor: '#f0fdf4', padding: '10px', borderRadius: '8px' }}>
              <Wallet color="#10b981" size={24} />
            </div>
            <div>
              <p style={{ fontSize: '0.75rem', color: '#64748b', textTransform: 'uppercase', fontWeight: 700 }}>Outstanding Balance</p>
              <h2 style={{ fontSize: '1.25rem', fontWeight: 700, color: '#0f172a' }}>Php {profile?.debt?.toLocaleString()}</h2>
            </div>
          </div>
        </header>

        <div className="table-container">
          <h3 style={{ padding: '20px', fontSize: '1rem', fontWeight: 700, borderBottom: '1px solid #f1f5f9' }}>Payroll History</h3>
          <table className="data-table">
            <thead>
              <tr>
                <th>Date</th>
                <th>Period</th>
                <th>Net Pay</th>
                <th>Outstanding Balance</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {history.length > 0 ? history.map((t) => (
                <tr key={t.id}>
                  <td>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}><Calendar size={16} color="#94a3b8" /> {new Date(t.processedAt).toLocaleDateString()}</div>
                  </td>
                  <td><span style={{ fontWeight: 600 }}>{t.monthYear}</span></td>
                  <td style={{ fontWeight: 700 }}>Php {t.netPay?.toLocaleString()}</td>
                  <td>Php {profile?.debt?.toLocaleString()}</td>
                  <td>
                    <div style={{ display: 'flex', gap: '12px' }}>
                      <button onClick={() => handleAction(t, 'view')} className="btn-icon" title="View PDF" style={{ color: '#3b82f6' }}><Eye size={18} /></button>
                      <button onClick={() => handleAction(t, 'download')} className="btn-icon" title="Download PDF" style={{ color: '#10b981' }}><Download size={18} /></button>
                    </div>
                  </td>
                </tr>
              )) : (
                <tr><td colSpan="5" style={{ textAlign: 'center', padding: '40px', color: '#94a3b8' }}>No payroll records found yet.</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </main>

      {/* PDF VIEWER MODAL */}
      {viewingPdf && (
        <div style={{ position: 'fixed', top: 0, left: 0, width: '100%', height: '100%', backgroundColor: 'rgba(15, 23, 42, 0.8)', zIndex: 1000, display: 'flex', flexDirection: 'column', alignItems: 'center', padding: '40px' }}>
          <div style={{ width: '100%', maxWidth: '900px', display: 'flex', justifyContent: 'flex-end', marginBottom: '10px' }}>
            <button onClick={() => setViewingPdf(null)} style={{ background: '#ef4444', border: 'none', color: 'white', padding: '8px 16px', borderRadius: '6px', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '8px' }}>
              <X size={18} /> Close Preview
            </button>
          </div>
          <iframe src={viewingPdf} style={{ width: '100%', maxWidth: '900px', height: '100%', borderRadius: '8px', border: 'none', backgroundColor: 'white' }} title="Payslip Preview" />
        </div>
      )}
    </div>
  );
};

export default EmployeeDashboard;