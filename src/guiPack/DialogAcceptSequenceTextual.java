
package guiPack;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import JavaLib.*;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import javax.swing.ImageIcon;

public class DialogAcceptSequenceTextual extends javax.swing.JDialog {

    Vector <Integer> imgSequence;
    String textualPassword;
    public boolean running;
    
    /** Creates new form MessageBox */
    public DialogAcceptSequenceTextual(java.awt.Frame parent) {
        super(parent, true);
        initComponents();
        
        Dimension sd  = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(sd.width / 2 - this.getWidth()/ 2, sd.height / 2 - this.getHeight()/ 2);

        imgSequence = new Vector<Integer>();
        textualPassword = "";

        running = true;

        Timer t = new Timer();
        Show3DTimerTask show3DTT = new Show3DTimerTask(this);
        t.schedule(show3DTT, 250);
    }

    class Show3DTimerTask extends TimerTask {
        DialogAcceptSequenceTextual parent;
        int img;

        public Show3DTimerTask(DialogAcceptSequenceTextual parent) {
            this.parent = parent;
            img = 0;
        }

        public void run() {
            addText("Initiating 3D Arena...");

            // delete old passwords.
            try {
                new File("d:\\temp\\sequence.txt").delete();
            }catch(Exception e) {
                addText("Exception Deleting Old Sequence!");
                System.out.println("Exception: " + e);
            }
            try {
                new File("c:\\temp\\password.txt").delete();
            }catch(Exception e) {
                addText("Exception Deleting Old Sequence!");
                System.out.println("Exception: " + e);
            }
            try { Thread.sleep(500); } catch(Exception e ) { ; }

            addText("Building 3D Visualization...");
            try {
                Desktop.getDesktop().open(new File(System.getProperty("user.dir") + "\\AcceptSequenceTextual\\application.windows32\\AcceptSequenceTextual.exe"));
            }catch(Exception e) {
                addText("ERROR VISUALISING 3D ARENA!");
                System.out.println("Exception: " + e);
            }

            while(parent.running) {
                img++;
                if(img==6) {
                    img = 0;
                }
                parent.jLabelStatus.setIcon(new ImageIcon(System.getProperty("user.dir") + "\\src\\zImgPack\\" + img + ".png"));

                File f = new File("d:\\temp\\sequence.txt");
                if(f.exists()) {
                    addText("Reading Sequence");
                    String s = "";
                    try {
                        BufferedReader br = new BufferedReader(new FileReader("d:\\temp\\sequence.txt"));
                        String temp = br.readLine();
                        while(temp!=null) {
                            int seq = Integer.parseInt(temp);
                            imgSequence.add(seq);
                            temp = br.readLine();
                        }
                        br.close();
                        addText("Done Reading Sequence!");
                        running = false;
                    }catch(Exception e) {
                        System.out.println("Exception: " + e);
                        addText("Error Reading Sequence!");
                        break;
                    }

                    try {
                        BufferedReader br = new BufferedReader(new FileReader("d:\\temp\\password.txt"));
                        textualPassword = br.readLine();
                        br.close();
                        addText("Done Reading Password!");
                        running = false;
                    }catch(Exception e) {
                        System.out.println("Exception: " + e);
                        addText("Error Reading Sequence!");
                        break;
                    }

                    // again - delete old passwords.
                    try {
                        new File("d:\\temp\\sequence.txt").delete();
                    }catch(Exception e) {
                        addText("Exception Deleting Old Sequence!");
                        System.out.println("Exception: " + e);
                    }
                    try {
                        new File("d:\\temp\\password.txt").delete();
                    }catch(Exception e) {
                        addText("Exception Deleting Old Sequence!");
                        System.out.println("Exception: " + e);
                    }                    

                }else {
                    addText("Waiting...");
                    try {
                        Thread.sleep(2000);
                    }catch(Exception e) {
                        ;
                    }
                }
            }
            
            addText("Done");
            parent.setVisible(false);
        }

        public void addText(String s) {
            parent.jTextOutput.setText(s + "\n" + parent.jTextOutput.getText());
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabelMain = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextOutput = new javax.swing.JTextArea();
        jLabelStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(51, 0, 0));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        new LoadForm();

        jLabelMain.setBackground(new java.awt.Color(153, 153, 153));
        jLabelMain.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelMain.setForeground(new java.awt.Color(255, 255, 255));
        jLabelMain.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelMain.setText("INTERFACE MODULE");
        jLabelMain.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButton2.setBackground(new java.awt.Color(51, 0, 0));
        jButton2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton2.setText("CANCEL");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jTextOutput.setBackground(new java.awt.Color(0, 0, 0));
        jTextOutput.setColumns(20);
        jTextOutput.setEditable(false);
        jTextOutput.setFont(new java.awt.Font("Arial", 0, 9)); // NOI18N
        jTextOutput.setForeground(new java.awt.Color(51, 255, 0));
        jTextOutput.setRows(5);
        jScrollPane1.setViewportView(jTextOutput);

        jLabelStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/zImgPack/0.png"))); // NOI18N
        jLabelStatus.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                    .add(jLabelStatus, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabelMain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                    .add(jButton2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabelMain, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jLabelStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 94, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton2)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
// TODO add your handling code here:
        
        running = false;
        setVisible(false);
        
    }//GEN-LAST:event_jButton2ActionPerformed

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabelMain;
    public javax.swing.JLabel jLabelStatus;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JTextArea jTextOutput;
    // End of variables declaration//GEN-END:variables
    
}
