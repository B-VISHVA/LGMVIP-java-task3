import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Calculator extends JFrame implements ActionListener {
    private JTextField textField;

    public Calculator() {
        setTitle("---Calculator---");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        textField = new JTextField();
        textField.setPreferredSize(new Dimension(380, 50));
        textField.setFont(new Font("Arial", Font.PLAIN, 20));
        textField.setEditable(false);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 4, 15, 15));// space
        
        String[] buttons = {
            "AC","1", "2", "3","+",
            "sin","4","5", "6","-",
            "cos","7",  "8","9","*",
            "tan","0",".", "=","/",
            "(", ")","sqrt", "log", "exp"
        };//"---alignment of txt field to order of an button---"
        
        for (String button : buttons) {
            JButton btn = new JButton(button);
            btn.setFont(new Font("Arial", Font.BOLD, 18));
            btn.addActionListener(this);
            buttonPanel.add(btn);
        }
        

        mainPanel.add(textField, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        String currentText = textField.getText();

        switch (command) {
            case "=":
                try {
                    double result = evaluateExpression(currentText);
                    textField.setText(Double.toString(result));
                } catch (Exception ex) {
                    textField.setText("Error");
                }
                break;
            case "sqrt":
                textField.setText(currentText + "sqrt(");
                break;
            case "AC":
                textField.setText(""); // Clear the text field
                break;
            default:
                textField.setText(currentText + command);
                break;
        }
    }

    private double evaluateExpression(String expression) {
        return new Object() {
            int pos = -1;
            int ch;

            void nextChar() {
                ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expression.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expression.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') {
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = expression.substring(startPos, this.pos);
                    x = parseFactor();
                    switch (func) {
                        case "sin":
                            x = Math.sin(Math.toRadians(x));
                            break;
                        case "cos":
                            x = Math.cos(Math.toRadians(x));
                            break;
                        case "tan":
                            x = Math.tan(Math.toRadians(x));
                            break;
                        case "sqrt":
                            x = Math.sqrt(x);
                            break;
                        case "log":
                            x = Math.log10(x);
                            break;
                        case "exp":
                            x = Math.exp(x);
                            break;
                        default:
                            throw new RuntimeException("Unknown function: " + func);
                    }
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor());

                return x;
            }
        }.parse();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Calculator();
            }
        });
    }
}
