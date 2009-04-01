(in-ns 'clj-android)

(defn- capitalize-string [#^String t]
  (str (.toUpperCase (.substring t 0 1)) (.substring t 1)))

(defmacro log [message]
  "Send a debug log message."
  `(android.util.Log/d ~(str (ns-name *ns*)) ~message))

(defmacro start-tracing [name]
  "Start method tracing."
  `(android.os.Debug/startMethodTracing ~(ns-name *ns*)))

(defmacro stop-tracing []
  "Stop method tracing."
  `(android.os.Debug/stopMethodTracing))

(defmacro log-time [tag form]
  "Log amount of time taken to evalute the given form."
  `(let [before# (System/nanoTime)
         result# ~form
         amount# (- (System/nanoTime) before#)]
     (log (str ~tag " took " (cond (> amount# 1000000000)
                                   (format "%.3fs" (float (/ amount# 1000000000)))
                                   (> amount# 1000000)
                                   (format "%dms" (int (/ amount# 1000000)))
                                   :else
                                   (str amount# "%dns"))))
     result#))
