(ns gamification.routes
  (:require
   [bidi.bidi :as bidi]
   [pushy.core :as pushy]
   [re-frame.core :as re-frame]))

(def routes ["/" {""      :home
                  "about" :about
                  "login" :login
                  "register" :register}])

(defn- parse-url [url]
  (bidi/match-route routes url))

(defn- dispatch-route [matched-route]
  (let [panel-name (keyword (str (name (:handler matched-route)) "-panel"))]
    (re-frame/dispatch [:gamification.events/set-active-panel panel-name])))

(def history (pushy/pushy dispatch-route parse-url))

(defn app-routes []
  (pushy/start! history))

(def url-for (partial bidi/path-for routes))

;; -- set-token! -----------------------
;; To change route after some actions we will need to set url and for that
;; we will use set-token! that needs history and the token
(defn set-token! [url-key]
  (let [url (url-for url-key)]
    (pushy/set-token! history url)))
