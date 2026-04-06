import { useState, useEffect } from 'react';
import Header from '../components/Header';
import { UserPlus, ClipboardList, Calculator, CheckCircle, Search } from 'lucide-react';

const PayrollProcessing = () => {
  const [employees, setEmployees] = useState([]);
  const [selectedEmp, setSelectedEmp] = useState(null);
  
  // Form Inputs
  const [formData, setFormData] = useState({
    workingHours: 0, count40ft: 0, count20ft: 0, otHours: 0,
    otContainer: 0, outOfTown: 0, sss: 0, pagibig: 0,
    philhealth: 0, cashAdvance: 0, absences: 0, otherDebts: 0
  });

  const [rates, setRates] = useState({ rate40ft: 500, rate20ft: 300, rateOtHour: 150, rateOtContainer: 100 });

  useEffect(() => {
    // Mock Fetch Active Employees
    setEmployees([
      { id: 1, name: 'Sarah Chen', position: 'DRIVER', baseSalary: 600 },
      { id: 2, name: 'Marcus Rodriguez', position: 'HELPER', baseSalary: 450 }
    ]);
  }, []);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: parseFloat(value) || 0 }));
  };

  // Live Calculations
  const calcBase = selectedEmp ? (formData.workingHours * selectedEmp.baseSalary) : 0;
  const calc40ft = formData.count40ft * rates.rate40ft;
  const calc20ft = formData.count20ft * rates.rate20ft;
  const calcOtPay = formData.otHours * rates.rateOtHour;
  const calcOtCont = formData.otContainer * rates.rateOtContainer;
  
  const grossPay = calcBase + calc40ft + calc20ft + calcOtPay + calcOtCont + formData.outOfTown;
  
  const absenceDeduction = selectedEmp ? (formData.absences * selectedEmp.baseSalary) : 0;
  const totalDeductions = formData.sss + formData.philhealth + formData.pagibig + 
                         absenceDeduction + formData.cashAdvance + formData.otherDebts;
  
  const totalSalary = grossPay - totalDeductions;

  return (
    <div className="dashboard-container">
      <Header />
      <main className="dashboard-content">
        <header style={{ marginBottom: '32px' }}>
          <h1 style={{ fontSize: '1.75rem', fontWeight: 700 }}>Payroll Processing</h1>
          <p style={{ color: '#64748b' }}>Process employee salary, deductions, and adjustments in 3 simple steps.</p>
        </header>

        <div className="payroll-grid">
          <section>
            {/* Step 1: Select Employee */}
            <div className="step-card">
              <div className="step-header">
                <UserPlus size={20} />
                <h3 style={{ fontSize: '1rem', fontWeight: 600 }}>Step 1: Select Employee</h3>
              </div>
              <div className="form-group">
                <label>Employee Name</label>
                <select onChange={(e) => setSelectedEmp(employees.find(emp => emp.id === parseInt(e.target.value)))}>
                  <option value="">Search and select employee...</option>
                  {employees.map(emp => (
                    <option key={emp.id} value={emp.id}>{emp.name} ({emp.position})</option>
                  ))}
                </select>
              </div>
            </div>

            {/* Step 2: Input Details */}
            <div className="step-card">
              <div className="step-header">
                <ClipboardList size={20} />
                <h3 style={{ fontSize: '1rem', fontWeight: 600 }}>Step 2: Adjustment Details</h3>
              </div>
              <div className="grid-2-col">
                <div className="form-group"><label>Working Hours</label><input type="number" name="workingHours" onChange={handleInputChange} placeholder="0.0" /></div>
                <div className="form-group"><label>40 Foot Container</label><input type="number" name="count40ft" onChange={handleInputChange} placeholder="0.00" /></div>
                <div className="form-group"><label>20-foot container</label><input type="number" name="count20ft" onChange={handleInputChange} placeholder="0.0" /></div>
                <div className="form-group"><label>Overtime Hours</label><input type="number" name="otHours" onChange={handleInputChange} placeholder="0.0" /></div>
                <div className="form-group"><label>Overtime Container</label><input type="number" name="otContainer" onChange={handleInputChange} placeholder="0.0" /></div>
                <div className="form-group"><label>Out of Town Trips</label><input type="number" name="outOfTown" onChange={handleInputChange} placeholder="0.0" /></div>
                <div className="form-group"><label>SSS</label><input type="number" name="sss" onChange={handleInputChange} placeholder="₱ 0.00" /></div>
                <div className="form-group"><label>PagIBIG</label><input type="number" name="pagibig" onChange={handleInputChange} placeholder="₱ 0.00" /></div>
                <div className="form-group"><label>PhilHealth</label><input type="number" name="philhealth" onChange={handleInputChange} placeholder="₱ 0.00" /></div>
                <div className="form-group"><label>Cash Advances</label><input type="number" name="cashAdvance" onChange={handleInputChange} placeholder="₱ 0.00" /></div>
                <div className="form-group"><label>Absences</label><input type="number" name="absences" onChange={handleInputChange} placeholder="0" /></div>
                <div className="form-group"><label>Other Debts</label><input type="number" name="otherDebts" onChange={handleInputChange} placeholder="₱ 0.00" /></div>
              </div>
            </div>
          </section>

          {/* Step 3: Calculation Preview */}
          <aside className="preview-card">
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '24px' }}>
              <h3 style={{ fontSize: '1.1rem', fontWeight: 600 }}>Calculation Preview</h3>
              <Calculator size={20} />
            </div>

            <div className="preview-row"><span>Base Salary</span><span>₱ {calcBase.toFixed(2)}</span></div>
            <div className="preview-row"><span>40ftr Container</span><span>₱ {calc40ft.toFixed(2)}</span></div>
            <div className="preview-row"><span>20ftr Container</span><span>₱ {calc20ft.toFixed(2)}</span></div>
            <div className="preview-row"><span>Overtime Pay</span><span>₱ {calcOtPay.toFixed(2)}</span></div>
            <div className="preview-row"><span>Overtime Container</span><span>₱ {calcOtCont.toFixed(2)}</span></div>
            <div className="preview-row"><span>Out of Town Trips</span><span>₱ {formData.outOfTown.toFixed(2)}</span></div>

            <div className="preview-row main"><span>Gross Pay</span><span>₱ {grossPay.toFixed(2)}</span></div>

            <div className="preview-row" style={{ marginTop: '20px' }}><span>SSS</span><span>- ₱ {formData.sss.toFixed(2)}</span></div>
            <div className="preview-row"><span>PhilHealth</span><span>- ₱ {formData.philhealth.toFixed(2)}</span></div>
            <div className="preview-row"><span>PagIBIG</span><span>- ₱ {formData.pagibig.toFixed(2)}</span></div>
            <div className="preview-row"><span>Cash Advance</span><span>- ₱ {formData.cashAdvance.toFixed(2)}</span></div>
            <div className="preview-row"><span>Other Debts</span><span>- ₱ {formData.otherDebts.toFixed(2)}</span></div>

            <div className="total-salary-box">
              <span style={{ fontSize: '0.75rem', color: '#94a3b8', textTransform: 'uppercase' }}>Total Salary</span>
              <span className="total-amount">₱ {totalSalary.toFixed(2)}</span>
              <div className="verify-badge"><CheckCircle size={12} /> Verified</div>
            </div>

            <button className="btn-primary" style={{ backgroundColor: 'var(--success-emerald)', marginTop: '24px', width: '100%' }}>
              PROCESS PAYMENT
            </button>
            <p style={{ fontSize: '0.7rem', color: '#64748b', textAlign: 'center', marginTop: '16px' }}>
              By clicking "Process Payment", you confirm the adjustments are accurate and authorized.
            </p>
          </aside>
        </div>
      </main>
    </div>
  );
};

export default PayrollProcessing;