import javax.swing.*;
import javax.swing.border.EmptyBorder;

//import javafx.scene.chart.PieChart;

import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.time.*;

import static java.lang.Math.round;

public class Menu {

    public String writingPolice = "Garamond";  // set global variables from writing police as well as window size
    int windowX = 700;
    int windowY = 350;


    // money reloader
    private double userMoneyDouble;
    private String userMoney;


    // history reloader
    JPanel historyPanel;
    List<List<String>> history;
    JPanel rowPanel;
    String[] columnsName = {"ID", "Money Before", "Amount", "Type" , "Money After", "Date", "Notes"};


    // monthly report reloader
    JPanel barChartPanel;
    double income;
    double expenses;
    LocalDate today;
    IntegerPair results;
    JPanel monthlyReportWindow;


    // window
    JFrame frame;
    CardLayout cardLayout;
    JPanel panel;


    // db
    DB db = new DB();

    double actualMoney = db.getMoney();
    JButton moneyButton; // button for user money amount
    JTextField currentBudget; //

    // colors
    Color beigeParchemin = new Color(245,222,175); // name of the color
    Color colorBrown = new Color(205,133,63);


    public Menu() throws SQLException {
        //set up frame
        frame = new JFrame("BrokeNoMore Manager");
        frame.setSize(this.windowX, this.windowY);
        frame.setLocationRelativeTo(null); // put the frame in the middle of the frame
        frame.setResizable(true); // disallow resizing the window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // exit when close cross pressed

        cardLayout = new CardLayout();
        panel = new JPanel(cardLayout);

        JPanel menuLauncher = menuLauncher();
        JPanel toolWindow = toolWindow();
        JPanel moneyWindow = moneyWindow();
        JPanel converterWindow = converterWindow();
        JPanel monthlyReportWindow = monthlyReportWindow();
        JPanel actionsWindow = actionsWindow();
        JPanel historyWindow = historyWindow();
        JPanel limitWindow = limitWindow();
        JPanel budgetWindow = budgetWindow();

        panel.add(menuLauncher, "MenuLauncher");
        panel.add(toolWindow, "ToolWindow");
        panel.add(moneyWindow, "MoneyWindow");
        panel.add(converterWindow, "ConverterWindow");
        panel.add(actionsWindow, "actionsWindow");
        panel.add(historyWindow, "historyWindow");
        panel.add(limitWindow, "limitWindow");
        panel.add(budgetWindow, "budgetWindow");
        panel.add(monthlyReportWindow, "MonthlyReportWindow");
        frame.add(panel);

        frame.setVisible(true);
    }

    public JPanel menuLauncher() throws SQLException {

        userMoneyDouble = db.getMoney(); // get money amount from the databse
        userMoney = String.format("%.2f", userMoneyDouble); // formatting into a string
        // set menu panel
        JPanel menuLauncher = new JPanel(new BorderLayout());
        //frame.setTitle("BrokeNoMore Manager");

        JLabel titleLabel = new JLabel("Balance");
        titleLabel.setFont(new Font(writingPolice, Font.BOLD, 30));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER); // center the title

        moneyButton = new JButton(this.userMoney + "$"); // label where the amount of money is displayed "button" because we need to click on it
        moneyButton.setFont(new Font(writingPolice, Font.PLAIN, 50));
        moneyButton.setBackground(new Color(255, 239, 213)); // set color
        moneyButton.setHorizontalAlignment(SwingConstants.CENTER);

        if (db.getMoney() < 0){
            moneyButton.setBorder(BorderFactory.createLineBorder(Color.RED, 5, true));
        }
        else{
            moneyButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
        }

        JPanel panelBalance = new JPanel(); // making a panel where we will put all the components
        panelBalance.setLayout(new BoxLayout(panelBalance, BoxLayout.Y_AXIS));
        panelBalance.add(titleLabel);

        // monthly report button
        JButton monthlyReportButton = new JButton("Report");
        monthlyReportButton.setFont(new Font(writingPolice, Font.BOLD, 30));
        monthlyReportButton.setBackground(new Color(100,120,245));

        // add some spacing between the label and button
        panelBalance.add(Box.createVerticalStrut(5));
        panelBalance.add(moneyButton);



        // align components to the center
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        moneyButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // button tools
        JButton buttonTool = new JButton("Tools");
        buttonTool.setFont(new Font(writingPolice, Font.BOLD, 30));
        buttonTool.setBackground(new Color(245,222,175)); // button tool on main window

        // close button
        JButton buttonClose = new JButton("Close");
        buttonClose.setFont(new Font(writingPolice, Font.BOLD, 30));
        buttonClose.setBackground(colorBrown); // button close main

