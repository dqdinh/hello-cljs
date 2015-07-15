(ns hello-cljs.core
    (:require
      [om.core :as om :include-macros true]
      [hello-cljs.components :refer [like-seymore my-widget animals]]
      [sablono.core :refer-macros [html]])
    (:require-macros
      [hello-cljs.utils :refer [logger]]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload
(defonce app-state
  (atom
    {:likes 0
     :list ["lion" "zebra" "buffalo" "antelope" "gazelle"]}))

(prn @app-state)

(defn render! []
  (om/root
    (fn [data owner]
      (reify
        om/IRender
        (render [_]
          (html
            [:div.aside-left-menu
              (om/build animals data)
              (om/build my-widget data)
              (om/build like-seymore data)]))))
    app-state
    {:target (. js/document (getElementById "app"))}))

(render!)

(defn on-js-reload []
  (render!)
)

