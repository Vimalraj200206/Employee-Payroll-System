package com.college.payroll;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.print.PrinterException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PayrollApp extends JFrame {
    private final EmployeeDAO dao = new EmployeeDAO();
    private final JTextField idField = new JTextField(7);
    private final JTextField nameField = new JTextField(18);
    private final JTextField departmentField = new JTextField(18);
    private final JTextField designationField = new JTextField(18);
    private final JTextField basicField = new JTextField(12);
    private final JTextField allowancesField = new JTextField("0", 12);
    private final JTextField deductionsField = new JTextField("0", 12);
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Name", "Department", "Designation", "Basic (₹)", "Allowances (₹)", "Deductions (₹)", "Net Salary (₹)"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JTextArea payslipArea = new JTextArea(12, 35);
    private final NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    public PayrollApp() {
        super("Employee Payroll System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel heading = new JLabel("EMPLOYEE PAYROLL SYSTEM", SwingConstants.CENTER);
        heading.setFont(new Font("SansSerif", Font.BOLD, 23));
        heading.setBorder(BorderFactory.createEmptyBorder(12, 8, 8, 8));
        add(heading, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Employee Details"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.anchor = GridBagConstraints.WEST;
        addField(form, c, 0, 0, "Employee ID (for update/delete):", idField);
        addField(form, c, 0, 1, "Full name:", nameField);
        addField(form, c, 0, 2, "Department:", departmentField);
        addField(form, c, 0, 3, "Designation:", designationField);
        addField(form, c, 1, 0, "Basic salary:", basicField);
        addField(form, c, 1, 1, "Allowances:", allowancesField);
        addField(form, c, 1, 2, "Deductions:", deductionsField);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Employee");
        JButton updateButton = new JButton("Update Selected ID");
        JButton deleteButton = new JButton("Delete ID");
        JButton clearButton = new JButton("Clear Form");
        JButton refreshButton = new JButton("Refresh List");
        JButton payslipButton = new JButton("Generate Payslip");
        JButton printButton = new JButton("Print Payslip");
        buttons.add(addButton); buttons.add(updateButton); buttons.add(deleteButton);
        buttons.add(clearButton); buttons.add(refreshButton); buttons.add(payslipButton); buttons.add(printButton);

        JPanel left = new JPanel(new BorderLayout(5, 5));
        left.add(form, BorderLayout.NORTH);
        left.add(buttons, BorderLayout.CENTER);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) fillFormFromSelection();
        });

        payslipArea.setEditable(false);
        payslipArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JPanel payslipPanel = new JPanel(new BorderLayout());
        payslipPanel.setBorder(BorderFactory.createTitledBorder("Payslip Preview"));
        payslipPanel.add(new JScrollPane(payslipArea), BorderLayout.CENTER);

        JSplitPane bottom = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(table), payslipPanel);
        bottom.setResizeWeight(0.58);
        add(left, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        bottom.setPreferredSize(new Dimension(1000, 400));

        addButton.addActionListener(e -> addEmployee());
        updateButton.addActionListener(e -> updateEmployee());
        deleteButton.addActionListener(e -> deleteEmployee());
        clearButton.addActionListener(e -> clearForm());
        refreshButton.addActionListener(e -> refreshTable());
        payslipButton.addActionListener(e -> generatePayslip());
        printButton.addActionListener(e -> printPayslip());

        refreshTable();
    }

    private void addField(JPanel panel, GridBagConstraints c, int column, int row, String label, JTextField field) {
        c.gridx = column * 2; c.gridy = row;
        panel.add(new JLabel(label), c);
        c.gridx = column * 2 + 1;
        panel.add(field, c);
    }

    private void addEmployee() {
        try {
            FormData d = readForm();
            dao.add(d.name, d.department, d.designation, d.basic, d.allowances, d.deductions);
            showInfo("Employee added successfully.");
            refreshTable(); clearForm();
        } catch (Exception ex) { showError(ex); }
    }

    private void updateEmployee() {
        try {
            int id = readId();
            FormData d = readForm();
            dao.update(id, d.name, d.department, d.designation, d.basic, d.allowances, d.deductions);
            showInfo("Employee updated successfully.");
            refreshTable(); clearForm();
        } catch (Exception ex) { showError(ex); }
    }

    private void deleteEmployee() {
        try {
            int id = readId();
            int answer = JOptionPane.showConfirmDialog(this, "Delete employee ID " + id + "?", "Confirm delete", JOptionPane.YES_NO_OPTION);
            if (answer != JOptionPane.YES_OPTION) return;
            dao.delete(id);
            showInfo("Employee deleted.");
            refreshTable(); clearForm(); payslipArea.setText("");
        } catch (Exception ex) { showError(ex); }
    }

    private FormData readForm() {
        String name = nameField.getText().trim();
        String department = departmentField.getText().trim();
        String designation = designationField.getText().trim();
        if (name.isEmpty() || department.isEmpty() || designation.isEmpty())
            throw new IllegalArgumentException("Name, department, and designation are required.");
        BigDecimal basic = amount(basicField, "Basic salary");
        BigDecimal allowances = amount(allowancesField, "Allowances");
        BigDecimal deductions = amount(deductionsField, "Deductions");
        if (basic.signum() < 0 || allowances.signum() < 0 || deductions.signum() < 0)
            throw new IllegalArgumentException("Salary values cannot be negative.");
        if (deductions.compareTo(basic.add(allowances)) > 0)
            throw new IllegalArgumentException("Deductions cannot exceed basic salary plus allowances.");
        return new FormData(name, department, designation, basic, allowances, deductions);
    }

    private BigDecimal amount(JTextField field, String label) {
        try { return new BigDecimal(field.getText().trim()); }
        catch (NumberFormatException ex) { throw new IllegalArgumentException(label + " must be a valid number."); }
    }

    private int readId() {
        try { return Integer.parseInt(idField.getText().trim()); }
        catch (NumberFormatException ex) { throw new IllegalArgumentException("Enter a valid employee ID, or select a table row."); }
    }

    private void refreshTable() {
        try {
            model.setRowCount(0);
            List<Employee> employees = dao.getAll();
            for (Employee e : employees) {
                model.addRow(new Object[]{e.getId(), e.getName(), e.getDepartment(), e.getDesignation(),
                        e.getBasicSalary(), e.getAllowances(), e.getDeductions(), e.getNetSalary()});
            }
        } catch (Exception ex) { showError(ex); }
    }

    private void fillFormFromSelection() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return;
        int row = table.convertRowIndexToModel(viewRow);
        idField.setText(String.valueOf(model.getValueAt(row, 0)));
        nameField.setText(String.valueOf(model.getValueAt(row, 1)));
        departmentField.setText(String.valueOf(model.getValueAt(row, 2)));
        designationField.setText(String.valueOf(model.getValueAt(row, 3)));
        basicField.setText(String.valueOf(model.getValueAt(row, 4)));
        allowancesField.setText(String.valueOf(model.getValueAt(row, 5)));
        deductionsField.setText(String.valueOf(model.getValueAt(row, 6)));
    }

    private void generatePayslip() {
        try {
            int id = readId();
            Employee employee = dao.getAll().stream().filter(e -> e.getId() == id).findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Employee ID not found. Refresh the list and try again."));
            String slip = """
                    ========================================
                         EMPLOYEE PAYSLIP
                    ========================================
                    Employee ID : %d
                    Name        : %s
                    Department  : %s
                    Designation : %s
                    ----------------------------------------
                    Basic Salary: %s
                    Allowances  : %s
                    Deductions  : %s
                    ----------------------------------------
                    NET SALARY  : %s
                    ========================================
                    """.formatted(employee.getId(), employee.getName(), employee.getDepartment(),
                    employee.getDesignation(), currency.format(employee.getBasicSalary()),
                    currency.format(employee.getAllowances()), currency.format(employee.getDeductions()),
                    currency.format(employee.getNetSalary()));
            payslipArea.setText(slip);
        } catch (Exception ex) { showError(ex); }
    }

    private void printPayslip() {
        if (payslipArea.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Generate a payslip first.");
            return;
        }
        try { payslipArea.print(); }
        catch (PrinterException ex) { showError(ex); }
    }

    private void clearForm() {
        idField.setText(""); nameField.setText(""); departmentField.setText("");
        designationField.setText(""); basicField.setText("");
        allowancesField.setText("0"); deductionsField.setText("0");
        table.clearSelection();
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Employee Payroll System", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    private record FormData(String name, String department, String designation,
                            BigDecimal basic, BigDecimal allowances, BigDecimal deductions) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PayrollApp().setVisible(true));
    }
}
