(ns examples.pom
  (:use clj-android pom)
  (:import java.util.Calendar))

(defactivity Main
  (:resume (let [today (phase-of-the-moon)
                 tomorrow (phase-of-the-moon
                           (doto (Calendar/getInstance) (.add Calendar/HOUR 24)))
                 message (str
                          "The Moon is "
                          (cond (= 100 (Math/round today)) "Full"
                                (= 0  (Math/round today)) "New"
                                (= 50  (Math/round today))
                                (if (> tomorrow today) "at the First Quarter" "at the Last Quarter")
                                :else (str
                                       (if (> tomorrow today) "Waxing" "Waning") " "
                                       (if (> today 50) "Gibbous" "Crescent ")
                                       (format " (%1.0f%% of Full)" today))))]
             (content-view [TextView {:text message}]))))
