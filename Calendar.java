/*Calendar GUI class with notes
* For Program B
* Professor Roychowdhury
* Written By: Brian Pugnali and Arthur Patterson
* */
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
public class Calendar {
    static private JLabel lblMonth;
    static private JButton btnPrev, btnNext, btnOK, btnCancel;
    static private JTable tblCalendar;
    static private DefaultTableModel mtblCalendar;
    static private JTextArea notePad;
    static private String[] months =  {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    static private int realYear, realMonth, realDay, currentYear, currentMonth, selectedDay = -1;
    static public String[][] notes = new String[12][32];

    public static void main (String args[]){
        // Look and feel
        try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}
        catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException ignored) {}

        //Prepare frame
        JFrame frmMain = new JFrame("Note Calendar");
        frmMain.setSize(650, 375);
        Container pane = frmMain.getContentPane();
        pane.setLayout(null);
        frmMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create controls
        lblMonth = new JLabel ("January");
        btnPrev = new JButton ("Prev");
        btnNext = new JButton ("Fwd");
        btnOK = new JButton("Save Note");
        btnCancel = new JButton("Undo");
        mtblCalendar = new DefaultTableModel(){public boolean isCellEditable(int rowIndex, int mColIndex){return false;}};
        tblCalendar = new JTable(mtblCalendar);
        JScrollPane stblCalendar = new JScrollPane(tblCalendar);
        JPanel pnlCalendar = new JPanel(null);

        String selectedText = "String";
        notePad = new JTextArea(selectedText);


