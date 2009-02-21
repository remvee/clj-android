(ns twitter (:use clojure.xml))

(defstruct status :created-at :id :text :source :truncated :in_reply_to_status_id :in_reply_to_user_id :favorited :user)
(defstruct user :id :name :screen_name :description :location :profile_image_url :url :protected :followers_count)

(defn xml-children-by-tagname [element name]
  (filter #(= name (% :tag)) (element :content)))

(defn xml-first-child-by-tagname [element name]
  (first (xml-children-by-tagname element name)))

(defn xml-first-content-by-tagname [element name]
  (first ((xml-first-child-by-tagname element name) :content)))

(defn make-user [element]
  (let [f (fn [n] (xml-first-content-by-tagname element n))]
    (struct-map user
      :id (f :id)
      :name (f :name)
      :screen_name (f :screen_name)
      :description (f :description)
      :location (f :location)
      :profile_image_url (f :profile_image_url)
      :url (f :url)
      :protected (= "true" (f :protected))
      :followers_count (f :followers_count))))

(defn make-status [element]
  (let [f (fn [n] (xml-first-content-by-tagname element n))]
    (struct-map status
      :created-at (f :created_at)
      :id (f :id)
      :text (f :text)
      :source (f :source)
      :truncated (= "true" (f :truncated))
      :in_reply_to_status_id (f :in_reply_to_status_id)
      :in_reply_to_user_id (f :in_reply_to_user_id)
      :favorited (= "true" (f :favorited))
      :user (make-user (xml-first-child-by-tagname element :user)))))

(defn public-timeline []
  (let [url (new java.net.URL "http://twitter.com/statuses/public_timeline.xml")
        conn (.openConnection url)
        stream (.getInputStream conn)]
    (try
     (map #(make-status %) (xml-children-by-tagname (clojure.xml/parse stream) :status))
     (finally
      (.close stream)))))