(ns gamification.views
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [gamification.subs :as subs]
   [gamification.events :as events]
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


;; login


(defn login-panel []
  (let [default {:email "" :password ""}
        credential (reagent/atom default)]
    (fn []
      (let [{:keys [email password]} @credential
            login-user (fn [e credential]
                         (.preventDefault e)
                         (re-frame/dispatch [::events/login credential]))]
        [:main.pa4.black-80
         [:form.measure.center {:on-submit #(login-user % @credential)}
          [:fieldset#sign_up.ba.b--transparent.ph0.mh0
           [:legend.f4.fw6.ph0.mh0 "Sign In"]
           [:div.mt3
            [:label.db.fw6.lh-copy.f6 {:for "email-address"} "Email"]
            [:input#email-address.pa2.input-reset.ba.bg-transparent.hover-bg-black.hover-white.w-100
             {:name "email-address", :type "email"
              :value email :on-change #(swap! credential assoc :email (-> % .-target .-value))}]]
           [:div.mv3
            [:label.db.fw6.lh-copy.f6 {:for "password"} "Password"]
            [:input#password.b.pa2.input-reset.ba.bg-transparent.hover-bg-black.hover-white.w-100
             {:name "password", :type "password"
              :value password :on-change #(swap! credential assoc :password (-> % .-target .-value))}]]
           [:label.pa0.ma0.lh-copy.f6.pointer
            [:input {:type "checkbox"}]
            " Remember me"]]
          [:div
           [:input.b.ph3.pv2.input-reset.ba.b--black.bg-transparent.grow.pointer.f6.dib
            {:value "Sign in", :type "submit"}]]
          [:div.lh-copy.mt3
           [:a.f6.link.dim.black.db {:href "#0"} "Sign up"]
           [:a.f6.link.dim.black.db {:href "#0"} "Forgot your password?"]]]]))))

;; ---------------------


(defmulti panels identity)
(defmethod panels :home-panel [] [home-panel])
(defmethod panels :about-panel [] [about-panel])
(defmethod panels :login-panel [] [login-panel])
(defmethod panels :default [] [:div])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [::subs/active-panel])]
    (fn []
      (panels @active-panel))))

