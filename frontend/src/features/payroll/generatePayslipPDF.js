import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

// 🟢 REMOVED: allRates is no longer needed! We trust the backend's locked math.
export const generatePayslipPDF = (t, mode = 'download') => {
  try {
    const doc = new jsPDF();
    const pageWidth = doc.internal.pageSize.getWidth();
    
    // Helper to ensure we are working with clean numbers
    const num = (val) => Number(val) || 0;

    // 1. BRANDING & HEADER
    doc.setFont("helvetica", "bold");
    doc.setFontSize(22);
    doc.setTextColor(30, 41, 59);
    doc.text("PayStream", 14, 20);

    doc.setFontSize(10);
    doc.setFont("helvetica", "normal");
    doc.setTextColor(100, 116, 139);
    doc.text("Official Employee Payslip", 14, 28);

    // 2. EMPLOYEE NAME & POSITION
    doc.setFontSize(14);
    doc.setFont("helvetica", "bold");
    doc.setTextColor(15, 23, 42);
    
    // 🟢 THE FIX: Extract historical position snapshot with fallback logic
    const displayPosition = t.positionAtTime || t.employee?.position || '';
    doc.text(`${t.employee?.user?.firstname || 'Unknown'} ${t.employee?.user?.lastname || ''} - ${displayPosition.toUpperCase()}`, 14, 42);

    // 3. EARNINGS (Using permanently locked historical data)
    const earnings = [];
    
    const addLockedEarning = (label, count, rate, totalAmount) => {
      if (num(count) > 0) {
        earnings.push([`${label} (${num(count)} x Php ${num(rate).toLocaleString()})`, `Php ${num(totalAmount).toLocaleString()}`]);
      }
    };

    addLockedEarning("Base", t.workingDays, t.rateBase, t.payBase);
    addLockedEarning("40ft Container", t.count40ft, t.rate40ft, t.pay40ft);
    addLockedEarning("20ft Container", t.count20ft, t.rate20ft, t.pay20ft);
    addLockedEarning("Overtime Hours", t.overtimeHours, t.rateOtHour, t.payOtHour);
    addLockedEarning("Overtime Container", t.otContainerCount, t.rateOtContainer, t.payOtContainer);
    
    if (num(t.outOfTownTrips) > 0) {
      earnings.push(["Out of Town", `Php ${num(t.outOfTownTrips).toLocaleString()}`]);
    }

    autoTable(doc, {
      startY: 50,
      body: earnings,
      theme: 'plain',
      styles: { fontSize: 10, cellPadding: 2 },
      columnStyles: { 1: { halign: 'right' } }
    });

    // 4. DEDUCTIONS
    let currentY = doc.lastAutoTable.finalY + 12;
    const deductions = [];
    
    if (num(t.absences) > 0) {
      const absRate = num(t.rateBase);
      const absTotal = num(t.absenceDeductionAmount);
      deductions.push([`Absences (${num(t.absences)} x -Php ${absRate.toLocaleString()})`, `- Php ${absTotal.toLocaleString()}`]);
    }
    if (num(t.cashAdvance) > 0) deductions.push(["Cash Advance", `- Php ${num(t.cashAdvance).toLocaleString()}`]);
    if (num(t.otherDebts) > 0) deductions.push(["Other Debts", `- Php ${num(t.otherDebts).toLocaleString()}`]);
    if (num(t.sssDeduction) > 0) deductions.push(["SSS", `- Php ${num(t.sssDeduction).toLocaleString()}`]);
    if (num(t.philhealthDeduction) > 0) deductions.push(["PhilHealth", `- Php ${num(t.philhealthDeduction).toLocaleString()}`]);
    if (num(t.pagibigDeduction) > 0) deductions.push(["PagIBIG", `- Php ${num(t.pagibigDeduction).toLocaleString()}`]);

    autoTable(doc, {
      startY: currentY,
      body: deductions,
      theme: 'plain',
      styles: { fontSize: 10, cellPadding: 2, textColor: [100, 116, 139] },
      columnStyles: { 1: { halign: 'right' } }
    });

    // 5. TOTAL AMOUNT
    currentY = doc.lastAutoTable.finalY + 18;
    doc.setDrawColor(226, 232, 240);
    doc.line(14, currentY - 8, pageWidth - 14, currentY - 8);

    doc.setFontSize(13); 
    doc.setFont("helvetica", "bold");
    doc.setTextColor(30, 41, 59);
    doc.text("TOTAL AMOUNT", 14, currentY);
    doc.text(`Php ${num(t.netPay).toLocaleString(undefined, { minimumFractionDigits: 2 })}`, pageWidth - 14, currentY, { align: 'right' });

    // 6. SIGNATURE SECTION
    const sigY = currentY + 35;
    doc.setFontSize(10);
    doc.setFont("helvetica", "normal");
    doc.setTextColor(100, 116, 139);

    doc.text("Prepared by:", 14, sigY);
    doc.text("Received by:", pageWidth - 70, sigY);

    doc.setFont("helvetica", "bold");
    doc.setTextColor(15, 23, 42);
    doc.text("Admin", 14, sigY + 15);
    doc.text(`${t.employee?.user?.firstname || ''} ${t.employee?.user?.lastname || ''}`, pageWidth - 70, sigY + 15);

    doc.setDrawColor(148, 163, 184);
    doc.line(14, sigY + 17, 70, sigY + 17);
    doc.line(pageWidth - 70, sigY + 17, pageWidth - 14, sigY + 17);

    // MODE LOGIC
    if (mode === 'view') {
      return doc.output('bloburl');
    } else {
      doc.save(`Payslip_${t.employee?.user?.lastname || 'Unknown'}_${t.monthYear}.pdf`);
      return null;
    }
  } catch (err) {
    console.error("PDF Error:", err);
    alert("Check console for error details.");
    return null;
  }
};
