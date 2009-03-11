(ns examples.calc
  (:use clj-android)
  (:import (android.widget ArrayAdapter)))

(def calc-stack (ref []))

(defn calc-push [value]
  (dosync (commute calc-stack conj value)))

(defn calc-pop []
  (dosync (let [top (peek @calc-stack)]
            (commute calc-stack pop)
            top)))

(defn calc-list-adapter [context]
  (new ArrayAdapter context android.R$layout/simple_list_item_1 (into-array (map #(str %) (rseq @calc-stack)))))

(defmacro calc-do [& body]
  `(do
     (if (not= 0 (.length (.getText ~'edit-view)))
       (calc-push (Double/parseDouble (str (.getText ~'edit-view)))))
     ~@body
     (.setText ~'edit-view "")
     (.setAdapter ~'list-view (calc-list-adapter ~'context))
     true))

(def calc-opers {\+ #'+
                 \- #'-
                 \/ #'/
                 \* #'*})

(def calc-allowed-chars (.toCharArray "0123456789."))
(def calc-allowed-key-codes (key-event-key-code :back :del))

(defn include? [list value] (some #(= value %) list))
(defn swap [a b] (list b a))

(defactivity Main
  (:create (let [list-view (view [ListView {:adapter (calc-list-adapter context)}])
                 edit-view (view [EditText {}])]
             (on-key edit-view
                     (if (= (.getAction event) (key-event-action :down))
                       (let [char (char (.getUnicodeChar event))]
                         (cond
                          (include? (key-event-key-code :enter :space) key-code)
                          (calc-do)

                          (and (= key-code (key-event-key-code :del)) (= 0 (.length (.getText edit-view))))
                          (calc-do (calc-pop))
                            
                          (and (include? (keys calc-opers) char))
                          (calc-do
                            (if (> (count @calc-stack) 1)
                              (calc-push (apply (get calc-opers char) (swap (calc-pop) (calc-pop))))))
                                                        
                          true
                          (not (or (include? calc-allowed-chars char)
                                   (include? calc-allowed-key-codes key-code)))))))
             (content-view
              [LinearLayout {:layoutParams (layout-params :fill)}
               edit-view
               list-view]))))
  