        JPanel panelButtons = new JPanel(new GridLayout(1, 3, 10, 0)); // grid layout for displaying the two buttons at the bottom of the panel
        panelButtons.add(buttonTool);
        panelButtons.add(monthlyReportButton);
        panelButtons.add(buttonClose);


        // setting sizes
        buttonClose.setSize(250, 100);
        buttonTool.setSize(250, 100);

        // put an empty border for a margin effect
        panelButtons.setBorder(new EmptyBorder(20, 30, 20, 30));
        menuLauncher.add(panelBalance, BorderLayout.CENTER);
        menuLauncher.add(panelButtons, BorderLayout.SOUTH);

        // actions listener
        buttonTool.addActionListener(e -> {
            cardLayout.show(panel, "ToolWindow"); // showing "ToolWindow" panel
        });

        moneyButton.addActionListener(e -> {
            cardLayout.show(panel, "MoneyWindow");
        });

        buttonClose.addActionListener(e -> {
            frame.dispose();
            System.exit(0); // stop the Java program from running
        });

        monthlyReportButton.addActionListener(e -> {
            try {
                reloadReportWindow();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            cardLayout.show(panel, "MonthlyReportWindow");
        });
        return menuLauncher;
    }


    public JPanel toolWindow() {
        JPanel toolWindow = new JPanel();
        //("BrokeNoMore Tools");
        int nbRows = 2;
        int nbCols = 3;
        GridLayout grid = new GridLayout(nbRows, nbCols, 10, 10); // grid layout for all the buttons that represents the functionalities
        toolWindow.setLayout(grid);

        String[] buttonNames = {"Converter", "Set Limit", "Actions", "History", "Budget Planner", "Return to menu"}; // all the names of the buttons
        Color[] colors = {beigeParchemin, beigeParchemin, beigeParchemin, beigeParchemin, beigeParchemin, colorBrown}; // all the colors of the buttons, same order as the name of the buttons
        // list of actions listener to every single buttons
        ActionListener[] eventListeners = {
                e -> {
                    cardLayout.show(panel, "ConverterWindow");
                },

                e -> {
                    cardLayout.show(panel, "limitWindow");
                },

                e -> {
                    cardLayout.show(panel, "actionsWindow");
                },

                e -> {
                    cardLayout.show(panel, "historyWindow");
                    try {
                        reloadHistory(); // realod history each time the user want to see his history
                    } catch (SQLException ex) {
                        errorMessage(ex.getMessage());
                    }
                },

                e -> {cardLayout.show(panel, "budgetWindow");
                    try {
                        currentBudget.setText(String.format("%.2f", db.getMoneyBudget())); // update of the budget
                    } catch (SQLException ex) {
                        errorMessage("Failed to load current budget.");
                    }},

                e -> {cardLayout.show(panel, "MenuLauncher");}
        };

        for (int i = 1; i <= nbRows*nbCols; i++) {
            JButton button = new JButton(buttonNames[i-1]); // assign name to every buttons on our list
            button.setFont(new Font(writingPolice, Font.BOLD, 20)); // assigning the same font for every buttons
            button.setBackground(colors[i-1]); // assigning to each button its color
            toolWindow.add(button); // add the button one by one to the panel
            button.setBorder(BorderFactory.createLineBorder(Color.black, 2, true));

            button.addActionListener(eventListeners[i-1]); // put the corresponding action listener to every button, one by one, in the ordre
        }
        //frame.setVisible(true);
        return toolWindow;
    }

