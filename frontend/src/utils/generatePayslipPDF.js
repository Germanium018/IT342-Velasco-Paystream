import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

export const generatePayslipPDF = (t, allRates, mode = 'download') => {
  try {
    const doc = new jsPDF();
    const pageWidth = doc.internal.pageSize.getWidth();
    
    // Safety check for rates (Daily Rates from pay_rates table)
    const rates = allRates.find(r => r.position === t.employee?.position) || {};

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
    doc.text(`${t.employee?.user?.firstname} ${t.employee?.user?.lastname} - ${t.employee?.position}`, 14, 42);

    // 3. EARNINGS (Hide if 0)
    const earnings = [];
    
    // Using rates.baseRate (Daily Rate) instead of baseSalary (Monthly Salary)
    const dailyRate = num(rates.baseRate); 

    const addEarning = (label, count, rate) => {
      const total = num(count) * num(rate);
      if (total > 0) {
        // Using "Php" to avoid encoding errors
        earnings.push([`${label} (${num(count)} x Php ${num(rate).toLocaleString()})`, `Php ${total.toLocaleString()}`]);
      }
    };

    addEarning("Base", t.workingDays, dailyRate);
    addEarning("40ft Container", t.count40ft, rates.rate40ft);
    addEarning("20ft Container", t.count20ft, rates.rate20ft);
    addEarning("Overtime Hours", t.overtimeHours, rates.rateOtHour);
    addEarning("Overtime Container", t.otContainerCount, rates.rateOtContainer);
    
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

    // 4. DEDUCTIONS (Hide if 0)
    let currentY = doc.lastAutoTable.finalY + 12;
    const deductions = [];
    if (num(t.sssDeduction) > 0) deductions.push(["SSS", `- Php ${num(t.sssDeduction).toLocaleString()}`]);
    if (num(t.philhealthDeduction) > 0) deductions.push(["PhilHealth", `- Php ${num(t.philhealthDeduction).toLocaleString()}`]);
    if (num(t.pagibigDeduction) > 0) deductions.push(["PagIBIG", `- Php ${num(t.pagibigDeduction).toLocaleString()}`]);
    if (num(t.absences) > 0) {
      deductions.push([`Absences (${num(t.absences)} x -Php ${dailyRate.toLocaleString()})`, `- Php ${(num(t.absences) * dailyRate).toLocaleString()}`]);
    }
    if (num(t.cashAdvance) > 0) deductions.push(["Cash Advance", `- Php ${num(t.cashAdvance).toLocaleString()}`]);
    if (num(t.otherDebts) > 0) deductions.push(["Other Debts", `- Php ${num(t.otherDebts).toLocaleString()}`]);

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
    doc.text(`${t.employee?.user?.firstname} ${t.employee?.user?.lastname}`, pageWidth - 70, sigY + 15);

    doc.setDrawColor(148, 163, 184);
    doc.line(14, sigY + 17, 70, sigY + 17);
    doc.line(pageWidth - 70, sigY + 17, pageWidth - 14, sigY + 17);

    // MODE LOGIC
    if (mode === 'view') {
      return doc.output('bloburl');
    } else {
      doc.save(`Payslip_${t.employee?.user?.lastname}_${t.monthYear}.pdf`);
      return null;
    }
  } catch (err) {
    console.error("PDF Error:", err);
    alert("Check console for error details.");
    return null;
  }
};