(in-ns 'clj-android)

(defmacro key-event-key-code
  "Convenience macro to get to KeyEvent keycode constant.  Example: (= (key-event-key-code :del) (android.view.KeyEvent/KEYCODE_DEL))"
  ([code] `(. android.view.KeyEvent ~(symbol (str "KEYCODE_" (.toUpperCase (name code))))))
  ([first & rest] `(list ~@(map #(list '. 'android.view.KeyEvent (symbol (str "KEYCODE_" (.toUpperCase (name %))))) (cons first rest)))))

(defmacro key-event-action
  "Convenience macro to get to KeyEvent action constant.  Example: (= (key-event-action :down) (android.view.KeyEvent/ACTION_DOWN))"
  ([code] `(. android.view.KeyEvent ~(symbol (str "ACTION_" (.toUpperCase (name code))))))
  ([first & rest] `(list ~@(map #(list '. 'android.view.KeyEvent (symbol (str "ACTION_" (.toUpperCase (name %))))) (cons first rest)))))

(defmacro on-key [view handler]
  "Attach a key listener to a view.  The handler form is provided 'view as a reference to the calling view."
  `(.setOnKeyListener ~view (proxy [android.view.View$OnKeyListener] []
                              (onKey [~'view ~'key-code ~'event] (boolean ~handler)))))

(defmacro on-click [view handler]
  "Attach a click listener to a view.  The handler form is provided 'view as a reference to the calling view."
  `(.setOnClickListener ~view (proxy [android.view.View$OnClickListener] []
                                (onClick [~'view] (boolean ~handler)))))
