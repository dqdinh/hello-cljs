(ns hello-cljs.components.misc
  (:require
    [clojure.string :as str]
    [clojure.data :as data]
    [om.core :as om :include-macros true]
    [cljs.core.async :as async :refer [put! chan <!]]
    [sablono.core :refer-macros [html]])
  (:require-macros [cljs.core.async.macros :as am :refer [go]]))

(defn like-seymore [data owner]
  (reify
    om/IRender
    (render [_]
      (html
        [:div#seymore
         [:h1 "popular: " (:likes @data)]
         [:div [:a {:href "#seymore"
                    ;; FIXME: Error: No protocol method ISwap.-swap!
                    ;; defined for type om.core/MapCursor: [object Object]
                    :on-click #(om/transact! data :likes inc)}
                "Thumbs up"]]]))))

(defn my-widget [data owner]
  (reify
    om/IRender
    (render [_]
      (html
        [:h1 {:style {:color "blue"}} "Holloo"]))))

(defn stripe [text bgc]
  (let [st {:backgroundColor bgc}]
    [:li {:style st} text]))

(defn animals [data owner]
  (om/component
    (html
      [:ul
       ;; Map will pair the each color with an element from the list
       ;; until it works through the entire list.
       ;; https://clojuredocs.org/clojure.core/cycle#example-557b10f6e4b01ad59b65f4f2
       (map stripe (:list data) (cycle ["#ff0" "#f00"]))])))
