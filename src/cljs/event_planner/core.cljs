(ns event-planner.core
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [reagent.core :as r]
            [event-planner.pages.index :refer [index-page]]
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

(register-sub
 :current-page
 (fn [db _]
   (:current-page @db)))

(def pages {:index index-page})

(defn root-component []
  (let [current-page (subscribe [:current-page])
        page-component (pages current-page index-page)]
    ;return page + args in current-page
    (into [page-component] (rest current-page))))

(defn render []
  (r/render-component [root-component]
                      (js/document.getElementById "app")))

(defn ^:export main
  []
  (dispatch-sync [:initialize])
  (render))
