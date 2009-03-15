(ns examples.calc
  (:use clj-android)
  (:import (android.widget ArrayAdapter)))

(defmacro get-text []
  `(str (.getText ~'edit-view)))
(defmacro set-text [value]
  `(.setText ~'edit-view ~value))
(defmacro set-list [lst]
  `(.setAdapter ~'list-view
                (ArrayAdapter.
                 ~'context
                 android.R$layout/simple_list_item_1
                 (into-array (map str (rseq ~lst))))))

(def calc-stack (ref []))

(defn calc-push [value]
  (dosync (commute calc-stack conj value)))

(defn calc-pop []
  (dosync (let [top (peek @calc-stack)]
            (commute calc-stack pop)
            top)))

(def calc-opers {\+ #'+
                 \- #'-
                 \/ #'/
                 \* #'*})

(def calc-allowed-chars (.toCharArray "0123456789."))
(def calc-allowed-key-codes (key-event-key-code :back :del))

(defmacro calc-do [& body]
  `(do
     (when-not (= (get-text) "")
       (calc-push (Double/parseDouble (get-text))))
     ~@body
     (set-text "")
     (set-list @calc-stack)
     true))

(defn include? [list value] (some #(= value %) list))
(defn swap [a b] (list b a))

(defactivity Main
  (:create (let [list-view (view [ListView {}])
                 edit-view (view [EditText {}])]
             (set-list @calc-stack)
             (on-key edit-view
                     (if (= (.getAction event) (key-event-action :down))
                       (let [char (char (.getUnicodeChar event))]
                         (cond
                          (include? (key-event-key-code :enter :space) key-code)
                          (calc-do)

                          (and (= key-code (key-event-key-code :del))
                               (= (get-text) "")
                               (> (count @calc-stack) 0))
                          (calc-do (calc-pop))
                            
                          (include? (keys calc-opers) char)
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
  