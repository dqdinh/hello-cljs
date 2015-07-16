(ns hello-cljs.components.registry
  (:require
    [clojure.string :as str]
    [clojure.data :as data]
    [om.core :as om :include-macros true]
    [cljs.core.async :as async :refer [put! chan <!]]
    [hello-cljs.utils :refer [display-name]]
    [sablono.core :refer-macros [html]]))
  (:require-macros
    [cljs.core.async.macros :as am :refer [go]])

(defn student-view [student owner]
  (reify
    om/IRender
    (render [_]
      (html
        [:li
         (display-name student)]))))

(defn professor-view [professor owner]
  (reify
    om/IRender
    (render [_]
      (html
        [:li
         [:div (display-name professor)]
         [:label "Classes"]
         [:ul
          (map (fn [p] [:li (om/value p)]) (:classes professor))]]))))

(defmulti entry-view (fn [person _] (:type person)))

(defmethod entry-view :student
  [person owner] (student-view person owner))

(defmethod entry-view :professor
  [person owner] (professor-view person owner))

(defn people [data]
  (->> (:people data)
       (mapv (fn [x]
               (if (:classes x)
                 (update-in x [:classes]
                   (fn [cs] (mapv (:classes data) cs)))
                 x)))))

(defn registry-view [data owner]
  (reify
    om/IRenderState
    (render-state [_ state]
      (html
        [:div#registry
         [:h2 "Registry"]
         [:ul
          (om/build-all entry-view (people data))]]))))

