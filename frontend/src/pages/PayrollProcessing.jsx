import { useState, useEffect } from 'react';
import axios from 'axios';
import Header from '../components/Header';
import { UserPlus, ClipboardList, Calculator, CheckCircle, Loader2, ShieldCheck } from 'lucide-react';

const PayrollProcessing = () => {
  const [employees, setEmployees] = useState([]);
  const [allRates, setAllRates] = useState([]); 
  const [selectedEmp, setSelectedEmp] = useState(null);
  const [loading, setLoading] = useState(true);
  
  // Toggles for Deductions
  const [includeSSS, setIncludeSSS] = useState(true);
  const [includePhilHealth, setIncludePhilHealth] = useState(true);
  const [includePagIBIG, setIncludePagIBIG] = useState(true);

  // Form Inputs
  const [formData, setFormData] = useState({
    workingDays: 0,
    count40ft: 0, 
    count20ft: 0, 
    otHours: 0,
    otContainer: 0, 
    outOfTown: 0, 
    cashAdvance: 0, 
    absences: 0, 
    otherDebts: 0 // New Field
  });

  const [activeRates, setActiveRates] = useState({ 
    baseRate: 0, rate40ft: 0, rate20ft: 0, rateOtHour: 0, rateOtContainer: 0,
    rateSss: 0, ratePhilhealth: 0, ratePagibig: 0
  });

  const getAuthHeader = () => ({
    headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
  });

  useEffect(() => {
    const loadInitialData = async () => {
      try {
        const [empRes, rateRes] = await Promise.all([
          axios.get('http://localhost:8080/api/v1/employees', getAuthHeader()),
          axios.get('http://localhost:8080/api/v1/rates', getAuthHeader())
        ]);
        setEmployees(empRes.data.filter(e => e.status === 'ACTIVE'));
        setAllRates(rateRes.data);
      } catch (err) {
        console.error("Failed to load payroll data", err);
      } finally {
        setLoading(false);
      }
    };
    loadInitialData();
  }, []);

  useEffect(() => {
    if (selectedEmp) {
      const positionRates = allRates.find(r => r.position === selectedEmp.position);
      if (positionRates) {
        setActiveRates({
          baseRate: positionRates.baseRate || 0,
          rate40ft: positionRates.rate40ft || 0,
          rate20ft: positionRates.rate20ft || 0,
          rateOtHour: positionRates.rateOtHour || 0,
          rateOtContainer: positionRates.rateOtContainer || 0,
          rateSss: positionRates.rateSss || 0,
          ratePhilhealth: positionRates.ratePhilhealth || 0,
          ratePagibig: positionRates.ratePagibig || 0
        });
      }
    }
  }, [selectedEmp, allRates]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: parseFloat(value) || 0 }));
  };

  // --- CALCULATIONS ---
  const calcBase = formData.workingDays * activeRates.baseRate; 
  const calc40ft = formData.count40ft * activeRates.rate40ft;
  const calc20ft = formData.count20ft * activeRates.rate20ft;
  const calcOtPay = formData.otHours * activeRates.rateOtHour;
  const calcOtCont = formData.otContainer * activeRates.rateOtContainer;
  
  const grossPay = calcBase + calc40ft + calc20ft + calcOtPay + calcOtCont + formData.outOfTown;
  
  // Deduction Logic (Only apply if toggle is ON)
  const sssDeduction = includeSSS ? activeRates.rateSss : 0;
  const phDeduction = includePhilHealth ? activeRates.ratePhilhealth : 0;
  const piDeduction = includePagIBIG ? activeRates.ratePagibig : 0;
  const absenceDeduction = formData.absences * activeRates.baseRate; 

  const totalDeductions = sssDeduction + phDeduction + piDeduction + 
                          absenceDeduction + formData.cashAdvance + formData.otherDebts;
  
  const totalSalary = grossPay - totalDeductions;

  if (loading) return (
    <div className="dashboard-container">
      <Header /><div style={{ padding: '100px', textAlign: 'center' }}><Loader2 className="animate-spin" size={40} color="#3b82f6" /></div>
    </div>
  );

  return (
    <div className="dashboard-container">
      <Header />
      <main className="dashboard-content">
        <header style={{ marginBottom: '32px' }}>
          <h1 style={{ fontSize: '1.75rem', fontWeight: 700 }}>Payroll Processing</h1>
        </header>

        <div className="payroll-grid">
          <section>
            {/* Step 1 */}
            <div className="step-card">
              <div className="step-header">
                <UserPlus size={20} color="#3b82f6" />
                <h3 style={{ fontSize: '1rem', fontWeight: 700 }}>Step 1: Select Employee</h3>
              </div>
              <div className="form-group">
                <label>Active Employee</label>
                <select onChange={(e) => setSelectedEmp(employees.find(emp => emp.id === parseInt(e.target.value)))}>
                  <option value="">Choose an employee...</option>
                  {employees.map(emp => (
                    <option key={emp.id} value={emp.id}>
                        {emp.user?.firstname} {emp.user?.lastname} ({emp.position})
                    </option>
                  ))}
                </select>
              </div>
            </div>

            {/* Step 2: Adjustment Details */}
            <div className="step-card">
              <div className="step-header">
                <ClipboardList size={20} color="#3b82f6" />
                <h3 style={{ fontSize: '1rem', fontWeight: 700 }}>Step 2: Adjustment Details</h3>
              </div>
              <div className="grid-2-col">
                <div className="form-group"><label>Working Days</label><input type="number" name="workingDays" onChange={handleInputChange} value={formData.workingDays} /></div>
                <div className="form-group"><label>40 Foot Containers</label><input type="number" name="count40ft" onChange={handleInputChange} value={formData.count40ft} /></div>
                <div className="form-group"><label>20 Foot Containers</label><input type="number" name="count20ft" onChange={handleInputChange} value={formData.count20ft} /></div>
                <div className="form-group"><label>Overtime Hours</label><input type="number" name="otHours" onChange={handleInputChange} value={formData.otHours} /></div>
                <div className="form-group"><label>Overtime Containers</label><input type="number" name="otContainer" onChange={handleInputChange} value={formData.otContainer} /></div>
                <div className="form-group"><label>Out of Town</label><input type="number" name="outOfTown" onChange={handleInputChange} value={formData.outOfTown} /></div>
              </div>

              <div style={{ marginTop: '24px', borderTop: '1px solid #e2e8f0', paddingTop: '24px' }}>
                <h4 style={{ fontSize: '0.9rem', color: '#1e293b', fontWeight: 700, marginBottom: '20px' }}>Deductions</h4>
                
                {/* Switches Row */}
                <div className="grid-2-col" style={{ gridTemplateColumns: '1fr 1fr 1fr', marginBottom: '24px' }}>
                   <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                      <label className="switch">
                        <input type="checkbox" checked={includeSSS} onChange={() => setIncludeSSS(!includeSSS)} />
                        <span className="slider"></span>
                      </label>
                      <span style={{ fontSize: '0.85rem', fontWeight: 600 }}>SSS</span>
                   </div>
                   <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                      <label className="switch">
                        <input type="checkbox" checked={includePhilHealth} onChange={() => setIncludePhilHealth(!includePhilHealth)} />
                        <span className="slider"></span>
                      </label>
                      <span style={{ fontSize: '0.85rem', fontWeight: 600 }}>PhilHealth</span>
                   </div>
                   <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                      <label className="switch">
                        <input type="checkbox" checked={includePagIBIG} onChange={() => setIncludePagIBIG(!includePagIBIG)} />
                        <span className="slider"></span>
                      </label>
                      <span style={{ fontSize: '0.85rem', fontWeight: 600 }}>PagIBIG</span>
                   </div>
                </div>

                <div className="grid-2-col">
                   <div className="form-group"><label>Cash Advances</label><input type="number" name="cashAdvance" value={formData.cashAdvance} onChange={handleInputChange} /></div>
                   <div className="form-group"><label>Days of Absence</label><input type="number" name="absences" value={formData.absences} onChange={handleInputChange} /></div>
                   <div className="form-group" style={{ gridColumn: 'span 2' }}><label>Other Debts</label><input type="number" name="otherDebts" value={formData.otherDebts} onChange={handleInputChange} /></div>
                </div>
              </div>
            </div>
          </section>

          {/* Step 3: Calculation Preview */}
          <aside className="preview-card">
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '24px' }}>
              <h3 style={{ fontSize: '1.1rem', fontWeight: 700 }}>Calculation Preview</h3>
              <Calculator size={20} />
            </div>

            <div className="preview-row"><span>Base ({formData.workingDays} days × ₱{activeRates.baseRate})</span><span>₱ {calcBase.toLocaleString()}</span></div>
            <div className="preview-row"><span>40ft (₱{activeRates.rate40ft})</span><span>₱ {calc40ft.toLocaleString()}</span></div>
            <div className="preview-row"><span>20ft (₱{activeRates.rate20ft})</span><span>₱ {calc20ft.toLocaleString()}</span></div>
            <div className="preview-row"><span>Overtime Hours</span><span>₱ {calcOtPay.toLocaleString()}</span></div>
            <div className="preview-row"><span>Overtime Container</span><span>₱ {calcOtCont.toLocaleString()}</span></div>
            <div className="preview-row"><span>Out of Town</span><span>₱ {formData.outOfTown.toLocaleString()}</span></div>

            <div className="preview-row main"><span>Gross Pay</span><span>₱ {grossPay.toLocaleString()}</span></div>

            <div className="preview-row" style={{ marginTop: '20px' }}><span>SSS</span><span>- ₱ {sssDeduction.toLocaleString()}</span></div>
            <div className="preview-row"><span>PhilHealth</span><span>- ₱ {phDeduction.toLocaleString()}</span></div>
            <div className="preview-row"><span>PagIBIG</span><span>- ₱ {piDeduction.toLocaleString()}</span></div>
            <div className="preview-row"><span>Absences</span><span>- ₱ {absenceDeduction.toLocaleString()}</span></div>
            <div className="preview-row"><span>Cash Advances</span><span>- ₱ {formData.cashAdvance.toLocaleString()}</span></div>
            <div className="preview-row"><span>Other Debts</span><span>- ₱ {formData.otherDebts.toLocaleString()}</span></div>

            <div className="total-salary-box">
              <span style={{ fontSize: '0.75rem', color: '#94a3b8', textTransform: 'uppercase' }}>Net Take Home Pay</span>
              <span className="total-amount">₱ {totalSalary.toLocaleString(undefined, { minimumFractionDigits: 2 })}</span>
              <div className="verify-badge"><CheckCircle size={12} /> Live Sync Active</div>
            </div>

            <button className="btn-primary" style={{ backgroundColor: 'var(--success-emerald)', marginTop: '24px', width: '100%' }}>
              GENERATE PAYSLIP
            </button>
          </aside>
        </div>
      </main>
    </div>
  );
};

export default PayrollProcessing;