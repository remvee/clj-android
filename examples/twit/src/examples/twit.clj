(ns examples.twit
  (:use clj-android)
  (:require twitter)
  (:import (examples.twit R$layout R$id)
           (android.view Window)
           (android.widget ArrayAdapter)))

(defactivity About
  (:create (content-view [ScrollView {} [TextView {:text "Twit is an examples application for the clj-android package.."}]])))

(defactivity PublicTimeline (:extends android.app.ListActivity)
  (:create (.requestWindowFeature context Window/FEATURE_INDETERMINATE_PROGRESS)
           (.setContentView context R$layout/public_timeline))
  (:resume (.start (new Thread (fn []
                                 (ui (.setProgressBarIndeterminateVisibility context true))
                                 (let [statuses (into-array (map #(str "@" ((% :user) :screen_name) ": " (% :text)) (twitter/public-timeline)))]
                                   (ui (.setListAdapter context (new ArrayAdapter context android.R$layout/simple_list_item_1 statuses))))
                                 (ui (.setProgressBarIndeterminateVisibility context false)))))))

(defactivity Main
  (:create (.setContentView context R$layout/main)
           (on-click (view-by-id R$id/about_button)
                     (start-activity About))
           (on-click (view-by-id R$id/public_timeline_button)
                     (start-activity PublicTimeline))))