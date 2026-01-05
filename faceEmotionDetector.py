# installations done in terminal window for project
# installed opencv to access camera in real time
# installed deepface this is the ai repsoncible for analysing the face and thus the emotion
# :)

import cv2
from deepface import DeepFace


# chatgpt lines needed for project to work referr to gpt to undertsand very hard

face_cascade_path = cv2.data.haarcascades + 'haarcascade_frontalface_default.xml'
face_cascade = cv2.CascadeClassifier()
if not face_cascade.load(face_cascade_path):
    print("!!! Failed to load face cascade.")
    exit()


myWebcam = cv2.VideoCapture(0)


if not myWebcam.isOpened():
    print("Error: Could not open webcam.")
    exit()

print("Welcome to the face emotion detector!")
print("Your camera is opening")
print("If you would like to quit please enter x  in the camera window")




# --- Main Loop ---
while True:

    # framTaken is called frame since the libray cv2 reads take 100 frames or more per second and siaplysa this hence not a live feed
    # .read() returns the fram alongside if the frame was taken was succesful or not hence taking two values
    isFrameSuccesful, frameTaken = myWebcam.read()
    if not isFrameSuccesful:
        break

    # inverting live feed for a narutal view/ my preference
    frameTaken = cv2.flip(frameTaken, 1)

    #adding some text onto camera window to show user what to do to obtain emotion
    # function works like cv2.putText then on what , where , font , scale , colour,thickness
    # okay for some odd reason the colour convention rather than being RGB is BGR
    (cv2.putText
    (
        frameTaken,
        "Please press x to end camera",
        (30, 50),
        cv2.FONT_HERSHEY_SIMPLEX,
        1,
        (208,224,64),
        2
    ))
    (cv2.putText
        (
        frameTaken,
        "Press SPACE to find your emotion",
        (30, 90),  # <-- Note the different 'y' (90)
        cv2.FONT_HERSHEY_SIMPLEX,
        1,
        (208, 224, 64),  # Your turquoise color
        2
    ))


    # displaying camera in real time/ frames update so quick u think its real time
    cv2.imshow("Camera Feed", frameTaken)

    # the libray of cv2 only allows the window we display to accpet one key inputs hence we use one letter here
    key = cv2.waitKey(1) & 0xFF

    if key == ord('x'):
        break

    if key == ord(' '):
        print("Your face will be analysed right now : )")

        #so here i am using the face_cascade to save time since the ai will take a very long time to analyse a full image
        # thus i am using the face cxascade tool to obtain the cords of the face in the frame and sending that the deepface
        #ai therforie it will be much muich faster than sending the whle frame , before using deepface only took 30 sec for one analysis

        # obtaining grayscale to find the cords of the face to feed to the ai
        frameToObtainFaceCords = cv2.cvtColor(frameTaken, cv2.COLOR_BGR2GRAY)
        # finding the cords of the face
        face = face_cascade.detectMultiScale(frameToObtainFaceCords, 1.1, 5)

        # making sure that there was at leats one face in the picture
        if len(face) > 0:
            #getting cords of the face
            (x, y, w, h) = face[0]  # Get the first face

            # cutting face from the image
            faceOnly = frameTaken[y:y + h, x:x + w]

            try:
                #now we send face only to DeepFace ai
                analysis = (DeepFace.analyze()
                    (
                    faceOnly,
                    actions=['emotion'],
                    enforce_detection=False,
                    silent=True
                ))

                dominant_emotion = analysis[0]['dominant_emotion']
                print(f" You have a : {dominant_emotion} face  ")



            except Exception as e:
                #catch the excpetion and dsiplay it
                print(f" FAIL the error is : {e}")
        else:
            print("You can not fool me there is no face")

# when exit the while with x this occurs
print("Goodbye")
myWebcam.release()
cv2.destroyAllWindows()

