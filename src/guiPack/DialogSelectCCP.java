
package guiPack;
import java.awt.*;
import JavaLib.*;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.Vector;
import javax.swing.ImageIcon;

/**
 *
 * @author  Ravee
 */
public class DialogSelectCCP extends javax.swing.JDialog {
    public boolean ok;
    
    BufferedImage biIn, biOut;
    Graphics2D graphics2D, graphics2D2;
    int ww, hh;
    int inPixels[][], outPixels[][];
    double gaussian[][];
    Vector<Point> points;
    boolean single;
    int shape;
    
    int startX1;
    int startY1;
    
    
    /** Creates new form MessageBox */
    public DialogSelectCCP(java.awt.Frame parent, int ww, int hh, int inPixels[][], int outPixels[][]) {
        super(parent, true);
        
        initComponents();
        
        Dimension sd  = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(sd.width / 2 - this.getWidth()/ 2, sd.height / 2 - this.getHeight()/ 2);
        
        this.ww = ww;
        this.hh = hh;
        this.inPixels = inPixels;
        this.outPixels = outPixels;
        
        points = new Vector<Point>();
        ok = false;
        
        biIn = new BufferedImage(ww, hh, BufferedImage.TYPE_INT_RGB);
        biOut = new BufferedImage(ww, hh, BufferedImage.TYPE_INT_RGB);
        graphics2D = biIn.createGraphics();
        graphics2D2 = biOut.createGraphics();
        
        shape = 0;
        gaussian = calculateGaussian(5, 0.01);
        
        jLabelImage.setIcon(new ImageIcon(biOut));
    }
    
    
    public void reset(String imageFileName, boolean single) {
        this.single = single;
        points.clear();
        ok = false;
        
        shape = 0;
        
        loadImage(imageFileName);
        repaintImage(true);        
    }

    void loadImage(String fname) {
        try
        {
            Image image = Toolkit.getDefaultToolkit().getImage(fname);
            MediaTracker mediaTracker = new MediaTracker(new Container());
            mediaTracker.addImage(image, 0);
            mediaTracker.waitForID(0);
            System.out.println("Input Image Read!");

            graphics2D.drawImage(image, 0, 0, ww, hh, null);
            
            for(int yy=0;yy<hh;yy++) {
                for(int xx=0;xx<ww;xx++) {
                    inPixels[yy][xx] = biIn.getRGB(xx, yy) & 0xffffff;
                    outPixels[yy][xx] = 0;
                }
            }
            jLabelImage.repaint();
       }catch(Exception e) {
            System.out.println("Exception : " + e);
       }
    }
    
