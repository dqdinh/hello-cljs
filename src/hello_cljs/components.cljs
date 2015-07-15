(ns hello-cljs.components
  (:require
    [om.core :as om :include-macros true]
    [cljs.core.async :as async :refer [put! chan <!]]
    [sablono.core :refer-macros [html]])
  (:require-macros [cljs.core.async.macros :as am :refer [go]]))

(defn like-seymore [data owner]
  (reify
    om/IRender
    (render [_]
      (html
        [:div
         [:h1 "popular: " (:likes @data)]
         [:div [:a {:href "#"
                    ;; FIXME: Error: No protocol method ISwap.-swap!
                    ;; defined for type om.core/MapCursor: [object Object]
                    :onClick #(swap! data assoc :likes inc)}
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

(defn middle-name [{:keys [middle middle-initial]}]
  (cond
    middle (str " " middle)
    middle-initial (str " " middle-initial ".")))

(defn display-name [{:keys [first last] :as contact}]
  (str last "," first (middle-name contact)))

(defn contact-view [contact owner]
  (reify
    om/IRenderState
    (render-state [_ {:keys [delete]}]
      (html
        [:li
         [:span (display-name contact)]
         [:button {:on-click #(put! delete @contact)}"Delete"]]))))

(defn contacts-view [data owner]
  (reify
    om/IInitState
      (init-state [_]
        {:delete (chan)})

    om/IWillMount
    (will-mount [_]
      (let [delete (om/get-state owner :delete)]
        (go (loop []
          (let [contact (<! delete)]
            (om/transact! data :contacts
              ;; NOTE: vec is used to transform result of `remove`
              ;; which returns a lazy seq back into a vector
              (fn [xs] (vec (remove #(= contact %) xs))))
            (recur))))))

    om/IRenderState
    (render-state [_ {:keys [delete]}]
      (html
        [:div
          [:h1 "Contact List"]
          [:ul
            (om/build-all contact-view
                          (:contacts data)
                          {:init-state {:delete delete}})]]))))