        //Set border
        pnlCalendar.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "2017 Calendar"));
        notePad.setBorder(BorderFactory.createTitledBorder("Note"));

        //Assign listeners to buttons
        btnPrev.addActionListener(new btnPrevAction());
        btnNext.addActionListener(new btnNextAction());
        btnOK.addActionListener(new btnOKAction());
        btnCancel.addActionListener(new btnCancelAction());

        tblCalendar.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                JTable target = (JTable)e.getSource();
                // Not allow selection of empty days, do nothing if same day selected
                if (target.getValueAt(target.getSelectedRow(), target.getSelectedColumn()) != null) {
                    String day = target.getValueAt(target.getSelectedRow(), target.getSelectedColumn()).toString();
                    setNotePad(Integer.parseInt(day));
                } else {
                    setNotePad(0);
                }
            }
        });

        //Add buttons
        pane.add(notePad);
        pane.add(pnlCalendar);
        pane.add(btnOK);
        pane.add(btnCancel);
        pnlCalendar.add(lblMonth);
        pnlCalendar.add(btnPrev);
        pnlCalendar.add(btnNext);
        pnlCalendar.add(stblCalendar);

        //Set sizes
        pnlCalendar.setBounds(0, 0, 320, 335);
        notePad.setBounds(325, 0, 320, 300);
        notePad.setLineWrap(true);
        lblMonth.setBounds(160-lblMonth.getPreferredSize().width/2, 25, 100, 25);
        btnPrev.setBounds(10, 25, 50, 25);
        btnNext.setBounds(260, 25, 50, 25);
        btnOK.setBounds(335, 310, 100, 25);
        btnCancel.setBounds(535 , 310, 100, 25);
        stblCalendar.setBounds(10, 50, 300, 250);

        //Make frame visible
        frmMain.setResizable(false);
        frmMain.setVisible(true);

        //Get current month and year
        GregorianCalendar cal = new GregorianCalendar();
        realDay = cal.get(GregorianCalendar.DAY_OF_MONTH);
        realMonth = cal.get(GregorianCalendar.MONTH);
        realYear = cal.get(GregorianCalendar.YEAR);
        currentMonth = realMonth;
        currentYear = realYear;

        //Add headers
        String[] headers = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i=0; i<7; i++){
            mtblCalendar.addColumn(headers[i]);
        }

        tblCalendar.getParent().setBackground(tblCalendar.getBackground());

        //No resize/reorder
        tblCalendar.getTableHeader().setResizingAllowed(false);
        tblCalendar.getTableHeader().setReorderingAllowed(false);

        //Single cell selection
        tblCalendar.setColumnSelectionAllowed(true);
        tblCalendar.setRowSelectionAllowed(true);
        tblCalendar.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //Set rows and columns count
        tblCalendar.setRowHeight(38);
        mtblCalendar.setColumnCount(7);
        mtblCalendar.setRowCount(6);

        //fill in notes and refresh calendar
        fillNotes();
        refreshCalendar (realMonth, realYear);
    }
    static private void fillNotes() {
        for (int m=1; m<=11; m++){
            for (int d=1; d <= 31; d++) {
                notes[m][d] = "No Notes";
            }
        }
    }

    static private void setNotePad(int day) {
        if (day != -1) {
            refreshCalendar(currentMonth, currentYear);
        }
        if (day == 0) {
            btnOK.setEnabled(false);
            btnCancel.setEnabled(false);
            lblMonth.setText(months[currentMonth]);
            notePad.replaceRange(null, 0, notePad.getText().length());
            notePad.replaceSelection("");
            notePad.setBorder(BorderFactory.createTitledBorder("No day selected"));
        } else {
            btnOK.setEnabled(true);
            btnCancel.setEnabled(true);
            selectedDay = day;
            lblMonth.setText(months[currentMonth] + " " + selectedDay);
            notePad.replaceRange(null, 0, notePad.getText().length());
            notePad.replaceSelection(notes[currentMonth][selectedDay]);
            notePad.setBorder(BorderFactory.createTitledBorder("Notes for " + (currentMonth + 1) + '/' + selectedDay));
        }
    }
    static private void refreshCalendar(int month, int year){
        //Variables
        int numOfDays, startOfMonth;

        //Set buttons
        btnPrev.setEnabled(true);
        btnNext.setEnabled(true);
        if (month == 0 && year <= realYear-10){btnPrev.setEnabled(false);}
        if (month == 11 && year >= realYear+100){btnNext.setEnabled(false);}

        //Set and align month label
        lblMonth.setText(months[month]);
        lblMonth.setBounds(160-lblMonth.getPreferredSize().width/2, 25, 180, 25);

        //Reset table
        for (int i=0; i<6; i++){
            for (int j=0; j<7; j++){
                mtblCalendar.setValueAt(null, i, j);
            }
        }

        //Get first day of month and number of days
        GregorianCalendar cal = new GregorianCalendar(year, month, 1);
        numOfDays = cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
        startOfMonth = cal.get(GregorianCalendar.DAY_OF_WEEK);

        //Draw calendar
        for (int i=1; i<=numOfDays; i++){
            int row = (i + startOfMonth - 2) / 7;
            int column  =  (i+startOfMonth-2)%7;
            mtblCalendar.setValueAt(i, row, column);
        }
        tblCalendar.setDefaultRenderer(tblCalendar.getColumnClass(0), new tblCalendarRenderer());
    }
    // Extend renderer to handle day selection
    static class tblCalendarRenderer extends DefaultTableCellRenderer{
        public Component getTableCellRendererComponent (JTable table, Object value, boolean selected, boolean focused, int row, int column){
            super.getTableCellRendererComponent(table, value, selected, focused, row, column);
            if (column == 0 || column == 6){
                setBackground(new Color(255, 220, 220));
            }
            else{
                setBackground(new Color(255, 255, 255));
            }
            if (value != null){
                if (Integer.parseInt(value.toString()) == realDay && currentMonth == realMonth && currentYear == realYear){
                    setBackground(new Color(220, 220, 255));
                    if (selectedDay == -1) {
                        setNotePad(realDay);
                        setBackground(new Color(122, 216, 147));
                    }
                }
            }
            if (selected && value != null) {
                setBackground(new Color(122, 216, 147));
            }
            setBorder(null);
            setForeground(Color.black);
            return this;
        }
    }


    static class btnPrevAction implements ActionListener{
        public void actionPerformed (ActionEvent e){
            if (currentMonth == 0){
                currentMonth = 11;
                currentYear -= 1;
            }
            else{
                currentMonth -= 1;
            }
            refreshCalendar(currentMonth, currentYear);
        }
    }
    static class btnNextAction implements ActionListener{
        public void actionPerformed (ActionEvent e){
            if (currentMonth == 11){
                currentMonth = 0;
                currentYear += 1;
            }
            else{
                currentMonth += 1;
            }
            refreshCalendar(currentMonth, currentYear);
        }
    }
    static class btnOKAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (selectedDay != -1) {
                notes[currentMonth][selectedDay] = notePad.getText();
            }
        }
    }

    static class btnCancelAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (selectedDay != -1) {
                notePad.replaceRange(null, 0, notePad.getText().length());
                notePad.replaceSelection(notes[currentMonth][selectedDay]);
            }
        }
    }

}