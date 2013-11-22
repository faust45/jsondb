(ns jsondb.imgio
  (:import
    [java.io File]
    [javax.imageio ImageIO]
    [java.awt.image BufferedImage])
  (:import [org.imgscalr Scalr Scalr$Method Scalr$Mode]))


(def automatic
  Scalr$Method/AUTOMATIC)

(def speed
  Scalr$Method/SPEED)

(def balanced
  Scalr$Method/BALANCED)

(def quality
  Scalr$Method/QUALITY)

(def ultra-quality
  Scalr$Method/ULTRA_QUALITY)

(defn dimensions 
  [image]
  [(.getWidth image) (.getHeight image)])

(defn buffered-image 
  [image]
  (if (instance? BufferedImage image)
    image
    (ImageIO/read image)))

(defn as-file 
  [ext [label buffered-image]]
  (let [[width height] (dimensions buffered-image)
        out (File/createTempFile (str width "x" height "_") (str "." ext))]
    (ImageIO/write buffered-image ext out)
    {:label label :file out}))

(defn as-stream
  [buffered-image ext]
  (let [baos (java.io.ByteArrayOutputStream.)]
    (ImageIO/write buffered-image ext baos)
    (java.io.ByteArrayInputStream. (.toByteArray baos))))

(defn resize
  [file [label [width height]]]
  (Scalr/resize (buffered-image file) automatic width height nil))


