(ns hello-cljs.async-walkthrough
  (:require
    [goog.dom :as dom]
    [goog.events :as events]
    [cljs.core.async :as async :refer [put! >! <! alts! chan sliding-buffer close!]])
  (:require-macros [cljs.core.async.macros :as am :refer [go]])
 (:import [goog.net Jsonp]
          [goog.events EventType]
          [goog Uri]))

(def wiki-search-url
  "http://en.wikipedia.org/w/api.php?action=opensearch&format=json&search=")

(defn listen [el type]
  (let [out (chan)]
    (events/listen el type
      (fn [e] (put! out e)))
    out))

(defn jsonp [uri]
  (let [out (chan)
        req (Jsonp. (Uri. uri))]
    (.send req nil (fn [res] (put! out res)))
    out))

(defn query-url [q]
  (str wiki-search-url q))

;; (go (.log js/console (<! (jsonp (query-url "cats")))))

(defn user-query []
  (.-value (dom/getElement "query")))

(defn render-query [results]
  (str
    "<ul>"
    (apply str
      (for [result results]
        (str "<li>" result "</li>")))
    "</ul>"))

(defn init[]
  (let [clicks (listen (dom/getElement "search") "click")
        results-view (dom/getElement "results")]
    (go (while true
          (<! clicks)
          (let [[_ results] (<! (jsonp (query-url (user-query))))]
            (set! (.-innerHTML results-view) (render-query results)))))))

(init)


