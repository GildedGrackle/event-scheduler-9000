(ns event-planner.pages.index
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [reagent.core :as r]
            [reagent-forms.core :refer [bind-fields]]
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
(defn drop-down
  [vals id]
  [:select.form-control {:field :list :id id}
   (for [[key name] vals]
     [:option {:key key} name])])

(defn military->12h [hour]
  (cond
    (= hour 0) "12 am"
    (< hour 12) (str hour " am")
    (= hour 12) "12 pm"
    (> hour 12) (str (- hour 12) " pm")))

(defn time-drop-down
  [id]
  (let [hours (concat (drop 1 (range 24)) (take 1 (range 24)))
        hour-names (map (comp military->12h) hours)]
    (drop-down (map vector hours hour-names) id)))

(def recurring-event-form-template
  [:div
   [:div.form-group
    [:label "Title:"] [:input {:field :text :id :title}]]
   [:div.form-group
    [:label "Days of Week:"]
    [:div.btn-group {:field :multi-select :id :days}
     (for [day days]
       [:button.btn.btn-default {:key day}
        [:span (first day)]])]]
   [:div.form-group
    [:label "Start Time:"] (time-drop-down :start-time)]
   [:div.form-group
    [:label "End Time:"] (time-drop-down :end-time)]])

(defn recurring-event-form
  []
  (let [doc (r/atom {:title "Your Event!"
                     :days (remove #{"Saturday" "Sunday"} days)
                     :start-time 9
                     :end-time 17})]
    (fn []
      [:div
       [bind-fields
        recurring-event-form-template
        doc]
       [:button {:type "button"
                 :on-click #(.log js/console (pr-str @doc))}
        "Create Event"]])))

(defn index-page []
  (let []
    [:div
     [:a {:href "/events/1"} "Event 1"]
     [recurring-event-form]]))
