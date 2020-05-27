(ns gamification.views
  (:require
   [re-frame.core :as re-frame]
   [gamification.subs :as subs]
   [gamification.routes :as routes]))


;; home


(defn home-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1 (str "Hello from " @name ". This is the Home Page.")]
     [:div
      [:a {:href (routes/url-for :about)}
       "go to About Page"]]]))


;; about


(defn about-panel []
  [:div
   [:h1 "This is the About Page."]
   [:div
    [:a {:href (routes/url-for :home)}
     "go to Home Page"]]])


;; ---------------------


(defmulti panels identity)
(defmethod panels :home-panel [] [home-panel])
(defmethod panels :about-panel [] [about-panel])
(defmethod panels :default [] [:div])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [::subs/active-panel])]
    (fn []
      (panels @active-panel))))

