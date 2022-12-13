import cv2

def face_detect(image, nogui = False, cascasdepath = "haarcascade_frontalface_default.xml"):

    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

    face_cascade = cv2.CascadeClassifier(cascasdepath)

    faces = face_cascade.detectMultiScale(
        gray,
        scaleFactor = 1.2,
        minNeighbors = 5,
        minSize = (30,30),
        flags = cv2.CASCADE_SCALE_IMAGE
        )

    for (x,y,w,h) in faces:
        cv2.rectangle(image, (x,y), (x+h, y+h), (0, 255, 0), 2)

    if nogui:
        return len(faces)
    else:
        cv2.imshow("Faces found", image)
        cv2.waitKey(0)