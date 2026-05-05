import { useState, useEffect } from 'react';
import axios from 'axios';
import Header from '../components/Header';
import { Search, Download, Loader2, Calendar, FileText } from 'lucide-react';
import { generatePayslipPDF } from '../utils/generatePayslipPDF';

const Payslips = () => {
  const [transactions, setTransactions] = useState([]);
  const [rates, setRates] = useState([]); // Needed for PDF multiplier math
  const [searchTerm, setSearchTerm] = useState('');
  const [loading, setLoading] = useState(true);

  const getAuthHeader = () => ({
    headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
  });

  const loadData = async () => {
    try {
      const [transRes, ratesRes] = await Promise.all([
        axios.get('http://localhost:8080/api/v1/payroll/all', getAuthHeader()),
        axios.get('http://localhost:8080/api/v1/rates', getAuthHeader())
      ]);
      setTransactions(Array.isArray(transRes.data) ? transRes.data : []);
      setRates(Array.isArray(ratesRes.data) ? ratesRes.data : []);
    } catch (error) {
      console.error("Initialization failed:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const filteredTransactions = transactions.filter(t => {
    const fullName = `${t.employee?.user?.firstname || ''} ${t.employee?.user?.lastname || ''}`.toLowerCase();
    const dateLabel = (t.monthYear || '').toLowerCase();
    return fullName.includes(searchTerm.toLowerCase()) || dateLabel.includes(searchTerm.toLowerCase());
  });

  const handleDownloadPDF = (transaction) => {
    generatePayslipPDF(transaction, rates);
  };

  if (loading) return (
    <div className="dashboard-container">
      <Header />
      <div className="loader-center" style={{ display: 'flex', justifyContent: 'center', padding: '100px' }}>
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
            <h1 style={{ fontSize: '2rem', color: '#0f172a', fontWeight: 700 }}>Generated Payslips</h1>
            <p style={{ color: '#64748b' }}>Search and download employee records</p>
          </div>
          <div className="search-wrapper">
            <Search className="input-icon" size={20} />
            <input 
              type="text" 
              placeholder="Filter by name or month..." 
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
        </header>

        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>Date Generated</th>
                <th>Employee</th>
                <th>Period</th>
                <th>Net Pay</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredTransactions.map((t) => (
                <tr key={t.id}>
                  <td>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                      <Calendar size={16} color="#94a3b8" />
                      {new Date(t.processedAt).toLocaleDateString()}
                    </div>
                  </td>
                  <td>
                    <span style={{ fontWeight: 600 }}>
                      {t.employee?.user?.firstname} {t.employee?.user?.lastname}
                    </span>
                  </td>
                  <td>{t.monthYear}</td>
                  <td style={{ fontWeight: 700 }}>
                    ₱{Number(t.netPay || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}
                  </td>
                  <td>
                    <span className="status-pill active">{t.transactionStatus}</span>
                  </td>
                  <td>
                    <button 
                      className="btn-icon" 
                      onClick={() => handleDownloadPDF(t)}
                      style={{ color: '#3b82f6' }}
                    >
                      <Download size={18} />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </main>
    </div>
  );
};

export default Payslips;