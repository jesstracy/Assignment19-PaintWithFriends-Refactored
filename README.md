Painting With Friends
=====================

A JavaFX application that allows a user to draw on his/her own screen, connect to a friend and send that friend his/her strokes (effectively "drawing" on the friend's screen), and switch turns to receive what the friend is drawing.

Objective
---------

Create an app that allows the following:

Using a keyboard shortcut ("d"), allow the user to toggle whether or not moving the mouse draws on the screen.
Using the up and down arrow, allow the user to change the size of the stroke used to draw.
Allow the user to enter the IP address of a "friend" and to connect to that friend.
If user A is connected to their friend, user B, then whatever user A is drawing on their screen should also draw on user B's screen.
Note: your program will be running on both user's computers.

Other controls:
* "c" changes the color of your stroke
* Up and down arrows change the width of your stroke
* Press the "replay" to replay your drawing

Extra 1:

User B should be able to request "control" of the drawing
When user B has control of the drawing, user A sees what user B is drawing
Note: when a user has control of the drawing, the other user's mouse movements do not draw on either screen

Extra 2:

Allow each user (user A or user B) to "save" the drawing
For this exercise, you will save the drawing as a list of "strokes" that can be redrawn at any time ...

Extra 3:

Animate the redrawing so that it doesn't just "appear" almost instantaneously (accomplished this by using the DelayedTask class since we can't manipulate the main thread that the UI works on)

Instructions for running the app
--------------------------------

On two computers:

Enter the IP address of the other user in line 154 of Main.java. Then run Main.java from both computers and request to connect to the other person's IP address. You can then send the user your strokes, and they can request to send their strokes back to you!

On a single computer:

Draw by yourself by running Main.java. Or, see the app's multiplayer functionality by running two instances of Main.java. On the second screen, select "localhost" and click connect. You will now be able to see your strokes be drawn on the first screen as well! (On a single computer, however, you cannot switch turns since the localhost address is already in use.)