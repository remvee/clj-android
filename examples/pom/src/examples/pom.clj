(ns examples.pom
  (:use clj-android pom))

(defactivity Main
  (:resume (content-view [TextView {:text (str (Math/round (phase-of-the-moon)) "% of full")}])))
