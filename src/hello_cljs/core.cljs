(ns hello-cljs.core
    (:require
      [om.core :as om :include-macros true]
      [hello-cljs.components :refer [like-seymore my-widget]]
      [sablono.core :as s])
    (:require-macros
      [hello-cljs.utils :refer [logger]]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {:likes 0}))

(reset! app-state {:likes 9})

(.log js/console (prn-str @app-state))


(defn stripe [text bgc]
  (let [st {:backgroundColor bgc}]
    (s/html
      [:li {:style st}])))


(defn render! []
  (om/root
    (fn [data owner]
      (reify
        om/IRender
        (render [_]
          (s/html
            [:nav.aside-left-menu
              (om/build my-widget data)
              (om/build like-seymore data)]))))
    app-state
    {:target (. js/document (getElementById "app"))}))

(defn on-js-reload []
  (render!)
)

