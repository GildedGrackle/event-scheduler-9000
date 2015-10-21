(ns event-planner.core
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
 :initialize
 (fn
   [db _]
   {}))

(register-handler
 :clicked
 (fn
   [db _]
   (POST "/api/event"
         {:handler #(dispatch [:event-response %1])
          :error-handler #(dispatch [:event-response %1])
          :format :json
          :params {:recurring true
                   :title "Some Time in the Future"
                   :days-of-week ["Monday"]
                   :start-time 9
                   :end-time 17}})
   (update-in db [:clicks] inc)))

(register-handler
 :event-response
 (fn
   [db [_ data]]
   (assoc db :data data)))

;; Subscriptions on database

(register-sub
 :clicks
 (fn
   [db _]
   (reaction (:clicks @db))))

(register-sub
 :data
 (fn
   [db _]
   (reaction (:data @db))))

(def days ["Sunday"
           "Monday"
           "Tuesday"
           "Wednesday"
           "Thursday"
           "Friday"
           "Saturday"])

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


(defn simple-component []
  (let [clicks (subscribe [:clicks])
        data (subscribe [:data])]
    [:div
     [:p "I am a component!"]
     [:p "I have so many clicks: " @clicks]
     [:p "Look at this sick data: " @data]
     [:p.someclass
      "I have " [:em "bold"]
      [:span {:style {:color "red"}} " and red "] "text."]
     [:button {:type "button"
               :on-click #(dispatch [:clicked])} "Click Me!"]
     [recurring-event-form]]))



(defn render-simple []
  (r/render-component [simple-component]
                      (js/document.getElementById "app")))

(defn ^:export main
  []
  (dispatch-sync [:initialize])
  (render-simple))
