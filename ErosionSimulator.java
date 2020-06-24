import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class ErosionSimulator extends JPanel
{
    private static final long serialVersionUID = 1L;
    private static int detailLevel = 25;  // Percent of x-axis size to make into terrain points, higher gives more detail
    private static int numPoints = 0;
    private static int[] xPoints;   // Array of x coordinates, one for each height
    private static int[] yPoints;   // Array of heights making up the terrain
    private static boolean newTerrain = true;   // If true, create a completely new terrain on next frame update
    
    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Erosion Simulator");
        frame.setSize(800,800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(new ErosionSimulator()); // Add simulator to frame
        createUI(frame);    // Add buttons and input fields
        frame.setVisible(true);
    }

    public void generateTerrain()
    {
        int xSize = getWidth(); // Create parallel arrays for coordinates of terrain polygon points
        int ySize = getHeight();
        numPoints = (int)(xSize * detailLevel / 100.0 + 3); // Plus 3 to give space to complete the polygon as a solid shape
        xPoints = new int[numPoints]; 
        yPoints = new int[numPoints];

        for(int i = 0; i < xPoints.length-2; i++)   // Create random points
        {
            xPoints[i] = i * (100 / detailLevel);   // Evenly space x points
            yPoints[i] = (int)(ySize * Math.random());  // Pick random point in entire height range
        }
        
        xPoints[numPoints-3] = xSize;  // Force terrain to reach right edge of frame
        xPoints[numPoints-2] = xSize;  // Bottom right corner of frame to make terrain polygon filled in
        yPoints[numPoints-2] = ySize;
        xPoints[numPoints-1] = 0;      // Botton left corner of frame
        yPoints[numPoints-1] = ySize;
    }

    public static void smooth()
    {
        for(int i = 1; i < xPoints.length-3; i++)   // Set the value of a point to the average of it and its neighbors
        {
            if(i == 1 || i == xPoints.length - 3)
                yPoints[i] = (yPoints[i-1] + yPoints[i] + yPoints[i+1]) / 3;
            else
                yPoints[i] = (yPoints[i-2] + yPoints[i-1] + yPoints[i] + yPoints[i+1] + yPoints[i+2]) / 5;
        }
    }

    public static void erode(int cycles)
    {
        int initialRemove = 6;  // Amount to remove from where the rain drop lands
        int leaveBehind = 1;    // Amount to leave behind with each point the drop moves to
        Random rn = new Random();
        
        for(int c = 0; c < cycles; c++) // Run for number of cycles specified
        {
            int rNum = rn.nextInt(numPoints-2); // Random position to start erosion cycle at
            yPoints[rNum] += initialRemove; // Remove a set amount of dirt from starting position
            int dirtHeld = initialRemove;   // Track how much dirt the drop has left
            
            while(dirtHeld > 0)
            {
                if((rNum == 0 && yPoints[0] >= yPoints[1]) || (rNum == numPoints-3 && yPoints[numPoints-3] >= yPoints[numPoints-4]))    // Case where left and right most points are low spots
                {
                    yPoints[rNum]-= dirtHeld;
                    dirtHeld = 0;
                }
                else if(rNum != 0 && rNum != numPoints-3 && yPoints[rNum] >= yPoints[rNum-1] && yPoints[rNum] >= yPoints[rNum+1])   // Case for being the low point
                {
                    yPoints[rNum]-= dirtHeld;
                    dirtHeld = 0;
                }
                else if((rNum == numPoints-3 && yPoints[numPoints-3] < yPoints[numPoints-4]) || (rNum != 0 && yPoints[rNum-1] >= yPoints[rNum+1]))  // Case for left side being lower
                {
                    rNum--;
                    yPoints[rNum] -= leaveBehind;
                    dirtHeld -= leaveBehind;
                }
                else if((rNum == 0 && yPoints[0] < yPoints[1]) || (rNum != numPoints-3 && yPoints[rNum-1] < yPoints[rNum+1]))   // Case for right side being lower
                {
                    rNum++;
                    yPoints[rNum] -= leaveBehind;
                    dirtHeld -= leaveBehind;
                }
            }
        }
    }

    public void paintComponent(Graphics g)
    {
        if(newTerrain == true)  // Only generate new terrain on first run and when button is pressed
        {
            generateTerrain();
            newTerrain = false;
        }
        g.fillPolygon(xPoints, yPoints, xPoints.length);
    }

    public static void createUI(JFrame frame)   // Add JPanel with user interface to JFrame
    {
        JTextField detailLevelText = new JTextField("25", 3);
        JButton newTerrainButton = new JButton("New Terrain");   // Button to create a new terrain map
        newTerrainButton.addActionListener(e -> 
        {
            newTerrain = true; 
            detailLevel = Integer.parseInt(detailLevelText.getText());
            frame.repaint();
        });
        
        JButton smoothButton = new JButton("Smooth");   // Button to run smooth function
        smoothButton.addActionListener(e -> 
        {
            smooth(); 
            frame.repaint();
        });
      
        JTextField erosionText = new JTextField("1000",4);    // User chooses number of cycles for the erode function to run
        JButton erodeButton = new JButton("Erode");   // Button to run erode function
        erodeButton.addActionListener(e -> 
        {
            erode(Integer.parseInt(erosionText.getText())); 
            frame.repaint();
        });
      
        JPanel buttonPanel = new JPanel();  // Container for UI
        buttonPanel.add(newTerrainButton);
        buttonPanel.add(smoothButton);
        buttonPanel.add(erodeButton);
        buttonPanel.add(new JLabel("Erosion Cycles"));  
        buttonPanel.add(erosionText);
        buttonPanel.add(new JLabel("Detail Level"));
        buttonPanel.add(detailLevelText);

        frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }
}