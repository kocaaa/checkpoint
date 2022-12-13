import fastapi
import uvicorn
import numpy
import cv2

from fastapi import File

import face_detection as fd
import image_compression as compression

host = "127.0.0.1"
# host = "147.91.204.115"
port = 10046

app = fastapi.FastAPI()

@app.post("/count_faces")
def countFaces(file: bytes = File(...)):
    nparr = numpy.fromstring(file, numpy.uint8)
    img_np = cv2.imdecode(nparr, cv2.IMREAD_COLOR)

    number_of_faces = fd.face_detect(img_np, True)

    return number_of_faces

@app.post("/compress")
def compressImage(file: bytes = File(...)):
    compressed_bytes = compression.compress_image(file)
    
    return compressed_bytes

if __name__ == "__main__":
  uvicorn.run(app, host=host, port=port)