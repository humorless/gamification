(ns gamification.core
  (:require
   [reagent.core :as reagent]
   [reagent.dom :as rdom]
   [re-frame.core :as re-frame]
   [gamification.events :as events]
   [gamification.routes :as routes]
   [gamification.views :as views]
   [gamification.config :as config]))

(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [views/main-panel] root-el)))

(defn backendless-setup []
  (set! (.-serverURL js/Backendless) "https://api.backendless.com")
  (.initApp js/Backendless config/APP-ID config/API-KEY))

(defn backendless-save-data []
  (-> (.save
       (.of (.. js/Backendless -Data) "TestTable")
       (js-obj "foo" "bar"))
      (.then #(js/console.log (str "Object saved. ObjectId " %)))
      (.catch #(js/console.log (str "got error - " %)))))

(defn run-backendless-test []
  (backendless-setup)
  (backendless-save-data))

(defn init []
  (routes/app-routes)
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