    void repaintImage(boolean regenerate) {
        int col, r, g, b;
        int w = ww / 5;
        int h = hh / 5;
        
        if(regenerate) {
            // size of generated area
            Random rand = new Random();

            startX1 = rand.nextInt(ww-w);
            startY1 = rand.nextInt(hh-h);
            shape = shape ^ 1;
            
        }
            
        for(int y=0;y<hh;y++) {
            for(int x=0;x<ww;x++) {
                biOut.setRGB(x, y, biIn.getRGB(x, y) & 0xFFFFFF);
            }
        }

        if(!jToggleDisablePCCP.isSelected()) {
            int sumR, sumG, sumB, pix;
            for(int y=2;y<hh-2;y++) {
                for(int x=2;x<ww-2;x++) {

                    sumR = sumG = sumB = 0;
                    for(int yy=y-2;yy<=y+2;yy++) {
                        for(int xx=x-2;xx<=x+2;xx++) {
                            col = biIn.getRGB(xx, yy) & 0xFFFFFF;
                            b = col & 0xff;
                            g = (col >> 8) & 0xff;
                            r = (col >> 16) & 0xff;

                            r = g = b = (r+g+b)/3;

                            sumR += (r * gaussian[2+(yy-y)][2+(xx-x)]) ;
                            sumG += (g * gaussian[2+(yy-y)][2+(xx-x)]) ;
                            sumB += (b * gaussian[2+(yy-y)][2+(xx-x)]) ;
                        }
                    }
                    // average of 24 surrounding pixels + center
                    r = Math.min((int)sumR,255);
                    g = Math.min((int)sumG,255);
                    b = Math.min((int)sumB,255);

                    r = (int)(r * 0.6);
                    g = (int)(g * 0.6);
                    b = (int)(b * 0.6);

                    if(shape==0) {
                        if(x>=startX1 && x<=(startX1+w) && y>=startY1 && y<=(startY1+h)) {
                            biOut.setRGB(x, y, biIn.getRGB(x, y));
                        }else {
                            pix = (b | (g<<8) | (r<<16));
                            biOut.setRGB(x, y, pix);
                        }
                    }else if(shape==1) {
                        int cx = startX1 + (w/2);
                        int cy = startY1 + (h/2);
                        int rad = (w + h) / 2;
                        int dist = (int)(Math.sqrt(Math.pow(cy-y, 2) + Math.pow(cx-x, 2)));

                        if(dist < rad) {
                            biOut.setRGB(x, y, biIn.getRGB(x, y));
                        }else {
                            pix = (b | (g<<8) | (r<<16));
                            biOut.setRGB(x, y, pix);
                        }
                    }
                }
            }
        }
        
        if(jToggleVisualizePoint.isSelected()) {
            if(points.size()>0) {
                for(Point p: points) {
                    graphics2D2.setColor(Color.GREEN);
                    graphics2D2.drawRect(p.x-10, p.y-10, 21, 21);
                    graphics2D2.setColor(Color.RED);
                    graphics2D2.drawRect(p.x-8, p.y-8, 17, 17);
                }
            }
        }
        
        jLabelImage.repaint();
    }
    
    
    double[][] calculateGaussian(int len, double weight) {
        double kernel[][] = new double[len][len];
        int radius = len / 2;
        double a = -2.0 * radius * radius / Math.log10(weight);
        double sum = 0.0;
        for (int y = 0; y < len; y++) {
            for (int x = 0; x < len; x++) {
                double dist = Math.sqrt((x - radius) * (x - radius) + (y - radius) * (y - radius));
                kernel[y][x] = Math.exp(-dist * dist / a);
                sum += kernel[y][x];
            }
        }
        //for Kernel-Sum of 1.0
        for (int y = 0; y < len; y++)
        {
            for (int x = 0; x < len; x++)
            {
                kernel[y][x] /= sum;
            }
        }        
        return kernel;
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
        jLabelImage = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jToggleVisualizePoint = new javax.swing.JToggleButton();
        jToggleDisablePCCP = new javax.swing.JToggleButton();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(102, 102, 102));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabelMain.setBackground(new java.awt.Color(102, 102, 102));
        jLabelMain.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelMain.setForeground(new java.awt.Color(255, 255, 255));
        jLabelMain.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelMain.setText("CCP SELECTION");
        jLabelMain.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabelImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagePack/Back400x600.PNG"))); // NOI18N
        jLabelImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelImageMouseClicked(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton2.setText("CANCEL");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jToggleVisualizePoint.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jToggleVisualizePoint.setSelected(true);
        jToggleVisualizePoint.setText("VISUALIZE POINT");
        jToggleVisualizePoint.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jToggleVisualizePointItemStateChanged(evt);
            }
        });

        jToggleDisablePCCP.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jToggleDisablePCCP.setText("Persuasion OFF");
        jToggleDisablePCCP.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jToggleDisablePCCPItemStateChanged(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton3.setText("Re GENERATE");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jButton2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jButton1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jToggleVisualizePoint, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jToggleDisablePCCP, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jButton3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jToggleVisualizePoint, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 47, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jToggleDisablePCCP, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 47, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jButton3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 43, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jButton1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 61, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jButton2)
                .addContainerGap())
        );

        jPanel2Layout.linkSize(new java.awt.Component[] {jButton3, jToggleDisablePCCP, jToggleVisualizePoint}, org.jdesktop.layout.GroupLayout.VERTICAL);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabelMain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jLabelImage)
                        .add(18, 18, 18)
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabelMain)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jLabelImage, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        new LoadForm();

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
        ok = false;
        setVisible(false);
        
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
// TODO add your handling code here:
        
        if(points.size()>0) {
            ok = true;
        }
        setVisible(false);
        
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        repaintImage(true);
        
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jToggleVisualizePointItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jToggleVisualizePointItemStateChanged
        // TODO add your handling code here:
        repaintImage(false);
        
    }//GEN-LAST:event_jToggleVisualizePointItemStateChanged

    private void jToggleDisablePCCPItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jToggleDisablePCCPItemStateChanged
        // TODO add your handling code here:
        repaintImage(false);
        
    }//GEN-LAST:event_jToggleDisablePCCPItemStateChanged

    private void jLabelImageMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelImageMouseClicked
        // TODO add your handling code here:
        Point p = new Point(evt.getX(), evt.getY());
        if(single) {
            points.clear();
            points.add(p);
        }
        
        repaintImage(false);
        
            
    }//GEN-LAST:event_jLabelImageMouseClicked

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabelImage;
    private javax.swing.JLabel jLabelMain;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JToggleButton jToggleDisablePCCP;
    private javax.swing.JToggleButton jToggleVisualizePoint;
    // End of variables declaration//GEN-END:variables
    
}