    public JPanel monthlyReportWindow() throws SQLException {
        JPanel monthlyReportWindow = new JPanel(new BorderLayout());
        LocalDate today = LocalDate.now();
        IntegerPair results = db.getMonthlyHistory(today);

        JButton buttonReturn = new JButton("Return to Menu");
        buttonReturn.setFont(new Font(writingPolice, Font.BOLD, 30));
        buttonReturn.setBackground(colorBrown);

        buttonReturn.addActionListener(eventListeners -> {
            cardLayout.show(panel, "MenuLauncher");
        });

        class BarChart extends JPanel {

            public BarChart(double income_, double expenses_) {
                income = income_;
                expenses = expenses_;
                setPreferredSize(new Dimension(300, 300));
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                int width = 100; // Width of each bar
                int height = getHeight();
                int maxBarHeight = height - 50; // Leave some space at the top and bottom

                // Calculate the height of income and expense bars based on the total
                double total = income + Math.abs(expenses);
                int incomeBarHeight = (int) (income / total * maxBarHeight);
                int expenseBarHeight = (int) (Math.abs(expenses) / total * maxBarHeight);

                // Draw income bar (green)
                g2d.setColor(Color.GREEN);
                g2d.fillRect(50, height - incomeBarHeight - 30, width, incomeBarHeight);

                // Draw labels
                g2d.setColor(Color.RED);
                g2d.fillRect(200, height - expenseBarHeight - 30, width, expenseBarHeight);

                // Draw expenses bar (red)
                g2d.setColor(Color.BLACK);
                g2d.drawString("Income", 70, height - 10);
                g2d.drawString("Expenses", 220, height - 10);

                // print value above charts
                g2d.setColor(Color.BLACK);
                g2d.drawString(String.format("%.2f", income), 70, height - incomeBarHeight - 40); // position above the income bar
                g2d.drawString(String.format("%.2f", expenses), 220, height - expenseBarHeight - 40);
            }
        }

        barChartPanel = new BarChart(results.getFirst(), results.getSecond());
        monthlyReportWindow.add(buttonReturn, BorderLayout.SOUTH);
        monthlyReportWindow.add(barChartPanel, BorderLayout.CENTER);
        return monthlyReportWindow;
    }

    public JPanel moneyWindow(){
        JPanel moneyWindow = new JPanel(new BorderLayout());
        //frame.setTitle("Asset Manager");


        JLabel titleLabel = new JLabel("Asset Viewer");
        titleLabel.setFont(new Font(writingPolice, Font.BOLD, 30));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER); // center

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.add(titleLabel);

        String[][] moneyRepartition = {{"1", "In-Bank", "1500$", "1500$"},
                {"2", "Cash", "51.18$", "51.18$"},
                {"3", "Euro", "20€", "22.09$"},
                {"4", "Gold", "0.035 oz", "12.97$"},
                {"5", "Bitcoin", "0.00002 ₿", "11.45$"}
        };

        String[] column = {"Rank", "Asset", "Quantity", "Dollar Value"};

        JTable tableMoney = new JTable(moneyRepartition, column);
        tableMoney.setSize(500,250);
        tableMoney.setPreferredSize(new Dimension(500,500));
        JScrollPane scrollPane = new JScrollPane(tableMoney);

        tableMoney.getColumnModel().getColumn(0).setPreferredWidth(20); // set the cells width

        tableMoney.setRowHeight(40);
        tableMoney.setFont(new Font(writingPolice, Font.BOLD, 30));

        JButton buttonReturn = new JButton("Return to Menu");
        buttonReturn.setFont(new Font(writingPolice, Font.BOLD, 30));
        buttonReturn.setBackground(colorBrown);

        titlePanel.setVisible(true);
        moneyWindow.add(titlePanel, BorderLayout.NORTH);
        moneyWindow.add(buttonReturn, BorderLayout.SOUTH);
        moneyWindow.add(scrollPane, BorderLayout.CENTER);

