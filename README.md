# Erosion Simulator

Creates terrain by simulating erosion on random height maps. Erosion is done by simulating a raindrop that picks up dirt at a certain point and drops the dirt off along its path to a low point.

## Usage

**New Terrain:** Generate a new height map.

**Smooth:** Averages neighboring points to make a smoother terrain.

**Erode:** Run erosion function on current map for set amount of cycles.

**Erosion Cycles:** Amount of times to run the erosion function each time erode button is pressed.

**Detail Level:** Percent of x-axis to be used as points in map. 25% = every 4 pixels is a data point.