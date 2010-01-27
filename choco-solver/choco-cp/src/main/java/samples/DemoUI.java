/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  °(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package samples;

import choco.kernel.common.logging.ChocoLogging;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.logging.Logger;

/**
 * Main class for the demo
 */
public class DemoUI {
    protected final static Logger LOGGER = ChocoLogging.getMainLogger();

  public JList list;
  public JTextPane result, code;
  int demoNb;
  public boolean solving = false;

  public String[] demos = new String[]{
    "Queen",
    "SendMoreMoney",
    "SteinerSystem",
    "CycloHexan"
  };

  public void createGUI(Container contentPane) {
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    list = new JList(demos);
    list.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        updateCode();
      }
    });
    panel.add(list, BorderLayout.CENTER);
    panel.setBorder(BorderFactory.createTitledBorder("Problems"));
    panel.setBackground(Color.white);
    JButton button = new JButton("Run !");
    button.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if (!solving) runDemo();
      }
    });
    panel.add(button, BorderLayout.SOUTH);
    result = new JTextPane();
    result.setFont(new Font("Courier new", Font.BOLD, 12));
    result.setEditable(false);
    result.setBorder(BorderFactory.createTitledBorder("Results"));
    JScrollPane scroll = new JScrollPane(result);
    MyDocument document = new MyDocument();
    code = new JTextPane(document);
    code.setFont(new Font("Courier new", Font.PLAIN, 11));
    code.setEditable(false);
    code.setBorder(BorderFactory.createTitledBorder("How the code looks like ?"));
    JScrollPane scroll2 = new JScrollPane(code);
    JSplitPane subsplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroll, scroll2);
    JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel, subsplit);
    split.setDividerLocation(100);
    contentPane.add(split, BorderLayout.CENTER);
    subsplit.setDividerLocation(200);
  }

  public void runDemo() {
    demoNb = list.getSelectedIndex();
    if (demoNb < 0) {
      result.setText("No selected demonstration !\n" +
          "Please click on one demonstration on the right.");
    } else {
      solving = true;
      try {
        Thread th = new Thread() {
          public void run() {
            String ret;

            try {
              Class demoClass =
                  this.getClass().getClassLoader().
                  loadClass("samples." + demos[demoNb]);
              Object demoObject = demoClass.newInstance();
              Method demoMethod =
                  demoClass.getMethod("demo", new Class[]{});
              Object demoReturn = demoMethod.invoke(demoObject);
              ret = (String) demoReturn;
            } catch (ClassNotFoundException e) {
              ret = "Class not found !";
            } catch (NoSuchMethodException e) {
              ret = "No valid class found !";
            } catch (InstantiationException e) {
              ret = "Cannot create the demo object !";
            } catch (IllegalAccessException e) {
              ret = "Cannot access to the constructor and/or method of the demo !";
            } catch (InvocationTargetException e) {
              ret = "Demo throwed an exception : ";
              Throwable t = e;
              while (t != null) {
                Object[] traces = t.getStackTrace();
                  for (Object trace : traces) {
                      ret += trace + "\n";
                  }
                LOGGER.info(t.toString() + "\n");
                t = t.getCause();
              }
            }

            solving = false;
            result.setText(ret + "\nModel solved.");
          }
        };
        th.start();
      } catch (Exception e) {
        LOGGER.severe("Solving error !");
      }
    }
  }

  public void updateCode() {
    int nb = list.getSelectedIndex();
    StringBuffer buf = new StringBuffer();
    buf.append(MessageFormat.format("Code of {0}.java.\n\n", demos[nb]));
    InputStream in = this.getClass().getResourceAsStream("/src/samples/" + demos[nb] + ".java");
    if (in != null) {
      Reader reader = new BufferedReader(new InputStreamReader(in));
      int ch;
      try {
        while ((ch = reader.read()) > -1) {
          buf.append((char) ch);
        }
        reader.close();
      } catch (IOException e) {
        LOGGER.info("IO exception in DemoUI : " + Arrays.toString(e.getStackTrace()));
      }
    } else {
      buf.append("Source code not found !");
    }
    code.setText(buf.toString());
    code.setCaretPosition(0);
  }

  static class MyDocument extends DefaultStyledDocument {
    private Hashtable<String, Object> keywords;

    DefaultStyledDocument doc;
    MutableAttributeSet normal;
    MutableAttributeSet keyword;
    MutableAttributeSet comment;
    MutableAttributeSet quote;
    MutableAttributeSet impclasses1;

    public MyDocument() {
      doc = this;
      putProperty(DefaultEditorKit.EndOfLineStringProperty, "\n");
      normal = new SimpleAttributeSet();
      StyleConstants.setForeground(normal, Color.black);
      comment = new SimpleAttributeSet();
      StyleConstants.setForeground(comment, Color.lightGray);
      StyleConstants.setItalic(comment, true);
      keyword = new SimpleAttributeSet();
      StyleConstants.setForeground(keyword, new Color(110, 80, 220));
      StyleConstants.setBold(keyword, true);
      quote = new SimpleAttributeSet();
      StyleConstants.setForeground(quote, new Color(54, 150, 54));
      StyleConstants.setBold(quote, true);
      Object dummyObject = new Object();
      keywords = new Hashtable<String, Object>();
      keywords.put("abstract", dummyObject);
      keywords.put("boolean", dummyObject);
      keywords.put("break", dummyObject);
      keywords.put("byte", dummyObject);
      keywords.put("byvalue", dummyObject);
      keywords.put("case", dummyObject);
      keywords.put("cast", dummyObject);
      keywords.put("catch", dummyObject);
      keywords.put("char", dummyObject);
      keywords.put("class", dummyObject);
      keywords.put("const", dummyObject);
      keywords.put("continue", dummyObject);
      keywords.put("default", dummyObject);
      keywords.put("do", dummyObject);
      keywords.put("double", dummyObject);
      keywords.put("else", dummyObject);
      keywords.put("extends", dummyObject);
      keywords.put("false", dummyObject);
      keywords.put("final", dummyObject);
      keywords.put("finally", dummyObject);
      keywords.put("float", dummyObject);
      keywords.put("for", dummyObject);
      keywords.put("future", dummyObject);
      keywords.put("generic", dummyObject);
      keywords.put("goto", dummyObject);
      keywords.put("if", dummyObject);
      keywords.put("implements", dummyObject);
      keywords.put("import", dummyObject);
      keywords.put("inner", dummyObject);
      keywords.put("instanceof", dummyObject);
      keywords.put("int", dummyObject);
      keywords.put("interface", dummyObject);
      keywords.put("long", dummyObject);
      keywords.put("native", dummyObject);
      keywords.put("new", dummyObject);
      keywords.put("null", dummyObject);
      keywords.put("operator", dummyObject);
      keywords.put("outer", dummyObject);
      keywords.put("package", dummyObject);
      keywords.put("private", dummyObject);
      keywords.put("protected", dummyObject);
      keywords.put("public", dummyObject);
      keywords.put("rest", dummyObject);
      keywords.put("return", dummyObject);
      keywords.put("short", dummyObject);
      keywords.put("static", dummyObject);
      keywords.put("super", dummyObject);
      keywords.put("switch", dummyObject);
      keywords.put("synchronized", dummyObject);
      keywords.put("this", dummyObject);
      keywords.put("throw", dummyObject);
      keywords.put("throws", dummyObject);
      keywords.put("transient", dummyObject);
      keywords.put("true", dummyObject);
      keywords.put("try", dummyObject);
      keywords.put("var", dummyObject);
      keywords.put("void", dummyObject);
      keywords.put("volatile", dummyObject);
      keywords.put("while", dummyObject);
    }

    public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
      super.insertString(offset, str, a);
      processChangedLines(offset, str.length());
    }

    public void remove(int offset, int length) throws BadLocationException {
      super.remove(offset, length);
      processChangedLines(offset, 0);
    }

    public void processChangedLines(int offset, int length) throws BadLocationException {
      String content = doc.getText(0, doc.getLength());
      Element root = doc.getDefaultRootElement();
      int startLine = root.getElementIndex(offset);
      int endLine = root.getElementIndex(offset + length);
      for (int i = startLine; i <= endLine; i++) {
        int startOffset = root.getElement(i).getStartOffset();
        int endOffset = root.getElement(i).getEndOffset();
        applyHighlighting(content, startOffset, endOffset - 1);
      }
    }

    public void applyHighlighting(String content, int startOffset, int endOffset) throws BadLocationException {
      int index;
      int lineLength = endOffset - startOffset;
      int contentLength = content.length();
      if (endOffset >= contentLength) endOffset = contentLength - 1;
      //  set normal attributes for the line
      doc.setCharacterAttributes(startOffset, lineLength, normal, true);
      //  check for multi line comment
      String multiLineStartDelimiter = "/*";
      String multiLineEndDelimiter = "*/";
      index = content.lastIndexOf(multiLineStartDelimiter, endOffset);
      if (index > -1) {
        int index2 = content.indexOf(multiLineEndDelimiter, index);
        if ((index2 == -1) || (index2 > endOffset)) {
          doc.setCharacterAttributes(index, endOffset - index + 1, comment, false);
          return;
        } else if (index2 >= startOffset) {
          doc.setCharacterAttributes(index, index2 + 2 - index, comment, false);
          return;
        }
      }			//  check for single line comment
      String singleLineDelimiter = "//";
      index = content.indexOf(singleLineDelimiter, startOffset);
      if ((index > -1) && (index < endOffset)) {
        doc.setCharacterAttributes(index, endOffset - index + 1, comment, false);
        endOffset = index - 1;
      }			//  check for tokens
      checkForTokens(content, startOffset, endOffset);
    }

    private void checkForTokens(String content, int startOffset, int endOffset) {
      while (startOffset <= endOffset) {				//  find the start of a new token
        while (isDelimiter(content.substring(startOffset, startOffset + 1))) {
          if (startOffset < endOffset) startOffset++; else return;
        }			//
        if (isQuoteDelimiter(content.substring(startOffset, startOffset + 1)))
          startOffset = getQuoteToken(content, startOffset, endOffset);
        else
          startOffset = getOtherToken(content, startOffset, endOffset);
      }
    }

    private boolean isDelimiter(String character) {
      String operands = ";:{}()[]+-/%<=>!&|^~*";
        return Character.isWhitespace(character.charAt(0)) || operands.indexOf(character) != -1;
    }

    private boolean isQuoteDelimiter(String character) {
      String quoteDelimiters = "\"'";
        return quoteDelimiters.indexOf(character) != -1;
    }

    private boolean isKeyword(String token) {
      Object o = keywords.get(token);
      return o != null;
    }

    private int getQuoteToken(String content, int startOffset, int endOffset) {
      String quoteDelimiter = content.substring(startOffset, startOffset + 1);
      String escapedDelimiter = "\\" + quoteDelimiter;
      int index;
      int endOfQuote = startOffset;			//  skip over the escaped quotes in this quote
      index = content.indexOf(escapedDelimiter, endOfQuote + 1);
      while ((index > -1) && (index < endOffset)) {
        endOfQuote = index + 1;
        index = content.indexOf(escapedDelimiter, endOfQuote);
      }			// now find the matching delimiter
      index = content.indexOf(quoteDelimiter, endOfQuote + 1);
      if ((index == -1) || (index > endOffset)) endOfQuote = endOffset; else endOfQuote = index;
      doc.setCharacterAttributes(startOffset, endOfQuote - startOffset + 1, quote, false);
      //String token = content.substring(startOffset, endOfQuote + 1);
      //LOGGER.info( "quote: " + token );
      return endOfQuote + 1;
    }

    private int getOtherToken(String content, int startOffset, int endOffset) {
      int endOfToken = startOffset + 1;
      while (endOfToken <= endOffset) {
        if (isDelimiter(content.substring(endOfToken, endOfToken + 1))) break;
        endOfToken++;
      }
      String token = content.substring(startOffset, endOfToken);
      //LOGGER.info( "found: " + token );
      if (isKeyword(token)) doc.setCharacterAttributes(startOffset, endOfToken - startOffset, keyword, false);
      //if (isimpclasses1(token)) doc.setCharacterAttributes(startOffset, endOfToken - startOffset, impclasses1, false);
      return endOfToken + 1;
    }
  }
}


