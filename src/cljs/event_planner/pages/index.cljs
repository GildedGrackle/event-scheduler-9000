(ns event-planner.pages.index
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [reagent.core :as r]
            [ajax.core :refer [GET POST]]
            [re-frame.core :refer [register-handler
                                   path
                                   register-sub
                                   dispatch
                                   dispatch-sync
                                   subscribe]]))

(register-handler
 :event-response
 (fn
   [db [_ event]]
   (let [event-id (:id event 1)]
     (assoc-in db [:events event-id] event))))

(def days ["Sunday"
           "Monday"
           "Tuesday"
           "Wednesday"
           "Thursday"
           "Friday"
           "Saturday"])

;; Views
(defn checkbox
  [val name]
  [:input {:type "checkbox" :value val} name])

(defn drop-down
  [vals]
  [:select
   (for [[key name] vals]
     [:option {:value key} name])])

(defn military->12h [hour]
  (if (> hour 12)
    (str (- hour 12) " pm")
    (str hour " am")))

;; FIXME time is hard
(defn time-drop-down
  []
  (let [hours (map inc (range 24))]
    [drop-down (map vector
                    hours
                    (map military->12h hours))]))

(defn recurring-event-form
  []
  [:form
   "Title:" [:input {:type "text"}]
   "Days of Week:" (for [day days] [checkbox "help" day])
   "Start Time:" [time-drop-down]
   "End Time:" [time-drop-down]])


(defn index-page []
  (let []
    [:div
     [:a {:href "/events/1"} "Event 1"]
     [recurring-event-form]]))
