from math import sqrt
import xml.dom.minidom
from java.awt import Color, FileDialog
from ij.gui import Plot
from java.util import ArrayList
from ij import IJ
import os

#@ Integer(label='Starting track', value=2) start_track
#@ Integer(label='How many tracks', value=8) number_of_tracks
#@ File(label="Select a file") myFile
#start_track = 8
#number_of_tracks=6
# Open file and parse it as XML.

#fd = FileDialog(IJ.getInstance(), "Open xml file from TrackMate", FileDialog.LOAD)
#fd.show()
print(myFile)
myFile = str(myFile)
dom = xml.dom.minidom.parse(myFile)
def calculate_mean_squared_displacement(x, y, t):
    """
    Calculate the mean squared displacement for 10 different time intervals.
    :param x: List of x-coordinates.
    :param y: List of y-coordinates.
    :param t: List of times.
    :return: Tuple of (list of time intervals, list of mean squared displacements).
    """
    n = len(x)
    max_time_gap = int(max(t) / 5)
    time_gaps = range(1, max_time_gap+1)

    msd_list = []
    for delta_t in time_gaps:
        # Calculate the squared displacement for all possible pairs of time points
        squared_displacements = []
        for i in range(n):
            for j in range(i+1, n):
                if abs(t[i] - t[j]) == delta_t:
                    squared_displacement = (x[j] - x[i])**2 + (y[j] - y[i])**2
                    squared_displacements.append(squared_displacement)

        # Calculate the mean squared displacement
        if len(squared_displacements):
            msd = sum(squared_displacements) / len(squared_displacements)

        else:
            msd = 0
        msd_list.append(msd)

    return (time_gaps, msd_list)



# Get all particles. But we keep only the first one.
particles = dom.getElementsByTagName('particle')


# Plot MSD using IJ graphs.
plot = Plot("MSD", "delta T", "displacement (um)")
#plot.setColor(Color.RED)
#plot.addPoints(time_gap_array, msd_array, Plot.LINE)
#plot.show()

colors = [Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.CYAN, Color.MAGENTA, Color.YELLOW, Color.PINK, Color.GRAY, Color.BLACK]
color = 0
legends = ""
print("Processing " +str(number_of_tracks)+" tracks")
for i in range(start_track, start_track+number_of_tracks):

    daphnia = particles[i]

    # Extract the Y coordinate over time of this daphnia.
    t = [];
    y = [];
    x = [];
    spots = daphnia.getElementsByTagName('detection')
    for s in spots:
        t.append( int( s.getAttribute('t') ) )
        y.append( float( s.getAttribute('y') ) )
        x.append( float( s.getAttribute('x') ) )

    # Convert the data to Java ArrayLists
    time_gap_array = ArrayList()
    msd_array = ArrayList()
#    for i in range(len(time_gap)):
#        time_gap_array.add(float(time_gap[i]))
#        msd_array.add(float(msd_list[i]))
    print(color)
    times, squared_displacements = calculate_mean_squared_displacement(x, y, t)
    print("Finished one track!")
    plot.setColor(colors[color])
    plot.addPoints(times, squared_displacements, Plot.LINE)
    color=color+1
    legends= legends + "Particle Track = "+str(i)+"\n"

plot.addLegend(legends)
plot.setLimitsToFit(True)
plot.show()
