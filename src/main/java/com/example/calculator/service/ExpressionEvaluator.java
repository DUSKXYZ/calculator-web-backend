package com.example.calculator.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 表达式求值器。
 * 没有用 eval 之类直接执行代码的方式，而是自己实现了"调度场算法"：
 * 先把中缀表达式转换成后缀表达式（逆波兰式），再用栈计算结果。
 * 支持：加减乘除、括号、小数、一元正负号（例如 -5、3*-2）。
 */
public class ExpressionEvaluator {

    /**
     * 计算表达式，表达式非法或除零时抛出 IllegalArgumentException
     */
    public static double evaluate(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalArgumentException("表达式不能为空");
        }
        List<String> tokens = tokenize(expression);
        List<String> rpn = toRpn(tokens);
        return calcRpn(rpn);
    }

    /**
     * 第一步：把字符串切成一个个 token（数字、运算符、括号）
     */
    private static List<String> tokenize(String expression) {
        List<String> tokens = new ArrayList<>();
        StringBuilder number = new StringBuilder();
        String expectOperand = "start"; // 记录上一个 token 类型，用来判断正负号

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (c == ' ') {
                continue; // 忽略空格
            }

            if (Character.isDigit(c) || c == '.') {
                number.append(c);
                expectOperand = "number";
                continue;
            }

            // 遇到运算符或括号，先把前面攒的数字存起来
            if (number.length() > 0) {
                tokens.add(number.toString());
                number.setLength(0);
            }

            if (c == '+' || c == '-') {
                // 如果 + / - 出现在表达式开头、左括号后或运算符后，说明它是一元正负号
                boolean unary = expectOperand.equals("start")
                        || expectOperand.equals("operator")
                        || expectOperand.equals("leftParen");
                tokens.add(unary ? "u" + c : String.valueOf(c));
                expectOperand = "operator";
            } else if (c == '*' || c == '/') {
                tokens.add(String.valueOf(c));
                expectOperand = "operator";
            } else if (c == '(') {
                tokens.add("(");
                expectOperand = "leftParen";
            } else if (c == ')') {
                tokens.add(")");
                expectOperand = "number";
            } else {
                throw new IllegalArgumentException("表达式中含有非法字符: " + c);
            }
        }

        if (number.length() > 0) {
            tokens.add(number.toString());
        }
        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("表达式不能为空");
        }
        return tokens;
    }

    /** 运算符优先级 */
    private static int priority(String op) {
        switch (op) {
            case "u+":
            case "u-":
                return 3; // 一元正负号优先级最高
            case "*":
            case "/":
                return 2;
            case "+":
            case "-":
                return 1;
            default:
                return 0;
        }
    }

    /**
     * 第二步：调度场算法，把中缀 token 列表转成后缀表达式
     */
    private static List<String> toRpn(List<String> tokens) {
        List<String> output = new ArrayList<>();
        Stack<String> opStack = new Stack<>();

        for (String token : tokens) {
            if (isNumber(token)) {
                output.add(token);
            } else if (token.equals("(")) {
                opStack.push(token);
            } else if (token.equals(")")) {
                // 把左括号之前的运算符都弹出来
                while (!opStack.isEmpty() && !opStack.peek().equals("(")) {
                    output.add(opStack.pop());
                }
                if (opStack.isEmpty()) {
                    throw new IllegalArgumentException("括号不匹配");
                }
                opStack.pop(); // 弹出左括号
            } else {
                // 普通运算符：弹出优先级更高或相等的运算符
                while (!opStack.isEmpty() && !opStack.peek().equals("(")
                        && priority(opStack.peek()) >= priority(token)) {
                    output.add(opStack.pop());
                }
                opStack.push(token);
            }
        }

        while (!opStack.isEmpty()) {
            String op = opStack.pop();
            if (op.equals("(")) {
                throw new IllegalArgumentException("括号不匹配");
            }
            output.add(op);
        }
        return output;
    }

    /**
     * 第三步：用栈计算后缀表达式
     */
    private static double calcRpn(List<String> rpn) {
        Stack<Double> stack = new Stack<>();

        for (String token : rpn) {
            if (isNumber(token)) {
                double value;
                try {
                    value = Double.parseDouble(token);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("数字格式不正确: " + token);
                }
                stack.push(value);
            } else if (token.equals("u+") || token.equals("u-")) {
                // 一元正负号只需要一个操作数
                if (stack.isEmpty()) {
                    throw new IllegalArgumentException("表达式格式不正确");
                }
                double value = stack.pop();
                stack.push(token.equals("u-") ? -value : value);
            } else {
                if (stack.size() < 2) {
                    throw new IllegalArgumentException("表达式格式不正确");
                }
                double b = stack.pop();
                double a = stack.pop();
                switch (token) {
                    case "+":
                        stack.push(a + b);
                        break;
                    case "-":
                        stack.push(a - b);
                        break;
                    case "*":
                        stack.push(a * b);
                        break;
                    case "/":
                        if (b == 0) {
                            throw new IllegalArgumentException("除数不能为零");
                        }
                        stack.push(a / b);
                        break;
                    default:
                        throw new IllegalArgumentException("未知运算符: " + token);
                }
            }
        }

        if (stack.size() != 1) {
            throw new IllegalArgumentException("表达式格式不正确");
        }
        double result = stack.pop();
        if (Double.isNaN(result) || Double.isInfinite(result)) {
            throw new IllegalArgumentException("计算结果无效");
        }
        return result;
    }

    /** 判断 token 是不是数字 */
    private static boolean isNumber(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        return Character.isDigit(token.charAt(0)) || token.charAt(0) == '.';
    }

    /**
     * 把计算结果格式化成字符串。
     * 整数结果显示成 "20" 而不是 "20.0"；
     * 小数用 BigDecimal 处理浮点误差，最多保留 10 位小数并去掉末尾的 0，
     * 这样 0.1+0.2 会显示成 "0.3" 而不是 "0.30000000000000004"。
     */
    public static String formatResult(double value) {
        if (value == Math.floor(value) && !Double.isInfinite(value)
                && Math.abs(value) < 1e15) {
            return String.valueOf((long) value);
        }
        return new BigDecimal(Double.toString(value))
                .setScale(10, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }
}