        buttonReturn.addActionListener(e -> {
            cardLayout.show(panel, "MenuLauncher");
        });
        return moneyWindow;
    }

    public JPanel converterWindow(){
        JPanel converterWindow = new JPanel(null);
        //frame.setTitle("Converter");

        // creating two text areas
        JTextArea textArea = new JTextArea();
        JTextArea textArea2 = new JTextArea();

        // align, set size, set borders of the text areas
        textArea.setBounds(100, 110, 200, 50);
        textArea.setFont(new Font(writingPolice, Font.BOLD, 30));
        textArea2.setBounds(400, 110, 200, 50);
        textArea2.setFont(new Font(writingPolice, Font.BOLD, 30));
        textArea.setBorder(BorderFactory.createLineBorder(Color.black, 5, true));
        textArea2.setBorder(BorderFactory.createLineBorder(Color.black, 5, true));

        textArea2.setEditable(false);

        String[] listCurrencies = {"EUR", "USD", "GBP", "WON", "CA"}; // put all the currencies we are proposing
        JComboBox<String> currenciesBox = new JComboBox<>(listCurrencies); // create 2 comboboxes
        JComboBox<String> currenciesBox2 = new JComboBox<>(listCurrencies);
        currenciesBox2.setSelectedIndex(1); // put the default index at 1 = USD so we have different currencies when we arrive on this menu

        // setting font, size and adding them to the window
        currenciesBox.setFont(new Font(writingPolice, Font.BOLD, 30));
        currenciesBox.setBounds(100, 40, 200, 50);

        currenciesBox2.setFont(new Font(writingPolice, Font.BOLD, 30));
        currenciesBox2.setBounds(400, 40, 200, 50);

        converterWindow.add(currenciesBox);
        converterWindow.add(currenciesBox2);

        converterWindow.add(textArea);
        converterWindow.add(textArea2);

        // setting up two buttons, same code pattern for these two button at the bottom as the menuLauncher() function
        JButton buttonConvert = new JButton("Convert");
        buttonConvert.setFont(new Font(writingPolice, Font.BOLD, 30));
        buttonConvert.setBackground(beigeParchemin);

        JButton buttonReturn = new JButton("Return to tools");
        buttonReturn.setFont(new Font(writingPolice, Font.BOLD, 30));
        buttonReturn.setBackground(colorBrown);

        buttonReturn.setBounds(400, 250, 250, 50);
        buttonConvert.setBounds(50, 250, 250, 50);

        converterWindow.add(buttonConvert);
        converterWindow.add(buttonReturn);

        buttonConvert.addActionListener(e -> {
            if (textArea.getText().isEmpty()){
                errorMessage("Please enter the amount you want to convert");
                return;
            }

            if (!textArea.getText().matches("^[0-9]+([,.][0-9])?([0-9])?$")){ // regex to matches only numbers with 2 decimal after the comma or the dot
                errorMessage("Please only enter a number !");
                return;
            }

            Map<String, Double> conversionRates = new HashMap<>(); // creating a dictionnary

            conversionRates.put("EUR", 1.0); // 1 eur = 1 eur
            conversionRates.put("USD", 1.09); // 1 eur = 1.09 usd
            conversionRates.put("GBP", 0.84); // and so on...
            conversionRates.put("WON", 1474.5);
            conversionRates.put("CA", 1.50);

            // get the selected currencies for each boxes
            String startCurr = (String) currenciesBox.getSelectedItem();
            String targetCurr = (String) currenciesBox2.getSelectedItem();

            String userInput = textArea.getText().replace(",", "."); // replacing comma with dot whever we emcounter them
            double amount = Double.parseDouble(userInput); // convert to double

            double amountEur = amount / conversionRates.get(startCurr); // convert in EUR

            double amountTarget = amountEur * conversionRates.get(targetCurr); // convert in the target currency

            textArea2.setText(String.format("%.2f", amountTarget)); // convert double to string and display with the specific format
        });

        buttonReturn.addActionListener(e -> {
            cardLayout.show(panel, "ToolWindow");
        });
        return converterWindow;
    }


    public JPanel actionsWindow() throws SQLException {
        JPanel actionsWindow = new JPanel(new BorderLayout(20,20));

        // adding buttons with the same logic as previously
        JButton buttonLog = new JButton("Log Action");
        buttonLog.setFont(new Font(writingPolice, Font.BOLD, 30));
        buttonLog.setBackground(beigeParchemin); // green

        JButton buttonReturn = new JButton("Return to Tools");
        buttonReturn.setFont(new Font(writingPolice, Font.BOLD, 30));
        buttonReturn.setBackground(colorBrown);

        JPanel panelButtons = new JPanel(new GridLayout(1, 2, 10, 0));
        panelButtons.add(buttonLog);
        panelButtons.add(buttonReturn);
        panelButtons.setBorder(new EmptyBorder(20, 30, 20, 30));

        actionsWindow.add(panelButtons, BorderLayout.SOUTH);

        // adding text areas with the same logic as before
        JTextArea amountMoneyArea = new JTextArea();
        amountMoneyArea.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5, true));
        amountMoneyArea.setFont(new Font(writingPolice, Font.BOLD, 30));

        String[] typeTransaction = {"Deposit", "Withdraw"}; // two types of transactions
        JComboBox<String> typeBox = new JComboBox<>(typeTransaction); // combobox for the type of transaction
        typeBox.setFont(new Font(writingPolice, Font.BOLD, 30));

        JTextArea transactionNotesArea = new JTextArea();
        transactionNotesArea.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5, true));
        transactionNotesArea.setFont(new Font(writingPolice, Font.BOLD, 30));

        // adding corresponding text to each input box
        JPanel panelActions = new JPanel(new GridLayout(3, 2, 2, 20));
        panelActions.add(new JLabel("Amount:"));
        panelActions.add(amountMoneyArea);
        panelActions.add(new JLabel("Notes:"));
        panelActions.add(transactionNotesArea);
        panelActions.add(new JLabel("Transaction Type:"));
        panelActions.add(typeBox);

        panelButtons.setFont(new Font(writingPolice, Font.BOLD, 30));

        actionsWindow.add(panelActions, BorderLayout.CENTER);


        buttonReturn.addActionListener(e -> {
            cardLayout.show(panel, "ToolWindow");
        });

        buttonLog.addActionListener(e -> {
            double moneyBefore = 0; // variable for the money before the transaction
            double moneyAfter = 0; // variable for the money after the transaction
            String notes = transactionNotesArea.getText(); // notes that the user want to put for the transaction
            String type = typeBox.getSelectedItem().toString(); // get the option the user want : "deposit" or "withdraw"

            try {
                reloadMoney(); // reloading the amount of money
                moneyBefore = db.getMoney();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

            if (amountMoneyArea.getText().isEmpty() || transactionNotesArea.getText().isEmpty()){
                errorMessage("Please fill all the fields !"); // if the fields are empty, display error
                return;
            }

            if (!amountMoneyArea.getText().matches("^[0-9]+([,.][0-9])?([0-9])?$")){  // regex matching number with only one "." or ","
                errorMessage("Please enter numbers only on the money area !");
                return;
            }

            String moneyAreaDoubleString = amountMoneyArea.getText().replace(",", "."); // we allowed "," caracter, so we should replace it by "." so it will not get an error when parsing to double

            double transactionAmount = Double.parseDouble(moneyAreaDoubleString); // convert to double

            if (transactionAmount < 0){
                errorMessage("Please enter a positive amount of money !"); // check positive amount of money
                return;
            }

            //check budget
            String userAnswer = "hello world"; //to take teh decision of the user, to withdraw or not if it exceeds budget
            try{
                if (Objects.equals(type, "Withdraw") && transactionAmount > db.getMoneyBudget()){ // check if the user want to withdraw more money than he actually wants as he has set a budget

                    //warn the user if it exceeds the budget
                    userAnswer = JOptionPane.showInputDialog("If you withdraw " + transactionAmount + "$, you will go over your budget. " +
                            "Do you really want to exceed the previously set budget (Y/N): ");
                    if (userAnswer == null) { //case where the user close the window and doesn't answer
                        JOptionPane.showMessageDialog(null,
                                "Transaction cancelled. No input provided.",
                                "Information",
                                JOptionPane.INFORMATION_MESSAGE);
                        return; // Annuler la transaction si l'utilisateur annule ou ne fournit pas de réponse
                    }



                    //loop to check valid input
                    while(!(userAnswer.equalsIgnoreCase("Y") || userAnswer.equalsIgnoreCase("N"))){
                        userAnswer = JOptionPane.showInputDialog("Please enter a valid input, Y or N: ");
                        if (userAnswer == null) {
                            JOptionPane.showMessageDialog(null,
                                    "Transaction cancelled. No input provided.",
                                    "Information",
                                    JOptionPane.INFORMATION_MESSAGE);
                            return; // Annuler la transaction si l'utilisateur annule encore
                        }
                    }
                    // If N, cancel transaction
                    if (userAnswer.equalsIgnoreCase("N")) {
                        JOptionPane.showMessageDialog(null,
                                "Transaction cancelled. You chose not to exceed your budget.",
                                "Information",
                                JOptionPane.INFORMATION_MESSAGE); // Show information message

                        return; // Cancel the transaction
                    }
                    //continue

                }

            } catch(SQLException ex){
                errorMessage(ex.getMessage());
            }


            //check limit
            try{
                if (Objects.equals(type, "Withdraw") && transactionAmount > db.getMoney() + db.getMoneyLimit()){ // check if the user want to withdraw more money than ha actually has
                    errorMessage("You can't withdraw " + transactionAmount + "$ ! You will be overdrawn by " + String.format("%.2f", db.getMoney() - transactionAmount) + "$ !");
                    return;
                }
            } catch (SQLException ex) {
                errorMessage(ex.getMessage());
            }

            if (type == "Deposit"){
                moneyAfter = moneyBefore + transactionAmount; // calculate the money after if the user make deposti
                try {
                    db.addMoney(transactionAmount); // add money corresponding to the amount of money
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            else if (type == "Withdraw"){
                moneyAfter = moneyBefore - transactionAmount; // calculate the money after if the user make withdrawing
                try {
                    db.addMoney(-transactionAmount); // add money corresponding to the negative of the value of money

                    if(userAnswer.equalsIgnoreCase("Y")){ //it means that the transaction amount go aver the limit so we reset the budget to its iniatial value
                        db.setMoneyBudget(2000);
                        JOptionPane.showMessageDialog(null, "Your budget planner has been reset to 2000$.", "Information", JOptionPane.INFORMATION_MESSAGE);
                    }else {
                        db.setMoneyBudget(db.getMoneyBudget()-transactionAmount);
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            try {
                db.addLogs(moneyBefore, transactionAmount, type, moneyAfter, notes); // add the actions to the logs
            }
            catch (SQLException ex) {
                ex.printStackTrace();
            }

            try {
                reloadMoney(); // reload the user money in the database
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

            if (type == "Deposit"){ // display message accordingly
                infoMessage("Successfully deposit " + transactionAmount + "$ ! You have now " + String.format("%.2f", moneyAfter) + "$ !");
            }

            if (type == "Withdraw"){
                try {
                    if(userAnswer.equalsIgnoreCase("Y")){
                        infoMessage("Successful withdraw " + transactionAmount + "$ ! You have now " + String.format("%.2f", moneyAfter) + "$ !");
                    }else {
                        infoMessage("Successful withdraw " + transactionAmount + "$ ! You have now " + String.format("%.2f", moneyAfter) +
                                "$ and you have now " + db.getMoneyBudget() + "$ still available on your budget !");
                    }
                }catch(SQLException ex){
                    ex.printStackTrace();
                }
            }

            // reset to text areas, ready to proceed another withdraw/deposit
            amountMoneyArea.setText("");
            transactionNotesArea.setText("");

        });
        return actionsWindow;
    }

    public JPanel historyWindow() throws SQLException {

        JPanel historyWindow = new JPanel(new BorderLayout(20, 20));

        // implementing buttons using the same logic as before
        JButton buttonReturn = new JButton("Return to Tools");
        buttonReturn.setFont(new Font(writingPolice, Font.BOLD, 30));
        buttonReturn.setBackground(colorBrown);

        JPanel panelButtons = new JPanel(new GridLayout(1, 1, 10, 0));
        panelButtons.add(buttonReturn);
        panelButtons.setBorder(new EmptyBorder(20, 30, 20, 30));

        historyWindow.add(panelButtons, BorderLayout.SOUTH);

        historyPanel = new JPanel();
        historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(historyPanel);
        historyWindow.add(scrollPane, BorderLayout.CENTER);

        JPanel headerPanel = new JPanel(new GridLayout(1, 7, 5, 0)); // gird panel for displaying the columns if the database

        for (String header : columnsName) { // add the names of the columns (equivalent of for header in columnsName)
            JLabel headerLabel = new JLabel(header);
            headerLabel.setFont(new Font(writingPolice, Font.BOLD, 20));
            headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
            headerPanel.add(headerLabel);
        }
        historyPanel.add(headerPanel); // add column names

        try {
            history = db.getHistory(); // get history from DB, table : "logs"

            for (List<String> row : history) { // equivalent to (for row in history)
                rowPanel = new JPanel(new GridLayout(1, 7, 5, 0));

                for (String data : row) { // equivalent to (for data in row)
                    JLabel label = new JLabel(data);
                    label.setHorizontalAlignment(SwingConstants.CENTER); // align cells
                    rowPanel.add(label);
                    label.setBorder(BorderFactory.createLineBorder(beigeParchemin));
                }
                rowPanel.setBorder(BorderFactory.createLineBorder(beigeParchemin)); // interline border
                historyPanel.setBorder(BorderFactory.createLineBorder(beigeParchemin)); // outline (exterior) border
                historyPanel.add(rowPanel);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        buttonReturn.addActionListener(e -> {
            cardLayout.show(panel, "ToolWindow");
        });

        return historyWindow;
    }


    /*public JPanel monthlyReportWindow() throws SQLException {

    }*/

    public void reloadHistory() throws SQLException {
        // this function is simply the history function in order to reload it
        historyPanel.removeAll(); // delete everything and rewrite everything : maybe need to improve by just adding the new line each time

        history = db.getHistory(); // get history from DB, table : "logs"

        rowPanel = new JPanel(new GridLayout(1, 7, 5, 0)); // grid layout for the headers

        for (String header : columnsName) { // add the names of the columns
            JLabel headerLabel = new JLabel(header);
            headerLabel.setFont(new Font(writingPolice, Font.BOLD, 20));
            headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
            rowPanel.add(headerLabel);
        }
        historyPanel.add(rowPanel); // add column names

        for (List<String> row : history) {
            rowPanel = new JPanel(new GridLayout(1, 7, 5, 0));

            for (String data : row) {
                JLabel label = new JLabel(data);
                label.setHorizontalAlignment(SwingConstants.CENTER); // align cells
                rowPanel.add(label);
                label.setBorder(BorderFactory.createLineBorder(beigeParchemin));
            }
            rowPanel.setBorder(BorderFactory.createLineBorder(beigeParchemin)); // interline border
            historyPanel.setBorder(BorderFactory.createLineBorder(beigeParchemin)); // outline (exterior) border
            historyPanel.add(rowPanel);
        }

        panel.revalidate();
        panel.repaint();
    }

    public JPanel budgetWindow() throws SQLException {
        JPanel budgetWindow = new JPanel(new GridLayout(3, 2, 10, 10)); // 2 rows, 2 columns with gaps
        budgetWindow.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding to the panel

        // Create 2 labels with corresponding text areas
        // LABELS
        JLabel budgetArea = new JLabel("Set new budget", JLabel.CENTER); // Center the label text
        budgetArea.setFont(new Font(writingPolice, Font.BOLD, 30));
        budgetArea.setVerticalAlignment(JLabel.CENTER); // Align vertically to center with the text fields

        JLabel budgetAreaDisplay = new JLabel("Current budget", JLabel.CENTER); // Center the label text
        budgetAreaDisplay.setFont(new Font(writingPolice, Font.BOLD, 30));
        budgetAreaDisplay.setVerticalAlignment(JLabel.CENTER); // Align vertically to center with the text fields

        // Text AREAS
        JTextField newBudget = new JTextField();
        newBudget.setFont(new Font(writingPolice, Font.BOLD, 30));
        currentBudget = new JTextField();
        currentBudget.setFont(new Font(writingPolice, Font.BOLD, 30)); //show the current budget
        newBudget.setBorder(BorderFactory.createLineBorder(Color.black, 5, true));
        currentBudget.setBorder(BorderFactory.createLineBorder(Color.black, 5, true));


        currentBudget.setText(String.format("%.2f", db.getMoneyBudget())); // Call getMoneyBudget() to get the correct value

        currentBudget.setEditable(false);
        // Buttons
        JButton buttonSetBudget = new JButton("Set budget");
        buttonSetBudget.setFont(new Font(writingPolice, Font.BOLD, 30));
        buttonSetBudget.setBackground(beigeParchemin);


        JButton buttonReturn = new JButton("Return to tools");
        buttonReturn.setFont(new Font(writingPolice, Font.BOLD, 30));
        buttonReturn.setBackground(colorBrown);


        // Add labels, text fields, and buttons to the panel
        budgetWindow.add(budgetArea);
        budgetWindow.add(budgetAreaDisplay);
        budgetWindow.add(newBudget);
        budgetWindow.add(currentBudget);
        budgetWindow.add(buttonSetBudget);
        budgetWindow.add(buttonReturn);

        buttonSetBudget.addActionListener(e -> {
            if (newBudget.getText().isEmpty()){
                errorMessage("You must specify the new budget !");
                return;
            }

            if ( !!! newBudget.getText().matches("^[0-9]+([,.][0-9])?([0-9])?$")){ // allowing only number with 2 decimal maximum after the comma or the dot
                errorMessage("Put a valid budget amount !");
                return;
            }

            try {
                String amountString = newBudget.getText().replace(",", "."); // replace comma with dot if needed
                double amountDouble = Double.parseDouble(amountString); // convert to double

                db.setMoneyBudget(amountDouble); // set money budget into db
                currentBudget.setText(String.format("%.2f", amountDouble)); // display the new budget in the second text area
                infoMessage("Succesfully set the new budget at " + amountString + "$ !");
                newBudget.setText("");

            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        buttonReturn.addActionListener(e -> {

            cardLayout.show(panel, "ToolWindow");
        });

        return budgetWindow;
    }


    public JPanel limitWindow() throws SQLException {
        JPanel limitWindow = new JPanel(null);

        // create 2 texts areas, set text, font, borders
        JTextArea textArea = new JTextArea();
        JTextArea textArea2 = new JTextArea();

        textArea.setBounds(100, 110, 200, 50);
        textArea.setFont(new Font(writingPolice, Font.BOLD, 30));
        textArea2.setBounds(400, 110, 200, 50);
        textArea2.setFont(new Font(writingPolice, Font.BOLD, 30));
        textArea.setBorder(BorderFactory.createLineBorder(Color.black, 5, true));
        textArea2.setBorder(BorderFactory.createLineBorder(Color.black, 5, true));

        textArea2.setEditable(false);

        textArea2.setText(String.format("%.2f", db.getMoneyLimit()));

        // put two labels
        JLabel limitArea = new JLabel("Set new limit");
        JLabel limitAreaDisplay = new JLabel("Current limit");

        limitArea.setFont(new Font(writingPolice, Font.BOLD, 30));
        limitArea.setBounds(110, 60, 200, 50);

        limitAreaDisplay.setFont(new Font(writingPolice, Font.BOLD, 30));
        limitAreaDisplay.setBounds(410, 60, 200, 50);

        limitWindow.add(limitArea);
        limitWindow.add(limitAreaDisplay);

        limitWindow.add(textArea);
        limitWindow.add(textArea2);

        // implementing two buttons, using the same logic as before
        JButton buttonSetLimit = new JButton("Set Limit");
        buttonSetLimit.setFont(new Font(writingPolice, Font.BOLD, 30));
        buttonSetLimit.setBackground(beigeParchemin);

        JButton buttonReturn = new JButton("Return to tools");
        buttonReturn.setFont(new Font(writingPolice, Font.BOLD, 30));
        buttonReturn.setBackground(colorBrown);

        buttonReturn.setBounds(400, 250, 250, 50);
        buttonSetLimit.setBounds(50, 250, 250, 50);

        limitWindow.add(buttonSetLimit);
        limitWindow.add(buttonReturn);

        buttonSetLimit.addActionListener(e -> {
            if (textArea.getText().isEmpty()){
                errorMessage("You must specify the new limit !");
                return;
            }

            if ( !!! textArea.getText().matches("^[0-9]+([,.][0-9])?([0-9])?$")){ // allowing only number with 2 decimal maximum after the comma or the dot
                errorMessage("Put a valid limit amount !");
                return;
            }

            try {
                String amountString = textArea.getText().replace(",", "."); // replace comma with dot if needed
                double amountDouble = Double.parseDouble(amountString); // convert to double

                db.setMoneyLimit(amountDouble); // set money limit into db
                textArea2.setText(String.format("%.2f", amountDouble)); // display the new limit in the second text area
                infoMessage("Succesfully set the new limit at " + amountString + "$ !");
                textArea.setText("");

            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        buttonReturn.addActionListener(e -> {
            cardLayout.show(panel, "ToolWindow");
        });
        return limitWindow;
    }



    public void reloadMoney() throws SQLException {
        // function used to reload money in the display in the home menu, the function take element from the original function
        userMoneyDouble = db.getMoney();
        userMoney = String.format("%.2f", userMoneyDouble);
        moneyButton.setText(userMoney + "$");
        if (db.getMoney() <= 0){
            moneyButton.setBorder(BorderFactory.createLineBorder(Color.RED, 5, true));
        }
        else{
            moneyButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
        }

        panel.revalidate();
        panel.repaint();
        this.actualMoney = db.getMoney(); // update the money
    }

    public void reloadReportWindow() throws SQLException {

        barChartPanel.removeAll();

        today = LocalDate.now();
        results = db.getMonthlyHistory(today);

        JButton buttonReturn = new JButton("Return to Menu");
        buttonReturn.setFont(new Font(writingPolice, Font.BOLD, 30));
        buttonReturn.setBackground(colorBrown);

        buttonReturn.addActionListener(eventListeners -> {
            cardLayout.show(panel, "MenuLauncher");
        });

        class BarChart extends JPanel {

            public BarChart(double income_, double expenses_) {
                income = income_;
                expenses = expenses_;
                setPreferredSize(new Dimension(300, 300));
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                int width = 100; // Width of each bar
                int height = getHeight();
                int maxBarHeight = height - 50; // Leave some space at the top and bottom

                // Calculate the height of income and expense bars based on the total
                double total = income + Math.abs(expenses);
                int incomeBarHeight = (int) (income / total * maxBarHeight);
                int expenseBarHeight = (int) (Math.abs(expenses) / total * maxBarHeight);

                // Draw income bar (green)
                g2d.setColor(Color.GREEN);
                g2d.fillRect(50, height - incomeBarHeight - 30, width, incomeBarHeight);

                // Draw labels
                g2d.setColor(Color.RED);
                g2d.fillRect(200, height - expenseBarHeight - 30, width, expenseBarHeight);

                // Draw expenses bar (red)
                g2d.setColor(Color.BLACK);
                g2d.drawString("Income", 70, height - 10);
                g2d.drawString("Expenses", 220, height - 10);

                // print value above charts
                g2d.setColor(Color.BLACK);
                g2d.drawString(String.format("%.2f", income), 70, height - incomeBarHeight - 40); // position above the income bar
                g2d.drawString(String.format("%.2f", expenses), 220, height - expenseBarHeight - 40);
            }
        }

        barChartPanel = new BarChart(results.getFirst(), results.getSecond());

        panel.revalidate();
        panel.repaint();
    }

    public void errorMessage(String message){
        JOptionPane.showMessageDialog(panel, "🚨 WARNING : " + message); // format all error message so we onyl have to call this function when we want to display an error message
    }

    public void infoMessage(String message){
        JOptionPane.showMessageDialog(panel, "INFO : " + message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    DatabaseCreation.createTableIfNotExists();
                    new Menu();

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}