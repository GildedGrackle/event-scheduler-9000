(ns event-planner.core
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [reagent.core :as r]
            [event-planner.pages.index :refer [index-page]]
            [event-planner.pages.events :refer [events-page]]
            [ajax.core :refer [GET POST]]
            [re-frame.core :refer [register-handler
                                   path
                                   register-sub
                                   dispatch
                                   dispatch-sync
                                   subscribe]]
            [secretary.core :as secretary :refer-macros [defroute]]
            [pushy.core :as pushy])
  (:import goog.History))

(register-handler
 :initialize
 (fn
   [db _]
   (or db {})))

(register-handler
 :change-page
 (fn [db [_ page & args]]
   (.log js/console (pr-str db page args))
   (assoc-in db [:current-page] (into [page] args))))

(register-sub
 :current-page
 (fn [db _]
   (:current-page @db)))

(def pages {:index index-page
            :events events-page})

(defn header-component []
    [:nav {:class "navbar navbar-default navbar-static-top"
          :role "navigation"}
      [:div.container
        [:div.navbar-header
        [:button {:type "button"
                  :class "navbar-toggle collapsed"
                  :data-toggle "collapse"
                  :data-target "#navbar"
                  :aria-expanded "false"}
          [:span.sr-only "Toggle navigation"]
          [:span.icon-bar]
          [:span.icon-bar]
          [:span.icon-bar]]
          [:a.navbar-brand {:href "/"} "Schedule-Omatic 9000"]]
      [:div {:class "collapse navbar-collapse"
             :id "navbar"}
        [:ui.nav.navbar-nav.navbar-right
          [:li [:a {:href "/"} "Home"]]
          [:li [:a {:href "#"}"About Us"]]
          [:li [:a {:href "#"} "Login"]]
          ]]]])

(defn root-component []
  (let [current-page (subscribe [:current-page])
        page-component (pages (first current-page) index-page)]
    ;return page + args in current-page
    (into [page-component] (rest current-page))))

(defn wrapper-component []
  [:div
   [header-component]
   [root-component]])

(defn render []
  (r/render-component [wrapper-component]
                      (js/document.getElementById "app")))

(defn ^:export main
  []
  (dispatch-sync [:initialize])
  (render))

(defroute home-path "/" []
  (dispatch [:change-page :index]))

(defroute event-path "/events/:id" [id]
  (dispatch [:change-page :events id]))

(secretary/set-config! :prefix "/")
(def history (pushy/pushy secretary/dispatch!
                          (fn [x] (when (secretary/locate-route x) x))))

;; Start event listeners
(pushy/start! history)
