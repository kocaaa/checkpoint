from PIL import Image
import PIL
import os
import glob
import io

def compress_image(image):

    im = Image.open(io.BytesIO(image))

    ratio = im.size[0]/1280

    if(im.size[0] > 1280):
        im = im.resize((int(im.size[0]/ratio),int(im.size[1]/ratio)), Image.ANTIALIAS)

    im.save("compressed_image.jpg", optimize=True, quality=30) 

    image = open("compressed_image.jpg", "rb")
    file = image.read()
    bytes = bytearray(file)

    return bytes