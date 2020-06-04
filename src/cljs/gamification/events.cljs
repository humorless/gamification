(ns gamification.events
  (:require
   [re-frame.core :as re-frame]
   [gamification.db :as db]
   [gamification.config :as config]
   [gamification.routes :as routes]
   [pushy.core :as pushy]
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

(re-frame/reg-fx
 :set-url
 (fn [url-key]
   (routes/set-token! url-key)))


;; --- Post Registration
;;


(re-frame/reg-event-fx
 ::register
 (fn-traced [cofx [_ {:keys [email password]}]]
            {:db (assoc-in (:db cofx) [:loading :register] true)
             :http-xhrio {:method :post
                          :uri (endpoint "users" "register")
                          :params {:email email :password password}
                          :format (ajax/json-request-format)
                          :response-format (ajax/json-response-format {:keyword? true})
                          :on-success [::register-success]
                          :on-failure [::api-request-error :register]}}))

(re-frame/reg-event-fx
 ::register-success
 (fn-traced [{db :db} [_ resp]]
            {:db         (-> db
                             (assoc-in [:loading :register] false))
             :set-url    :home
             :dispatch-n [[::complete-request :register]
                          [::set-active-panel :home-panel]]}))

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
                          :on-failure [::api-request-error :login]}}))

(re-frame/reg-event-fx
 ::login-success
 (fn-traced [{db :db} [_  resp]]
            {:db         (-> db
                             (assoc-in [:loading :login] false)
                             (assoc-in [:user :objectId] (get resp "objectId"))
                             (assoc-in [:user :user-token] (get resp "user-token")))
             :set-url    :home
             :dispatch-n [[::complete-request :login]
                          [::set-active-panel :home-panel]]}))

(re-frame/reg-event-db
 ::api-request-error                                        ;; triggered when we get request-error from the server
 (fn-traced [db [_ request-type response]]                        ;; destructure to obtain request-type and response
            (-> db                                                ;; when we complete a request we need to clean so that our ui is nice and tidy
                (assoc-in [:errors request-type] (get-in response [:response]))
                (assoc-in [:loading request-type] false))))

(re-frame/reg-event-fx
 ::complete-request
 (fn-traced [cofx [_ _]]
            {}))
