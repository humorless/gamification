(ns gamification.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 ::active-panel
 (fn [db _]
   (:active-panel db)))

(re-frame/reg-sub
 ::loading
 (fn [db _]
   (:loading db)))

(re-frame/reg-sub
 ::errors
 (fn [db _]
   (:errors db)))
