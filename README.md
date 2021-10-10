# Disk Space Pie chart

This is a graphical Java Swing application that allows you to 
quickly review disk usage per directory and locate the biggest 
disk space hogs.

Useful if you have large disks installed to your laptop/desktop but 
still see free disk space slowly disappearing. Sometimes you find 
something unexpected and unnecessary sitting there and taking large 
amounts of storage.

Simply clone the repository and start it with Maven
like this:

    mvn compile exec:java


It should be relatively intuitive to use: the start screen allows 
you to select the root directory to inspect. You can drill down to 
a specific directory by clicking on it in the pie chart. 

NOTE: This is just a quick hack I put together after getting fed up
with low level OS utilities: while user experience and code might not 
be the best, I thought it is worth sharing as being able to see a 
visual representation of disk usage as a pie chart makes a huge 
difference. 
