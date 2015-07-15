(ns hello-cljs.core
    (:require
      [om.core :as om :include-macros true]
      [hello-cljs.components
        :refer [like-seymore my-widget animals contacts-view]]
      [sablono.core :refer-macros [html]])
    (:require-macros
      [hello-cljs.utils :refer [logger]]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload
(defonce app-state
  (atom
    {:likes 0
     :contacts
        [{:first "Ben" :last "Bitdiddle" :email "benb@mit.edu"}
         {:first "Alyssa" :middle-initial "P" :last "Hacker" :email "aphacker@mit.edu"}
         {:first "Eva" :middle "Lu" :last "Ator" :email "eval@mit.edu"}
         {:first "Louis" :last "Reasoner" :email "prolog@mit.edu"}
         {:first "Cy" :middle-initial "D" :last "Effect" :email "bugs@mit.edu"}
         {:first "Lem" :middle-initial "E" :last "Tweakit" :email "morebugs@mit.edu"}]}
     :list ["lion" "zebra" "buffalo" "antelope" "gazelle"]))

(defn render! []
  (om/root
    (fn [data owner]
      (reify
        om/IRender
        (render [_]
          (html
            [:div.aside-left-menu
              (om/build contacts-view data)
              (om/build animals data)
              (om/build my-widget data)
              (om/build like-seymore data)]))))
    app-state
    {:target (. js/document (getElementById "app"))}))

(render!)

(defn on-js-reload [] (render!))

