(ns hello-cljs.components.classes
  (:require
    [clojure.string :as str]
    [clojure.data :as data]
    [om.core :as om :include-macros true]
    [cljs.core.async :as async :refer [put! chan <!]]
    [sablono.core :refer-macros [html]])
  (:require-macros [cljs.core.async.macros :as am :refer [go]]))

(extend-type string
  ICloneable
  (-clone [s] (js/String. s)))

(extend-type js/String
  ICloneable
  (-clone [s] (js/String. s))
  om/IValue
  (-value [s] (str s)))

(defn display [show]
  (if show
    {}
    (:display "none")))

(defn handle-change [e text owner]
  (om/transact! text (fn [_] (.. e -target -value))))

(defn commit-change [text owner]
  (om/set-state! owner :editing false))

(defn editable [text owner]
  (reify
    om/IInitState
    (init-state [_]
      {:editing false})
    om/IRenderState
    (render-state [_ {:keys [editing]}]
      (html
        [:li
         [:span {:style (display (not editing))} (om/value text)]
         [:input {:style (display editing)
                  :value (om/value text)
                  :on-change #(handle-change % text owner)
                  :on-key-down #(when (= (.-key %) "Enter")
                                  (commit-change text owner))
                  :on-blur (fn [e] (commit-change text owner))}]
         [:button {:style (display (not editing))
                  :on-click #(om/set-state! owner :editing true)}
          "Edit"]]))))

(defn classes-view [data owner]
  (reify
    om/IRender
    (render [_]
      (html
        [:div {:id "classes"}
         [:h2 "Classes"]
         [:ul
          (om/build-all editable (vals (:classes data)))]]))))

