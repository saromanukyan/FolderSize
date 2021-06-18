package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.CharacterIterator;
import java.text.DecimalFormat;
import java.text.StringCharacterIterator;

public class FolderSizeWindow extends JFrame {
    Container container = getContentPane();
    ImageIcon mainIcon = new ImageIcon("images\\up103.png");

    JLabel imageLabel = new JLabel("", mainIcon, SwingConstants.CENTER);
    JLabel nameLabel = new JLabel("Calculate Folder Size");
    JLabel folderSizeLabel = new JLabel();

    JTextField inputTextField = new JTextField("   Enter folder directory");

    JButton calculateBtn = new JButton("Calculate");
    static boolean firstClick = true;
    public static long fileSize;

    public FolderSizeWindow() {
        setVisible(true);
        setBounds(0, 0, 600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        container.setLayout(null);
        setLocationAndSize();
        setStyle();
        addComponentsToContainer();
        addActionEvent();
    }

    private void addActionEvent() {
        inputTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                if (firstClick) {
                    inputTextField.setText("");
                    inputTextField.setForeground(Color.BLACK);
                    firstClick = false;
                }
            }
        });

        calculateBtn.addActionListener(e -> {

            if (new File(inputTextField.getText()).isDirectory()) {

                Thread tempThread = new Thread(() -> fileSize = getFileSize(new File(inputTextField.getText())));

                Thread counterThread = new Thread(() -> {
                    while (tempThread.isAlive()) {
                        folderSizeLabel.setText(String.format("Size:    %s  ( %s bytes )",
                                getHumanReadableSize(fileSize), getFormattedSize(fileSize)));

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }

                    }
                    folderSizeLabel.setText(String.format("Size:    %s  ( %s bytes )",
                            getHumanReadableSize(fileSize), getFormattedSize(fileSize)));
                    fileSize = 0;

                });
                counterThread.start();
                tempThread.start();
            } else {
                JOptionPane.showMessageDialog(this, "Please, enter correct directory");

            }

        });
    }

    private void addComponentsToContainer() {
        container.add(imageLabel);
        container.add(nameLabel);
        container.add(inputTextField);
        container.add(calculateBtn);
        container.add(folderSizeLabel);

    }

    private void setStyle() {
        folderSizeLabel.setFont(nameLabel.getFont().deriveFont(18f));
        inputTextField.setForeground(Color.lightGray);
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.ITALIC, 34f));
        inputTextField.setFont(nameLabel.getFont().deriveFont(Font.ITALIC, 15f));
        // folderSizeLabel.setForeground(Color.blue);
        calculateBtn.setFont(nameLabel.getFont().deriveFont(18f));
    }

    private void setLocationAndSize() {
        imageLabel.setBounds(250, 20, 103, 103);
        nameLabel.setBounds(130, 140, 400, 50);
        inputTextField.setBounds(40, 220, 500, 30);
        folderSizeLabel.setBounds(130, 270, 500, 30);
        calculateBtn.setBounds(90, 320, 400, 40);
    }

    public static long getFileSize(File folder) {
        long foldersize = 0;
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {

                if (file.isDirectory()) {
                    foldersize += getFileSize(file);
                } else {
                    foldersize += file.length();
                    fileSize += file.length();
                }
            }
        }
        return foldersize;
    }

    private String getHumanReadableSize(long bytes) {
        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + " B";
        }
        long value = absB;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(bytes);
        return String.format("%.1f %cB", value / 1024.0, ci.current());
    }

    private String getFormattedSize(long size) {
        return new DecimalFormat("#,##0.#").format(size);
    }
}
