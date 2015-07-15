(ns hello-cljs.components
  (:require
    [om.core :as om :include-macros true]
    [sablono.core :as s]))

(defn like-seymore [data owner]
  (reify
    om/IRender
    (render [_]
      (s/html
        [:div
         [:h1 "popular: " (:likes @data)]
         [:div [:a {:href "#"
                    :onClick #(swap! data assoc :likes inc)}
                "Thumbs up"]]]))))

(defn my-widget [data owner]
  (reify
    om/IRender
    (render [_]
      (s/html
        [:h1 {:style {:color "blue"}} "Holloo"]))))

