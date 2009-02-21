(in-ns 'clj-android)

(import '(android.view ViewGroup$LayoutParams))

(def #^{:private true} layout-params-map {:fill-parent ViewGroup$LayoutParams/FILL_PARENT
                                          :fill ViewGroup$LayoutParams/FILL_PARENT
                                          :wrap-content ViewGroup$LayoutParams/WRAP_CONTENT
                                          :wrap ViewGroup$LayoutParams/WRAP_CONTENT})

(defmacro #^{:private true} layout-params-translate [value]
  `(if (keyword? ~value) (layout-params-map ~value) ~value))

(defn layout-params
  "Construct layout params with keyword shortcuts; :fill and :wrap."
  ([both]
     (let [#^Integer both (layout-params-translate both)]
       (new ViewGroup$LayoutParams both both)))
  ([width height]
     (let [#^Integer width (layout-params-translate width)
           #^Integer height (layout-params-translate height)]
       (new ViewGroup$LayoutParams width height))))

(defmacro view-by-id [id]
  "Expand to .findViewById on context."
  `(.findViewById ~'context ~id))

(defn- view-builder [tree]
  (if (vector? tree)
    (let [class-name (symbol (str "android.widget." (first tree)))
          attributes (second tree)
          rest (drop 2 tree)
          view (gensym)]
      (concat
       (list 'let [view (list 'new class-name 'context)])
       (map #(list (symbol (str ".set" (capitalize-string (name %)))) view (attributes %)) (keys attributes))
       (map #(list '.addView view (view-builder %)) rest)
       (list view)))
    tree))

(defmacro view [tree]
  "Produce initialization code from given structure.  Example:

  (view [ScrollView {}
         [TextView {:text \"Hello world!\"}]])"
  (view-builder tree))

(defmacro content-view [tree]
  "Apply context.setContentView using view-builder."
  `(.setContentView ~'context (view ~tree)))