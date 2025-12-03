# Color Checker

This application implements OpenCV to create a lightweight live object detection application. It is
configured to work against a color checker but could easily be used to check against any object by 
simply changing the reference image

## Project Set up

All that should be required to compile the application is the android SDK with SDK 35 downloaded.
Using the latest android studio you should simply need to open the SDK Manager and make sure Android
15 is installed. 

Because of the OpenCV integration we will also need access to CMake. This is done 
by adding `cmake.dir=/Applications/CMake.app/Contents` (or the path to your install) to the projects
root local.properties file 

## Main Components

### Object Detection
Object detection is the main responsibility of this application. I chose to use ORB for object 
detection because it seemed the best fit for matching where reference images were of different 
sizes. I have no prior OpenCV experience so there may be more appropriate algorithms. 

To make any future updates simpler I wrapped the Object detection in a simple interface that takes 
a given `Mat` and returns a `MatchResult`. This sealed return type was preferred over a boolean 
in order to return additional data bout the match to facilitate overlay drawing or other tasks. The
`Success` case has `ObjectDetection` instances, which are a pair of keypoint and mat for the object 
and the scene.

This structure was chosen as these are the main data structures used in ORB detection. It is likely 
this could change for different object matching algos. If this is the case we could update 
`MatchResult.Match` to return a generic type of data.

### Mat Decoration
In order to give a useful UI to the user we can draw a bounding box around the object when detected.
This is simply a case of finding the corners of the matching object using `Imgproc.line` to draw
a set of lines between the points of the bounding box.

Drawing over  a scene seems like a common function that could easily be stacked. So the Mat 
decoration is put behind a MatDecorator interface to allow multiple implementations. The 
implementation ObjectDetectionMatDecorator draws a box, but we could easily draw other UI elements 
based on different requirements sor feedback data. 

This feeds into the previous comment of the MatchResult.Match data potentially being generic. I 
could envisage a situation where an ObjectMatcher returned a percentage confidence value and we 
implemented a MatDecorator to draw a percentage Bar - although this might be better handled in the
android UI rather than Mat drawing tools.

### CameraViewManager

The `CameraBridgeViewBase` class is useful for returning a stream of Mat objects via callbacks. It
also need to be enabled / disabled on lifecycle changes. Rather than pollute the activity with this
we can encapsulate in the `CameraViewManager` and receive lifecycle events via implementation of
`DefaultLifecycleObserver` 

This the gives us the opportunity to provide a `MatDecorator` implementation to draw on the camera 
feed. In this case we pass in the `ObjectDectionMatDecorator`. This could easily be updated to take
and `Iterable<MatDecorator>` to stack decorations sequentially.

## Quality
Without access to a real ColorChecker testing effectiveness of the application is difficult. I 
confirmed behaviour against the pass/fail reference images provided using a instrumentation test. 
This did require some tweaking of the `ORBObjectDetector.Config` from the default. When this config 
was used in the application it did not manage to draw any bounding boxes. However this could be 
because when doing a physical test I could only point the phones camera an a flat image of a color 
checker on another screen.

Because of this I opted to leave the config in the application at the looser default in order to 
demonstrate the drawing implementation. This does result in some slightly weird bounding boxes. 
Hopefully when used on a real object the stricter config would work better - I am unable to test
this currently.

### Potential Improvements
Deeper investigation is needed into OpenCV to fully understand the ORBDetection algorithm in order 
to tweak it. More reading on Lowes ratio would be beneficial as well as a generally improved mental
model of the OpenCV APIs.

One obvious Bug with the overlay drawing is that it will often draw boxes that are not boxes. We 
could easily enforce this with some require calls in Quadrilateral to assert tha provided points
actually create a quadrilateral, and when they do not we reject the match.

Speed on my device seems fine but testing across a range of devices would be useful particularly 
with varying aspect ratios of camera inputs.

The current object detection is trained against a single reference image. It seems feasible to
use a range of images. A naive implementation could involve multiple detectors trained on a single
reference each, run concurrently with the closest match taking precedence or some average taken. 
The use of the `ObjectDetector` interface would support this via a 
`AverageObjectDetector(detectors:Set<ObjectDetector>)` or 
`ClosesObjectDetector(detectors:Set<ObjectDetector>)` 






