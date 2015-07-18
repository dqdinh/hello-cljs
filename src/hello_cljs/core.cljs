(ns hello-cljs.core
    (:require
      [om.core :as om :include-macros true]
      [hello-cljs.async-walkthrough]
      [hello-cljs.async-secret-combination]
      [hello-cljs.components.misc :refer [like-seymore my-widget animals]]
      [hello-cljs.components.contacts :refer [contacts-view]]
      [hello-cljs.components.registry :refer [registry-view]]
      [hello-cljs.components.classes :refer [classes-view]]
      [sablono.core :refer-macros [html]]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload
(defonce app-state
  (atom
    {:likes 0
     :people
          [{:type :student :first "Ben" :last "Bitdiddle" :email "benb@mit.edu"}
           {:type :student :first "Alyssa" :middle-initial "P" :last "Hacker"
            :email "aphacker@mit.edu"}
           {:type :professor :first "Gerald" :middle "Jay" :last "Sussman"
            :email "metacirc@mit.edu" :classes [:6001 :6946]}
           {:type :student :first "Eva" :middle "Lu" :last "Ator" :email "eval@mit.edu"}
           {:type :student :first "Louis" :last "Reasoner" :email "prolog@mit.edu"}
           {:type :professor :first "Hal" :last "Abelson" :email "evalapply@mit.edu"
            :classes [:6001]}]
      :classes
        {:6001 "The Structure and Interpretation of Computer Programs"
         :6946 "The Structure and Interpretation of Classical Mechanics"
         :1806 "Linear Algebra"}
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
              (om/build classes-view data)
              (om/build registry-view data)
              (om/build contacts-view data)
              (om/build animals data)
              (om/build my-widget data)
              (om/build like-seymore data)]))))
    app-state
    {:target (. js/document (getElementById "app"))}))

(render!)

(defn on-js-reload [] (render!))

