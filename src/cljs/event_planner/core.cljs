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
               :on-click #(dispatch [:clicked])} "Click Me!"]]))

(defn render-simple []
  (r/render-component [simple-component]
                      (js/document.getElementById "app")))

(defn ^:export main
  []
  (dispatch-sync [:initialize])
  (render-simple))
