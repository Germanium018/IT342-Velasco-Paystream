import { useState, useEffect } from 'react';
import axios from 'axios';
import Header from '../components/Header';
import { Save, ShieldCheck } from 'lucide-react';

const Rates = () => {
  const [rates, setRates] = useState([]);
  const [loading, setLoading] = useState(true);

  const getAuthHeader = () => {
    const token = localStorage.getItem('token');
    return { headers: { Authorization: `Bearer ${token}` } };
  };

  useEffect(() => {
    fetchRates();
  }, []);

  const fetchRates = async () => {
    try {
      const res = await axios.get('http://localhost:8080/api/v1/rates', getAuthHeader());
      setRates(res.data);
    } catch (err) {
      console.error("Error fetching rates", err);
    } finally {
      setLoading(false);
    }
  };

  const handleUpdate = async (position, updatedData) => {
    try {
      // Before saving, ensure all empty strings are converted back to 0
      const payload = { ...updatedData };
      Object.keys(payload).forEach(key => {
        if (payload[key] === "") payload[key] = 0;
      });

      await axios.put(
        `http://localhost:8080/api/v1/rates/${position}`, 
        payload, 
        getAuthHeader()
      );
      alert(`${position} Rates Updated Successfully!`);
      fetchRates();
    } catch (err) {
      alert("Failed to update rates. Check your connection.");
    }
  };

  if (loading) return (
    <div className="dashboard-container">
      <Header />
      <div style={{ padding: '40px', textAlign: 'center' }}><h2>Loading rates...</h2></div>
    </div>
  );

  const renderSection = (title, pos) => {
    // Fallback data if DB is empty
    const data = rates.find(r => r.position === pos) || { 
        position: pos, baseRate: 0, rate40ft: 0, rate20ft: 0, 
        rateOtContainer: 0, rateOtHour: 0, rateSss: 0, ratePhilhealth: 0, ratePagibig: 0 
    };

    const handleChange = (field, val) => {
        // ALLOW EMPTY STRING so user can backspace and type freely
        const updated = { ...data, [field]: val === "" ? "" : parseFloat(val) };
        setRates(rates.map(r => r.position === pos ? updated : r));
    };

    return (
      <div className="step-card" style={{ marginBottom: '32px' }}>
        <div className="step-header">
          <h3 style={{ fontSize: '1.25rem', color: '#3b82f6' }}>{title} Configuration</h3>
        </div>
        
        <div className="grid-2-col">
          <div className="form-group">
            <label>Base Salary Rate</label>
            <input type="number" value={data.baseRate} onChange={(e) => handleChange('baseRate', e.target.value)} />
          </div>
          <div className="form-group">
            <label>40ftr Container Rate</label>
            <input type="number" value={data.rate40ft} onChange={(e) => handleChange('rate40ft', e.target.value)} />
          </div>
          <div className="form-group">
            <label>20ftr Container Rate</label>
            <input type="number" value={data.rate20ft} onChange={(e) => handleChange('rate20ft', e.target.value)} />
          </div>
          <div className="form-group">
            <label>Overtime Pay (Hour)</label>
            <input type="number" value={data.rateOtHour} onChange={(e) => handleChange('rateOtHour', e.target.value)} />
          </div>
          <div className="form-group">
            <label>Overtime Container Rate</label>
            <input type="number" value={data.rateOtContainer} onChange={(e) => handleChange('rateOtContainer', e.target.value)} />
          </div>
        </div>
        
        <div style={{ margin: '24px 0', borderTop: '1px solid #e2e8f0', paddingTop: '24px' }}>
          <h4 style={{ fontSize: '0.9rem', color: '#64748b', marginBottom: '16px', display: 'flex', alignItems: 'center', gap: '8px' }}>
            <ShieldCheck size={16} /> Statutory Deductions (Standard)
          </h4>
          <div className="grid-2-col" style={{ gridTemplateColumns: '1fr 1fr 1fr' }}>
            <div className="form-group">
              <label>SSS</label>
              <input type="number" value={data.rateSss} onChange={(e) => handleChange('rateSss', e.target.value)} />
            </div>
            <div className="form-group">
              <label>PhilHealth</label>
              <input type="number" value={data.ratePhilhealth} onChange={(e) => handleChange('ratePhilhealth', e.target.value)} />
            </div>
            <div className="form-group">
              <label>Pag-IBIG</label>
              <input type="number" value={data.ratePagibig} onChange={(e) => handleChange('ratePagibig', e.target.value)} />
            </div>
          </div>
        </div>

        <button className="btn-primary" style={{ marginTop: '16px', width: 'auto', padding: '0 32px' }} onClick={() => handleUpdate(pos, data)}>
          <Save size={18} /> Save {title} Rates
        </button>
      </div>
    );
  };

  return (
    <div className="dashboard-container">
      <Header />
      <main className="dashboard-content">
        <header style={{ marginBottom: '40px' }}>
          <h1 style={{ fontSize: '2rem', fontWeight: 700 }}>Rate Management</h1>
          <p style={{ color: '#64748b' }}>Configure position-based salary multipliers and standard statutory deductions.</p>
        </header>

        {renderSection('Driver', 'DRIVER')}
        {renderSection('Helper', 'HELPER')}
      </main>
    </div>
  );
};

export default Rates;