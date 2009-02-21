(in-ns 'clj-android)

(defn- capitalize-string [#^String t]
  (str (.toUpperCase (.substring t 0 1)) (.substring t 1)))

(defmacro log [message]
  "Send a debug log message."
  `(android.util.Log/d ~(str (ns-name *ns*) ~message)))

(defmacro start-tracing [name]
  "Start method tracing."
  `(android.os.Debug/startMethodTracing ~(ns-name *ns*)))

(defmacro stop-tracing []
  "Stop method tracing."
  `(android.os.Debug/stopMethodTracing))
