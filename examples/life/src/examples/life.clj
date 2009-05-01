;; Based on:
;;   Contributed by Larry Sherrill with a lot of help from forum members.
;;   See http://en.wikipedia.org/wiki/Conway's_Game_of_Life

(ns examples.life
  (:use clj-android))

(def cells (ref {}))

(def running (ref false))

(defn determine-initial-state [x y]
  (= 0 (rand-int 5)))

(defn determine-new-state [x y]
  (let [neighbor-count
        (count (for [dx [-1 0 1] dy [-1 0 1]
                     :when (and (not (= 0 dx dy))
                                (cells [(+ x dx) (+ y dy)]))]
                 :alive))]
    (if (cells [x y])
      (< 1 neighbor-count 4)
      (= neighbor-count 3))))

(defn calc-state [cell-state]
  (dosync
   (ref-set cells
            (reduce conj {}
                    (for [x (range 32) y (range 43)]
                      [[x y] (cell-state x y)])))))

(gen-class
 :name life.View
 :extends android.view.View)

(defn -onDraw [view canvas]
  (let [red (doto (android.graphics.Paint.) (. setColor android.graphics.Color/GREEN))]
    (doseq [[[x y] state] (filter last @cells)]
      (let [[x y] [(* 10 x) (* 10 y)]]
        (doto canvas
          (. drawRect x y (+ 10 x) (+ 10 y) red))))))

(defactivity Main
  (:create 
   (calc-state determine-initial-state)
   (let [life-view (life.View. context)]
     (content-view life-view)
     (. (Thread.
         #(loop []
            (when @running
              (log-time "calc-state" (calc-state determine-new-state))
              (ui (.invalidate life-view)))
            (Thread/sleep 100)
            (recur)))
        start)))
  (:resume
   (dosync (ref-set running true)))
  (:pause
   (dosync (ref-set running false))))
