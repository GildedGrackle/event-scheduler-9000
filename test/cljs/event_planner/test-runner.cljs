(ns event_planner.test-runner
  (:require
   [cljs.test :refer-macros [run-tests]]
   [event_planner.core-test]))

(enable-console-print!)

(defn runner []
  (if (cljs.test/successful?
       (run-tests
        'event_planner.core-test))
    0
    1))
