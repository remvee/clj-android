(in-ns 'clj-android)

(defmacro ui [body]
  "Run the given body form in the UI thread.  This method expects 'context to reference the current activity."
  `(.runOnUiThread ~'context (fn [] ~body)))

(defn- activity-name->class [name]
  (symbol (str (ns-name *ns*) "." name)))

(defmacro defactivity [name & options]
  "Define an activity class."
  (let [qname (activity-name->class name)
        prefix (gensym)
        get-option (fn [n] (rest (first (filter #(= n (first %)) options))))]
    `(do
       (gen-class
        :name ~qname
        :extends ~(or (first (get-option :extends)) 'android.app.Activity)
        :prefix ~prefix
        :exposes-methods {~'onCreate ~'superOnCreate
                          ~'onStart ~'superOnStart
                          ~'onRestart ~'superOnRestart
                          ~'onResume ~'superOnResume
                          ~'onPause ~'superOnPause
                          ~'onStop ~'superOnStop
                          ~'onDestroy ~'superOnDestroy})
       ~(if (get-option :create)
          `(defn ~(symbol (str prefix 'onCreate)) [~'context ~'savedInstanceState]
             (.superOnCreate ~'context ~'savedInstanceState)
             (do ~@(get-option :create))))
       ~@(map (fn [#^clojure.lang.Keyword n]
                `(defn ~(symbol (str prefix "on" (capitalize-string (.getName n)))) [~'context]
                   (~(symbol (str ".superOn" (capitalize-string (.getName n)))) ~'context)
                   (do ~@(get-option n))))
              (filter #(get-option %) '(:start :restart :resume :pause :stop :destroy))))))

(defmacro start-activity [name & context]
  "Start an activity as define by defactivity."
  (let [c (or context '(context))]
    `(.startActivity ~@c (new android.content.Intent ~@c ~(activity-name->class name)))))
