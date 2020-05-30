(ns gamification.events
  (:require
   [re-frame.core :as re-frame]
   [gamification.db :as db]
   [gamification.config :as config]
   [clojure.string :as string]
   [ajax.core :as ajax]
   [day8.re-frame.http-fx]
   [day8.re-frame.tracing :refer-macros [fn-traced]]))

;;--- Helpers -------------------------
(defn endpoint
  "Concat any params to api-uri separated by /"
  [& params]
  (string/join "/" (concat [config/API-URI config/APP-ID config/API-KEY] params)))

(re-frame/reg-event-db
 ::initialize-db
 (fn-traced [_ _]
            db/default-db))

(re-frame/reg-event-db
 ::set-active-panel
 (fn-traced [db [_ active-panel]]
            (assoc db :active-panel active-panel)))

(re-frame/reg-event-fx
 ::login
 (fn-traced [cofx [_ {:keys [email password]}]]
            {:db (assoc-in (:db cofx) [:loading :login] true)
             :http-xhrio {:method :post
                          :uri (endpoint "users" "login")
                          :params {:login email :password password}
                          :format (ajax/json-request-format)
                          :response-format (ajax/json-response-format {:keyword? true})
                          :on-success [::login-success]
                          :on-failure [::api-request-error ::login]}}))
